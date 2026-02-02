package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.dto.community.CommunitySignatureConfig;
import com.scnsoft.eldermark.validation.ValidationGroups;

import javax.validation.constraints.NotNull;

public class CommunitySignatureConfigDto implements CommunitySignatureConfig {

    private Boolean canEdit;

    @NotNull(groups = ValidationGroups.CommunitySignatureConfig.class)
    private Boolean isPinEnabled;

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    @Override
    public Boolean getIsPinEnabled() {
        return isPinEnabled;
    }

    public void setIsPinEnabled(Boolean signaturePinEnabled) {
        isPinEnabled = signaturePinEnabled;
    }
}
