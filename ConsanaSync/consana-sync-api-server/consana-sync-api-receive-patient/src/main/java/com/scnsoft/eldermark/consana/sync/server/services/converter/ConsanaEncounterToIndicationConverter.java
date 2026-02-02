package com.scnsoft.eldermark.consana.sync.server.services.converter;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Indication;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import com.scnsoft.eldermark.consana.sync.server.model.enums.CodeSystem;
import com.scnsoft.eldermark.consana.sync.server.services.CcdCodeService;
import com.scnsoft.eldermark.consana.sync.server.utils.FhirConversionUtils;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Encounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Transactional(propagation = Propagation.SUPPORTS, noRollbackFor = Exception.class)
public class ConsanaEncounterToIndicationConverter {

    @Autowired
    private CcdCodeService ccdCodeService;

    @Autowired
    private FhirConversionUtils fhirConversionUtils;

    public List<Indication> convert(Encounter source, Resident resident, List<Indication> target) {
        if (!source.hasReason()) {
            if (target != null) {
                target.clear();
            }
            return target;
        }

        var indications = new ArrayList<Indication>();
        source.getReason().stream()
                .filter(CodeableConcept::hasCoding)
                .forEach(cc -> {
                    var codes = cc.getCoding().stream()
                            .map(c -> {
                                CodeSystem codeSystem = fhirConversionUtils.getCodeSystem(c, "Encounter");
                                if (codeSystem == null) {
                                    return null;
                                }
                                return ccdCodeService.findOrCreate(c, codeSystem);
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    var code = codes.stream()
                            .filter(c -> CodeSystem.SNOMED_CT.getOid().equals(c.getCodeSystem()))
                            .findFirst()
                            .orElseGet(() -> codes.stream()
                                    .findFirst()
                                    .orElse(null));
                    if (code == null) {
                        return;
                    }

                    Indication indication = FhirConversionUtils.getOrCreateCollectionElement(target, indications.size(), Indication::new);
                    indication.setLegacyId(0L);
                    indication.setLegacyTable("Encounter_NWHIN");
                    indication.setDatabase(resident.getDatabase());
                    indication.setValue(code);
                    indications.add(indication);
                });
        return FhirConversionUtils.updateCollection(indications, target);
    }
}
