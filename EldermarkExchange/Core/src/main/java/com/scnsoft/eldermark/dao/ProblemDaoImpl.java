package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Problem;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Repository
public class ProblemDaoImpl extends ResidentAwareDaoImpl<Problem> implements ProblemDao {

    public ProblemDaoImpl() {
        super(Problem.class);
    }

    @Override
    public List<Problem> listByResidentId(Long residentId) {
        return listResidentProblems(Collections.singleton(residentId), true, true, true, null);
    }

    public List<Problem> listResidentProblems(Long residentId, boolean includeActive, boolean includeInactive, boolean includeOther) {
        return listResidentProblems(Collections.singleton(residentId), includeActive, includeInactive, includeOther, null);
    }

    @Override
    public List<Problem> listResidentProblems(Collection<Long> residentIds, boolean includeActive, boolean includeInactive, boolean includeOther, Pageable pageable) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return Collections.emptyList();
        }

        String queryStr = "SELECT p FROM Problem p WHERE p.resident.id IN :residentIds ";
        if (includeActive && includeInactive && includeOther) {
            // do nothing
        } else if (includeActive) {
            queryStr += " AND ((lower(statusCode) = 'active') OR ((statusCode IS NULL) AND (timeLow < :currentDate) AND (timeHigh IS NULL OR timeHigh > :currentDate))) ";
        } else if (includeInactive) {
            queryStr += " AND ((lower(statusCode) = 'completed') OR (statusCode IS NULL AND timeHigh < :currentDate)) ";
        } else if (includeOther) {
            queryStr += " AND NOT ((statusCode IS NULL) AND (timeLow < :currentDate) AND (timeHigh IS NULL OR timeHigh > :currentDate)) AND NOT (statusCode IS NULL AND timeHigh < :currentDate)" +
                    " AND NOT (lower(statusCode) = 'active' OR lower(statusCode) = 'completed') ";
        } else {
            return Collections.emptyList();
        }
        queryStr += " ORDER BY p.timeLow DESC";

        TypedQuery<Problem> query = entityManager.createQuery(queryStr, Problem.class);
        query.setParameter("residentIds", residentIds);
        if (!(includeActive && includeInactive && includeOther)) {
            query.setParameter("currentDate", new Date());
        }
        applyPageable(query, pageable);

        return query.getResultList();
    }

    @Override
    public Long countResidentProblems(Collection<Long> residentIds, boolean includeActive, boolean includeInactive, boolean includeOther) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return 0L;
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Problem> root = query.from(Problem.class);
        Path<Object> residentIdColumn = root.get("resident").get("id");
        final Date now = new Date();

        // ((lower(statusCode) = 'active') OR ((statusCode IS NULL) AND (timeLow < :currentDate) AND (timeHigh IS NULL OR timeHigh > :currentDate)))
        Expression<Boolean> isActive = cb.or(
                cb.equal(cb.lower(root.<String>get("statusCode")), "active"),
                cb.and(
                        cb.and(cb.isNull(root.get("statusCode")), cb.lessThan(root.<Date>get("timeLow"), now)),
                        cb.or(root.get("timeHigh").isNull(), cb.greaterThan(root.<Date>get("timeHigh"), now))
                )
        );
        // ((lower(statusCode) = 'completed') OR (statusCode IS NULL AND timeHigh < :currentDate))
        Expression<Boolean> isInactive = cb.or(
                cb.equal(cb.lower(root.<String>get("statusCode")), "completed"),
                cb.and(cb.isNull(root.get("statusCode")), cb.lessThanOrEqualTo(root.<Date>get("timeHigh"), now))
        );

        Expression<Boolean> condition;
        if (includeActive && includeInactive && includeOther) {
            condition = residentIdColumn.in(residentIds);
        } else if (includeActive) {
            condition = cb.and(residentIdColumn.in(residentIds), isActive);
        } else if (includeInactive) {
            condition = cb.and(residentIdColumn.in(residentIds), isInactive);
        } else if (includeOther) {
            Expression<Boolean> isOther = cb.not(cb.or(isActive, isInactive));
            condition = cb.and(residentIdColumn.in(residentIds), isOther);
        } else {
            return 0L;
        }
        query = query.select(cb.count(root)).where(condition);

        TypedQuery<Long> queryCount = entityManager.createQuery(query);
        return queryCount.getSingleResult();
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (Problem problem : this.listByResidentId(residentId)) {
            this.delete(problem);
            ++count;
        }

        return count;
    }

}
