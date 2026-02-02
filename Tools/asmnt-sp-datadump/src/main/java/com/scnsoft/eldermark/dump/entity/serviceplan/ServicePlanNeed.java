package com.scnsoft.eldermark.dump.entity.serviceplan;


import javax.persistence.*;

@Entity
@Table(name = "ServicePlanNeed")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ServicePlanNeed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "service_plan_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private ClientServicePlan servicePlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ServicePlanNeedType domain;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClientServicePlan getServicePlan() {
        return servicePlan;
    }

    public void setServicePlan(ClientServicePlan servicePlan) {
        this.servicePlan = servicePlan;
    }

    public ServicePlanNeedType getDomain() {
        return domain;
    }

    public void setDomain(ServicePlanNeedType domain) {
        this.domain = domain;
    }
}