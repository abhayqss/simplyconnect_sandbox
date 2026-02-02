package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.dao.BaseDaoImpl;
import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.services.carecoordination.CareCoordinationResidentFilter;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.PatientDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.PatientListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.PatientsFilterDto;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by pzhurba on 24-Sep-15.
 */
@Repository
public class CareCoordinationResidentDaoImpl extends BaseDaoImpl<CareCoordinationResident> implements CareCoordinationResidentDao {
    private static final Logger logger = LoggerFactory.getLogger(CareCoordinationResidentDaoImpl.class);

    public CareCoordinationResidentDaoImpl() {
        super(CareCoordinationResident.class);
    }


    public List<CareCoordinationResident> getResidentsForEmployeeByResidentCareTeam(final Long employeeId) {
        final TypedQuery<CareCoordinationResident> query = entityManager.createQuery("Select o.resident from ResidentCareTeamMember o WHERE o.employee.id = :employeeId", entityClass);
        query.setParameter("employeeId", employeeId);
        return query.getResultList();
    }

    public CareCoordinationResident getResidentByIdentityFields(Long organizationId, String ssn, Date dateOfBirth, String lastName, String firstName) {
        final TypedQuery<CareCoordinationResident> query = entityManager.createQuery(
                "Select r FROM CareCoordinationResident r LEFT JOIN r.facility f " +
                        "WHERE f.id = :organizationId AND r.socialSecurity=:ssn AND r.birthDate=:birthDate AND r.lastName=:lastName AND r.firstName=:firstName ", entityClass);
        query.setParameter("organizationId", organizationId);
        query.setParameter("ssn", ssn);
        query.setParameter("birthDate", dateOfBirth);
        query.setParameter("lastName", lastName);
        query.setParameter("firstName", firstName);
        List<CareCoordinationResident> residents = query.getResultList();
        if (residents.size() > 0) return residents.get(0);
        else return null;
    }

    public boolean checkExistResidentByIdentityFields(Long organizationId, String ssn, Date dateOfBirth, String lastName, String firstName) {
        final TypedQuery<Long> query = entityManager.createQuery(
                "Select count(r.id) FROM CareCoordinationResident r LEFT JOIN r.facility f " +
                        "WHERE f.id = :organizationId AND r.socialSecurity=:ssn AND r.birthDate=:birthDate AND r.lastName=:lastName AND r.firstName=:firstName ", Long.class);
        query.setParameter("organizationId", organizationId);
        query.setParameter("ssn", ssn);
        query.setParameter("birthDate", dateOfBirth);
        query.setParameter("lastName", lastName);
        query.setParameter("firstName", firstName);
        return query.getSingleResult() > 0;
    }

