package com.scnsoft.eldermark.dto;

public class ClientNameCommunityIdListItemDto {
    private Long id;
    private String fullName;
    private Long communityId;
    private String communityName;

    public ClientNameCommunityIdListItemDto() {
    }

    public ClientNameCommunityIdListItemDto(Long id, String fullName, Long communityId, String communityName) {
        this.id = id;
        this.fullName = fullName;
        this.communityId = communityId;
        this.communityName = communityName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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
}
