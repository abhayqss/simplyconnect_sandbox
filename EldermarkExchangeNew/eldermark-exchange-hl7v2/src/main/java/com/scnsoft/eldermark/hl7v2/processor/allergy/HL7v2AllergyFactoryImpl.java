package com.scnsoft.eldermark.hl7v2.processor.allergy;

import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.Allergy;
import com.scnsoft.eldermark.entity.document.ccd.ReactionObservation;
import com.scnsoft.eldermark.entity.document.ccd.SeverityObservation;
import com.scnsoft.eldermark.entity.document.facesheet.AllergyObservation;
import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.datatype.ISCodedValueForUserDefinedTables;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0128AllergySeverity;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.entity.xds.message.EVNSegmentContainingMessage;
import com.scnsoft.eldermark.entity.xds.segment.AdtAL1AllergySegment;
import com.scnsoft.eldermark.hl7v2.processor.CcdCodeResolverService;
import com.scnsoft.eldermark.hl7v2.processor.DataTypeUtils;
import com.scnsoft.eldermark.service.CcdCodeService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
class HL7v2AllergyFactoryImpl implements HL7v2AllergyFactory {
    public static final String ALLERGY_ADT_LEGACY_TABLE = "Allergy_ADT";
    public static final String ACTIVE_STATUS_CODE = "55561003";
    @Autowired
    private CcdCodeService ccdCodeService;

    @Autowired
    private CcdCodeResolverService ccdCodeResolverService;

    private static final Map<String, String> HL7V2_CCD_SEVERITY_MAP = Map.of(
            "MI", "255604002",
            "MO", "6736007",
            "SV", "24484000"
    );
    private static final Map<String, String> HL7V2_CCD_ALLERGY_MAP = Map.of(
            "AA", "419199007",
            "DA", "416098002",
            "EA", "419199007",
            "FA", "414285001",
            "LA", "419199007",
            "MA", "419199007",
            "MC", "418038007",
            "PA", "419199007"
    );


    @Override
    public Optional<Allergy> createAllergy(Client client, AdtAL1AllergySegment al1, AdtMessage msg) {
        if (!containsRequiredData(al1)) {
            return Optional.empty();
        }
        var allergy = new Allergy();
        allergy.setClientId(client.getId());
        allergy.setClient(client);
        allergy.setOrganizationId(client.getOrganizationId());
        allergy.setOrganization(client.getOrganization());
        allergy.setLegacyId(0);
        allergy.setAl1Id(al1.getId());

        var timeLow = resolveLowTime(msg);
        allergy.setTimeLow(timeLow);

        allergy.setAllergyObservations(Set.of(createAllergyObservation(al1, client.getOrganization(), timeLow)));
        allergy.getAllergyObservations().forEach(ao -> ao.setAllergy(allergy));

        return Optional.of(allergy);
    }

    private boolean containsRequiredData(AdtAL1AllergySegment al1) {
        return !DataTypeUtils.isEmpty(al1.getAllergyCode());
    }

    private Date resolveLowTime(AdtMessage msg) {
        if (msg instanceof EVNSegmentContainingMessage) {
            var evn = ((EVNSegmentContainingMessage) msg).getEvn();
            return Optional.ofNullable(evn.getRecordedDatetime())
                    .or(() -> Optional.ofNullable(msg.getMsh().getDatetime()))
                    .map(Date::from)
                    .orElseGet(Date::new);
        }
        return null;
    }

    private AllergyObservation createAllergyObservation(AdtAL1AllergySegment al, Organization organization, Date timeLow) {
        var allergyObservation = new AllergyObservation();

        allergyObservation.setOrganizationId(organization.getId());
        allergyObservation.setOrganization(organization);
        allergyObservation.setLegacyId(0);
        allergyObservation.setTimeLow(timeLow);

        allergyObservation.setObservationStatusCode(ccdCodeService.findByCodeAndValueSet(ACTIVE_STATUS_CODE, ValueSetEnum.PROBLEM_STATUS));

        processAllergenType(allergyObservation, al.getAllergenType());

        processAllergyCode(allergyObservation, al.getAllergyCode());

        processSeverity(allergyObservation, al.getAllergySeverity(), organization);

        addReactions(allergyObservation, al.getAllergyReactions(), organization);

        return allergyObservation;
    }

