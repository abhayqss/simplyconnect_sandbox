package com.scnsoft.eldermark.shared.carecoordination.service;

/**
 * Created by knetkachou on 4/3/2017.
 */
public class AffiliatedOrganizationNotificationDto {
//    private String primaryComunity;
    private String primaryOrganization;
    private String affiliatedOrganization;
    private String affiliatedCommunity;
    private String fullName;
    private String recipientFullName;
    private String email;
    private String link;

//    public String getPrimaryComunity() {
//        return primaryComunity;
//    }
//
//    public void setPrimaryComunity(String primaryComunity) {
//        this.primaryComunity = primaryComunity;
//    }

    public String getPrimaryOrganization() {
        return primaryOrganization;
    }

    public void setPrimaryOrganization(String primaryOrganization) {
        this.primaryOrganization = primaryOrganization;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRecipientFullName() {
        return recipientFullName;
    }

    public void setRecipientFullName(String recipientFullName) {
        this.recipientFullName = recipientFullName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAffiliatedOrganization() {
        return affiliatedOrganization;
    }

    public void setAffiliatedOrganization(String affiliatedOrganization) {
        this.affiliatedOrganization = affiliatedOrganization;
    }
}
