package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.password.EmployeePasswordSecurity;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"legacy_id", "database_id"})
}, indexes = {
        @Index(name = "IX_employee_database", columnList = "database_id")
})
/*
@Cacheable
@org.hibernate.annotations.Cache(usage= CacheConcurrencyStrategy.READ_WRITE, region="employee")
*/
@AttributeOverride(name = "legacyId", column = @Column(name = "legacy_id", nullable = false, length = 25))
public class Employee extends StringLegacyIdAwareEntity implements Serializable {

    @Column(name = "first_name", nullable = false, columnDefinition = "nvarchar(50)")
    private String firstName;

    @Column(name = "first_name_hash", nullable = false, updatable = false, insertable = false)
    private Long firstNameHash;

    @Column(name = "last_name", nullable = false, columnDefinition = "nvarchar(50)")
    private String lastName;

    @Column(name = "last_name_hash", nullable = false, updatable = false, insertable = false)
    private Long lastNameHash;

    @Column(name = "inactive", nullable = false)
    private EmployeeStatus status;

    @Column(name = "login", length = 255, nullable = false)
    private String loginName;

    @Column(name = "login_hash", nullable = false, updatable = false, insertable = false)
    private Long loginHash;

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

    @Column(name = "ccn_community_id")
    private Long communityId;

    @ManyToMany
    @JoinTable(name = "Employee_Groups",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups;

    @ManyToMany
    @JoinTable(name = "Employee_Role",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person person;

    @JoinColumn(name = "care_team_role_id", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private CareTeamRole careTeamRole;

    @OneToOne(mappedBy = "employee", fetch = FetchType.EAGER)
    private EmployeePasswordSecurity employeePasswordSecurity;
    
    @Column(name="qa_incident_reports")
    private Boolean qaIncidentReports;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getFirstNameHash() {
        return firstNameHash;
    }

    public void setFirstNameHash(Long firstNameHash) {
        this.firstNameHash = firstNameHash;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getLastNameHash() {
        return lastNameHash;
    }

    public void setLastNameHash(Long lastNameHash) {
        this.lastNameHash = lastNameHash;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Long getLoginHash() {
        return loginHash;
    }

    public void setLoginHash(Long loginHash) {
        this.loginHash = loginHash;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        String result = StringUtils.isNotEmpty(getFirstName()) ? getFirstName() : "";
        if (StringUtils.isNotEmpty(getLastName())) {
            result = StringUtils.isNotEmpty(result) ? result + " " + getLastName() : getLastName();
        }
        return result;
    }

    public String getMiddleName() {
        String middleName = null;
        for (Name name : person.getNames()) {
            if ("L".equals(name.getNameUse())) {
                middleName = name.getMiddle();
                break;
            }
        }
        return middleName;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public CareTeamRole getCareTeamRole() {
        return careTeamRole;
    }

    public void setCareTeamRole(CareTeamRole careTeamRole) {
        this.careTeamRole = careTeamRole;
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

    public void setSecureMessagingError(DirectErrorCode errorCode) {
        secureMessagingError = errorCode != null ? errorCode.name() : null;
    }

    public void setSecureMessagingError(DirectErrorCode errorCode, String info) {
        secureMessagingError = errorCode.name() + ": " + info;
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

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public EmployeePasswordSecurity getEmployeePasswordSecurity() {
        return employeePasswordSecurity;
    }

    public void setEmployeePasswordSecurity(EmployeePasswordSecurity employeePasswordSecurity) {
        this.employeePasswordSecurity = employeePasswordSecurity;
    }
    
    public Boolean getQaIncidentReports() {
        return qaIncidentReports;
    }

    public void setQaIncidentReports(Boolean qaIncidentReports) {
        this.qaIncidentReports = qaIncidentReports;
    }

    @PreUpdate
    @PrePersist
    protected void onUpdate() {
        this.modifiedTimestamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Employee{" +
                "legacyId='" + getLegacyId() + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", status=" + status +
                ", loginName='" + loginName + '\'' +
                ", careTeamRole=" + careTeamRole +
                '}';
    }
}
