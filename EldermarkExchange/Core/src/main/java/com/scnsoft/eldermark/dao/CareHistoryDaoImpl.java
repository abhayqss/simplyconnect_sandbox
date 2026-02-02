package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.CareHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class CareHistoryDaoImpl extends ResidentAwareDaoImpl<CareHistory> implements CareHistoryDao {

    public CareHistoryDaoImpl() {
        super(CareHistory.class);
    }

    @Override
    public List<CareHistory> listByResidentId(Long residentId) {
        TypedQuery<CareHistory> query = entityManager.createQuery(
                "select p from CareHistory p join p.resident res where res.id = :residentId and p.endDate is null order by p.startDate desc",
                CareHistory.class);
        query.setParameter("residentId", residentId);
        return query.getResultList();
    }

    @Override
    public List<CareHistory> listByResidentIds(List<Long> residentIds, Pageable pageable) {
        TypedQuery<CareHistory> query = entityManager.createQuery(
                "select p from CareHistory p join p.resident res where res.id IN :residentIds and p.endDate is null order by p.startDate desc",
                CareHistory.class);
        query.setParameter("residentIds", residentIds);
        applyPageable(query, pageable);

        return query.getResultList();
    }

}
