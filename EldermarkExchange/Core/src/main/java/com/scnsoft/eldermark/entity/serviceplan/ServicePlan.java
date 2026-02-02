package com.scnsoft.eldermark.entity.serviceplan;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.ResidentAwareAuditableEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ServicePlan")
public class ServicePlan extends ResidentAwareAuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(name="service_plan_status", nullable = false)
    private ServicePlanStatus servicePlanStatus;

    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Employee employee;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created")
    private Date dateCreated;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_completed")
    private Date dateCompleted;

    @OneToOne(mappedBy="servicePlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private ServicePlanScoring scoring;

    @OneToMany(mappedBy = "servicePlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServicePlanNeed> needs;

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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Date dateCompleted) {
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
