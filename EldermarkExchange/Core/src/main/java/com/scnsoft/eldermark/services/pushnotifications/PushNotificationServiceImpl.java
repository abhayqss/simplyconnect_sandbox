package com.scnsoft.eldermark.services.pushnotifications;

import com.scnsoft.eldermark.dao.phr.PushNotificationRegistrationDao;
import com.scnsoft.eldermark.entity.phr.PushNotificationRegistration;
import com.scnsoft.eldermark.shared.json.FcmResult;
import com.scnsoft.eldermark.shared.json.FcmSuccessMessageDto;
import com.scnsoft.eldermark.shared.phr.PushNotificationVO;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.raudi.pushraven.FcmResponse;
import us.raudi.pushraven.Notification;
import us.raudi.pushraven.Pushraven;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import static com.scnsoft.eldermark.entity.phr.PushNotificationRegistration.ServiceProvider.FCM;

/**
 * Firebase push notifications service
 *
 * @author phomal Created on 6/8/2017.
 */
@Service
public class PushNotificationServiceImpl implements PushNotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationServiceImpl.class);
    
    @Autowired
    private PushNotificationRegistrationDao pushNotificationRegistrationDao;
    
    @Value("${fcm.server.key}")
    private String fcmServerKey;
    
    @PostConstruct
    public void initPushraven() {
        Pushraven.setKey(fcmServerKey);
    }
    
    @Override
    public Collection<String> getTokens(Long userId, PushNotificationRegistration.ServiceProvider serviceProvider) {
        final List<PushNotificationRegistration> registrations = pushNotificationRegistrationDao
                .findByUserIdAndServiceProviderAndAppName(userId, serviceProvider, PushNotificationRegistration.PHR_APP);
        List<String> tokens = new ArrayList<String>();
        CollectionUtils.collect(registrations, new BeanToPropertyValueTransformer("regId"), tokens);
        return tokens;
    }
    
    private void evictTokens(List<String> tokens, PushNotificationRegistration.ServiceProvider serviceProvider) {
        pushNotificationRegistrationDao.deleteByDeviceTokenAndServiceProviderAndAppName(tokens, serviceProvider,
                PushNotificationRegistration.PHR_APP);
    }
    
    @Async
    @Override
    @Transactional
    public Future<Boolean> send(PushNotificationVO pushNotificationVO) {
        final List<String> tokens = pushNotificationVO.getTokens();

        if (CollectionUtils.isEmpty(tokens)) {
            return new AsyncResult<Boolean>(false);
        }
        if (!FCM.equals(pushNotificationVO.getServiceProvider())) {
            logger.error("Not implemented: " + pushNotificationVO.getServiceProvider());
            return new AsyncResult<Boolean>(false);
        }
        
        Notification raven = new Notification().title(pushNotificationVO.getTitle()).text(pushNotificationVO.getText())
                // .tag(pushNotificationVO.getTag())                              
                .data(pushNotificationVO.getPayload())                
                .priority(10);
        
        if(pushNotificationVO.getNotification() != null) {
            raven = new Notification().title(pushNotificationVO.getTitle()).text(pushNotificationVO.getText())
                    // .tag(pushNotificationVO.getTag())
                    .notification(pushNotificationVO.getNotification()) 
                    .sound("default")
                    .data(pushNotificationVO.getPayload())
                    .sound("default")
                    .priority(10);
        }
        boolean multicast = tokens.size() > 1;
        if (!multicast) {
            raven.to(tokens.get(0));
        } else {
            raven.addAllMulticasts(tokens);
        }
        logger.debug("Let's push a raven: " + raven.toJSON());
        
        final FcmResponse response = Pushraven.push(raven);
        if (response.getResponseCode() != 200) {
            logger.warn(response.toString());
            
            return new AsyncResult<Boolean>(false);
        } else {
            final String json = response.getSuccessResponseMessage();
            final FcmSuccessMessageDto messageDto = FcmSuccessMessageDto.fromJsonString(json);
            if (messageDto == null) {
                return new AsyncResult<Boolean>(false);
            } else if (messageDto.getFailure() == 0) {
                return new AsyncResult<Boolean>(messageDto.getSuccess() > 0);
            }
            
            List<String> badTokens = new ArrayList<String>();
            final Iterator<String> tokenIterator = tokens.iterator();
            final Iterator<FcmResult> resultIterator = messageDto.getResults().iterator();
            while (tokenIterator.hasNext() && resultIterator.hasNext()) {
                final FcmResult result = resultIterator.next();
                final String token = tokenIterator.next();
                if ("NotRegistered".equals(result.getError()) || "InvalidRegistration".equals(result.getError())) {
                    badTokens.add(token);
                }
            }
            if (CollectionUtils.isNotEmpty(badTokens)) {
                logger.info("Evicting bad tokens. FCM success response: {}", json);
                evictTokens(badTokens, pushNotificationVO.getServiceProvider());
            }
            
            return new AsyncResult<Boolean>(messageDto.getSuccess() > 0);
        }
    }
}
