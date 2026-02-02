package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.server.model.entity.CcdCode;
import com.scnsoft.eldermark.consana.sync.server.model.entity.ConsanaUnknownCode;
import com.scnsoft.eldermark.consana.sync.server.model.entity.ProblemObservation;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import com.scnsoft.eldermark.consana.sync.server.model.enums.CodeSystem;
import com.scnsoft.eldermark.consana.sync.server.services.CcdCodeService;
import com.scnsoft.eldermark.consana.sync.server.services.ConsanaUnknownCodeService;
import com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.consana.sync.server.constants.FhirConstants.PROBLEM_STATUS_VALUE_SET;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
public class ConsanaConditionToProblemObservationConverter implements ConsanaDomainResourceToLongLegacyIdAwareEntityConverter<Condition, ProblemObservation> {

    private static final Logger logger = LoggerFactory.getLogger(ConsanaConditionToProblemObservationConverter.class);

    private static final Map<String, String> CLINICAL_STATUS_MAPPING = Map.of(
            "active", "55561003",
            "relapse", "55561003",
            "remission", "73425007",
            "resolved", "413322009");

    @Autowired
    private CcdCodeService ccdCodeService;

    @Autowired
    private ConsanaUnknownCodeService consanaUnknownCodeService;

    @Autowired
    private ConsanaConditionToProblemConverter consanaConditionToProblemConverter;

    @Override
    public ProblemObservation convert(Condition source, Resident resident) {
        var target = new ProblemObservation();
        return convertInto(source, resident, target);
    }

    @Override
    public ProblemObservation convertInto(Condition source, Resident resident, ProblemObservation target) {
        target.setAgeObservationUnit(source.hasOnsetAge() ? source.getOnsetAge().getUnit() : null);
        target.setAgeObservationValue(source.hasOnsetAge() ? source.getOnsetAge().getValue().intValue() : null);
        target.setProblemDateTimeLow(source.hasOnsetDateTimeType() ? FhirConversionUtils.getInstant(source.getOnsetDateTimeType().getValue()) : null);
        target.setOnsetDate(target.getProblemDateTimeLow());
        target.setProblemDateTimeHigh(source.hasAbatementDateTimeType() ?
                FhirConversionUtils.getInstant(source.getAbatementDateTimeType().getValue()) : null);
        try {
            addProblemData(source.getCode(), target);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            return null;
        }

        target.setDatabase(resident.getDatabase());
        target.setLegacyId(0L);
        target.setNegationInd(false);

        target.setProblemStatusCode(convertProblemStatusCode(source.getClinicalStatus()));
        target.setProblemStatusText(target.getProblemStatusCode() != null ? target.getProblemStatusCode().getDisplayName() : null);
        target.setManual(false);
        target.setRecordedDate(FhirConversionUtils.getInstant(source.getDateRecorded()));
        target.setComments(source.getNotes());
        target.setConsanaId(source.getId());
        target.setProblem(consanaConditionToProblemConverter.convert(source, resident, target.getProblem()));
        return target;
    }

    private void addProblemData(CodeableConcept code, ProblemObservation target) {
        var translations = new TreeSet<>(Comparator.comparingLong(CcdCode::getId));
        var icdCodes = new ArrayList<String>();
        var icdCodeSet = new ArrayList<String>();
        if (code != null && CollectionUtils.isNotEmpty(code.getCoding())) {
            var isSnomedCodeAdded = false;
            ConsanaUnknownCode unknownCode = null;
            CcdCode ccdCode = null;
            for (var coding : code.getCoding()) {
                CodeSystem codeSystem;
                //If coding.system is not provided - coding.system = SNOMED
                if (coding.getSystem() == null) {
                    codeSystem = CodeSystem.SNOMED_CT;
                } else {
                    codeSystem = CodeSystem.findBySystemUrl(coding.getSystem());
                }

                if (codeSystem == null) {
                    unknownCode = consanaUnknownCodeService.saveCode(new ConsanaUnknownCode(coding, "Condition"));
                    logger.info("Added a new unknown code with id: {}", unknownCode.getId());
                    continue;
                }
                ccdCode = ccdCodeService.findOrCreate(coding, codeSystem);
                if (!isSnomedCodeAdded && CodeSystem.SNOMED_CT.equals(codeSystem)) {
                    target.setProblemCode(ccdCode);
                    target.setProblemName(coding.getDisplay());
                    isSnomedCodeAdded = true;
                } else {
                    translations.add(ccdCode);
                }
                icdCodes.add(coding.getCode());
                icdCodeSet.add(codeSystem.getDisplayName());
            }

            if (unknownCode != null && ccdCode == null) {
                throw new IllegalArgumentException("Unknown code(s) without other correct codes");
            }

            target.setTranslations(translations);
            target.setProblemIcdCode(icdCodes.stream().filter(StringUtils::isNotEmpty).collect(Collectors.joining("\n")));
            target.setProblemIcdCodeSet(icdCodeSet.stream().filter(StringUtils::isNotEmpty).collect(Collectors.joining("\n")));
        } else {
            target.setProblemCode(null);
            target.setProblemName(null);
            target.setTranslations(null);
            target.setProblemIcdCode(null);
            target.setProblemIcdCodeSet(null);
        }
    }

    private CcdCode convertProblemStatusCode(String clinicalStatus) {
        if (StringUtils.isEmpty(clinicalStatus)) {
            return null;
        }
        var code = CLINICAL_STATUS_MAPPING.get(clinicalStatus.toLowerCase());
        return ccdCodeService.findByCodeAndValueSet(code, PROBLEM_STATUS_VALUE_SET);
    }
}
