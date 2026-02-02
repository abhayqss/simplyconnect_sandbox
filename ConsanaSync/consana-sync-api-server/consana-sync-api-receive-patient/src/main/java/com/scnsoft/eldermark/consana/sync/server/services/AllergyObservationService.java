package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.model.entity.AllergyObservation;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;

import java.util.List;

public interface AllergyObservationService {

    List<AllergyObservation> findAllConsanaAllergyObservations(Resident resident);

    AllergyObservation saveAllergyObservation(AllergyObservation allergyObservation);

    void delete(AllergyObservation allergyObservation);
}
