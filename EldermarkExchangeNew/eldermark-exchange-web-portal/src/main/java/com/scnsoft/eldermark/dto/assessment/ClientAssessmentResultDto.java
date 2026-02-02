package com.scnsoft.eldermark.dto.assessment;

import com.scnsoft.eldermark.beans.security.projection.dto.ClientAssessmentResultSecurityFieldsAware;
import com.scnsoft.eldermark.entity.assessment.AssessmentStatus;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

public class ClientAssessmentResultDto implements ClientAssessmentResultSecurityFieldsAware {
    private Long id;

    @NotNull
    private Long typeId;

    private String comment;

    private Long dateCompleted;

    private Long dateAssigned;

    private Long contactId;

    private String contactName;

    @NotEmpty
    private String dataJson;

    private Long score;

    private Long clientId;

    @NotNull
    private AssessmentStatus statusName;

    private String statusTitle;

    private Long timeToEdit;

    private Boolean hasErrors;

    private Set<String> servicePlanNeedIdentificationExcludedQuestions;

    private Set<String> servicePlanNeedIdentificationExcludedSections;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Long dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public Long getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(Long dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public AssessmentStatus getStatusName() {
        return statusName;
    }

    public void setStatusName(AssessmentStatus statusName) {
        this.statusName = statusName;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    @Override
    public Long getAssessmentId() {
        return getTypeId();
    }

    public Long getTimeToEdit() {
        return timeToEdit;
    }

    public void setTimeToEdit(Long timeToEdit) {
        this.timeToEdit = timeToEdit;
    }

    public Boolean getHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(Boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public Set<String> getServicePlanNeedIdentificationExcludedQuestions() {
        return servicePlanNeedIdentificationExcludedQuestions;
    }

    public void setServicePlanNeedIdentificationExcludedQuestions(Set<String> servicePlanNeedIdentificationExcludedQuestions) {
        this.servicePlanNeedIdentificationExcludedQuestions = servicePlanNeedIdentificationExcludedQuestions;
    }

    public Set<String> getServicePlanNeedIdentificationExcludedSections() {
        return servicePlanNeedIdentificationExcludedSections;
    }

    public void setServicePlanNeedIdentificationExcludedSections(Set<String> servicePlanNeedIdentificationExcludedSections) {
        this.servicePlanNeedIdentificationExcludedSections = servicePlanNeedIdentificationExcludedSections;
    }
}
