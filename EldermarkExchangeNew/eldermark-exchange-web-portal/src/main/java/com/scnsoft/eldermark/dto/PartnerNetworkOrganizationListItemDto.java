package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;

import java.util.Set;

public class PartnerNetworkOrganizationListItemDto extends IdentifiedTitledEntityDto {

    private Set<IdentifiedTitledEntityDto> communities;

    public Set<IdentifiedTitledEntityDto> getCommunities() {
        return communities;
    }

    public void setCommunities(Set<IdentifiedTitledEntityDto> communities) {
        this.communities = communities;
    }
}
