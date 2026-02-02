package com.scnsoft.eldermark.beans;


import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.entity.document.ccd.ClientAllergyStatus;

import java.util.Set;

public class ClientAllergyFilter implements ClientIdAware {

    private Long clientId;
    private Set<ClientAllergyStatus> statuses;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Set<ClientAllergyStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(Set<ClientAllergyStatus> statuses) {
        this.statuses = statuses;
    }
}
