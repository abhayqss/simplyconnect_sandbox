package com.scnsoft.eldermark.consana.sync.server.dao;

import com.scnsoft.eldermark.consana.sync.server.model.entity.ProblemObservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemObservationDao extends JpaRepository<ProblemObservation, Long> {

    List<ProblemObservation> getAllByConsanaIdIsNotNullAndProblem_ResidentId(Long residentId);
}
