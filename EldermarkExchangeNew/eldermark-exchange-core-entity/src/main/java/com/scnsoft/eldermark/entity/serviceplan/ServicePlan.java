package com.scnsoft.eldermark.entity.serviceplan;

import com.scnsoft.eldermark.entity.ClientAwareAuditableEntity;
import com.scnsoft.eldermark.entity.Employee;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "ServicePlan")
public class ServicePlan extends ClientAwareAuditableEntity {

    @Column(name = "resident_id", insertable = false, updatable = false, nullable = false)
    private Long clientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_plan_status", nullable = false)
    private ServicePlanStatus servicePlanStatus;

    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Employee employee;

    @Column(name = "date_created")
    private Instant dateCreated;

    @Column(name = "date_completed")
    private Instant dateCompleted;

    @OneToOne(mappedBy = "servicePlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private ServicePlanScoring scoring;

    @OneToMany(mappedBy = "servicePlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServicePlanNeed> needs;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public ServicePlanStatus getServicePlanStatus() {
        return servicePlanStatus;
    }

    public void setServicePlanStatus(ServicePlanStatus servicePlanStatus) {
        this.servicePlanStatus = servicePlanStatus;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Instant getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Instant dateCompleted) {
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
