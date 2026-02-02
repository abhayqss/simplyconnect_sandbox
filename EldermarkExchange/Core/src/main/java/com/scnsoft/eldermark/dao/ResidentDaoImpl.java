package com.scnsoft.eldermark.dao;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.scnsoft.eldermark.dao.exceptions.MultipleEntitiesFoundException;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.palatiumcare.NotifyResident;
import com.scnsoft.eldermark.shared.ResidentFilter;
import com.scnsoft.eldermark.shared.administration.MatchStatus;
import com.scnsoft.eldermark.shared.administration.MergeStatus;
import com.scnsoft.eldermark.shared.administration.SearchMode;
import com.scnsoft.eldermark.shared.phr.utils.Normalizer;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import javax.persistence.Query;

import javax.persistence.EntityGraph;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

import static com.scnsoft.eldermark.dao.dialect.SqlServerCustomDialect.MSSQL_WHERE_IN_PARAM_LIMIT;
import static org.springframework.data.util.ClassTypeInformation.from;

@Repository
public class ResidentDaoImpl extends BaseDaoImpl<Resident> implements ResidentDao {
    private static final Logger logger = LoggerFactory.getLogger(ResidentDaoImpl.class);

    public ResidentDaoImpl() {
        super(Resident.class);
    }

    @Override
    public List<Resident> getResidents(ResidentFilter filter, Pageable pageable) {
        TypedQuery<Resident> query = createResidentCriteriaQuery(filter, false, pageable.getSort());
        applyPageable(query, pageable);
        return query.getResultList();
    }

