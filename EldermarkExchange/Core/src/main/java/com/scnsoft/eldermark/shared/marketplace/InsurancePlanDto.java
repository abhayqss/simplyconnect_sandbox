package com.scnsoft.eldermark.shared.marketplace;

/**
 * @author stsiushkevich
 */
public class InsurancePlanDto {
    private Long id;
    private String name;
    private Long insuranceId = null;

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

    public Long getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(Long insuranceId) {
        this.insuranceId = insuranceId;
    }
}
