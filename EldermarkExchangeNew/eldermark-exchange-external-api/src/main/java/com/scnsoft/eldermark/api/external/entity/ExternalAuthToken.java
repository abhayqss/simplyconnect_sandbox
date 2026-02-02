package com.scnsoft.eldermark.api.external.entity;

import com.scnsoft.eldermark.entity.phr.BasePhrExternalAuthToken;

import javax.persistence.*;

@Entity
@Table(name = "AuthToken")
public class ExternalAuthToken extends BasePhrExternalAuthToken {

    @ManyToOne(optional = true)
    @JoinColumn(name = "user_app_id", referencedColumnName = "id", nullable = true)
    private ThirdPartyApplication userApplication;

    public ThirdPartyApplication getUserApplication() {
        return userApplication;
    }

    public void setUserApplication(ThirdPartyApplication userApplication) {
        this.userApplication = userApplication;
    }
}
