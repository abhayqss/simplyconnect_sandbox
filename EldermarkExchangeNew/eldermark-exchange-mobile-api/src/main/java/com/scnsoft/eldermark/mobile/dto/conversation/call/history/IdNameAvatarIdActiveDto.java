package com.scnsoft.eldermark.mobile.dto.conversation.call.history;

import com.scnsoft.eldermark.beans.projection.AvatarIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;

public class IdNameAvatarIdActiveDto implements IdAware, AvatarIdAware {

    private Long id;
    private Long avatarId;
    private String avatarName;
    private boolean isActive;

    public IdNameAvatarIdActiveDto(Long id, Long avatarId, String avatarName, boolean isActive) {
        this.id = id;
        this.avatarId = avatarId;
        this.avatarName = avatarName;
        this.isActive = isActive;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
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

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }
}