    @Override
    public List<PatientListItemDto> getResidentsForEmployee(final Set<Long> employeeIds, final PatientsFilterDto filter, final Long databaseId, final List<Long> filterCommunityIds, final Pageable pageable, boolean isAdmin, Set<Long> employeeCommunityIds) {

        final StringBuilder sb = new StringBuilder();

        String showSurvivingDeactivatedQuery = "";
        String showMergedDeactivatedQuery = "";
        if (filter == null || !filter.getShowDeactivated()) {
            showSurvivingDeactivatedQuery = " AND mr.survivingCCResident.active = true ";
            showMergedDeactivatedQuery = " AND mr.mergedCCResident.active = true ";
        }

        sb.append("SELECT DISTINCT r.id, r.firstName, r.lastName, r.birthDate, gender.displayName, r.ssnLastFourDigits, r.hashKey, r.databaseId, " +
                "(select count(distinct ev.id) from Event ev INNER JOIN ev.resident ev_resident INNER JOIN ev.eventType ev_type " +
                "where ev_type.service = false and (ev_resident.id =r.id " +
                ")) + " +
                "(select count(distinct ev.id) from Event ev INNER JOIN ev.resident ev_resident INNER JOIN ev.eventType ev_type " +
                "INNER JOIN ev_resident.mainResidents merged_surv " +
                "where ev_type.service = false AND merged_surv.survivingResident.id = r.id " +
                ") + " +
                "(select count(distinct ev.id) from Event ev INNER JOIN ev.resident ev_resident INNER JOIN ev.eventType ev_type " +
                "INNER JOIN ev_resident.secondaryResidents merged_merg " +
                "where ev_type.service = false  AND merged_merg.mergedResident.id = r.id " +
                ") as ecount, facility.name, r.active, r.dateCreated, " +
                "(select count(mr.id) from MpiMergedResidents mr where (mr.survivingCCResident.id = r.id or  mr.mergedCCResident.id = r.id) and merged = 1 " +
                "and ((mr.survivingCCResident.databaseId=:databaseId  " + showSurvivingDeactivatedQuery + "and mr.survivingCCResident.id != r.id) " +
                "or (mr.mergedCCResident.databaseId=:databaseId  " + showMergedDeactivatedQuery + " and mr.mergedCCResident.id != r.id))) as merged_count ");


        residentsForEmployeeQueryApplyFromAndWhereClause(sb, filter, filterCommunityIds, isAdmin, employeeCommunityIds);
        residentsForEmployeeQueryApplySortable(sb, pageable);
        logger.info(sb.toString());

        final Query query = entityManager.createQuery(sb.toString());
        residentsForEmployeeQueryApplyQueryParams(query, employeeIds, filter, databaseId, filterCommunityIds, isAdmin, employeeCommunityIds);

        if (pageable != null) {
            applyPageable(query, pageable);
        }
        List<Object[]> resultSets = query.getResultList();
        List<PatientListItemDto> results = new ArrayList<PatientListItemDto>();
        for (Object[] resultSet : resultSets) {
            PatientListItemDto result = new PatientListItemDto();
            result.setId((Long) resultSet[0]);
            result.setFirstName((String) resultSet[1]);
            result.setLastName((String) resultSet[2]);
            result.setBirthDate((Date) resultSet[3]);
            result.setGender((String) resultSet[4]);
            String ssnLastFourDigits = (String) resultSet[5];
            if (isNotBlank(ssnLastFourDigits)) {
                result.setSsn("###-##-" + resultSet[5]);
            }
            result.setHashKey((String) resultSet[6]);
            result.setOrganizationId((Long) resultSet[7]);
            result.setEventCount((Long) resultSet[8]);
            result.setCommunity((String) resultSet[9]);
            result.setActive((Boolean) resultSet[10]);
            result.setDateCreated((Date) resultSet[11]);
            result.setHasMerged(((Long) resultSet[12]) > 0);
            results.add(result);
        }
        return results;
    }


    @Override
    public List<Long> getResidentIdsForEmployee(final Set<Long> employeeIds, final Long databaseId, final List<Long> filterCommunityIds, boolean isAdmin, Set<Long> employeeCommunityIds) {

        final StringBuilder sb = new StringBuilder("SELECT DISTINCT r.id ");

        final PatientsFilterDto filter = new PatientsFilterDto();
        filter.setShowDeactivated(Boolean.TRUE);
        residentsForEmployeeQueryApplyFromAndWhereClause(sb, filter, filterCommunityIds, isAdmin, employeeCommunityIds);
        logger.info(sb.toString());

        final TypedQuery<Long> query = entityManager.createQuery(sb.toString(), Long.class);
        residentsForEmployeeQueryApplyQueryParams(query, employeeIds, null, databaseId, filterCommunityIds, isAdmin, employeeCommunityIds);
        return query.getResultList();
    }


