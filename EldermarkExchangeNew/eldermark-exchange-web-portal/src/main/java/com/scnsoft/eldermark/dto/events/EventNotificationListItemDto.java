package com.scnsoft.eldermark.dto.events;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.Organization_;
import com.scnsoft.eldermark.entity.event.GroupedEventNotification_;

public class EventNotificationListItemDto {
    private Long contactAvatarId;
    private Long contactId;

    @DefaultSort
    @EntitySort.List(
            {
                    @EntitySort(joined = {GroupedEventNotification_.EMPLOYEE, Employee_.FIRST_NAME}),
                    @EntitySort(joined = {GroupedEventNotification_.EMPLOYEE, Employee_.LAST_NAME})
            }
    )
    private String contactFullName;
    private String contactFirstName;
    private String contactLastName;
    private String careTeamMemberRole;

    @EntitySort(GroupedEventNotification_.RESPONSIBILITY)
    private String responsibility;

    @EntitySort(joined = {GroupedEventNotification_.EMPLOYEE, Employee_.ORGANIZATION, Organization_.NAME})
    private String organization;
    private Long dateCreated;
    private String channels;
    private String hint;
    private String contactPhone;
    private String contactEmail;
    private boolean canViewContact;

    public Long getContactAvatarId() {
        return contactAvatarId;
    }

    public void setContactAvatarId(Long contactAvatarId) {
        this.contactAvatarId = contactAvatarId;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getContactFullName() {
        return contactFullName;
    }

    public void setContactFullName(String contactFullName) {
        this.contactFullName = contactFullName;
    }

    public String getContactFirstName() {
        return contactFirstName;
    }

    public void setContactFirstName(String contactFirstName) {
        this.contactFirstName = contactFirstName;
    }

    public String getContactLastName() {
        return contactLastName;
    }

    public void setContactLastName(String contactLastName) {
        this.contactLastName = contactLastName;
    }

    public String getCareTeamMemberRole() {
        return careTeamMemberRole;
    }

    public void setCareTeamMemberRole(String careTeamMemberRole) {
        this.careTeamMemberRole = careTeamMemberRole;
    }

    public String getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getChannels() {
        return channels;
    }

    public void setChannels(String channels) {
        this.channels = channels;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public boolean getCanViewContact() {
        return canViewContact;
    }

    public void setCanViewContact(boolean canViewContact) {
        this.canViewContact = canViewContact;
    }
}
