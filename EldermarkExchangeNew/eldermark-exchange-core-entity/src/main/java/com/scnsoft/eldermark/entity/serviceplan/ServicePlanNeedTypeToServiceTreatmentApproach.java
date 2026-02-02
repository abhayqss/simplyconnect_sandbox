package com.scnsoft.eldermark.entity.serviceplan;

import com.scnsoft.eldermark.entity.ServicesTreatmentApproach;

import javax.persistence.*;

@Entity
@Table(name = "ServicePlanNeedType_ServicesTreatmentApproach")
public class ServicePlanNeedTypeToServiceTreatmentApproach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_plan_type", nullable = false)
    private ServicePlanNeedType servicePlanNeedType;

    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "id")
    private ServicesTreatmentApproach servicesTreatmentApproach;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServicePlanNeedType getServicePlanNeedType() {
        return servicePlanNeedType;
    }

    public void setServicePlanNeedType(ServicePlanNeedType servicePlanNeedType) {
        this.servicePlanNeedType = servicePlanNeedType;
    }

    public ServicesTreatmentApproach getServicesTreatmentApproach() {
        return servicesTreatmentApproach;
    }

    public void setServicesTreatmentApproach(ServicesTreatmentApproach servicesTreatmentApproach) {
        this.servicesTreatmentApproach = servicesTreatmentApproach;
    }
}
