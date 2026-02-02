package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Allergy;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Repository
public class AllergyDaoImpl extends ResidentAwareDaoImpl<Allergy> implements AllergyDao {

    public AllergyDaoImpl() {
        super(Allergy.class);
    }


    @Override
    public List<Allergy> listByResidentId(Long residentId) {
        return listResidentAllergies(residentId, true, true, true);
    }

    @Override
    public List<Allergy> listResidentAllergies(Long residentId, boolean includeActive, boolean includeInactive, boolean includeResolved) {
        return listResidentAllergies(Collections.singleton(residentId), includeActive, includeInactive, includeResolved, null);
    }

    @Override
    public List<Allergy> listResidentAllergies(Collection<Long> residentIds, boolean includeActive, boolean includeInactive, boolean includeResolved, Pageable pageable) {
        final boolean isPaged = pageable != null;
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("select a from Allergy a INNER JOIN ");
        if(isPaged){
            queryStr.append("a.allergyObservations ao");
        } else{
            queryStr.append("FETCH a.allergyObservations ao LEFT JOIN FETCH ao.reactionObservations ro");
        }
        queryStr.append(" LEFT JOIN ao.observationStatusCode status WHERE a.resident.id IN :residentIds");
        if (includeActive && includeInactive && includeResolved) {
            // do nothing
        } else if (includeActive) {
            queryStr.append(" AND ((lower(status.displayName) = 'active') OR ((a.timeLow < :currentDate) AND ((a.timeHigh IS NULL) OR (a.timeHigh > :currentDate)))) ");
        } else if (includeResolved) {
            queryStr.append(" AND ((lower(status.displayName) = 'resolved') OR (a.timeHigh <= :currentDate)) ");
        } else if (includeInactive) {
            queryStr.append(" AND (lower(status.displayName) = 'inactive')");
        } else {
            return Collections.emptyList();
        }

        queryStr.append(" ORDER BY ao.productText ASC");

        TypedQuery<Allergy> query = entityManager.createQuery(queryStr.toString(), Allergy.class);
        query.setParameter("residentIds", residentIds);
        if (!includeInactive) {
            query.setParameter("currentDate", new Date());
        }
        if (pageable != null) {
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        return query.getResultList();
    }

    @Override
    public Long countResidentAllergies(Collection<Long> residentIds, boolean includeActive, boolean includeInactive, boolean includeResolved) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return 0L;
        }
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("select count(a) from Allergy a LEFT JOIN a.allergyObservations ao LEFT JOIN ao.observationStatusCode status WHERE a.resident.id IN :residentIds");
        if (includeActive && includeInactive && includeResolved) {
            // do nothing
        } else if (includeActive) {
            queryStr.append(" AND ((lower(status.displayName) = 'active') OR ((a.timeLow < :currentDate) AND ((a.timeHigh IS NULL) OR (a.timeHigh > :currentDate)))) ");
        } else if (includeResolved) {
            queryStr.append(" AND ((lower(status.displayName) = 'resolved') OR (a.timeHigh <= :currentDate)) ");
        } else if (includeInactive) {
            queryStr.append(" AND (lower(status.displayName) = 'inactive')");
        } else {
            return 0L;
        }
        TypedQuery<Long> query = entityManager.createQuery(queryStr.toString(), Long.class);
        query.setParameter("residentIds", residentIds);
        if (!includeInactive) {
            query.setParameter("currentDate", new Date());
        }

        return query.getSingleResult();
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (Allergy allergy : this.listByResidentId(residentId)) {
            this.delete(allergy);
            ++count;
        }

        return count;
    }

}
