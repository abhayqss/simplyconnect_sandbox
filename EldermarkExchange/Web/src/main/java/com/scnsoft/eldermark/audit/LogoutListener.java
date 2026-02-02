package com.scnsoft.eldermark.audit;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.entity.AuditLogAction;
import com.scnsoft.eldermark.facades.AuditLoggingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LogoutListener implements ApplicationListener<SessionDestroyedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(LogoutListener.class);

    @Autowired
    private AuditLoggingFacade loggingFacade;

    @Override
    public void onApplicationEvent(SessionDestroyedEvent event)
    {
        List<SecurityContext> lstSecurityContext = event.getSecurityContexts();
        for (SecurityContext securityContext : lstSecurityContext)
        {
            Authentication authentication =  securityContext.getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof ExchangeUserDetails) {
                Long employeeId = null;

                try {
                    employeeId = ((ExchangeUserDetails) authentication.getPrincipal()).getEmployeeId();

                    loggingFacade.logLogout(employeeId);
                } catch (Exception e) {
                    logger.error(AuditLogUtils.errorToString(AuditLogAction.LOG_OUT, employeeId, null, null), e);
                }
            }
        }
    }
}