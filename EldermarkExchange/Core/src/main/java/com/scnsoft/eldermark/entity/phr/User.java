package com.scnsoft.eldermark.entity.phr;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.password.UserPasswordSecurity;
import com.scnsoft.eldermark.services.PersonService;
import com.scnsoft.eldermark.shared.phr.utils.Normalizer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.annotation.Generated;
import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User is a record, that is created single time for any resident/contact/parent etc. and bound to all records, that user should have access to.
 * <br/>
 * User should not necessarily be employee of some organization, or patient of some community. User is anyone who USES the system.
 *
 * @author averazub
 * @author phomal
 *
 * Created by averazub on 12/27/2016.
 */
@Entity
@Table(name = "UserMobile")
public class User extends BaseEntity {

    @Column(name="token_encoded", unique = true)
    private String tokenEncoded;
    @Column(name="password_encoded")
    private String passwordEncoded;
    @Column(name="phr_patient")
    private Boolean phrPatient;

    @Column(name="resident_id")
    private Long residentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="resident_id", insertable = false, updatable = false)
    private Resident resident;
    @Column(name="employee_id")
    private Long employeeId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="employee_id", insertable = false, updatable = false)
    private Employee employee;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="database_id")
    private Database database;

    @Column(name="ssn", length = 9)
    private String ssn;
    @Column(name = "phone", nullable = false, length = 50)
    private String phone;
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by")
    private User inviter;

    /**
     * The time difference between UTC time and local time, in minutes
     */
    @Column(name="timezone_offset")
    private Integer timeZoneOffset;

    @Column(name="autocreated")
    private Boolean autocreated;

    @Column(name="email_normalized", updatable = false, insertable = false, unique = true)
    private String emailNormalized;
    @Column(name="phone_normalized", updatable = false, insertable = false)
    private String phoneNormalized;

    // It may be a first name entered by the user during registration as well as a first name entered by inviter in PHR mobile app
    @Column(name = "first_name")
    private String firstName;

    // It may be a last name entered by the user during registration as well as a last name entered by inviter in PHR mobile app
    @Column(name = "last_name")
    private String lastName;

    @OneToMany(mappedBy = "user")
    private Set<UserAccountType> accountTypes = new HashSet<UserAccountType>();

    @Column(name = "secondary_email")
    private String secondaryEmail;

    @Column(name = "secondary_phone")
    private String secondaryPhone;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    private UserPasswordSecurity userPasswordSecurity;

    /**
     * User Builder
     */
    @Generated(value = "Builder Generator", comments = "https://plugins.jetbrains.com/plugin/6585-builder-generator")
    public static final class Builder {
        private Long id;
        private String tokenEncoded;
        private String passwordEncoded;
        private Boolean phrPatient;
        private Resident resident;
        private Employee employee;
        private Database database;
        private String ssn;
        private String phone;
        private String email;
        private String firstName;
        private String lastName;
        private User inviter;
        private Integer timeZoneOffset;
        private Boolean autocreated;
        private Set<UserAccountType> accountTypes;
        private String secondaryEmail;
        private String secondaryPhone;

        private Builder() {
        }

        public static Builder anUser() {
            return new Builder();
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withTokenEncoded(String tokenEncoded) {
            this.tokenEncoded = tokenEncoded;
            return this;
        }

        public Builder withPasswordEncoded(String passwordEncoded) {
            this.passwordEncoded = passwordEncoded;
            return this;
        }

        public Builder withPhrPatient(Boolean phrPatient) {
            this.phrPatient = phrPatient;
            return this;
        }

        public Builder withResident(Resident resident) {
            this.resident = resident;
            return this;
        }

        public Builder withEmployee(Employee employee) {
            this.employee = employee;
            return this;
        }

        public Builder withDatabase(Database database) {
            this.database = database;
            return this;
        }

        public Builder withSsn(String ssn) {
            this.ssn = ssn;
            return this;
        }

        public Builder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withInviter(User inviter) {
            this.inviter = inviter;
            return this;
        }

        public Builder withTimeZoneOffset(Integer timeZoneOffset) {
            this.timeZoneOffset = timeZoneOffset;
            return this;
        }

        public Builder withAutocreated(Boolean autocreated) {
            this.autocreated = autocreated;
            return this;
        }

        public Builder withAccountTypes(Set<UserAccountType> accountTypes) {
            this.accountTypes = accountTypes;
            return this;
        }

        public Builder withSecondaryEmail(String secondaryEmail) {
            this.secondaryEmail = secondaryEmail;
            return this;
        }

        public Builder withSecondaryPhone(String secondaryPhone) {
            this.secondaryPhone = secondaryPhone;
            return this;
        }

        public User build() {
            User user = new User();
            user.setId(id);
            user.setTokenEncoded(tokenEncoded);
            user.setPasswordEncoded(passwordEncoded);
            user.setPhrPatient(phrPatient);
            user.setResident(resident);
            if (resident != null) {
                user.setResidentId(resident.getId());
                // populate missing fields from resident
                if (ssn == null) {
                    ssn = resident.getSocialSecurity();
                }
                if (phone == null) {
                    phone = PersonService.getPersonTelecomValue(resident.getPerson(), PersonTelecomCode.HP);
                }
                if (email == null) {
                    email = PersonService.getPersonTelecomValue(resident.getPerson(), PersonTelecomCode.EMAIL);
                }
                if (firstName == null) {
                    firstName = resident.getFirstName();
                }
                if (lastName == null) {
                    lastName = resident.getLastName();
                }
            }
            user.setEmployee(employee);
            if (employee != null) {
                user.setEmployeeId(employee.getId());
            }
            user.setSsn(ssn);
            user.setPhone(phone);
            user.setPhoneNormalized(Normalizer.normalizePhone(phone));
            user.setEmail(email);
            user.setEmailNormalized(Normalizer.normalizeEmail(email));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setInviter(inviter);
            user.setTimeZoneOffset(timeZoneOffset);
            user.setAutocreated(autocreated);
            user.setAccountTypes(accountTypes);
            user.setSecondaryEmail(secondaryEmail);
            user.setSecondaryPhone(secondaryPhone);
            return user;
        }
    }

    public String getTokenEncoded() {
        return tokenEncoded;
    }

    public void setTokenEncoded(String tokenEncoded) {
        this.tokenEncoded = tokenEncoded;
    }

    public String getPasswordEncoded() {
        return passwordEncoded;
    }

    public void setPasswordEncoded(String passwordEncoded) {
        this.passwordEncoded = passwordEncoded;
    }

    public Boolean getPhrPatient() {
        return phrPatient;
    }

    public void setPhrPatient(Boolean phrPatient) {
        this.phrPatient = phrPatient;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getSsnLastFourDigits() {
        return StringUtils.right(getSsn(), 4);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(Integer timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }

    public Boolean getAutocreated() {
        return autocreated;
    }

    public void setAutocreated(Boolean autocreated) {
        this.autocreated = autocreated;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<UserAccountType> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(Set<UserAccountType> accountTypes) {
        this.accountTypes = accountTypes;
    }

    public String getEmailNormalized() {
        return emailNormalized;
    }

    private void setEmailNormalized(String emailNormalized) {
        this.emailNormalized = emailNormalized;
    }

    public String getPhoneNormalized() {
        return phoneNormalized;
    }

    private void setPhoneNormalized(String phoneNormalized) {
        this.phoneNormalized = phoneNormalized;
    }

    public User getInviter() {
        return inviter;
    }

    public void setInviter(User inviter) {
        this.inviter = inviter;
    }

    public String getSecondaryEmail() {
        return secondaryEmail;
    }

    public void setSecondaryEmail(String secondaryEmail) {
        this.secondaryEmail = secondaryEmail;
    }

    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    public void setSecondaryPhone(String secondaryPhone) {
        this.secondaryPhone = secondaryPhone;
    }

    public UserPasswordSecurity getUserPasswordSecurity() {
        return userPasswordSecurity;
    }

    public void setUserPasswordSecurity(UserPasswordSecurity userPasswordSecurity) {
        this.userPasswordSecurity = userPasswordSecurity;
    }

// =============================================================================================

    public PersonAddress getPrimaryAddress() {
        if (getEmployeeId() != null) {
            final List<PersonAddress> addresses = getEmployee().getPerson().getAddresses();
            if (CollectionUtils.isEmpty(addresses)) {
                return null;
            } else {
                return addresses.get(0);
            }
        }
        return null;
    }

    public String getGender() {
        return (getResident() != null && getResident().getGender() != null) ? getResident().getGender().getDisplayName() : null;
    }

    public String getFullName() {
        if (StringUtils.isNotBlank(this.firstName) && StringUtils.isNotBlank(this.lastName)) {
            return this.firstName + " " + this.lastName;
        }
        return null;
    }

    public String getEmployeeFullName() {
        if (getEmployeeId() != null) {
            return getEmployee().getFullName();
        }
        return null;
    }

    private String getResidentFullName() {
        if (getResidentId() != null) {
            return getResident().getFullName();
        }
        return null;
    }

    public String getResidentFullNameLegacy() {
        String fullName = getResidentFullName();
        if (fullName == null) {
            fullName = getFullName();
        }
        return fullName;
    }

    public String getEmployeeEmail() {
        if (getEmployeeId() != null) {
            return PersonService.getPersonEmailValue(getEmployee().getPerson());
        }
        return null;
    }

    public String getEmployeePhone() {
        if (getEmployeeId() != null) {
            return PersonService.getPersonPhoneValue(getEmployee().getPerson());
        }
        return null;
    }

    public String getResidentPhoneLegacy() {
        if (getResidentId() != null) {
            return PersonService.getPersonPhoneValue(getResident().getPerson());
        }
        return getPhone();
    }

    public String getResidentEmailLegacy() {
        if (getResidentId() != null) {
            return PersonService.getPersonEmailValue(getResident().getPerson());
        }
        return getEmail();
    }

    public String getResidentFirstNameLegacy() {
        if (getResidentId() != null) {
            return getResident().getFirstName();
        }
        return getFirstName();
    }

    public String getResidentLastNameLegacy() {
        if (getResidentId() != null) {
            return getResident().getLastName();
        }
        return getLastName();
    }

    public String getEmployeeFirstName() {
        if (getEmployeeId() != null) {
            return getEmployee().getFirstName();
        }
        return null;
    }

    public String getEmployeeLastName() {
        if (getEmployeeId() != null) {
            return getEmployee().getLastName();
        }
        return null;
    }

}
