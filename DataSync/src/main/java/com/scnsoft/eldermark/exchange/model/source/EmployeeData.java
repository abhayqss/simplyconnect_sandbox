package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;

public class EmployeeData extends IdentifiableSourceEntity<String> {
    public static final String TABLE_NAME = "Employee";

    public static final String ID = "id";
    public static final String FIRST_NAME = "First_Name";
    public static final String LAST_NAME = "Last_Name";
    public static final String LOGIN_NAME = "Login_Name";
    public static final String INACTIVE = "Inactive";
    public static final String SEC_GROUP_IDS = "Security_group_IDs";
    public static final String EMAIL = "Email_Address";
    public static final String ADDRESS = "Address";
    public static final String CITY = "City";
    public static final String STATE = "State";
    public static final String ZIP = "Zip";
    public static final String HOME_PHONE = "home_phone";
    public static final String LASTMOD_STAMP = "lastmod_stamp";

    private String id;
    private String firstName;
    private String lastName;
    private String loginName;
    private String password;
    private boolean isInactive;
    private String secGroupIds;
    private String email;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String homePhone;
    private Long lastmodStamp;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null) {
            throw new NullPointerException("id cannot be null");
        }
        this.id = id;
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

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isInactive() {
        return isInactive;
    }

    public void setInactive(boolean inactive) {
        isInactive = inactive;
    }

    public String getSecGroupIds() {
        return secGroupIds;
    }

    public void setSecGroupIds(String secGroupIds) {
        this.secGroupIds = secGroupIds;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public Long getLastmodStamp() {
        return lastmodStamp;
    }

    public void setLastmodStamp(Long lastmodStamp) {
        this.lastmodStamp = lastmodStamp;
    }

    @Override
    public String toString() {
        return "EmployeeData{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", loginName='" + loginName + '\'' +
                ", password='" + password + '\'' +
                ", isInactive=" + isInactive +
                ", secGroupIds='" + secGroupIds + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                ", homePhone='" + homePhone + '\'' +
                '}';
    }
}
