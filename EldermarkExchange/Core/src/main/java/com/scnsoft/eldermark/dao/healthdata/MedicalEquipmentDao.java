package com.scnsoft.eldermark.dao.healthdata;

import com.scnsoft.eldermark.entity.MedicalEquipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface MedicalEquipmentDao extends JpaRepository<MedicalEquipment, Long> {

    @Query("SELECT me FROM MedicalEquipment me WHERE me.id IN " +
            "(SELECT MIN(mee.id) FROM MedicalEquipment mee " +
            "WHERE mee.resident.id IN (:residentIds) " +
            "GROUP BY mee.effectiveTimeHigh, mee.quantity, mee.productInstance.id, mee.statusCode)")
    Page<MedicalEquipment> listResidentsMedicalEquipmentWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds, Pageable pageable);

    @Query("SELECT COUNT(me) FROM MedicalEquipment me WHERE me.id IN " +
            "(SELECT MIN(mee.id) FROM MedicalEquipment mee " +
            "WHERE mee.resident.id IN (:residentIds) " +
            "GROUP BY mee.effectiveTimeHigh, mee.quantity, mee.productInstance.id, mee.statusCode)")
    Long countResidentsMedicalEquipmentWithoutDuplicates(@Param("residentIds") Collection<Long> residentIds);
}
