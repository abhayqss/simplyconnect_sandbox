package com.scnsoft.eldermark.api.external.entity;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.phr.MobileUser;
import com.scnsoft.eldermark.api.shared.converter.RegistrationApplicationTypeConverter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author phomal
 * Created on 10/17/2017.
 */
@Entity
@Table(name = "UserMobileRegistrationApplication")
public class RegistrationApplication {

    @Id
    @GenericGenerator(name = "generator", strategy = "uuid2")
    @GeneratedValue(generator = "generator")
    @Column(name = "flow_id", columnDefinition="uniqueidentifier", nullable = false, unique = true)
    private String flowId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private MobileUser user;
    @ManyToOne
    @JoinColumn(name = "user_app_id")
    private ThirdPartyApplication thirdPartyApplication;

    @Column(name = "resident_id")
    private Long residentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", insertable = false, updatable = false)
    private Client resident;
    @Column(name = "employee_id")
    private Long employeeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;
    @Column(name = "employee_password")
    private String employeePassword;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "physician_id")
    private Physician physician;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "ssn", length = 9, columnDefinition = "char")
    private String ssn;
    @Column(name = "phone", length = 50, nullable = false)
    private String phone;
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by_user_id")
    private MobileUser inviter;

    @Column(name = "successful_signup_time")
    private Date lastSignupTime;
    @Column(name = "signup_start_time")
    private Date currentSignupTime;

    /**
     * The time difference between UTC time and local time, in minutes
     */
    @Column(name = "timezone_offset")
    private Integer timeZoneOffset;
    @Column(name = "phone_confirmation_code")
    private String phoneConfirmationCode;
    @Column(name = "phone_confirmation_attempt_cnt", nullable = false)
    private Integer phoneConfirmationAttemptCount;
    @Column(name = "signup_attempt_cnt", nullable = false)
    private Integer signupAttemptCount;
    @Column(name = "confirmation_code_issued_at")
    private Date confirmationCodeIssuedAt;

    @ManyToOne
    @JoinColumn(name = "registration_step", nullable = false, columnDefinition = "int")
    private RegistrationStep registrationStep;
    @Column(name = "registration_type", length = 8, columnDefinition = "char")
    @Convert(converter = RegistrationApplicationTypeConverter.class)
    private Type registrationType;

    @Column(name = "email_normalized", updatable = false, insertable = false)
    private String emailNormalized;
    @Column(name = "phone_normalized", updatable = false, insertable = false)
    private String phoneNormalized;

    // It may be a first name entered by the user during registration as well as a first name entered by inviter in PHR mobile app
    @Column(name = "first_name")
    private String firstName;

    // It may be a last name entered by the user during registration as well as a last name entered by inviter in PHR mobile app
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "app_description")
    private String appDescription;

    /**
     * MobileUser registration Type
     */
    public enum Type {
        SIGNUP_AS_CONSUMER("CONSUMER"),
        SIGNUP_AS_PROVIDER("PROVIDER"),
        I_HAVE_ACCOUNT("WEB ACNT"),
        APPLICATION("API USER");

        private final String valueDb;

        Type(final String valueDb) {
            this.valueDb = valueDb;
        }

        public String getValueDb() {
            return valueDb;
        }

        public static Type fromValue(String text) {
            for (Type t : Type.values()) {
                if (String.valueOf(t.valueDb).equals(text)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown RegistrationApplication.Type (value = " + text + ")");
        }

    }

    /**
     * User registration Step
     */
    public enum Step {
        INVITED("INVITED"),
        SIGN_UP("SIGN UP"),
        CONFIRMATION("CONFIRMATION"),
        WEB_ACCESS("WEB ACCESS"),
        COMPLETION("COMPLETION"),
        COMPLETED("COMPLETED");

        private final String nameDb;

        Step(final String valueDb) {
            this.nameDb = valueDb;
        }

        public String getNameDb() {
            return nameDb;
        }

        public static Type fromValue(String text) {
            for (Type t : Type.values()) {
                if (String.valueOf(t.valueDb).equals(text)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unknown RegistrationApplication.Step (value = " + text + ")");
        }

    }


    public MobileUser getUser() {
        return user;
    }

    public void setUser(MobileUser user) {
        this.user = user;
    }

    public ThirdPartyApplication getThirdPartyApplication() {
        return thirdPartyApplication;
    }

    public void setThirdPartyApplication(ThirdPartyApplication thirdPartyApplication) {
        this.thirdPartyApplication = thirdPartyApplication;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Client getResident() {
        return resident;
    }

    public void setResident(Client resident) {
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

    public Physician getPhysician() {
        return physician;
    }

    public void setPhysician(Physician physician) {
        this.physician = physician;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
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

    public MobileUser getInviter() {
        return inviter;
    }

    public void setInviter(MobileUser inviter) {
        this.inviter = inviter;
    }

    public Date getLastSignupTime() {
        return lastSignupTime;
    }

    public void setLastSignupTime(Date lastSignupTime) {
        this.lastSignupTime = lastSignupTime;
    }

    public Date getCurrentSignupTime() {
        return currentSignupTime;
    }

    public void setCurrentSignupTime(Date currentSignupTime) {
        this.currentSignupTime = currentSignupTime;
    }

    public Integer getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(Integer timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }

    public String getPhoneConfirmationCode() {
        return phoneConfirmationCode;
    }

    public void setPhoneConfirmationCode(String phoneConfirmationCode) {
        this.phoneConfirmationCode = phoneConfirmationCode;
    }

    public Integer getPhoneConfirmationAttemptCount() {
        return phoneConfirmationAttemptCount;
    }

    public void setPhoneConfirmationAttemptCount(Integer phoneConfirmationAttemptCount) {
        this.phoneConfirmationAttemptCount = phoneConfirmationAttemptCount;
    }

    public Integer getSignupAttemptCount() {
        return signupAttemptCount;
    }

    public void setSignupAttemptCount(Integer signupAttemptCount) {
        this.signupAttemptCount = signupAttemptCount;
    }

    public Date getConfirmationCodeIssuedAt() {
        return confirmationCodeIssuedAt;
    }

    public void setConfirmationCodeIssuedAt(Date confirmationCodeIssuedAt) {
        this.confirmationCodeIssuedAt = confirmationCodeIssuedAt;
    }

    public RegistrationStep getRegistrationStep() {
        return registrationStep;
    }

    public void setRegistrationStep(RegistrationStep registrationStep) {
        this.registrationStep = registrationStep;
    }

    public Type getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(Type registrationType) {
        this.registrationType = registrationType;
    }

    public String getEmailNormalized() {
        return emailNormalized;
    }

    public void setEmailNormalized(String emailNormalized) {
        this.emailNormalized = emailNormalized;
    }

    public String getPhoneNormalized() {
        return phoneNormalized;
    }

    public void setPhoneNormalized(String phoneNormalized) {
        this.phoneNormalized = phoneNormalized;
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

    public String getAppDescription() {
        return appDescription;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getEmployeePassword() {
        return employeePassword;
    }

    public void setEmployeePassword(String employeePassword) {
        this.employeePassword = employeePassword;
    }

    public static final class Builder {
        private String flowId;
        private MobileUser user;
        private ThirdPartyApplication thirdPartyApplication;
        private Client resident;
        private Employee employee;
        private String employeePassword;
        private Physician physician;
        private Person person;
        private String ssn;
        private String phone;
        private String email;
        private MobileUser inviter;
        private Date lastSignupTime;
        private Date currentSignupTime;
        private Integer timeZoneOffset;
        private String phoneConfirmationCode;
        private Integer phoneConfirmationAttemptCount;
        private Integer signupAttemptCount;
        private Date confirmationCodeIssuedAt;
        private RegistrationStep registrationStep;
        private Type registrationType;
        private String emailNormalized;
        private String phoneNormalized;
        // It may be a first name entered by the user during registration as well as a first name entered by inviter in PHR mobile app
        private String firstName;
        // It may be a last name entered by the user during registration as well as a last name entered by inviter in PHR mobile app
        private String lastName;
        private String appDescription;

        private Builder() {
        }

        public static Builder aRegistrationApplication() {
            return new Builder();
        }

        public Builder withFlowId(String flowId) {
            this.flowId = flowId;
            return this;
        }

        public Builder withUser(MobileUser user) {
            this.user = user;
            return this;
        }

        public Builder withThirdPartyApplication(ThirdPartyApplication thirdPartyApplication) {
            this.thirdPartyApplication = thirdPartyApplication;
            return this;
        }

        public Builder withResident(Client resident) {
            this.resident = resident;
            return this;
        }

        public Builder withEmployee(Employee employee) {
            this.employee = employee;
            return this;
        }

        public Builder withEmployeePassword(String employeePassword) {
            this.employeePassword = employeePassword;
            return this;
        }

        public Builder withPhysician(Physician physician) {
            this.physician = physician;
            return this;
        }

        public Builder withPerson(Person person) {
            this.person = person;
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

        public Builder withInviter(MobileUser inviter) {
            this.inviter = inviter;
            return this;
        }

        public Builder withLastSignupTime(Date lastSignupTime) {
            this.lastSignupTime = lastSignupTime;
            return this;
        }

        public Builder withCurrentSignupTime(Date currentSignupTime) {
            this.currentSignupTime = currentSignupTime;
            return this;
        }

        public Builder withTimeZoneOffset(Integer timeZoneOffset) {
            this.timeZoneOffset = timeZoneOffset;
            return this;
        }

        public Builder withPhoneConfirmationCode(String phoneConfirmationCode) {
            this.phoneConfirmationCode = phoneConfirmationCode;
            return this;
        }

        public Builder withPhoneConfirmationAttemptCount(Integer phoneConfirmationAttemptCount) {
            this.phoneConfirmationAttemptCount = phoneConfirmationAttemptCount;
            return this;
        }

        public Builder withSignupAttemptCount(Integer signupAttemptCount) {
            this.signupAttemptCount = signupAttemptCount;
            return this;
        }

        public Builder withConfirmationCodeIssuedAt(Date confirmationCodeIssuedAt) {
            this.confirmationCodeIssuedAt = confirmationCodeIssuedAt;
            return this;
        }

        public Builder withRegistrationStep(RegistrationStep registrationStep) {
            this.registrationStep = registrationStep;
            return this;
        }

        public Builder withRegistrationType(Type registrationType) {
            this.registrationType = registrationType;
            return this;
        }

        public Builder withEmailNormalized(String emailNormalized) {
            this.emailNormalized = emailNormalized;
            return this;
        }

        public Builder withPhoneNormalized(String phoneNormalized) {
            this.phoneNormalized = phoneNormalized;
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

        public Builder withAppDescription(String appDescription) {
            this.appDescription = appDescription;
            return this;
        }

        public RegistrationApplication build() {
            RegistrationApplication registrationApplication = new RegistrationApplication();
            registrationApplication.setFlowId(flowId);
            registrationApplication.setUser(user);
            registrationApplication.setThirdPartyApplication(thirdPartyApplication);
            registrationApplication.setResident(resident);
            if (resident != null) {
                registrationApplication.setResidentId(resident.getId());
            }
            registrationApplication.setEmployee(employee);
            if (employee != null) {
                registrationApplication.setEmployeeId(employee.getId());
            }
            registrationApplication.setEmployeePassword(employeePassword);
            registrationApplication.setPhysician(physician);
            registrationApplication.setPerson(person);
            registrationApplication.setSsn(ssn);
            registrationApplication.setPhone(phone);
            registrationApplication.setEmail(email);
            registrationApplication.setInviter(inviter);
            registrationApplication.setLastSignupTime(lastSignupTime);
            registrationApplication.setCurrentSignupTime(currentSignupTime);
            registrationApplication.setTimeZoneOffset(timeZoneOffset);
            registrationApplication.setPhoneConfirmationCode(phoneConfirmationCode);
            registrationApplication.setPhoneConfirmationAttemptCount(phoneConfirmationAttemptCount);
            registrationApplication.setSignupAttemptCount(signupAttemptCount);
            registrationApplication.setConfirmationCodeIssuedAt(confirmationCodeIssuedAt);
            registrationApplication.setRegistrationStep(registrationStep);
            registrationApplication.setRegistrationType(registrationType);
            registrationApplication.setEmailNormalized(emailNormalized);
            registrationApplication.setPhoneNormalized(phoneNormalized);
            registrationApplication.setFirstName(firstName);
            registrationApplication.setLastName(lastName);
            registrationApplication.setAppDescription(appDescription);
            return registrationApplication;
        }
    }
}