    public List<PatientListItemDto> getMergedResidents(final List<Long> residentIds, Long databaseId, Boolean showDeactivated) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return Collections.emptyList();
        }

        final StringBuilder queryStr = new StringBuilder("SELECT DISTINCT r.id, r.firstName, r.lastName, r.birthDate, ");
        queryStr.append("gender.displayName, r.ssnLastFourDigits, (select count(distinct ev.id) from Event ev ");
        queryStr.append("INNER JOIN ev.resident ev_resident INNER JOIN ev.eventType ev_type ");
        queryStr.append("where ev_type.service=false and (ev_resident.id = r.id or ev_resident.id in ");
        queryStr.append("(select merged_surv.survivingCCResident.id from MpiMergedResidents merged_surv ");
        queryStr.append("where merged_surv.mergedCCResident.id = r.id) or ev_resident.id in ");
        queryStr.append("(select merged_merg.mergedCCResident.id from MpiMergedResidents merged_merg ");
        queryStr.append("where merged_merg.survivingCCResident.id = r.id)) ) as ecount, facility.name, r.active, r.dateCreated ");
        queryStr.append("FROM CareCoordinationResident r INNER JOIN r.facility facility LEFT JOIN r.gender gender ");
        queryStr.append("where r.id in (:ids) and r.databaseId = :databaseId");

        if (!showDeactivated) {
            queryStr.append(" AND r.active = true ");
        }

        logger.info(queryStr.toString());

        final Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("ids", residentIds);
        query.setParameter("databaseId", databaseId);
        List<Object[]> resultSets = query.getResultList();
        List<PatientListItemDto> results = new ArrayList<>();
        for (Object[] resultSet : resultSets) {
            PatientListItemDto result = new PatientListItemDto();
            result.setId((Long) resultSet[0]);
            result.setFirstName((String) resultSet[1]);
            result.setLastName((String) resultSet[2]);
            result.setBirthDate((Date) resultSet[3]);
            result.setGender((String) resultSet[4]);
            result.setSsn("###-##-" + resultSet[5]);
            result.setEventCount((Long) resultSet[6]);
            result.setCommunity((String) resultSet[7]);
            result.setActive((Boolean) resultSet[8]);
            result.setDateCreated((Date) resultSet[9]);
            results.add(result);
        }
        return results;
    }

    public List<Long> getMergedResidentIds(final List<Long> residentIds, Long databaseId) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return Collections.emptyList();
        }

        final TypedQuery<Long> query = entityManager.createQuery("SELECT DISTINCT r.id FROM CareCoordinationResident r where r.id in (:ids) and r.databaseId = :databaseId", Long.class);
        query.setParameter("ids", residentIds);
        query.setParameter("databaseId", databaseId);
        return query.getResultList();
    }

