package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent marketplace services
 */
@ApiModel(description = "This DTO is intended to represent marketplace services")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-12-12T16:41:51.592+03:00")
public class MarketplaceServicesDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("levelsOfCare")
    private MarketplaceServiceSectionDto levelsOfCare = null;

    @JsonProperty("servicesTratmentApproaches")
    private MarketplaceServiceSectionDto servicesTratmentApproaches = null;

    @JsonProperty("emergencyServices")
    private MarketplaceServiceSectionDto emergencyServices = null;

    @JsonProperty("ancillaryServices")
    private MarketplaceServiceSectionDto ancillaryServices = null;

    @JsonProperty("ageGroups")
    private MarketplaceServiceSectionDto ageGroups = null;

    @JsonProperty("languageServices")
    private MarketplaceServiceSectionDto languageServices = null;

    @JsonProperty("paymentInsurances")
    private MarketplaceServiceInsuranceSectionDto paymentInsurances = null;


    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Levels of Care
     * @return levelsOfCare
     */
    @ApiModelProperty(value = "Levels of Care")
    public MarketplaceServiceSectionDto getLevelsOfCare() {
        return levelsOfCare;
    }

    public void setLevelsOfCare(MarketplaceServiceSectionDto levelsOfCare) {
        this.levelsOfCare = levelsOfCare;
    }

    /**
     * Services/Treatment Approaches
     * @return servicesTratmentApproaches
     */
    @ApiModelProperty(value = "Services/Treatment Approaches")
    public MarketplaceServiceSectionDto getServicesTratmentApproaches() {
        return servicesTratmentApproaches;
    }

    public void setServicesTratmentApproaches(MarketplaceServiceSectionDto servicesTratmentApproaches) {
        this.servicesTratmentApproaches = servicesTratmentApproaches;
    }

    /**
     * Emergency Services / Urgent Care
     * @return emergencyServices
     */
    @ApiModelProperty(value = "Emergency Services / Urgent Care")
    public MarketplaceServiceSectionDto getEmergencyServices() {
        return emergencyServices;
    }

    public void setEmergencyServices(MarketplaceServiceSectionDto emergencyServices) {
        this.emergencyServices = emergencyServices;
    }

    /**
     * Ancillary Services
     * @return ancillaryServices
     */
    @ApiModelProperty(value = "Ancillary Services")
    public MarketplaceServiceSectionDto getAncillaryServices() {
        return ancillaryServices;
    }

    public void setAncillaryServices(MarketplaceServiceSectionDto ancillaryServices) {
        this.ancillaryServices = ancillaryServices;
    }

    /**
     * Age Groups Accepted
     * @return ageGroups
     */
    @ApiModelProperty(value = "Age Groups Accepted")
    public MarketplaceServiceSectionDto getAgeGroups() {
        return ageGroups;
    }

    public void setAgeGroups(MarketplaceServiceSectionDto ageGroups) {
        this.ageGroups = ageGroups;
    }

    /**
     * Language Services
     * @return languageServices
     */
    @ApiModelProperty(value = "Language Services")
    public MarketplaceServiceSectionDto getLanguageServices() {
        return languageServices;
    }

    public void setLanguageServices(MarketplaceServiceSectionDto languageServices) {
        this.languageServices = languageServices;
    }

    /**
     * Payment/Insurance Accepted
     * @return paymentInsurances
     */
    @ApiModelProperty(value = "Payment/Insurance Accepted")
    public MarketplaceServiceInsuranceSectionDto getPaymentInsurances() {
        return paymentInsurances;
    }

    public void setPaymentInsurances(MarketplaceServiceInsuranceSectionDto paymentInsurances) {
        this.paymentInsurances = paymentInsurances;
    }

}
