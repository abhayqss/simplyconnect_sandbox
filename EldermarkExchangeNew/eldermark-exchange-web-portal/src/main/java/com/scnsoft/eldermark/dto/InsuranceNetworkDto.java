package com.scnsoft.eldermark.dto;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InsuranceNetworkDto {

    private Long id;

    private String title;

    private String name;

    private List<InsurancePlanDto> paymentPlans;

    @JsonProperty("isPopular")
    private Boolean popular;

    public InsuranceNetworkDto() {
    }

    public InsuranceNetworkDto(Long id, String title, String name, Boolean popular) {
        this.id = id;
        this.title = title;
        this.name = name;
        this.popular = popular;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<InsurancePlanDto> getPaymentPlans() {
        return paymentPlans;
    }

    public void setPaymentPlans(List<InsurancePlanDto> paymentPlans) {
        this.paymentPlans = paymentPlans;
    }

    public Boolean getPopular() {
        return popular;
    }

    public void setPopular(Boolean popular) {
        this.popular = popular;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof InsuranceNetworkDto))
            return false;
        InsuranceNetworkDto that = (InsuranceNetworkDto) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}