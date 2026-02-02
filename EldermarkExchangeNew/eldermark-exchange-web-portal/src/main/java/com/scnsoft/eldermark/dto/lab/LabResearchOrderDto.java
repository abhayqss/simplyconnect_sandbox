package com.scnsoft.eldermark.dto.lab;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scnsoft.eldermark.beans.security.projection.dto.LabSecurityFieldsAware;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabResearchOrderDto implements LabSecurityFieldsAware {

    private Long id;
    private String requisitionNumber;
    private Long createdDate;
    private String statusName;
    private String statusTitle;
    @NotEmpty
    private String reason;
    @Size(max = 256)
    private String clinic;
    @Size(max = 256)
    private String clinicAddress;
    @Size(max = 80)
    private String notes;
    private Long createdById;
    private String createdByName;
    @Size(max = 50)
    @NotEmpty
    private String providerFirstName;
    @Size(max = 50)
    @NotEmpty
    private String providerLastName;
    private String providerFullName;
    private Long orderDate;
    private List<String> icd10Codes;
    @Valid
    @NotNull
    private ClientSummaryLabsAdaptDto client;
    @Valid
    @NotNull
    private LabOrderSpecimenDto specimen;
    private LabResearchResultDto result;

    private boolean canReview;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequisitionNumber() {
        return requisitionNumber;
    }

    public void setRequisitionNumber(String requisitionNumber) {
        this.requisitionNumber = requisitionNumber;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }


    public String getProviderFirstName() {
        return providerFirstName;
    }

    public void setProviderFirstName(String providerFirstName) {
        this.providerFirstName = providerFirstName;
    }

    public String getProviderLastName() {
        return providerLastName;
    }

    public void setProviderLastName(String providerLastName) {
        this.providerLastName = providerLastName;
    }

    public String getProviderFullName() {
        return providerFullName;
    }

    public void setProviderFullName(String providerFullName) {
        this.providerFullName = providerFullName;
    }

    public Long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Long orderDate) {
        this.orderDate = orderDate;
    }

    public List<String> getIcd10Codes() {
        return icd10Codes;
    }

    public void setIcd10Codes(List<String> icd10Codes) {
        this.icd10Codes = icd10Codes;
    }


    public ClientSummaryLabsAdaptDto getClient() {
        return client;
    }

    public void setClient(ClientSummaryLabsAdaptDto client) {
        this.client = client;
    }

    public LabOrderSpecimenDto getSpecimen() {
        return specimen;
    }

    public void setSpecimen(LabOrderSpecimenDto specimen) {
        this.specimen = specimen;
    }

    public LabResearchResultDto getResult() {
        return result;
    }

    public void setResult(LabResearchResultDto result) {
        this.result = result;
    }

    public boolean getCanReview() {
        return canReview;
    }

    public void setCanReview(boolean canReview) {
        this.canReview = canReview;
    }

    @Override
    @JsonIgnore
    public Long getClientId() {
        return Optional.ofNullable(client).map(ClientSummaryLabsAdaptDto::getId).orElse(null);
    }
}
