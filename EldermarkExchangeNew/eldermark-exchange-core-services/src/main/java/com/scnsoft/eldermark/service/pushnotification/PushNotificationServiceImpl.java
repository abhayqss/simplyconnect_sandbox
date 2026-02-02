package com.scnsoft.eldermark.service.pushnotification;

import com.scnsoft.eldermark.dto.notification.PushNotificationVO;
import com.scnsoft.eldermark.service.pushnotification.sender.PushNotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Function;

@Service
public class PushNotificationServiceImpl implements PushNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationServiceImpl.class);

    @Autowired
    private List<PushNotificationSender> pushNotificationSenders;

    @Async
    @Override
    @Transactional
    public Future<SendPushNotificationResult> send(PushNotificationVO pushNotificationVO) {
        return sendAndMapResult(pushNotificationVO, AsyncResult::new);
    }

    @Override
    @Transactional
    public SendPushNotificationResult sendAndWait(PushNotificationVO pushNotificationVO) {
        return sendAndMapResult(pushNotificationVO, Function.identity());
    }

    private <T> T sendAndMapResult(PushNotificationVO pushNotificationVO, Function<SendPushNotificationResult, T> resultMapper) {
        var result = new SendPushNotificationResult();

        for (var sender : pushNotificationSenders) {
            //todo - handle exceptions inside senders so that transaction is not rolled back?
            try {
                result.merge(sender.sendAndWait(pushNotificationVO));
            } catch (Exception e) {
                logger.error("Exception during sending push notification by sender {}", sender.getClass().getSimpleName(), e);
                result.getExceptions().add(e);
            }
        }

        return resultMapper.apply(result);
    }
}
