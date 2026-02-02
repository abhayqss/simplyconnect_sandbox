package com.scnsoft.eldermark.listener;

import com.scnsoft.eldermark.service.twilio.ActiveVideoCallsSynchronizer;
import com.scnsoft.eldermark.service.twilio.LastMessageSynchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TwilioSynchronizerOnStartupListener {

    private static final Logger logger = LoggerFactory.getLogger(TwilioSynchronizerOnStartupListener.class);

    @Autowired
    private ActiveVideoCallsSynchronizer activeVideoCallsSynchronizer;

    @Autowired
    private LastMessageSynchronizer lastMessageSynchronizer;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void synchronizeActiveCalls() {
        logger.info("Synchronizing Twilio and Db active calls...");
        activeVideoCallsSynchronizer.synchronizeActiveCalls();
        logger.info("Active calls synchronization completed");
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void synchronizeLastMessages() {
        logger.info("Synchronizing Twilio and Db last message indexes...");
        lastMessageSynchronizer.synchronizeLastMessageIndexes();
        logger.info("Messages indexes synchronization completed");
    }
}