    @Override
    public List<Resident> getResidents(ResidentFilter filter, int start, int limit) {
        TypedQuery<Resident> query = createResidentCriteriaQuery(filter, false, null);
        query.setFirstResult(start);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public List<Resident> getResidents(ResidentFilter filter) {
        TypedQuery<Resident> query = createResidentCriteriaQuery(filter, false, null);
        return query.getResultList();
    }

    @Override
    public Long getResidentCount(ResidentFilter filter) {
        TypedQuery<Long> query = createResidentCriteriaQuery(filter, true, null);
        return query.getSingleResult();
    }

    @Override
    public Resident getResident(long residentId) {
        return getResident(residentId, false);
    }

    @Override
    public Resident getResident(long residentId, Boolean includeOptOut) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Resident> criteria = cb.createQuery(Resident.class);

        Root<Resident> root = criteria.from(Resident.class);
        Join<Resident, Organization> joinFacility = root.join("facility");

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.<Long>get("id"), residentId));
        if(!includeOptOut) {
            predicates.add(eligibleForDiscovery(cb, root));
        }
        predicates.addAll(OrganizationDaoImpl.eligibleForDiscovery(cb, joinFacility));

        criteria.where(predicates.toArray(new Predicate[]{}));

        try {
            TypedQuery<Resident> query = entityManager.createQuery(criteria);
            return query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Resident> getResidents(Collection<Long> residentIds) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return Collections.emptyList();
        }
        if (residentIds.size() > MSSQL_WHERE_IN_PARAM_LIMIT) {
            logger.warn("Dialect [com.scnsoft.eldermark.dao.dialect.SqlServerCustomDialect] limits the number of elements in an IN predicate to " +
                    MSSQL_WHERE_IN_PARAM_LIMIT + " entries. However, the given parameter list [residents] contains " + residentIds.size() +
                    " entries, which will likely cause failures to execute the query in the database. So the number of parameters is reduced to " +
                    MSSQL_WHERE_IN_PARAM_LIMIT + ".");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Resident> criteria = cb.createQuery(Resident.class);

        Root<Resident> root = criteria.from(Resident.class);
        Join<Resident, Organization> joinFacility = root.join("facility");

        List<Predicate> predicates = new ArrayList<>();

        Path<Object> residentIdColumn = root.get("id");
        // TODO create batch requests when residents list contains more than 2000 ids (if there's ever such case)
        predicates.add(residentIdColumn.in(Sets.newHashSet(Iterables.limit(residentIds, MSSQL_WHERE_IN_PARAM_LIMIT))));
        predicates.add(eligibleForDiscovery(cb, root));
        predicates.addAll(OrganizationDaoImpl.eligibleForDiscovery(cb, joinFacility));

        criteria.where(predicates.toArray(new Predicate[]{}));

        // fetch lazy initialized Resident properties (one-to-one relations)
        //EntityGraph<Resident> fetchGraph = entityManager.createEntityGraph(Resident.class);
        //fetchGraph.addSubgraph("mpi");

        TypedQuery<Resident> query = entityManager.createQuery(criteria);
                //.setHint("javax.persistence.loadgraph", fetchGraph);
        return query.getResultList();
    }

    @Override
    public Resident getResident(long databaseId, String residentLegacyId) {
        return getResident(databaseId, null, residentLegacyId, false);
    }

    @Override
    public Resident getResident(long databaseId, String residentLegacyId, boolean includeOptOut) {
        return getResident(databaseId, null, residentLegacyId, includeOptOut);
    }

    @Override
    public Long getResidentId(String databaseAlternativeId, String residentLegacyId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = cb.createQuery(Long.class);

        Root<Resident> root = criteria.from(Resident.class);
        Join<Resident, Database> joinDatabase = root.join("database");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.<Long>get("legacyId"), residentLegacyId));
        predicates.add(cb.equal(joinDatabase.<Long>get("alternativeId"), databaseAlternativeId));

        criteria.select(root.<Long>get("id")).where(predicates.toArray(new Predicate[]{}));

        try {
            TypedQuery<Long> query = entityManager.createQuery(criteria);
            return query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            return null;
        } catch (javax.persistence.NonUniqueResultException e) {
            throw new MultipleEntitiesFoundException("Multiple residents have been found: legacyId=" + residentLegacyId + " databaseAlternativeId=" + databaseAlternativeId, e);
        }
    }

    @Override
    public List<Resident> getResidentsByOrganization(long organizationId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Resident> criteria = cb.createQuery(Resident.class);

        Root<Resident> root = criteria.from(Resident.class);
        Join<Resident, Organization> joinFacility = root.join("facility");

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(joinFacility.<Long>get("id"), organizationId));
//        predicates.add(eligibleForDiscovery(cb, root));

        Path<String> firstName = root.get("firstName"); //legacyId
        Path<String> lastName = root.get("lastName");
        Path<String> legacyId = root.get("legacyId");
        predicates.add(cb.and(cb.notEqual(firstName, ""), cb.isNotNull(firstName)));
        predicates.add(cb.and(cb.notEqual(lastName, ""), cb.isNotNull(lastName)));
        predicates.add(cb.and(cb.notEqual(legacyId, ""), cb.isNotNull(legacyId)));

        predicates.addAll(OrganizationDaoImpl.eligibleForDiscovery(cb, joinFacility));

        criteria.where(predicates.toArray(new Predicate[]{}));

        TypedQuery<Resident> query = entityManager.createQuery(criteria);
        return query.getResultList();
    }

    @Override
    public List<Resident> filterResidentsByOrganization(Collection<Long> residentIds, long organizationId) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return Collections.emptyList();
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Resident> criteria = cb.createQuery(Resident.class);

        Root<Resident> root = criteria.from(Resident.class);
        Join<Resident, Organization> joinFacility = root.join("facility");

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(joinFacility.<Long>get("id"), organizationId));
        predicates.add(root.<Long>get("id").in(residentIds));

        criteria.where(predicates.toArray(new Predicate[]{}));

        TypedQuery<Resident> query = entityManager.createQuery(criteria);
        return query.getResultList();
    }

    @Override
    public Resident getResidentByIdentityFields(Long organizationId, String ssn, Date dateOfBirth, String lastName, String firstName) {
        final TypedQuery<Resident> query = entityManager.createQuery(
                "Select r FROM Resident r LEFT JOIN r.facility f LEFT JOIN r.person.names n " +
                        "WHERE f.id = :organizationId AND r.socialSecurity=:ssn AND r.birthDate=:birthDate " +
                        "AND n.family=:lastName AND n.given=:firstName AND n.nameUse=:nameUse", entityClass);
        query.setParameter("organizationId", organizationId);
        query.setParameter("ssn", ssn);
        query.setParameter("birthDate", dateOfBirth);
        query.setParameter("lastName", lastName);
        query.setParameter("firstName", firstName);
        query.setParameter("nameUse", NameUseCode.L.name());

        List<Resident> residents = query.getResultList();
        if (residents.size() > 0) {
            return residents.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Resident getResidentInCommunity(long communityId, String residentLegacyId) {
        return getResident(null, communityId, residentLegacyId, false);
    }

    @Override
    public Resident getResidentByIdentityFields(Long organizationId, long communityId, String residentLegacyId) {
        return getResident(organizationId, communityId, residentLegacyId, false);
    }

    private Resident getResident(Long databaseId, Long communityId, String residentLegacyId, boolean includeOptOut) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Resident> criteria = cb.createQuery(Resident.class);

        Root<Resident> root = criteria.from(Resident.class);
        Join<Resident, Organization> joinFacility = root.join("facility");
        Join<Resident, Database> joinDatabase = root.join("database");

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.<Long>get("legacyId"), residentLegacyId));
        if (databaseId != null) {
            predicates.add(cb.equal(joinDatabase.<Long>get("id"), databaseId));
        }
        if (communityId != null) {
            predicates.add(cb.equal(joinFacility.<Long>get("id"), communityId));
        }
        if(!includeOptOut) {
            predicates.add(eligibleForDiscovery(cb, root));
        }
        predicates.addAll(OrganizationDaoImpl.eligibleForDiscovery(cb, joinFacility));

        criteria.where(predicates.toArray(new Predicate[]{}));

        try {
            TypedQuery<Resident> query = entityManager.createQuery(criteria);
            return query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            return null;
        } catch (javax.persistence.NonUniqueResultException e) {
            throw new MultipleEntitiesFoundException("Multiple residents have been found: legacyId=" + residentLegacyId + " databaseId=" + databaseId, e);
        }
    }

    private TypedQuery createResidentCriteriaQuery(ResidentFilter filter, Boolean count, Sort sort) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery criteria;
        if (count) {
            criteria = cb.createQuery(Long.class);
        } else {
            criteria = cb.createQuery(Resident.class);
        }

        Root<Resident> root = criteria.from(Resident.class);
        if (count) {
            criteria.<Long>select(cb.countDistinct(root));
        }

        List<Predicate> predicates = new ArrayList<>();    // predicates that may be connected with conjunction or disjunction (depends on SearchMode)
        List<Predicate> predicatesAnd = new ArrayList<>(); // predicates that should be connected with conjunction
        ParameterExpression<Long> dividerParameter = cb.parameter(Long.class, "divider");

        Join<Resident, Person> joinPerson = root.join("person");

        String firstName = filter.getFirstName();
        String lastName = filter.getLastName();
        String middleName = filter.getMiddleName();
        boolean isExactFirstNameMatch = false;
        boolean isExactLastNameMatch = false;
        if (StringUtils.isNotBlank(firstName) || StringUtils.isNotBlank(lastName) || StringUtils.isNotBlank(middleName)) {
            Join<Person, Name> joinName = joinPerson.join("names");

            Predicate nameUseIsLegalOrSearch = cb.or(
                    //cb.isNull(joinName.<String>get("nameUse")),
                    cb.equal(joinName.<String>get("nameUse"), NameUseCode.L.name()),
                    cb.equal(joinName.<String>get("nameUse"), NameUseCode.SRCH.name()));
            if (StringUtils.isNotBlank(firstName)) {
                Predicate givenNameMatches;
                if (SearchMode.MATCH_ANY_LIKE.equals(filter.getMode())) {
                    givenNameMatches = cb.like(joinName.<String>get("givenNormalized"), String.format("%%%s%%", Normalizer.normalizeName(firstName)));
                } else {
                    isExactFirstNameMatch = true;
                    ParameterExpression<String> givenName = cb.parameter(String.class, "givenName");
                    // call stored function - JPA 2.1+ feature
                    Expression<Long> hashFunction = cb.function("dbo.hash_string", Long.class, givenName, dividerParameter);
                    givenNameMatches = cb.and(
                            cb.equal(joinName.<String>get("givenHash"), hashFunction),
                            cb.equal(joinName.<String>get("givenNormalized"), givenName));
                }
                predicates.add(cb.and(givenNameMatches, nameUseIsLegalOrSearch));
            }
            if (StringUtils.isNotBlank(lastName)) {
                Predicate familyNameMatches;
                if (SearchMode.MATCH_ANY_LIKE.equals(filter.getMode())) {
                    familyNameMatches = cb.like(joinName.<String>get("familyNormalized"), String.format("%%%s%%", Normalizer.normalizeName(lastName)));
                } else {
                    isExactLastNameMatch = true;
                    ParameterExpression<String> familyName = cb.parameter(String.class, "familyName");
                    // call stored function - JPA 2.1+ feature
                    Expression<Long> hashFunction = cb.function("dbo.hash_string", Long.class, familyName, dividerParameter);
                    familyNameMatches = cb.and(
                            cb.equal(joinName.<String>get("familyHash"), hashFunction),
                            cb.equal(joinName.<String>get("familyNormalized"), familyName));
                }

                predicates.add(cb.and(familyNameMatches, nameUseIsLegalOrSearch));
            }
            if (StringUtils.isNotBlank(middleName)) {
                ParameterExpression<String> middleNameParameter = cb.parameter(String.class, "middleName");
                // call stored function - JPA 2.1+ feature
                Expression<Long> hashFunction = cb.function("dbo.hash_string", Long.class, middleNameParameter, dividerParameter);
                Predicate middleNameMatches = cb.and(
                        cb.equal(joinName.<String>get("middleHash"), hashFunction),
                        cb.equal(joinName.<String>get("middleNormalized"), middleNameParameter));
                predicates.add(cb.and(middleNameMatches, nameUseIsLegalOrSearch));
            }
        }

        Join<Resident, CcdCode> joinGender = root.join("gender", JoinType.LEFT);
        if (filter.getGender() != null) {
            Predicate genderMatchesFilter = cb.equal(joinGender.<String>get("code"), filter.getGender().getAdministrativeGenderCode());
            if (SearchMode.MATCH_ALL.equals(filter.getMode())) {
                Predicate genderIsNull = cb.isNull(joinGender.get("code"));
                predicates.add(cb.or(genderMatchesFilter, genderIsNull));
            } else {
                predicates.add(genderMatchesFilter);
            }
        }

        Join<Resident, Organization> joinFacility = root.join("facility");

        predicatesAnd.add(eligibleForDiscovery(cb, root));
        predicatesAnd.addAll(OrganizationDaoImpl.eligibleForDiscovery(cb, joinFacility));

        String providerOrganizationName = filter.getProviderOrganization();
        Join<Resident, Database> joinDatabase = root.join("database");
        if (StringUtils.isNotBlank(providerOrganizationName)) {
            if (SearchMode.MATCH_ANY_LIKE.equals(filter.getMode())) {
                predicates.add(cb.like(
                        cb.lower(joinDatabase.<String>get("name")),
                        String.format("%%%s%%", StringUtils.lowerCase(providerOrganizationName))));
            } else {
                predicates.add(cb.equal(
                        cb.lower(joinDatabase.<String>get("name")),
                        StringUtils.lowerCase(providerOrganizationName)));
            }
        }
        if (filter.getDatabase() != null) {
            predicates.add(cb.equal(joinDatabase.get("id"), filter.getDatabase().getId()));
        }

        String communityName = filter.getCommunity();
        if (StringUtils.isNotBlank(communityName)) {
            if (SearchMode.MATCH_ANY_LIKE.equals(filter.getMode())) {
                predicates.add(cb.like(
                        cb.lower(joinFacility.<String>get("name")),
                        String.format("%%%s%%", StringUtils.lowerCase(communityName))));
            } else {
                predicates.add(cb.equal(
                        cb.lower(joinFacility.<String>get("name")),
                        StringUtils.lowerCase(communityName)));
            }
        }

        if (filter.getDateOfBirth() != null) {
            Path<Date> dateOfBirth = root.get("birthDate");
            ParameterExpression<Date> dateParameter = cb.parameter(Date.class, "birthDate");
            // call stored function - JPA 2.1+ feature
            Expression<Long> hashFunction = cb.function("dbo.hash_string", Long.class, dateParameter, dividerParameter);
            Predicate birthDatePredicate = cb.and(
                    cb.equal(root.get("birthDateHash"), hashFunction),
                    cb.equal(dateOfBirth, dateParameter));
            predicates.add(birthDatePredicate);
        }

        if (StringUtils.isNotBlank(filter.getCity()) || StringUtils.isNotBlank(filter.getState()) ||
                StringUtils.isNotBlank(filter.getPostalCode()) || StringUtils.isNotBlank(filter.getStreet())) {
            Join<Person, PersonAddress> joinAddress = joinPerson.join("addresses", JoinType.LEFT);

            if (StringUtils.isNotBlank(filter.getCity())) {
                predicates.add(cb.equal(cb.lower(joinAddress.<String>get("city")), filter.getCity().toLowerCase()));
            }

            if (StringUtils.isNotBlank(filter.getState())) {
                predicates.add(cb.equal(cb.lower(joinAddress.<String>get("state")), filter.getState().toLowerCase()));
            }

            if (StringUtils.isNotBlank(filter.getPostalCode())) {
                predicates.add(cb.equal(joinAddress.<String>get("postalCode"), filter.getPostalCode()));
            }

            if (StringUtils.isNotBlank(filter.getStreet())) {
                predicates.add(cb.equal(joinAddress.<String>get("streetAddress"), filter.getStreet()));
            }
        }

        if (StringUtils.isNotBlank(filter.getPhone())) {
            Join<Person, PersonTelecom> joinTelecom = joinPerson.join("telecoms", JoinType.LEFT);

            //Exclude joined email (JPA 2.0 doesn't support ON in JOIN clause)
            //Use code for phones is either HP, WP, or null, not EMAIL
            Expression<String> telecomUseCode = joinTelecom.get("useCode");
            ParameterExpression<String> phone = cb.parameter(String.class, "phoneNormalized");
            // call stored function - JPA 2.1+ feature
            Expression<Long> hashFunction = cb.function("dbo.hash_string", Long.class, phone, dividerParameter);
            predicates.add(cb.and(
                    cb.notEqual(telecomUseCode, PersonTelecomCode.EMAIL.name()),
                    cb.equal(joinTelecom.get("valueHash"), hashFunction),
                    cb.equal(joinTelecom.get("valueNormalized"), phone)));
        }

        if (StringUtils.isNotBlank(filter.getEmail())) {
            Join<Person, PersonTelecom> joinTelecom = joinPerson.join("telecoms", JoinType.LEFT);

            //Use code for emails is EMAIL
            Expression<String> telecomUseCode = joinTelecom.get("useCode");
            ParameterExpression<String> email = cb.parameter(String.class, "emailNormalized");
            // call stored function - JPA 2.1+ feature
            Expression<Long> hashFunction = cb.function("dbo.hash_string", Long.class, email, dividerParameter);
            predicates.add(cb.and(
                    cb.equal(telecomUseCode, PersonTelecomCode.EMAIL.name()),
                    cb.equal(joinTelecom.get("valueHash"), hashFunction),
                    cb.equal(joinTelecom.get("valueNormalized"), email)));
        }

        String ssn = filter.getSsn();
        boolean isExactSsnMatch = false;
        boolean matchSsnFourDigits = true;
        if (StringUtils.isNotBlank(ssn)) {
            matchSsnFourDigits = false;
            if (SearchMode.MATCH_ANY_LIKE.equals(filter.getMode())) {
                // partial searching by SSN is used in Administration > Suggested Matches tab
                predicates.add(cb.like(root.<String>get("socialSecurity"), String.format("%%%s%%", ssn)));
            } else if (ssn.length() == 9 && StringUtils.isNumeric(ssn)) {
                // SSN with 9 digits is used in PHR search and Administration > Manual Matching tab
                isExactSsnMatch = true;
                ParameterExpression<String> ssnParameter = cb.parameter(String.class, "ssn");
                // call stored function - JPA 2.1+ feature
                Expression<Long> hashFunction = cb.function("dbo.hash_string", Long.class, ssnParameter, dividerParameter);
                predicates.add(cb.and(
                        cb.equal(root.<String>get("socialSecurityHash"), hashFunction),
                        cb.equal(root.<String>get("socialSecurity"), ssnParameter)));
            } else if (ssn.length() == 4 && StringUtils.isNumeric(ssn)) {
                // SSN with 4 digits is a special case in PHR search
                predicates.add(cb.like(root.<String>get("socialSecurity"), String.format("%%%s", ssn)));
            } else {
                // Do not use an incorrect SSN for search
                predicates.add(cb.or());
                matchSsnFourDigits = true;
            }
        }

        if (matchSsnFourDigits && StringUtils.isNotBlank(filter.getLastFourDigitsOfSsn())) {
            predicates.add(cb.equal(root.<String>get("ssnLastFourDigits"), filter.getLastFourDigitsOfSsn()));
        }

        if (filter.getMatchStatus() != null && filter.getMergeStatus() != null) {
            Subquery<MpiMergedResidents> subquery;
            if (MatchStatus.NOT_MATCHED.equals(filter.getMatchStatus()) && MergeStatus.NOT_MERGED.equals(filter.getMergeStatus())) {
                // In this case use NOT EXISTS clause
                //subquery = createMpiMergedResidentsSubquery(cb, criteria, root, null, null);
                //predicatesAnd.add(cb.not(cb.exists(subquery)));
            } else {
                subquery = createMpiMergedResidentsSubquery(cb, criteria, root, filter.getMatchStatus(), filter.getMergeStatus());
                predicatesAnd.add(cb.exists(subquery));
            }
        }

        // by default, this predicate is true, in order to select all residents that are eligible for discovery
        Predicate residentMatches = cb.conjunction();
        switch (filter.getMode()) {
            case MATCH_ALL:
                residentMatches = cb.and(predicates.toArray(new Predicate[]{}));
                break;
            case MATCH_ANY:
            case MATCH_ANY_LIKE:
                if (!CollectionUtils.isEmpty(predicates)) {
                    residentMatches = cb.or(predicates.toArray(new Predicate[]{}));
                }
                break;
        }

        criteria.where(cb.and(predicatesAnd.toArray(new Predicate[]{})), residentMatches);

        Map<String, Path> orderByPaths = new HashMap<>();
        orderByPaths.put("residentNumber", root.get("legacyId"));

        boolean orderedByResidentNumber = false;
        boolean undefinedOrder = false;

        List<Order> orders = new ArrayList<>();
        if (sort != null && !count) {
            Join<Person, Name> joinName = joinPerson.join("names");
            orderByPaths.put("firstName", joinName.get("givenNormalized"));
            orderByPaths.put("lastName", joinName.get("familyNormalized"));
            orderByPaths.put("genderDisplayName", joinGender.get("code"));
            orderByPaths.put("gender", joinGender.get("code"));
            orderByPaths.put("ssn", root.get("socialSecurity"));
            orderByPaths.put("dateOfBirth", root.get("birthDate"));
            orderByPaths.put("organizationName", joinFacility.get("name"));
            orderByPaths.put("databaseName", joinDatabase.get("name"));
            orderByPaths.put("dateCreated", root.get("dateCreated"));

            for (Sort.Order order : sort) {
                if ("random".equals(order.getProperty())) {
                    undefinedOrder = true;
                    continue;
                }
                Path pathToProperty = orderByPaths.get(order.getProperty());
                orders.add(order.isAscending() ? cb.asc(cb.min(pathToProperty)) : cb.desc(cb.min(pathToProperty)));
            }
            orderedByResidentNumber = (sort.getOrderFor("residentNumber") != null);
        }

        if (!orderedByResidentNumber && !count && !undefinedOrder) {
            Path pathToProperty = orderByPaths.get("residentNumber");
            orders.add(cb.asc(cb.min(pathToProperty)));
        }

        if (!count) {
            // Select distinct residents
            // Fix for SQLServerException: ORDER BY items must appear in the select list if SELECT DISTINCT is specified
            criteria.<Resident>groupBy(getGroupByForResident(root));
            criteria.orderBy(orders);
        }

        TypedQuery query = entityManager.createQuery(criteria);

        // populate parameters
        if (filter.getDateOfBirth() != null) {
            query.setParameter("birthDate", filter.getDateOfBirth(), TemporalType.DATE);
            query.setParameter("divider", null);
        }
        if (StringUtils.isNotBlank(filter.getPhone())) {
            query.setParameter("phoneNormalized", Normalizer.normalizePhone(filter.getPhone()));
            // pass NULL for default parameter value
            query.setParameter("divider", null);
        }
        if (StringUtils.isNotBlank(filter.getEmail())) {
            query.setParameter("emailNormalized", Normalizer.normalizeEmail(filter.getEmail()));
            query.setParameter("divider", null);
        }
        if (isExactSsnMatch) {
            query.setParameter("ssn", ssn);
            query.setParameter("divider", null);
        }
        if (isExactFirstNameMatch) {
            query.setParameter("givenName", Normalizer.normalizeName(firstName));
            query.setParameter("divider", null);
        }
        if (isExactLastNameMatch) {
            query.setParameter("familyName", Normalizer.normalizeName(lastName));
            query.setParameter("divider", null);
        }
        if (StringUtils.isNotBlank(filter.getMiddleName())) {
            query.setParameter("middleName", Normalizer.normalizeName(filter.getMiddleName()));
            query.setParameter("divider", null);
        }
        return query;
    }

    private List<Path<?>> getGroupByForResident(Root<Resident> root) {
        return Arrays.asList(root,
                root.get("facility"),
                root.get("database"),
                root.get("legacyId"),
                root.get("legacyTable"),
                root.get("admitDate"),
                root.get("dischargeDate"),
                root.get("birthDate"),
                root.get("age"),
                root.get("gender"),
                root.get("maritalStatus"),
                root.get("ethnicGroup"),
                root.get("religion"),
                root.get("socialSecurity"),
                root.get("ssnLastFourDigits"),
                root.get("race"),
                root.get("isOptOut"),
                root.get("active"),
                //root.get("guardians"),
                //root.get("languages"),
                //root.get("documentationOfs"),
                //root.get("advanceDirectives"),
                //root.get("authors"),
                root.get("providerOrganization"),
                root.get("person"),
                root.get("dataEnterer"),
                root.get("custodian"),
                root.get("legalAuthenticator"),
                root.get("hashKey"),
                root.get("medicalRecordNumber"),
                root.get("veteran"),
                root.get("prevAddrStreet"),
                root.get("prevAddrCity"),
                root.get("prevAddrState"),
                root.get("prevAddrZip"),
                root.get("hospitalPreference"),
                root.get("transportationPreference"),
                root.get("ambulancePreference"),
                root.get("preadmissionNumber"),
                root.get("medicareNumber"),
                root.get("authorizationNumber"),
                root.get("authorizationNumberExpires"),
                root.get("medicaidNumber"),
                root.get("evacuationStatus"),
                root.get("unitNumber"),
                root.get("advanceDirectiveFreeText"),
                root.get("dentalInsurance"),
                //root.get("orders"),
                //root.get("alertNotes"),
                //root.get("healthPlans"),
                //root.get("residentCareTeamMembers"),
                //root.get("events"),
                root.get("socialSecurityHash"),
                root.get("birthDateHash"),
                root.get("firstName"),
                root.get("lastName"),
                root.get("middleName"),
                root.get("preferredName"),
                root.get("dateCreated"),
                root.get("birthPlace"),
                root.get("createdById"),
                root.get("intakeDate"),
                root.get("currentPharmacyName"),
                root.get("inNetworkInsurance"),
                root.get("insurancePlan"),
                root.get("groupNumber"),
                root.get("memberNumber"),
                //root.get("primaryCarePhysician"),
                root.get("lastUpdated"),
                root.get("status"),
                root.get("citizenship"),
                root.get("insurancePlanName"),
                root.get("consanaXrefId")
        );
    }

    /**
     * Subquery for checking the existence of "matched" or "maybe matched" residents
     * TODO : filter out residents non eligible for discovery ?
     */
    private Subquery<MpiMergedResidents> createMpiMergedResidentsSubquery(CriteriaBuilder cb, CommonAbstractCriteria criteria, Root<Resident> rootResident,
                                                                          MatchStatus matchStatus, MergeStatus mergeStatus) {
        Subquery<MpiMergedResidents> subquery = criteria.subquery(MpiMergedResidents.class);
        Root<MpiMergedResidents> fromMpiMergedResidents = subquery.from(MpiMergedResidents.class);
        subquery.select(fromMpiMergedResidents.<MpiMergedResidents>get("id"));

        List<Predicate> subqueryPredicates = new ArrayList<>();
        subqueryPredicates.add(cb.or(
                cb.equal(fromMpiMergedResidents.get("survivingResident"), rootResident),
                cb.equal(fromMpiMergedResidents.get("mergedResident"), rootResident) ));

        switch (matchStatus) {
            case SURELY_MATCHED:
                subqueryPredicates.add(cb.not(cb.equal(fromMpiMergedResidents.get("probablyMatched"), true)));
                subqueryPredicates.add(cb.equal(fromMpiMergedResidents.get("merged"), true));
                break;
            case MAYBE_MATCHED:
                subqueryPredicates.add(cb.equal(fromMpiMergedResidents.get("probablyMatched"), true));
                break;
            case NOT_MATCHED:
                subqueryPredicates.add(cb.not(cb.equal(fromMpiMergedResidents.get("probablyMatched"), true)));
                break;
        }

        switch (mergeStatus) {
            case MERGED_MANUALLY:
                subqueryPredicates.add(cb.equal(fromMpiMergedResidents.get("mergedManually"), true));
                subqueryPredicates.add(cb.equal(fromMpiMergedResidents.get("merged"), true));
                break;
            case MERGED_AUTOMATICALLY:
                subqueryPredicates.add(cb.equal(fromMpiMergedResidents.get("mergedAutomatically"), true));
                subqueryPredicates.add(cb.equal(fromMpiMergedResidents.get("merged"), true));
                break;
            case NOT_MERGED:
                subqueryPredicates.add(cb.not(cb.equal(fromMpiMergedResidents.get("merged"), true)));
                break;
        }

        subquery.where(cb.and(subqueryPredicates.toArray(new Predicate[] {})));

        return subquery;
    }

    /**
     * Not all patients are allowed to be discovered by HIE.
     */
    private Predicate eligibleForDiscovery(CriteriaBuilder cb, Path<Resident> fromResident) {
        // Resident was NOT opted out
        Path<Boolean> isOptOut = fromResident.get("isOptOut");

        Predicate p1 = cb.isFalse(isOptOut);
        Predicate p2 = cb.isNull(isOptOut);

        return cb.or(p1, p2);
    }

	public Date getResidentArchiveDate(Long residentId, Long organizationId) {
		String query1 = "from AdmittanceHistory where resident_id=:resident_id and organization_id=:organization_id";
		final Query query = entityManager.createQuery(query1);
		query.setParameter("resident_id", residentId);
		query.setParameter("organization_id", organizationId);

		try {
			List<AdmittanceHistory> ad = (List<AdmittanceHistory>) query.getResultList();
			Iterator<AdmittanceHistory> iterator = ad.iterator();
			while (iterator.hasNext()) {
				AdmittanceHistory residentArchiveDate = iterator.next();
				if (residentArchiveDate.getArchiveDate() != null) {
					return residentArchiveDate.getArchiveDate();
				} else {
					return null;
				}
			}

		} catch (Exception e) {
			// return null;
		}
		return null;

	}
}
