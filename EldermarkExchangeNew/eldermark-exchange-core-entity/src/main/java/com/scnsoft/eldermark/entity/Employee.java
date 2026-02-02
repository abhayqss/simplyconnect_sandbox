package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.beans.projection.AssociatedClientIdsAware;
import com.scnsoft.eldermark.beans.security.projection.entity.EmployeeSecurityAwareEntity;
import com.scnsoft.eldermark.entity.password.EmployeePasswordSecurity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@AttributeOverride(name = "legacyId", column = @Column(name = "legacy_id", nullable = false, length = 25))
public class Employee extends BaseEmployeeSecurityEntity implements EmployeeSecurityAwareEntity, AssociatedClientIdsAware, IdNamesBirthDateAware, EntityWithAvatar {

    private static final long serialVersionUID = 1L;

    @Column(name = "secure_email")
    private String secureMessaging;

    @Column(name = "secure_email_active", nullable = false)
    private Boolean isSecureMessagingActive;

    @Column(name = "secure_email_error")
    private String secureMessagingError;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "created_automatically")
    private Boolean createdAutomatically;

    @Column(name = "modified_timestamp")
    private Long modifiedTimestamp;

    @Column(name = "contact_4d", nullable = false)
    private Boolean contact4d;

    @Column(name = "ccn_company", length = 255)
    private String company;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "care_team_role_id", insertable = false, updatable = false)
    private Long careTeamRoleId;

    @OneToOne(mappedBy = "employee")
    private EmployeePasswordSecurity employeePasswordSecurity;

    @Column(name = "is_auto_status_changed", nullable = false)
    private boolean isAutoStatusChanged;

    @Column(name = "deactivate_datetime", nullable = false)
    private Instant deactivateDatetime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;

    @Column(name = "avatar_id", insertable = false, updatable = false)
    private Long avatarId;

    @Column(name = "qa_incident_reports")
    private Boolean qaIncidentReports;

    @Transient
    private MultipartFile multipartFile;

    @Transient
    private Boolean shouldRemoveAvatar;

    @Column(name = "labs_coordinator")
    private boolean labsCoordinator;

    @Column(name = "is_incident_report_reviewer")
    private boolean isIncidentReportReviewer;

    @Column(name = "is_community_address_used")
    private boolean isCommunityAddressUsed;

    @Column(name = "twilio_user_sid")
    private String twilioUserSid;

    @Column(name = "twilio_service_conversation_sid")
    private String twilioServiceConversationSid;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @JoinTable(name = "employee_associated_residents", joinColumns = {@JoinColumn(name = "employee_id")}, inverseJoinColumns = {@JoinColumn(name = "resident_id")})
    private List<Client> associatedClients;

    @ElementCollection
    @CollectionTable(name = "employee_associated_residents", joinColumns = @JoinColumn(name = "employee_id"))
    @Column(name = "resident_id", insertable = false, updatable = false)
    private Set<Long> associatedClientIds;

    @Column(name = "manual_activation_datetime")
    private Instant manualActivationDateTime;

    @ManyToMany
    @JoinTable(name = "Employee_FavouriteEmployee",
            joinColumns = @JoinColumn(name = "favourite_employee_id",
                    nullable = false,
                    insertable = false,
                    updatable = false),
            inverseJoinColumns = @JoinColumn(name = "employee_id",
                    nullable = false,
                    insertable = false,
                    updatable = false)
    )
    private List<Employee> addedAsFavouriteToEmployees;

    @ElementCollection
    @CollectionTable(name = "Employee_FavouriteEmployee", joinColumns = @JoinColumn(name = "favourite_employee_id", nullable = false))
    @Column(name = "employee_id", nullable = false)
    private Set<Long> addedAsFavouriteToEmployeeIds;

    @Transient
    private Boolean shouldRemovePrimaryContacts;

    public boolean isAutoStatusChanged() {
        return isAutoStatusChanged;
    }

    public void setAutoStatusChanged(boolean autoStatusChanged) {
        isAutoStatusChanged = autoStatusChanged;
    }

    public Instant getDeactivateDatetime() {
        return deactivateDatetime;
    }

    public void setDeactivateDatetime(Instant deactivateDatetime) {
        this.deactivateDatetime = deactivateDatetime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Long getCareTeamRoleId() {
        return careTeamRoleId;
    }

    public void setCareTeamRoleId(Long careTeamRoleId) {
        this.careTeamRoleId = careTeamRoleId;
    }

    public Boolean getCreatedAutomatically() {
        return createdAutomatically;
    }

    public void setCreatedAutomatically(Boolean createdAutomatically) {
        this.createdAutomatically = createdAutomatically;
    }

    public String getSecureMessaging() {
        return secureMessaging;
    }

    public void setSecureMessaging(String secureMessaging) {
        this.secureMessaging = secureMessaging;
    }

    public boolean isSecureMessagingActive() {
        return isSecureMessagingActive != null && isSecureMessagingActive;
    }

    public void setSecureMessagingActive(boolean secureMessagingActive) {
        isSecureMessagingActive = secureMessagingActive;
    }

    public String getSecureMessagingError() {
        return secureMessagingError;
    }

    public Long getModifiedTimestamp() {
        return modifiedTimestamp;
    }

    public void setModifiedTimestamp(Long modifiedTimestamp) {
        this.modifiedTimestamp = modifiedTimestamp;
    }

    public Boolean getContact4d() {
        return contact4d;
    }

    public void setContact4d(Boolean contact4d) {
        this.contact4d = contact4d;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public EmployeePasswordSecurity getEmployeePasswordSecurity() {
        return employeePasswordSecurity;
    }

    public void setEmployeePasswordSecurity(EmployeePasswordSecurity employeePasswordSecurity) {
        this.employeePasswordSecurity = employeePasswordSecurity;
    }

    public Boolean getIsSecureMessagingActive() {
        return isSecureMessagingActive;
    }

    public void setIsSecureMessagingActive(Boolean isSecureMessagingActive) {
        this.isSecureMessagingActive = isSecureMessagingActive;
    }

    public void setSecureMessagingError(String secureMessagingError) {
        this.secureMessagingError = secureMessagingError;
    }

    @Override
    public Avatar getAvatar() {
        return avatar;
    }

    @Override
    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    @Override
    public Long getAvatarId() {
        return avatarId;
    }

    @Override
    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public Boolean getQaIncidentReports() {
        return qaIncidentReports;
    }

    public void setQaIncidentReports(Boolean qaIncidentReports) {
        this.qaIncidentReports = qaIncidentReports;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public Boolean getShouldRemoveAvatar() {
        return shouldRemoveAvatar;
    }

    public void setShouldRemoveAvatar(Boolean shouldRemoveAvatar) {
        this.shouldRemoveAvatar = shouldRemoveAvatar;
    }

    public boolean getLabsCoordinator() {
        return labsCoordinator;
    }

    public void setLabsCoordinator(boolean labsCoordinator) {
        this.labsCoordinator = labsCoordinator;
    }


    public boolean isIncidentReportReviewer() {
        return isIncidentReportReviewer;
    }

    public void setIncidentReportReviewer(boolean incidentReportReviewer) {
        isIncidentReportReviewer = incidentReportReviewer;
    }

    public boolean getIsCommunityAddressUsed() {
        return isCommunityAddressUsed;
    }

    public void setIsCommunityAddressUsed(boolean isCommunityAddressUsed) {
        this.isCommunityAddressUsed = isCommunityAddressUsed;
    }

    public String getTwilioUserSid() {
        return twilioUserSid;
    }

    public Employee setTwilioUserSid(String twilioUserSid) {
        this.twilioUserSid = twilioUserSid;
        return this;
    }

    public String getTwilioServiceConversationSid() {
        return twilioServiceConversationSid;
    }

    public void setTwilioServiceConversationSid(String twilioServiceConversationSid) {
        this.twilioServiceConversationSid = twilioServiceConversationSid;
    }

    @Override
    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getFullName() {
        return Stream.of(getFirstName(), getLastName()).filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" "));
    }

    public List<Client> getAssociatedClients() {
        return associatedClients;
    }

    public void setAssociatedClients(List<Client> associatedClients) {
        this.associatedClients = associatedClients;
    }

    @Override
    public Set<Long> getAssociatedClientIds() {
        return associatedClientIds;
    }

    public void setAssociatedClientIds(Set<Long> associatedClientIds) {
        this.associatedClientIds = associatedClientIds;
    }

    public List<Employee> getAddedAsFavouriteToEmployees() {
        return addedAsFavouriteToEmployees;
    }

    public void setAddedAsFavouriteToEmployees(List<Employee> addedAsFavouriteToEmployees) {
        this.addedAsFavouriteToEmployees = addedAsFavouriteToEmployees;
    }

    public Set<Long> getAddedAsFavouriteToEmployeeIds() {
        return addedAsFavouriteToEmployeeIds;
    }

    public void setAddedAsFavouriteToEmployeeIds(Set<Long> addedAsFavouriteToEmployeeIds) {
        this.addedAsFavouriteToEmployeeIds = addedAsFavouriteToEmployeeIds;
    }

    public Instant getManualActivationDateTime() {
        return manualActivationDateTime;
    }

    public void setManualActivationDateTime(final Instant manualActivationDate) {
        this.manualActivationDateTime = manualActivationDate;
    }

    public Boolean getShouldRemovePrimaryContacts() {
        return shouldRemovePrimaryContacts;
    }

    public void setShouldRemovePrimaryContacts(Boolean shouldRemovePrimaryContacts) {
        this.shouldRemovePrimaryContacts = shouldRemovePrimaryContacts;
    }
}
