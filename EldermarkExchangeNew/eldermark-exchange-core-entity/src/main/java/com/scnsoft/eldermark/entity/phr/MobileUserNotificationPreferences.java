package com.scnsoft.eldermark.entity.phr;

import com.scnsoft.eldermark.entity.NotificationPreferences;

import javax.persistence.*;

@Entity
@Table(name = "UserMobileNotificationPreferences")
@PrimaryKeyJoinColumn(name="id", referencedColumnName = "id")
public class MobileUserNotificationPreferences extends NotificationPreferences {

    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    @ManyToOne(optional = false)
    private MobileUser mobileUser;

    public MobileUser getMobileUser() {
        return mobileUser;
    }

    public void setMobileUser(MobileUser mobileUser) {
        this.mobileUser = mobileUser;
    }
}
