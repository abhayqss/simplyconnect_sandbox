package com.scnsoft.eldermark.beans;


import com.scnsoft.eldermark.beans.projection.ClientIdAware;

public class ClientMedicationFilter implements ClientIdAware {

    private Long clientId;

    private String name;

    private Boolean includeActive;
    private Boolean includeInactive;
    private Boolean includeUnknown;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIncludeActive() {
        return includeActive;
    }

    public void setIncludeActive(Boolean includeActive) {
        this.includeActive = includeActive;
    }

    public Boolean getIncludeInactive() {
        return includeInactive;
    }

    public void setIncludeInactive(Boolean includeInactive) {
        this.includeInactive = includeInactive;
    }

    public Boolean getIncludeUnknown() {
        return includeUnknown;
    }

    public void setIncludeUnknown(Boolean includeUnknown) {
        this.includeUnknown = includeUnknown;
    }
}
