package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.scansol.shared.ScanSolOrganizationDto;
import com.scnsoft.scansol.shared.ScanSolRoleDto;

import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

/**
 * Date: 14.05.15
 * Time: 15:18
 */
@Repository
public class OrganizationDaoImpl extends BaseDaoImpl<Organization> implements OrganizationDao {
    public OrganizationDaoImpl() {
        super(Organization.class);
    }

    @Override
    public List<Organization> getOrganizationsByEmployee(long employeeId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Organization> criteria = cb.createQuery(Organization.class);

        Root<EmployeeOrganization> root = criteria.from(EmployeeOrganization.class);

        Join<EmployeeOrganization, Organization> joinOrganization = root.join("organization");
        Join<EmployeeOrganization, Employee> joinEmployee = root.join("employee");

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(joinEmployee.<Long>get("id"), employeeId));
        predicates.addAll(eligibleForDiscovery(cb, joinOrganization));

        criteria.where(predicates.toArray(new Predicate[]{}));

        criteria.orderBy(cb.asc(joinOrganization.<String>get("name")));

        criteria.select(joinOrganization);

        TypedQuery<Organization> query = entityManager.createQuery(criteria);
        return query.getResultList();
    }

    @Override
    public List<Organization> getOrganizationsByEmployee(long employeeId, RoleCode role) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Organization> query = builder.createQuery(Organization.class);

        Root<EmployeeOrganization> from = query.from(EmployeeOrganization.class);

        Join<EmployeeOrganization, Organization> joinOrganization = from.join("organization");
        Join<EmployeeOrganization, Employee> joinEmployee = from.join("employee");

        Subquery<Long> subquery1 = query.subquery(Long.class);
        Root<EmployeeOrganization> subquery1From = subquery1.from(EmployeeOrganization.class);
        Join<EmployeeOrganization, Employee> subquery1JoinEmployee = subquery1From.join("employee");
        Join<Group, Role> joinGroupRoles = subquery1From.join("groups").join("roles");
        subquery1.where(
                builder.equal(joinGroupRoles.<String>get("code"), role),
                builder.equal(subquery1JoinEmployee.<Long>get("id"), joinEmployee.<Long>get("id")));
        subquery1.select(subquery1From.<Long>get("id"));

        Subquery<Long> subquery2 = query.subquery(Long.class);
        Root<EmployeeOrganization> subquery2From = subquery2.from(EmployeeOrganization.class);
        Join<EmployeeOrganization, Employee> subquery2JoinEmployee = subquery2From.join("employee");
        Join<EmployeeOrganization, Role> joinRoles = subquery2From.join("roles");
        subquery2.where(
                builder.equal(joinRoles.<String>get("code"), role),
                builder.equal(subquery2JoinEmployee.<Long>get("id"), joinEmployee.<Long>get("id")));
        subquery2.select(subquery2From.<Long>get("id"));

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(builder.equal(joinEmployee.<Long>get("id"), employeeId));
        predicates.addAll(eligibleForDiscovery(builder, joinOrganization));
        predicates.add(builder.or(from.<Long>get("id").in(subquery1), from.<Long>get("id").in(subquery2)));

        query.where(predicates.toArray(new Predicate[]{}));

        query.orderBy(builder.asc(joinOrganization.<String>get("name")));

        query.select(joinOrganization);

        return entityManager.createQuery(query).getResultList();
    }

    
	@Override
	public void getOrganizationsByRole(long employeeId, ScanSolOrganizationDto organization) {
		Query q = entityManager.createNativeQuery("Select name from Role where id in ("
				+ "Select role_id from Employee_Organization_Role where employee_organization_id in ("
				+ "Select id from Employee_Organization where employee_id=:employee and organization_id=:employee_organization_id))");
		q.setParameter("employee_organization_id", organization.getId());
		q.setParameter("employee", employeeId);
		List<ScanSolRoleDto> role = q.getResultList();
		organization.setRoles(role);
	}

	@Override
	public void getOrganizationsByGroupRole(long employeeId, ScanSolOrganizationDto organization) {
		Query q = entityManager.createNativeQuery("Select name from Role where id in("
				+ "Select role_id from Groups_Role where group_id in ("
				+ "Select group_id from Employee_Organization_Group where employee_organization_id  in ("
				+ "Select id from Employee_Organization where employee_id=:employee and organization_id=:employee_organization_id)))");
		q.setParameter("employee_organization_id", organization.getId());
		q.setParameter("employee", employeeId);
		List<ScanSolRoleDto> role = q.getResultList();
		if (role.isEmpty()) {
			getOrganizationsByRole(employeeId, organization);
		} else {
			organization.setRoles(role);
		}
	}

	@Override
	public void getOrganizationsByEmployeeGroup(long employeeId, ScanSolOrganizationDto organization) {
		Query q = entityManager.createNativeQuery(
				"Select name from Role where id in(" + "Select role_id from Groups_Role where group_id in ("
						+ "Select group_id from Employee_Groups where employee_id=:employee))");
		q.setParameter("employee", employeeId);
		List<ScanSolRoleDto> role = q.getResultList();
		if (role.isEmpty()) {
			getOrganizationsByGroupRole(employeeId, organization);
		} else {
			organization.setRoles(role);
		}
	}
    
    @Override
    public List<Organization> getOrganizationsByDatabase(long databaseId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery criteria = cb.createQuery(Organization.class);

        Root<Organization> root = criteria.from(Organization.class);
        criteria.select(root);

        Join<Organization, Database> joinDatabase = root.join("database");

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(joinDatabase.<String>get("id"), databaseId));

        predicates.addAll(eligibleForDiscovery(cb, root));

        criteria.where(predicates.toArray(new Predicate[]{}));

        TypedQuery<Organization> query = entityManager.createQuery(criteria);
        return query.getResultList();
    }

    @Override
    public Long getOrganizationsByDatabaseCount(long databaseId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery criteria = cb.createQuery(Long.class);

        Root<Organization> root = criteria.from(Organization.class);
        criteria.select(cb.countDistinct(root));

        Join<Organization, Database> joinDatabase = root.join("database");

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(joinDatabase.<String>get("id"), databaseId));

        predicates.addAll(eligibleForDiscovery(cb, root));

        criteria.where(predicates.toArray(new Predicate[]{}));

        TypedQuery<Long> query = entityManager.createQuery(criteria);
        return query.getSingleResult();
    }

    /**
     * Not all organizations are allowed to be discovered by HIE.
     */
    public static List<Predicate> eligibleForDiscovery(CriteriaBuilder cb, From fromFacility) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        predicates.add(cb.equal(fromFacility.<String>get("legacyTable"), "Company"));

        // Community is NOT inactive
        Path<Boolean> isInactive = fromFacility.get("isInactive");
        predicates.add(cb.or(cb.isFalse(isInactive), cb.isNull(isInactive)));

        // Community is ALLOWED to be discovered by Eldermark OR by Cloud
        boolean isEldermarkUser = SecurityUtils.isEldermarkUser(); //TODO NEED TO ADD ELDERMARK USER TO CARE COORDINATION USER
        boolean isCloudUser = SecurityUtils.isCloudUser() || SecurityUtils.isCloudManager();

        Path<Boolean> eligibleForExchange = fromFacility.get("moduleHie");
        Path<Boolean> eligibleForCloud = fromFacility.get("moduleCloudStorage");

        if (isEldermarkUser && isCloudUser) {
            predicates.add(cb.or(cb.isTrue(eligibleForExchange), cb.isTrue(eligibleForCloud)));
        } else if (isEldermarkUser) {
            predicates.add(cb.isTrue(eligibleForExchange));
        } else if (isCloudUser) {
            predicates.add(cb.isTrue(eligibleForCloud));
        }

        return predicates;
    }

    @Override
    public Organization getOrganization(long id) {
        return get(id);
    }

    @Override
    public Pair<String, String> getOrganizationLogos(long id) {
        Query q = entityManager.createQuery("Select o.mainLogoPath, o.additionalLogoPath from Organization o where o.id=:id");
        q.setParameter("id", id);
        Object[] result = (Object[]) q.getSingleResult();
        return new Pair<String, String>((String)result[0], (String)result[1]);
    }

    @Override
    public Organization getOrganizationByName(final String name) {
        TypedQuery<Organization> query = entityManager.createQuery("Select o from Organization o WHERE o.name =:name", Organization.class);
        query.setParameter("name", name);
        return query.getSingleResult();
    }

    @Override
    public Organization getOrganizationByNameAndDatabase(String name, Long databaseId) {
        TypedQuery<Organization> query = entityManager.createQuery("Select o from Organization o WHERE o.name = :name and o.databaseId = :databaseId", Organization.class);
        query.setParameter("name", name);
        query.setParameter("databaseId", databaseId);
        return query.getSingleResult();
    }

    @Override
    public Organization getOrganizationByOid(String oid, Long databaseId) {
        TypedQuery<Organization> query = entityManager.createQuery("Select o from Organization o WHERE o.oid = :oid and o.databaseId = :databaseId", Organization.class);
        query.setParameter("oid", oid);
        query.setParameter("databaseId", databaseId);
        return query.getSingleResult();
    }

    @Override
    public List<Organization> getOrganizationByOidAndDatabaseOid(String oid, String databaseOid) {
        TypedQuery<Organization> query = entityManager.createQuery("Select o from Organization o WHERE o.oid = :oid and o.database.oid = :databaseOid", Organization.class);
        query.setParameter("oid", oid);
        query.setParameter("databaseOid", databaseOid);
        return query.getResultList();
    }

    @Override
    public List<Organization> getOrganizationByNameAndDatabaseOid(String name, String databaseOid) {
        TypedQuery<Organization> query = entityManager.createQuery("Select o from Organization o WHERE o.name = :name and o.database.oid = :databaseOid", Organization.class);
        query.setParameter("name", name);
        query.setParameter("databaseOid", databaseOid);
        return query.getResultList();
    }

    @Override
    public List<Organization> getOrganizationByOidAndNameAndDatabaseOid(String oid, String name, String databaseOid) {
        TypedQuery<Organization> query = entityManager.createQuery("Select o from Organization o WHERE o.oid = :oid and o.name = :name and o.database.oid = :databaseOid", Organization.class);
        query.setParameter("oid", oid);
        query.setParameter("name", name);
        query.setParameter("databaseOid", databaseOid);
        return query.getResultList();
    }

    @Override
    public List<Long> getDatabasesByOrganizationIds(List<Long> organizationIds) {
        TypedQuery<Long> query = entityManager.createQuery("Select o.databaseId from Organization o WHERE o.id IN :organizationIds", Long.class);
        query.setParameter("organizationIds", organizationIds);
        return query.getResultList();
    }

    @Override
    public List<Organization> getPharmacyByResidentId(Long residentId) {
        Query query = entityManager.createNativeQuery("select * from Organization o " +
                "join ResPharmacy resPharm on o.id = resPharm.pharmacy_id " +
                "join OrganizationAddress orgAddress on orgAddress.org_id = o.id " +
                "WHERE resPharm.resident_id = :residentId", Organization.class);
        query.setParameter("residentId", residentId);
        return query.getResultList();
    }
}
