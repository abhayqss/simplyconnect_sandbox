package com.scnsoft.eldermark.web.session;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.facades.DirectConfigurationFacade;
import com.scnsoft.eldermark.facades.DirectMessagesFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SecureMessagingConfig implements Serializable {
    private volatile boolean initialized;

    @Autowired
    private DirectMessagesFacade directMessagesFacade;

    // TODO consider marking this field as transient or make DirectConfigurationFacade class Serializable; otherwise Tomcat throws java.io.NotSerializableException
    @Autowired
    private DirectConfigurationFacade directConfigurationFacade;

    private boolean isAccountRegistered;

    public SecureMessagingConfig() {
        initialized = false;
    }

    public boolean isAccountRegistered(ExchangeUserDetails principal) {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    Long employeeId = principal.getEmployeeId();
                    String companyCode = principal.getCompanyCode();
                    isAccountRegistered = directMessagesFacade.isSecureMessagingActive(employeeId) &&
                                          directConfigurationFacade.isConfigured(companyCode);
                    initialized = true;
                }
            }
        }
        return isAccountRegistered;
    }

    public void resetCache() {
        initialized = false;
    }
}
