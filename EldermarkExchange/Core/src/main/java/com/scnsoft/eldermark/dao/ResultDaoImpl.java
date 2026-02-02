package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Result;
import org.springframework.stereotype.Repository;

@Repository
public class ResultDaoImpl extends ResidentAwareDaoImpl<Result> implements ResultDao {

    public ResultDaoImpl() {
        super(Result.class);
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (Result result : this.listByResidentId(residentId)) {
            this.delete(result);
            ++count;
        }

        return count;
    }

}
