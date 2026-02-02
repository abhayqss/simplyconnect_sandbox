package com.scnsoft.eldermark.entity.phr;

import com.scnsoft.eldermark.entity.NotificationPreferences;

import javax.persistence.*;

/**
 * @author phomal
 */
@Entity
@Table(name = "UserMobileNotificationPreferences")
@PrimaryKeyJoinColumn(name="id", referencedColumnName = "id")
public class UserMobileNotificationPreferences extends NotificationPreferences {
    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false, insertable = false)
    @ManyToOne(optional = false)
    private User user;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public User getUser() {
        return user;
    }

    private void setUser(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
