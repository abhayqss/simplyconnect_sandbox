package com.scnsoft.eldermark.entity.community;

import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.basic.StringLegacyTableAwareEntity;

import javax.persistence.*;

@Entity
@Table(name = "OrganizationAddress")
public class CommunityAddress extends StringLegacyTableAwareEntity implements Address {

    private static final long serialVersionUID = 1L;

    @Column(name = "use_code")
    private String postalAddressUse;

    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country")
    private String country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Community community;

    @Column(name = "longitude", columnDefinition = "decimal(10,4)")
    private Double longitude;

    @Column(name = "latitude", columnDefinition = "decimal(10,4)")
    private Double latitude;

    @Column(name = "locationUpToDate")
    private Boolean locationUpToDate;

    @PreUpdate
    @PrePersist
    public void onUpdate() {
        this.setLocationUpToDate(false);
    }

    public Boolean getLocationUpToDate() {
        return locationUpToDate;
    }

    public void setLocationUpToDate(Boolean locationUpToDate) {
        this.locationUpToDate = locationUpToDate;
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

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    @Transient
    public String getDisplayAddress() {
        //todo check all usages and investigate if needed to switch to ', ' separator
        return getDisplayAddress(" ");
    }
}
