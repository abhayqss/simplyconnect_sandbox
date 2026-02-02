package com.scnsoft.eldermark.services.apns;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.notnoop.apns.ApnsService;
import com.scnsoft.eldermark.entity.phr.ApnsModel;

import java.util.concurrent.Future;

@Service
public class ApnsNotificationServiceImpl implements ApnsNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(ApnsNotificationServiceImpl.class);

    @Autowired
    private ApnsServiceFactory apnsServiceFactory;

    @Async
    @Override
    public Future<Boolean> voipPush(ApnsModel payloadmodel) {
        logger.info("voip apns model service impl");
        if (CollectionUtils.isNotEmpty(payloadmodel.getToken())) {
            logger.info("try to creating voip apns service");
            ApnsService service = apnsServiceFactory.createApnsService();
            logger.info("voip apns service created");
            Map<String, Object> mapPayload = new HashMap<String, Object>();
            mapPayload.put(payloadmodel.getApnsKey(), new JSONObject(payloadmodel.getAps()));
            if (payloadmodel.getData() != null)
                mapPayload.put("data", new JSONObject(payloadmodel.getData()));
            String payload = new JSONObject(mapPayload).toString();
            logger.info("Try to sending voip message with this payload: {}", payload);
            logger.info("Try to sending voip message with this payload: {}", payload);
            service.push(payloadmodel.getToken(), payload);
            logger.info("APNS push notification sent.");
            logger.info("APNS push notification sent.");
            return new AsyncResult<>(true);
        }
        return new AsyncResult<>(false);
    }

}
