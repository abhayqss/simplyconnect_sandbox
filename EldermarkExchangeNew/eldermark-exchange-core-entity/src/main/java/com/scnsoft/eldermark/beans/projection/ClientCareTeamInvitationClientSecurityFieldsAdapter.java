package com.scnsoft.eldermark.beans.projection;

public class ClientCareTeamInvitationClientSecurityFieldsAdapter implements IdAndOrganizationIdAndCommunityIdAware {

    private final ClientCareTeamInvitationSecurityFieldsAware invitation;

    public ClientCareTeamInvitationClientSecurityFieldsAdapter(ClientCareTeamInvitationSecurityFieldsAware invitation) {
        this.invitation = invitation;
    }

    @Override
    public Long getCommunityId() {
        return invitation.getClientCommunityId();
    }

    @Override
    public Long getId() {
        return invitation.getClientId();
    }

    @Override
    public Long getOrganizationId() {
        return invitation.getClientOrganizationId();
    }
}
