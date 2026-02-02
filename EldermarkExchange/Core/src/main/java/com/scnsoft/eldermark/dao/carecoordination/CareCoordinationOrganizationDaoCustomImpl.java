package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.AffiliatedOrganizations;
import com.scnsoft.eldermark.shared.carecoordination.AffiliatedOrgItemDto;
import com.scnsoft.eldermark.shared.carecoordination.AffiliatedOrganizationDto;
import com.scnsoft.eldermark.shared.carecoordination.OrganizationFilterDto;
import com.scnsoft.eldermark.shared.carecoordination.OrganizationListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by averazub on 6/24/2016.
 */
@Component
public class CareCoordinationOrganizationDaoCustomImpl {


    @PersistenceContext
    protected EntityManager entityManager;

    public List<Pair<Long, String>> getBriefList() {
        final Query query = entityManager.createQuery("Select db.id, db.name from Database db order by name");
        List<Object[]> resultSet = query.getResultList();
        List<Pair<Long, String>> results = new ArrayList<Pair<Long, String>>();
        for (Object[] item : resultSet) {
            results.add(new Pair<Long, String>((Long) item[0], (String) item[1]));
        }
        return results;
    }

    public Long getCount() {
        final TypedQuery<Long> query = entityManager.createQuery("Select count(db.id) from Database db", Long.class);
        return query.getSingleResult();
    }

    public List<Pair<Long, String>> getPrimaryBriefList(Set<Long> databaseIds) {
        final Query query = entityManager.createNamedQuery("db.getPrimaryBriefList");
        query.setParameter("databaseIds", databaseIds);
        List<Object[]> resultSet = query.getResultList();
        List<Pair<Long, String>> results = new ArrayList<Pair<Long, String>>();
        for (Object[] item : resultSet) {
            results.add(new Pair<Long, String>((Long) item[0], (String) item[1]));
        }
        return results;
    }

    public List<Pair<Long, String>> getAffiliatedBriefList(Set<Long> databaseIds) {
        final Query query = entityManager.createNamedQuery("db.getAffiliatedBriefList");
        query.setParameter("databaseIds", databaseIds);
        List<Object[]> resultSet = query.getResultList();
        List<Pair<Long, String>> results = new ArrayList<Pair<Long, String>>();
        for (Object[] item : resultSet) {
            results.add(new Pair<Long, String>((Long) item[0], (String) item[1]));
        }
        return results;
    }

    public Long getPrimaryCount(Set<Long> employeeDbIds) {
        final TypedQuery<Long> query = entityManager.createNamedQuery("db.getPrimaryCount", Long.class);
        query.setParameter("employeeDbIds", employeeDbIds);
        return query.getSingleResult();
    }

    public boolean checkDatabaseAccess(long databaseId, Set<Long> employeeDbIds) {
        final TypedQuery<Long> query = entityManager.createNamedQuery("db.checkDatabaseAccess", Long.class);
        query.setParameter("databaseId", databaseId);
        query.setParameter("employeeDbIds", employeeDbIds);
        return query.getSingleResult()>0;
    }


    public List<OrganizationListItemDto> getOrganizationsList(final OrganizationFilterDto filter, Pageable pageable, boolean isEldermarkUser, boolean isCloudUser) {
        final StringBuilder sb = new StringBuilder();
        sb.append("select db.id, db.name, db.last_modified, ").append(getOrgCountField(isEldermarkUser, isCloudUser))
                .append(", doc.affiliated_org_count, db.created_automatically").append(" from SourceDatabase db ")
                .append(" left outer join database_org_count doc on doc.database_id = db.id ");

        applyOrganizationListWhereClause(sb, filter);
        applyOrganizationListSortable(sb, pageable, isEldermarkUser, isCloudUser);

        final Query query = entityManager.createNativeQuery(sb.toString());

        applyOrganizationListQueryParams(query, filter);

        if (pageable != null) {
            query.setMaxResults(pageable.getPageSize());
            query.setFirstResult(pageable.getOffset());
        }

        List<Object[]> resultSets = query.getResultList();
        List<OrganizationListItemDto> results = new ArrayList<OrganizationListItemDto>();
        for (Object[] resultSet : resultSets) {
            OrganizationListItemDto result = new OrganizationListItemDto();
            Long databaseId = ((BigInteger) resultSet[0]).longValue();
            result.setId(databaseId);
            result.setName((String) resultSet[1]);
            result.setLastModified((Date) resultSet[2]);
            Integer count = (Integer) resultSet[3];
            result.setCommunityCount(count == null ? 0 : count);
            Integer affiliatedCount = (Integer)resultSet[4];
            result.setAffilatedCount(affiliatedCount);
            if (affiliatedCount != null  && affiliatedCount > 0) {
                result.setAffiliatedOrgItems(getAffiliatedOrgs(databaseId));
            }
            result.setCreatedAutomatically((Boolean)resultSet[5]);
            results.add(result);
        }
        return results;
    }

