package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Allergy;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

/**
 * @deprecated Transition to Spring Data repositories is recommended. Use {@link com.scnsoft.eldermark.dao.healthdata.AllergyObservationDao AllergyObservationDao} instead.
 */
public interface AllergyDao extends ResidentAwareDao<Allergy> {
    /**
     * Get a list of allergies ordered by {@code allergyObservation.productText} for the specified resident
     * @return a list of allergies
     */
    List<Allergy> listResidentAllergies(Long residentId, boolean includeActive, boolean includeInactive, boolean includeResolved);

    /**
     * Get a list of allergies ordered by {@code allergyObservation.productText} for the specified residents
     * @return a list of allergies
     */
    List<Allergy> listResidentAllergies(Collection<Long> residentIds, boolean includeActive, boolean includeInactive, boolean includeResolved, Pageable pageable);

    /**
     * Count allergies
     * @return allergies total count
     */
    Long countResidentAllergies(Collection<Long> residentIds, boolean includeActive, boolean includeInactive, boolean includeResolved);
}