//    private boolean isNotBlankFilter(PatientsFilterDto filter) {
//        if (filter == null) {
//            return false;
//        }
//        if (StringUtils.isNotBlank(filter.getFirstName()) || StringUtils.isNotBlank(filter.getLastName()) || StringUtils.isNotBlank(filter.getLastFourSsn()) ||
//                filter.getBirthDate()!=null || filter.getGender()!=null) {
//            return true;
//        }
//        else {
//            return false;
//        }
//    }

    @Override
    public List<KeyValueDto> getResidentNamesForEmployee(final Set<Long> employeeIds, final Long databaseId, final List<Long> communityIds, boolean isAdmin, Set<Long> employeeCommunityIds) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT r.id, r.firstName, r.lastName ");
        sb.append("FROM CareCoordinationResident r ");
        sb.append("LEFT JOIN r.residentCareTeamMembers rctm ");
        sb.append("INNER JOIN r.facility facility ");
        sb.append("LEFT JOIN facility.organizationCareTeamMembers octm ");
        sb.append("WHERE r.database.id=:databaseId ");

        if (!isAdmin) {
            sb.append("AND ( (octm.employee.id IN (:employeeIds)) OR (rctm.employee.id IN (:employeeIds)) OR (r.createdById IN (:employeeIds))");
            if (CollectionUtils.isNotEmpty(employeeCommunityIds)) {
                sb.append(" OR facility.id in (:employeeCommunityIds)");
            }
            sb.append(")");
        }

        if ((communityIds != null) && (!communityIds.isEmpty())) {
            sb.append(" AND facility.id IN (:communityIds) ");
        }

        sb.append("ORDER BY r.firstName, r.lastName ");

        logger.info(sb.toString());

        final Query query = entityManager.createQuery(sb.toString());

        if (!isAdmin) {
            query.setParameter("employeeIds", employeeIds);
        }

        if (!isAdmin && CollectionUtils.isNotEmpty(employeeCommunityIds)) {
            query.setParameter("employeeCommunityIds", employeeCommunityIds);
        }

        query.setParameter("databaseId", databaseId);

        if ((communityIds != null) && (!communityIds.isEmpty())) {
            query.setParameter("communityIds", communityIds);
        }

        List<Object[]> resultSet = query.getResultList();
        List<KeyValueDto> results = new ArrayList<KeyValueDto>();
        for (Object[] resultSetItem : resultSet) {
            Long id = (Long) resultSetItem[0];
            String firstName = (String) resultSetItem[1];
            String lastName = (String) resultSetItem[2];
            if (firstName == null) firstName = "";
            if (lastName == null) lastName = "";
            results.add(new KeyValueDto(id, firstName + " " + lastName));
        }

        return results;
    }


    @Override
    public Long getResidentsForEmployeeCount(Set<Long> employeeIds, PatientsFilterDto filter, final Long databaseId, final List<Long> filterCommunityIds, boolean isAdmin, Set<Long> employeeCommunityIds) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT count (DISTINCT r.id)  ");
        residentsForEmployeeQueryApplyFromAndWhereClause(sb, filter, filterCommunityIds, isAdmin, employeeCommunityIds);
        logger.info(sb.toString());
        final Query query = entityManager.createQuery(sb.toString());
        residentsForEmployeeQueryApplyQueryParams(query, employeeIds, filter, databaseId, filterCommunityIds, isAdmin, employeeCommunityIds);


        return (Long) query.getSingleResult();
    }

    @Override
    public List<CareCoordinationResident> findCareCoordinationResident(CareCoordinationResidentFilter filter, Organization organization) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT r FROM CareCoordinationResident r ");
        sb.append("  INNER JOIN r.facility facility ");
        sb.append("  INNER JOIN r.person person ");
        sb.append("  INNER JOIN person.names names ");
        sb.append("WHERE ");

        sb.append(" facility =              :facility");
        sb.append(" AND names.given =       :firstName");
        sb.append(" AND names.family =      :lastName");
        sb.append(" AND r.socialSecurity =  :ssn");
        sb.append(" AND r.birthDate =       :birthDate");

        final TypedQuery<CareCoordinationResident> query = entityManager.createQuery(sb.toString(), entityClass);

        query.setParameter("facility", organization);
        query.setParameter("firstName", filter.getFirstName());
        query.setParameter("lastName", filter.getLastName());
        query.setParameter("ssn", filter.getSsn());
        query.setParameter("birthDate", filter.getDateOfBirth());


        return query.getResultList();
    }


    private StringBuilder residentsForEmployeeQueryApplyFromAndWhereClause(StringBuilder sb, final PatientsFilterDto filter, final List<Long> filterCommunityIds, boolean isAdmin, Set<Long> employeeCommunityIds) {
        sb.append("FROM CareCoordinationResident r ");
        sb.append("INNER JOIN r.facility facility ");
        sb.append("LEFT JOIN r.gender gender ");

        if (!isAdmin) {
            sb.append("LEFT JOIN r.residentCareTeamMembers rctm ");
            sb.append("LEFT JOIN facility.organizationCareTeamMembers octm ");
        }

        if (filter != null && isNotBlank(filter.getPrimaryCarePhysician()) ){
            sb.append("LEFT JOIN r.residentComprehensiveAssessments comprehensiveAssessments LEFT JOIN comprehensiveAssessments.residentAssessmentResult assessmResult ");
        }

        sb.append("WHERE r.database.id=:databaseId ");

        if (filter == null || !filter.getShowDeactivated()) {
            sb.append(" AND r.active = true ");
        }

        if (!isAdmin) {
            sb.append("AND ( (octm.employee.id IN (:employeeIds)) OR (rctm.employee.id IN (:employeeIds)) OR (r.createdById IN (:employeeIds))");
            if (CollectionUtils.isNotEmpty(employeeCommunityIds)) {
                sb.append(" OR facility.id in (:employeeCommunityIds)");
            }
            sb.append(")");
        }

        if (filter != null) {
            if (isNotBlank(filter.getFirstName())) {
                sb.append(" AND r.firstName LIKE :firstName");
            }
            if (isNotBlank(filter.getLastName())) {
                sb.append(" AND r.lastName LIKE :lastName");
            }
            if (filter.getGender() != null) {
                sb.append(" AND gender.code = :gender");
            }
            if (isNotBlank(filter.getLastFourSsn())) {
                sb.append(" AND r.ssnLastFourDigits LIKE :ssnLastFourDigits");
            }
            if (filter.getBirthDate() != null) {
                sb.append(" AND r.birthDate = :birthDate");
            }
            /*if (isNotBlank(filter.getPrimaryCarePhysician())){
                sb.append(" AND ((r.primaryCarePhysician LIKE :primaryCarePhysician) " +
                        "OR ((CONCAT(comprehensiveAssessments.primaryCarePhysicianFirstName, ' ', comprehensiveAssessments.primaryCarePhysicianLastName) LIKE :primaryCarePhysician) " +
                        "OR (CONCAT(comprehensiveAssessments.primaryCarePhysicianLastName, ' ', comprehensiveAssessments.primaryCarePhysicianFirstName) LIKE :primaryCarePhysician) " +
                        "   AND assessmResult.archived = false)) ");
            }*/
            if (isNotBlank(filter.getInsuranceNetwork())){
                sb.append(" AND r.inNetworkInsurance.displayName LIKE :inNetworkInsurance");
            }
        }

        if (CollectionUtils.isNotEmpty(filterCommunityIds)) {
            sb.append(" AND facility.id IN (:communityIds) ");
        }
        return sb;
    }

    private Query residentsForEmployeeQueryApplyQueryParams(Query query, final Set<Long> employeeIds, final PatientsFilterDto filter,
                                                            final Long databaseId, final List<Long> filterCommunityIds, boolean isAdmin,
                                                            Set<Long> employeeCommunityIds) {
        if (!isAdmin) {
            query.setParameter("employeeIds", employeeIds);
            if (CollectionUtils.isNotEmpty(employeeCommunityIds)) {
                query.setParameter("employeeCommunityIds", employeeCommunityIds);
            }
        }

        if (filter != null) {
            if (isNotBlank(filter.getFirstName())) {
                query.setParameter("firstName", "%" + filter.getFirstName() + "%");
            }
            if (isNotBlank(filter.getLastName())) {
                query.setParameter("lastName", "%" + filter.getLastName() + "%");
            }
            if (filter.getGender() != null) {
                query.setParameter("gender", filter.getGender().getAdministrativeGenderCode());
            }
            if (isNotBlank(filter.getLastFourSsn())) {
                query.setParameter("ssnLastFourDigits", "%" + filter.getLastFourSsn() + "%");
            }
            if (filter.getBirthDate() != null) {
                query.setParameter("birthDate", filter.getBirthDate());
            }
            /*if (isNotBlank(filter.getPrimaryCarePhysician())){
                query.setParameter("primaryCarePhysician", "%" + filter.getPrimaryCarePhysician() + "%");
            }*/
            if (isNotBlank(filter.getInsuranceNetwork())){
                query.setParameter("inNetworkInsurance", "%" + filter.getInsuranceNetwork() + "%");
            }
        }

        query.setParameter("databaseId", databaseId);

        if (CollectionUtils.isNotEmpty(filterCommunityIds)) {
            query.setParameter("communityIds", filterCommunityIds);
        }

        return query;
    }

    private StringBuilder residentsForEmployeeQueryApplySortable(StringBuilder sb, final Pageable pageable) {
        if (pageable != null && pageable.getSort() != null) {
            final Sort sort = pageable.getSort();
            final Map<String, String> fieldByColumn = new HashMap<String, String>() {{
                put("lastName", "r.lastName");
                put("firstName", "r.firstName");
                put("gender", "gender.displayName");
                put("birthDate", "r.birthDate");
                put("dateCreated", "r.dateCreated");
                put("eventCount", "ecount");
                put("ssn", "r.ssnLastFourDigits");
                put("community", "facility.name");
            }};
            boolean multiColumnSort = false;
            // sorting orders priority is important if data is sorted by multiple columns
            for (Sort.Order order : sort) {
                String field = fieldByColumn.get(order.getProperty());
                boolean added = addSort(sb, order, field, multiColumnSort);
                multiColumnSort |= added;
            }
        }
        return sb;
    }

    @Override
    public List<CareCoordinationResident> getResidentsByData(String ssn, String email, String phone) {
        return getResidentsByData(ssn, email, phone, null, null);
    }

    @Override
    public List<CareCoordinationResident> getResidentsByData(String ssn, String email, String phone, String firstName, String lastName) {
        final StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT r FROM CareCoordinationResident r INNER JOIN r.person p LEFT JOIN p.telecoms ph ");
        queryStr.append("LEFT JOIN p.telecoms em LEFT JOIN p.names n WHERE ph.useCode='HP' AND em.useCode='EMAIL' ");
        queryStr.append("AND r.socialSecurity = :ssn AND (ph.valueNormalized=:phone OR ph.value=:phone) ");
        queryStr.append("AND (em.valueNormalized=:email OR em.value=:email) ");
        if (firstName != null) {
            queryStr.append("AND (n.givenNormalized=:firstName OR n.given=:firstName) ");
        }
        if (lastName != null) {
            queryStr.append("AND (n.familyNormalized=:lastName OR n.family=:lastName) ");
        }
        Query query = entityManager.createQuery(queryStr.toString(), CareCoordinationResident.class);
        query.setParameter("ssn", ssn);
        query.setParameter("email", email);
        query.setParameter("phone", phone);
        if (firstName != null) {
            query.setParameter("firstName", firstName);
        }
        if (lastName != null) {
            query.setParameter("lastName", lastName);
        }
        return (List<CareCoordinationResident>) query.getResultList();
    }

    public List<CareCoordinationResident> search(PatientDto patient) {
        // get the full text entity manager
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

        // create the query using Hibernate Search query DSL
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(CareCoordinationResident.class).get();

        // a very basic query by keywords
        org.apache.lucene.search.Query query = queryBuilder.bool()
                .must(queryBuilder.keyword().fuzzy().onFields("firstName").matching(patient.getFirstName()).createQuery())
                .must(queryBuilder.keyword().fuzzy().onFields("lastName").matching(patient.getLastName()).createQuery())
                .must(queryBuilder.keyword().fuzzy().onFields("socialSecurity").matching(patient.getSsn()).createQuery())
                .createQuery();

        // wrap Lucene query in an Hibernate Query object
        FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(query, CareCoordinationResident.class);

        // execute search and return results (sorted by relevance as default)
        return jpaQuery.getResultList();
    }

    public void createIndex() {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        try {
            fullTextEntityManager.createIndexer().startAndWait();
            updateMpiLog();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteIndex(Long residentId) {
        FullTextEntityManager fullTextSession = Search.getFullTextEntityManager(entityManager);
        fullTextSession.purge(CareCoordinationResident.class, residentId);
    }

    @Override
    @Transactional
    public void addToIndex(CareCoordinationResident resident) {
        FullTextEntityManager fullTextSession = Search.getFullTextEntityManager(entityManager);
        fullTextSession.index(resident);
    }

    @Override
    public boolean isFirstTimeIndexed() {
        final Query query = entityManager.createQuery("select count(ml) from MpiLog ml where lastIndexUpdated is not null");
        return ((Long) query.getSingleResult()) == 0L;
    }

    @Override
    public List<CareCoordinationResident> getLastUpdatedResidents() {
        final TypedQuery<CareCoordinationResident> query = entityManager.createQuery("Select r from CareCoordinationResident r,MpiLog ml where r.lastUpdated >= ml.lastIndexUpdated", entityClass);
        return query.getResultList();
    }

    @Override
    public void updateMpiLog() {
        final Query query = entityManager.createNativeQuery("update MPI_log set last_index_updated = GETDATE()");
        query.executeUpdate();
    }

    @Override
    public List<String> getDeletedResidentRecords() {
        final StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT deleted_record FROM DataSyncDeletedDataLog dsl, MPI_log ml where target_table_name = 'Resident' and dsl.deleted_date >= ml.last_index_updated");
        final Query query = entityManager.createNativeQuery(queryStr.toString());
        return query.getResultList();
    }

    @Override
    public List<Long> getResidentIdsCreatedByEmployeeId(Set<Long> employeeIds, Long databaseId) {
        if (CollectionUtils.isEmpty(employeeIds)) {
            return Collections.emptyList();
        }

        final TypedQuery<Long> query = (TypedQuery<Long>) entityManager.createNamedQuery("ccResident.selectId");
        query.setParameter("employeeIds", employeeIds);
        query.setParameter("databaseId", databaseId);
        return query.getResultList();
    }

    @Override
    public Long getCommunityId(Long residentId) {
        final TypedQuery<Long> query = (TypedQuery<Long>) entityManager.createNamedQuery("ccResident.selectFacilityId");
        query.setParameter("residentId", residentId);
        return query.getSingleResult();
    }

    @Override
    public Long getCreatedById(Long residentId) {
        final TypedQuery<Long> query = (TypedQuery<Long>) entityManager.createNamedQuery("ccResident.selectCreatedById");
        query.setParameter("residentId", residentId);
        return query.getSingleResult();
    }

    @Override
    public void deletePersonAddresses(Long personId) {
        final Query query =
                entityManager.createQuery("delete from PersonAddress WHERE person.id =:personId");
        query.setParameter("personId", personId);
        query.executeUpdate();
    }

    @Override
    public Long getResidentIdWithMemberId(String memberId, Long databaseId, Long communityId) {
        final TypedQuery<Long> query = entityManager.createQuery("Select r.id from CareCoordinationResident r " +
                "WHERE r.databaseId = :databaseId and r.facility.id = :communityId and r.memberNumber = :memberNumber", Long.class);
        query.setParameter("databaseId", databaseId);
        query.setParameter("communityId", communityId);
        query.setParameter("memberNumber", memberId);
        final List<Long> result = query.getResultList();
        if (CollectionUtils.isNotEmpty(result)) {
            return result.get(0);
        }
        return null;
    }

    @Override
    public Long getResidentIdWithMedicaidNumber(String medicaidNumber, Long databaseId, Long communityId) {
        final TypedQuery<Long> query = entityManager.createQuery("Select r.id from CareCoordinationResident r " +
                "WHERE r.databaseId = :databaseId and r.facility.id = :communityId and r.medicaidNumber = :medicaidNumber", Long.class);
        query.setParameter("databaseId", databaseId);
        query.setParameter("communityId", communityId);
        query.setParameter("medicaidNumber", medicaidNumber);
        final List<Long> result = query.getResultList();
        if (CollectionUtils.isNotEmpty(result)) {
            return result.get(0);
        }
        return null;

    }

    @Override
    public Long getResidentIdWithMedicareNumber(String medicareNumber, Long databaseId, Long communityId) {
        final TypedQuery<Long> query = entityManager.createQuery("Select r.id from CareCoordinationResident r " +
                "WHERE r.databaseId = :databaseId and r.facility.id = :communityId and r.medicareNumber = :medicareNumber", Long.class);
        query.setParameter("databaseId", databaseId);
        query.setParameter("communityId", communityId);
        query.setParameter("medicareNumber", medicareNumber);
        final List<Long> result = query.getResultList();
        if (CollectionUtils.isNotEmpty(result)) {
            return result.get(0);
        }
        return null;
    }

}
