package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;

public interface CustomProblemObservationDao {
    Map<Long, Long> countsByClientId(Specification<ProblemObservation> spec);
}
