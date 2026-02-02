package com.scnsoft.eldermark.mobile.dto.client;

public class ClientListItemDto {

    private Long id;

    private boolean isFavourite;
    private Long avatarId;
    private String avatarName;

    private String firstName;
    private String middleName;
    private String lastName;

    private String fullName;

    private Long communityId;
    private String communityName;
    private Long organizationId;
    private String organizationName;

    private ClientAssociatedContactDto associatedContact;

    private boolean canView;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
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

    public ClientAssociatedContactDto getAssociatedContact() {
        return associatedContact;
    }

    public void setAssociatedContact(ClientAssociatedContactDto associatedContact) {
        this.associatedContact = associatedContact;
    }

    public boolean getCanView() {
        return canView;
    }

    public void setCanView(boolean canView) {
        this.canView = canView;
    }
}