    private void processAllergenType(AllergyObservation allergyObservation, CECodedElement allergenType) {
        if (allergenType != null && StringUtils.isNotEmpty(allergenType.getIdentifier())) {
            allergyObservation.setAdverseEventTypeText(
                    CareCoordinationUtils.concat(
                            " - ",
                            allergenType.getIdentifier(),
                            Optional.ofNullable(allergenType.getHl7CodeTable()).map(HL7CodeTable::getValue).orElse(null)
                    )
            );

            var translatedCode = HL7V2_CCD_ALLERGY_MAP.getOrDefault(allergenType.getIdentifier(), null);
            if (translatedCode != null) {
                allergyObservation.setAdverseEventTypeCode(
                        ccdCodeService.findByCodeAndValueSet(translatedCode, ValueSetEnum.ADVERSE_EVENT_TYPE)
                );
            }
        }
    }

    private void processAllergyCode(AllergyObservation allergyObservation, CECodedElement allergyCode) {
        if (allergyCode != null) {
            var allergyCcdCode = ccdCodeResolverService.resolveCode(allergyCode);
            var productText = Stream.of(
                            allergyCode.getText(),
                            allergyCcdCode.map(CcdCode::getDisplayName).orElse(null)
                    )
                    .filter(StringUtils::isNotEmpty)
                    .findFirst()
                    .orElse(null);
            allergyObservation.setProductText(productText);
            allergyObservation.setProductCode(allergyCcdCode.orElse(null));
        }
    }

    private void processSeverity(AllergyObservation allergyObservation,
                                 ISCodedValueForUserDefinedTables<HL7CodeTable0128AllergySeverity> allergySeverity,
                                 Organization organization) {
        if (allergySeverity == null || "U".equals(allergySeverity.getRawCode())) {
            return;
        }

        var severityCode = Optional.ofNullable(HL7V2_CCD_SEVERITY_MAP.getOrDefault(allergySeverity.getRawCode(), null))
                .map(mappedCode -> ccdCodeService.findByCodeAndValueSet(mappedCode, ValueSetEnum.PROBLEM_SEVERITY));

        var severityText = Optional.ofNullable(allergySeverity.getHl7CodeTable())
                .map(HL7CodeTable::getValue)
                .orElse(null);

        if (severityCode.isEmpty() && StringUtils.isEmpty(severityText)) {
            return;
        }

        var severityObservation = new SeverityObservation();
        severityObservation.setLegacyId(0);
        severityObservation.setLegacyTable(ALLERGY_ADT_LEGACY_TABLE);
        severityObservation.setOrganization(organization);
        severityObservation.setOrganizationId(organization.getId());

        severityObservation.setSeverityCode(severityCode.orElse(null));
        severityObservation.setSeverityText(severityText);

        allergyObservation.setSeverityObservation(severityObservation);
    }

    private void addReactions(AllergyObservation allergyObservation, List<String> allergyReactions, Organization organization) {
        if (CollectionUtils.isNotEmpty(allergyReactions)) {
            var reactions = allergyReactions.stream()
                    .map(alReaction -> {
                        var reaction = new ReactionObservation();
                        reaction.setReactionText(alReaction);
                        reaction.setOrganization(organization);
                        reaction.setOrganizationId(organization.getId());
                        reaction.setLegacyId("");
                        reaction.setLegacyTable(ALLERGY_ADT_LEGACY_TABLE);
                        return reaction;
                    })
                    .collect(Collectors.toSet());

            allergyObservation.setReactionObservations(reactions);
        }
    }
}
