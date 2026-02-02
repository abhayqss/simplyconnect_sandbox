package com.scnsoft.eldermark.beans;

public class OrganizationFilter {

    private Boolean excludeAffiliated;
    private boolean canViewSdohReportsOnly;
    private boolean checkCommunitiesExist;
    private boolean excludeExternal;
    private Boolean isChatEnabled;
    private Boolean isESignEnabled;
    private Boolean areAppointmentsEnabled;

    public Boolean getExcludeAffiliated() {
        return excludeAffiliated;
    }

    public void setExcludeAffiliated(Boolean excludeAffiliated) {
        this.excludeAffiliated = excludeAffiliated;
    }

    public boolean getCanViewSdohReportsOnly() {
        return canViewSdohReportsOnly;
    }

    public void setCanViewSdohReportsOnly(boolean canViewSdohReportsOnly) {
        this.canViewSdohReportsOnly = canViewSdohReportsOnly;
    }

    public boolean isCheckCommunitiesExist() {
        return checkCommunitiesExist;
    }

    public void setCheckCommunitiesExist(boolean checkCommunitiesExist) {
        this.checkCommunitiesExist = checkCommunitiesExist;
    }

    public boolean getExcludeExternal() {
        return excludeExternal;
    }

    public void setExcludeExternal(boolean excludeExternal) {
        this.excludeExternal = excludeExternal;
    }

    public Boolean getIsChatEnabled() {
        return isChatEnabled;
    }

    public void setIsChatEnabled(Boolean chatEnabled) {
        isChatEnabled = chatEnabled;
    }

    public Boolean getIsESignEnabled() {
        return isESignEnabled;
    }

    public void setIsESignEnabled(Boolean eSignEnabled) {
        isESignEnabled = eSignEnabled;
    }

    public Boolean getAreAppointmentsEnabled() {
        return areAppointmentsEnabled;
    }

    public void setAreAppointmentsEnabled(Boolean areAppointmentsEnabled) {
        this.areAppointmentsEnabled = areAppointmentsEnabled;
    }
}
