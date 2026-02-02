package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-03-28T19:32:17.348+03:00")
public class Profile {

    @JsonProperty("birthDate")
    private Long birthDate = null;

    @JsonProperty("firstName")
    private String firstName = null;

    @JsonProperty("gender")
    private String gender = null;

    @JsonProperty("lastName")
    private String lastName = null;


    @ApiModelProperty(example = "1336338000000")
    public Long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Long birthDate) {
        this.birthDate = birthDate;
    }

    @ApiModelProperty(example = "Donald")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @ApiModelProperty(example = "Duck")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public static final class Builder {
        private Long birthDate = null;
        private String firstName = null;
        private String gender = null;
        private String lastName = null;

        private Builder() {
        }

        public static Builder aProfile() {
            return new Builder();
        }

        public Builder withBirthDate(Long birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withGender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Profile build() {
            Profile profile = new Profile();
            profile.setBirthDate(birthDate);
            profile.setFirstName(firstName);
            profile.setGender(gender);
            profile.setLastName(lastName);
            return profile;
        }
    }
}
