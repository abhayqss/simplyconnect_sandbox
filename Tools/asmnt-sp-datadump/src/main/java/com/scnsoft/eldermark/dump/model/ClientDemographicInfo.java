package com.scnsoft.eldermark.dump.model;

import java.time.Instant;
import java.time.LocalDate;

public class ClientDemographicInfo extends BaseClientInfo {

    private Instant intakeDate;
    private Instant admitDate;
    private LocalDate birthDate;
    private String gender;
    private AddressInfo address;
    private String race;

    public Instant getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(Instant intakeDate) {
        this.intakeDate = intakeDate;
    }

    public Instant getAdmitDate() {
        return admitDate;
    }

    public void setAdmitDate(Instant admitDate) {
        this.admitDate = admitDate;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public AddressInfo getAddress() {
        return address;
    }

    public void setAddress(AddressInfo address) {
        this.address = address;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }
}
