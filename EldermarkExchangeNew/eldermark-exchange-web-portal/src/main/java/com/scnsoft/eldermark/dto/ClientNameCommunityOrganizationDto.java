package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.dto.ClientNameCommunityIdListItemDto;

public class ClientNameCommunityOrganizationDto extends ClientNameCommunityIdListItemDto {

    private Long organizationId;
    private String organizationName;

    public ClientNameCommunityOrganizationDto() {
    }

    public ClientNameCommunityOrganizationDto(Long id, String fullName, Long communityId, String communityName, Long organizationId, String organizationName) {
        super(id, fullName, communityId, communityName);
        this.organizationId = organizationId;
        this.organizationName = organizationName;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}
