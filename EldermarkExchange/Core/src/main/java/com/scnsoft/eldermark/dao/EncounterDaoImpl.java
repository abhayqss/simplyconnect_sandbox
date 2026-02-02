package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Encounter;
import org.springframework.stereotype.Repository;

@Repository
public class EncounterDaoImpl extends ResidentAwareDaoImpl<Encounter> implements EncounterDao {

    public EncounterDaoImpl() {
        super(Encounter.class);
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (Encounter encounter : this.listByResidentId(residentId)) {
            this.delete(encounter);
            ++count;
        }

        return count;
    }

}
