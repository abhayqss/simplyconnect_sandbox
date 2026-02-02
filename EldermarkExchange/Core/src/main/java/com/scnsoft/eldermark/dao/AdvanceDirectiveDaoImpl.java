package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.AdvanceDirective;
import org.springframework.stereotype.Repository;

@Repository
public class AdvanceDirectiveDaoImpl extends ResidentAwareDaoImpl<AdvanceDirective> implements AdvanceDirectiveDao {

    public AdvanceDirectiveDaoImpl() {
        super(AdvanceDirective.class);
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (AdvanceDirective advanceDirective : this.listByResidentId(residentId)) {
            this.delete(advanceDirective);
            ++count;
        }

        return count;
    }
}
