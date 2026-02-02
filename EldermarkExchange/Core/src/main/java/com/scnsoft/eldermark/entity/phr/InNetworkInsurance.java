package com.scnsoft.eldermark.entity.phr;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.scnsoft.eldermark.entity.marketplace.DisplayableNamedKeyEntity;
import com.scnsoft.eldermark.entity.marketplace.InsurancePlan;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.List;

@Immutable
@Entity
@Table(name = "InNetworkInsurance")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class InNetworkInsurance extends DisplayableNamedKeyEntity {

    @Column(name = "is_popular", nullable = false)
    private Boolean isPopular;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "inNetworkInsurance")
    private List<InsurancePlan> insurancePlans;

    @Column(name = "is_first_group")
    private Boolean isFirstGroup;

    public List<InsurancePlan> getInsurancePlans() {
        return insurancePlans;
    }

    public void setInsurancePlans(List<InsurancePlan> insurancePlans) {
        this.insurancePlans = insurancePlans;
    }

    public Boolean getFirstGroup() {
        return isFirstGroup;
    }

    public void setFirstGroup(Boolean firstGroup) {
        isFirstGroup = firstGroup;
    }

    public Boolean getPopular() {
        return isPopular;
    }

    public void setPopular(Boolean popular) {
        isPopular = popular;
    }
}
