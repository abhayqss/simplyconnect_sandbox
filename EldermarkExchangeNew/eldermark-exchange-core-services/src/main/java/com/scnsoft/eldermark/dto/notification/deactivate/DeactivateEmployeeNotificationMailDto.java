package com.scnsoft.eldermark.dto.notification.deactivate;

public class DeactivateEmployeeNotificationMailDto {
    private String receiverFullName;
    private String receiverEmail;
    private String username;
    private String companyId;
    private String deactivateDate;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getDeactivateDate() {
        return deactivateDate;
    }

    public void setDeactivateDate(String deactivateDate) {
        this.deactivateDate = deactivateDate;
    }
}
