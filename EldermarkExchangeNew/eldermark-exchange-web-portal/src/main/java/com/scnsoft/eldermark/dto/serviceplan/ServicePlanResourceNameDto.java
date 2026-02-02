package com.scnsoft.eldermark.dto.serviceplan;

public class ServicePlanResourceNameDto {
    private String resourceName;
    private String providerName;

    public ServicePlanResourceNameDto(String resourceName, String providerName) {
        this.resourceName = resourceName;
        this.providerName = providerName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
}
