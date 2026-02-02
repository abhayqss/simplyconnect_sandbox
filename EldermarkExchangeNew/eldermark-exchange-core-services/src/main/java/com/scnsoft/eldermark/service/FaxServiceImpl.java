package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dto.notification.BaseFaxNotificationDto;
import com.scnsoft.eldermark.exception.ApplicationException;
import net.interfax.rest.client.InterFAX;
import net.interfax.rest.client.impl.DefaultInterFAXClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.Future;

@Service
public class FaxServiceImpl implements FaxService {
    private static final Logger logger = LoggerFactory.getLogger(FaxServiceImpl.class);

    @Value("${interfax.username}")
    private String interfaxUsername;

    @Value("${interfax.password}")
    private String interfaxPassword;

    @PostConstruct
    void validate() {
        if (StringUtils.isAnyBlank(interfaxUsername, interfaxPassword)) {
            throw new ApplicationException("FaxService username and password are not configured");
        }
    }

    @Override
    @Async
    public Future<Boolean> send(BaseFaxNotificationDto faxDto, byte[] content) {
        return new AsyncResult<>(sendAndWait(faxDto, content));
    }

    @Override
    public boolean sendAndWait(BaseFaxNotificationDto faxDto, byte[] content) {
        if (StringUtils.isBlank(faxDto.getFaxNumber())) {
            logger.warn("Can't send fax, because destination number not defined");
            return false;
        }

        try {
            InterFAX interFAX = new DefaultInterFAXClient(interfaxUsername, interfaxPassword);

            InputStream[] inputStreams = {new ByteArrayInputStream(content)};
            String[] mediaTypes = {"application/pdf"};

            logger.info("Sending Fax...");

            var apiResponse = interFAX.sendFax(faxDto.getFaxNumber(), inputStreams, mediaTypes);

            logger.info("interFAX.sendFax call returned code: {} ", apiResponse.getStatusCode());
            if (!HttpStatus.valueOf(apiResponse.getStatusCode()).is2xxSuccessful()) {
                logger.warn("interFAX responded {}", apiResponse.getResponseBody());
                return false;
            }
        } catch (Exception e) {
            logger.error("Error sending Fax ", e);
            return false;
        }
        return true;
    }
}
