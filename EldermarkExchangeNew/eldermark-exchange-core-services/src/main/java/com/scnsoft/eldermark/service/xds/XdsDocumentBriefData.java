package com.scnsoft.eldermark.service.xds;

/**
 * Created by averazub on 12/1/2016.
 */
public class XdsDocumentBriefData {
    private String uuid;
    private Boolean exists;
    private Boolean approved;


    public XdsDocumentBriefData() {
    }

    public XdsDocumentBriefData(String uuid, Boolean exists, Boolean approved) {
        this.uuid = uuid;
        this.exists = exists;
        this.approved = approved;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }
}
