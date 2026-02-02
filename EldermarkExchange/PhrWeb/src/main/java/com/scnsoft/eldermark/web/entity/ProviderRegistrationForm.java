package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * This DTO is intended to represent a data submitted by user who wants to become a provider (aka physician / doctor).
 */
@ApiModel(description = "This DTO is intended to represent a data submitted by user who wants to become a provider (aka physician / doctor).")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-05-26T15:53:21.084+03:00")
public class ProviderRegistrationForm {

    @JsonProperty("fax")
    private String fax = null;

    @JsonProperty("phone")
    private String phone = null;

    @JsonProperty("email")
    private String email = null;

    @JsonProperty("firstName")
    private String firstName = null;

    @JsonProperty("lastName")
    private String lastName = null;

    @JsonProperty("timeZoneOffset")
    private String timeZoneOffset = null;

    private AddressEditDto address = null;

    private ProfessionalProfileDto professional = null;

    @JsonIgnore
    private List<MultipartFile> files = null;

    /**
     * fax
     *
     * @return fax
     */
    @ApiModelProperty(value = "fax")
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * phone number
     *
     * @return phone
     */
    @ApiModelProperty(required = true, value = "phone number")
    @NotNull
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * email address
     *
     * @return email
     */
    @ApiModelProperty(required = true, value = "email address")
    @NotNull
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * first name
     *
     * @return firstName
     */
    @ApiModelProperty(example = "Gregory", required = true, value = "first name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * last name
     *
     * @return lastName
     */
    @ApiModelProperty(example = "House", required = true, value = "last name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Timezone Offset
     *
     * @return timeZoneOffset
     */
    @ApiModelProperty(required = true, value = "Timezone Offset")
    @NotNull
    public String getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(String timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }

    @ApiModelProperty(required = true, value = "Address")
    @NotNull
    public AddressEditDto getAddress() {
        return address;
    }

    public void setAddress(AddressEditDto address) {
        this.address = address;
    }

    @ApiModelProperty(required = true, value = "Professional profile")
    @NotNull
    public ProfessionalProfileDto getProfessional() {
        return professional;
    }

    public void setProfessional(ProfessionalProfileDto professional) {
        this.professional = professional;
    }

    public List<MultipartFile> getFiles() {
        return files;
    }

    public void setFiles(List<MultipartFile> files) {
        this.files = files;
    }

    /**
     * Provider registration form builder
     */
    public static final class Builder {
        private String fax = null;
        private String phone = null;
        private String email = null;
        private String firstName = null;
        private String lastName = null;
        private String timeZoneOffset = null;
        private AddressEditDto address = null;
        private ProfessionalProfileDto professional = null;
        private List<MultipartFile> files = null;

        private Builder() {
        }

        public static Builder aProviderRegistrationForm() {
            return new Builder();
        }

        public Builder withFax(String fax) {
            this.fax = fax;
            return this;
        }

        public Builder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withTimeZoneOffset(String timeZoneOffset) {
            this.timeZoneOffset = timeZoneOffset;
            return this;
        }

        public Builder withAddress(AddressEditDto address) {
            this.address = address;
            return this;
        }

        public Builder withProfessional(ProfessionalProfileDto professional) {
            this.professional = professional;
            return this;
        }

        public Builder withFiles(List<MultipartFile> files) {
            this.files = files;
            return this;
        }

        public ProviderRegistrationForm build() {
            ProviderRegistrationForm providerRegistrationForm = new ProviderRegistrationForm();
            providerRegistrationForm.setFax(fax);
            providerRegistrationForm.setPhone(phone);
            providerRegistrationForm.setEmail(email);
            providerRegistrationForm.setFirstName(firstName);
            providerRegistrationForm.setLastName(lastName);
            providerRegistrationForm.setTimeZoneOffset(timeZoneOffset);
            providerRegistrationForm.setAddress(address);
            providerRegistrationForm.setProfessional(professional);
            providerRegistrationForm.setFiles(files);
            return providerRegistrationForm;
        }
    }
}
