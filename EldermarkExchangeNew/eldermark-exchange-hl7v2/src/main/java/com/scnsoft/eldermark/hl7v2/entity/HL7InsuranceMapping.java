package com.scnsoft.eldermark.hl7v2.entity;

import com.scnsoft.eldermark.entity.InNetworkInsurance;

import javax.persistence.*;

@Entity
@Table(name = "HL7InsuranceMapping")
public class HL7InsuranceMapping {

    @Id
    @Column(name = "hl7_insurance_name", nullable = false)
    private String hl7InsuranceName;

    @OneToOne
    @JoinColumn(name = "in_network_insurance_id", nullable = false)
    private InNetworkInsurance inNetworkInsurance;

    public String getHl7InsuranceName() {
        return hl7InsuranceName;
    }

    public void setHl7InsuranceName(String hl7InsuranceName) {
        this.hl7InsuranceName = hl7InsuranceName;
    }

    public InNetworkInsurance getInNetworkInsurance() {
        return inNetworkInsurance;
    }

    public void setInNetworkInsurance(InNetworkInsurance inNetworkInsurance) {
        this.inNetworkInsurance = inNetworkInsurance;
    }
}
