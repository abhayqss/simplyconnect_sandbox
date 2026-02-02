package com.scnsoft.eldermark.mobile.dto.ccd.medication;

public class MedicationListItemDto extends BaseMedicationDto {

    boolean canEdit;

    public boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }
}
