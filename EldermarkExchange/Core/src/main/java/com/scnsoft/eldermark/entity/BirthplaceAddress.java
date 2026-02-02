package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table
public class BirthplaceAddress implements Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(length = 15, name = "use_code")
    private String postalAddressUse;

    @Column(length = 255, name = "street_address")
    private String streetAddress;

    @Column(length = 128, name = "city")
    private String city;

    @Column(length = 100, name = "state")
    private String state;

    @Column(length = 50, name = "postal_code")
    private String postalCode;

    @Column(length = 100, name = "country")
    private String country;

    @OneToOne
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BirthplaceAddress that = (BirthplaceAddress) o;
        return Objects.equals(getPostalAddressUse(), that.getPostalAddressUse()) &&
                Objects.equals(getStreetAddress(), that.getStreetAddress()) &&
                Objects.equals(getCity(), that.getCity()) &&
                Objects.equals(getState(), that.getState()) &&
                Objects.equals(getPostalCode(), that.getPostalCode()) &&
                Objects.equals(getCountry(), that.getCountry()) &&
                Objects.equals(getResident(), that.getResident());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPostalAddressUse(), getStreetAddress(), getCity(), getState(), getPostalCode(), getCountry(), getResident());
    }


}
