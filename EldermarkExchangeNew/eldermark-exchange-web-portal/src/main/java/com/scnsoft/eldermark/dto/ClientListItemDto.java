package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.Client_;

import java.util.List;

public class ClientListItemDto {

    private Long id;

    @EntitySort.List(
            {
                    @EntitySort(Client_.FIRST_NAME),
                    @EntitySort(Client_.LAST_NAME)
            }
    )
    private String fullName;
    private String gender;
    private String birthDate;
    private String ssnLastFourDigits;
    private String community;
    private Long communityId;
    private Long createdDate;
    private Long avatarId;

    //TODO remove field
    private Long events;
    private List<ClientListItemDto> merged;
    private Boolean isActive;
    private String riskScore;
    private boolean canView;
    private boolean canEdit;
    private boolean canRequestSignature;

    @EntitySort(Client_.UNIT_NUMBER)
    private String unit;

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

    public String getSsnLastFourDigits() {
        return ssnLastFourDigits;
    }

    public void setSsnLastFourDigits(String ssnLastFourDigits) {
        this.ssnLastFourDigits = ssnLastFourDigits;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public Long getEvents() {
        return events;
    }

    public void setEvents(Long events) {
        this.events = events;
    }

    public List<ClientListItemDto> getMerged() {
        return merged;
    }

    public void setMerged(List<ClientListItemDto> merged) {
        this.merged = merged;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public String getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(String riskScore) {
        this.riskScore = riskScore;
    }

    public boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean getCanView() {
        return canView;
    }

    public void setCanView(boolean canView) {
        this.canView = canView;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean getCanRequestSignature() {
        return canRequestSignature;
    }

    public void setCanRequestSignature(boolean canRequestSignature) {
        this.canRequestSignature = canRequestSignature;
    }
}
