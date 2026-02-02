package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InsurancePlanDto {

    private Long id;
    private String title;
    private String name;
    @JsonProperty("isPopular")
    private Boolean popular;

    public InsurancePlanDto() {
    }

    public InsurancePlanDto(Long id, String title, String name, Boolean popular) {
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

    public Boolean getPopular() {
        return popular;
    }

    public void setPopular(Boolean popular) {
        this.popular = popular;
    }

}
