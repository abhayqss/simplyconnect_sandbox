package com.scnsoft.eldermark.services.consana.impl;

import com.scnsoft.eldermark.services.consana.ResidentUpdateQueueProducer;
import com.scnsoft.eldermark.services.consana.model.ResidentUpdateQueueDto;
import com.scnsoft.eldermark.services.consana.model.ResidentUpdateType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

@Component
public class ResidentUpdateQueueProducerImpl implements ResidentUpdateQueueProducer {

    private static final Logger logger = LoggerFactory.getLogger(ResidentUpdateQueueProducerImpl.class);

    private final JmsTemplate jmsTemplate;
    private final String queueDestination;
    private final boolean enabled;

    @Autowired
    public ResidentUpdateQueueProducerImpl(JmsTemplate jmsTemplate, @Value("${jms.queue.residentUpdate}") String queueDestination,
                                           @Value("${jms.enabled}") boolean enabled) {
        this.jmsTemplate = jmsTemplate;
        this.queueDestination = queueDestination;
        this.enabled = enabled;
    }

    @Override
    public boolean putToResidentUpdateQueue(ResidentUpdateQueueDto residentUpdateDto) {
        if (!enabled) {
            logger.info("Called ResidentUpdateQueueProducerImpl.putToResidentUpdateQueue - jms is disabled.");
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
