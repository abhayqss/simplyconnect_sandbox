package com.scnsoft.eldermark.dto;

import java.util.List;

public class ClientBillingInfoDto {

    private String medicare;
    private String medicaid;
    private List<BillingItemDto> billingItems;

    public String getMedicare() {
        return medicare;
    }

    public void setMedicare(String medicare) {
        this.medicare = medicare;
    }

    public String getMedicaid() {
        return medicaid;
    }

    public void setMedicaid(String medicaid) {
        this.medicaid = medicaid;
    }

    public List<BillingItemDto> getBillingItems() {
        return billingItems;
    }

    public void setBillingItems(List<BillingItemDto> billingItems) {
        this.billingItems = billingItems;
    }

}
