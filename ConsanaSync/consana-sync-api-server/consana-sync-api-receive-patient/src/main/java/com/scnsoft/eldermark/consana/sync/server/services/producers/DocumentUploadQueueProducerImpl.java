package com.scnsoft.eldermark.consana.sync.server.services.producers;

import com.scnsoft.eldermark.consana.sync.server.common.model.dto.DocumentUploadQueueDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(noRollbackFor = Exception.class)
public class DocumentUploadQueueProducerImpl implements DocumentUploadQueueProducer {

    private static final Logger logger = LoggerFactory.getLogger(DocumentUploadQueueProducerImpl.class);

    @Value("${queue.documentUpload.destination}")
    private String queueDestination;

    private final JmsTemplate jmsTemplate;

    @Autowired
    public DocumentUploadQueueProducerImpl(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void send(DocumentUploadQueueDto uploadDocumentData) {
        logger.info("Sending to {} queue: {}", queueDestination, uploadDocumentData);
        jmsTemplate.convertAndSend(queueDestination, uploadDocumentData);
    }

}
