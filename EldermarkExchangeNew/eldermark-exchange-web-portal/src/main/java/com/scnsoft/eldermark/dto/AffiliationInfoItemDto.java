package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedViewableEntityDto;

import java.util.List;

public class AffiliationInfoItemDto {

    private IdentifiedNamedViewableEntityDto organization;
    private List<IdentifiedNamedViewableEntityDto> communities;

    public IdentifiedNamedViewableEntityDto getOrganization() {
        return organization;
    }

    public void setOrganization(IdentifiedNamedViewableEntityDto organization) {
        this.organization = organization;
    }

    public List<IdentifiedNamedViewableEntityDto> getCommunities() {
        return communities;
    }

    public void setCommunities(List<IdentifiedNamedViewableEntityDto> communities) {
        this.communities = communities;
    }
}
