package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Informant;
import org.springframework.stereotype.Repository;

@Repository
public class InformantDaoImpl extends ResidentAwareDaoImpl<Informant> implements InformantDao {

    public InformantDaoImpl() {
        super(Informant.class);
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (Informant informant : this.listByResidentId(residentId)) {
            this.delete(informant);
            ++count;
        }

        return count;
    }

}
