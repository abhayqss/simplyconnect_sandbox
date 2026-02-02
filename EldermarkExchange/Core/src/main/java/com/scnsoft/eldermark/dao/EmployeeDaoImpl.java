package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.exceptions.MultipleEntitiesFoundException;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.SimpleEmployee;
import com.scnsoft.eldermark.shared.carecoordination.contacts.ContactFilterDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EmployeeDaoImpl extends BaseDaoImpl<Employee> implements EmployeeDao {

    public EmployeeDaoImpl() {
        super(Employee.class);
    }


    @Override
    public Employee getActiveEmployee(String loginName, String loginCompanyId) {
        if (loginName == null || loginCompanyId == null) {
            return null;
        }

        TypedQuery<Long> query = entityManager.createQuery(
                "select e.id from Employee e join e.database db join db.systemSetup sysSetup" +
                        " where e.loginName = :loginName and sysSetup.loginCompanyId=:loginCompanyId and e.status=0",
                Long.class);

        query.setParameter("loginName", loginName);
        query.setParameter("loginCompanyId", loginCompanyId);

        try {
            Long employeeId = query.getSingleResult();
            return this.get(employeeId);
        } catch (javax.persistence.NoResultException e) {
            return null;
        } catch (javax.persistence.NonUniqueResultException e) {
            throw new MultipleEntitiesFoundException("login name is not unique: " + loginName, e);
        }

    }

    @Override
    public Employee getEmployee(long employeeId) {
        return entityManager.find(Employee.class, employeeId);
    }

    @Override
    public Employee getEmployee(long databaseId, String employeeLegacyId) {
        TypedQuery<Employee> query = entityManager.createQuery(
                "select e from Employee e where e.legacyId = :legacyId and e.database.id=:databaseId",
                Employee.class);
        query.setParameter("legacyId", employeeLegacyId);
        query.setParameter("databaseId", databaseId);

        try {
            return query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            return null;
        } catch (javax.persistence.NonUniqueResultException e) {
            throw new MultipleEntitiesFoundException("Multiple employees have been found: legacyId="
                    + employeeLegacyId + " databaseId=" + databaseId, e);
        }
    }

    @Override
    public Employee getEmployeeByLogin(long databaseId, String login) {
        TypedQuery<Employee> query = entityManager.createQuery(
                "select e from Employee e where e.loginName = :login and e.database.id=:databaseId",
                Employee.class);
        query.setParameter("login", login);
        query.setParameter("databaseId", databaseId);

        try {
            return query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            return null;
        } catch (javax.persistence.NonUniqueResultException e) {
            throw new MultipleEntitiesFoundException("Multiple employees have been found: login="
                    + login + " databaseId=" + databaseId, e);
        }
    }

    @Override
    public boolean isEmployeeLoginTaken(long databaseId, String login) {
        TypedQuery<Employee> query = entityManager.createQuery(
                "select e from Employee e where lower(e.loginName) = lower(:login) and e.database.id = :databaseId",
                Employee.class);
        query.setParameter("login", login);
        query.setParameter("databaseId", databaseId);
        query.setMaxResults(1);

        try {
            query.getSingleResult();
            return true;
        } catch (javax.persistence.NoResultException e) {
            return false;
        }
    }

    @Override
    public boolean isSecureEmailExist(String secureEmail) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT count(e) from Employee e where e.secureMessaging = :secureEmail",
                Long.class);
        query.setParameter("secureEmail", secureEmail);

        return query.getSingleResult() != 0L;
    }

    @Override
    public Long getEmployeeIdBySecureEmail(String secureEmail) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT e.id from Employee e where e.secureMessaging = :secureEmail",
                Long.class);
        query.setParameter("secureEmail", secureEmail);

        return query.getSingleResult();
    }

    @Override
    //TODO remove copypaste
    public List<Employee> getEmployeeForDatabase(final Database database, final ContactFilterDto filter, final Pageable pageable, Long currentEmployeeId) {
        final StringBuilder queryString = new StringBuilder();

        queryString.append("SELECT e from Employee e");
        queryString.append(" LEFT JOIN e.careTeamRole careTeamRole");
        queryString.append(" WHERE e.database = :database");

        if (StringUtils.isNotBlank(filter.getFirstName())) {
            queryString.append(" AND e.firstName LIKE :firstName");
        }
        if (StringUtils.isNotBlank(filter.getLastName())) {
            queryString.append(" AND e.lastName LIKE :lastName");
        }
        if (StringUtils.isNotBlank(filter.getEmail())) {
            queryString.append(" AND e.loginName LIKE :login");
        }
        if (filter.getRoleId() != null) {
            queryString.append(" AND careTeamRole.id LIKE :roleId");
        }
        if (CollectionUtils.isNotEmpty(filter.getEmployeeIds())) {
            queryString.append(" AND e.id IN (:employeeIds)");
        }
        EmployeeStatus status = null;
        if (filter.getStatus()!=null && filter.getStatus()>=0) {
            status = EmployeeStatus.getByValue(filter.getStatus());
        }
        if (status!=null) {
            queryString.append(" AND e.status = :status");
        }
//        if (communityId != null) {
//            queryString.append(" AND e.communityId LIKE :communityId");
//        }

        // The record of logged in user should be displayed at the top of the list (on the first page of the results) regardless sorting rules.
        queryString.append(" ORDER BY CASE WHEN e.id=")
                .append(currentEmployeeId)
                .append(" THEN 0 ELSE 1 END ASC");
        if (pageable.getSort() != null) {
            final Map<String, String> fieldByColumn = new HashMap<String, String>() {{
                put("displayName", "e.firstName");
                put("role", "careTeamRole.name");
                put("status", "e.status");
            }};
            boolean multiColumnSort = true;
            // sorting orders priority is important if data is sorted by multiple columns
            for (Sort.Order order : pageable.getSort()) {
                String field = fieldByColumn.get(order.getProperty());
                addSort(queryString, order, field, multiColumnSort);
            }
        }

        final TypedQuery<Employee> query = entityManager.createQuery(
                queryString.toString(),
                Employee.class);
        query.setParameter("database", database);

        if (StringUtils.isNotBlank(filter.getFirstName())) {
            query.setParameter("firstName", "%" + filter.getFirstName() + "%");
        }
        if (StringUtils.isNotBlank(filter.getLastName())) {
            query.setParameter("lastName", "%" + filter.getLastName() + "%");
        }
        if (StringUtils.isNotBlank(filter.getEmail())) {
            query.setParameter("login", "%" + filter.getEmail() + "%");
        }
        if (filter.getRoleId() != null) {
            query.setParameter("roleId", filter.getRoleId());
        }
        if (CollectionUtils.isNotEmpty(filter.getEmployeeIds())) {
            query.setParameter("employeeIds", filter.getEmployeeIds());
        }
//        if (filter.getStatus()!=null && filter.getStatus()>0 && EmployeeStatus.getByValue(filter.getStatus())!=null) {
        if (status!=null) {
            query.setParameter("status", EmployeeStatus.getByValue(filter.getStatus()));
        }
//        if (communityId != null) {
//            query.setParameter("communityId", communityId);
//        }

        applyPageable(query, pageable);

        return query.getResultList();
    }

    @Override
    //TODO remove copypaste
    public Long getEmployeeForDatabaseCount(final Database database, final ContactFilterDto filter) {
        final StringBuilder queryString = new StringBuilder();

        queryString.append("SELECT count(e) from Employee e");
        queryString.append(" LEFT JOIN e.careTeamRole careTeamRole");
        queryString.append(" WHERE e.database = :database");

        if (StringUtils.isNotBlank(filter.getFirstName())) {
            queryString.append(" AND e.firstName LIKE :firstName");
        }
        if (StringUtils.isNotBlank(filter.getLastName())) {
            queryString.append(" AND e.lastName LIKE :lastName");
        }
        if (StringUtils.isNotBlank(filter.getEmail())) {
            queryString.append(" AND e.loginName LIKE :login");
        }
        if (filter.getRoleId() != null) {
            queryString.append(" AND careTeamRole.id LIKE :roleId");
        }
        if (CollectionUtils.isNotEmpty(filter.getEmployeeIds())) {
            queryString.append(" AND e.id IN (:employeeIds)");
        }
        EmployeeStatus status = null;
        if (filter.getStatus()!=null && filter.getStatus()>=0) {
            status = EmployeeStatus.getByValue(filter.getStatus());
        }
        if (status!=null) {
            queryString.append(" AND e.status = :status");
        }

        final Query query = entityManager.createQuery(queryString.toString());
        query.setParameter("database", database);

        if (StringUtils.isNotBlank(filter.getFirstName())) {
            query.setParameter("firstName", "%" + filter.getFirstName() + "%");
        }
        if (StringUtils.isNotBlank(filter.getLastName())) {
            query.setParameter("lastName", "%" + filter.getLastName() + "%");
        }
        if (StringUtils.isNotBlank(filter.getEmail())) {
            query.setParameter("login", "%" + filter.getEmail() + "%");
        }
        if (filter.getRoleId() != null) {
            query.setParameter("roleId", filter.getRoleId());
        }
        if (CollectionUtils.isNotEmpty(filter.getEmployeeIds())) {
            query.setParameter("employeeIds", filter.getEmployeeIds());
        }
        if (status!=null) {
            query.setParameter("status", EmployeeStatus.getByValue(filter.getStatus()));
        }

        return (Long) query.getSingleResult();
    }

    @Override
    public Long getEmployeeCommunityId(long employeeId) {
        final Query query = entityManager.createQuery("SELECT communityId from Employee where id = :employeeId");
        query.setParameter("employeeId", employeeId);
        Object result = query.getSingleResult();
        return (Long)result;
    }

    @Override
    public List<Employee> getEmployeeForDatabase(final Long databaseId) {
        final StringBuilder queryString = new StringBuilder();

        queryString.append("select e from Employee e where e.database.id=:databaseId");
//
//        if (StringUtils.isNotBlank(searchString)) {
//            queryString.append(" AND (e.firstName LIKE :searchString OR e.lastName LIKE :searchString)");
//        }
//        if (communityId!=null){
//            queryString.append(" AND e.communityId =:communityId ");
//        }

        queryString.append(" ORDER BY e.firstName, e.lastName");

        final TypedQuery<Employee> query = entityManager.createQuery(
                queryString.toString(),
                Employee.class);
        query.setParameter("databaseId", databaseId);
//        if (communityId!=null) {
//            query.setParameter("communityId", communityId);
//        }
//
//        if (StringUtils.isNotBlank(searchString)) {
//            query.setParameter("searchString", "%" + searchString + "%");
//        }

        return query.getResultList();
    }

    @Override
    public List<SimpleEmployee> getEmployeeList(final Long databaseId, final Long communityId, final String searchString) {
        final StringBuilder queryString = new StringBuilder();

        queryString.append("select e from SimpleEmployee e where e.database.id=:databaseId");

        if (StringUtils.isNotBlank(searchString)) {
            queryString.append(" AND (e.firstName LIKE :searchString OR e.lastName LIKE :searchString)");
        }
        if (communityId!=null){
            queryString.append(" AND e.communityId =:communityId ");
        }
        queryString.append(" AND e.status!=2");
        queryString.append(" ORDER BY e.firstName, e.lastName");

        final TypedQuery<SimpleEmployee> query = entityManager.createQuery(
                queryString.toString(),SimpleEmployee.class);
        query.setParameter("databaseId", databaseId);
        if (communityId!=null) {
            query.setParameter("communityId", communityId);
        }

        if (StringUtils.isNotBlank(searchString)) {
            query.setParameter("searchString", "%" + searchString + "%");
        }
        return query.getResultList();
//        List<Object[]> resultSets = query.getResultList();
//        List<KeyValueDto> results = new ArrayList<KeyValueDto>();
//        for (Object[] resultSet: resultSets) {
//            KeyValueDto result = new KeyValueDto();
//            result.setId((Long) resultSet[0]);
//            result.setLabel((String) resultSet[1]);
//            results.add(result);
//        }
//        return results;
    }

    @Override
    public List<SimpleEmployee> getAffiliatedEmployeeList(Long organizationId, Long databaseId) {
        final StringBuilder queryString = new StringBuilder();

        queryString.append("select distinct e from SimpleEmployee e, AffiliatedOrganizations ao where e.database.id=ao.affiliatedDatabaseId " +
                "and (ao.primaryOrganizationId=:organizationId or (ao.primaryOrganizationId is null and ao.primaryDatabaseId = :databaseId)) " +
                "and e.status!=2");
        queryString.append(" ORDER BY e.firstName, e.lastName");

        final TypedQuery<SimpleEmployee> query = entityManager.createQuery(
                queryString.toString(), SimpleEmployee.class);
        query.setParameter("databaseId", databaseId);
        query.setParameter("organizationId", organizationId);

//        List<Object[]> resultSets = query.getResultList();
//        List<KeyValueDto> results = new ArrayList<KeyValueDto>();
//        for (Object[] resultSet: resultSets) {
//            KeyValueDto result = new KeyValueDto();
//            result.setId((Long) resultSet[0]);
//            result.setLabel((String) resultSet[1]);
//            results.add(result);
//        }
//        return results;
        return query.getResultList();
    }

    @Override
       public List<Employee> getAdministrators(Long databaseId) {
        final StringBuilder queryString = new StringBuilder();

        queryString.append("select e from Employee e where e.careTeamRole.code = 'ROLE_ADMINISTRATOR' " +
                "and e.databaseId = :databaseId");

        final TypedQuery<Employee> query = entityManager.createQuery(
                queryString.toString(),
                Employee.class);
        query.setParameter("databaseId", databaseId);

        return query.getResultList();
    }

    @Override
    public List<Employee> getCommunityAdministrators(Long communityId) {
        final StringBuilder queryString = new StringBuilder();

        queryString.append("select e from Employee e where e.careTeamRole.code = 'ROLE_COMMUNITY_ADMINISTRATOR' " +
                "and e.communityId = :communityId");

        final TypedQuery<Employee> query = entityManager.createQuery(
                queryString.toString(),
                Employee.class);
        query.setParameter("communityId", communityId);

        return query.getResultList();
    }


