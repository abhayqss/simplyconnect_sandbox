package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Medication;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

@Component
public class MedicationDaoImpl extends ResidentAwareDaoImpl<Medication> implements MedicationDao {

    public MedicationDaoImpl() {
        super(Medication.class);
    }

    @Override
    public List<Medication> listByResidentId(Long residentId) {
        return listResidentMedications(residentId, true, true);
    }

    @Override
    public List<Medication> listResidentMedications(Long residentId, boolean includeActive, boolean includeInactive) {
        return listResidentMedications(Collections.singleton(residentId), includeActive, includeInactive, null);
    }

    @Override
    public List<Medication> listResidentMedications(Collection<Long> residentIds, boolean includeActive, boolean includeInactive, Pageable pageable) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return Collections.emptyList();
        }
        if (!(includeActive || includeInactive)) {
            return Collections.emptyList();
        }

        // "LEFT JOIN FETCH m.indications i " is conditional because combined with a pagination it doesn't provide the desirable effect here.
        // When `maxResults` is specified with collection fetch, Hibernate is fetching everything (!)
        // and then trying to apply the first/max result restrictions in memory.
        // And the JPA spec says:
        // "The effect of applying setMaxResults or setFirstResult to a query involving fetch joins over collections is undefined."
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT m FROM Medication m INNER JOIN FETCH m.medicationInformation mi ");
        if(pageable == null) {
            queryStr.append("LEFT JOIN FETCH m.indications i ");
        }
        queryStr.append("WHERE m.resident.id IN :residentIds ");
        if (includeActive && includeInactive) {
            // do nothing
        } else if (includeActive) {
            queryStr.append(" AND (m.medicationStarted < :currentDate) AND ((m.medicationStopped IS NULL) or (m.medicationStopped > :currentDate)) ");
        } else if (includeInactive) {
            queryStr.append(" AND (m.medicationStopped <= :currentDate) ");
        } else {
            return new ArrayList<Medication>();
        }
        TypedQuery<Medication> query = entityManager.createQuery(queryStr.toString(), Medication.class);
        query.setParameter("residentIds", residentIds);
        if (!(includeActive && includeInactive)) {
            query.setParameter("currentDate", new Date());
        }
        applyPageable(query, pageable);
        if (pageable != null && pageable.getSort() != null) {
            // TODO apply sort
        }

        return query.getResultList();
    }

    @Override
    public Long countResidentMedications(Collection<Long> residentIds, boolean includeActive, boolean includeInactive) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return 0L;
        }
        if (!(includeActive || includeInactive)) {
            return 0L;
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Medication> root = query.from(Medication.class);
        Expression<Boolean> condition = where(cb, root, residentIds, includeActive, includeInactive);
        query = query.select(cb.count(root)).where(condition);

        TypedQuery<Long> queryCount = entityManager.createQuery(query);
        return queryCount.getSingleResult();
    }

    private static Expression<Boolean> where(CriteriaBuilder cb, Root<Medication> root, Collection<Long> residentIds, boolean includeActive, boolean includeInactive) {
        Path<Object> residentIdColumn = root.get("resident").get("id");
        final Date now = new Date();
        Expression<Boolean> condition;
        if (includeActive && includeInactive) {
            condition = residentIdColumn.in(residentIds);
        } else if (includeActive) {
            Expression<Boolean> isActive = cb.and(cb.lessThan(root.<Date>get("medicationStarted"), now),
                    cb.or(root.get("medicationStopped").isNull(), cb.greaterThan(root.<Date>get("medicationStopped"), now)));
            condition = cb.and(residentIdColumn.in(residentIds), isActive);
        } else if (includeInactive) {
            Expression<Boolean> isInactive = cb.lessThanOrEqualTo(root.<Date>get("medicationStopped"), now);
            condition = cb.and(residentIdColumn.in(residentIds), isInactive);
        } else {
            // A disjunction with zero disjuncts is false.
            return cb.disjunction();
        }

        return condition;
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (Medication medication : this.listByResidentId(residentId)) {
            this.delete(medication);
            ++count;
        }

        return count;
    }

}
