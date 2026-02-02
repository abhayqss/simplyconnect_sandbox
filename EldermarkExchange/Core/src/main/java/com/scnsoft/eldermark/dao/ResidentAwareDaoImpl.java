package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.BasicEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

import static com.scnsoft.eldermark.dao.dialect.SqlServerCustomDialect.MSSQL_WHERE_IN_PARAM_LIMIT;

/**
 * Base class for the Database Access Objects that handle the reading and removal a class from the database
 * by a corresponding {@link com.scnsoft.eldermark.entity.Resident}.
 *
 * @author phomal
 * Created on 2/13/2017.
 */
public class ResidentAwareDaoImpl<T extends BasicEntity> extends BaseDaoImpl<T> implements ResidentAwareDao<T> {
    private static final Logger logger = LoggerFactory.getLogger(ResidentAwareDaoImpl.class);

    public ResidentAwareDaoImpl(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public List<T> listByResidentId(Long residentId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(entityClass);
        Root<T> root = query.from(entityClass);
        ParameterExpression<Long> id = cb.parameter(Long.class);
        query.select(root).where(cb.equal(root.get("resident").get("id"), id));

        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        typedQuery.setParameter(id, residentId);
        return typedQuery.getResultList();
    }

    @Override
    public Collection<T> listByResidentId(Long residentId, boolean aggregated) {
        if (!aggregated) {
            return new HashSet<T>(listByResidentId(residentId));
        } else {
            StoredProcedureQuery findMergedPatientsProc = entityManager.createStoredProcedureQuery("find_merged_patients");
            findMergedPatientsProc.registerStoredProcedureParameter("residentId", Long.class, ParameterMode.IN);
            findMergedPatientsProc.setParameter("residentId", residentId);
            findMergedPatientsProc.execute();
            List<Long> residents = findMergedPatientsProc.getResultList();

            return listByResidentIds(residents);
        }
    }

    @Override
    public Long countByResidentIds(Collection<Long> residentIds) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return 0L;
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> root = query.from(entityClass);
        Path<Object> residentIdColumn = root.get("resident").get("id");
        query = query.select(cb.count(root)).where(residentIdColumn.in(residentIds));

        TypedQuery<Long> queryCount = entityManager.createQuery(query);

        return queryCount.getSingleResult();
    }

    @Override
    public final Collection<T> listByResidentIds(List<Long> residents) {
        return listByResidentIds(residents, null);
    }

    @Override
    public Collection<T> listByResidentIds(List<Long> residents, Pageable pageable) {
        if (CollectionUtils.isEmpty(residents)) {
            return Collections.emptySet();
        }

        if (residents.size() > MSSQL_WHERE_IN_PARAM_LIMIT) {
            logger.warn("Dialect [com.scnsoft.eldermark.dao.dialect.SqlServerCustomDialect] limits the number of elements in an IN predicate " +
                    "to " + MSSQL_WHERE_IN_PARAM_LIMIT + " entries. However, the given parameter list [residents] contains " + residents.size() +
                    " entries, which will likely cause failures to execute the query in the database. So the number of parameters is reduced " +
                    "to " + MSSQL_WHERE_IN_PARAM_LIMIT + ".");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(entityClass);
        Root<T> root = query.from(entityClass);
        Path<Object> residentIdColumn = root.get("resident").get("id");
        // TODO create batch requests when residents list contains more than 2000 ids (if there's ever such case)
        query.select(root).where(residentIdColumn.in(residents.subList(0, Math.min(residents.size(), MSSQL_WHERE_IN_PARAM_LIMIT))));

        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        applyPageable(typedQuery, pageable);
        return new HashSet<T>(typedQuery.getResultList());
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<T> delete = cb.createCriteriaDelete(entityClass);
        Root<T> root = delete.from(entityClass);
        delete.where(cb.equal(root.get("resident").get("id"), residentId));

        Query query = entityManager.createQuery(delete);
        return query.executeUpdate();
    }
}
