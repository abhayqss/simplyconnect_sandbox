package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Problem;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

/**
 * @deprecated Transition to Spring Data repositories is recommended. Use {@link com.scnsoft.eldermark.dao.healthdata.ProblemObservationDao ProblemObservationDao} instead.
 */
public interface ProblemDao extends ResidentAwareDao<Problem> {
    List<Problem> listResidentProblems(Long residentId, boolean includeActive, boolean includeInactive, boolean includeOther);
    List<Problem> listResidentProblems(Collection<Long> residentIds, boolean includeActive, boolean includeInactive, boolean includeOther, Pageable pageable);
    Long countResidentProblems(Collection<Long> activeResidentIds, boolean includeActive, boolean includeInactive, boolean includeOther);
}
