package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDaoImpl;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.ResidentCareTeamMember;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

/**
 * @author mradzivonenka
 * @author Netkachev
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 24-Sep-15.
 */
@Repository
public class ResidentCareTeamMemberDaoImpl extends BaseDaoImpl<ResidentCareTeamMember> implements ResidentCareTeamMemberDao {
    public ResidentCareTeamMemberDaoImpl() {
        super(ResidentCareTeamMember.class);
    }

    @Override
    public List<ResidentCareTeamMember> getCareTeamMembers(Collection<Long> residentIds, Boolean affiliated, final Pageable pageable) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return Collections.emptyList();
        }

        final StringBuilder sb = new StringBuilder("Select o from ResidentCareTeamMember o ");
        sb.append(" LEFT JOIN FETCH o.careTeamRole ctr ");
        sb.append(" INNER JOIN FETCH o.employee e");

        sb.append(" WHERE o.resident.id IN (:residentIds) ");

        if (affiliated != null) {
            if (affiliated) {
                sb.append(" AND e.databaseId != o.resident.databaseId");
            } else {
                sb.append(" AND e.databaseId = o.resident.databaseId");
            }
        }

        final Map<String, String> fieldByColumn = new HashMap<String, String>() {{
            put("employee.label", "e.firstName");
            put("role.label", "ctr.name");
            put("employeeDatabaseName", "e.database.name");
        }};
        applySort(sb, pageable, fieldByColumn);

        final TypedQuery<ResidentCareTeamMember> query = entityManager.createQuery(sb.toString(), ResidentCareTeamMember.class);

        query.setParameter("residentIds", residentIds);
        applyPageable(query, pageable);

        return query.getResultList();
    }

    @Override
    public List<ResidentCareTeamMember> getCareTeamMembers(Long residentId) {
        return getCareTeamMembers(Collections.singleton(residentId), null, null);
    }

    @Override
    public Long getCareTeamMembersCount(Long residentId) {
        final Query query = entityManager.createQuery("Select count(o) from ResidentCareTeamMember o where o.resident.id = :residentId");
        query.setParameter("residentId", residentId);

        return (Long) query.getSingleResult();
    }

    @Override
    public List<ResidentCareTeamMember> getResidentCareTeamMembersByEmployeeAndRole(final Long residentId, final Long employeeId, final Long roleId) {
        final StringBuffer sb = new StringBuffer("Select o from ResidentCareTeamMember o ");
        sb.append(" LEFT JOIN o.careTeamRole ");
        sb.append(" INNER JOIN o.employee ");
        sb.append(" INNER JOIN o.resident ");

        sb.append(" WHERE o.employee.id = :employeeId ");
        sb.append(" AND o.careTeamRole.id = :roleId ");
        sb.append(" AND o.resident.id = :residentId ");

        final TypedQuery<ResidentCareTeamMember> query = entityManager.createQuery(sb.toString(), ResidentCareTeamMember.class);

        query.setParameter("employeeId", employeeId);
        query.setParameter("roleId", roleId);
        query.setParameter("residentId", residentId);

        return query.getResultList();
    }

    @Override
    public List<Long> getCareTeamResidentIdsByEmployeeId(Set<Long> employeeIds, Long databaseId) {
        if (CollectionUtils.isEmpty(employeeIds)) {
            return Collections.emptyList();
        }

        final String strQuery = "SELECT o.resident.id FROM ResidentCareTeamMember o JOIN o.resident WHERE o.employee.id in :employeeIds and o.resident.databaseId=:databaseId";
        final TypedQuery<Long> query = entityManager.createQuery(strQuery, Long.class);
        query.setParameter("employeeIds", employeeIds);
        query.setParameter("databaseId", databaseId);
        return query.getResultList();
    }

    @Override
    public List<Long> getCareTeamResidentIdsByEmployeeId(Set<Long> employeeIds) {
        if (CollectionUtils.isEmpty(employeeIds)) {
            return Collections.emptyList();
        }

        final String strQuery = "SELECT o.resident.id FROM ResidentCareTeamMember o JOIN o.resident WHERE o.employee.id in :employeeIds";
        final TypedQuery<Long> query = entityManager.createQuery(strQuery, Long.class);
        query.setParameter("employeeIds", employeeIds);
        return query.getResultList();
    }

    @Override
    public List<Long> getCareTeamResidentIdsByEmployeeIdWithAccessRightsCheck(Set<Long> employeeIds, AccessRight accessRight) {
        if (CollectionUtils.isEmpty(employeeIds)) {
            return Collections.emptyList();
        }

        final String strQuery = "SELECT r.id FROM ResidentCareTeamMember o JOIN o.resident r JOIN o.accessRights ar WHERE o.employee.id IN :employeeIds AND ar = :accessRight";
        final TypedQuery<Long> query = entityManager.createQuery(strQuery, Long.class);
        query.setParameter("employeeIds", employeeIds);
        query.setParameter("accessRight", accessRight);
        return query.getResultList();
    }

    @Override
    public List<ResidentCareTeamMember> getCareTeamMembersByEmployeeIds(Set<Long> employeeIds, Pageable pageable) {
        if (CollectionUtils.isEmpty(employeeIds)) {
            return Collections.emptyList();
        }

        final StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT o FROM ResidentCareTeamMember o JOIN FETCH o.resident r WHERE o.employee.id in :employeeIds");
        final Map<String, String> fieldByColumn = new HashMap<String, String>() {{
            put("resident.fullName", "r.lastName");
            put("resident.lastName", "r.lastName");
            put("resident.firstName", "r.firstName");
            put("employee.label", "o.employee.firstName");
            put("role.label", "o.careTeamRole.name");
            put("employeeDatabaseName", "o.employee.database.name");
        }};
        applySort(queryString, pageable, fieldByColumn);
        final TypedQuery<ResidentCareTeamMember> query = entityManager.createQuery(queryString.toString(), ResidentCareTeamMember.class);
        query.setParameter("employeeIds", employeeIds);
        applyPageable(query, pageable);

        return query.getResultList();
    }

    private void applySort(StringBuilder queryString, Pageable pageable, Map<String, String> fieldByColumn) {
        if (pageable != null && pageable.getSort() != null) {
            boolean multiColumnSort = false;
            for (Sort.Order order : pageable.getSort()) {
                String field = fieldByColumn.get(order.getProperty());
                final boolean added = addSort(queryString, order, field, multiColumnSort);
                multiColumnSort |= added;
            }
        }
    }

    @Override
    public Long getCareTeamMembersCountByEmployeeIds(Set<Long> employeeIds) {
        if (CollectionUtils.isEmpty(employeeIds)) {
            return 0L;
        }

        final String strQuery = "SELECT coalesce(count(o), 0) FROM ResidentCareTeamMember o WHERE o.employee.id IN :employeeIds";
        final TypedQuery<Long> query = entityManager.createQuery(strQuery, Long.class);
        query.setParameter("employeeIds", employeeIds);
        return query.getSingleResult();
    }

    @Override
    public ResidentCareTeamMember getResidentCareTeamMemberByEmployeeIdAndResidentId(Long employeeId, Long residentId) {
        final String strQuery = "SELECT rctm FROM ResidentCareTeamMember rctm WHERE rctm.employee.id = :employeeId and rctm.resident.id = :residentId";
        final TypedQuery<ResidentCareTeamMember> query = entityManager.createQuery(strQuery, ResidentCareTeamMember.class);
        query.setParameter("employeeId", employeeId);
        query.setParameter("residentId", residentId);
        query.setMaxResults(1);
        return query.getSingleResult();
    }

    @Override
    public List<ResidentCareTeamMember> getResidentCareTeamMembersByEmployeeIdAndResidentIds(Long employeeId, List<Long> residentIds) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return Collections.emptyList();
        }

        final String strQuery = "SELECT rctm FROM ResidentCareTeamMember rctm WHERE rctm.employee.id = :employeeId and rctm.resident.id IN :residentIds";
        final TypedQuery<ResidentCareTeamMember> query = entityManager.createQuery(strQuery, ResidentCareTeamMember.class);
        query.setParameter("employeeId", employeeId);
        query.setParameter("residentIds", residentIds);
        return query.getResultList();
    }

    private List<ResidentCareTeamMember> getPrimaryCareTeamMembersWithAccessRightCheckExcludeInactive(Set<Long> residentIds, AccessRight accessRight, boolean affiliated) {
        StringBuilder strQuery = new StringBuilder();
        strQuery.append("SELECT rctm FROM ResidentCareTeamMember rctm INNER JOIN rctm.resident r INNER JOIN rctm.accessRights ar ");
        strQuery.append("INNER JOIN FETCH rctm.employee e WHERE rctm.resident.id in (:residentIds) AND e.databaseId");
        if(affiliated){
            strQuery.append("<>");
        } else{
            strQuery.append("=");
        }
        strQuery.append(" r.databaseId  AND ar = :accessRight AND (e.status=0 OR e.createdAutomatically = true)");
        final TypedQuery<ResidentCareTeamMember> query = entityManager.createQuery(strQuery.toString(), ResidentCareTeamMember.class);
        query.setParameter("residentIds", residentIds);
        query.setParameter("accessRight", accessRight);
        return query.getResultList();
    }

    @Override
    public List<ResidentCareTeamMember> getPrimaryCareTeamMembersWithAccessRightCheckExcludeInactive(Set<Long> residentIds, AccessRight accessRight) {
        return getPrimaryCareTeamMembersWithAccessRightCheckExcludeInactive(residentIds, accessRight, false);
    }

    @Override
    public List<ResidentCareTeamMember> getAffiliatedCareTeamMembersWithAccessRightCheckExcludeInactive(Set<Long> residentIds, AccessRight accessRight) {
        return getPrimaryCareTeamMembersWithAccessRightCheckExcludeInactive(residentIds, accessRight, true);
    }

    @Override
    public void deleteByIdIn(List<Long> idsToDelete) {
        final Query query = entityManager.createNamedQuery("residentCareTeamMember.delete");
        query.setParameter("idsToDelete", idsToDelete);
        query.executeUpdate();
    }

    @Override
    public List<Long> getResidentCareTeamMemberIdsFromDeletedAffiliatedRelation() {
        final TypedQuery<Long> query = entityManager.createNamedQuery("residentCareTeamMember.getIdsToDelete", Long.class);
        return query.getResultList();
    }

    @Override
    public boolean checkHasResidentCareTeamMember(Set<Long> employeeIds, Long residentId) {
        if (CollectionUtils.isEmpty(employeeIds)) {
            return false;
        }

        final TypedQuery<Long> query = entityManager.createNamedQuery("residentCareTeamMember.count", Long.class);
        query.setParameter("residentId", residentId);
        query.setParameter("employeeIds", employeeIds);
        return query.getSingleResult()>0L;
    }

    @Override
    public Boolean getIncludeInFaceSheetById(Long careTeamMemberId) {
        final Query query = entityManager.createQuery("Select o.includeInFaceSheet from ResidentCareTeamMember o where o.id = :careTeamMemberId");
        query.setParameter("careTeamMemberId", careTeamMemberId);
        Object result = query.getSingleResult();
        return result==null ? false : (Boolean)result;
    }
    
    @Override
    public List<ResidentCareTeamMember> getCareTeamMembersToBeIncludedInFacesheet(Collection<Long> residentIds) {
        final Query query = entityManager.createQuery("Select o from ResidentCareTeamMember o where o.resident.id in :residentIds and o.includeInFaceSheet = :includeInFaceSheet");
        query.setParameter("residentIds", residentIds);
        query.setParameter("includeInFaceSheet", true);
        return query.getResultList();
    }

}
