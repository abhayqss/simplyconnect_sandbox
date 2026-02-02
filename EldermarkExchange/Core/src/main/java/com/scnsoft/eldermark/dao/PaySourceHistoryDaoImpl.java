package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.PaySourceHistory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class PaySourceHistoryDaoImpl extends ResidentAwareDaoImpl<PaySourceHistory> implements PaySourceHistoryDao {
    @PersistenceContext
    private EntityManager entityManager;

    public PaySourceHistoryDaoImpl() {
        super(PaySourceHistory.class);
    }

    @Override
    public List<PaySourceHistory> listByResidentId(Long residentId) {
        TypedQuery<PaySourceHistory> query = entityManager.createQuery(
                "select p from PaySourceHistory p join p.resident res" +
                        " where res.id = :residentId and p.endDate is null order by p.id desc",
                PaySourceHistory.class);
        query.setParameter("residentId", residentId);
        return query.getResultList();
    }
}
