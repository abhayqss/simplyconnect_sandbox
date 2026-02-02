package com.scnsoft.eldermark.audit;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.AuditLogAction;
import com.scnsoft.eldermark.facades.AuditLoggingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {
    private static final Logger logger = LoggerFactory.getLogger(LoginListener.class);

    @Autowired
    private AuditLoggingFacade loggingFacade;

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event)
    {
        Authentication authentication =  event.getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof ExchangeUserDetails ) {
            Long employeeId = null;

            try {
                employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();

                loggingFacade.logLogin(employeeId);
            } catch (Exception e) {
                logger.error(AuditLogUtils.errorToString(AuditLogAction.LOG_IN, employeeId, null, null), e);
            }
        }
    }
}