package com.scnsoft.eldermark.dto.document.folder;

import com.scnsoft.eldermark.entity.document.DocumentFolderType;
import com.scnsoft.eldermark.validation.SpELAssert;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@SpELAssert(
        value = "organizationId != null || #isNotEmpty(communityIds)",
        message = "At least one of the fields: organizationId, communityIds shouldn't be empty",
        helpers = {CollectionUtils.class}
)
public class DocumentFolderFilterDto {

    private Long organizationId;
    private List<Long> communityIds;
    private List<DocumentFolderType> types;
    private boolean canUpload;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(List<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public List<DocumentFolderType> getTypes() {
        return types;
    }

    public void setTypes(List<DocumentFolderType> types) {
        this.types = types;
    }

    public boolean isCanUpload() {
        return canUpload;
    }

    public void setCanUpload(boolean canUpload) {
        this.canUpload = canUpload;
    }
}
