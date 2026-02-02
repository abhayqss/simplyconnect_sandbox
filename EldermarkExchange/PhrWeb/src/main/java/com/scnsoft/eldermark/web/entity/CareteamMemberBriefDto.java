package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.entity.InvitationStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Generated;

/**
 * This DTO is intended to represent a Care Team Member (CTM = Contact) list item.
 */
@ApiModel(description = "This DTO is intended to represent a Care Team Member (CTM = Contact) list item.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-31T16:16:46.317+03:00")
public class CareteamMemberBriefDto implements Comparable<CareteamMemberBriefDto> {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("userId")
    private Long userId = null;

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

    @JsonProperty("dataSource")
    private DataSourceDto dataSource = null;
    
    @JsonProperty("chatUserId")
    private Long chatUserId = null;
    
    @JsonProperty("chatThread")
    private String chatThread = null;
        
    public Long getChatUserId() {
        return chatUserId;
    }


    public String getChatThread() {
        return chatThread;
    }


    public void setChatThread(String chatThread) {
        this.chatThread = chatThread;
    }


    public void setChatUserId(Long chatUserId) {
        this.chatUserId = chatUserId;
    }


    /**
     * Contact id
     * minimum: 1
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
     * User id
     * minimum: 1
     *
     * @return userId
     */
    @ApiModelProperty(value = "User id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    @ApiModelProperty
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

    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int compareTo(CareteamMemberBriefDto o) {
        return ObjectUtils.compare(this.fullName, o.fullName);
    }

}
