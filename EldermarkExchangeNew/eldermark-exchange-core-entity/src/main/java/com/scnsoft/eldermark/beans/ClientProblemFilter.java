package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;

public class ClientProblemFilter implements ClientIdAware {
    private Long clientId;
    private Boolean includeActive;
    private Boolean includeOther;
    private Boolean includeResolved;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Boolean getIncludeActive() {
        return includeActive;
    }

    public void setIncludeActive(Boolean includeActive) {
        this.includeActive = includeActive;
    }

    public Boolean getIncludeOther() {
        return includeOther;
    }

    public void setIncludeOther(Boolean includeOther) {
        this.includeOther = includeOther;
    }

    public Boolean getIncludeResolved() {
        return includeResolved;
    }

    public void setIncludeResolved(Boolean includeResolved) {
        this.includeResolved = includeResolved;
    }
}
