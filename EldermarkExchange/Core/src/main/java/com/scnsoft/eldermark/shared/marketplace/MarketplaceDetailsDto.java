package com.scnsoft.eldermark.shared.marketplace;

import java.util.List;
import java.util.Map;

/**
 * @author phomal
 * Created on 11/29/2017.
 */
public class MarketplaceDetailsDto extends MarketplaceInfoDto {
    private List<String> levelOfCares;
    private List<String> serviceTreatmentApproaches;
    private List<String> emergencyServices;
    private List<String> ageGroupsAccepted;
    private List<String> languageServices;
    private List<String> ancillaryServices;
    private Map<String, String[]> selectedInNetworkInsurancePlanNames;
    private String servicesSummaryDescription;

    public List<String> getLevelOfCares() {
        return levelOfCares;
    }

    public void setLevelOfCares(List<String> levelOfCares) {
        this.levelOfCares = levelOfCares;
    }

    public List<String> getServiceTreatmentApproaches() {
        return serviceTreatmentApproaches;
    }

    public void setServiceTreatmentApproaches(List<String> serviceTreatmentApproaches) {
        this.serviceTreatmentApproaches = serviceTreatmentApproaches;
    }

    public List<String> getEmergencyServices() {
        return emergencyServices;
    }

    public void setEmergencyServices(List<String> emergencyServices) {
        this.emergencyServices = emergencyServices;
    }

    public List<String> getLanguageServices() {
        return languageServices;
    }

    public void setLanguageServices(List<String> languageServices) {
        this.languageServices = languageServices;
    }

    public List<String> getAgeGroupsAccepted() {
        return ageGroupsAccepted;
    }

    public void setAgeGroupsAccepted(List<String> ageGroupsAccepted) {
        this.ageGroupsAccepted = ageGroupsAccepted;
    }

    public String getServicesSummaryDescription() {
        return servicesSummaryDescription;
    }

    public void setServicesSummaryDescription(String servicesSummaryDescription) {
        this.servicesSummaryDescription = servicesSummaryDescription;
    }

    public List<String> getAncillaryServices() {
        return ancillaryServices;
    }

    public void setAncillaryServices(List<String> ancillaryServices) {
        this.ancillaryServices = ancillaryServices;
    }

    public Map<String, String[]> getSelectedInNetworkInsurancePlanNames() {
        return selectedInNetworkInsurancePlanNames;
    }

    public void setSelectedInNetworkInsurancePlanNames(Map<String, String[]> selectedInNetworkInsurancePlans) {
        this.selectedInNetworkInsurancePlanNames = selectedInNetworkInsurancePlans;
    }
}
