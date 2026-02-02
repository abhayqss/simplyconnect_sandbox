package com.scnsoft.eldermark.services.jms.producer.impl;

import com.scnsoft.eldermark.services.integration.qualifacts.QualifactsIntegrationEnabledCondition;
import com.scnsoft.eldermark.services.jms.producer.QualifactsDocumentUploadQueueProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@Conditional(QualifactsIntegrationEnabledCondition.class)
public class QualifactsDocumentUploadQueueProducerImpl implements QualifactsDocumentUploadQueueProducer {

    private static final Logger logger = LoggerFactory.getLogger(QualifactsDocumentUploadQueueProducerImpl.class);

    private final JmsTemplate jmsTemplate;
    private final String queueDestination;
    private final boolean enabled;

    @Autowired
    public QualifactsDocumentUploadQueueProducerImpl(JmsTemplate jmsTemplate,
                                                     @Value("${jms.queue.qualifactsDocumentUpload.destination}") String queueDestination,
                                           @Value("${jms.enabled}") boolean enabled) {
        this.jmsTemplate = jmsTemplate;
        this.queueDestination = queueDestination;
        this.enabled = enabled;
    }

    @Override
    public void putToQueue(Long documentId) {
        if (!enabled) {
            logger.info("Called QualifactsDocumentUploadQueueProducerImpl.putToQueue - jms is disabled.");
            return;
        }
        logger.debug("Sending {} to QualifactsDocumentUpload queue");
        jmsTemplate.convertAndSend(queueDestination, documentId);
    }
}
