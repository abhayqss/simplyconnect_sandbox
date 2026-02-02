package com.scnsoft.eldermark.shared.carecoordination;

public class ChangeOrganizationResultDto {
    private Boolean success;
    private Boolean showCommunitiesTab;

    public ChangeOrganizationResultDto(Boolean success, Boolean showCommunitiesTab) {
        this.success = success;
        this.showCommunitiesTab = showCommunitiesTab;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getShowCommunitiesTab() {
        return showCommunitiesTab;
    }

    public void setShowCommunitiesTab(Boolean showCommunitiesTab) {
        this.showCommunitiesTab = showCommunitiesTab;
    }
}
