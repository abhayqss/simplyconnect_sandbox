package com.scnsoft.eldermark.dto;

public class ServiceTypeListItemDto extends ServiceCategoryAwareIdentifiedTitledDto {

    private boolean canAdditionalClinicalInfoBeShared;
    private boolean isClientRelated;
    private boolean isBusinessRelated;

    public ServiceTypeListItemDto(
            Long id,
            String label,
            Long serviceCategoryId,
            String serviceCategoryTitle,
            boolean canAdditionalClinicalInfoBeShared,
            boolean isClientRelated,
            boolean isBusinessRelated
    ) {
        super(id, label, serviceCategoryId, serviceCategoryTitle);
        this.canAdditionalClinicalInfoBeShared = canAdditionalClinicalInfoBeShared;
        this.isClientRelated = isClientRelated;
        this.isBusinessRelated = isBusinessRelated;
    }

    public boolean isCanAdditionalClinicalInfoBeShared() {
        return canAdditionalClinicalInfoBeShared;
    }

    public void setCanAdditionalClinicalInfoBeShared(boolean canAdditionalClinicalInfoBeShared) {
        this.canAdditionalClinicalInfoBeShared = canAdditionalClinicalInfoBeShared;
    }

    public boolean getIsClientRelated() {
        return isClientRelated;
    }

    public void setIsClientRelated(boolean clientRelated) {
        isClientRelated = clientRelated;
    }

    public boolean getIsBusinessRelated() {
        return isBusinessRelated;
    }

    public void setIsBusinessRelated(boolean businessRelated) {
        isBusinessRelated = businessRelated;
    }
}
