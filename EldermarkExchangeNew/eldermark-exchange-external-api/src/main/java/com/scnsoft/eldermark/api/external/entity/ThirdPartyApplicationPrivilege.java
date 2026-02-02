package com.scnsoft.eldermark.api.external.entity;

import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Permissions matrix for external API
 *
 * @author phomal
 * Created on 1/18/2018.
 */
@Entity
@IdClass(ThirdPartyApplicationPrivilege.class)
@Table(name = "UserThirdPartyApplication_Privilege")
public class ThirdPartyApplicationPrivilege implements Serializable {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "privilege_id", referencedColumnName = "id", nullable = false)
    private Privilege privilege;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_app_id", referencedColumnName = "id", nullable = false)
    private ThirdPartyApplication application;

    @Id
    @ManyToOne
    @JoinColumn(name = "database_id", referencedColumnName = "id")
    private Organization organization;

    @Id
    @ManyToOne
    @JoinColumn(name = "organization_id", referencedColumnName = "id")
    private Community community;

    public Privilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }

    public ThirdPartyApplication getApplication() {
        return application;
    }

    public void setApplication(ThirdPartyApplication application) {
        this.application = application;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization database) {
        this.organization = database;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community organization) {
        this.community = organization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ThirdPartyApplicationPrivilege that = (ThirdPartyApplicationPrivilege) o;

        return new EqualsBuilder()
                .append(getPrivilege(), that.getPrivilege())
                .append(getApplication(), that.getApplication())
                .append(getOrganization(), that.getOrganization())
                .append(getCommunity(), that.getCommunity())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getPrivilege())
                .append(getApplication())
                .append(getOrganization())
                .append(getCommunity())
                .toHashCode();
    }
}
