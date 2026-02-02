package com.scnsoft.eldermark.entity.phr;

import javax.persistence.*;
import java.util.Date;

/**
 * @author phomal
 * Created by phomal on 1/18/2018.
 */
@Entity
@Table(name = "AuthToken")
public class AuthToken extends BaseEntity {

    @Column(name="token_encoded", unique = true, nullable = false)
    private String tokenEncoded;

    @ManyToOne(optional = true)
    @JoinColumn(name = "user_mobile_id", referencedColumnName = "id", nullable = true)
    private User userMobile;

    @ManyToOne(optional = true)
    @JoinColumn(name = "user_app_id", referencedColumnName = "id", nullable = true)
    private ThirdPartyApplication userApplication;

    @Column(name = "issued_at", nullable = false)
    private Date issuedAt;

    @Column(name = "expiration_time")
    private Date expirationTime;

    public String getTokenEncoded() {
        return tokenEncoded;
    }

    public void setTokenEncoded(String tokenEncoded) {
        this.tokenEncoded = tokenEncoded;
    }

    public User getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(User userMobile) {
        this.userMobile = userMobile;
    }

    public ThirdPartyApplication getUserApplication() {
        return userApplication;
    }

    public void setUserApplication(ThirdPartyApplication userApplication) {
        this.userApplication = userApplication;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }
}
