package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.server.model.entity.AllergyObservation;
import com.scnsoft.eldermark.consana.sync.server.model.entity.CcdCode;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import com.scnsoft.eldermark.consana.sync.server.model.enums.CodeSystem;
import com.scnsoft.eldermark.consana.sync.server.services.CcdCodeService;
import com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils;
import org.hl7.fhir.instance.model.AllergyIntolerance;
import org.hl7.fhir.instance.model.Coding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
public class ConsanaAllergyIntoleranceToAllergyObservationConverter implements ConsanaDomainResourceToLongLegacyIdAwareEntityConverter<AllergyIntolerance, AllergyObservation> {

    @Autowired
    private CcdCodeService ccdCodeService;

    @Autowired
    private FhirConversionUtils fhirConversionUtils;

    @Autowired
    private ConsanaAllergyIntoleranceToAllergyConverter allergyConverter;

    @Autowired
    private ConsanaAllergyIntoleranceToReactionObservationConverter reactionObservationConverter;

    @Autowired
    private ConsanaAllergyIntoleranceToSeverityObservationConverter severityObservationConverter;

    @Override
    public AllergyObservation convert(AllergyIntolerance source, Resident resident) {
        var target = new AllergyObservation();
        return convertInto(source, resident, target);
    }

    @Override
    public AllergyObservation convertInto(AllergyIntolerance source, Resident resident, AllergyObservation target) {
        target.setLegacyId(0L);
        target.setDatabase(resident.getDatabase());
        target.setTimeLow(FhirConversionUtils.getInstant(source.getOnset()));
        target.setProductCode(convertProductCode(source));
        target.setProductText(convertProductText(source, target.getProductCode()));
        target.setAdverseEventTypeCode(convertEventTypeCode(source.getCategory(), source.getType()));
        target.setAdverseEventTypeText(target.getAdverseEventTypeCode() != null ? target.getAdverseEventTypeCode().getDisplayName() : null);
        target.setObservationStatusCode(convertObservationStatusCode(source.getStatus()));
        target.setConsanaId(source.getId());
        target.setAllergy(allergyConverter.convert(source, resident, target.getAllergy()));
        target.setSeverityObservation(severityObservationConverter.convert(source, resident, target.getSeverityObservation()));
        target.setReactionObservations(reactionObservationConverter.convert(source, resident, target.getReactionObservations()));
        return target;
    }

    private CcdCode convertProductCode(AllergyIntolerance source) {
        if (source == null || !source.hasSubstance()) {
            return null;
        }

        return Stream.ofNullable(source.getSubstance().getCoding())
                .flatMap(List::stream)
                .map(c -> {
                    CodeSystem codeSystem = fhirConversionUtils.getCodeSystem(c, "AllergyIntolerance");
                    if (codeSystem != null) {
                        return ccdCodeService.findOrCreate(c, codeSystem);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private String convertProductText(AllergyIntolerance source, CcdCode code) {
        if (source == null || !source.hasSubstance()) {
            return null;
        }

        var substance = source.getSubstance();
        if (substance.hasText()) {
            return substance.getText();
        }

        var text = Stream.ofNullable(substance.getCoding())
                .flatMap(List::stream)
                .filter(Coding::hasDisplay)
                .map(Coding::getDisplay)
                .findFirst()
                .orElse(null);
        if (text != null) {
            return text;
        }

        return code != null ? code.getDisplayName() : null;
    }

    private CcdCode convertEventTypeCode(AllergyIntolerance.AllergyIntoleranceCategory category, AllergyIntolerance.AllergyIntoleranceType type) {
        if (AllergyIntolerance.AllergyIntoleranceCategory.FOOD == category && AllergyIntolerance.AllergyIntoleranceType.INTOLERANCE == type) {
            return ccdCodeService.findByCodeAndCodeSystem("235719002", CodeSystem.SNOMED_CT.getOid());
        }
        if (AllergyIntolerance.AllergyIntoleranceCategory.FOOD == category && (type == null || AllergyIntolerance.AllergyIntoleranceType.ALLERGY == type)) {
            return ccdCodeService.findByCodeAndCodeSystem("414285001", CodeSystem.SNOMED_CT.getOid());
        }
        if (AllergyIntolerance.AllergyIntoleranceCategory.MEDICATION == category && AllergyIntolerance.AllergyIntoleranceType.INTOLERANCE == type) {
            return ccdCodeService.findByCodeAndCodeSystem("59037007", CodeSystem.SNOMED_CT.getOid());
        }
        if (AllergyIntolerance.AllergyIntoleranceCategory.MEDICATION == category && (type == null || AllergyIntolerance.AllergyIntoleranceType.ALLERGY == type)) {
            return ccdCodeService.findByCodeAndCodeSystem("416098002", CodeSystem.SNOMED_CT.getOid());
        }
        if ((AllergyIntolerance.AllergyIntoleranceCategory.ENVIRONMENT == category || AllergyIntolerance.AllergyIntoleranceCategory.OTHER == category)) {
            return ccdCodeService.findByCodeAndCodeSystem("419199007", CodeSystem.SNOMED_CT.getOid());
        }
        return null;
    }

    private CcdCode convertObservationStatusCode(AllergyIntolerance.AllergyIntoleranceStatus status) {
        if ((AllergyIntolerance.AllergyIntoleranceStatus.ACTIVE == status)) {
            return ccdCodeService.findByCodeAndCodeSystem("55561003", CodeSystem.SNOMED_CT.getOid());
        }
        if ((AllergyIntolerance.AllergyIntoleranceStatus.INACTIVE == status)) {
            return ccdCodeService.findByCodeAndCodeSystem("73425007", CodeSystem.SNOMED_CT.getOid());
        }
        return null;
    }
}