//    @Override
//    public List<Employee> getAffiliatedEmployeesForResident(Long residentId) {
//        final StringBuilder queryString = new StringBuilder();
//
//        queryString.append("select distinct e from Employee e, AffiliatedOrganizations ao where e.database.id=ao.affiliatedDatabaseId " +
//                "and (ao.primaryOrganizationId=(select facility.id from Resident where id = :residentId) or " +
//                "(ao.primaryOrganizationId is null and ao.primaryDatabaseId = (select database.id from Resident where id = :residentId)))");
//        queryString.append(" ORDER BY e.firstName, e.lastName");
//
//        final TypedQuery<Employee> query = entityManager.createQuery(
//                queryString.toString(),
//                Employee.class);
//        query.setParameter("residentId", residentId);
//
//        return query.getResultList();
//    }

    @Override
    public List<Employee> getEmployees(List<Long> ids) {
        TypedQuery<Employee> query = entityManager.createQuery(
                "SELECT e from Employee e where e.id in :employeeIds",
                Employee.class);
        query.setParameter("employeeIds", ids);
        return query.getResultList();
    }

    @Override
    public List<Employee> getEmployeesByData(String emailNormalized, String phoneNormalized) {
        TypedQuery<Employee> query = entityManager.createQuery(
                "SELECT DISTINCT e from Employee e join e.person.telecoms t " +
                        "WHERE (e.loginHash = dbo.hash_string(:email, 150) AND e.loginName = :email)" +
                        " AND t.useCode <> 'EMAIL' AND (t.valueHash = dbo.hash_string(:phone, default) AND t.valueNormalized = :phone) ",
                Employee.class);
        query.setParameter("email", emailNormalized);
        query.setParameter("phone", phoneNormalized);
        return query.getResultList();
    }

    @Override
    public List<Employee> getEmployeesByData(List<Long> databaseIds, String emailNormalized, String phoneNormalized, String firstName, String lastName, Pageable pageable) {
        StringBuilder sQuery = new StringBuilder();
        sQuery.append("SELECT DISTINCT e from Employee e join e.person.telecoms ph left join e.person.telecoms em ");
        sQuery.append("WHERE (ph.useCode <> 'EMAIL' AND (ph.valueHash = dbo.hash_string(:phone, default) AND ph.valueNormalized = :phone)) ");
        sQuery.append("AND ((e.loginHash = dbo.hash_string(:email, 150) AND lower(e.loginName) = :email) OR ");
        sQuery.append("(em.useCode = 'EMAIL' AND em.valueHash = dbo.hash_string(:email, default) AND em.valueNormalized = :email)) ");
        if (firstName != null) {
            sQuery.append("AND (e.firstNameHash = dbo.hash_string(:firstName, 150) AND lower(e.firstName) = :firstName) ");
        }
        if (lastName != null) {
            sQuery.append("AND (e.lastNameHash = dbo.hash_string(:lastName, 150) AND lower(e.lastName) = :lastName) ");
        }
        if (databaseIds != null) {
            sQuery.append("AND e.databaseId IN :databaseIds ");
        }
        TypedQuery<Employee> query = entityManager.createQuery(sQuery.toString(), Employee.class);
        query.setParameter("email", emailNormalized);
        query.setParameter("phone", phoneNormalized);
        if (firstName != null) {
            query.setParameter("firstName", StringUtils.lowerCase(firstName));
        }
        if (lastName != null) {
            query.setParameter("lastName", StringUtils.lowerCase(lastName));
        }
        if (databaseIds != null) {
            query.setParameter("databaseIds", databaseIds);
        }
        applyPageable(query, pageable);

        return query.getResultList();
    }
}
