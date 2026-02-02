package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.shared.palatiumcare.location.NotifyLocationDto;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.CareTeamRelation;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.shared.Gender;
import com.scnsoft.eldermark.shared.ccd.PersonDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Map;

/**
 * This DTO is intended to represent a Care Receiver.<br>[NOTE] Do we need `dataSource` attribute here?
 */
@ApiModel(description = "This DTO is intended to represent a Care Receiver.<br>[NOTE] Do we need `dataSource` attribute here?")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-11-01T14:04:43.919+03:00")
public class CareReceiverDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("userId")
    private Long userId = null;

    @JsonProperty("person")
    private PersonDto person = null;


    /**
     * This attribute indicates the nature of the relationship between a patient and a CTM
     */
    @JsonProperty("relation")
    private CareTeamRelation.Relation relation = null;

    @JsonProperty("careTeamRole")
    private CareTeamRoleCode careTeamRole = null;

    @JsonProperty("photoUrl")
    private String photoUrl = null;

    @JsonProperty("accessRights")
    private Map<AccessRight.Code, Boolean> accessRights = null;

    @JsonProperty("canInviteFriend")
    private Boolean canInviteFriend = null;

    @JsonProperty("gender")
    private Gender gender = null;

    @JsonProperty("community")
    private String community = null;

    @JsonProperty("communityId")
    private Long communityId = null;

    @JsonProperty("contactPhone")
    private String contactPhone = null;

    @JsonProperty("contactEmail")
    private String contactEmail = null;

    @JsonProperty("age")
    private Integer age = null;

    @JsonProperty("ssnLastFourDigits")
    private String ssnLastFourDigits = null;

    @JsonProperty("dataSource")
    private DataSourceDto dataSource = null;


    @JsonProperty("location")
    private NotifyLocationDto locationDto = null;
    
    @JsonProperty("chatUserId")
    private Long chatUserId = null;
    
    @JsonProperty("chatThread")
    private String chatThread = null;


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
    @NotNull
    @Min(1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * User id. Not null.
     * minimum: 1
     *
     * @return userId
     */
    @ApiModelProperty(example = "42", value = "User id. Not null")
    @NotNull
    @Min(1)
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
    public CareTeamRoleCode getCareTeamRole() {
        return careTeamRole;
    }

    public void setCareTeamRole(CareTeamRoleCode careTeamRole) {
        this.careTeamRole = careTeamRole;
    }

    /**
     * url. Null if this care receiver doesn't have a photo
     *
     * @return photoUrl
     */
    @ApiModelProperty(example = "/phr/42/profile/avatar", value = "url. Null if this care receiver doesn't have a photo")
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @ApiModelProperty
    public Map<AccessRight.Code, Boolean> getAccessRights() {
        return accessRights;
    }

    public void setAccessRights(Map<AccessRight.Code, Boolean> accessRights) {
        this.accessRights = accessRights;
    }

    /**
     * Has the current user a possibility to invite a new member to the care receiver's (patient's) care team? This check is based only on System Role and doesn't take into account Access Rights managed by the patient him/herself.
     *
     * @return canInviteFriend
     */
    @ApiModelProperty(value = "Has the current user a possibility to invite a new member to the care receiver's (patient's) care team? This check is based only on System Role and doesn't take into account Access Rights managed by the patient him/herself.")
    public Boolean getCanInviteFriend() {
        return canInviteFriend;
    }

    public void setCanInviteFriend(Boolean canInviteFriend) {
        this.canInviteFriend = canInviteFriend;
    }

    @ApiModelProperty
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @ApiModelProperty(example = "Minnesota Assisted Living")
    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    @ApiModelProperty
    @Min(1)
    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    /**
     * primary phone
     *
     * @return contactPhone
     */
    @ApiModelProperty(example = "6458765432", value = "primary phone")
    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    /**
     * primary email
     *
     * @return contactEmail
     */
    @ApiModelProperty(example = "cpatnode@test.com", value = "primary email")
    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    /**
     * Age
     *
     * @return age
     */
    @ApiModelProperty(example = "65", value = "Age")
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * Last 4 digits of SSN
     *
     * @return ssnLastFourDigits
     */
    @ApiModelProperty(example = "5678", value = "Last 4 digits of SSN")
    @Pattern(regexp = "^\\d{4}$")
    public String getSsnLastFourDigits() {
        return ssnLastFourDigits;
    }

    public void setSsnLastFourDigits(String ssnLastFourDigits) {
        this.ssnLastFourDigits = ssnLastFourDigits;
    }

    @ApiModelProperty
    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }

    public NotifyLocationDto getLocationDto() {
        return locationDto;
    }

    public void setLocationDto(NotifyLocationDto locationDto) {
        this.locationDto = locationDto;
    }
}