package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.beans.security.projection.dto.ContactSecurityFieldsAware;
import com.scnsoft.eldermark.validation.ValidationGroups;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

public class ContactDto extends NameDto implements ContactSecurityFieldsAware {

    @NotNull(groups = ValidationGroups.Update.class)
    private Long id;

    //commented since login=email in all contacts created via web
    //Phase 2 will include a solution for 4D contacts which may have login != email
    //private String email;

    @NotEmpty
    @Size(max = 256)
    @Pattern(groups = ValidationGroups.Create.class, regexp = ValidationRegExpConstants.EMAIL_REGEXP)
    private String login;

    @NotNull
    private Long systemRoleId;
    
    private String systemRoleTitle;

    private String systemRoleName;

    private String organizationName;

    @NotNull
    private Long organizationId;

    private String communityName;

    @NotNull
    private Long communityId;

    private AddressDto address;

    @Size(max = 16)
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String phone;

    @Size(max = 16)
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String fax;

    @NotEmpty
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String mobilePhone;
    
    private MultipartFile avatar;
    
    private Long avatarId;

    private String avatarName;

    private Boolean shouldRemoveAvatar;

    @Size(max = 256)
    @Pattern(regexp = ValidationRegExpConstants.EMAIL_REGEXP)
    private String secureMail;
    
    private Boolean enableContact;
    
    private TypeDto status;
    
    private boolean isSecureMessagingEnabled;

    private boolean isQaIncidentReports;

    @NotNull
    private Boolean isCommunityAddressUsed;

    @Size(max = 100)
    private List<Long> associatedClientIds;

    private List<ClientNameCommunityIdListItemDto> associatedClients;

    private List<String> activePrimaryContactClientNames;

    private Integer inactivePrimaryContactClientsCount;

    private Boolean shouldRemovePrimaryContacts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Boolean getEnableContact() {
        return enableContact;
    }

    public void setEnableContact(Boolean enableContact) {
        this.enableContact = enableContact;
    }

    public Long getSystemRoleId() {
        return systemRoleId;
    }

    public void setSystemRoleId(Long systemRoleId) {
        this.systemRoleId = systemRoleId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getSecureMail() {
        return secureMail;
    }

    public void setSecureMail(String secureMail) {
        this.secureMail = secureMail;
    }

    public boolean isEnableContact() {
        return enableContact;
    }

    public void setEnableContact(boolean enableContact) {
        this.enableContact = enableContact;
    }

    public String getSystemRoleTitle() {
        return systemRoleTitle;
    }

    public void setSystemRoleTitle(String systemRoleTitle) {
        this.systemRoleTitle = systemRoleTitle;
    }

    public String getSystemRoleName() {
        return systemRoleName;
    }

    public void setSystemRoleName(String systemRoleName) {
        this.systemRoleName = systemRoleName;
    }

    public TypeDto getStatus() {
        return status;
    }

    public void setStatus(TypeDto status) {
        this.status = status;
    }

    public boolean isSecureMessagingEnabled() {
        return isSecureMessagingEnabled;
    }

    public void setSecureMessagingEnabled(boolean isSecureMessagingEnabled) {
        this.isSecureMessagingEnabled = isSecureMessagingEnabled;
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

    public MultipartFile getAvatar() {
		return avatar;
	}

	public void setAvatar(MultipartFile avatar) {
		this.avatar = avatar;
	}

    public Boolean getShouldRemoveAvatar() {
        return shouldRemoveAvatar;
    }

    public void setShouldRemoveAvatar(Boolean shouldRemoveAvatar) {
        this.shouldRemoveAvatar = shouldRemoveAvatar;
    }

    public boolean isQaIncidentReports() {
        return isQaIncidentReports;
    }

    public void setQaIncidentReports(boolean qaIncidentReports) {
        isQaIncidentReports = qaIncidentReports;
    }

    public Boolean getIsCommunityAddressUsed() {
        return isCommunityAddressUsed;
    }

    public void setIsCommunityAddressUsed(Boolean isCommunityAddressUsed) {
        this.isCommunityAddressUsed = isCommunityAddressUsed;
    }

    public List<Long> getAssociatedClientIds() {
        return associatedClientIds;
    }

    public void setAssociatedClientIds(List<Long> associatedClientIds) {
        this.associatedClientIds = associatedClientIds;
    }

    public List<ClientNameCommunityIdListItemDto> getAssociatedClients() {
        return associatedClients;
    }

    public void setAssociatedClients(List<ClientNameCommunityIdListItemDto> associatedClients) {
        this.associatedClients = associatedClients;
    }

    public List<String> getActivePrimaryContactClientNames() {
        return activePrimaryContactClientNames;
    }

    public void setActivePrimaryContactClientNames(List<String> activePrimaryContactClientNames) {
        this.activePrimaryContactClientNames = activePrimaryContactClientNames;
    }

    public Integer getInactivePrimaryContactClientsCount() {
        return inactivePrimaryContactClientsCount;
    }

    public void setInactivePrimaryContactClientsCount(Integer inactivePrimaryContactClientsCount) {
        this.inactivePrimaryContactClientsCount = inactivePrimaryContactClientsCount;
    }

    public Boolean getShouldRemovePrimaryContacts() {
        return shouldRemovePrimaryContacts;
    }

    public void setShouldRemovePrimaryContacts(Boolean shouldRemovePrimaryContacts) {
        this.shouldRemovePrimaryContacts = shouldRemovePrimaryContacts;
    }
}
