package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class RoleDaoImpl implements RoleDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Role> getEmployeeRoles(long employeeId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> criteria = cb.createQuery(Role.class);

        Root<Employee> root = criteria.from(Employee.class);
        Join<Employee, Role> joinRoles = root.join("roles");

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(root.<Long>get("id"), employeeId));

        criteria.where(predicates.toArray(new Predicate[]{}));

        criteria.select(joinRoles);

        TypedQuery<Role> query = entityManager.createQuery(criteria);
        return query.getResultList();
    }

    @Override
    public List<Role> getEmployeeOrganizationRoles(long employeeId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> criteria = cb.createQuery(Role.class);

        Root<EmployeeOrganization> root = criteria.from(EmployeeOrganization.class);
        Join<EmployeeOrganization, Role> joinRoles = root.join("roles");

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(root.join("employee").<Long>get("id"), employeeId));

        criteria.where(predicates.toArray(new Predicate[]{}));

        criteria.select(joinRoles);

        TypedQuery<Role> query = entityManager.createQuery(criteria);
        return query.getResultList();
    }

    @Override
    public List<Role> getEmployeeOrganizationGroupRoles(long employeeId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> criteria = cb.createQuery(Role.class);

        Root<EmployeeOrganization> root = criteria.from(EmployeeOrganization.class);
        Join<Group, Role> joinRoles = root.join("groups").join("roles");

        criteria.where((new Predicate[]{cb.equal(root.join("employee").<Long>get("id"), employeeId)}));

        criteria.select(joinRoles);

        TypedQuery<Role> query = entityManager.createQuery(criteria);
        return query.getResultList();
    }

    @Override
    public List<Role> getEmployeeGroupRoles(long employeeId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> criteria = cb.createQuery(Role.class);

        Root<Employee> root = criteria.from(Employee.class);
        Join<Employee, Group> joinGroups = root.join("groups");
        Join<Group, Role> joinRoles = joinGroups.join("roles");

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(root.<Long>get("id"), employeeId));

        criteria.where(predicates.toArray(new Predicate[]{}));

        criteria.select(joinRoles);

        TypedQuery<Role> query = entityManager.createQuery(criteria);
        return query.getResultList();
    }

    @Override
    public boolean hasRole(long employeeId, RoleCode code) {
        Set<Role> roles = new HashSet<Role>();
        Role role = findByCode(code);

        roles.addAll(getEmployeeRoles(employeeId));
        roles.addAll(getEmployeeGroupRoles(employeeId));
        roles.addAll(getEmployeeOrganizationRoles(employeeId));
        roles.addAll(getEmployeeOrganizationGroupRoles(employeeId));

        return roles.contains(role);
    }

    @Override
    public Role findByCode(RoleCode code) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> criteria = cb.createQuery(Role.class);

        Root<Role> root = criteria.from(Role.class);
        criteria.where(new Predicate[]{
                cb.equal(root.<RoleCode>get("code"), code)
        });

        criteria.select(root);

        TypedQuery<Role> query = entityManager.createQuery(criteria);
        return query.getSingleResult();
    }
}
