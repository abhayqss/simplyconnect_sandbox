package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table
@Data
public class ConsanaResidentInsurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name="resident_id")
    public Resident resident;

    @Column(name = "in_network_insurance_name")
    public String inNetworkInsuranceName;

    @Column(name = "insurance_plan_code")
    public String insurancePlanCode;

}
