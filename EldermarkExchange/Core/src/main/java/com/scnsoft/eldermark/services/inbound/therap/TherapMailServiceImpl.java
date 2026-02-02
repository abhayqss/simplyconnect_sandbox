package com.scnsoft.eldermark.services.inbound.therap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scnsoft.eldermark.dao.DatabaseJpaDao;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.inbound.summary.ProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.TherapMailNotificationDto;
import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapEntitiesProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapEntityFileProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapEntityRecordProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapTotalProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.event.TherapEventFileProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.event.TherapEventsProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.idf.TherapIdfFileProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.idf.TherapIdfsProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.programenrollment.TherapProgramEnrollmentFileProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.programenrollment.TherapProgramEnrollmentsProcessingSummary;
import com.scnsoft.eldermark.services.inbound.ReportService;
import com.scnsoft.eldermark.services.mail.ExchangeMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Provider;
import java.util.*;

@Service
@Conditional(TherapInboundFilesServiceRunCondition.class)
public class TherapMailServiceImpl implements TherapMailService {

    private static final Logger logger = LoggerFactory.getLogger(TherapMailServiceImpl.class);

    private final ExchangeMailService exchangeMailService;
    private final DatabaseJpaDao databaseJpaDao;
    private final EmployeeDao employeeDao;
    private final ReportService reportService;

    @Value("${therap.sftp.workingDirectory}")
    private String sftpBaseFolder;

    @Value("${therap.sftp.reportfile.postfix}")
    private String reportPostfix;

    @Value("${simplyconnect.devteam.email}")
    private String simplyConnectEmail;

    @Value("${simplyconnect.devteam.name}")
    private String simplyConnectTeamName;

    @Autowired
    public TherapMailServiceImpl(ExchangeMailService exchangeMailService, DatabaseJpaDao databaseJpaDao, EmployeeDao employeeDao, ReportService reportService) {
        this.exchangeMailService = exchangeMailService;
        this.databaseJpaDao = databaseJpaDao;
        this.employeeDao = employeeDao;
        this.reportService = reportService;
    }

    @Override
    @Transactional(readOnly = true)
    public void sendEmailNotifications(TherapTotalProcessingSummary summary) {
        List<TherapMailNotificationDto> mailDtoList;

        if (ProcessingSummary.ProcessingStatus.ERROR.equals(summary.getStatus())) {
            mailDtoList = createErrorNotificationList(summary);
        } else {
            mailDtoList = createNotificationList(summary);
        }

        for (TherapMailNotificationDto mailDto : mailDtoList) {
            exchangeMailService.sendTherapNotification(mailDto);
        }
    }

    private List<TherapMailNotificationDto> createErrorNotificationList(TherapTotalProcessingSummary summary) {
        final TherapMailNotificationDto errorDto = createSimplyConnectMailDto(summary, buildErrorReport(summary));
        return Collections.singletonList(errorDto);
    }

    private List<TherapMailNotificationDto> createNotificationList(TherapTotalProcessingSummary summary) {
        final List<TherapMailNotificationDto> result = new ArrayList<>();

        final Map<String, TherapTotalProcessingSummary> summariesForOrganizations = splitSummaryForOrganizations(summary);

        for (Map.Entry<String, TherapTotalProcessingSummary> entry : summariesForOrganizations.entrySet()) {
            final byte[] errorReportBytes;

            final Database database = resolveDatabase(entry.getKey());
            if (database != null) {
                final List<Employee> administrators = employeeDao.getAdministrators(database.getId());
                errorReportBytes = buildErrorReport(entry.getValue());

                for (Employee administrator : administrators) {
                    final TherapMailNotificationDto therapMailNotificationDto = createMailDto(entry.getValue(), administrator, errorReportBytes);
                    if (therapMailNotificationDto != null) {
                        result.add(therapMailNotificationDto);
                    }
                }
            } else {
                entry.getValue().setStatus(ProcessingSummary.ProcessingStatus.ERROR);
                entry.getValue().setMessage("Couldn't resolve Organization with code [" + entry.getKey() + "]");
                errorReportBytes = buildErrorReport(entry.getValue());
            }

            result.add(createSimplyConnectMailDto(entry.getValue(), errorReportBytes));
        }
        return result;

    }

