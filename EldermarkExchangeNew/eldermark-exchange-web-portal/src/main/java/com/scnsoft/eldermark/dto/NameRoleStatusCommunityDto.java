package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;

public class NameRoleStatusCommunityDto extends IdentifiedNamedEntityDto {

    private String status;
    private String role;
    private String communityTitle;

    public NameRoleStatusCommunityDto(
            Long id,
            String name,
            String role,
            String status,
            String communityTitle
    ) {
        super(id, name);
        this.role = role;
        this.status = status;
        this.communityTitle = communityTitle;
    }

    public String getStatus() {
        return status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCommunityTitle() {
        return communityTitle;
    }

    public void setCommunityTitle(String communityTitle) {
        this.communityTitle = communityTitle;
    }
}