    private List<AffiliatedOrgItemDto> getAffiliatedOrgs(Long primaryDatabaseId) {
        final StringBuilder sb = new StringBuilder();
        sb.append("select distinct db.id, db.name from AffiliatedOrganizations ao join SourceDatabase db on db.id = ao.affiliated_database_id where ao.primary_database_id = :primaryDbId");
        final Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("primaryDbId", primaryDatabaseId);
        List<Object[]> resultSets = query.getResultList();
        List<AffiliatedOrgItemDto> results = new ArrayList<AffiliatedOrgItemDto>();
        for (Object[] resultSet : resultSets) {
            AffiliatedOrgItemDto result = new AffiliatedOrgItemDto();
            Long databaseId = ((BigInteger) resultSet[0]).longValue();
            result.setId(databaseId);
            result.setName((String) resultSet[1]);
            results.add(result);
        }
        return results;
    }

    public List<AffiliatedOrganizationDto> getAffiliatedOrganizationsInfo(Long primaryDatabaseId) {
        return  getAffiliatedOrganizationsInfo(primaryDatabaseId, null,  AffiliatedRequestType.AFFILIATED);
    }

    public List<AffiliatedOrganizationDto> getPrimaryOrganizationsInfo(Long affiliatedDatabaseId) {
        return  getAffiliatedOrganizationsInfo(null, affiliatedDatabaseId, AffiliatedRequestType.PRIMARY);
    }

    public List<AffiliatedOrganizationDto> getPrimaryAffiliatedOrganizationInfo(Long primaryDatabaseId, Long affiliatedDatabaseId) {
        return  getAffiliatedOrganizationsInfo(primaryDatabaseId, affiliatedDatabaseId, AffiliatedRequestType.BOTH);
    }

    private List<AffiliatedOrganizationDto> getAffiliatedOrganizationsInfo(Long primaryDatabaseId, Long affiliatedDatabasebid, AffiliatedRequestType requestType) {
        final StringBuilder sb = new StringBuilder();
        sb.append("select distinct dbprimary.name as pr_org_name, dbaffiliated.name as af_org_name, communityprimary.name as pr_comm_name, communityaffiliated.name as af_comm_name," +
                "dbprimary.id as pr_org_id, dbaffiliated.id as af_org_id, communityprimary.id as pr_comm_id,  communityaffiliated.id as af_comm_id from AffiliatedOrganizations ao ");
        sb.append("join SourceDatabase dbprimary on dbprimary.id = ao.primary_database_id ");
        sb.append("join SourceDatabase dbaffiliated on dbaffiliated.id = ao.affiliated_database_id ");
        sb.append("left join Organization communityprimary on communityprimary.id = ao.primary_organization_id ");
        sb.append("left join Organization communityaffiliated on communityaffiliated.id = ao.affiliated_organization_id ");
        switch (requestType) {
            case PRIMARY: {
                sb.append("where ao.affiliated_database_id = :afdbId");
                break;
            }
            case AFFILIATED: {
                sb.append("where ao.primary_database_id = :prdbId");
                break;
            }
            case BOTH: {
                sb.append("where ao.affiliated_database_id = :afdbId");
                sb.append(" and ");
                sb.append("ao.primary_database_id = :prdbId");
                break;
            }
        }
        final Query query = entityManager.createNativeQuery(sb.toString());
        switch (requestType) {
            case PRIMARY: {
                query.setParameter("afdbId", affiliatedDatabasebid);
                break;
            }
            case AFFILIATED: {
                query.setParameter("prdbId", primaryDatabaseId);
                break;
            }
            case BOTH: {
                query.setParameter("afdbId", affiliatedDatabasebid);
                query.setParameter("prdbId", primaryDatabaseId);
                break;
            }
        }
        List<Object[]> resultSets = query.getResultList();
        List<AffiliatedOrganizationDto> results = new ArrayList<AffiliatedOrganizationDto>();
        for (Object[] resultSet : resultSets) {
            AffiliatedOrganizationDto result = new AffiliatedOrganizationDto();
            result.setPrimaryOrganizationName((String)resultSet[0]);
            result.setAffiliatedOrganizationName((String) resultSet[1]);
            result.setPrimaryCommunityName((String) resultSet[2]);
            result.setAffiliatedCommunityName((String) resultSet[3]);
            result.setPrimaryOrganizationId(resultSet[4] != null ? ((BigInteger) resultSet[4]).longValue() : null);
            result.setAffiliatedOrganizationId(resultSet[5] != null ? ((BigInteger) resultSet[5]).longValue() : null);
            result.setPrimaryCommunityId(resultSet[6] != null ? ((BigInteger) resultSet[6]).longValue() : null);
            result.setAffiliatedCommunityId(resultSet[7] != null ? ((BigInteger) resultSet[7]).longValue() : null);
            results.add(result);
        }
        return results;
    }


