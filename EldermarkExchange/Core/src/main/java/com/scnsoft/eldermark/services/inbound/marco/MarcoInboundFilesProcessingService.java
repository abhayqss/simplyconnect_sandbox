package com.scnsoft.eldermark.services.inbound.marco;

import com.scnsoft.eldermark.dao.inbound.marco.MarcoEmailSettingsDao;
import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.MPI;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.inbound.marco.MarcoDocumentEmailDto;
import com.scnsoft.eldermark.entity.inbound.marco.MarcoInboundFile;
import com.scnsoft.eldermark.entity.inbound.marco.MarcoIntegrationDocument;
import com.scnsoft.eldermark.entity.inbound.marco.email.MarcoEmailNotificationTrigger;
import com.scnsoft.eldermark.entity.inbound.marco.email.MarcoEmailSettings;
import com.scnsoft.eldermark.entity.inbound.marco.summary.MarcoProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.summary.ProcessingSummary;
import com.scnsoft.eldermark.exception.integration.marco.MarcoInboundException;
import com.scnsoft.eldermark.exception.integration.marco.MarcoUnassignedReason;
import com.scnsoft.eldermark.services.DocumentService;
import com.scnsoft.eldermark.services.inbound.AbstractInboundFilesProcessingService;
import com.scnsoft.eldermark.services.inbound.InboundFileGateway;
import com.scnsoft.eldermark.services.jms.producer.QualifactsDocumentUploadQueueProducer;
import com.scnsoft.eldermark.services.mail.ExchangeMailService;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Conditional(MarcoInboundFilesServiceRunCondition.class)
public class MarcoInboundFilesProcessingService extends AbstractInboundFilesProcessingService<MarcoInboundFile, MarcoProcessingSummary> {

    private static final Logger logger = LoggerFactory.getLogger(MarcoInboundFilesProcessingService.class);
    private final XStream xStream;

    @Value("${qualifacts.sftp.lssi.oid}")
    private String lssiDatabaseOid;

    private final InboundFileGateway<MarcoInboundFile, MarcoProcessingSummary> inboundFileGateway;
    private final MarcoDocumentsService marcoDocumentsService;
    private final DocumentService documentService;
    private final ExchangeMailService exchangeMailService;
    private final MPIService mpiService;

    private QualifactsDocumentUploadQueueProducer queueProducer;


    @Autowired
    public MarcoInboundFilesProcessingService(XStream xStream,
                                              InboundFileGateway<MarcoInboundFile, MarcoProcessingSummary> inboundFileGateway,
                                              MarcoDocumentsService marcoDocumentsService,
                                              DocumentService documentService,
                                              ExchangeMailService exchangeMailService, MPIService mpiService) {
        this.xStream = xStream;
        this.inboundFileGateway = inboundFileGateway;
        this.marcoDocumentsService = marcoDocumentsService;
        this.documentService = documentService;
        this.exchangeMailService = exchangeMailService;
        this.mpiService = mpiService;
    }

    @Autowired(required = false)
    public void setQueueProducer(QualifactsDocumentUploadQueueProducer queueProducer) {
        this.queueProducer = queueProducer;
    }

    @Override
    protected List<MarcoInboundFile> loadFiles() {
        return inboundFileGateway.loadFiles();
    }

    @Override
    protected MarcoProcessingSummary process(MarcoInboundFile inboundFile) {
        final MarcoProcessingSummary marcoProcessingSummary = new MarcoProcessingSummary();
        MarcoIntegrationDocument marcoIntegrationDocument;
        final MarcoDocumentMetadata metadata = createMetadata(inboundFile.getMetadataFile());

        try {
            marcoProcessingSummary.setFileName(inboundFile.getDocument().getName());
            marcoProcessingSummary.setMetadataFileName(inboundFile.getMetadataFile().getName());

            final Document assignedDocument = marcoDocumentsService.uploadDocument(metadata, inboundFile.getDocument());

            marcoIntegrationDocument = marcoDocumentsService.createNewMarcoIntegrationDocumentLog(
                    metadata,
                    assignedDocument
            );

            marcoProcessingSummary.setStatus(ProcessingSummary.ProcessingStatus.OK);
            marcoProcessingSummary.setAssigned(true);
            marcoProcessingSummary.setDocumentId(assignedDocument.getId());
            marcoProcessingSummary.setResidentId(documentService.getResident(assignedDocument.getId()));

            if (queueProducer != null) {
                queueProducer.putToQueue(assignedDocument.getId());
            }

        } catch (MarcoInboundException me) {
            logger.warn("Couldn't upload marco document: {}", me.getMessage());
            fillProcessingSummaryErrorFields(marcoProcessingSummary, me);
            marcoProcessingSummary.setStatus(ProcessingSummary.ProcessingStatus.WARN);

            marcoIntegrationDocument = marcoDocumentsService.createNewMarcoIntegrationDocumentLog(
                    metadata,
                    me.getUnassignedReason()
            );

        } catch (Exception e) {
            logger.warn("Couldn't upload marco document: {}", e.getMessage());
            fillProcessingSummaryErrorFields(marcoProcessingSummary, e);
            marcoProcessingSummary.setStatus(ProcessingSummary.ProcessingStatus.WARN);

            marcoIntegrationDocument = marcoDocumentsService.createNewMarcoIntegrationDocumentLog(
                    metadata,
                    MarcoUnassignedReason.INTERNAL_ERROR
            );
        }

        marcoProcessingSummary.setMarcoIntegrationDocumentId(marcoIntegrationDocument.getId());
        marcoProcessingSummary.setMarcoIntegrationDocument(marcoIntegrationDocument);
        return marcoProcessingSummary;
    }

