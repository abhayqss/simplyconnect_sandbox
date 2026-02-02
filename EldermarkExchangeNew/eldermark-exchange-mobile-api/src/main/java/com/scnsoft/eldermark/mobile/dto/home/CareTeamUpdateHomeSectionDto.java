package com.scnsoft.eldermark.mobile.dto.home;

import com.scnsoft.eldermark.entity.careteam.CareTeamMemberModificationType;

public class CareTeamUpdateHomeSectionDto {

    private Long updateId;
    //todo security - allow to view avatar of deleted care team member
    private Long avatarId;
    private String avatarName;
    private Long careTeamMemberId;
    private String firstName;
    private String lastName;
    private String middleName;
    private CareTeamMemberModificationType status;
    private String performerFirstName;
    private String performerLastName;
    private String performerMiddleName;
    private Long clientId;

    public Long getUpdateId() {
        return updateId;
    }

    public void setUpdateId(Long updateId) {
        this.updateId = updateId;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public Long getCareTeamMemberId() {
        return careTeamMemberId;
    }

    public void setCareTeamMemberId(Long careTeamMemberId) {
        this.careTeamMemberId = careTeamMemberId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public CareTeamMemberModificationType getStatus() {
        return status;
    }

    public void setStatus(CareTeamMemberModificationType status) {
        this.status = status;
    }

    public String getPerformerFirstName() {
        return performerFirstName;
    }

    public void setPerformerFirstName(String performerFirstName) {
        this.performerFirstName = performerFirstName;
    }

    public String getPerformerLastName() {
        return performerLastName;
    }

    public void setPerformerLastName(String performerLastName) {
        this.performerLastName = performerLastName;
    }

    public String getPerformerMiddleName() {
        return performerMiddleName;
    }

    public void setPerformerMiddleName(String performerMiddleName) {
        this.performerMiddleName = performerMiddleName;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
