package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedEntityDto;

public class CareTeamMemberRoleAvatarAwareDto implements IdentifiedEntityDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private Long avatarId;
    private String role;

    public CareTeamMemberRoleAvatarAwareDto(Long id, String firstName, String lastName, String fullName, Long avatarId, String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.avatarId = avatarId;
        this.role = role;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