    private MarcoDocumentMetadata createMetadata(File metadataFile) {
        final MarcoDocumentMetadata metadata = (MarcoDocumentMetadata) xStream.fromXML(metadataFile);
        return metadata;
    }

    @Override
    protected void afterProcessingStatusOk(MarcoInboundFile remoteFile, MarcoProcessingSummary processingSummary) {
        logger.info("Successfully processed {} ", remoteFile.getDocument().getName());

        inboundFileGateway.afterProcessingStatusOk(remoteFile, processingSummary);

        sendEmailNotifications(processingSummary);
    }

    @Override
    protected void afterProcessingStatusWarn(MarcoInboundFile remoteFile, MarcoProcessingSummary processingSummary) {
        logger.info("Processed {} with warnings ", remoteFile.getDocument().getName());

        inboundFileGateway.afterProcessingStatusWarn(remoteFile, processingSummary);
        sendEmailNotifications(processingSummary);
    }

    @Override
    protected void afterProcessingStatusError(MarcoInboundFile remoteFile, Exception exception) {
        logger.warn("Processed {} with errors ", remoteFile.getDocument().getName(), exception);

        MarcoProcessingSummary summary = new MarcoProcessingSummary();
        fillProcessingSummaryErrorFields(summary, exception);

        summary.setFileName(remoteFile.getDocument().getName());
        summary.setMetadataFileName(remoteFile.getMetadataFile().getName());

        inboundFileGateway.afterProcessingStatusError(remoteFile, summary);
        sendEmailNotifications(summary);
    }

    private void sendEmailNotifications(MarcoProcessingSummary processingSummary) {
        final List<MarcoDocumentEmailDto> emailDtoList = buildNotificationDtos(processingSummary);
        for (MarcoDocumentEmailDto emailDto : emailDtoList) {
            exchangeMailService.sendMarcoNotification(emailDto);
        }

    }

    @Autowired
    private MarcoEmailSettingsDao marcoEmailSettingsDao;

    private List<MarcoDocumentEmailDto> buildNotificationDtos(MarcoProcessingSummary summary) {
        if (summary.getMarcoIntegrationDocument() == null) {
            logger.warn("Marco integration document is null - no notifications will be sent");
            return Collections.emptyList();
        }
        final String organizationName = summary.getMarcoIntegrationDocument().getOrganizationName();
        List<MarcoEmailSettings> settings = marcoEmailSettingsDao.findAllByDatabaseNameAndNotificationTrigger(organizationName,
                summary.isAssigned() ? MarcoEmailNotificationTrigger.ON_ASSIGNED : MarcoEmailNotificationTrigger.ON_UNASSIGNED);

        final List<MarcoDocumentEmailDto> result = new ArrayList<>();

        for (MarcoEmailSettings setting : settings) {
            result.add(buildNotificationDto(setting, summary));
        }

        return result;
    }

    private MarcoDocumentEmailDto buildNotificationDto(MarcoEmailSettings settings, MarcoProcessingSummary summary) {
        final MarcoDocumentEmailDto result = new MarcoDocumentEmailDto();
        result.setToEmail(settings.getEmail());

        result.setAssigned(summary.isAssigned());
        result.setFileName(summary.getFileName());

        result.setRecipientName(settings.getRecipientName());

        final MarcoIntegrationDocument marcoIntegrationDocument = summary.getMarcoIntegrationDocument();

        if (marcoIntegrationDocument.getDocument() != null) {
            final Document document = marcoIntegrationDocument.getDocument();
            final Resident resident = documentService.getResident(document);
            result.setPatientInitials(CareCoordinationUtils.getResidentInitials(resident));
            result.setDocumentTitle(document.getDocumentTitle());

            final MPI mpi = mpiService.findMpiForResidentOrMergedAndDatabaseOid(resident.getId(), lssiDatabaseOid);
            if (mpi != null) {
                result.setMpiPatientId(mpi.getPatientId());
            }
        }

        if (marcoIntegrationDocument.getUnassignedReason() != null) {
            result.setErrorMessage(marcoIntegrationDocument.getUnassignedReason().message());
        }

        result.setSubject(settings.getSubject());
        result.setOrganzationName(marcoIntegrationDocument.getOrganizationName());

        return result;
    }

}
