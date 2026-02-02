package com.scnsoft.eldermark.consana.sync.client.model.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "resident")
@NamedEntityGraphs({
        @NamedEntityGraph(name = "databaseAndFacilityJoins", attributeNodes = {
                @NamedAttributeNode("database"),
                @NamedAttributeNode("facility"),
        })
})
public class Resident extends BaseReadOnlyEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "database_id", nullable = false, insertable = false, updatable = false)
    private Database database;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facility_id")
    private Organization facility;

    @Column(name = "consana_xref_id")
    private String consanaXrefId;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gender_id")
    private CcdCode gender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "marital_status_id")
    private CcdCode maritalStatus;

    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;

    public Resident() {
    }

    public Resident(Long id, String consanaXrefId) {
        super(id);
        this.consanaXrefId = consanaXrefId;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public Organization getFacility() {
        return facility;
    }

    public void setFacility(Organization facility) {
        this.facility = facility;
    }

    public String getConsanaXrefId() {
        return consanaXrefId;
    }

    public void setConsanaXrefId(String consanaXrefId) {
        this.consanaXrefId = consanaXrefId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public CcdCode getGender() {
        return gender;
    }

    public void setGender(CcdCode gender) {
        this.gender = gender;
    }

    public CcdCode getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(CcdCode maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
