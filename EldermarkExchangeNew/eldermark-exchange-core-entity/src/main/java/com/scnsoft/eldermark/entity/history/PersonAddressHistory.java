package com.scnsoft.eldermark.entity.history;

import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "PersonAddress_History")
public class PersonAddressHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    //updated on db level
    @Column(name = "updated_datetime", insertable = false, updatable = false)
    private Instant updatedDatetime;

    //updated on db level
    @Column(name = "deleted_datetime", insertable = false, updatable = false)
    private Instant deletedDatetime;

    @Column(name = "person_address_id")
    private Long personAddress;

    @Column(name = "use_code")
    private String postalAddressUse;

    @Column(name = "street_address", columnDefinition = "nvarchar(256)")
    @Nationalized
    private String streetAddress;

    @Column(name = "city", columnDefinition = "nvarchar(256)")
    @Nationalized
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country")
    private String country;

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "legacy_table", nullable = false)
    private String legacyTable;

    @Column(name = "database_id", nullable = false)
    private Long organizationId;

    @Column(name = "legacy_id", nullable = false)
    private String legacyId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getUpdatedDatetime() {
        return updatedDatetime;
    }

    public void setUpdatedDatetime(Instant updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
    }

    public Instant getDeletedDatetime() {
        return deletedDatetime;
    }

    public void setDeletedDatetime(Instant deletedDatetime) {
        this.deletedDatetime = deletedDatetime;
    }

    public Long getPersonAddress() {
        return personAddress;
    }

    public void setPersonAddress(Long personAddress) {
        this.personAddress = personAddress;
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

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getLegacyTable() {
        return legacyTable;
    }

    public void setLegacyTable(String legacyTable) {
        this.legacyTable = legacyTable;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }
}
