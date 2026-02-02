package org.openhealthtools.openxds.registry;

/**
 * Created by averazub on 12/1/2016.
 */
public class DocumentBriefData {
    private String uuid;
    private Boolean exists;
    private Boolean approved;


    public DocumentBriefData() {
    }

    public DocumentBriefData(String uuid, Boolean exists, Boolean approved) {
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
