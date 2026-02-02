package com.scnsoft.eldermark.shared.carecoordination.community;

import com.scnsoft.eldermark.shared.marketplace.MarketplaceDto;


public class CommunityCreateDto {
    private Long id;
    private String name;
    private String oid;
    private String email;
    private String phone;
    private String mainLogoPath;

    private String city;
    private String street;
    private String postalCode;
    private Long stateId;

    private MarketplaceDto marketplace = new MarketplaceDto();

    public CommunityCreateDto() {}

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

    public String getMainLogoPath() {
        return mainLogoPath;
    }

    public void setMainLogoPath(String mainLogoPath) {
        this.mainLogoPath = mainLogoPath;
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

    public MarketplaceDto getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(MarketplaceDto marketplace) {
        this.marketplace = marketplace;
    }
}
