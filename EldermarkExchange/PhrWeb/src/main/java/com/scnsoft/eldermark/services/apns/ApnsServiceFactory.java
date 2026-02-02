package com.scnsoft.eldermark.services.apns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

@Service
public class ApnsServiceFactory {

	private static final Logger logger = LoggerFactory.getLogger(ApnsServiceFactory.class);

	@Value("${voip.password}")
	private String voipPassword;

	@Value("${voip.certi.path}")
	private String voipPath;

	@Value("${is.app.production}")
	Boolean isProduction;

	public final ApnsService createApnsService() {
        try {
            logger.info("try to create apns service");
            if (isProduction)
                return APNS.newService().withCert(voipPath, voipPassword).withProductionDestination().build();
            else
                return APNS.newService().withCert(voipPath, voipPassword).withSandboxDestination().build();
        } catch (Exception e) {
            logger.info("APNS service create issue {0}", e.getMessage());
            return null;
        }

    }
}
