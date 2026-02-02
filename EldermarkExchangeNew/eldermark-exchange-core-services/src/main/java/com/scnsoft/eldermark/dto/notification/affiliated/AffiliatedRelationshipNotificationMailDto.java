package com.scnsoft.eldermark.dto.notification.affiliated;

public class AffiliatedRelationshipNotificationMailDto {

    private String authorFullName;
    private String receiverFullName;
    private String receiverEmail;
    private String primaryOrganizationName;
    private String affiliatedOrganizationName;
    private boolean isTerminated;

    public String getAuthorFullName() {
        return authorFullName;
    }

    public void setAuthorFullName(String authorFullName) {
        this.authorFullName = authorFullName;
    }

    public String getReceiverFullName() {
        return receiverFullName;
    }

    public void setReceiverFullName(String receiverFullName) {
        this.receiverFullName = receiverFullName;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getPrimaryOrganizationName() {
        return primaryOrganizationName;
    }

    public void setPrimaryOrganizationName(String primaryOrganizationName) {
        this.primaryOrganizationName = primaryOrganizationName;
    }

    public String getAffiliatedOrganizationName() {
        return affiliatedOrganizationName;
    }

    public void setAffiliatedOrganizationName(String affiliatedOrganizationName) {
        this.affiliatedOrganizationName = affiliatedOrganizationName;
    }

    public boolean isTerminated() {
        return isTerminated;
    }

    public void setTerminated(boolean terminated) {
        isTerminated = terminated;
    }
}
