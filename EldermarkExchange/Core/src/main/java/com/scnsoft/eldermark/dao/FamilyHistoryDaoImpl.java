package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.FamilyHistory;
import org.springframework.stereotype.Repository;

@Repository
public class FamilyHistoryDaoImpl extends ResidentAwareDaoImpl<FamilyHistory> implements FamilyHistoryDao {

    public FamilyHistoryDaoImpl() {
        super(FamilyHistory.class);
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (FamilyHistory familyHistory : this.listByResidentId(residentId)) {
            this.delete(familyHistory);
            ++count;
        }

        return count;
    }
}
