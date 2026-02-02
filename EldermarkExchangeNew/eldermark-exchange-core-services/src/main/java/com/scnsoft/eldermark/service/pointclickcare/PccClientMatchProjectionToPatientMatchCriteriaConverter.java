package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dto.pointclickcare.filter.patient.PCCPatientFilterExactMatchCriteria;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PCCPatientDetails;
import com.scnsoft.eldermark.dto.pointclickcare.projection.PccClientMatchProjection;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@ConditionalOnProperty(value = "pcc.patientMatch.enabled", havingValue = "true")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class PccClientMatchProjectionToPatientMatchCriteriaConverter implements Converter<PccClientMatchProjection, PCCPatientFilterExactMatchCriteria> {
    private final Map<String, PCCPatientDetails.Gender> GENDER_MAP = Map.of(
            "F", PCCPatientDetails.Gender.FEMALE,
            "M", PCCPatientDetails.Gender.MALE,
            "UN", PCCPatientDetails.Gender.UNKNOWN
    );

    @Override
    public PCCPatientFilterExactMatchCriteria convert(PccClientMatchProjection client) {
        var criteria = new PCCPatientFilterExactMatchCriteria();
        criteria.setBirthDate(client.getBirthDate());
        criteria.setFacId(client.getCommunityPccFacilityId());
        criteria.setFirstName(client.getFirstName());
        criteria.setGender(mapGender(client.getGenderCodeSystem(), client.getGenderCode()));
        criteria.setLastName(client.getLastName());
        criteria.setMedicaidNumber(client.getMedicaidNumber());
        criteria.setMedicareNumber(client.getMedicareNumber());
        return criteria;
    }

    private PCCPatientDetails.Gender mapGender(String genderCodeSystem, String genderCode) {
        if (StringUtils.isEmpty(genderCode) || StringUtils.isEmpty(genderCode)) {
            return PCCPatientDetails.Gender.UNKNOWN;
        }

        if (CodeSystem.ADMINISTRATIVE_GENDER.getOid().equals(genderCodeSystem)) {
            return GENDER_MAP.getOrDefault(genderCode, PCCPatientDetails.Gender.UNKNOWN);
        }
        return PCCPatientDetails.Gender.UNKNOWN;
    }
}
