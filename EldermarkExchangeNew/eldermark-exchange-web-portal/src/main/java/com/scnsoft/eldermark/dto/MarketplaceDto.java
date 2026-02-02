package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.validation.ValidationRegExpConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class MarketplaceDto {

    private Long id;

    @NotEmpty
    @Size(max = 20000)
    private String servicesSummaryDescription;

    @NotEmpty
    private List<Long> serviceCategoryIds = new ArrayList<Long>();
    @NotEmpty
    private List<Long> serviceIds = new ArrayList<Long>();
    private List<Long> languageIds = new ArrayList<Long>();

    private boolean confirmVisibility;

    private boolean isReferralEnabled;
    private boolean canAddReferral;
    private boolean isSaved;

    private Integer rating;

    private List<@NotEmpty @Pattern(regexp = ValidationRegExpConstants.EMAIL_REGEXP) @Size(max = 256) String> referralEmails;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServicesSummaryDescription() {
        return servicesSummaryDescription;
    }

    public void setServicesSummaryDescription(String servicesSummaryDescription) {
        this.servicesSummaryDescription = servicesSummaryDescription;
    }

    public List<Long> getServiceCategoryIds() {
        return serviceCategoryIds;
    }

    public void setServiceCategoryIds(List<Long> serviceCategoryIds) {
        this.serviceCategoryIds = serviceCategoryIds;
    }

    public List<Long> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Long> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public List<Long> getLanguageIds() {
        return languageIds;
    }

    public void setLanguageIds(List<Long> languageIds) {
        this.languageIds = languageIds;
    }

    public boolean getConfirmVisibility() {
        return confirmVisibility;
    }

    public void setConfirmVisibility(boolean confirmVisibility) {
        this.confirmVisibility = confirmVisibility;
    }

    public List<String> getReferralEmails() {
        return referralEmails;
    }

    public void setReferralEmails(List<String> referralEmails) {
        this.referralEmails = referralEmails;
    }

    public boolean getIsReferralEnabled() {
        return isReferralEnabled;
    }

    public void setIsReferralEnabled(boolean referralEnabled) {
        isReferralEnabled = referralEnabled;
    }

    public boolean getCanAddReferral() {
        return canAddReferral;
    }

    public void setCanAddReferral(boolean canAddReferral) {
        this.canAddReferral = canAddReferral;
    }

    public boolean getIsSaved() {
        return isSaved;
    }

    public void setIsSaved(boolean saved) {
        isSaved = saved;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
