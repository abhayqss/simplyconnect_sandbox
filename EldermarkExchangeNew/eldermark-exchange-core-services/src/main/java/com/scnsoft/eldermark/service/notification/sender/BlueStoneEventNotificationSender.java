package com.scnsoft.eldermark.service.notification.sender;

import com.scnsoft.eldermark.entity.EventNotification;
import com.scnsoft.eldermark.entity.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Service
@Transactional
public class BlueStoneEventNotificationSender extends BaseEventNotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(BlueStoneEventNotificationSender.class);

    @Value("${blue.stone.url}")
    private String blueStoneUrl;
    @Value("${blue.stone.username}")
    private String blueStoneUserName;
    @Value("${blue.stone.password}")
    private String blueStonePassword;

    @Autowired
    @Qualifier("jsonRestTemplateBuilder")
    private RestTemplateBuilder restTemplateBuilder;

    @Override
    protected boolean send(EventNotification eventNotification) {
        //integration is not working

        try {
            var content = eventNotification.getContent();
            logger.info("Sending message to bluestone : {}", content);

            var requestEntity = prepareRequest(content);

            logger.info("{}", requestEntity.toString());

            //http should client trust everything? bluestone certificate seems valid.
            var response = restTemplateBuilder.build().postForEntity(blueStoneUrl, requestEntity, String.class);

            logger.info("Blue Stone Response : \n {} \n {}", response.getStatusCodeValue(), response.getBody());
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Blue Stone communication success");
                return true;
            }
        } catch (RuntimeException ex) {
            logger.error("Error communication with Blue stone bridge", ex);
        }

        return false;
    }

    private HttpEntity<String> prepareRequest(String content) {
        var headers = new HttpHeaders();
        headers.setBasicAuth(blueStoneUserName, blueStonePassword, StandardCharsets.UTF_8);
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.add("Content-Encoding", StandardCharsets.UTF_8.displayName());

        return new HttpEntity<>(content, headers);
    }

    @Override
    public NotificationType supportedNotificationType() {
        return NotificationType.BLUE_STONE;
    }
}
