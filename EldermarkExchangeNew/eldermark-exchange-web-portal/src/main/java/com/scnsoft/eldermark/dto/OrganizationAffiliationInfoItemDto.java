package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedViewableEntityDto;

import java.util.List;

public class OrganizationAffiliationInfoItemDto extends AffiliationInfoItemDto {

    private List<IdentifiedNamedViewableEntityDto> ownCommunities;

    public List<IdentifiedNamedViewableEntityDto> getOwnCommunities() {
        return ownCommunities;
    }

    public void setOwnCommunities(List<IdentifiedNamedViewableEntityDto> ownCommunities) {
        this.ownCommunities = ownCommunities;
    }
}
