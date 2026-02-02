package com.scnsoft.eldermark.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Immutable
@Entity
@Table(name = "InsurancePlan")
public class InsurancePlan extends BasicInsuranceEntity {
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "in_network_insurance_id", nullable = false)
    private InNetworkInsurance inNetworkInsurance;

    @Column(name = "in_network_insurance_id", nullable = false, insertable = false, updatable = false)
    private Long inNetworkInsuranceId;

    public InNetworkInsurance getInNetworkInsurance() {
        return inNetworkInsurance;
    }

    public void setInNetworkInsurance(InNetworkInsurance inNetworkInsurance) {
        this.inNetworkInsurance = inNetworkInsurance;
    }

    public Long getInNetworkInsuranceId() {
        return inNetworkInsuranceId;
    }

    public void setInNetworkInsuranceId(Long inNetworkInsuranceId) {
        this.inNetworkInsuranceId = inNetworkInsuranceId;
    }

}
