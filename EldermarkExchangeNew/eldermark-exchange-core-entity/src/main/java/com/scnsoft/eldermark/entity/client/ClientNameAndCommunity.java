package com.scnsoft.eldermark.entity.client;

import java.util.Objects;

@Deprecated
public class ClientNameAndCommunity extends ClientName implements ClientNameAndCommunityAware {

    private Long communityId;
    private String communityName;

    public ClientNameAndCommunity(Long id, String firstName, String lastName, Long communityId, String communityName) {
        super(id, firstName, lastName);
        this.communityId = communityId;
        this.communityName = communityName;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ClientNameAndCommunity that = (ClientNameAndCommunity) o;
        return Objects.equals(communityId, that.communityId) && Objects.equals(communityName, that.communityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(communityId, communityName);
    }
}
