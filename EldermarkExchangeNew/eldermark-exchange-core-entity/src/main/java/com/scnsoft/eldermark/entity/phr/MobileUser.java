package com.scnsoft.eldermark.entity.phr;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "UserMobile")
public class MobileUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @OneToOne
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", insertable = false, updatable = false)
    private Client client;

    // It may be a first name entered by the user during registration as well as a first name entered by inviter in PHR mobile app
    @Column(name = "first_name")
    private String firstName;

    // It may be a last name entered by the user during registration as well as a last name entered by inviter in PHR mobile app
    @Column(name = "last_name")
    private String lastName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mobileUser")
    private List<MobileUserNotificationPreferences> mobileUserNotificationPreferencesList;

    @Column(name = "phone", nullable = false, length = 50)
    private String phone;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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

    public List<MobileUserNotificationPreferences> getMobileUserNotificationPreferencesList() {
        return mobileUserNotificationPreferencesList;
    }

    public void setMobileUserNotificationPreferencesList(List<MobileUserNotificationPreferences> mobileUserNotificationPreferencesList) {
        this.mobileUserNotificationPreferencesList = mobileUserNotificationPreferencesList;
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

    public String getClientFullNameLegacy() {
        String fullName = getClientFullName();
        if (fullName == null) {
            fullName = getFullName();
        }
        return fullName;
    }

    private String getClientFullName() {
        if (getClient() != null) {
            return getClient().getFullName();
        }
        return null;
    }

    public String getFullName() {
        if (StringUtils.isNotBlank(this.firstName) && StringUtils.isNotBlank(this.lastName)) {
            return this.firstName + " " + this.lastName;
        }
        return null;
    }

    public String getClientEmailLegacy() {
        if (getClient() != null) {
            return PersonTelecomUtils.findValue(getClient().getPerson(), PersonTelecomCode.EMAIL).orElse(null);
        }
        return getEmail();
    }

    public String getClientPhoneLegacy() {
        if (getClient() != null) {
            return PersonTelecomUtils.findValue(getClient().getPerson(), PersonTelecomCode.MC)
                    .orElse(PersonTelecomUtils.findValue(getClient().getPerson(), PersonTelecomCode.WP)
                            .orElse(PersonTelecomUtils.findValue(getClient().getPerson(), PersonTelecomCode.HP).orElse(null)));
        }
        return getPhone();
    }
}