    private Database resolveDatabase(String key) {
        logger.info("Looking for database with oid = [{}]", key);
        Database result = databaseJpaDao.findByOid(key);
        return result;
    }

    private Map<String, TherapTotalProcessingSummary> splitSummaryForOrganizations(TherapTotalProcessingSummary summary) {
        final Map<String, TherapTotalProcessingSummary> result = new HashMap<>();
        final Set<String> databaseCodes = fetchDatabaseCodes(summary);
        for (String databaseOid : databaseCodes) {
            final TherapTotalProcessingSummary filteredSummary = new TherapTotalProcessingSummary();

            filteredSummary.setFileName(summary.getFileName());
            filteredSummary.setProcessedAt(summary.getProcessedAt());

            filteredSummary.setEnrollmentsProcessingSummary(filterEntitiesProcessingSummary(
                    summary.getEnrollmentsProcessingSummary(),
                    databaseOid,
                    new TherapProgramEnrollmentsProcessingSummary(),
                    new Provider<TherapProgramEnrollmentFileProcessingSummary>() {
                        @Override
                        public TherapProgramEnrollmentFileProcessingSummary get() {
                            return new TherapProgramEnrollmentFileProcessingSummary();
                        }
                    }));

            filteredSummary.setIdfsProcessingSummary(filterEntitiesProcessingSummary(
                    summary.getIdfsProcessingSummary(),
                    databaseOid,
                    new TherapIdfsProcessingSummary(),
                    new Provider<TherapIdfFileProcessingSummary>() {
                        @Override
                        public TherapIdfFileProcessingSummary get() {
                            return new TherapIdfFileProcessingSummary();
                        }
                    }));

            filteredSummary.setEventsProcessingSummary(filterEntitiesProcessingSummary(
                    summary.getEventsProcessingSummary(),
                    databaseOid,
                    new TherapEventsProcessingSummary(),
                    new Provider<TherapEventFileProcessingSummary>() {
                        @Override
                        public TherapEventFileProcessingSummary get() {
                            return new TherapEventFileProcessingSummary();
                        }
                    }));

            filteredSummary.propagateStatusAndMessage();
            if (!ProcessingSummary.ProcessingStatus.OK.equals(filteredSummary.getStatus())) {
                result.put(databaseOid, filteredSummary);
            }
        }
        return result;
    }

    private <R extends TherapEntityFileProcessingSummary, T extends TherapEntitiesProcessingSummary<R>> T filterEntitiesProcessingSummary(
            T entitiesProcessingSummary, String databaseOid, T filtered, Provider<R> entityFileConstructor) {
        if (entitiesProcessingSummary == null) {
            return null;
        }
        for (R fileSummary : entitiesProcessingSummary.getFilesProcessingSummary()) {
            if (databaseOid.equals(extractOrganizationCode(fileSummary.getFileName()))) {
                filtered.setFoundFiles(filtered.getFoundFiles() + 1);
                if (!ProcessingSummary.ProcessingStatus.ERROR.equals(fileSummary.getStatus())) {
                    filtered.setProcessedFiles(filtered.getProcessedFiles() + 1);
                }
                if (!ProcessingSummary.ProcessingStatus.OK.equals(fileSummary.getStatus())) {
                    filtered.getFilesProcessingSummary().add((R) filterEntityFileProcessingSummary(fileSummary, entityFileConstructor.get()));
                }
            }
        }
        filtered.propagateStatusAndMessage();
        return filtered;
    }

    /**
     * Copies source fields to target with only not OK records
     *
     * @param source
     * @param target
     * @param <M>
     * @param <R>
     * @return
     */
    private <M extends TherapEntityRecordProcessingSummary, R extends TherapEntityFileProcessingSummary<M>> R filterEntityFileProcessingSummary(R source, R target) {
        target.setStatus(source.getStatus());
        target.setMessage(source.getMessage());
        target.setStackTrace(source.getStackTrace());

        target.setFileName(source.getFileName());
        target.setTotalRecords(source.getTotalRecords());
        target.setProcessedRecords(source.getProcessedRecords());

        for (M recordSummary : source.getRecordsProcessingSummary()) {
            if (!ProcessingSummary.ProcessingStatus.OK.equals(recordSummary.getStatus())) {
                target.getRecordsProcessingSummary().add(recordSummary);
            }
        }
        return target;
    }

