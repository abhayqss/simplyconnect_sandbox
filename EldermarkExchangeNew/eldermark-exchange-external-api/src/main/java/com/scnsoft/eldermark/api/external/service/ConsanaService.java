package com.scnsoft.eldermark.api.external.service;


import com.scnsoft.eldermark.api.external.web.dto.ConsanaResidentDto;
import com.scnsoft.eldermark.api.external.web.dto.ConsanaXrefPatientIdDto;

import java.util.Optional;

public interface ConsanaService {

    Optional<Long> getResidentIdByXref(ConsanaXrefPatientIdDto consanaXrefPatientIdDto);

    ConsanaResidentDto getResident(Long residentId);
}
