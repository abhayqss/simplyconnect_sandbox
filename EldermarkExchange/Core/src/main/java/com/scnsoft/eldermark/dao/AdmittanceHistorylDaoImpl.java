package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.AdmittanceHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class AdmittanceHistorylDaoImpl extends ResidentAwareDaoImpl<AdmittanceHistory> implements AdmittanceHistoryDao {

    public AdmittanceHistorylDaoImpl() {
        super(AdmittanceHistory.class);
    }

    @Override
    public List<AdmittanceHistory> listByResidentId(Long residentId) {
        TypedQuery<AdmittanceHistory> query = entityManager.createQuery(
                "select a from AdmittanceHistory a join a.resident res where res.id = :residentId order by a.id desc",
                AdmittanceHistory.class);
        query.setParameter("residentId", residentId);
        return query.getResultList();
    }

    @Override
    public List<AdmittanceHistory> listByResidentIds(List<Long> residentIds, Pageable pageable) {
        TypedQuery<AdmittanceHistory> query = entityManager.createQuery(
                "select a from AdmittanceHistory a join a.resident res where res.id IN :residentIds order by a.id desc",
                AdmittanceHistory.class);
        query.setParameter("residentIds", residentIds);
        applyPageable(query, pageable);

        return query.getResultList();
    }
}
