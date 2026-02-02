package com.scnsoft.eldermark.services.inbound.document;

import com.scnsoft.eldermark.dao.inbound.document.DocumentAssignmentEmailSettingsDao;
import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.inbound.document.DocumentAssignmentInboundFile;
import com.scnsoft.eldermark.entity.inbound.document.DocumentAssignmentLog;
import com.scnsoft.eldermark.entity.inbound.document.email.DocumentAssignmentEmailSetting;
import com.scnsoft.eldermark.entity.inbound.document.email.DocumentAssignmentNotificationTrigger;
import com.scnsoft.eldermark.entity.inbound.document.summary.DocumentAssignmentProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.marco.MarcoDocumentEmailDto;
import com.scnsoft.eldermark.entity.inbound.summary.ProcessingSummary;
import com.scnsoft.eldermark.exception.integration.inbound.document.DocumentAssignmentErrorType;
import com.scnsoft.eldermark.exception.integration.inbound.document.DocumentAssignmentInboundException;
import com.scnsoft.eldermark.services.DocumentService;
import com.scnsoft.eldermark.services.inbound.AbstractInboundFilesProcessingService;
import com.scnsoft.eldermark.services.inbound.InboundFileGateway;
import com.scnsoft.eldermark.services.mail.ExchangeMailService;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Conditional(DocumentAssignmentRunCondition.class)
public class DocumentAssignmentProcessingService extends AbstractInboundFilesProcessingService<DocumentAssignmentInboundFile, DocumentAssignmentProcessingSummary> {

    private static final Logger logger = LoggerFactory.getLogger(DocumentAssignmentProcessingService.class);

    @Autowired
    private InboundFileGateway<DocumentAssignmentInboundFile, DocumentAssignmentProcessingSummary> inboundFileGateway;

    @Autowired
    private DocumentsAssignmentService documentsAssignmentService;

    @Autowired(required = false)
    private List<DocumentAssignmentListener> listeners;

    @Autowired
    private ExchangeMailService exchangeMailService;

    @Autowired
    private DocumentAssignmentEmailSettingsDao emailSettingsDao;

    @Autowired
    private DocumentService documentService;

    @Override
    protected List<DocumentAssignmentInboundFile> loadFiles() {
        return inboundFileGateway.loadFiles();
    }

    @Override
    protected DocumentAssignmentProcessingSummary process(DocumentAssignmentInboundFile inboundFile) {
        DocumentAssignmentProcessingSummary summary = new DocumentAssignmentProcessingSummary();
        DocumentAssignmentLog documentAssignmentLog;
        try {
            summary.setFileName(inboundFile.getFile().getName());

            Document document = documentsAssignmentService.uploadDocument(inboundFile);

            summary.setStatus(ProcessingSummary.ProcessingStatus.OK);
            summary.setAssigned(true);
            summary.setDocumentId(document.getId());
            summary.setResidentId(documentService.getResident(document.getId()));

            documentAssignmentLog = documentsAssignmentService.createDocumentAssignmentLog(inboundFile, document);

            notifyListeners(document);
        } catch (Exception ex) {
            logger.warn("Couldn't assign document:", ex);
            fillProcessingSummaryErrorFields(summary, ex);
            summary.setStatus(ProcessingSummary.ProcessingStatus.WARN);

            documentAssignmentLog = documentsAssignmentService.createDocumentAssignmentLog(inboundFile, resolveUnassignedReason(ex));
        }
        summary.setDocumentAssignmentLog(documentAssignmentLog);
        summary.setDocumentAssignmentLogId(documentAssignmentLog.getId());
        return summary;
    }

    private DocumentAssignmentErrorType resolveUnassignedReason(Exception ex) {
        if (ex instanceof DocumentAssignmentInboundException) {
            return ((DocumentAssignmentInboundException) ex).getUnassignedReason();
        }
        return DocumentAssignmentErrorType.INTERNAL_ERROR;
    }

