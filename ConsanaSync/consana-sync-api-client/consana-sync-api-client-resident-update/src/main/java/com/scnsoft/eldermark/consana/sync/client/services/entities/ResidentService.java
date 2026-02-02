package com.scnsoft.eldermark.consana.sync.client.services.entities;

import com.scnsoft.eldermark.consana.sync.client.model.entities.Resident;

import java.util.stream.Stream;

public interface ResidentService {

    Stream<Resident> getMergedResidents(Long residentId);

    Resident getOne(Long residentId);

    Resident updateXrefId(Resident resident);

    boolean isPharmacyNamesAndAdmittedDateCorrect(Resident resident);
}
