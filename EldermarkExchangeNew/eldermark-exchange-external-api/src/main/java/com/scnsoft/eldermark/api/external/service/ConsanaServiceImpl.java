package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.ConsanaResidentDto;
import com.scnsoft.eldermark.api.external.web.dto.ConsanaXrefPatientIdDto;
import com.scnsoft.eldermark.api.shared.exception.PhrException;
import com.scnsoft.eldermark.api.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.entity.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConsanaServiceImpl implements ConsanaService {

    private static final Logger logger = LoggerFactory.getLogger(ConsanaServiceImpl.class);

    private final PrivilegesService privilegesService;
    private final ConsanaClientXrefIdResolver consanaClientXrefIdResolver;
    private final ResidentsService residentsService;
    private final ClientDao clientDao;

    @Autowired
    public ConsanaServiceImpl(PrivilegesService privilegesService,
                              ConsanaClientXrefIdResolver consanaClientXrefIdResolver,
                              ResidentsService residentsService,
                              ClientDao clientDao) {
        this.privilegesService = privilegesService;
        this.consanaClientXrefIdResolver = consanaClientXrefIdResolver;
        this.residentsService = residentsService;
        this.clientDao = clientDao;
    }

    @Override
    public Optional<Long> getResidentIdByXref(ConsanaXrefPatientIdDto consanaXrefPatientIdDto) {
        logger.info("Resolving resident id by Consana xref id [{}]", consanaXrefPatientIdDto);
        if (!privilegesService.hasConsanaAccess()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        return consanaClientXrefIdResolver.resolveClientId(consanaXrefPatientIdDto);
    }

    @Override
    public ConsanaResidentDto getResident(Long residentId) {
        logger.info("Loading consana-specific details of resident [{}]", residentId);
        residentsService.checkAccessOrThrow(residentId);
        return convertDetailed(clientDao.getOne(residentId));
    }

    private static ConsanaResidentDto convertDetailed(Client resident) {
        ConsanaResidentDto dto = new ConsanaResidentDto();
        ResidentsServiceImpl.fillBaseDetailsDto(dto, resident);
        dto.setIsActive(Boolean.TRUE.equals(resident.getActive()));

        return dto;
    }
}
