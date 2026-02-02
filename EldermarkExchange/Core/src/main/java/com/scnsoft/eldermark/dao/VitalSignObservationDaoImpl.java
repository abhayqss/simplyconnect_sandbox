package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.VitalSignObservation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.Collection;

/**
 * @author phomal
 * Created on 9/18/2017.
 */
@Repository
public class VitalSignObservationDaoImpl extends BaseDaoImpl<VitalSignObservation> implements VitalSignObservationDao {

    public VitalSignObservationDaoImpl() {
        super(VitalSignObservation.class);
    }

    @Override
    public Long countByResidentIds(Collection<Long> residentIds) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return 0L;
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<VitalSignObservation> root = query.from(entityClass);
        final Join<Object, Object> vitalSign = root.join("vitalSign");
        Path<Object> residentIdColumn = vitalSign.get("resident").get("id");
        query = query.select(cb.count(root)).where(residentIdColumn.in(residentIds));

        TypedQuery<Long> queryCount = entityManager.createQuery(query);

        return queryCount.getSingleResult();
    }

}
