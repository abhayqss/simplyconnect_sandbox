package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
public class OrganizationAddress extends StringLegacyTableAwareEntity implements Address {
    @Column(length = 15, name = "use_code")
    private String postalAddressUse;

    @Column(length = 255, name = "street_address")
    private String streetAddress;

    @Column(length = 100, name = "city")
    private String city;

    @Column(length = 100, name = "state")
    private String state;

    @Column(length = 50, name = "postal_code")
    private String postalCode;

    @Column(length = 100, name = "country")
    private String country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Organization organization;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "locationUpToDate")
    private Boolean locationUpToDate;

    @PreUpdate
    @PrePersist
    public void onUpdate() {
        this.setLocationUpToDate(false);
    }

    public String getPostalAddressUse() {
        return postalAddressUse;
    }

    public void setPostalAddressUse(String postalAddressUse) {
        this.postalAddressUse = postalAddressUse;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getLocationUpToDate() {
        return locationUpToDate;
    }

    public void setLocationUpToDate(Boolean locationUpToDate) {
        this.locationUpToDate = locationUpToDate;
    }

    @Transient
    public String getDisplayAddress() {
        return (getStreetAddress()==null?"":getStreetAddress() + " ") +
                (getCity()==null?"":getCity() + " ") +
                (getState()==null?"":getState() + " ") +
                (getPostalCode()==null?"":getPostalCode());
    }
}
