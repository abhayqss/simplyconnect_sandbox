package com.scnsoft.eldermark.dto.client.appointment;

import com.scnsoft.eldermark.beans.security.projection.dto.ClientAppointmentSecurityFieldsAware;
import com.scnsoft.eldermark.entity.client.appointment.*;
import com.scnsoft.eldermark.validation.ValidationGroups;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

public class ClientAppointmentDto implements ClientAppointmentSecurityFieldsAware {

    @NotNull(groups = ValidationGroups.Update.class)
    private Long id;

    @NotEmpty
    @Size(max = 256)
    private String title;

    @NotNull
    private ClientAppointmentStatus status;
    private String statusTitle;
    private Boolean isPublic;

    @NotEmpty
    @Size(max = 256)
    private String location;

    @NotNull
    private ClientAppointmentType type;
    private String typeTitle;
    private ClientAppointmentServiceCategory serviceCategory;
    private String serviceCategoryTitle;

    @Size(max = 256)
    private String referralSource;

    @Size(max = 5000)
    private String reasonForVisit;

    @Size(max = 5000)
    private String directionsInstructions;

    @Size(max = 5000)
    private String notes;
    private Long clientId;
    private String clientName;
    private String clientDOB;
    private boolean canViewClient;
    private Long communityId;
    private String communityName;
    private Long organizationId;
    private String organizationName;
    private Long creatorId;
    private String creatorName;
    private Set<Long> serviceProviderIds;
    private List<String> serviceProviderNames;

    @NotNull
    private Long dateFrom;

    @NotNull
    private Long dateTo;
    private Set<ClientAppointmentReminder> reminders;
    private Set<String> reminderTitles;
    private Set<ClientAppointmentNotificationMethod> notificationMethods;
    private Set<String> notificationMethodTitles;
    private String email;
    private String phone;
    private boolean canEdit;
    private boolean canCancel;
    private boolean canDuplicate;
    private boolean canComplete;
    private Boolean isExternalProviderServiceProvider;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ClientAppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(ClientAppointmentStatus status) {
        this.status = status;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ClientAppointmentType getType() {
        return type;
    }

    public void setType(ClientAppointmentType type) {
        this.type = type;
    }

    public ClientAppointmentServiceCategory getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(ClientAppointmentServiceCategory serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public String getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(String referralSource) {
        this.referralSource = referralSource;
    }

    public String getReasonForVisit() {
        return reasonForVisit;
    }

    public void setReasonForVisit(String reasonForVisit) {
        this.reasonForVisit = reasonForVisit;
    }

    public String getDirectionsInstructions() {
        return directionsInstructions;
    }

    public void setDirectionsInstructions(String directionsInstructions) {
        this.directionsInstructions = directionsInstructions;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Set<Long> getServiceProviderIds() {
        return serviceProviderIds;
    }

    public void setServiceProviderIds(Set<Long> serviceProviderIds) {
        this.serviceProviderIds = serviceProviderIds;
    }

    public Long getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Long dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Long getDateTo() {
        return dateTo;
    }

    public void setDateTo(Long dateTo) {
        this.dateTo = dateTo;
    }

    public Set<ClientAppointmentReminder> getReminders() {
        return reminders;
    }

    public void setReminders(Set<ClientAppointmentReminder> reminders) {
        this.reminders = reminders;
    }

    public Set<ClientAppointmentNotificationMethod> getNotificationMethods() {
        return notificationMethods;
    }

    public void setNotificationMethods(Set<ClientAppointmentNotificationMethod> notificationMethods) {
        this.notificationMethods = notificationMethods;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public String getServiceCategoryTitle() {
        return serviceCategoryTitle;
    }

    public void setServiceCategoryTitle(String serviceCategoryTitle) {
        this.serviceCategoryTitle = serviceCategoryTitle;
    }

    public Set<String> getReminderTitles() {
        return reminderTitles;
    }

    public void setReminderTitles(Set<String> reminderTitles) {
        this.reminderTitles = reminderTitles;
    }

    public Set<String> getNotificationMethodTitles() {
        return notificationMethodTitles;
    }

    public void setNotificationMethodTitles(Set<String> notificationMethodTitles) {
        this.notificationMethodTitles = notificationMethodTitles;
    }

    public boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public boolean getCanCancel() {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public boolean getCanDuplicate() {
        return canDuplicate;
    }

    public void setCanDuplicate(boolean canDuplicate) {
        this.canDuplicate = canDuplicate;
    }

    public boolean getCanComplete() {
        return canComplete;
    }

    public void setCanComplete(boolean canComplete) {
        this.canComplete = canComplete;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public boolean getCanViewClient() {
        return canViewClient;
    }

    public void setCanViewClient(boolean canViewClient) {
        this.canViewClient = canViewClient;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public List<String> getServiceProviderNames() {
        return serviceProviderNames;
    }

    public void setServiceProviderNames(List<String> serviceProviderNames) {
        this.serviceProviderNames = serviceProviderNames;
    }

    public String getClientDOB() {
        return clientDOB;
    }

    public void setClientDOB(String clientDOB) {
        this.clientDOB = clientDOB;
    }

    public Boolean getIsExternalProviderServiceProvider() {
        return isExternalProviderServiceProvider;
    }

    public void setIsExternalProviderServiceProvider(Boolean isExternalProviderServiceProvider) {
        this.isExternalProviderServiceProvider = isExternalProviderServiceProvider;
    }
}
