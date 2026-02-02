package com.scnsoft.eldermark.beans.security.projection.dto;

import java.util.Collections;
import java.util.Set;

public class ClientAppointmentClientOnlySecurityFieldsAwareImpl implements ClientAppointmentSecurityFieldsAware {

    private Long clientId;

    public ClientAppointmentClientOnlySecurityFieldsAwareImpl(Long clientId) {
        this.clientId = clientId;
    }

    @Override
    public Long getCreatorId() {
        return null;
    }

    @Override
    public Boolean getIsPublic() {
        return null;
    }

    @Override
    public Set<Long> getServiceProviderIds() {
        return Collections.emptySet();
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public Long getClientId() {
        return clientId;
    }
}
