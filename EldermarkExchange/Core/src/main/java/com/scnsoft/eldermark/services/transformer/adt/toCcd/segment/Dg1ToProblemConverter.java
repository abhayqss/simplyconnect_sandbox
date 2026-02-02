package com.scnsoft.eldermark.services.transformer.adt.toCcd.segment;

import com.scnsoft.eldermark.dao.CcdCodeDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.segment.AdtDG1DiagnosisSegment;
import com.scnsoft.eldermark.services.cda.CcdCodeService;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.services.transformer.ResidentAwareConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;

@Component
@Scope("prototype")
public class Dg1ToProblemConverter extends ListAndItemTransformer<AdtDG1DiagnosisSegment, Problem> implements ResidentAwareConverter<AdtDG1DiagnosisSegment, Problem> {

    private static final String DIAGNOSIS_CODE = "282291009";
    private static final String DIAGNOSIS_CODE_SYSTEM_OID = CodeSystem.SNOMED_CT.getOid();
    private static final String DIAGNOSIS_VALUE_SET = "2.16.840.1.113883.1.11.20.14";

    @Autowired
    private CcdCodeDao ccdCodeDao;

    @Autowired
    private CcdCodeService ccdCodeService;

    private Resident resident;
    private Date eventDate;

    @Override
    public Problem convert(AdtDG1DiagnosisSegment adtDG1DiagnosisSegment) {
        if (isNotEnoughData(adtDG1DiagnosisSegment)) {
            return null;
        }

        final Problem problem = new Problem();
        final ProblemObservation problemObservation = new ProblemObservation();

        bindProblemWithObservation(problem, problemObservation);
        populateProblem(problem, adtDG1DiagnosisSegment);
        populateProblemObservation(problemObservation, adtDG1DiagnosisSegment);

        return problem;
    }

    private boolean isNotEnoughData(AdtDG1DiagnosisSegment adtDG1DiagnosisSegment) {
        return isDiagnosisEmpty(adtDG1DiagnosisSegment) || isUnknownCodeSystem(adtDG1DiagnosisSegment.getDiagnosisCode().getNameOfCodingSystem());
    }

    private boolean isDiagnosisEmpty(AdtDG1DiagnosisSegment adtDG1DiagnosisSegment) {
        return adtDG1DiagnosisSegment.getDiagnosisCode() == null
                || StringUtils.isEmpty(adtDG1DiagnosisSegment.getDiagnosisCode().getIdentifier())
                || StringUtils.isEmpty(adtDG1DiagnosisSegment.getDiagnosisCode().getNameOfCodingSystem());
    }

    private boolean isUnknownCodeSystem(String nameOfCodingSystem) {
        return CodeSystem.findByDisplayName(nameOfCodingSystem) == null;
    }

    private void bindProblemWithObservation(Problem problem, ProblemObservation problemObservation) {
        problem.setProblemObservations(Collections.singletonList(problemObservation));
        problemObservation.setProblem(problem);
    }

    private void populateProblem(Problem problem, AdtDG1DiagnosisSegment adtDG1DiagnosisSegment) {
        problem.setLegacyId(0L);
        problem.setTimeLow(getDg1Date(adtDG1DiagnosisSegment));
        problem.setDatabase(resident.getDatabase());
        problem.setResident(resident);
    }

    private void populateProblemObservation(ProblemObservation problemObservation, AdtDG1DiagnosisSegment adtDG1DiagnosisSegment) {
        problemObservation.setProblemDateTimeLow(getDg1Date(adtDG1DiagnosisSegment));

        final CcdCode problemValueCode = findOrCreateCode(adtDG1DiagnosisSegment.getDiagnosisCode());
        problemObservation.setProblemName(StringUtils.defaultIfEmpty(adtDG1DiagnosisSegment.getDiagnosisCode().getText(), problemValueCode.getDisplayName()));

        problemObservation.setDatabase(resident.getDatabase());
        problemObservation.setLegacyId(0L);
        problemObservation.setNegationInd(false);
        problemObservation.setProblemType(getProblemTypeDiagnosisCode());
        problemObservation.setProblemCode(problemValueCode);
        problemObservation.setProblemIcdCode(adtDG1DiagnosisSegment.getDiagnosisCode().getIdentifier());
        problemObservation.setProblemIcdCodeSet(adtDG1DiagnosisSegment.getDiagnosisCode().getNameOfCodingSystem());
        problemObservation.setManual(false);
    }

    private Date getDg1Date(AdtDG1DiagnosisSegment adtDG1DiagnosisSegment) {
        return adtDG1DiagnosisSegment.getDiagnosisDateTime() == null ? eventDate : adtDG1DiagnosisSegment.getDiagnosisDateTime();
    }

    private CcdCode findOrCreateCode(CECodedElement diagnosisCode) {
        return ccdCodeService.findOrCreate(diagnosisCode.getIdentifier(), diagnosisCode.getText(), CodeSystem.findByDisplayName(diagnosisCode.getNameOfCodingSystem()));
    }

    private CcdCode getProblemTypeDiagnosisCode() {
        return ccdCodeDao.getCcdCode(DIAGNOSIS_CODE, DIAGNOSIS_CODE_SYSTEM_OID, DIAGNOSIS_VALUE_SET);
    }

    @Override
    public Dg1ToProblemConverter withResident(Resident resident) {
        this.resident = resident;
        return this;
    }

    public Dg1ToProblemConverter withEventDate(Date eventDate) {
        this.eventDate = eventDate;
        return this;
    }
}
