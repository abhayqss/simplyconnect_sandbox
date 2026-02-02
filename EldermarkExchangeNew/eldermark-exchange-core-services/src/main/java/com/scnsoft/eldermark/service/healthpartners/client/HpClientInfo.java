package com.scnsoft.eldermark.service.healthpartners.client;

import java.time.LocalDate;
import java.util.Objects;

public class HpClientInfo {
    private String memberIdentifier;
    private String memberFirstName;
    private String memberMiddleName;
    private String memberLastName;
    private LocalDate birthDate;

    private Long communityId;
    private boolean active;

    public HpClientInfo(String memberIdentifier, String memberFirstName, String memberMiddleName,
                        String memberLastName, LocalDate birthDate, Long communityId,
                        boolean active) {
        this.memberIdentifier = memberIdentifier;
        this.memberFirstName = memberFirstName;
        this.memberMiddleName = memberMiddleName;
        this.memberLastName = memberLastName;
        this.birthDate = birthDate;
        this.communityId = communityId;
        this.active = active;
    }

    public HpClientInfo() {

    }

    public String getMemberIdentifier() {
        return memberIdentifier;
    }

    public void setMemberIdentifier(String memberIdentifier) {
        this.memberIdentifier = memberIdentifier;
    }

    public String getMemberFirstName() {
        return memberFirstName;
    }

    public void setMemberFirstName(String memberFirstName) {
        this.memberFirstName = memberFirstName;
    }

    public String getMemberMiddleName() {
        return memberMiddleName;
    }

    public void setMemberMiddleName(String memberMiddleName) {
        this.memberMiddleName = memberMiddleName;
    }

    public String getMemberLastName() {
        return memberLastName;
    }

    public void setMemberLastName(String memberLastName) {
        this.memberLastName = memberLastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }


    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HpClientInfo that = (HpClientInfo) o;
        return Objects.equals(memberIdentifier, that.memberIdentifier) &&
                Objects.equals(memberFirstName, that.memberFirstName) &&
                Objects.equals(memberMiddleName, that.memberMiddleName) &&
                Objects.equals(memberLastName, that.memberLastName) &&
                Objects.equals(birthDate, that.birthDate) &&
                Objects.equals(communityId, that.communityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberIdentifier, memberFirstName, memberMiddleName, memberLastName, birthDate, communityId);
    }
}