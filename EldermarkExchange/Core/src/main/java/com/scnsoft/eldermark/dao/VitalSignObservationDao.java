package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.VitalSignObservation;

import java.util.Collection;

/**
 * @deprecated Transition to Spring Data repositories is recommended. Use {@link com.scnsoft.eldermark.dao.healthdata.VitalSignObservationDao VitalSignObservationDao} instead.
 */
public interface VitalSignObservationDao extends BaseDao<VitalSignObservation> {

    /**
     * Execute a SELECT count(*) query for residents set and return the query result.
     *
     * @param residentIds Resident IDs
     * @return count
     */
    Long countByResidentIds(Collection<Long> residentIds);

}
