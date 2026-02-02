package com.scnsoft.eldermark.consana.sync.client.model.entities;

import org.hibernate.annotations.Nationalized;

import javax.persistence.*;

@Entity
@Table(indexes = {@Index(name = "PersonId_Index", columnList = "person_id")})
@AttributeOverride(name = "legacyId", column = @Column(name = "legacy_id", nullable = false, length = 25))
public class PersonAddress extends StringLegacyTableAwareEntity {

    @Column(length = 15, name = "use_code")
    private String postalAddressUse;

    @Column(name = "street_address", columnDefinition = "nvarchar(255)")
    private String streetAddress;

    @Nationalized
    @Column(length = 128, name = "city")
    private String city;

    @Column(length = 100, name = "state")
    private String state;

    @Column(length = 50, name = "postal_code")
    private String postalCode;

    @Column(length = 100, name = "country")
    private String country;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
