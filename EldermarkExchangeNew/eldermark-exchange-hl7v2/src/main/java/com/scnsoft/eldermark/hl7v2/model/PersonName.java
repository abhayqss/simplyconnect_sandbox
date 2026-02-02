package com.scnsoft.eldermark.hl7v2.model;

import java.util.Objects;

public class PersonName {
    private String lastName;
    private String firstName;
    private String secondName;
    private String suffix;
    private String prefix;
    private String degree;
    private String nameTypeCode;
    private String nameRepresentationCode;

    public PersonName() {
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return this.secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDegree() {
        return this.degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getNameTypeCode() {
        return this.nameTypeCode;
    }

    public void setNameTypeCode(String nameTypeCode) {
        this.nameTypeCode = nameTypeCode;
    }

    public String getNameRepresentationCode() {
        return this.nameRepresentationCode;
    }

    public void setNameRepresentationCode(String nameRepresentationCode) {
        this.nameRepresentationCode = nameRepresentationCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonName that = (PersonName) o;
        return Objects.equals(lastName, that.lastName) && Objects.equals(firstName, that.firstName) && Objects.equals(secondName, that.secondName) && Objects.equals(suffix, that.suffix) && Objects.equals(prefix, that.prefix) && Objects.equals(degree, that.degree) && Objects.equals(nameTypeCode, that.nameTypeCode) && Objects.equals(nameRepresentationCode, that.nameRepresentationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastName, firstName, secondName, suffix, prefix, degree, nameTypeCode, nameRepresentationCode);
    }

    @Override
    public String toString() {
        return "PersonName{" +
                "lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", suffix='" + suffix + '\'' +
                ", prefix='" + prefix + '\'' +
                ", degree='" + degree + '\'' +
                ", nameTypeCode='" + nameTypeCode + '\'' +
                ", nameRepresentationCode='" + nameRepresentationCode + '\'' +
                '}';
    }
}
