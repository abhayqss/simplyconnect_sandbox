package com.scnsoft.eldermark.dto.employee;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.State;

import java.time.LocalDate;

public class EmployeeUpdates {

    private Employee employee;

    private EmployeeStatus status;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    private byte[] avatarData;
    private String avatarMimeType;
    private boolean shouldDeleteAvatar;

    private String street;
    private String city;
    private State state;
    private String zipCode;
    private String cellPhone;
    private String homePhone;
    private String email;

    public EmployeeUpdates(Employee employee) {
        this.employee = employee;
    }

    public EmployeeUpdates(Employee employee, EmployeeStatus status) {
        this.employee = employee;
        this.status = status;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public byte[] getAvatarData() {
        return avatarData;
    }

    public void setAvatarData(byte[] avatarData) {
        this.avatarData = avatarData;
    }

    public String getAvatarMimeType() {
        return avatarMimeType;
    }

    public void setAvatarMimeType(String avatarMimeType) {
        this.avatarMimeType = avatarMimeType;
    }

    public boolean isShouldDeleteAvatar() {
        return shouldDeleteAvatar;
    }

    public void setShouldDeleteAvatar(boolean shouldDeleteAvatar) {
        this.shouldDeleteAvatar = shouldDeleteAvatar;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
