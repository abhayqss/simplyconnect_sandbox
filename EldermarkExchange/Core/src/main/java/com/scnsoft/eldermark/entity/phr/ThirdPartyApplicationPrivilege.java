package com.scnsoft.eldermark.entity.phr;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Organization;
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
    private Database database;

    @Id
    @ManyToOne
    @JoinColumn(name = "organization_id", referencedColumnName = "id")
    private Organization organization;

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

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ThirdPartyApplicationPrivilege that = (ThirdPartyApplicationPrivilege) o;

        return new EqualsBuilder()
                .append(getPrivilege(), that.getPrivilege())
                .append(getApplication(), that.getApplication())
                .append(getDatabase(), that.getDatabase())
                .append(getOrganization(), that.getOrganization())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getPrivilege())
                .append(getApplication())
                .append(getDatabase())
                .append(getOrganization())
                .toHashCode();
    }
}
