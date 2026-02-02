package com.scnsoft.eldermark.consana.sync.client.services;

import com.scnsoft.eldermark.consana.sync.client.model.entities.Resident;

import java.util.Optional;

public interface ResidentService {

    Optional<Resident> findById(Long residentId);

}
