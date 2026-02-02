package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.dao.VitalSignObservationDao;
import com.scnsoft.eldermark.api.external.web.dto.ConceptDescriptorDto;
import com.scnsoft.eldermark.api.external.web.dto.VitalSignObservationDetailsDto;
import com.scnsoft.eldermark.api.external.web.dto.VitalSignObservationReport;
import com.scnsoft.eldermark.api.shared.entity.VitalSignType;
import com.scnsoft.eldermark.api.shared.exception.ValidationException;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.ccd.VitalSign;
import com.scnsoft.eldermark.entity.document.ccd.VitalSignObservation;
import com.scnsoft.eldermark.service.document.cda.CcdCodeCustomService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;

@Service
@Transactional
public class VitalSignsServiceImpl implements VitalSignsService {

    private final ResidentsService residentsService;
    private final VitalSignObservationDao vitalSignObservationDao;
    private final CcdCodeCustomService ccdCodeService;

    @Autowired
    public VitalSignsServiceImpl(ResidentsService residentsService, VitalSignObservationDao vitalSignObservationDao, CcdCodeCustomService ccdCodeService) {
        this.residentsService = residentsService;
        this.vitalSignObservationDao = vitalSignObservationDao;
        this.ccdCodeService = ccdCodeService;
    }

    @Override
    public VitalSignObservationDetailsDto create(Long residentId, VitalSignObservationDetailsDto body) {
        residentsService.checkAccessOrThrow(residentId);

        VitalSign newVitalSign = new VitalSign();
        VitalSignObservation newObservation = new VitalSignObservation();
        newVitalSign.setVitalSignObservations(Collections.singletonList(newObservation));
        newObservation.setVitalSign(newVitalSign);
        newVitalSign.setLegacyId("API");
        newObservation.setLegacyId("API");

        newVitalSign.setEffectiveTime(new Date(body.getDateTime()));
        final Client resident = residentsService.getEntity(residentId);
        newVitalSign.setClient(resident);
        newVitalSign.setOrganization(resident.getOrganization());
        newObservation.setOrganization(resident.getOrganization());

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
        var code = ccdCodeService.findOrCreate(loincCode, loincDisplayName, CodeSystem.LOINC).orElse(null);
        newObservation.setResultTypeCode(code);

        return convert(vitalSignObservationDao.save(newObservation));
    }

    @Override
    @Transactional(readOnly = true)
    public VitalSignObservationDetailsDto get(Long residentId, Long vitalSignId) {
        residentsService.checkAccessOrThrow(residentId);
        // TODO implement
        return new VitalSignObservationDetailsDto();
    }

    @Override
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
