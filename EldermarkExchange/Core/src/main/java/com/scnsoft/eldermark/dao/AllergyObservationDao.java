package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.AllergyObservation;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

/**
 * @deprecated Transition to Spring Data repositories is recommended. Use {@link com.scnsoft.eldermark.dao.healthdata.AllergyObservationDao AllergyObservationDao} instead.
 */
public interface AllergyObservationDao extends BaseDao<AllergyObservation> {

    /**
     * Execute a SELECT query and return the query results as a typed List.
     *
     * @param residentId Resident ID
     * @return a list of the results
     */
    List<AllergyObservation> listByResidentId(Long residentId);

    /**
     * Execute a SELECT count(*) query for residents set and return the query result.
     *
     * @param residentIds Resident IDs
     * @return count
     */
    Long countByResidentIds(Collection<Long> residentIds);

    /**
     * Get a list of allergy observations ordered by {@code productText} for the specified resident
     * @return a list of allergies
     */
    List<AllergyObservation> listResidentAllergies(Long residentId, boolean includeActive, boolean includeInactive, boolean includeResolved);

    /**
     * Get a list of allergy observations ordered by {@code productText} for the specified residents
     * @return a list of allergies
     */
    List<AllergyObservation> listResidentAllergies(Collection<Long> residentIds, boolean includeActive, boolean includeInactive, boolean includeResolved, Pageable pageable);

    /**
     * Count allergy observations
     * @return allergies total count
     */
    Long countResidentAllergies(Collection<Long> residentIds, boolean includeActive, boolean includeInactive, boolean includeResolved);

}
