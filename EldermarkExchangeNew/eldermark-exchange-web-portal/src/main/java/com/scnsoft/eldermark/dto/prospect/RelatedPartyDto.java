package com.scnsoft.eldermark.dto.prospect;

import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.entity.prospect.RelatedPartyRelationship;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class RelatedPartyDto {

    @Size(max = 256)
    @NotEmpty
    private String firstName;

    @Size(max = 256)
    @NotEmpty
    private String lastName;

    private String fullName;

    @Size(max = 256)
    @Pattern(regexp = ValidationRegExpConstants.EMAIL_REGEXP)
    @NotEmpty
    private String email;

    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    @NotNull
    private String cellPhone;

    @Valid
    @NotNull
    private AddressDto address;

    @NotNull
    private RelatedPartyRelationship relationshipTypeName;

    private String relationshipTypeTitle;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public RelatedPartyRelationship getRelationshipTypeName() {
        return relationshipTypeName;
    }

    public void setRelationshipTypeName(RelatedPartyRelationship relationshipTypeName) {
        this.relationshipTypeName = relationshipTypeName;
    }

    public String getRelationshipTypeTitle() {
        return relationshipTypeTitle;
    }

    public void setRelationshipTypeTitle(String relationshipTypeTitle) {
        this.relationshipTypeTitle = relationshipTypeTitle;
    }
}
