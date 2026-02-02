package com.scnsoft.eldermark.dto;

public class ClientNameBirthdayDto extends ClientNameDto {

    private String birthDate;

    public ClientNameBirthdayDto(Long id, String firstName, String lastName, String fullName, String birthDate) {
        super(id, firstName, lastName, fullName);
        this.birthDate = birthDate;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
}
