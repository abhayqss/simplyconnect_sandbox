package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.ConsanaXrefPatientIdDto;

import java.util.Optional;

interface ConsanaClientXrefIdResolver {

    Optional<Long> resolveClientId(ConsanaXrefPatientIdDto consanaXrefPatientIdDto);
}
