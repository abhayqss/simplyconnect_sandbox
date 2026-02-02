package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;

import javax.validation.constraints.NotNull;
import java.util.List;

public class AffiliatedRelationshipItemDto {

    private List<IdentifiedTitledEntityDto> primaryCommunities;
    @NotNull
    private IdentifiedTitledEntityDto affiliatedOrganization;

    private List<IdentifiedTitledEntityDto> affiliatedCommunities;

    public List<IdentifiedTitledEntityDto> getPrimaryCommunities() {
        return primaryCommunities;
    }

    public void setPrimaryCommunities(List<IdentifiedTitledEntityDto> primaryCommunities) {
        this.primaryCommunities = primaryCommunities;
    }

    public IdentifiedTitledEntityDto getAffiliatedOrganization() {
        return affiliatedOrganization;
    }

    public void setAffiliatedOrganization(IdentifiedTitledEntityDto affiliatedOrganization) {
        this.affiliatedOrganization = affiliatedOrganization;
    }

    public List<IdentifiedTitledEntityDto> getAffiliatedCommunities() {
        return affiliatedCommunities;
    }

    public void setAffiliatedCommunities(List<IdentifiedTitledEntityDto> affiliatedCommunities) {
        this.affiliatedCommunities = affiliatedCommunities;
    }
}
