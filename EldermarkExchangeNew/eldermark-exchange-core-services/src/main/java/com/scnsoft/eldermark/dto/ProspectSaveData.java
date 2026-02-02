package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.entity.prospect.Prospect;

public class ProspectSaveData {

    private Prospect entity;
    private AvatarUpdateData prospectAvatar;
    private AvatarUpdateData secondOccupantAvatar;

    public Prospect getEntity() {
        return entity;
    }

    public void setEntity(Prospect entity) {
        this.entity = entity;
    }

    public AvatarUpdateData getProspectAvatar() {
        return prospectAvatar;
    }

    public void setProspectAvatar(AvatarUpdateData prospectAvatar) {
        this.prospectAvatar = prospectAvatar;
    }

    public AvatarUpdateData getSecondOccupantAvatar() {
        return secondOccupantAvatar;
    }

    public void setSecondOccupantAvatar(AvatarUpdateData secondOccupantAvatar) {
        this.secondOccupantAvatar = secondOccupantAvatar;
    }
}
