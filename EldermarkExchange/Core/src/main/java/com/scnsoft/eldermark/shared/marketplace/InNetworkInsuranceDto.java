package com.scnsoft.eldermark.shared.marketplace;

import com.scnsoft.eldermark.entity.marketplace.InsurancePlan;
import org.codehaus.jackson.annotate.JsonBackReference;

import java.util.List;

public class InNetworkInsuranceDto {

    private Long id;
    private String name;
    private String code;
    private Boolean popular;
    @JsonBackReference
    private List<InsurancePlan> insurancePlans;
    private List<Long> planIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getPopular() {
        return popular;
    }

    public void setPopular(Boolean popular) {
        this.popular = popular;
    }

    public List<Long> getPlanIds() {
        return planIds;
    }

    public void setPlanIds(List<Long> planIds) {
        this.planIds = planIds;
    }

    public List<InsurancePlan> getInsurancePlans() {
        return insurancePlans;
    }

    public void setInsurancePlans(List<InsurancePlan> insurancePlans) {
        this.insurancePlans = insurancePlans;
    }
}
