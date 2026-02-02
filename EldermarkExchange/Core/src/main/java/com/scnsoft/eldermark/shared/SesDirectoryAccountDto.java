package com.scnsoft.eldermark.shared;


import java.util.List;

public class SesDirectoryAccountDto {
    private String title;
    private String name;
    private String email;
    private String speciality;
    private List<String> stateLicences;
    private String registrationType;
    private List<String> npiNumbers;

    public int getId() {
        return this.hashCode();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public List<String> getStateLicences() {
        return stateLicences;
    }

    public void setStateLicences(List<String> stateLicences) {
        this.stateLicences = stateLicences;
    }

    public String getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(String registrationType) {
        this.registrationType = registrationType;
    }

    public List<String> getNpiNumbers() {
        return npiNumbers;
    }

    public void setNpiNumbers(List<String> npiNumbers) {
        this.npiNumbers = npiNumbers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SesDirectoryAccountDto that = (SesDirectoryAccountDto) o;

        if (getTitle() != null ? !getTitle().equals(that.getTitle()) : that.getTitle() != null) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getEmail() != null ? !getEmail().equals(that.getEmail()) : that.getEmail() != null) return false;
        if (getSpeciality() != null ? !getSpeciality().equals(that.getSpeciality()) : that.getSpeciality() != null)
            return false;
        if (getStateLicences() != null ? !getStateLicences().equals(that.getStateLicences()) : that.getStateLicences() != null)
            return false;
        if (getRegistrationType() != null ? !getRegistrationType().equals(that.getRegistrationType()) : that.getRegistrationType() != null)
            return false;
        return getNpiNumbers() != null ? getNpiNumbers().equals(that.getNpiNumbers()) : that.getNpiNumbers() == null;
    }

    @Override
    public int hashCode() {
        int result = getTitle() != null ? getTitle().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (getSpeciality() != null ? getSpeciality().hashCode() : 0);
        result = 31 * result + (getStateLicences() != null ? getStateLicences().hashCode() : 0);
        result = 31 * result + (getRegistrationType() != null ? getRegistrationType().hashCode() : 0);
        result = 31 * result + (getNpiNumbers() != null ? getNpiNumbers().hashCode() : 0);
        return result;
    }
}
