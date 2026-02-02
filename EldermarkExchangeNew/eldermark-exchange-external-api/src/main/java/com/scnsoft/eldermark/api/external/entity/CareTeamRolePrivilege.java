package com.scnsoft.eldermark.api.external.entity;

import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Permissions matrix for PHR Mobile app (Care Team section)
 * @author phomal
 * Created on 11/20/2017.
 */
@Entity
@IdClass(CareTeamRolePrivilege.class)
@Table(name = "CareTeamRole_Privilege")
public class CareTeamRolePrivilege implements Serializable {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "privilege_id", referencedColumnName = "id", nullable = false)
    private Privilege privilege;

    /**
     * logged in user role
     */
    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "care_team_role_id", referencedColumnName = "id", nullable = false)
    private CareTeamRole role;

    /**
     * care team member role or null for any
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "care_team_member_role_id", referencedColumnName = "id")
    private CareTeamRole ctmRole;

    public Privilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }

    public CareTeamRole getRole() {
        return role;
    }

    public void setRole(CareTeamRole role) {
        this.role = role;
    }

    public CareTeamRole getCtmRole() {
        return ctmRole;
    }

    public void setCtmRole(CareTeamRole ctmRole) {
        this.ctmRole = ctmRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CareTeamRolePrivilege that = (CareTeamRolePrivilege) o;

        return new EqualsBuilder()
                .append(getPrivilege(), that.getPrivilege())
                .append(getRole(), that.getRole())
                .append(getCtmRole(), that.getCtmRole())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getPrivilege())
                .append(getRole())
                .append(getCtmRole())
                .toHashCode();
    }
}
