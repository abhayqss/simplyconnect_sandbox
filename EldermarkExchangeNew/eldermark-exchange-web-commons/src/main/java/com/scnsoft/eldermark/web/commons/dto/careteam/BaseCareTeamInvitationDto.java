package com.scnsoft.eldermark.web.commons.dto.careteam;

import com.scnsoft.eldermark.web.commons.validation.Age;
import com.scnsoft.eldermark.web.commons.validation.AgeConstraintValidator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;


public abstract class BaseCareTeamInvitationDto {

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotNull
    @Age(message = "The Care Team Member must be over 18 year old",
            value = 18,
            rules = {AgeConstraintValidator.Rule.GREATER_THAN, AgeConstraintValidator.Rule.EQUAL}
    )
    private LocalDate birthDate;

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
}
