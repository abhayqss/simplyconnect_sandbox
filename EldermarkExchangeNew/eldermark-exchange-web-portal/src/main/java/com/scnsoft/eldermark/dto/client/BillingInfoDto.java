package com.scnsoft.eldermark.dto.client;

import java.util.List;

public class BillingInfoDto {

    private String medicareNumber;

    private String medicaidNumber;

    private List<BillingItemDto> items;

    public String getMedicareNumber() {
        return medicareNumber;
    }

    public void setMedicareNumber(String medicareNumber) {
        this.medicareNumber = medicareNumber;
    }

    public String getMedicaidNumber() {
        return medicaidNumber;
    }

    public void setMedicaidNumber(String medicaidNumber) {
        this.medicaidNumber = medicaidNumber;
    }

    public List<BillingItemDto> getItems() {
        return items;
    }

    public void setItems(List<BillingItemDto> items) {
        this.items = items;
    }

}
