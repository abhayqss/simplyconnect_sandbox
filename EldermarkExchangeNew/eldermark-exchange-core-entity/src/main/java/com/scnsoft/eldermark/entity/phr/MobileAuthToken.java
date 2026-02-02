package com.scnsoft.eldermark.entity.phr;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "AuthToken")
public class MobileAuthToken extends BasePhrExternalAuthToken {

    @ManyToOne(optional = true)
    @JoinColumn(name = "user_mobile_id", referencedColumnName = "id", nullable = true)
    private MobileUser mobileUser;

    public MobileUser getMobileUser() {
        return mobileUser;
    }

    public void setMobileUser(MobileUser mobileUser) {
        this.mobileUser = mobileUser;
    }
}
