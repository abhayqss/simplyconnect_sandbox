package com.scnsoft.eldermark.entity.serviceplan;

import javax.persistence.*;

@Entity
@Table(name="ServicePlanNeed")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ServicePlanNeed implements Comparable<ServicePlanNeed> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "chain_id")
    private Long chainId;

    @JoinColumn(name = "service_plan_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private ServicePlan servicePlan;

    @Enumerated(EnumType.STRING)
    @Column(name="priority", nullable = false)
    private ServicePlanNeedPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false)
    private ServicePlanNeedType type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

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

    public ServicePlanNeedType getType() {
        return type;
    }

    public void setType(ServicePlanNeedType type) {
        this.type = type;
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
