package com.scnsoft.eldermark.shared.marketplace;

import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * @author phomal
 * Created on 11/29/2017.
 */
public class MarketplaceDto {
    private String appointmentsEmail;
    private String appointmentsSecureEmail;
    private String servicesSummaryDescription;
    private List<Long> primaryFocusIds;
    private List<Long> communityTypeIds;
    private List<Long> levelOfCareIds;
    private List<Long> ageGroupIds;
    private List<Long> serviceTreatmentApproachIds;
    private List<Long> emergencyServiceIds;
    private List<Long> languageServiceIds;
    private List<Long> ancillaryServiceIds;
    private List<Long> selectedInNetworkInsuranceIds;
    private Map<Long, List<Long>> selectedInNetworkInsurancePlanIds;
    private Boolean allInsurancesAccepted;
    private Boolean allowAppointments;
    private Boolean confirmVisibility;
    private String prerequisite;
    private String exclusion;

    public String getAppointmentsEmail() {
        return appointmentsEmail;
    }

    public void setAppointmentsEmail(String appointmentsEmail) {
        this.appointmentsEmail = appointmentsEmail;
    }

    public String getAppointmentsSecureEmail() {
        return appointmentsSecureEmail;
    }

    public void setAppointmentsSecureEmail(String appointmentsSecureEmail) {
        this.appointmentsSecureEmail = appointmentsSecureEmail;
    }

    public String getServicesSummaryDescription() {
        return servicesSummaryDescription;
    }

    public void setServicesSummaryDescription(String servicesSummaryDescription) {
        this.servicesSummaryDescription = servicesSummaryDescription;
    }

    public List<Long> getPrimaryFocusIds() {
        return primaryFocusIds;
    }

    public void setPrimaryFocusIds(List<Long> primaryFocusIds) {
        this.primaryFocusIds = primaryFocusIds;
    }

    public List<Long> getCommunityTypeIds() {
        return communityTypeIds;
    }

    public void setCommunityTypeIds(List<Long> communityTypeIds) {
        this.communityTypeIds = communityTypeIds;
    }

    public List<Long> getLevelOfCareIds() {
        return levelOfCareIds;
    }

    public void setLevelOfCareIds(List<Long> levelOfCareIds) {
        this.levelOfCareIds = levelOfCareIds;
    }

    public List<Long> getAgeGroupIds() {
        return ageGroupIds;
    }

    public void setAgeGroupIds(List<Long> ageGroupIds) {
        this.ageGroupIds = ageGroupIds;
    }

    public List<Long> getServiceTreatmentApproachIds() {
        return serviceTreatmentApproachIds;
    }

    public void setServiceTreatmentApproachIds(List<Long> serviceTreatmentApproachIds) {
        this.serviceTreatmentApproachIds = serviceTreatmentApproachIds;
    }

    public List<Long> getEmergencyServiceIds() {
        return emergencyServiceIds;
    }

    public void setEmergencyServiceIds(List<Long> emergencyServiceIds) {
        this.emergencyServiceIds = emergencyServiceIds;
    }

    public List<Long> getLanguageServiceIds() {
        return languageServiceIds;
    }

    public void setLanguageServiceIds(List<Long> languageServiceIds) {
        this.languageServiceIds = languageServiceIds;
    }

    public List<Long> getAncillaryServiceIds() {
        return ancillaryServiceIds;
    }

    public void setAncillaryServiceIds(List<Long> ancillaryServiceIds) {
        this.ancillaryServiceIds = ancillaryServiceIds;
    }

    public Boolean getAllInsurancesAccepted() {
        return allInsurancesAccepted;
    }

    public void setAllInsurancesAccepted(Boolean allInsurancesAccepted) {
        this.allInsurancesAccepted = allInsurancesAccepted;
    }

    public Boolean getAllowAppointments() {
        return allowAppointments;
    }

    public void setAllowAppointments(Boolean allowAppointments) {
        this.allowAppointments = allowAppointments;
    }

    public Boolean getConfirmVisibility() {
        return confirmVisibility;
    }

    public void setConfirmVisibility(Boolean confirmVisibility) {
        this.confirmVisibility = confirmVisibility;
    }

    public Map<Long, List<Long>> getSelectedInNetworkInsurancePlanIds() {
        return selectedInNetworkInsurancePlanIds;
    }

    public void setSelectedInNetworkInsurancePlanIds(Map<Long, List<Long>> selectedInNetworkInsurancePlanIds) {
        this.selectedInNetworkInsurancePlanIds = selectedInNetworkInsurancePlanIds;
    }

    public List<Long> getSelectedInNetworkInsuranceIds() {
        return selectedInNetworkInsuranceIds;
    }

    public void setSelectedInNetworkInsuranceIds(List<Long> selectedInNetworkInsuranceIds) {
        this.selectedInNetworkInsuranceIds = selectedInNetworkInsuranceIds;
    }

    public String getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(String prerequisite) {
        this.prerequisite = prerequisite;
    }

    public String getExclusion() {
        return exclusion;
    }

    public void setExclusion(String exclusion) {
        this.exclusion = exclusion;
    }

    public boolean isEmpty() {
        return null == ObjectUtils.firstNonNull(allInsurancesAccepted, allowAppointments, confirmVisibility,
                appointmentsEmail, appointmentsSecureEmail, servicesSummaryDescription,
                primaryFocusIds, communityTypeIds, levelOfCareIds, ageGroupIds, serviceTreatmentApproachIds, emergencyServiceIds, languageServiceIds,
                ancillaryServiceIds, prerequisite, exclusion);
    }
}
