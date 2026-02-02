package com.scnsoft.eldermark.dto.prospect;

import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.document.CcdCode_;
import com.scnsoft.eldermark.entity.prospect.Prospect_;

public class ProspectListItemDto {

    private Long id;

    private Long avatarId;

    @EntitySort.List(
            {
                    @EntitySort(Prospect_.FIRST_NAME),
                    @EntitySort(Prospect_.MIDDLE_NAME),
                    @EntitySort(Prospect_.LAST_NAME)
            }
    )
    private String fullName;

    @EntitySort(joined = {Prospect_.GENDER, CcdCode_.DISPLAY_NAME})
    private String gender;

    private String birthDate;

    private Long documentSent;

    private Long documentReceived;

    @EntitySort(joined = {Prospect_.COMMUNITY, Community_.NAME})
    private String communityName;

    private Long createdDate;

    private Boolean isActive;

    private Boolean canView;

    private Boolean canEdit;

    private Boolean canRequestSignature;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
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

    public Long getDocumentSent() {
        return documentSent;
    }

    public void setDocumentSent(Long documentSent) {
        this.documentSent = documentSent;
    }

    public Long getDocumentReceived() {
        return documentReceived;
    }

    public void setDocumentReceived(Long documentReceived) {
        this.documentReceived = documentReceived;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Boolean getCanView() {
        return canView;
    }

    public void setCanView(Boolean canView) {
        this.canView = canView;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    public Boolean getCanRequestSignature() {
        return canRequestSignature;
    }

    public void setCanRequestSignature(Boolean canRequestSignature) {
        this.canRequestSignature = canRequestSignature;
    }
}
