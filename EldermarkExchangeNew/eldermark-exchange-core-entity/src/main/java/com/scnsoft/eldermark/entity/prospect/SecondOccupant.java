package com.scnsoft.eldermark.entity.prospect;

import com.scnsoft.eldermark.entity.Avatar;
import com.scnsoft.eldermark.entity.EntityWithAvatar;
import com.scnsoft.eldermark.entity.InNetworkInsurance;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "SecondOccupant")
public class SecondOccupant extends BasicEntity implements EntityWithAvatar {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @ManyToOne
    @JoinColumn(name = "in_network_insurance_id")
    private InNetworkInsurance inNetworkInsurance;

    @Column(name = "insurance_plan")
    private String insurancePlan;

    @ManyToOne
    @JoinColumn(name = "gender_id")
    private CcdCode gender;

    @Column(name = "ssn")
    private String socialSecurity;

    @ManyToOne
    @JoinColumn(name = "marital_status_id")
    private CcdCode maritalStatus;

    @ManyToOne
    @JoinColumn(name = "race_id")
    private CcdCode race;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "veteran")
    private Veteran veteran;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "person_id")
    private Person person;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;

    @Column(name = "avatar_id", insertable = false, updatable = false)
    private Long avatarId;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "secondOccupant")
    private Prospect prospect;

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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public InNetworkInsurance getInNetworkInsurance() {
        return inNetworkInsurance;
    }

    public void setInNetworkInsurance(InNetworkInsurance inNetworkInsurance) {
        this.inNetworkInsurance = inNetworkInsurance;
    }

    public String getInsurancePlan() {
        return insurancePlan;
    }

    public void setInsurancePlan(String insurancePlan) {
        this.insurancePlan = insurancePlan;
    }

    public CcdCode getGender() {
        return gender;
    }

    public void setGender(CcdCode gender) {
        this.gender = gender;
    }

    public String getSocialSecurity() {
        return socialSecurity;
    }

    public void setSocialSecurity(String socialSecurity) {
        this.socialSecurity = socialSecurity;
    }

    public CcdCode getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(CcdCode maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public CcdCode getRace() {
        return race;
    }

    public void setRace(CcdCode race) {
        this.race = race;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Veteran getVeteran() {
        return veteran;
    }

    public void setVeteran(Veteran veteran) {
        this.veteran = veteran;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public Avatar getAvatar() {
        return avatar;
    }

    @Override
    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    @Override
    public Long getAvatarId() {
        return avatarId;
    }

    @Override
    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public Prospect getProspect() {
        return prospect;
    }

    public void setProspect(Prospect prospect) {
        this.prospect = prospect;
    }
}
