package com.scnsoft.eldermark.consana.sync.server.model.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.List;

@Immutable
@Entity
@Table(name = "InNetworkInsurance")
public class InNetworkInsurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "inNetworkInsurance")
    private List<InsurancePlan> insurancePlans;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<InsurancePlan> getInsurancePlans() {
        return insurancePlans;
    }

    public void setInsurancePlans(List<InsurancePlan> insurancePlans) {
        this.insurancePlans = insurancePlans;
    }

}
