package com.scnsoft.eldermark.api.external.entity;

import com.scnsoft.eldermark.api.shared.entity.BaseEntity;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.Set;

/**
 * Permissions (privileges) used in PHR Mobile app
 *
 * @author phomal
 * Created on 11/1/2017.
 */
@Immutable
@Entity
@Table(name = "Privilege")
public class Privilege extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    private Name name;

    public enum Name {
        /**
         * Add a new Family/Friend member to Resident's care team.
         */
        CARE_TEAM_LIST_INVITE_FRIEND,
        /**
         * Add a new Medical staff member to Resident's care team.
         */
        CARE_TEAM_LIST_INVITE_PHYSICIAN,
        /**
         * Remove a person from Resident's Care Team.
         */
        CARE_TEAM_DELETE,
        /**
         * SELF Remove from Resident's Care Team if associated with "Unaffiliated" organization.
         */
        CARE_TEAM_DELETE_SELF,
        /**
         * Mark a care team member as an emergency contact.
         */
        CARE_TEAM_EMERGENCY_WRITE,
        /**
         * Full access.
         */
        ADMINISTRATIVE,
        /**
         * Special access to Nucleus data.
         */
        SPECIAL_NUCLEUS,
        /**
         * Read organization info and contents (communities, residents, employees).
         * Equivalent to COMMUNITY_READ privilege for all existent and created communities of organization.
         */
        ORGANIZATION_READ,
        /**
         * Read community info and contents (residents, employees).
         */
        COMMUNITY_READ,
        /**
         * Add a new note
         */
        ADD_NOTE,
        /**
         * Access to view notes
         */
        VIEW_NOTE,
        /**
         * Access to editing note.
         */
        EDIT_NOTE,
        /**
         * Special Consana privilege.
         */
        SPECIAL_CONSANA
    }

    @OneToMany(mappedBy = "privilege", cascade = CascadeType.REMOVE)
    private Set<CareTeamRolePrivilege> roles;

    @OneToMany(mappedBy = "privilege", cascade = CascadeType.REMOVE)
    private Set<ThirdPartyApplicationPrivilege> applicationPrivileges;

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Set<CareTeamRolePrivilege> getRoles() {
        return roles;
    }

    public void setRoles(Set<CareTeamRolePrivilege> roles) {
        this.roles = roles;
    }

    public Set<ThirdPartyApplicationPrivilege> getApplicationPrivileges() {
        return applicationPrivileges;
    }

    public void setApplicationPrivileges(Set<ThirdPartyApplicationPrivilege> applicationPrivileges) {
        this.applicationPrivileges = applicationPrivileges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Privilege privilege = (Privilege) o;

        return new EqualsBuilder()
                .append(getName(), privilege.getName())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getName())
                .toHashCode();
    }

}
