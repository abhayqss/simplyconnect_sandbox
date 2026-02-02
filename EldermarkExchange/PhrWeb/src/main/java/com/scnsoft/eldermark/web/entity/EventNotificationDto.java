package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.dao.carecoordination.NotificationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;

/**
 * This DTO is intended to represent event notifications.
 */
@ApiModel(description = "This DTO is intended to represent event notifications.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-31T16:16:46.317+03:00")
public class EventNotificationDto {

    @JsonProperty("careTeamRole")
    private String careTeamRole = null;

    @JsonProperty("contactName")
    private String contactName = null;

    @JsonProperty("contactId")
    private Long contactId = null;

    @JsonProperty("editableContact")
    private Boolean editableContact = null;

    @JsonProperty("userId")
    private Long userId = null;

    @JsonProperty("dateTime")
    private Long dateTime = null;

    @JsonProperty("description")
    private String description = null;

    @JsonProperty("destination")
    private String destination = null;

    @JsonProperty("details")
    private String details = null;

    @JsonProperty("notificationType")
    private NotificationType notificationType = null;

    @JsonProperty("responsibility")
    private ResponsibilityEnum responsibility = null;

    @JsonProperty("dataSource")
    private DataSourceDto dataSource = null;


    @ApiModelProperty
    public String getCareTeamRole() {
        return careTeamRole;
    }

    public void setCareTeamRole(String careTeamRole) {
        this.careTeamRole = careTeamRole;
    }

    @ApiModelProperty(example = "Charles Xavier")
    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * Contact id
     * minimum: 1
     *
     * @return contactId
     */
    @ApiModelProperty(value = "Contact id")
    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    /**
     * true for care team members added by a patient him/herself (if current user is the patient), otherwise false
     *
     * @return editableContact
     */
    @ApiModelProperty(value = "true for care team members added by a patient him/herself (if current user is the patient), otherwise false")
    public Boolean getEditableContact() {
        return editableContact;
    }

    public void setEditableContact(Boolean editableContact) {
        this.editableContact = editableContact;
    }

    /**
     * Contact photo url. Photo url can be constructed like /phr/{userId}/profile/avatar
     *
     * @return photoUrl
     */
    @ApiModelProperty(value = "Contact photo url. Photo url can be constructed like /phr/{userId}/profile/avatar")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @ApiModelProperty(example = "1490952527600")
    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    @ApiModelProperty(example = "Affiliated CT Member, RBA/Altair")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ApiModelProperty(example = "rba.cxavier@direct.simplyhie.com")
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @ApiModelProperty(example = "A new event has been logged to the Simply Connect system and you are Informed for this type of event.")
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @ApiModelProperty
    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    @ApiModelProperty
    public ResponsibilityEnum getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(ResponsibilityEnum responsibility) {
        this.responsibility = responsibility;
    }

    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }

}

