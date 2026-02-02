package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.server.model.entity.CcdCode;
import com.scnsoft.eldermark.consana.sync.server.model.entity.ReactionObservation;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import com.scnsoft.eldermark.consana.sync.server.model.enums.CodeSystem;
import com.scnsoft.eldermark.consana.sync.server.services.CcdCodeService;
import com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils;
import org.hl7.fhir.instance.model.AllergyIntolerance;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Coding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
public class ConsanaAllergyIntoleranceToReactionObservationConverter {

    @Autowired
    private CcdCodeService ccdCodeService;

    @Autowired
    private FhirConversionUtils fhirConversionUtils;

    public List<ReactionObservation> convert(AllergyIntolerance allergyIntolerance, Resident resident, List<ReactionObservation> target) {
        if (!allergyIntolerance.hasReaction()) {
            if (target != null) {
                target.clear();
            }
            return target;
        }

        var reactionObservations = new HashSet<ReactionObservation>();
        allergyIntolerance.getReaction()
                .forEach(source -> {
                    var reactionObservation = FhirConversionUtils.getOrCreateCollectionElement(target, reactionObservations.size(), ReactionObservation::new);
                    reactionObservation.setDatabase(resident.getDatabase());
                    reactionObservation.setLegacyId("0");
                    reactionObservation.setLegacyTable("Allergy_NWHIN");
                    reactionObservation.setTimeLow(FhirConversionUtils.getInstant(source.getOnset()));
                    reactionObservation.setReactionCode(convertReactionCode(source));
                    reactionObservation.setReactionText(convertReactionText(source, reactionObservation.getReactionCode()));
                    reactionObservations.add(reactionObservation);
                });
        return FhirConversionUtils.updateCollection(new ArrayList<>(reactionObservations), target);
    }

    private CcdCode convertReactionCode(AllergyIntolerance.AllergyIntoleranceReactionComponent reaction) {
        if (reaction != null && reaction.hasManifestation()) {
            return reaction.getManifestation().stream()
                    .map(CodeableConcept::getCoding)
                    .flatMap(List::stream)
                    .map(c -> {
                        CodeSystem codeSystem = fhirConversionUtils.getCodeSystem(c, "AllergyIntolerance.reaction.manifestation");
                        if (codeSystem == null) {
                            return null;
                        }
                        var code = CodeSystem.ICD_10_CM == codeSystem ? convertCodeForICD10System(c.getCode()) : c.getCode();
                        return ccdCodeService.findOrCreate(code, c.getDisplay(), codeSystem.getOid(), codeSystem.getDisplayName());
                    })
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private String convertCodeForICD10System(String code) {
        if (code.length() <= 3 || code.contains(".")) {
            return code;
        }
        return code.substring(0, 3) + "." + code.substring(3);
    }

    private String convertReactionText(AllergyIntolerance.AllergyIntoleranceReactionComponent reaction, CcdCode code) {
        if (reaction != null && reaction.hasManifestation()) {
            var text = reaction.getManifestation().stream()
                    .map(CodeableConcept::getCoding)
                    .flatMap(List::stream)
                    .filter(Coding::hasDisplay)
                    .map(Coding::getDisplay)
                    .findFirst()
                    .orElse(null);
            if (text != null) {
                return text;
            }
        }
        return code != null ? code.getDisplayName() : null;
    }
}
