package com.scnsoft.eldermark.shared.marketplace;

import java.util.List;

/**
 * Created by phomal on 11/27/2017.
 */
public class PaymentAndInsuranceDto {
    private Long id;
    private String label;
    private List<PaymentAndInsuranceDto> relatedInsurances;

    public PaymentAndInsuranceDto() {}

    public PaymentAndInsuranceDto(Long id, String label) {
        this.id = id;
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<PaymentAndInsuranceDto> getRelatedInsurances() {
        return relatedInsurances;
    }

    public void setRelatedInsurances(List<PaymentAndInsuranceDto> relatedInsurances) {
        this.relatedInsurances = relatedInsurances;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentAndInsuranceDto that = (PaymentAndInsuranceDto) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        return !(getLabel() != null ? !getLabel().equals(that.getLabel()) : that.getLabel() != null);
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getLabel() != null ? getLabel().hashCode() : 0);
        return result;
    }
}
