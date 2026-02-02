package com.scnsoft.eldermark.mobile.dto.client;

import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

public class ClientDto extends ClientListItemDto {

    private String unit;
    private Long genderId;
    private String gender;
    private String birthDate;
    private String cellPhone;
    private ClientPrimaryContactDto primaryContact;

    private boolean canViewCareTeam;

    private boolean canViewMedications;

    private boolean canViewLocationHistory;
    private boolean canReportLocation;

    private boolean canInviteCareTeamMember;

    private HieConsentPolicyType hieConsentPolicyName;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getGenderId() {
        return genderId;
    }

    public void setGenderId(Long genderId) {
        this.genderId = genderId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public ClientPrimaryContactDto getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(ClientPrimaryContactDto primaryContact) {
        this.primaryContact = primaryContact;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public boolean isCanViewCareTeam() {
        return canViewCareTeam;
    }

    public void setCanViewCareTeam(boolean canViewCareTeam) {
        this.canViewCareTeam = canViewCareTeam;
    }

    public boolean getCanViewMedications() {
        return canViewMedications;
    }

    public void setCanViewMedications(boolean canViewMedications) {
        this.canViewMedications = canViewMedications;
    }

    public boolean getCanViewLocationHistory() {
        return canViewLocationHistory;
    }

    public void setCanViewLocationHistory(boolean canViewLocationHistory) {
        this.canViewLocationHistory = canViewLocationHistory;
    }

    public boolean getCanReportLocation() {
        return canReportLocation;
    }

    public void setCanReportLocation(boolean canReportLocation) {
        this.canReportLocation = canReportLocation;
    }

    public boolean getCanInviteCareTeamMember() {
        return canInviteCareTeamMember;
    }

    public void setCanInviteCareTeamMember(boolean canInviteCareTeamMember) {
        this.canInviteCareTeamMember = canInviteCareTeamMember;
    }

    public HieConsentPolicyType getHieConsentPolicyName() {
        return hieConsentPolicyName;
    }

    public void setHieConsentPolicyName(HieConsentPolicyType hieConsentPolicyName) {
        this.hieConsentPolicyName = hieConsentPolicyName;
    }
}
