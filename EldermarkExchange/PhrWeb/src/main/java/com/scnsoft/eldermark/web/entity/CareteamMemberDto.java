package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.entity.CareTeamRelation;
import com.scnsoft.eldermark.entity.CareTeamRelationship;
import com.scnsoft.eldermark.entity.InvitationStatus;
import com.scnsoft.eldermark.shared.ccd.PersonDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Generated;

/**
 * This DTO is intended to represent a Care Team Member (CTM = Contact).<br>[NOTE] The `physicianInfo` attribute is populated only when Care Team Member is of type \"MEDICAL_STAFF\".<br>[NOTE2] Remove unused `dataSource` attribute?
 */
@ApiModel(description = "This DTO is intended to represent a Care Team Member (CTM = Contact).<br>[NOTE] The `physicianInfo` attribute is populated only when Care Team Member is of type \"MEDICAL_STAFF\".")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-31T16:16:46.317+03:00")
public class CareteamMemberDto implements Comparable<CareteamMemberDto> {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("userId")
    private Long userId = null;

    @JsonProperty("person")
    private PersonDto person = null;

    @JsonProperty("relationship")
    private CareTeamRelationship.Relationship relationship = null;

    @JsonProperty("relation")
    private CareTeamRelation.Relation relation = null;

    @JsonProperty("careTeamRole")
    private String careTeamRole = null;

    @JsonProperty("editable")
    private Boolean editable = null;

    @JsonProperty("physicianInfo")
    private PhysicianDto physicianInfo = null;

    @JsonProperty("photoUrl")
    private String photoUrl = null;

    @JsonProperty("emergencyContact")
    private Boolean emergencyContact = null;

    @JsonProperty("invitationStatus")
    private InvitationStatus invitationStatus = null;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("contactStatus")
    private ContactStatus contactStatus = null;

    @JsonProperty("ssnLastFourDigits")
    private String ssnLastFourDigits = null;

    @JsonProperty("contactPhone")
    private String contactPhone = null;

    @JsonProperty("contactEmail")
    private String contactEmail = null;

    @JsonProperty("ssn")
    private String ssn = null;

    @JsonProperty("dataSource")
    private DataSourceDto dataSource = null;

    @JsonProperty("chatUserId")
    private Long chatUserId = null;
    
    @JsonProperty("chatThread")
    private String chatThread = null;

    @JsonProperty("includedInFaceSheet")
    private Boolean includedInFaceSheet;

    public Long getChatUserId() {
        return chatUserId;
    }

    public void setChatUserId(Long chatUserId) {
        this.chatUserId = chatUserId;
    }

    public String getChatThread() {
        return chatThread;
    }

    public void setChatThread(String chatThread) {
        this.chatThread = chatThread;
    }

    /**
     * Contact id
     *
     * @return id
     */
    @ApiModelProperty(value = "Contact id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * User id. May be null if this CTM has not completed registration in Mobile app.
     * minimum: 1
     *
     * @return userId
     */
    @ApiModelProperty(value = "User id. May be null if this CTM has not completed registration in Mobile app.")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @ApiModelProperty
    public PersonDto getPerson() {
        return person;
    }

    public void setPerson(PersonDto person) {
        this.person = person;
    }

    /**
     * Member role. MEDICAL_STAFF = Doctor, FRIEND_FAMILY = Relative = Friend or Family Member
     *
     * @return relationship
     */
    @ApiModelProperty(value = "Member role. MEDICAL_STAFF = Doctor, FRIEND_FAMILY = Relative = Friend or Family Member")
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

    @ApiModelProperty
    public String getCareTeamRole() {
        return careTeamRole;
    }

    public void setCareTeamRole(String careTeamRole) {
        this.careTeamRole = careTeamRole;
    }

    /**
     * Is it allowed to edit this CTM object? Usually it's `true` for CTMs with \"FRIEND_FAMILY\" role added by the current user.
     *
     * @return editable
     */
    @ApiModelProperty(value = "Is it allowed to edit this CTM object? Usually it's `true` for CTMs with \"FRIEND_FAMILY\" role added by the current user.")
    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    @ApiModelProperty
    public PhysicianDto getPhysicianInfo() {
        return physicianInfo;
    }

    public void setPhysicianInfo(PhysicianDto physicianInfo) {
        this.physicianInfo = physicianInfo;
    }

    /**
     * url. [NOTE] Do we need this attribute? Photo url can be constructed like /phr/{userId}/profile/avatar
     *
     * @return photoUrl
     */
    @ApiModelProperty(value = "url. [NOTE] Do we need this attribute? Photo url can be constructed like /phr/{userId}/profile/avatar")
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * Is this CTM set as \"Emergency contact\"?
     *
     * @return emergencyContact
     */
    @ApiModelProperty(value = "Is this CTM set as \"Emergency contact\"?")
    public Boolean getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(Boolean emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    /**
     * Invitation status
     *
     * @return invitationStatus
     */
    @ApiModelProperty(value = "Invitation status")
    public InvitationStatus getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(InvitationStatus invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    /**
     * This field is used to represent a contact status right after invitation. CREATED is returned for new contacts created during invitation (an invitation has been sent); EXISTING_ACTIVE is returned for existing active contacts (they don't need an invitation); EXISTING_PENDING is returned for existing pending contacts (an invitation has been sent before, but not accepted yet).
     *
     * @return contactStatus
     */
    @ApiModelProperty(value = "This field is used to represent a contact status right after invitation. CREATED is returned for new contacts created during invitation (an invitation has been sent); EXISTING_ACTIVE is returned for existing active contacts (they don't need an invitation); EXISTING_PENDING is returned for existing pending contacts (an invitation has been sent before, but not accepted yet).")
    public ContactStatus getContactStatus() {
        return contactStatus;
    }

    public void setContactStatus(ContactStatus contactStatus) {
        this.contactStatus = contactStatus;
    }

    /**
     * Last 4 digits of SSN
     *
     * @return ssnLastFourDigits
     */
    @ApiModelProperty(example = "5678", value = "Last 4 digits of SSN")
    public String getSsnLastFourDigits() {
        return ssnLastFourDigits;
    }

    public void setSsnLastFourDigits(String ssnLastFourDigits) {
        this.ssnLastFourDigits = ssnLastFourDigits;
    }

    @ApiModelProperty
    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    @ApiModelProperty
    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    /**
     * Social security number (SSN). This field is used in Contact Edit
     *
     * @return ssn
     */
    @ApiModelProperty(example = "123456789", value = "Social security number (SSN). This field is used in Contact Edit")
    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    @ApiModelProperty
    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }
    
    public Boolean getIncludedInFaceSheet() {
        return includedInFaceSheet;
    }

    public void setIncludedInFaceSheet(Boolean includedInFaceSheet) {
        this.includedInFaceSheet = includedInFaceSheet;
    }

    @Override
    public int compareTo(CareteamMemberDto o) {
        final String fullName = StringUtils.trim(this.getPerson().getNames().get(0).getFullName());
        final String fullName2 = StringUtils.trim(o.getPerson().getNames().get(0).getFullName());
        return ObjectUtils.compare(fullName, fullName2);
    }

}

