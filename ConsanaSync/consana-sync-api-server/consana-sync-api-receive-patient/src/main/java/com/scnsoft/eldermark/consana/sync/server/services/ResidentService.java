package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.model.ResidentIdentifyingData;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;

import java.util.Optional;

public interface ResidentService {

    Optional<Resident> find(ResidentIdentifyingData residentIdentifyingData);

    Resident updateEmptyFields(Resident target, Resident source);

    Resident create(Resident res);

    Resident update(Resident res);

}
