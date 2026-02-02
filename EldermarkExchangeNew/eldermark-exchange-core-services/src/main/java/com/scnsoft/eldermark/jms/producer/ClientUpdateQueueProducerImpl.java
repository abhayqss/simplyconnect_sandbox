package com.scnsoft.eldermark.jms.producer;

import com.scnsoft.eldermark.jms.dto.ResidentUpdateQueueDto;
import com.scnsoft.eldermark.jms.dto.ResidentUpdateType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Set;

@Service
public class ClientUpdateQueueProducerImpl implements ClientUpdateQueueProducer {

    private static final Logger logger = LoggerFactory.getLogger(ClientUpdateQueueProducerImpl.class);

    private final JmsTemplate jmsTemplate;
    private final String queueDestination;
    private final boolean jmsSendingEnabled;

    @Autowired
    public ClientUpdateQueueProducerImpl(JmsTemplate jmsTemplate,
                                         @Value("${jms.queue.residentUpdate.destination}") String queueDestination,
                                         @Value("${jms.sending.enabled}") boolean jmsSendingEnabled) {
        this.jmsTemplate = jmsTemplate;
        this.queueDestination = queueDestination;
        this.jmsSendingEnabled = jmsSendingEnabled;
    }

    @Override
    public boolean putToResidentUpdateQueue(ResidentUpdateQueueDto residentUpdateDto) {
        if (!jmsSendingEnabled) {
            logger.info("Called ResidentUpdateQueueProducerImpl.putToResidentUpdateQueue - jms is disabled.");
            return true;
        }
        if (CollectionUtils.isEmpty(residentUpdateDto.getUpdateTypes())) {
            logger.info("Attempt to send ResidentUpdate with empty types - won't push to queue");
            return true;
        }
        try {
            jmsTemplate.convertAndSend(queueDestination, residentUpdateDto);
            return true;
        } catch (JmsException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    @Override
    public boolean putToResidentUpdateQueue(Long residentId, Set<ResidentUpdateType> updateTypes) {
        return putToResidentUpdateQueue(new ResidentUpdateQueueDto(residentId, updateTypes, System.currentTimeMillis()));
    }

    @Override
    public boolean putToResidentUpdateQueue(Long residentId, ResidentUpdateType updateType) {
        return putToResidentUpdateQueue(residentId, EnumSet.of(updateType));
    }

}
