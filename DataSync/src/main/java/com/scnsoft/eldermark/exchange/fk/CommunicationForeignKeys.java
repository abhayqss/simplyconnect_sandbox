package com.scnsoft.eldermark.exchange.fk;

public class CommunicationForeignKeys {
    private Long professionalContactId;
    private Long completedByEmployeeId;
    private Long createdByEmployeeId;
    private Long inquiryId;
    private Long communicationTypeId;
    private Long prospectId;
    private Long organizationId;

    public Long getProfessionalContactId() {
        return professionalContactId;
    }

    public void setProfessionalContactId(Long professionalContactId) {
        this.professionalContactId = professionalContactId;
    }

    public Long getCompletedByEmployeeId() {
        return completedByEmployeeId;
    }

    public void setCompletedByEmployeeId(Long completedByEmployeeId) {
        this.completedByEmployeeId = completedByEmployeeId;
    }

    public Long getCreatedByEmployeeId() {
        return createdByEmployeeId;
    }

    public void setCreatedByEmployeeId(Long createdByEmployeeId) {
        this.createdByEmployeeId = createdByEmployeeId;
    }

    public Long getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(Long inquiryId) {
        this.inquiryId = inquiryId;
    }

    public Long getCommunicationTypeId() {
        return communicationTypeId;
    }

    public void setCommunicationTypeId(Long communicationTypeId) {
        this.communicationTypeId = communicationTypeId;
    }

    public Long getProspectId() {
        return prospectId;
    }

    public void setProspectId(Long prospectId) {
        this.prospectId = prospectId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
