package com.scnsoft.eldermark.entity.note;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ServiceStatusCheckNote")
public class ServiceStatusCheckNote extends Note {

    @ManyToOne
    @JoinColumn(name = "service_plan_id", referencedColumnName = "id", nullable = false)
    private ServicePlan servicePlan;

    @Column(name = "resource_name", nullable = false)
    private String resourceName;

    @Column(name = "provider_name")
    private String providerName;

    @Column(name = "audit_person", nullable = false)
    private String auditPerson;

    @Column(name = "check_date", nullable = false)
    private Instant checkDate;

    @Column(name = "next_check_date")
    private Instant nextCheckDate;

    @Column(name = "service_provided", nullable = false, columnDefinition = "tinyint")
    private Boolean serviceProvided;

    public ServicePlan getServicePlan() {
        return servicePlan;
    }

    public void setServicePlan(ServicePlan servicePlan) {
        this.servicePlan = servicePlan;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getAuditPerson() {
        return auditPerson;
    }

    public void setAuditPerson(String auditPerson) {
        this.auditPerson = auditPerson;
    }

    public Instant getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Instant checkDate) {
        this.checkDate = checkDate;
    }

    public Instant getNextCheckDate() {
        return nextCheckDate;
    }

    public void setNextCheckDate(Instant nextCheckDate) {
        this.nextCheckDate = nextCheckDate;
    }

    public Boolean getServiceProvided() {
        return serviceProvided;
    }

    public void setServiceProvided(Boolean serviceProvided) {
        this.serviceProvided = serviceProvided;
    }
}