    private void notifyListeners(Document document) {
        if (CollectionUtils.isNotEmpty(listeners)) {
            for (DocumentAssignmentListener listener : listeners) {
                try {
                    listener.postSuccessfulProcessing(document);
                } catch (Exception e) {
                    logger.warn("Exception during postSuccessfulProcessing of {}", listener.getClass().getName(), e);
                }
            }
        }
    }

    @Override
    protected void afterProcessingStatusOk(DocumentAssignmentInboundFile remoteFile, DocumentAssignmentProcessingSummary processingSummary) {
        logger.info("Successfully processed {} ", remoteFile.getFile().getName());

        inboundFileGateway.afterProcessingStatusOk(remoteFile, processingSummary);

        sendEmailNotifications(remoteFile, processingSummary);
    }

    @Override
    protected void afterProcessingStatusWarn(DocumentAssignmentInboundFile remoteFile, DocumentAssignmentProcessingSummary processingSummary) {
        logger.info("Processed {} with warnings ", remoteFile.getFile().getName());

        inboundFileGateway.afterProcessingStatusWarn(remoteFile, processingSummary);
        sendEmailNotifications(remoteFile, processingSummary);
    }

    @Override
    protected void afterProcessingStatusError(DocumentAssignmentInboundFile remoteFile, Exception exception) {
        DocumentAssignmentProcessingSummary summary = new DocumentAssignmentProcessingSummary();
        fillProcessingSummaryErrorFields(summary, exception);
        summary.setFileName(remoteFile.getFile().getName());

        inboundFileGateway.afterProcessingStatusError(remoteFile, summary);
        sendEmailNotifications(remoteFile, summary);
    }

    private void sendEmailNotifications(DocumentAssignmentInboundFile remoteFile, DocumentAssignmentProcessingSummary processingSummary) {
        final List<MarcoDocumentEmailDto> emailDtoList = buildNotificationDtos(remoteFile, processingSummary);
        for (MarcoDocumentEmailDto emailDto : emailDtoList) {
            // marco template is used because it complies to what is needed
            exchangeMailService.sendMarcoNotification(emailDto);
        }
    }

    private List<MarcoDocumentEmailDto> buildNotificationDtos(DocumentAssignmentInboundFile remoteFile, DocumentAssignmentProcessingSummary summary) {
        final String organizationName = summary.getDocumentAssignmentLog().getOrganizationName();
        List<DocumentAssignmentEmailSetting> settings = emailSettingsDao.findAllByDatabaseNameAndNotificationTriggerAndDisabledIsFalse(
                organizationName,
                summary.getAssigned() ? DocumentAssignmentNotificationTrigger.ON_ASSIGNED : DocumentAssignmentNotificationTrigger.ON_UNASSIGNED);

        final List<MarcoDocumentEmailDto> result = new ArrayList<>();

        for (DocumentAssignmentEmailSetting setting : settings) {
            result.add(buildNotificationDto(setting, remoteFile, summary));
        }

        return result;
    }

    private MarcoDocumentEmailDto buildNotificationDto(DocumentAssignmentEmailSetting settings, DocumentAssignmentInboundFile remoteFile, DocumentAssignmentProcessingSummary summary) {
        final MarcoDocumentEmailDto result = new MarcoDocumentEmailDto();
        result.setToEmail(settings.getEmail());

        result.setAssigned(summary.getAssigned());
        result.setFileName(summary.getFileName());

        result.setRecipientName(settings.getRecipientName());

        final DocumentAssignmentLog documentAssignmentLog = summary.getDocumentAssignmentLog();

        if (documentAssignmentLog.getDocument() != null) {
            final Document document = documentAssignmentLog.getDocument();
            final Resident resident = documentService.getResident(document);
            result.setPatientInitials(CareCoordinationUtils.getResidentInitials(resident));
            result.setDocumentTitle(document.getDocumentTitle());
            result.setMpiPatientId(remoteFile.getMpiPatientId());
        }

        if (documentAssignmentLog.getUnassignedReason() != null) {
            result.setErrorMessage(documentAssignmentLog.getUnassignedReason().message());
        }

        result.setSubject(settings.getSubject());
        result.setOrganzationName(documentAssignmentLog.getOrganizationName());
        return result;
    }

}
