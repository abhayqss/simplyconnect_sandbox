package com.scnsoft.eldermark.entity;

import javax.persistence.*;

/**
 * @author pzhurba
 */
@Entity
@Table(name = "OrganizationCareTeamMember")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
@NamedQueries({
        @NamedQuery(name = "organizationCareTeamMember.excludeInactive", query = "SELECT octm FROM OrganizationCareTeamMember octm " +
                "JOIN FETCH octm.careTeamRole INNER JOIN octm.employee e WHERE octm.organization.id = :organizationId " +
                "AND (e.status = 0 OR e.createdAutomatically = true)"),
        @NamedQuery(name = "organizationCareTeamMember.idsToDelete", query = "select octm.id from OrganizationCareTeamMember octm " +
                "where octm.organization.databaseId not in (select ao.primaryDatabaseId from AffiliatedOrganizations ao " +
                "where ao.primaryDatabaseId = octm.organization.databaseId and ao.affiliatedDatabaseId = octm.employee.databaseId) " +
                "and octm.employee.databaseId <> octm.organization.databaseId"),
        @NamedQuery(name = "organizationCareTeamMember.countOrganizationCareTeamMember", query = "select count(octm.id) " +
                "from OrganizationCareTeamMember octm where octm.organization.id = :organizationId and octm.employee.id in (:employeeIds)"),
        @NamedQuery(name = "organizationCareTeamMember.getCctOrganizationIdsForEmployee", query = "select octm.organization.id " +
                "from OrganizationCareTeamMember octm JOIN octm.organization o where o.id = octm.organization.id " +
                "AND octm.employee.id = :employeeId and o.database.id = :databaseId"),
        @NamedQuery(name = "organizationCareTeamMember.getCctOrganizationIdsForEmployees", query = "select octm.organization.id " +
                "from OrganizationCareTeamMember octm JOIN octm.organization o where o.id = octm.organization.id " +
                "AND octm.employee.id IN (:employeeIds) and o.database.id = :databaseId")
})
public class OrganizationCareTeamMember extends CareTeamMember {
    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "organization_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Organization organization;

    public OrganizationCareTeamMember() {
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

}
