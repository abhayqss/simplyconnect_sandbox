package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_ServicePlan")
public class AuditLogServicePlanRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "service_plan_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ServicePlan servicePlan;

    @Column(name = "service_plan_id", nullable = false)
    private Long servicePlantId;

    public ServicePlan getServicePlan() {
        return servicePlan;
    }

    public void setServicePlan(ServicePlan servicePlan) {
        this.servicePlan = servicePlan;
    }

    public Long getServicePlantId() {
        return servicePlantId;
    }

    public void setServicePlantId(Long servicePlantId) {
        this.servicePlantId = servicePlantId;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(servicePlantId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Collections.emptyList();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.SERVICE_PLAN;
    }
}
