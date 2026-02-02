package com.scnsoft.eldermark.service.inbound.healthpartners.fileprocessors;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.scnsoft.eldermark.dto.healthpartners.HpCsvRecord;
import com.scnsoft.eldermark.entity.healthpartner.BaseHealthPartnersRecord;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpRecordProcessingSummary;
import com.scnsoft.eldermark.jms.dto.ResidentUpdateType;
import com.scnsoft.eldermark.jms.producer.ClientUpdateQueueProducer;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.service.inbound.ProcessingSummarySupport;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseHpFileProcessor<
        R extends HpRecordProcessingSummary,
        T extends HpFileProcessingSummary<R>,
        D extends HpCsvRecord> implements HealthPartnersFileProcessor {
    public static final ZoneId CT_ZONE = ZoneId.of("America/Chicago");
    private static final Logger logger = LoggerFactory.getLogger(BaseHpFileProcessor.class);


    private final Class<D> csvRecordClass;
    private final char separator;
    private final HpFileType hpFileType;
    private final Comparator<D> sortComparator;
    private final ClientUpdateQueueProducer clientUpdateQueueProducer;
    private final DocumentEncryptionService documentEncryptionService;

    protected BaseHpFileProcessor(Class<D> csvRecordClass, char separator, HpFileType hpFileType,
                                  Comparator<D> sortComparator,
                                  ClientUpdateQueueProducer clientUpdateQueueProducer,
                                  DocumentEncryptionService documentEncryptionService) {
        this.csvRecordClass = csvRecordClass;
        this.separator = separator;
        this.hpFileType = hpFileType;
        this.sortComparator = sortComparator;
        this.clientUpdateQueueProducer = clientUpdateQueueProducer;
        this.documentEncryptionService = documentEncryptionService;
    }

    @Override
    public HpFileType supportedFileType() {
        return hpFileType;
    }

    @Override
    public HpFileProcessingSummary<?> process(Long fileLogId, File file, Long communityId) {
        var fileProcessingSummary = (HpFileProcessingSummary<R>) hpFileType.createFileSummary();
        fileProcessingSummary.setFileName(file.getName());
        fileProcessingSummary.setRecordProcessingSummaries(new ArrayList<>());
        fileProcessingSummary.setFileLogId(fileLogId);

        try {
            var records = sort(readFromFile(file));
            fileProcessingSummary.setTotalRecords(records.size());

            var claimRecordProcessingSummaries = fileProcessingSummary.getRecordProcessingSummaries();

            //since incoming data is sorted by client we can trigger update only once after client was processed
            Long previousClient = null;
            Set<ResidentUpdateType> updateTypes = EnumSet.noneOf(ResidentUpdateType.class);

            for (var record : records) {
                var result = processRecord(fileLogId, record, communityId);
                claimRecordProcessingSummaries.add(result);

                if (!Objects.equals(previousClient, result.getClientId())) {
                    if (previousClient != null) {
                        clientUpdateQueueProducer.putToResidentUpdateQueue(previousClient, updateTypes);
                    }
                    previousClient = result.getClientId();
                    updateTypes.clear();
                }

                if (result.getUpdateTypes() != null) {
                    updateTypes.addAll(result.getUpdateTypes());
                }
            }

            if (previousClient != null) {
                clientUpdateQueueProducer.putToResidentUpdateQueue(previousClient, updateTypes);
            }

            fileProcessingSummary.setRecordProcessingSummaries(claimRecordProcessingSummaries);
            fileProcessingSummary.setProcessedRecords(
                    (int) claimRecordProcessingSummaries.stream()
                            .filter(rs -> !ProcessingSummary.ProcessingStatus.ERROR.equals(rs.getStatus()))
                            .count()
            );

        } catch (Exception ex) {
            logger.warn("Error during processing file: ", ex);
            ProcessingSummarySupport.fillProcessingSummaryErrorFields(fileProcessingSummary, ex);
        }

        return fileProcessingSummary;
    }

    protected List<D> readFromFile(File file) throws Exception {
        HeaderColumnNameMappingStrategy<D> strategy
                = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(csvRecordClass);
        var decrypted  = documentEncryptionService.decrypt(Files.readAllBytes(file.toPath()));
        try (var fileReader = new StringReader(new String(decrypted, StandardCharsets.UTF_8))) {
            return new CsvToBeanBuilder<D>(fileReader)
                    .withMappingStrategy(strategy)
                    .withSeparator(separator)
                    .withIgnoreQuotations(true)
                    .build()
                    .stream()
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            logger.info("error during csv parsing, unwrapping exception", e);
            throw (Exception) e.getCause();
        }
    }

    //HP should sort files on their side, but we'll still apply sorting on our side
    //just to be sure that adjustment claims come after original claims and to be able to
    //work with test files HP provided us with prior to implementing sorting on their side.
    //For better performance we'll check sorting prior to sorting. Also claim numbers are
    //actually numbers, so we sort them as numbers (first length, then value), but as claim
    //number is specified as strings in spec, we don't actually cast them to numbers
    protected List<D> sort(List<D> records) {
        if (sortComparator == null || CollectionUtils.isEmpty(records)) {
            logger.info("Won't sort incoming file");
            return records;
        }

        //add line numbers to be able to trace original record from ack file
        int lineCounter = 2;
        for (D e : records) {
            e.setLineNumber(lineCounter++);
        }

        if (isAlreadySorted(records)) {
            logger.info("Won't sort incoming file because already sorted");
            return records;
        }

        logger.info("Sorting incoming file");
        records.sort(sortComparator);
        logger.info("File sorted");
        return records;
    }

    private boolean isAlreadySorted(List<D> records) {
        var prev = records.get(0);
        for (int i = 1; i < records.size(); i++) {
            if (sortComparator.compare(records.get(i), prev) < 0) {
                return false;
            }
            prev = records.get(i);
        }
        return true;
    }

    protected void fillBase(D csvRecord, BaseHealthPartnersRecord record) {
        record.setReceived(Instant.now());
        record.setMemberIdentifier(nullIfEmpty(csvRecord.getMemberIdentifier()));
        record.setMemberFirstName(nullIfEmpty(csvRecord.getMemberFirstName()));
        record.setMemberMiddleName(nullIfEmpty(csvRecord.getMemberMiddleName()));
        record.setMemberLastName(nullIfEmpty(csvRecord.getMemberLastName()));
        record.setBirthDate(csvRecord.getDateOfBirth());
    }

    protected abstract R processRecord(Long fileLogId, D record, Long communityId);

    protected String nullIfEmpty(String str) {
        return StringUtils.defaultIfEmpty(str, null);
    }

}
