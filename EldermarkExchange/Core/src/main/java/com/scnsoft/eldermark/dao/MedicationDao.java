package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Medication;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

/**
 * @deprecated Transition to Spring Data repositories is recommended. Use {@link com.scnsoft.eldermark.dao.healthdata.MedicationDao MedicationDao} instead.
 */
public interface MedicationDao extends ResidentAwareDao<Medication> {
    List<Medication> listResidentMedications(Long residentId, boolean includeActive, boolean includeInactive);
    List<Medication> listResidentMedications(Collection<Long> residentIds, boolean includeActive, boolean includeInactive, Pageable pageable);
    Long countResidentMedications(Collection<Long> residentIds, boolean includeActive, boolean includeInactive);
}
