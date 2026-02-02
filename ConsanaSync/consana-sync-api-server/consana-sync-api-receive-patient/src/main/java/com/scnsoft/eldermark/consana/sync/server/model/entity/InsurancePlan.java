package com.scnsoft.eldermark.consana.sync.server.model.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Immutable
@Entity
public class InsurancePlan extends DisplayableNamedKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "in_network_insurance_id", nullable = false)
    private InNetworkInsurance inNetworkInsurance;

    @Column(name = "in_network_insurance_id", nullable = false, insertable = false, updatable = false)
    private Long inNetworkInsuranceId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
