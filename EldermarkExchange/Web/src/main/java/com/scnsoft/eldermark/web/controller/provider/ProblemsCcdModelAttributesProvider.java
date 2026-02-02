package com.scnsoft.eldermark.web.controller.provider;

import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.CcdCodeDao;
import com.scnsoft.eldermark.entity.CcdSection;
import com.scnsoft.eldermark.services.ccd.section.ProblemService;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.shared.ccd.ProblemObservationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Transactional(readOnly = true)
public class ProblemsCcdModelAttributesProvider implements CcdModelAttributesProvider {

    public static final String PROBLEM_TYPE_VALUE_SET_CODE = "2.16.840.1.113883.1.11.20.14";
    public static final String PROBLEM_STATUS_VALUE_SET_CODE = "2.16.840.1.113883.1.11.20.13";
    public static final String SNOMED_CLINICAL_TERMS_CODE_SYSTEM_CODE = "2.16.840.1.113883.6.96";

    public static final String ICD_10_CM_CODE_SYSTEM_CODE = "2.16.840.1.113883.6.90";

    @Autowired
    private CcdCodeDao ccdCodeDao;

    @Autowired
    private ProblemService problemService;

    @Override
    public Map<String, Object> getAttributesForAdd(Long residentId) {
        final Map<String, Object> result = new HashMap<>();
        final ProblemObservationDto problemObservationDto = new ProblemObservationDto();
        problemObservationDto.setRecordedBy(SecurityUtils.getAuthenticatedUser().getEmployee().getFullName());

        result.put("problem", problemObservationDto);
        result.put("problemTypes", CcdUtils.transform(ccdCodeDao.listCcdCodesByValueSetAndCodeSystem(PROBLEM_TYPE_VALUE_SET_CODE, SNOMED_CLINICAL_TERMS_CODE_SYSTEM_CODE)));
        result.put("problemStatuses", CcdUtils.transform(ccdCodeDao.listCcdCodesByValueSetAndCodeSystem(PROBLEM_STATUS_VALUE_SET_CODE, SNOMED_CLINICAL_TERMS_CODE_SYSTEM_CODE)));
        result.put("modalTitle", "Add a New Problem");
        return result;
    }

    @Override
    public Map<String, Object> getAttributesForEdit(Long residentId, Long problemObservationId) {
        final Map<String, Object> result = new HashMap<>();
        final ProblemObservationDto problemObservationDto = problemService.getProblemObservationDto(problemObservationId);

        result.put("problem", problemObservationDto);
        result.put("problemValue", Collections.singletonList(problemObservationDto.getValue()));
        result.put("problemTypes", CcdUtils.transform(ccdCodeDao.listCcdCodesByValueSetAndCodeSystem(PROBLEM_TYPE_VALUE_SET_CODE, SNOMED_CLINICAL_TERMS_CODE_SYSTEM_CODE)));
        result.put("problemStatuses", CcdUtils.transform(ccdCodeDao.listCcdCodesByValueSetAndCodeSystem(PROBLEM_STATUS_VALUE_SET_CODE, SNOMED_CLINICAL_TERMS_CODE_SYSTEM_CODE)));
        result.put("modalTitle", "Edit Problem");
        return result;
    }

    @Override
    public Map<String, Object> getAttributesForView(Long residentId, Long problemObservationId) {
        final Map<String, Object> result = new HashMap<>();
        final ProblemObservationDto problemObservationDto = problemService.getProblemObservationDto(problemObservationId);

        result.put("problem", problemObservationDto);
        result.put("problemValue", Collections.singletonList(problemObservationDto.getValue()));
        result.put("problemTypes", Collections.singletonList(problemObservationDto.getType()));
        result.put("problemStatuses", Collections.singletonList(problemObservationDto.getStatus()));
        result.put("modalTitle", "Problem");

        return result;
    }

    @Override
    public CcdSection getSection() {
        return CcdSection.PROBLEMS;
    }
}