    private Set<String> fetchDatabaseCodes(TherapTotalProcessingSummary summary) {
        final Set<String> result = new HashSet<>();

        if (summary.getEnrollmentsProcessingSummary() != null) {
            for (TherapProgramEnrollmentFileProcessingSummary fileProcessingSummary: summary.getEnrollmentsProcessingSummary().getFilesProcessingSummary()) {
                result.add(extractOrganizationCode(fileProcessingSummary.getFileName()));
            }
        }

        if (summary.getIdfsProcessingSummary() != null) {
            for (TherapIdfFileProcessingSummary fileProcessingSummary: summary.getIdfsProcessingSummary().getFilesProcessingSummary()) {
                result.add(extractOrganizationCode(fileProcessingSummary.getFileName()));
            }
        }

        if (summary.getEventsProcessingSummary() != null) {
            for (TherapEventFileProcessingSummary eventFileSummary : summary.getEventsProcessingSummary().getFilesProcessingSummary()) {
                result.add(extractOrganizationCode(eventFileSummary.getFileName()));
            }
        }

        return result;
    }

    private TherapMailNotificationDto createMailDto(TherapTotalProcessingSummary summary, Employee organizationAdmin, byte[] errorReport) {
        final String email = fetchEmail(organizationAdmin);
        if (email == null) {
            return null;
        }
        return createMailDto(summary, email, organizationAdmin.getFirstName(), errorReport);
    }

    private TherapMailNotificationDto createSimplyConnectMailDto(TherapTotalProcessingSummary summary, byte[] errorReport) {
        return createMailDto(summary, simplyConnectEmail, simplyConnectTeamName, errorReport);
    }

    private TherapMailNotificationDto createMailDto(TherapTotalProcessingSummary summary, String email, String name, byte[] errorReport) {
        final TherapMailNotificationDto mailNotificationDto = new TherapMailNotificationDto();

        mailNotificationDto.setRecipientEmail(email);
        mailNotificationDto.setRecipientName(name);

        mailNotificationDto.setFileName(summary.getFileName());
        mailNotificationDto.setStatus(summary.getStatus().toString());

        convertEntities(mailNotificationDto.getEnrollments(), summary.getEnrollmentsProcessingSummary());
        convertEntities(mailNotificationDto.getIdfs(), summary.getIdfsProcessingSummary());
        convertEntities(mailNotificationDto.getEvents(), summary.getEventsProcessingSummary());

        mailNotificationDto.setErrorReport(errorReport);

        return mailNotificationDto;
    }

    private <R extends TherapEntityFileProcessingSummary,
            T extends TherapEntitiesProcessingSummary<R>> void convertEntities(List<TherapMailNotificationDto.FileProcessingResultDto> list, T entitiesSummary) {
        if (entitiesSummary != null) {
            for (R fileSummary : entitiesSummary.getFilesProcessingSummary()) {
                TherapMailNotificationDto.FileProcessingResultDto fileSummaryDto = new TherapMailNotificationDto.FileProcessingResultDto();
                fileSummaryDto.setFileName(fetchLastNodeInPath(fileSummary.getFileName()));
                fileSummaryDto.setStatus(fileSummary.getStatus().toString());
                fileSummaryDto.setNotProcessed(fileSummary.getTotalRecords() - fileSummary.getProcessedRecords());
                fileSummaryDto.setTotal(fileSummary.getTotalRecords());
                list.add(fileSummaryDto);
            }
        }
    }

    private String fetchLastNodeInPath(String fileName) {
        return fileName.substring(fileName.lastIndexOf('/') + 1);
    }

    private String fetchEmail(Employee employee) {
        Person person = employee.getPerson();
        for (final PersonTelecom telecom : person.getTelecoms()) {
            if (telecom.getUseCode().equals(PersonTelecomCode.EMAIL.toString())) {
                return telecom.getValue();
            }
        }
        logger.warn("Couldn't fetch email for employee [{}]", employee.getId());
        return null;
    }

    private String extractOrganizationCode(String s) {
        final String[] folders = s.split("/");
        return folders[1].substring(0, folders[1].indexOf('$'));
    }

    private byte[] buildErrorReport(TherapTotalProcessingSummary summary) {
        try {
            final String report = reportService.createRemoteReport(summary);
            return report.getBytes();
        } catch (JsonProcessingException e) {
            logger.warn("Error during creating report for [{}]", summary, e);
        }
        return new byte[0];
    }
}
