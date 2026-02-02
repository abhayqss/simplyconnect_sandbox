package org.openhealthtools.openxds.entity;

import javax.persistence.*;

@Entity
public class PersonAddress extends StringLegacyIdAwareEntity {
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
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(name = "legacy_table", nullable = false, length = 25)
    private String legacyTable;


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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getLegacyTable() {
        return legacyTable;
    }

    public void setLegacyTable(String legacyTable) {
        this.legacyTable = legacyTable;
    }
}
