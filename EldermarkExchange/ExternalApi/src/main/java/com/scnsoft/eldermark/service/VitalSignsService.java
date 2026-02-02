package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.healthdata.VitalSignObservationDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.VitalSignType;
import com.scnsoft.eldermark.services.cda.CcdCodeService;
import com.scnsoft.eldermark.shared.exception.ValidationException;
import com.scnsoft.eldermark.web.entity.ConceptDescriptorDto;
import com.scnsoft.eldermark.web.entity.VitalSignObservationDetailsDto;
import com.scnsoft.eldermark.web.entity.VitalSignObservationReport;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;

/**
 * @author phomal
 * Created on 1/31/2018.
 */
@Service
@Transactional
public class VitalSignsService {

    private final ResidentsService residentsService;
    private final VitalSignObservationDao vitalSignObservationDao;
    private final CcdCodeService ccdCodeService;

    @Autowired
    public VitalSignsService(ResidentsService residentsService, VitalSignObservationDao vitalSignObservationDao, CcdCodeService ccdCodeService) {
        this.residentsService = residentsService;
        this.vitalSignObservationDao = vitalSignObservationDao;
        this.ccdCodeService = ccdCodeService;
    }

    public VitalSignObservationDetailsDto create(Long residentId, VitalSignObservationDetailsDto body) {
        residentsService.checkAccessOrThrow(residentId);

        VitalSign newVitalSign = new VitalSign();
        VitalSignObservation newObservation = new VitalSignObservation();
        newVitalSign.setVitalSignObservations(Collections.singletonList(newObservation));
        newObservation.setVitalSign(newVitalSign);
        newVitalSign.setLegacyId("API");
        newObservation.setLegacyId("API");

        newVitalSign.setEffectiveTime(new Date(body.getDateTime()));
        final Resident resident = residentsService.getEntity(residentId);
        newVitalSign.setResident(resident);
        newVitalSign.setDatabase(resident.getDatabase());
        newObservation.setDatabase(resident.getDatabase());

        newObservation.setEffectiveTime(newVitalSign.getEffectiveTime());
        newObservation.setUnit(StringUtils.trimToNull(body.getUnit()));
        newObservation.setValue(body.getValue());

        final String loincCode, loincDisplayName;
        if (body.getType() != null) {
            loincCode = body.getType().code();
            loincDisplayName = body.getType().displayName();
        } else if (body.getLoinc() != null) {
            loincCode = StringUtils.trimToNull(body.getLoinc().getCode());
            loincDisplayName = StringUtils.trimToNull(body.getLoinc().getDisplayName());
        } else {
            throw new ValidationException("'loinc' can not be null when 'type' is null.");
        }
        final CcdCode code = ccdCodeService.findOrCreate(loincCode, loincDisplayName, CodeSystem.LOINC);
        newObservation.setResultTypeCode(code);

        return convert(vitalSignObservationDao.save(newObservation));
    }

    @Transactional(readOnly = true)
    public VitalSignObservationDetailsDto get(Long residentId, Long vitalSignId) {
        residentsService.checkAccessOrThrow(residentId);
        // TODO implement
        return new VitalSignObservationDetailsDto();
    }

    @Transactional(readOnly = true)
    public VitalSignObservationReport report(Long residentId, VitalSignType type, String dateFrom, String dateTo, Pageable pageable) {
        residentsService.checkAccessOrThrow(residentId);
        // TODO implement
        return null;
    }


    private static VitalSignObservationDetailsDto convert(VitalSignObservation src) {
        VitalSignObservationDetailsDto dto = new VitalSignObservationDetailsDto();
        if (src.getEffectiveTime() != null) {
            dto.setDateTime(src.getEffectiveTime().getTime());
        }
        dto.setValue(src.getValue());
        dto.setUnit(src.getUnit());
        final CcdCode resultType = src.getResultTypeCode();
        if (resultType != null) {
            dto.setType(VitalSignType.getByCode(resultType.getCode()));
            if (CodeSystem.LOINC.getOid().equalsIgnoreCase(resultType.getCodeSystem())) {
                ConceptDescriptorDto cdDto = new ConceptDescriptorDto();
                cdDto.setCode(resultType.getCode());
                cdDto.setDisplayName(resultType.getDisplayName());
                dto.setLoinc(cdDto);
            }
        }

        return dto;
    }

}
