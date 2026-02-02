package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Payer;
import org.springframework.stereotype.Repository;

@Repository
public class PayerDaoImpl extends ResidentAwareDaoImpl<Payer> implements PayerDao {

    public PayerDaoImpl() {
        super(Payer.class);
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (Payer payer : this.listByResidentId(residentId)) {
            this.delete(payer);
            ++count;
        }

        return count;
    }
}
