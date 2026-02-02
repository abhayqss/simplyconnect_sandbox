package com.scnsoft.eldermark.api.external.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.api.shared.entity.InvitationStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;

/**
 * This DTO is intended to represent a Care Team Member (CTM = Contact) list item.
 */
@ApiModel(description = "This DTO is intended to represent a Care Team Member (CTM = Contact) list item.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T14:21:48.776+03:00")
public class CareteamMemberBriefDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("employeeId")
    private Long employeeId = null;

    @JsonProperty("fullName")
    private String fullName = null;

    @JsonProperty("contactPhone")
    private String contactPhone = null;

    @JsonProperty("careTeamRole")
    private String careTeamRole = null;

    @JsonProperty("emergencyContact")
    private Boolean emergencyContact = null;

    @JsonProperty("invitationStatus")
    private InvitationStatus invitationStatus = null;


    /**
     * Contact id
     * minimum: 1
     *
     * @return id
     */
    @Min(1)
    @ApiModelProperty(value = "Contact id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * employee id
     * minimum: 1
     *
     * @return employeeId
     */
    @Min(1)
    @ApiModelProperty(value = "employee id")
    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * The text of the name
     *
     * @return fullName
     */
    @ApiModelProperty(example = "Lukas Smith", value = "The text of the name")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * phone number
     *
     * @return contactPhone
     */
    @ApiModelProperty(example = "+6458765432", value = "phone number")
    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }


    @ApiModelProperty(value = "")
    public String getCareTeamRole() {
        return careTeamRole;
    }

    public void setCareTeamRole(String careTeamRole) {
        this.careTeamRole = careTeamRole;
    }

    /**
     * Is this CTM set as \"Emergency contact\"?
     *
     * @return emergencyContact
     */
    @ApiModelProperty(value = "Is this CTM set as \"Emergency contact\"?")
    public Boolean EmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(Boolean emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    /**
     * Invitation status. Pending / Active status is used for Friend contacts, Pending / Active / Declined status is used for Medical Staff contacts.
     *
     * @return invitationStatus
     */
    @ApiModelProperty(value = "Invitation status. Pending / Active status is used for Friend contacts, Pending / Active / Declined status is used for Medical Staff contacts.")
    public InvitationStatus getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(InvitationStatus invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

}

