package com.scnsoft.eldermark.entity.phr;

import com.scnsoft.eldermark.entity.Organization;

import javax.persistence.*;

/**
 * @author averazub
 * @author phomal
 * Created by averazub on 1/11/2017.
 */
@Entity
@Table(name = "UserResidentRecords")
public class UserResidentRecord extends BaseEntity {

    @Column(name="user_id")
    private Long userId;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true, updatable = false, insertable = false)
    private User user;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_application_flow_id", nullable = true)
    private RegistrationApplication userRegistrationApplication;

    /**
     * Resident ID
     */
    @Column(name="resident_id")
    private Long residentId;

    /**
     * Resident facility ID
     */
    @Column(name="provider_id")
    private Long providerId;

    @Column(name="is_current")
    private Boolean current;

    @Column(name="found_by_matching", nullable = false)
    private Boolean foundByMatching;

    /**
     * Resident facility name
     */
    @Column(name="provider_name")
    private String providerName;

    /**
     * Resident facility
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", insertable = false, updatable = false)
    private Organization organization;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RegistrationApplication getUserRegistrationApplication() {
        return userRegistrationApplication;
    }

    public void setUserRegistrationApplication(RegistrationApplication userRegistrationApplication) {
        this.userRegistrationApplication = userRegistrationApplication;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public Boolean getFoundByMatching() {
        return foundByMatching;
    }

    public void setFoundByMatching(Boolean foundByMatching) {
        this.foundByMatching = foundByMatching;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}

