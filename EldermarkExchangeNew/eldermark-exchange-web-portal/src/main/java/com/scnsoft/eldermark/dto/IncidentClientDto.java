package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncidentClientDto {

    private Long id;

    @NotEmpty
    private String fullName;

    @NotEmpty
    @Size(max = 256)
    private String unit;

    @NotEmpty
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String phone;

    @NotEmpty
    @Size(max = 256)
    private String siteName;

    @NotEmpty
    @Size(max = 256)
    private String address;

    private Long avatarId;

    private boolean canView;

    private Long communityId;

    private boolean hasAssignedCareTeamMembersWithEnabledConversations;
    private boolean hasAssignedCareTeamMembersWithEnabledVideoConversations;
    private HieConsentPolicyType hieConsentPolicyName;
    private Boolean isActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean getCanView() {
        return canView;
    }

    public void setCanView(boolean canView) {
        this.canView = canView;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public boolean getHasAssignedCareTeamMembersWithEnabledConversations() {
        return hasAssignedCareTeamMembersWithEnabledConversations;
    }

    public void setHasAssignedCareTeamMembersWithEnabledConversations(boolean hasAssignedCareTeamMembersWithEnabledConversations) {
        this.hasAssignedCareTeamMembersWithEnabledConversations = hasAssignedCareTeamMembersWithEnabledConversations;
    }

    public boolean getHasAssignedCareTeamMembersWithEnabledVideoConversations() {
        return hasAssignedCareTeamMembersWithEnabledVideoConversations;
    }

    public void setHasAssignedCareTeamMembersWithEnabledVideoConversations(boolean hasAssignedCareTeamMembersWithEnabledVideoConversations) {
        this.hasAssignedCareTeamMembersWithEnabledVideoConversations = hasAssignedCareTeamMembersWithEnabledVideoConversations;
    }

    public HieConsentPolicyType getHieConsentPolicyName() {
        return hieConsentPolicyName;
    }

    public void setHieConsentPolicyName(HieConsentPolicyType hieConsentPolicyName) {
        this.hieConsentPolicyName = hieConsentPolicyName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
