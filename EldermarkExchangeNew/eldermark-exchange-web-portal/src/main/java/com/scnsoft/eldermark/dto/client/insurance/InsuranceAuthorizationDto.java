package com.scnsoft.eldermark.dto.client.insurance;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class InsuranceAuthorizationDto {

    private Long id;

    @NotNull
    private Long startDate;

    @NotNull
    private Long endDate;

    @Size(max = 128)
    @NotNull
    private String number;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
