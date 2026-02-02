package com.scnsoft.eldermark.entity.phr;

import com.scnsoft.eldermark.entity.marketplace.InsurancePlan;
import com.scnsoft.eldermark.entity.marketplace.Marketplace;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

/**
 * @author stsiushkevich
 */

@Entity
@Table(name = "Marketplace_InNetworkInsurance_InsurancePlan")
public class MarketplaceInNetworkInsurancePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "marketplace_id", nullable = false)
    private Marketplace marketplace;

    @ManyToOne
    @JoinColumn(name = "in_network_insurance_id", nullable = false)
    private InNetworkInsurance inNetworkInsurance;

    @ManyToOne
    @JoinColumn(name = "insurance_plan_id")
    private InsurancePlan insurancePlan;

    public MarketplaceInNetworkInsurancePlan() {

    }

    public MarketplaceInNetworkInsurancePlan(Marketplace marketplace, InNetworkInsurance inNetworkInsurance, InsurancePlan insurancePlan) {
        this.marketplace = marketplace;
        this.inNetworkInsurance = inNetworkInsurance;
        this.insurancePlan = insurancePlan;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Marketplace getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(Marketplace marketplace) {
        this.marketplace = marketplace;
    }

    public InNetworkInsurance getInNetworkInsurance() {
        return inNetworkInsurance;
    }

    public void setInNetworkInsurance(InNetworkInsurance inNetworkInsurance) {
        this.inNetworkInsurance = inNetworkInsurance;
    }

    public InsurancePlan getInsurancePlan() {
        return insurancePlan;
    }

    public void setInsurancePlan(InsurancePlan insurancePlan) {
        this.insurancePlan = insurancePlan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketplaceInNetworkInsurancePlan that = (MarketplaceInNetworkInsurancePlan) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(marketplace, that.marketplace) &&
                Objects.equals(inNetworkInsurance, that.inNetworkInsurance) &&
                Objects.equals(insurancePlan, that.insurancePlan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, marketplace, inNetworkInsurance, insurancePlan);
    }
}
