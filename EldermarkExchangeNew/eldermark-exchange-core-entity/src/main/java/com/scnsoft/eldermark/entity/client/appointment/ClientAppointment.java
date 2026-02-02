package com.scnsoft.eldermark.entity.client.appointment;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.basic.AuditableEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "ResidentAppointment")
public class ClientAppointment extends AuditableEntity {

    @Column(name = "title", nullable = false, length = 256)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_status", nullable = false, length = 50)
    private ClientAppointmentStatus status;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "location", nullable = false, length = 256)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", nullable = false, length = 50)
    private ClientAppointmentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_category", length = 50)
    private ClientAppointmentServiceCategory serviceCategory;

    @Column(name = "referral_source", length = 256)
    private String referralSource;

    @Column(name = "reason_for_visit", length = 5000)
    private String reasonForVisit;

    @Column(name = "directions_instructions", length = 5000)
    private String directionsInstructions;

    @Column(name = "notes", length = 5000)
    private String notes;

    @JoinColumn(name = "resident_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Client client;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long clientId;

    @JoinColumn(name = "creator_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Employee creator;

    @Column(name = "creator_id", nullable = false, insertable = false, updatable = false)
    private Long creatorId;

    @ManyToMany
    @JoinTable(name = "ResidentAppointment_ServiceProvider", joinColumns = @JoinColumn(name = "resident_appointment_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "employee_id", nullable = false))
    private List<Employee> serviceProviders;

    @ElementCollection
    @CollectionTable(name = "ResidentAppointment_ServiceProvider", joinColumns = @JoinColumn(name = "resident_appointment_id", nullable = false, insertable = false, updatable = false))
    @Column(name = "employee_id", nullable = false, insertable = false, updatable = false)
    private Set<Long> serviceProviderIds;

    @Column(name = "date_from", nullable = false)
    private Instant dateFrom;

    @Column(name = "date_to", nullable = false)
    private Instant dateTo;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "ResidentAppointment_Reminder", joinColumns = @JoinColumn(name = "resident_appointment_id", nullable = false))
    @Column(name = "reminder", nullable = false)
    private Set<ClientAppointmentReminder> reminders;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "ResidentAppointment_NotificationMethod", joinColumns = @JoinColumn(name = "resident_appointment_id", nullable = false))
    @Column(name = "notification_method", nullable = false)
    private Set<ClientAppointmentNotificationMethod> notificationMethods;

    @Column(name = "email", length = 256)
    private String email;

    @Column(name = "phone", length = 16)
    private String phone;

    @Column(name = "cancellation_reason", length = 5000)
    private String cancellationReason;

    @Column(name = "is_external_provider_service_provider")
    private Boolean isExternalProviderServiceProvider;

    @JoinColumn(name = "canceled_by_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Employee canceledBy;

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Employee getCreator() {
        return creator;
    }

    public void setCreator(Employee creator) {
        this.creator = creator;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public List<Employee> getServiceProviders() {
        return serviceProviders;
    }

    public void setServiceProviders(List<Employee> serviceProviders) {
        this.serviceProviders = serviceProviders;
    }

    public Set<Long> getServiceProviderIds() {
        return serviceProviderIds;
    }

    public void setServiceProviderIds(Set<Long> serviceProviderIds) {
        this.serviceProviderIds = serviceProviderIds;
    }

    public Instant getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Instant dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Instant getDateTo() {
        return dateTo;
    }

    public void setDateTo(Instant dateTo) {
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

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public Boolean getIsExternalProviderServiceProvider() {
        return isExternalProviderServiceProvider;
    }

    public void setIsExternalProviderServiceProvider(Boolean isExternalProviderServiceProvider) {
        this.isExternalProviderServiceProvider = isExternalProviderServiceProvider;
    }

    public Employee getCanceledBy() {
        return canceledBy;
    }

    public void setCanceledBy(Employee canceledBy) {
        this.canceledBy = canceledBy;
    }
}
