package com.scnsoft.eldermark.services.inbound.therap;

import com.opencsv.bean.CsvToBeanBuilder;
import com.scnsoft.eldermark.entity.Event;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.inbound.summary.ProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.TherapProcessingContext;
import com.scnsoft.eldermark.entity.inbound.therap.csv.TherapEventCSV;
import com.scnsoft.eldermark.entity.inbound.therap.csv.TherapIdfCSV;
import com.scnsoft.eldermark.entity.inbound.therap.csv.TherapProgramEnrollmentCsv;
import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapEntitiesProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapEntityFileProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapEntityRecordProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapTotalProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.event.TherapEventFileProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.event.TherapEventRecordProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.event.TherapEventsProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.idf.TherapIdfFileProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.idf.TherapIdfRecordProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.idf.TherapIdfsProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.programenrollment.TherapProgramEnrollmentFileProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.programenrollment.TherapProgramEnrollmentRecordProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.programenrollment.TherapProgramEnrollmentsProcessingSummary;
import com.scnsoft.eldermark.services.exceptions.TherapBusinessException;
import com.scnsoft.eldermark.services.inbound.AbstractInboundFilesProcessingService;
import com.scnsoft.eldermark.services.inbound.InboundFileGateway;
import com.scnsoft.eldermark.services.inbound.therap.idf.TherapIdfService;
import com.scnsoft.eldermark.services.inbound.therap.programenrollment.TherapOrganizationService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.shared.phr.utils.Normalizer;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

