package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.model.entity.ProblemObservation;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;

import java.util.List;

public interface ProblemObservationService {

    List<ProblemObservation> findAllConsanaProblemObservationsByResident(Resident resident);

    ProblemObservation saveProblemObservation(ProblemObservation problemObservation);

    void delete(ProblemObservation problemObservation);
}
