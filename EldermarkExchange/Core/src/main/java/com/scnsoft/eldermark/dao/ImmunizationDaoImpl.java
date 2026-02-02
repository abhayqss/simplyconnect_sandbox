package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Immunization;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
public class ImmunizationDaoImpl extends ResidentAwareDaoImpl<Immunization> implements ImmunizationDao {

    public ImmunizationDaoImpl() {
        super(Immunization.class);
    }

    @Override
    public List<Immunization> listResidentImmunizations(Collection<Long> residentIds, Pageable pageable) {
        if (CollectionUtils.isEmpty(residentIds)) {
            return Collections.emptyList();
        }

        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT i FROM Immunization i INNER JOIN FETCH i.immunizationMedicationInformation imi ");
        queryStr.append("LEFT JOIN FETCH i.reactionObservation ro LEFT JOIN FETCH i.immunizationRefusalReason irr ");
        queryStr.append("LEFT JOIN FETCH i.instructions instructions LEFT JOIN FETCH i.site site LEFT JOIN FETCH i.route route ");
        if (pageable == null) {
            queryStr.append("LEFT JOIN FETCH i.indications");
        }
        queryStr.append(" WHERE i.resident.id IN :residentIds ORDER BY i.immunizationStarted DESC, imi.text ASC");

        TypedQuery<Immunization> query = entityManager.createQuery(queryStr.toString(), Immunization.class);
        query.setParameter("residentIds", residentIds);
        applyPageable(query, pageable);

        return query.getResultList();
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (Immunization immunization : this.listByResidentId(residentId)) {
            this.delete(immunization);
            ++count;
        }

        return count;
    }

}