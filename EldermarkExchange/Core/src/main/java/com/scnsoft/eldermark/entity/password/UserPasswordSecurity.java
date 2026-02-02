package com.scnsoft.eldermark.entity.password;

import com.scnsoft.eldermark.entity.phr.BaseEntity;
import com.scnsoft.eldermark.entity.phr.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "UserPasswordSecurity")
public class UserPasswordSecurity extends BaseEntity {

    @Column(name = "failed_logons")
    private Integer failedLogonsCount;

    @Column(name = "locked", nullable = false)
    private Boolean locked;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "locked_time")
    private Date lockedTime;

    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @OneToOne(optional = false)
    private User user;

    public Integer getFailedLogonsCount() {
        return failedLogonsCount;
    }

    public void setFailedLogonsCount(Integer failedLogonsCount) {
        this.failedLogonsCount = failedLogonsCount;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Date getLockedTime() {
        return lockedTime;
    }

    public void setLockedTime(Date lockedTime) {
        this.lockedTime = lockedTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
