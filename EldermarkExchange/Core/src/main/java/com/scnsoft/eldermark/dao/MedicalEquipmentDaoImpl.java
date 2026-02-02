package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.MedicalEquipment;
import org.springframework.stereotype.Repository;

@Repository
public class MedicalEquipmentDaoImpl extends ResidentAwareDaoImpl<MedicalEquipment> implements MedicalEquipmentDao {

    public MedicalEquipmentDaoImpl() {
        super(MedicalEquipment.class);
    }

    @Override
    public int deleteByResidentId(Long residentId) {
        int count = 0;
        for (MedicalEquipment medicalEquipment : this.listByResidentId(residentId)) {
            this.delete(medicalEquipment);
            ++count;
        }

        return count;
    }

}