@Service
@Conditional(TherapInboundFilesServiceRunCondition.class)
public class TherapInboundFilesProcessingService extends AbstractInboundFilesProcessingService<File, TherapTotalProcessingSummary> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractInboundFilesProcessingService.class);
    private static final String PROGRAM_ENROLLMENTS_FILE_NAME_CONTAINS = "$PROGRAM_ENROLLMENT$";
    private static final String EVENT_FILE_NAME_CONTAINS = "$GER_EVENT$";
    private static final String IDF_FILE_NAME_CONTAINS = "$IDF_DETAIL$";

    private final InboundFileGateway<File, TherapTotalProcessingSummary> inboundFileGateway;
    private final TherapOrganizationService therapOrganizationService;
    private final TherapEventService therapEventService;
    private final TherapIdfService therapCsvIdfService;
    private final TherapMailService therapMailService;

    @Autowired
    public TherapInboundFilesProcessingService(InboundFileGateway<File, TherapTotalProcessingSummary> inboundFileGateway, TherapOrganizationService therapOrganizationService, TherapEventService therapEventService, TherapIdfService therapCsvIdfService, TherapMailService therapMailService) {
        this.inboundFileGateway = inboundFileGateway;
        this.therapOrganizationService = therapOrganizationService;
        this.therapEventService = therapEventService;
        this.therapCsvIdfService = therapCsvIdfService;
        this.therapMailService = therapMailService;
    }

    @Override
    protected List<File> loadFiles() {
        return inboundFileGateway.loadFiles();
    }

    @Override
    protected TherapTotalProcessingSummary process(File remoteFile) throws Exception {
        logger.info("Process {}", remoteFile.getName());
        final ZipFile zipFile = new ZipFile(remoteFile);
        final TherapTotalProcessingSummary summary = new TherapTotalProcessingSummary();
        summary.setFileName(remoteFile.getName());

        final TherapProcessingContext ctx = new TherapProcessingContext();

        summary.setEnrollmentsProcessingSummary(processEnrollments(zipFile, ctx));
        summary.setIdfsProcessingSummary(processIdfs(zipFile, ctx));
        summary.setEventsProcessingSummary(processEvents(zipFile, ctx));

        summary.propagateStatusAndMessage();

        zipFile.close();

        return summary;
    }

    private TherapProgramEnrollmentsProcessingSummary processEnrollments(ZipFile zipFile, TherapProcessingContext ctx) {
        final TherapProgramEnrollmentsProcessingSummary enrollmentsSummary = new TherapProgramEnrollmentsProcessingSummary();

        try {
            final List<ZipArchiveEntry> enrollmentFiles = findEnrollmentFiles(zipFile);
            enrollmentsSummary.setFoundFiles(enrollmentFiles.size());

            for (ZipArchiveEntry entry : enrollmentFiles) {
                TherapProgramEnrollmentFileProcessingSummary fileProcessingSummary = processEnrollmentFile(zipFile, entry, ctx);

                updateEntitiesSummaryWithFileSummary(enrollmentsSummary, fileProcessingSummary);
            }
            enrollmentsSummary.propagateStatusAndMessage();
        } catch (Exception ex) {
            logger.warn("Error occurred during processing therap program enrollments, reason: {}", ExceptionUtils.getStackTrace(ex));
            fillProcessingSummaryErrorFields(enrollmentsSummary, ex);
        }
        return enrollmentsSummary;
    }

    private List<ZipArchiveEntry> findEnrollmentFiles(ZipFile zipFile) {
        return findFiles(zipFile, PROGRAM_ENROLLMENTS_FILE_NAME_CONTAINS);
    }

    private TherapProgramEnrollmentFileProcessingSummary processEnrollmentFile(ZipFile zipFile, ZipArchiveEntry entry, TherapProcessingContext ctx) {
        final TherapProgramEnrollmentFileProcessingSummary fileProcessingSummary = new TherapProgramEnrollmentFileProcessingSummary();
        fileProcessingSummary.setFileName(entry.getName());

        try {
            final InputStream inputStream = zipFile.getInputStream(entry);
            final List<TherapProgramEnrollmentCsv> enrollmentCsvList = readFromInputStream(inputStream, TherapProgramEnrollmentCsv.class);

            fileProcessingSummary.setTotalRecords(enrollmentCsvList.size());

            for (TherapProgramEnrollmentCsv enrollment : enrollmentCsvList) {
                TherapProgramEnrollmentRecordProcessingSummary recordProcessingSummary = processEnrollmentRecord(enrollment, ctx);

                updateFileSummaryWithRecordSummary(fileProcessingSummary, recordProcessingSummary);
            }
            fileProcessingSummary.propagateStatusAndMessage();
        } catch (Exception ex) {
            logger.warn("Error during processing event file: {}", ExceptionUtils.getStackTrace(ex));
            fillProcessingSummaryErrorFields(fileProcessingSummary, ex);
        }
        return fileProcessingSummary;
    }

    private TherapProgramEnrollmentRecordProcessingSummary processEnrollmentRecord(TherapProgramEnrollmentCsv enrollment, TherapProcessingContext ctx) {
        logger.info("Processing enrollment record with for IDFFORMID = [{}]", enrollment.getIdFormId());
        logger.debug("Enrollment data is {} ", enrollment);

        final TherapProgramEnrollmentRecordProcessingSummary recordProcessingSummary = new TherapProgramEnrollmentRecordProcessingSummary();
        recordProcessingSummary.setIdfFormId(enrollment.getIdFormId());
        recordProcessingSummary.setProgramId(enrollment.getPgmId());
        recordProcessingSummary.setProgramName(enrollment.getPgmName());

        try {
            final Pair<Long, Boolean> communityIdAndAlreadyExisted = therapOrganizationService.findOrCreateOrganization(enrollment);

            recordProcessingSummary.setCommunityId(communityIdAndAlreadyExisted.getFirst());
            recordProcessingSummary.setAlreadyExisted(communityIdAndAlreadyExisted.getSecond());

            ctx.getIdfFormIdToCommunityId().put(recordProcessingSummary.getIdfFormId(), recordProcessingSummary.getCommunityId());

            recordProcessingSummary.propagateStatusAndMessage();
        } catch (Exception ex) {
            logger.warn("Error during processing event CSV record: {}", ExceptionUtils.getStackTrace(ex));
            fillProcessingSummaryErrorFields(recordProcessingSummary, ex);
        }
        return recordProcessingSummary;
    }

    private TherapIdfsProcessingSummary processIdfs(ZipFile zipFile, TherapProcessingContext ctx) {
        final TherapIdfsProcessingSummary idfsProcessingSummary = new TherapIdfsProcessingSummary();
        try {
            final List<ZipArchiveEntry> idfFiles = findIdfFiles(zipFile);
            idfsProcessingSummary.setFoundFiles(idfFiles.size());

            if (ctx.getIdfFormIdToCommunityId().isEmpty()) {
                logger.info("Idfs will not be processed - no program enrollment files.");
                idfsProcessingSummary.setMessage("Idfs will not be processed - no program enrollment files.");
            } else {
                for (ZipArchiveEntry entry : idfFiles) {
                    TherapIdfFileProcessingSummary fileProcessingSummary = processIdfFile(zipFile, entry, ctx);
                    updateEntitiesSummaryWithFileSummary(idfsProcessingSummary, fileProcessingSummary);
                }
            }

            idfsProcessingSummary.propagateStatusAndMessage();
        } catch (Exception ex) {
            logger.warn("Error occurred during processing therap idf details, reason: {}", ExceptionUtils.getStackTrace(ex));
            fillProcessingSummaryErrorFields(idfsProcessingSummary, ex);
        }
        return idfsProcessingSummary;
    }

    private List<ZipArchiveEntry> findIdfFiles(ZipFile zipFile) {
        return findFiles(zipFile, IDF_FILE_NAME_CONTAINS);
    }

    private TherapIdfFileProcessingSummary processIdfFile(ZipFile zipFile, ZipArchiveEntry entry, TherapProcessingContext ctx) {
        final TherapIdfFileProcessingSummary fileProcessingSummary = new TherapIdfFileProcessingSummary();
        fileProcessingSummary.setFileName(entry.getName());
        try {
            final InputStream inputStream = zipFile.getInputStream(entry);
            final List<TherapIdfCSV> therapIdfs = readFromInputStream(inputStream, TherapIdfCSV.class);

            fileProcessingSummary.setTotalRecords(therapIdfs.size());
            for (TherapIdfCSV therapIdf : therapIdfs) {
                TherapIdfRecordProcessingSummary recordProcessingSummary = processIdfRecord(therapIdf, ctx);
                updateFileSummaryWithRecordSummary(fileProcessingSummary, recordProcessingSummary);
            }
            fileProcessingSummary.propagateStatusAndMessage();
        } catch (Exception ex) {
            logger.warn("Error during processing idf file: {}", ExceptionUtils.getStackTrace(ex));
            fillProcessingSummaryErrorFields(fileProcessingSummary, ex);
        }
        return fileProcessingSummary;

    }

    private TherapIdfRecordProcessingSummary processIdfRecord(TherapIdfCSV therapIdf, TherapProcessingContext ctx) {
        logger.info("Processing IDF record with IDFFORMID = [{}]", therapIdf.getIdFormId());
        logger.debug("Idfs data is {} ", therapIdf.toString());

        final TherapIdfRecordProcessingSummary recordProcessingSummary = new TherapIdfRecordProcessingSummary();
        recordProcessingSummary.setIdfFormId(therapIdf.getIdFormId());
        recordProcessingSummary.setStatus(ProcessingSummary.ProcessingStatus.OK);

        try {
            Long idfCommunityId = ctx.getIdfFormIdToCommunityId().get(therapIdf.getIdFormId());
            therapIdf.setSsn(Normalizer.normalizePhone(therapIdf.getSsn()));

            final Pair<Resident, Boolean> residentAndAlreadyExisted = therapCsvIdfService.createOrUpdateResident(therapIdf, idfCommunityId);
            recordProcessingSummary.setResidentId(residentAndAlreadyExisted.getFirst().getId());
            recordProcessingSummary.setResidentAlreadyExisted(residentAndAlreadyExisted.getSecond());

            ctx.getIdfFormIdToResidentId().put(therapIdf.getIdFormId(), recordProcessingSummary.getResidentId());

            recordProcessingSummary.propagateStatusAndMessage();

            logger.info("IDF record with IDFFORMID = [{}] processed successfully", therapIdf.getIdFormId());
        } catch (Exception ex) {
            logger.warn("Error during processing idf CSV record: {}", ExceptionUtils.getStackTrace(ex));
            fillProcessingSummaryErrorFields(recordProcessingSummary, ex);
        }
        return recordProcessingSummary;
    }

    private TherapEventsProcessingSummary processEvents(ZipFile zipFile, TherapProcessingContext ctx) {
        final TherapEventsProcessingSummary eventProcessingSummary = new TherapEventsProcessingSummary();
        try {
            final List<ZipArchiveEntry> eventFiles = findEventFiles(zipFile);
            eventProcessingSummary.setFoundFiles(eventFiles.size());
            for (ZipArchiveEntry entry : eventFiles) {
                TherapEventFileProcessingSummary fileProcessingSummary = processEventFile(zipFile, entry, ctx);

                updateEntitiesSummaryWithFileSummary(eventProcessingSummary, fileProcessingSummary);
            }
            eventProcessingSummary.propagateStatusAndMessage();
        } catch (Exception ex) {
            logger.warn("Error occurred during processing therap events, reason: {}", ExceptionUtils.getStackTrace(ex));
            fillProcessingSummaryErrorFields(eventProcessingSummary, ex);
        }
        return eventProcessingSummary;

    }

    private List<ZipArchiveEntry> findEventFiles(ZipFile zipFile) {
        return findFiles(zipFile, EVENT_FILE_NAME_CONTAINS);
    }

    private TherapEventFileProcessingSummary processEventFile(ZipFile zipFile, ZipArchiveEntry entry, TherapProcessingContext ctx) {
        final TherapEventFileProcessingSummary fileProcessingSummary = new TherapEventFileProcessingSummary();
        fileProcessingSummary.setFileName(entry.getName());
        try {
            final InputStream inputStream = zipFile.getInputStream(entry);
            final List<TherapEventCSV> therapEvents = readFromInputStream(inputStream, TherapEventCSV.class);

            fileProcessingSummary.setTotalRecords(therapEvents.size());

            for (TherapEventCSV therapEvent : therapEvents) {
                TherapEventRecordProcessingSummary recordProcessingSummary = processEventRecord(therapEvent, ctx);
                updateFileSummaryWithRecordSummary(fileProcessingSummary, recordProcessingSummary);
            }
            fileProcessingSummary.propagateStatusAndMessage();
        } catch (Exception ex) {
            logger.warn("Error during processing event file: {}", ExceptionUtils.getStackTrace(ex));
            fillProcessingSummaryErrorFields(fileProcessingSummary, ex);
        }
        return fileProcessingSummary;
    }

    private TherapEventRecordProcessingSummary processEventRecord(TherapEventCSV therapEvent, TherapProcessingContext ctx) {
        logger.info("Processing event record with GERFORMID = [{}]", therapEvent.getGerFormId());
        logger.debug("Event data is {} ", therapEvent);

        final TherapEventRecordProcessingSummary recordProcessingSummary = new TherapEventRecordProcessingSummary();
        recordProcessingSummary.setGerFormId(therapEvent.getGerFormId());
        recordProcessingSummary.setIdfFormId(therapEvent.getIdFormId());

        try {
            therapEvent.setIndividualSsn(Normalizer.normalizePhone(therapEvent.getIndividualSsn()));
            final Long idfResidentId = ctx.getIdfFormIdToResidentId().get(therapEvent.getIdFormId());
            final List<Event> createdEvents = therapEventService.createEvents(therapEvent, idfResidentId);
            recordProcessingSummary.setEventsCreated(createdEvents.size());
            if (createdEvents.size() == 0) {
                throw new TherapBusinessException("Couldn't create event record though it is valid");
            }
            for (Event e: createdEvents) {
                recordProcessingSummary.getEventsCreatedIds().add(e.getId());
            }
            recordProcessingSummary.propagateStatusAndMessage();
        } catch (Exception ex) {
            logger.warn("Error during processing event CSV record: {}", ExceptionUtils.getStackTrace(ex));
            fillProcessingSummaryErrorFields(recordProcessingSummary, ex);
        }
        return recordProcessingSummary;
    }

    private List<ZipArchiveEntry> findFiles(ZipFile zipFile, String nameContains) {
        final List<ZipArchiveEntry> files = new ArrayList<>();
        for (Enumeration<ZipArchiveEntry> entries = zipFile.getEntries(); entries.hasMoreElements(); ) {
            final ZipArchiveEntry entry = entries.nextElement();
            logger.info("Archive entry [{}]", entry.getName());

            if (!entry.isDirectory() && entry.getName().contains(nameContains)) {
                files.add(entry);
            }
        }
        return files;
    }

    private <T> List<T> readFromInputStream(InputStream inputStream, Class<T> clazz) throws Exception {
        try {
            return new CsvToBeanBuilder<T>(new InputStreamReader(inputStream))
                    .withType(clazz)
                    .build()
                    .parse();
        } catch (RuntimeException e) {
            logger.info("error during csv parsing, unwrapping exception", e);
            throw (Exception) e.getCause();
        }
    }

    @Override
    protected void afterProcessingStatusOk(File remoteFile, TherapTotalProcessingSummary processingSummary) {
        logger.info("Successfully processed {}", remoteFile.getName());
        inboundFileGateway.afterProcessingStatusOk(remoteFile, processingSummary);
    }

    @Override
    protected void afterProcessingStatusWarn(File remoteFile, TherapTotalProcessingSummary processingSummary) {
        logger.info("Processed {} with warnings ", remoteFile.getName());
        sendEmail(processingSummary);
        inboundFileGateway.afterProcessingStatusWarn(remoteFile, processingSummary);
    }

    @Override
    protected void afterProcessingStatusError(File remoteFile, Exception exception) {
        logger.info("Processed {} with error, exception is: {}", remoteFile.getName(), ExceptionUtils.getStackTrace(exception));

        final TherapTotalProcessingSummary summary = new TherapTotalProcessingSummary();
        summary.setFileName(remoteFile.getName());
        summary.setProcessedAt(new Date());
        fillProcessingSummaryErrorFields(summary, exception);

        sendEmail(summary);

        inboundFileGateway.afterProcessingStatusError(remoteFile, summary);
    }

    private void sendEmail(TherapTotalProcessingSummary processingSummary) {
        therapMailService.sendEmailNotifications(processingSummary);
    }

    private <F extends TherapEntityFileProcessingSummary> void updateEntitiesSummaryWithFileSummary(TherapEntitiesProcessingSummary<F> entitiesSummary, F fileProcessingSummary) {
        entitiesSummary.getFilesProcessingSummary().add(fileProcessingSummary);
        if (!ProcessingSummary.ProcessingStatus.ERROR.equals(fileProcessingSummary.getStatus())) {
            entitiesSummary.setProcessedFiles(entitiesSummary.getProcessedFiles() + 1);
        }
    }

    private <R extends TherapEntityRecordProcessingSummary> void updateFileSummaryWithRecordSummary(TherapEntityFileProcessingSummary<R> fileSummary,
                                                                                                    R recordSummary) {
        fileSummary.getRecordsProcessingSummary().add(recordSummary);
        if (!ProcessingSummary.ProcessingStatus.ERROR.equals(recordSummary.getStatus())) {
            fileSummary.setProcessedRecords(fileSummary.getProcessedRecords() + 1);
        }
    }
}