    public Integer getOrganizationsCount(final OrganizationFilterDto filter) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT count(*) from SourceDatabase db  ");
        applyOrganizationListWhereClause(sb, filter);
        final Query query = entityManager.createNativeQuery(sb.toString());
        applyOrganizationListQueryParams(query, filter);
        return (Integer) query.getSingleResult();
    }

    private void applyOrganizationListWhereClause(StringBuilder sb, OrganizationFilterDto filter) {
        if (filter != null) {
            boolean firstClause = true;
            if (filter.getLoginCompanyId() != null) {
                sb.append("left outer join  SystemSetup as ss on ss.database_id = db.id ");
                firstClause = checkClause(sb, firstClause);
                sb.append("ss.login_company_is = :loginCompanyId");
            }
            if (StringUtils.isNotBlank(filter.getName())) {
                firstClause = checkClause(sb, firstClause);
                sb.append("db.name like :name");
            }
            if (filter.getId() != null) {
                firstClause = checkClause(sb, firstClause);
                sb.append("db.id = :id");
            }
            if (filter.getIsService() != null) {
                checkClause(sb, firstClause);
                sb.append("db.is_service = :isService");
            }
        }
    }

    private boolean checkClause(StringBuilder sb, boolean firstClause) {
        if (firstClause) {
            sb.append(" where ");
            firstClause = false;
        } else {
            sb.append(" and ");
        }
        return firstClause;
    }

    private void applyOrganizationListSortable(StringBuilder sb, Pageable pageable, boolean isEldermarkUser, boolean isCloudUser) {
        boolean added = false;
        if (pageable != null && pageable.getSort() != null) {
            final Sort sort = pageable.getSort();
            added = checkAddSort(sb, sort, "systemSetup.loginCompanyId", "loginCompanyId");
            if (!added) {
                if (sort.getOrderFor("communityCount") != null) {
                    if (isEldermarkUser && isCloudUser) {
                        addSort(sb, sort, "doc.org_hie_or_cloud_count", "communityCount");
                    } else if (isEldermarkUser) {
                        addSort(sb, sort, "doc.org_hie_count", "communityCount");
                    } else if (isCloudUser) {
                        addSort(sb, sort, "doc.org_cloud_count", "communityCount");
                    } else {
                        addSort(sb, sort, "doc.org_count", "communityCount");
                    }
                    added = true;
                }
            }
            if (!added) {
                added = checkAddSort(sb, sort, "db.name", "name");
            }
            if (!added) {
                added = checkAddSort(sb, sort, "db.last_modified", "lastModified");
            }
            if (!added) {
                added = checkAddSort(sb, sort, "doc.affiliated_org_count", "affilatedCount");
            }
            if (!added) {
                added = checkAddSort(sb, sort, "db.created_automatically", "createdAutomatically");
            }
        }
        if (!added) {
            sb.append(" ORDER BY db.name");
        }
    }

    private void applyOrganizationListQueryParams(Query query, OrganizationFilterDto filter) {
        if (filter != null) {
            if (filter.getLoginCompanyId() != null) {
                query.setParameter("loginCompanyId", filter.getLoginCompanyId());
            }
            if (StringUtils.isNotBlank(filter.getName())) {
                query.setParameter("name", "%" + filter.getName() + "%");
            }
            if (filter.getId() != null) {
                query.setParameter("id", filter.getId());
            }
            if (filter.getIsService() != null) {
                query.setParameter("isService", filter.getIsService());
            }
        }
    }

    private StringBuilder getOrgCountField(boolean isEldermarkUser, boolean isCloudUser) {
        final StringBuilder sb = new StringBuilder();
        if (isEldermarkUser && isCloudUser) {
            sb.append("doc.org_hie_or_cloud_count ");
        } else if (isEldermarkUser) {
            sb.append("doc.org_hie_count ");
        } else if (isCloudUser) {
            sb.append("doc.org_cloud_count ");
        }
        return sb;
    }

    private static boolean checkAddSort(StringBuilder sb, Sort sort, String field, String column) {
        if (sort.getOrderFor(column) != null) {
            addSort(sb, sort, field, column);
            return true;
        }
        return false;
    }

    private static void addSort(StringBuilder sb, Sort sort, String field, String column) {
        sb.append(" ORDER BY ");
        sb.append(field);
        sb.append(" ");
        sb.append(sort.getOrderFor(column).getDirection());
    }

    public void addAffiliatedDetails(AffiliatedOrganizations affiliatedOrganizations) {
        entityManager.persist(affiliatedOrganizations);
    }

    private enum AffiliatedRequestType {
        PRIMARY,
        AFFILIATED,
        BOTH
    }
}
