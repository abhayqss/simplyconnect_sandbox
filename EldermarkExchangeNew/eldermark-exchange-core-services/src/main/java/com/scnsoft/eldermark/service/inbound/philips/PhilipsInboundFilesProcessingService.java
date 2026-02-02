package com.scnsoft.eldermark.service.inbound.philips;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.inbound.philips.PhilipsEventFileProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.philips.PhilipsEventRecordProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.philips.PhilipsEventCSV;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.service.inbound.AbstractInboundFilesProcessingService;
import com.scnsoft.eldermark.service.inbound.InboundFileGateway;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(
        value = "philips.integration.enabled",
        havingValue = "true"
)
public class PhilipsInboundFilesProcessingService extends AbstractInboundFilesProcessingService<File, PhilipsEventFileProcessingSummary> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractInboundFilesProcessingService.class);

    @Autowired
    private InboundFileGateway<File, PhilipsEventFileProcessingSummary> inboundFileGateway;

    @Autowired
    private PhilipsEventService philipsEventService;

    @Autowired
    private DocumentEncryptionService documentEncryptionService;


    @Override
    protected List<File> loadFiles() {
        return inboundFileGateway.loadFiles();
    }

    @Override
    protected PhilipsEventFileProcessingSummary process(File localFile) throws Exception {
        logger.info("Process {}", localFile.getName());
        final PhilipsEventFileProcessingSummary summary = new PhilipsEventFileProcessingSummary();
        summary.setFileName(localFile.getName());
        processEventFile(localFile, summary);
        summary.propagateStatusAndMessage();
        return summary;
    }

    private void processEventFile(File localFile, PhilipsEventFileProcessingSummary fileProcessingSummary) {
        fileProcessingSummary.setFileName(localFile.getName());
        try {
            final List<PhilipsEventCSV> philipsEvents = readFromFile(localFile);
            fileProcessingSummary.setTotalEvents(philipsEvents.size());
            List<PhilipsEventRecordProcessingSummary> eventRecordProcessingSummaries=new ArrayList<>();
            for (PhilipsEventCSV philipsEvent : philipsEvents) {
                PhilipsEventRecordProcessingSummary recordProcessingSummary = new PhilipsEventRecordProcessingSummary();
                validatePhilipsEventCsv(philipsEvent);
                processEventRecord(philipsEvent, recordProcessingSummary);
                updateFileSummaryWithRecordSummary(fileProcessingSummary, recordProcessingSummary);
                eventRecordProcessingSummaries.add(recordProcessingSummary);
            }
            fileProcessingSummary.setEventRecordProcessingSummaries(eventRecordProcessingSummaries);
        } catch (Exception ex) {
            logger.warn("Error during processing event file: {}", ExceptionUtils.getStackTrace(ex));
            fillProcessingSummaryErrorFields(fileProcessingSummary, ex);
        }
    }

    private PhilipsEventRecordProcessingSummary processEventRecord(PhilipsEventCSV philipsEvent, PhilipsEventRecordProcessingSummary recordProcessingSummary) {
        logger.warn("Event data is {} ", philipsEvent);
        try {
            Event event = philipsEventService.createEvent(philipsEvent);
            recordProcessingSummary.setEventId(event.getId());
            recordProcessingSummary.propagateStatusAndMessage();
        } catch (Exception ex) {
            logger.warn("Error during processing event CSV record: {}", ExceptionUtils.getStackTrace(ex));
            fillProcessingSummaryErrorFields(recordProcessingSummary, ex);
        }
        return recordProcessingSummary;
    }


    private List<PhilipsEventCSV> readFromFile(File file) throws Exception {
        HeaderColumnNameMappingStrategy<PhilipsEventCSV> strategy
                = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(PhilipsEventCSV.class);
        var decrypted  = documentEncryptionService.decrypt(Files.readAllBytes(file.toPath()));
        try (var fileReader = new StringReader(new String(decrypted, StandardCharsets.UTF_8))) {
            return new CsvToBeanBuilder<PhilipsEventCSV>(fileReader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();
        } catch (RuntimeException e) {
            logger.info("error during csv parsing, unwrapping exception", e);
            throw (Exception) e.getCause();
        }
    }

    @Override
    protected void afterProcessingStatusOk(File localFile, PhilipsEventFileProcessingSummary processingSummary) {
        logger.info("Successfully processed {}", localFile.getName());
        inboundFileGateway.afterProcessingStatusOk(localFile, processingSummary);
    }

    @Override
    protected void afterProcessingStatusWarn(File localFile, PhilipsEventFileProcessingSummary processingSummary) {
        logger.info("Processed {} with warnings ", localFile.getName());
        inboundFileGateway.afterProcessingStatusWarn(localFile, processingSummary);
    }

    @Override
    protected void afterProcessingStatusError(File localFile, Exception exception) {
        logger.info("Processed {} with error, exception is: {}", localFile.getName(), ExceptionUtils.getStackTrace(exception));
        final PhilipsEventFileProcessingSummary summary = new PhilipsEventFileProcessingSummary();
        summary.setFileName(localFile.getName());
        summary.setProcessedAt(Instant.now());
        fillProcessingSummaryErrorFields(summary, exception);
        inboundFileGateway.afterProcessingStatusError(localFile, summary);
    }

    private void updateFileSummaryWithRecordSummary(PhilipsEventFileProcessingSummary fileSummary,
                                                    PhilipsEventRecordProcessingSummary recordSummary) {
        fileSummary.getEventRecordProcessingSummaries().add(recordSummary);
        if (!ProcessingSummary.ProcessingStatus.ERROR.equals(recordSummary.getStatus())) {
            fileSummary.setProcessedEvents(fileSummary.getProcessedEvents() + 1);
        }
    }

    private void validatePhilipsEventCsv(PhilipsEventCSV philipsEventCSV) {
        if (philipsEventCSV.getMrn() == null) {
            throw new PhilipsFileProcessingException("empty mrn");
        }
        try {
            Long.parseLong(philipsEventCSV.getMrn());
        } catch (NumberFormatException ex) {
            throw new PhilipsFileProcessingException("wrong mrn: " + philipsEventCSV.getMrn());
        }
        if (philipsEventCSV.getSituation() == null)
            throw new PhilipsFileProcessingException("missing situation");
    }
}
