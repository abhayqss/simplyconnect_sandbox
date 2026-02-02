package com.scnsoft.eldermark.entity.phr;

import javax.persistence.*;
import java.util.Date;

/**
 * @author phomal
 * Created by phomal on 1/18/2018.
 */
@Entity
@Table(name = "UserThirdPartyApplication")
public class ThirdPartyApplication extends BaseEntity {

    @Column(name = "phone", length = 50)
    private String phone;
    @Column(name = "email")
    private String email;

    /**
     * The time difference between UTC time and local time, in minutes
     */
    @Column(name = "timezone_offset")
    private Integer timeZoneOffset;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
