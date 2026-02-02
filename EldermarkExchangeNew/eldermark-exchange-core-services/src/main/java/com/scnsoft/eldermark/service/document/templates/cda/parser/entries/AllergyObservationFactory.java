package com.scnsoft.eldermark.service.document.templates.cda.parser.entries;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.Allergy;
import com.scnsoft.eldermark.entity.document.ccd.ReactionObservation;
import com.scnsoft.eldermark.entity.document.ccd.SeverityObservation;
import com.scnsoft.eldermark.entity.document.facesheet.AllergyObservation;
import com.scnsoft.eldermark.service.document.cda.CcdCodeCustomService;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.mdht.uml.cda.EntryRelationship;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.cda.PlayingEntity;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.II;
import org.eclipse.mdht.uml.hl7.datatypes.PN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AllergyObservationFactory
        implements IgnorableParsableEntry<Observation> {

    private static final String VALUE_SET_ADVERSE_EVENT_TYPE_OID = "2.16.840.1.113883.3.88.12.3221.6.2";

    private final CcdCodeFactory ccdCodeFactory;

    @Autowired
    private CcdCodeCustomService ccdCodeService;

    @Autowired
    private ObservationFactory observationFactory;

    @Autowired
    public AllergyObservationFactory(CcdCodeFactory ccdCodeFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
    }

    @Override
    public boolean isEntryIgnored(Observation entry) {
        return CollectionUtils.isEmpty(entry.getParticipants());
    }

    // TODO replace List<EntryRelationship> with List<Observation>
    public Set<AllergyObservation> parse(final List<EntryRelationship> ccdEntryRelationships,
                                         final Allergy allergy,
                                         final Client resident,
                                         final String LEGACY_TABLE) {
        return CollectionUtils.emptyIfNull(ccdEntryRelationships).stream()
                .filter(this::filterEntryRelationship)
                .map(er -> parse(er.getObservation(), allergy, resident, LEGACY_TABLE))
                .collect(Collectors.toSet());
    }

    private boolean filterEntryRelationship(EntryRelationship input) {
        if (input == null || input.getObservation() == null) {
            return false;
        }
        return !isEntryIgnored(input.getObservation());
    }

    private AllergyObservation parse(Observation ccdObservation, Allergy allergy, Client resident, String LEGACY_TABLE) {
        Objects.requireNonNull(ccdObservation);

        final AllergyObservation allergyObservation = new AllergyObservation();
        allergyObservation.setAllergy(allergy);
        allergyObservation.setOrganization(resident.getOrganization());
        allergyObservation.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdObservation.getIds()));

        Pair<Date, Date> obsEffectiveTime = CcdTransform.IVLTStoHighLowDateOrIVLTStoCenterDateOrTsToDate(ccdObservation.getEffectiveTime());
        if (obsEffectiveTime != null) {
            allergyObservation.setTimeHigh(obsEffectiveTime.getFirst());
            allergyObservation.setTimeLow(obsEffectiveTime.getSecond());
        }

        if (CollectionUtils.isNotEmpty(ccdObservation.getValues()) && ccdObservation.getValues().get(0) instanceof CD) {
            // According to HL7 the value of “Observation / value” in an alert observation represents Alert Type
            CD ccdObservationValue = (CD) ccdObservation.getValues().get(0);
            allergyObservation.setAdverseEventTypeCode(ccdCodeFactory.convert(ccdObservationValue, VALUE_SET_ADVERSE_EVENT_TYPE_OID));
            allergyObservation.setAdverseEventTypeText(
                    CcdTransform.EDtoString(ccdObservationValue.getOriginalText(), allergyObservation.getAdverseEventTypeCode()));
        }

        if (CollectionUtils.isNotEmpty(ccdObservation.getParticipants()) && ccdObservation.getParticipants().get(0).getParticipantRole() != null
                && ccdObservation.getParticipants().get(0).getParticipantRole().getPlayingEntity() != null) {
            PlayingEntity ccdPlayingEntity = ccdObservation.getParticipants().get(0).getParticipantRole().getPlayingEntity();
            if (CollectionUtils.isNotEmpty(ccdPlayingEntity.getNames())) {
                PN name = ccdPlayingEntity.getNames().get(0);
                allergyObservation.setProductText(name.getText());
            } else {
                allergyObservation.setProductText(ccdPlayingEntity.getCode().getDisplayName());
            }
            allergyObservation.setProductCode(ccdCodeFactory.convert(ccdPlayingEntity.getCode()));
        }

        if (CollectionUtils.isNotEmpty(ccdObservation.getEntryRelationships())) {
            Set<ReactionObservation> reactionObservations = new HashSet<>();
            SeverityObservation severityObservation = null;
            for (EntryRelationship entryRelationship : ccdObservation.getEntryRelationships()) {
                switch (entryRelationship.getTypeCode()) {
                    case MFST:
                        ReactionObservation reactionObservation = observationFactory.parseReactionObservation(
                                entryRelationship.getObservation(), resident, LEGACY_TABLE);
                        if (reactionObservation != null) {
                            reactionObservations.add(reactionObservation);
                        }
                        break;
                    case SUBJ:
                        var observation = entryRelationship.getObservation();
                        var templateIdRoot = observation.getTemplateIds().stream().map(II::getRoot).findFirst().orElse(null);
                        if ("2.16.840.1.113883.10.20.22.4.28".equals(templateIdRoot)) {
                            //Allergy Status Observation
                            try {
                                CD observationValue = ObservationFactory.getValue(observation, CD.class);
                                allergyObservation.setObservationStatusCode(ccdCodeFactory.convert(observationValue));
                            } catch (ClassCastException exc) {
                                exc.printStackTrace();
                            }
                        } else if ("2.16.840.1.113883.10.20.22.4.8".equals(templateIdRoot)) {
                            //Severity Observation
                            if (severityObservation != null) {
                                continue;
                            }
                            severityObservation = observationFactory.parseSeverityObservation(observation, resident,
                                    LEGACY_TABLE);
                        }
                        break;
                }
            }
            allergyObservation.setReactionObservations(reactionObservations);
            allergyObservation.setSeverityObservation(severityObservation);
        }

        return allergyObservation;
    }
}
