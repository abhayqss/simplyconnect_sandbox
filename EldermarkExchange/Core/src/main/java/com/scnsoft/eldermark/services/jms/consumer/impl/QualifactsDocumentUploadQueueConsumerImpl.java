package com.scnsoft.eldermark.services.jms.consumer.impl;

import com.scnsoft.eldermark.dao.DocumentDao;
import com.scnsoft.eldermark.exception.integration.qualifacts.DocumentWithoutResidentException;
import com.scnsoft.eldermark.exception.integration.qualifacts.MissingClientIdException;
import com.scnsoft.eldermark.services.integration.qualifacts.QualifactsDocumentsGateway;
import com.scnsoft.eldermark.services.integration.qualifacts.QualifactsIntegrationEnabledCondition;
import com.scnsoft.eldermark.services.jms.consumer.QualifactsDocumentUploadQueueConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@Conditional(QualifactsIntegrationEnabledCondition.class)
public class QualifactsDocumentUploadQueueConsumerImpl implements QualifactsDocumentUploadQueueConsumer {

    private static final Logger logger = LoggerFactory.getLogger(QualifactsDocumentUploadQueueConsumerImpl.class);
    private final QualifactsDocumentsGateway qualifactsDocumentsGateway;
    private final DocumentDao documentDao;

    @Autowired
    public QualifactsDocumentUploadQueueConsumerImpl(QualifactsDocumentsGateway qualifactsDocumentsGateway, DocumentDao documentDao) {
        this.qualifactsDocumentsGateway = qualifactsDocumentsGateway;
        this.documentDao = documentDao;
    }

    @Override
    @JmsListener(
            destination = "${jms.queue.qualifactsDocumentUpload.destination}",
            concurrency = "${jms.queue.qualifactsDocumentUpload.concurrency}",
            containerFactory = "defaultJmsListenerContainerFactory"
    )
    public void consumeDocumentId(Long documentId) {
        logger.info("received {} from QualifactsDocumentUpload queue", documentId);
        try {
            qualifactsDocumentsGateway.sendDocumentToQualifacts(documentDao.findDocument(documentId));
        } catch (MissingClientIdException e) {
            logger.info("Attempt to send LSSI document, but client didn't have LSSI id in MPI", e);
        } catch (DocumentWithoutResidentException e) {
            logger.info("Attempt to send LSSI document, but document has no resident", e);
        }
    }

}
