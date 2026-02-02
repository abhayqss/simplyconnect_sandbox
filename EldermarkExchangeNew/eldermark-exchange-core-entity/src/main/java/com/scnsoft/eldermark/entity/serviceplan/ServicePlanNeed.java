package com.scnsoft.eldermark.entity.serviceplan;

import com.scnsoft.eldermark.entity.basic.HistoryIdsAwareEntity;

import javax.persistence.*;

@Entity
@Table(name = "ServicePlanNeed")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ServicePlanNeed extends HistoryIdsAwareEntity implements Comparable<ServicePlanNeed> {

    @JoinColumn(name = "service_plan_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private ServicePlan servicePlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private ServicePlanNeedPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ServicePlanNeedType domain;

    @JoinColumn(name = "program_type_id", referencedColumnName = "id")
    @ManyToOne
    private ProgramType programType;

    @JoinColumn(name = "program_subtype_id", referencedColumnName = "id")
    @ManyToOne
    private ProgramSubType programSubType;

    public ServicePlan getServicePlan() {
        return servicePlan;
    }

    public void setServicePlan(ServicePlan servicePlan) {
        this.servicePlan = servicePlan;
    }

    public ServicePlanNeedPriority getPriority() {
        return priority;
    }

    public void setPriority(ServicePlanNeedPriority priority) {
        this.priority = priority;
    }

    public ServicePlanNeedType getDomain() {
        return domain;
    }

    public void setDomain(ServicePlanNeedType domain) {
        this.domain = domain;
    }

    public ProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(ProgramType programType) {
        this.programType = programType;
    }

    public ProgramSubType getProgramSubType() {
        return programSubType;
    }

    public void setProgramSubType(ProgramSubType programSubType) {
        this.programSubType = programSubType;
    }

    @Override
    public int compareTo(ServicePlanNeed o2) {
        if (this.getPriority().getNumberPriority().compareTo(o2.getPriority().getNumberPriority()) != 0) {
            return this.getPriority().getNumberPriority().compareTo(o2.getPriority().getNumberPriority()) * (-1);
        } else if (this.getPriority().getDisplayName().compareToIgnoreCase(o2.getPriority().getDisplayName()) != 0) {
            return this.getPriority().getDisplayName().compareToIgnoreCase(o2.getPriority().getDisplayName());
        } else {
            return this.getId().compareTo(o2.getId());
        }
    }
}
