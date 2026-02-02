package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.SocialHistory;
import org.springframework.stereotype.Repository;

@Repository
public class SocialHistoryDaoImpl extends ResidentAwareDaoImpl<SocialHistory> implements SocialHistoryDao {

    public SocialHistoryDaoImpl() {
        super(SocialHistory.class);
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (SocialHistory socialHistory : this.listByResidentId(residentId)) {
            this.delete(socialHistory);
            ++count;
        }

        return count;
    }
}
