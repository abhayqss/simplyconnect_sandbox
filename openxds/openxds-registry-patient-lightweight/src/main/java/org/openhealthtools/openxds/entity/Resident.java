package org.openhealthtools.openxds.entity;


import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@AttributeOverrides({
        @AttributeOverride(name = "legacyId", column = @Column(name = "legacy_id", nullable = false, length = 25)),
        @AttributeOverride(name = "legacyTable", column = @Column(name = "legacy_table", nullable = true, length = 100))
})
public class Resident extends StringLegacyTableAwareEntity implements Serializable {
    @OneToOne(fetch = FetchType.EAGER)
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "birth_date")
    private Date birthDate;

    @ManyToOne
    @JoinColumn
    private CcdCode gender;

    @ManyToOne
    @JoinColumn
    private CcdCode religion;

    @ManyToOne
    @JoinColumn
    private CcdCode race;

    @ManyToOne
    @JoinColumn(name = "ethnic_group_id")
    private CcdCode ethnicGroup;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "resident")
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private List<Language> languages = new ArrayList<Language>();

    @ManyToOne
    @JoinColumn(name = "marital_status_id")
    private CcdCode maritalStatus;

    @Column(name = "ssn", length = 11)
    private String socialSecurity;

    @Column(name = "ssn_last_four_digits", length = 4)
    private String ssnLastFourDigits;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "mother_person_id")
    private Person mother;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "mother_account_number")
    private PersonIdentifier mothersId;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "patient_account_number")
    private PersonIdentifier patientAccountNumber;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birth_place")
    private String birthPlace;

    @Column(name = "birth_order")
    private Integer birthOrder;

    @Column(name = "citizenship")
    private String citizenship;

    @Column(name = "death_date")
    private Date deathDate;

    @Column(name = "death_indicator")
    private Boolean deathIndicator;

    @Column(name = "veteran")
    private String veteran;

    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Organization facility;

    //  private DriversLicense driversLicense;
    //  private List<Visit> visits = new ArrayList();


    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public CcdCode getGender() {
        return gender;
    }

    public void setGender(CcdCode gender) {
        this.gender = gender;
    }

    public CcdCode getReligion() {
        return religion;
    }

    public void setReligion(CcdCode religion) {
        this.religion = religion;
    }

    public CcdCode getRace() {
        return race;
    }

    public void setRace(CcdCode race) {
        this.race = race;
    }

    public CcdCode getEthnicGroup() {
        return ethnicGroup;
    }

    public void setEthnicGroup(CcdCode ethnicGroup) {
        this.ethnicGroup = ethnicGroup;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public CcdCode getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(CcdCode maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getSocialSecurity() {
        return socialSecurity;
    }

    public void setSocialSecurity(String socialSecurity) {
        this.socialSecurity = socialSecurity;
    }

    public String getSsnLastFourDigits() {
        return ssnLastFourDigits;
    }

    public void setSsnLastFourDigits(String ssnLastFourDigits) {
        this.ssnLastFourDigits = ssnLastFourDigits;
    }

    public Person getMother() {
        return mother;
    }

    public void setMother(Person mother) {
        this.mother = mother;
    }

    public PersonIdentifier getMothersId() {
        return mothersId;
    }

    public void setMothersId(PersonIdentifier mothersId) {
        this.mothersId = mothersId;
    }

    public PersonIdentifier getPatientAccountNumber() {
        return patientAccountNumber;
    }

    public void setPatientAccountNumber(PersonIdentifier patientAccountNumber) {
        this.patientAccountNumber = patientAccountNumber;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public int getBirthOrder() {
        return birthOrder;
    }

    public void setBirthOrder(Integer birthOrder) {
        this.birthOrder = birthOrder;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public Date getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(Date deathDate) {
        this.deathDate = deathDate;
    }

    public Boolean isDeathIndicator() {
        return deathIndicator;
    }

    public void setDeathIndicator(Boolean deathIndicator) {
        this.deathIndicator = deathIndicator;
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

    public Organization getFacility() {
        return facility;
    }

    public void setFacility(Organization facility) {
        this.facility = facility;
    }

    public String getVeteran() {
        return veteran;
    }

    public void setVeteran(String veteran) {
        this.veteran = veteran;
    }
}
