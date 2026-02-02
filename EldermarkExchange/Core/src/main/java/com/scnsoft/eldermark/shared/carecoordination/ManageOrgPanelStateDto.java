package com.scnsoft.eldermark.shared.carecoordination;

import java.util.List;

/**
 * Created by averazub on 4/21/2016.
 */
public class ManageOrgPanelStateDto {
    private List<KeyValueDto> organizations;
    private List<KeyValueDto> communities;
    private Long currentOrganizationId;
    private Long currentCommunityId;
    private boolean showPanel;
    private boolean showManageOrgs;
    private boolean showChooseOrgPanel;
    private boolean showChooseCommunityPanel;

    public List<KeyValueDto> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<KeyValueDto> organizations) {
        this.organizations = organizations;
    }

    public List<KeyValueDto> getCommunities() {
        return communities;
    }

    public void setCommunities(List<KeyValueDto> communities) {
        this.communities = communities;
    }

    public Long getCurrentOrganizationId() {
        return currentOrganizationId;
    }

    public void setCurrentOrganizationId(Long currentOrganizationId) {
        this.currentOrganizationId = currentOrganizationId;
    }

    public Long getCurrentCommunityId() {
        return currentCommunityId;
    }

    public void setCurrentCommunityId(Long currentCommunityId) {
        this.currentCommunityId = currentCommunityId;
    }

    public boolean isShowPanel() {
        return showPanel;
    }

    public void setShowPanel(boolean showPanel) {
        this.showPanel = showPanel;
    }

    public boolean isShowManageOrgs() {
        return showManageOrgs;
    }

    public void setShowManageOrgs(boolean showManageOrgs) {
        this.showManageOrgs = showManageOrgs;
    }

    public boolean isShowChooseOrgPanel() {
        return showChooseOrgPanel;
    }

    public void setShowChooseOrgPanel(boolean showChooseOrgPanel) {
        this.showChooseOrgPanel = showChooseOrgPanel;
    }

    public boolean isShowChooseCommunityPanel() {
        return showChooseCommunityPanel;
    }

    public void setShowChooseCommunityPanel(boolean showChooseCommunityPanel) {
        this.showChooseCommunityPanel = showChooseCommunityPanel;
    }
}
