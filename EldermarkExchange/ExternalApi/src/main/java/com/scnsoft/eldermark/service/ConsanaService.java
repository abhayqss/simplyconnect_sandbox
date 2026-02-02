package com.scnsoft.eldermark.service;

import com.google.common.base.Optional;
import com.scnsoft.eldermark.shared.ConsanaXrefPatientIdDto;

public interface ConsanaService {

    Optional<Long> getResidentIdByXref(ConsanaXrefPatientIdDto consanaXrefPatientIdDto);

}
