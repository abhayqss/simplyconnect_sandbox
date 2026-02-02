package com.scnsoft.eldermark.shared.carecoordination;

import com.scnsoft.eldermark.shared.carecoordination.community.CommunityListItemDto;
import com.scnsoft.eldermark.shared.marketplace.MarketplaceDto;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author averazub
 * @author knetkachou
 * @author mradzivonenka
 * @author phomal
 * Created by averazub on 3/21/2016.
 */
public class OrganizationDto {
    private Long id;
    @NotNull
    private String name;
    private String loginCompanyId;
    private String oid;
    private String email;
    private String phone;
    private String city;
    private String street;
    private String postalCode;
    private Long stateId;
    private Boolean copyEventNotificationsForPatients;
    private String mainLogoPath;
    private List<OrganizationAffiliatedDetailsDto> affiliatedDetails;
    private KeyValueDto state;
    private List<CommunityListItemDto> communities;
    private MarketplaceDto marketplace = new MarketplaceDto();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginCompanyId() {
        return loginCompanyId;
    }

    public void setLoginCompanyId(String loginCompanyId) {
        this.loginCompanyId = loginCompanyId;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public Boolean getCopyEventNotificationsForPatients() {
        return copyEventNotificationsForPatients;
    }

    public void setCopyEventNotificationsForPatients(Boolean copyEventNotificationsForPatients) {
        this.copyEventNotificationsForPatients = copyEventNotificationsForPatients;
    }

    public String getMainLogoPath() {
        return mainLogoPath;
    }

    public void setMainLogoPath(String mainLogoPath) {
        this.mainLogoPath = mainLogoPath;
    }

    public List<OrganizationAffiliatedDetailsDto> getAffiliatedDetails() {
        return affiliatedDetails;
    }

    public void setAffiliatedDetails(List<OrganizationAffiliatedDetailsDto> affiliatedDetails) {
        this.affiliatedDetails = affiliatedDetails;
    }

    public KeyValueDto getState() {
        return state;
    }

    public void setState(KeyValueDto state) {
        this.state = state;
    }

    public String getDisplayAddress() {
        return (getStreet()==null?"":getStreet() + " ") +
                (getCity()==null?"":getCity() + " ") +
                (getState()==null?"":getState().getLabel() + " ") +
                (getPostalCode()==null?"":getPostalCode());
    }

    public List<CommunityListItemDto> getCommunities() {
        return communities;
    }

    public void setCommunities(List<CommunityListItemDto> communities) {
        this.communities = communities;
    }

    public MarketplaceDto getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(MarketplaceDto marketplace) {
        this.marketplace = marketplace;
    }
}
