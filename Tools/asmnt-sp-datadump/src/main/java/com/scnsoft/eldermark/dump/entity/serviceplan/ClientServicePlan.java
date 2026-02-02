package com.scnsoft.eldermark.dump.entity.serviceplan;


import com.scnsoft.eldermark.dump.entity.ClientAwareAuditableEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "ServicePlan")
public class ClientServicePlan extends ClientAwareAuditableEntity {


    @Enumerated(EnumType.STRING)
    @Column(name = "service_plan_status", nullable = false)
    private ServicePlanStatus servicePlanStatus;

    @Column(name = "date_created")
    private LocalDate dateCreated;

    @Column(name = "date_completed")
    private LocalDate dateCompleted;

    @OneToOne(mappedBy = "servicePlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private ServicePlanScoring scoring;

    @OneToMany(mappedBy = "servicePlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ServicePlanNeed> needs;

    public ServicePlanStatus getServicePlanStatus() {
        return servicePlanStatus;
    }

    public void setServicePlanStatus(ServicePlanStatus servicePlanStatus) {
        this.servicePlanStatus = servicePlanStatus;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDate getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(LocalDate dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public ServicePlanScoring getScoring() {
        return scoring;
    }

    public void setScoring(ServicePlanScoring scoring) {
        this.scoring = scoring;
    }

    public List<ServicePlanNeed> getNeeds() {
        return needs;
    }

    public void setNeeds(List<ServicePlanNeed> needs) {
        this.needs = needs;
    }
}
