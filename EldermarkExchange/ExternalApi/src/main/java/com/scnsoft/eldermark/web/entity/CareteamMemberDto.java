package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.entity.CareTeamRelation;
import com.scnsoft.eldermark.entity.CareTeamRelationship;
import com.scnsoft.eldermark.entity.InvitationStatus;
import com.scnsoft.eldermark.shared.ccd.PersonDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;

/**
 * This DTO is intended to represent a Care Team Member (CTM).<br>[NOTE] The `physicianId` property is null when Care Team Member is not of type \"MEDICAL_STAFF\".
 */
@ApiModel(description = "This DTO is intended to represent a Care Team Member (CTM).<br>[NOTE] The `physicianId` property is null when Care Team Member is not of type \"MEDICAL_STAFF\".")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T14:21:48.776+03:00")
public class CareteamMemberDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("employeeId")
    private Long employeeId = null;

    @JsonProperty("person")
    private PersonDto person = null;

    @JsonProperty("relationship")
    private CareTeamRelationship.Relationship relationship = null;

    @JsonProperty("relation")
    private CareTeamRelation.Relation relation = null;

    @JsonProperty("careTeamRole")
    private String careTeamRole = null;

    @JsonProperty("physicianId")
    private Long physicianId = null;

    @JsonProperty("emergencyContact")
    private Boolean emergencyContact = null;

    @JsonProperty("invitationStatus")
    private InvitationStatus invitationStatus = null;

    @JsonProperty("contactPhone")
    private String contactPhone = null;

    @JsonProperty("contactEmail")
    private String contactEmail = null;


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
     * Employee id
     * minimum: 1
     *
     * @return employeeId
     */
    @Min(1)
    @ApiModelProperty(value = "Employee id")
    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }


    @ApiModelProperty
    public PersonDto getPerson() {
        return person;
    }

    public void setPerson(PersonDto person) {
        this.person = person;
    }

    /**
     * Member role.<ul> <li>`MEDICAL_STAFF` = Doctor = Physician = \"Care provider\"</li> <li>`FRIEND_FAMILY` = Relative = \"Friend or Family Member\"</li></ul>
     *
     * @return relationship
     */
    @ApiModelProperty(value = "Member role.<ul> <li>`MEDICAL_STAFF` = Doctor = Physician = \"Care provider\"</li> <li>`FRIEND_FAMILY` = Relative = \"Friend or Family Member\"</li></ul>")
    public CareTeamRelationship.Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(CareTeamRelationship.Relationship relationship) {
        this.relationship = relationship;
    }

    /**
     * This attribute indicates the nature of the relationship between a patient and a CTM
     *
     * @return relation
     */
    @ApiModelProperty(value = "This attribute indicates the nature of the relationship between a patient and a CTM")
    public CareTeamRelation.Relation getRelation() {
        return relation;
    }

    public void setRelation(CareTeamRelation.Relation relation) {
        this.relation = relation;
    }


    @ApiModelProperty(example = "Parent/Guardian")
    public String getCareTeamRole() {
        return careTeamRole;
    }

    public void setCareTeamRole(String careTeamRole) {
        this.careTeamRole = careTeamRole;
    }

    /**
     * Physician id
     * minimum: 1
     *
     * @return physicianId
     */
    @Min(1)
    @ApiModelProperty(value = "Physician id")
    public Long getPhysicianId() {
        return physicianId;
    }

    public void setPhysicianId(Long physicianId) {
        this.physicianId = physicianId;
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

    /**
     * phone number
     *
     * @return contactPhone
     */
    @ApiModelProperty(example = "6458765432", value = "phone number")
    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }


    @ApiModelProperty(example = "cpatnode@test.com")
    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

}

