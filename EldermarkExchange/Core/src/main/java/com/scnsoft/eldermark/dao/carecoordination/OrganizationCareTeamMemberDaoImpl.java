package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDaoImpl;
import com.scnsoft.eldermark.entity.OrganizationCareTeamMember;
import com.scnsoft.eldermark.shared.carecoordination.SimpleDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author averazub
 * @author mradzivonenka
 * @author Netkachev
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 24-Sep-15.
 */
@Repository
public class OrganizationCareTeamMemberDaoImpl extends BaseDaoImpl<OrganizationCareTeamMember> implements OrganizationCareTeamMemberDao {
    public OrganizationCareTeamMemberDaoImpl() {
        super(OrganizationCareTeamMember.class);
    }

    @Override
    public List<Long> getCctOrganizationIdsForEmployee(Set<Long> employeeIds, Long databaseId) {
        final Query query = entityManager.createNamedQuery("organizationCareTeamMember.getCctOrganizationIdsForEmployees")
                .setParameter("employeeIds", employeeIds)
                .setParameter("databaseId",databaseId);
        List<Object> resultSet = query.getResultList();
        List<Long> results = new ArrayList<Long>();
        for (Object resultItem: resultSet) {
            results.add((Long) resultItem);
        }
        return results;
    }

    @Override
    public List<Long> getCctOrganizationIdsForEmployee(Long employeeId, Long databaseId) {
        final Query query = entityManager.createNamedQuery("organizationCareTeamMember.getCctOrganizationIdsForEmployee")
                .setParameter("employeeId", employeeId)
                .setParameter("databaseId",databaseId);
        List<Object> resultSet = query.getResultList();
        List<Long> results = new ArrayList<Long>();
        for (Object resultItem: resultSet) {
            results.add((Long) resultItem);
        }
        return results;
    }

    @Override
    public List<Long> getPatientOrganizationIdsForEmployee(Long employeeId) {
        final Query query = entityManager.createNamedQuery("resident.getPatientOrganizationIdsForEmployee")
                .setParameter("employeeId", employeeId);
        List<Object> resultSet = query.getResultList();
        List<Long> results = new ArrayList<Long>();
        for (Object resultItem: resultSet) {
            results.add((Long) resultItem);
        }
        return results;
    }



    @Override
    public List<OrganizationCareTeamMember> getOrganizationCareTeamMembers(final Long organizationId, Boolean affiliated, Long databaseId, final Pageable pageable) {
        final StringBuilder sb = new StringBuilder("Select o from OrganizationCareTeamMember o ");

        sb.append(" LEFT JOIN o.careTeamRole ");
        sb.append(" LEFT JOIN o.employee e");
        sb.append(" WHERE o.organization.id = :organizationId");
        if (affiliated!=null) {
            if (affiliated) {
                sb.append(" AND e.databaseId != :databaseId");
            } else {
                sb.append(" AND e.databaseId = :databaseId");
            }
        }
        if (pageable != null && pageable.getSort() != null) {
            if (pageable.getSort().getOrderFor("employee.label") != null) {
                sb.append(" ORDER BY o.employee.firstName ");
                sb.append(pageable.getSort().getOrderFor("employee.label").getDirection());
            }
            if (pageable.getSort().getOrderFor("role.label") != null) {
                sb.append(" ORDER BY o.careTeamRole.name ");
                sb.append(pageable.getSort().getOrderFor("role.label").getDirection());
            }
        }

        final TypedQuery<OrganizationCareTeamMember> query
                = entityManager.createQuery(sb.toString(), entityClass);
        query.setParameter("organizationId", organizationId);
        if (affiliated!=null) {
            query.setParameter("databaseId", databaseId);
        }
        applyPageable(query, pageable);
        return query.getResultList();
    }

    @Override
    public List<OrganizationCareTeamMember> getOrganizationCareTeamMembersExcludeInactive(Long organizationId) {
        final TypedQuery<OrganizationCareTeamMember> query = entityManager.createNamedQuery("organizationCareTeamMember.excludeInactive", entityClass);
        query.setParameter("organizationId", organizationId);
        return query.getResultList();
    }

    @Override
    public Long getOrganizationCareTeamMembersCount(Long organizationId) {
        final Query query
                = entityManager.createQuery("Select count (o) from OrganizationCareTeamMember o where o.organization.id = :organizationId");
        query.setParameter("organizationId", organizationId);
        return (Long) query.getSingleResult();
    }

    @Override
    public List<OrganizationCareTeamMember> getOrganizationCareTeamMembersByEmployeeAndRole(final Long organizationId, final Long employeeId, final Long roleId) {
        final StringBuffer sb = new StringBuffer("Select o from OrganizationCareTeamMember o ");
        sb.append(" LEFT JOIN o.careTeamRole ");
        sb.append(" LEFT JOIN o.employee ");
        sb.append(" LEFT JOIN o.organization ");

        sb.append(" WHERE o.employee.id = :employeeId ");
        sb.append(" AND o.careTeamRole.id = :roleId ");
        sb.append(" AND o.organization.id = :organizationId ");

        final TypedQuery<OrganizationCareTeamMember> query = entityManager.createQuery(sb.toString(), OrganizationCareTeamMember.class);

        query.setParameter("employeeId", employeeId);
        query.setParameter("roleId", roleId);
        query.setParameter("organizationId", organizationId);

        return query.getResultList();
    }

