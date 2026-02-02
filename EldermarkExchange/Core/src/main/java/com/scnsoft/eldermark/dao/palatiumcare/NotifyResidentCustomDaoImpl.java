package com.scnsoft.eldermark.dao.palatiumcare;


import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.OrganizationCareTeamMember;
import com.scnsoft.eldermark.entity.ResidentCareTeamMember;
import com.scnsoft.eldermark.entity.palatiumcare.NotifyResident;

import com.scnsoft.eldermark.shared.palatiumcare.resident.NotifyResidentFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.Collections;
import java.util.List;

@Repository
public class NotifyResidentCustomDaoImpl implements NotifyResidentCustomDao {

    @PersistenceContext
    private EntityManager entityManager;

    private Predicate buildSearchPredicate(Root<NotifyResident> root, List<String> searchParams) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        if(searchParams == null || searchParams.size() < 1) return criteriaBuilder.and();

        Predicate firstNamePredicate = criteriaBuilder.and();
        Predicate lastNamePredicate = criteriaBuilder.and();
        Predicate roomPredicate = criteriaBuilder.and();

        firstNamePredicate = criteriaBuilder.or(criteriaBuilder.like(root.<String>get("firstName"), "%" + searchParams.get(0) + "%"));
        lastNamePredicate = criteriaBuilder.or(criteriaBuilder.like(root.<String>get("lastName"), "%" + searchParams.get(0) + "%"));
        roomPredicate = criteriaBuilder.or(criteriaBuilder.like(root.<String>get("location").<String>get("room"), "%" + searchParams.get(0) + "%"));
        searchParams.remove(0);

        for(String param: searchParams) {
            firstNamePredicate = criteriaBuilder.or(firstNamePredicate,
                    criteriaBuilder.like(root.<String>get("firstName"), "%" + param + "%"));
            lastNamePredicate = criteriaBuilder.or(lastNamePredicate,
                    criteriaBuilder.like(root.<String>get("lastName"), "%" + param + "%"));
            roomPredicate = criteriaBuilder.or(roomPredicate,
                    criteriaBuilder.like(root.<String>get("location").<String>get("room"), "%" + param + "%"));
        }
        return criteriaBuilder.or(firstNamePredicate, lastNamePredicate, roomPredicate);
    }

    @Override
    public List<NotifyResident> getCareTeamMemberResidentsByEmployeeId(Long employeeId, NotifyResidentFilter notifyResidentFilter, Pageable pageable) {
        List<String> searchParams = notifyResidentFilter.getParams() != null ? notifyResidentFilter.getParams(): Collections.<String>emptyList();        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<NotifyResident> criteriaQuery = criteriaBuilder.createQuery(NotifyResident.class);
        Root<NotifyResident> rootFromNotifyResident = criteriaQuery.from(NotifyResident.class);
        Join<NotifyResident, ResidentCareTeamMember> residentCareTeamMembersJoin
                = rootFromNotifyResident.join("residentCareTeamMembers", JoinType.LEFT);
        Join<NotifyResident, Employee> employeeJoin = residentCareTeamMembersJoin.join("employee", JoinType.LEFT);
        ParameterExpression<Long> employeeIdParam = criteriaBuilder.parameter(Long.class, "employeeId");
        Predicate employeeIdPredicate = criteriaBuilder.equal(employeeJoin.get("id"), employeeIdParam);
        criteriaQuery.where(employeeIdPredicate, buildSearchPredicate(rootFromNotifyResident, searchParams));
        criteriaQuery.distinct(Boolean.TRUE);
        TypedQuery<NotifyResident> q = entityManager.createQuery(criteriaQuery).setParameter("employeeId", employeeId)
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize()).setMaxResults(pageable.getPageSize());
        return q.getResultList();
    }


    @Override
    public List<NotifyResident> getCommunityResidentsByEmployeeId(Long employeeId, NotifyResidentFilter notifyResidentFilter, Pageable pageable) {
        List<String> searchParams = notifyResidentFilter.getParams() != null ? notifyResidentFilter.getParams(): Collections.<String>emptyList();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<NotifyResident> criteriaQuery = criteriaBuilder.createQuery(NotifyResident.class);
        Root<NotifyResident> rootFromNotifyResident = criteriaQuery.from(NotifyResident.class);
        Join<NotifyResident, Organization> facilityJoin = rootFromNotifyResident.join("facility", JoinType.LEFT);
        Join<NotifyResident, OrganizationCareTeamMember> organizationCareTeamMembersJoin
                = facilityJoin.join("organizationCareTeamMembers", JoinType.LEFT);
        Join<NotifyResident, Employee> employeeJoin = organizationCareTeamMembersJoin.join("organizationCareTeamMembers", JoinType.LEFT);
        ParameterExpression<Long> employeeIdParam = criteriaBuilder.parameter(Long.class, "employeeId");
        Predicate employeeIdPredicate = criteriaBuilder.equal(employeeJoin.get("id"), employeeIdParam);
        criteriaQuery.where(employeeIdPredicate, buildSearchPredicate(rootFromNotifyResident, searchParams));
        criteriaQuery.distinct(Boolean.TRUE);
        TypedQuery<NotifyResident> q = entityManager.createQuery(criteriaQuery).setParameter("employeeId", employeeId)
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize()).setMaxResults(pageable.getPageSize());
        return q.getResultList();
    }
}
