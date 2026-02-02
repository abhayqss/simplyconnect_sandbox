package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedViewableEntityDto;
import com.scnsoft.eldermark.entity.DatabaseOrgCountEntity_;
import com.scnsoft.eldermark.entity.Organization_;

import java.util.List;

public class OrganizationListItemDto {
    private Long id;
    @DefaultSort
    private String name;
    @EntitySort(joined = {Organization_.DATABASE_ORG_COUNT_ENTITY, DatabaseOrgCountEntity_.ORG_HIE_COUNT})
    private Long communityCount;
    @EntitySort(joined = {Organization_.DATABASE_ORG_COUNT_ENTITY, DatabaseOrgCountEntity_.AFFILIATED_ORG_COUNT})
    List<IdentifiedNamedViewableEntityDto> affiliatedOrganizations;
    private Boolean createdAutomatically;
    private Long lastModified;
    private boolean canEdit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCommunityCount() {
        return communityCount;
    }

    public void setCommunityCount(Long communityCount) {
        this.communityCount = communityCount;
    }

    public List<IdentifiedNamedViewableEntityDto> getAffiliatedOrganizations() {
        return affiliatedOrganizations;
    }

    public void setAffiliatedOrganizations(List<IdentifiedNamedViewableEntityDto> affiliatedOrganizations) {
        this.affiliatedOrganizations = affiliatedOrganizations;
    }

    public Boolean getCreatedAutomatically() {
        return createdAutomatically;
    }

    public void setCreatedAutomatically(Boolean createdAutomatically) {
        this.createdAutomatically = createdAutomatically;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }
}
