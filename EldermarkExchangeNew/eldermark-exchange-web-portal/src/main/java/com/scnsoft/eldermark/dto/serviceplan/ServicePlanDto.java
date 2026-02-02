package com.scnsoft.eldermark.dto.serviceplan;

import com.scnsoft.eldermark.beans.security.projection.dto.ServicePlanSecurityFieldsAware;
import com.scnsoft.eldermark.validation.SpELAssert;

import javax.validation.Valid;
import java.util.List;

@SpELAssert(
        applyIf = "id",
        value = "dateCreated != null",
        message = "dateCreated {javax.validation.constraints.NotEmpty.message}"
)
public class ServicePlanDto extends BaseServicePlanDto implements ServicePlanSecurityFieldsAware {

    private String createdBy;

    private boolean isCompleted;

    @Valid
    private List<ServicePlanNeedDto> needs;

    private Long clientId;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean completed) {
        isCompleted = completed;
    }

    public List<ServicePlanNeedDto> getNeeds() {
        return needs;
    }

    public void setNeeds(List<ServicePlanNeedDto> needs) {
        this.needs = needs;
    }

    @Override
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