    @Override
    public List<Long> getCareTeamResidentIdsByEmployeeId(Set<Long> employeeIds, Long databaseId) {
        if (CollectionUtils.isEmpty(employeeIds)) {
            return Collections.emptyList();
        }

        final StringBuffer sb = new StringBuffer("Select r.id from Resident r ");
        sb.append(" JOIN r.facility org ");
        sb.append(" JOIN org.organizationCareTeamMembers o ");
        sb.append(" WHERE o.employee.id in (:employeeIds) ");
        sb.append(" AND r.databaseId=:databaseId ");

        final TypedQuery<Long> query = entityManager.createQuery(sb.toString(), Long.class);
        query.setParameter("employeeIds", employeeIds);
        query.setParameter("databaseId", databaseId);
        return query.getResultList();
    }
    
    @Override
    public List<OrganizationCareTeamMember> getCareTeamMembersByEmployeeId(final Long employeeId, final Long organizationId) {
        final StringBuffer sb = new StringBuffer("Select o from OrganizationCareTeamMember o ");
        sb.append(" LEFT JOIN o.employee ");
        sb.append(" LEFT JOIN o.organization ");

        sb.append(" WHERE o.employee.id = :employeeId ");
        sb.append(" AND o.organization.id = :organizationId ");

        final TypedQuery<OrganizationCareTeamMember> query = entityManager.createQuery(sb.toString(), OrganizationCareTeamMember.class);

        query.setParameter("employeeId", employeeId);
        query.setParameter("organizationId", organizationId);

        return query.getResultList();
    }

    @Override
    public void deleteByIdIn(List<Long> idsToDelete) {
        final Query query = entityManager.createQuery("DELETE FROM OrganizationCareTeamMember WHERE id IN (:idsToDelete)");
        query.setParameter("idsToDelete", idsToDelete);
        query.executeUpdate();
    }

    @Override
    public List<Long> getOrganizationCareTeamMemberIdsFromDeletedAffiliatedRelation() {
        final TypedQuery<Long> query = entityManager.createNamedQuery("organizationCareTeamMember.idsToDelete", Long.class);
        return query.getResultList();
    }


    @Override
    public boolean hasAffiliatedCareTeamMembers(Long organizationId, Long databaseId, Long affiliatedDatabaseId, boolean otherOrgs) {
        final StringBuilder sb = new StringBuilder("Select count(*) from OrganizationCareTeamMember o ");

        sb.append(" WHERE o.organization.databaseId = :databaseId");
        sb.append(" AND o.employee.databaseId = :affiliatedDatabaseId");
        if (otherOrgs) {
            sb.append(" AND o.organization.id != :organizationId");
        }
        else {
            sb.append(" AND o.organization.id = :organizationId");
        }

        final TypedQuery<Long> query
                = entityManager.createQuery(sb.toString(), Long.class);
        query.setParameter("databaseId", databaseId);
        query.setParameter("organizationId", organizationId);
        query.setParameter("affiliatedDatabaseId", affiliatedDatabaseId);
        return query.getSingleResult()>0;
    }

    @Override
    public List<SimpleDto> getOtherOrganizationsWithAffiliatedMembers(Long organizationId, Long databaseId, Long affiliatedDatabaseId) {
        final StringBuilder sb = new StringBuilder("Select distinct o.organization.id, o.organization.name from OrganizationCareTeamMember o ");

        sb.append(" WHERE o.organization.databaseId = :databaseId");
        sb.append(" AND o.employee.databaseId = :affiliatedDatabaseId");
        sb.append(" AND o.organization.id != :organizationId");

        final Query query
                = entityManager.createQuery(sb.toString());
        query.setParameter("databaseId", databaseId);
        query.setParameter("organizationId", organizationId);
        query.setParameter("affiliatedDatabaseId", affiliatedDatabaseId);

        List<Object[]> resultSets = query.getResultList();
        List<SimpleDto> results = new ArrayList<SimpleDto>();
        for (Object[] resultSet: resultSets) {
            SimpleDto simpleDto = new SimpleDto();
            simpleDto.setId((Long) resultSet[0]);
            simpleDto.setName((String) resultSet[1]);
            results.add(simpleDto);
        }
        return results;
    }

    @Override
    public boolean checkHasOrganizationCareTeamMember(Set<Long> employeeIds, Long organizationId) {
        if (CollectionUtils.isEmpty(employeeIds)) {
            return false;
        }

        final TypedQuery<Long> query = entityManager.createNamedQuery("organizationCareTeamMember.countOrganizationCareTeamMember", Long.class);
        query.setParameter("organizationId", organizationId);
        query.setParameter("employeeIds", employeeIds);
        return query.getSingleResult()>0L;
    }
}
