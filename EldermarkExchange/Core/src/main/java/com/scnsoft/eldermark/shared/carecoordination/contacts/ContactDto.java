package com.scnsoft.eldermark.shared.carecoordination.contacts;

import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.shared.carecoordination.AddressDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.NameDto;

/**
 * Created by pzhurba on 29-Oct-15.
 */
public class ContactDto extends NameDto {
    private Long id;
    private KeyValueDto role;
    private String email;
    private AddressDto address;
    private String phone;
    private String fax;
    private Boolean enabledExchange;
    private CareTeamRoleCode roleCode;
    private CareTeamRoleCode oldRoleCode;
    private KeyValueDto organization;
    private String secureMessaging;
    private String oldSecureMessaging;
    private Boolean secureMessagingActive;
    private String secureMessagingError;
    private Boolean contact4d;
    private String login4d;
    private String company;
    private Long communityId;
    private String communityName;
    private String companyId;
    private Boolean expired;
    private Boolean qaIncidentReports;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public KeyValueDto getRole() {
        return role;
    }

    public void setRole(KeyValueDto role) {
        this.role = role;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public CareTeamRoleCode getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(CareTeamRoleCode roleCode) {
        this.roleCode = roleCode;
    }

    public CareTeamRoleCode getOldRoleCode() {
        return oldRoleCode;
    }

    public void setOldRoleCode(CareTeamRoleCode oldRoleCode) {
        this.oldRoleCode = oldRoleCode;
    }

    public KeyValueDto getOrganization() {
        return organization;
    }

    public void setOrganization(KeyValueDto organization) {
        this.organization = organization;
    }

    public Boolean getEnabledExchange() {
        return enabledExchange;
    }

    public void setEnabledExchange(Boolean enabledExchange) {
        this.enabledExchange = enabledExchange;
    }

    public String getSecureMessaging() {
        return secureMessaging;
    }

    public void setSecureMessaging(String secureMessaging) {
        this.secureMessaging = secureMessaging;
    }

    public Boolean getSecureMessagingActive() {
        return secureMessagingActive;
    }

    public void setSecureMessagingActive(Boolean secureMessagingActive) {
        this.secureMessagingActive = secureMessagingActive;
    }

    public String getSecureMessagingError() {
        return secureMessagingError;
    }

    public void setSecureMessagingError(String secureMessagingError) {
        this.secureMessagingError = secureMessagingError;
    }

    public Boolean getContact4d() {
        return contact4d;
    }

    public void setContact4d(Boolean contact4d) {
        this.contact4d = contact4d;
    }

    public String getLogin4d() {
        return login4d;
    }

    public void setLogin4d(String login4d) {
        this.login4d = login4d;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public String getOldSecureMessaging() {
        return oldSecureMessaging;
    }

    public void setOldSecureMessaging(String oldSecureMessaging) {
        this.oldSecureMessaging = oldSecureMessaging;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public Boolean getQaIncidentReports() {
        return qaIncidentReports;
    }

    public void setQaIncidentReports(Boolean qaIncidentReports) {
        this.qaIncidentReports = qaIncidentReports;
    }
}
