package com.scnsoft.eldermark.beans.twilio.user;

public class CommunityAndRoleAwareIdentityListItemDto extends IdentityListItemDto {

    private String role;

    public CommunityAndRoleAwareIdentityListItemDto(
            String identity,
            Long employeeId,
            String firstName,
            String lastName,
            Long communityId,
            String communityName,
            Long avatarId,
            String avatarName,
            Boolean isActive,
            String role,
            Boolean canChat,
            Boolean canCall
    ) {
        super(identity, employeeId, firstName, lastName, communityId, communityName, avatarId, avatarName, isActive, canChat, canCall);
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
