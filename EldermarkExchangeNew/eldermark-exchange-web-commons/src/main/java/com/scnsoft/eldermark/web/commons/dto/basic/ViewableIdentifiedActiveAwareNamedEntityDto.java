package com.scnsoft.eldermark.web.commons.dto.basic;

public class ViewableIdentifiedActiveAwareNamedEntityDto extends IdentifiedNamedEntityDto {
    private boolean canView;
    private Boolean isActive;

    public ViewableIdentifiedActiveAwareNamedEntityDto() {
    }

    public ViewableIdentifiedActiveAwareNamedEntityDto(Long id, String name, boolean canView, Boolean isActive) {
        super(id, name);
        this.canView = canView;
        this.isActive = isActive;
    }

    public boolean getCanView() {
        return canView;
    }

    public void setCanView(boolean canView) {
        this.canView = canView;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
