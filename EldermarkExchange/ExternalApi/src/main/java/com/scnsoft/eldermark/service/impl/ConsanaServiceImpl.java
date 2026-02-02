package com.scnsoft.eldermark.service.impl;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.service.ConsanaService;
import com.scnsoft.eldermark.service.PrivilegesService;
import com.scnsoft.eldermark.shared.ConsanaXrefPatientIdDto;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class ConsanaServiceImpl implements ConsanaService {

    private final PrivilegesService privilegesService;
    private final Converter<ConsanaXrefPatientIdDto, Optional<Resident>> consanaXrefPatientDtoToResidentIdConverter;

    @Autowired
    public ConsanaServiceImpl(PrivilegesService privilegesService, Converter<ConsanaXrefPatientIdDto, Optional<Resident>> consanaXrefPatientDtoToResidentIdConverter) {
        this.privilegesService = privilegesService;
        this.consanaXrefPatientDtoToResidentIdConverter = consanaXrefPatientDtoToResidentIdConverter;
    }

    @Override
    public Optional<Long> getResidentIdByXref(ConsanaXrefPatientIdDto consanaXrefPatientIdDto) {
        if (!privilegesService.hasConsanaAccess()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        return consanaXrefPatientDtoToResidentIdConverter.convert(consanaXrefPatientIdDto).transform(new Function<Resident, Long>() {
            @Override
            public Long apply(Resident resident) {
                return resident.getId();
            }
        });
    }
}
