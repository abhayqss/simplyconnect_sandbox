package com.scnsoft.eldermark.entity.marketplace;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.scnsoft.eldermark.entity.phr.InNetworkInsurance;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author phomal
 * Created on 12/6/2017.
 */
@Immutable
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class InsurancePlan extends DisplayableNamedKeyEntity {

    @ManyToOne(optional = false)

    @JoinColumn(name = "in_network_insurance_id", nullable = false)
    @JsonIgnore
    private InNetworkInsurance inNetworkInsurance;

    @Column(name = "in_network_insurance_id", nullable = false, insertable = false, updatable = false)
    private Long inNetworkInsuranceId;

    @Column(name = "is_popular")
    private Boolean isPopular;

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

    public Boolean getPopular() {
        return isPopular;
    }

    public void setPopular(Boolean popular) {
        isPopular = popular;
    }
}
