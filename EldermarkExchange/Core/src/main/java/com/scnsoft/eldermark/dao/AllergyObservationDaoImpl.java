package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.AllergyObservation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author phomal
 * Created on 9/15/2017.
 */
@Repository
public class AllergyObservationDaoImpl extends BaseDaoImpl<AllergyObservation> implements AllergyObservationDao {

    public AllergyObservationDaoImpl() {
        super(AllergyObservation.class);
    }

    @Override
    public List<AllergyObservation> listByResidentId(Long residentId) {
        return listResidentAllergies(residentId, true, true, true);
    }

    @Override
    public Long countByResidentIds(Collection<Long> residentIds) {
        return countResidentAllergies(residentIds, true, true, true);
    }

    @Override
    public List<AllergyObservation> listResidentAllergies(Long residentId, boolean includeActive, boolean includeInactive, boolean includeResolved) {
        return listResidentAllergies(Collections.singleton(residentId), true, true, true, null);
    }

    @Override
    public List<AllergyObservation> listResidentAllergies(Collection<Long> residentIds, boolean includeActive, boolean includeInactive, boolean includeResolved,
                                                          Pageable pageable) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return Collections.emptyList();
        }
        if (!(includeActive || includeInactive || includeResolved)) {
            return Collections.emptyList();
        }

        final boolean isPaged = pageable != null;
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT ao FROM AllergyObservation ao INNER JOIN FETCH ao.allergy a ");
        if (!isPaged) {
            queryStr.append(" LEFT JOIN FETCH ao.reactionObservations ro");
        }
        queryStr.append(" LEFT JOIN ao.observationStatusCode status ");
        queryStr.append(where(includeActive, includeInactive, includeResolved, true));

        queryStr.append(" ORDER BY ao.productText ASC");

        TypedQuery<AllergyObservation> query = entityManager.createQuery(queryStr.toString(), AllergyObservation.class);
        query.setParameter("residentIds", residentIds);
        if (!includeInactive) {
            query.setParameter("currentDate", new Date());
        }
        applyPageable(query, pageable);

        return query.getResultList();
    }

    @Override
    public Long countResidentAllergies(Collection<Long> residentIds, boolean includeActive, boolean includeInactive, boolean includeResolved) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return 0L;
        }
        if (!(includeActive || includeInactive || includeResolved)) {
            return 0L;
        }
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT count(ao) FROM AllergyObservation ao INNER JOIN ao.allergy a LEFT JOIN ao.observationStatusCode status ");
        queryStr.append(where(includeActive, includeInactive, includeResolved, true));

        TypedQuery<Long> query = entityManager.createQuery(queryStr.toString(), Long.class);
        query.setParameter("residentIds", residentIds);
        if (!includeInactive) {
            query.setParameter("currentDate", new Date());
        }

        return query.getSingleResult();
    }

    private static String where(boolean includeActive, boolean includeInactive, boolean includeResolved, boolean excludeDuplicates) {
        String whereStr = " WHERE " + (!excludeDuplicates ? "a.resident.id IN :residentIds " : "ao.id IN (" +
                "SELECT min(ao.id) FROM AllergyObservation ao " +
                "INNER JOIN ao.allergy a " +
                "LEFT JOIN ao.reactionObservations ro " +
                "WHERE a.resident.id IN :residentIds " +
                "GROUP BY ao.productText, ao.observationStatusCode, ro.reactionText) ");

        if (includeActive && includeInactive && includeResolved) {
            // do nothing
        } else if (includeActive) {
            whereStr += " AND ((lower(status.displayName) = 'active') OR ((a.timeLow < :currentDate) AND ((a.timeHigh IS NULL) OR (a.timeHigh > :currentDate)))) ";
        } else if (includeResolved) {
            whereStr += " AND ((lower(status.displayName) = 'resolved') OR (a.timeHigh <= :currentDate)) ";
        } else if (includeInactive) {
            whereStr += " AND (lower(status.displayName) = 'inactive')";
        } else {
            whereStr += " AND 1=0";
        }

        return whereStr;
    }

}
