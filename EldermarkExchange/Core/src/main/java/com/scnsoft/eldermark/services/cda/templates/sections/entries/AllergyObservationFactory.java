package com.scnsoft.eldermark.services.cda.templates.sections.entries;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.scnsoft.eldermark.cda.service.DocumentTypeResolver;
import com.scnsoft.eldermark.cda.service.DocumentTypeResolverImpl;
import com.scnsoft.eldermark.cda.service.schema.DocumentType;
import com.scnsoft.eldermark.cda.service.schema.enums.CcdElementCodeSystemEnum;
import com.scnsoft.eldermark.cda.service.schema.enums.CcdElementEnum;
import com.scnsoft.eldermark.cda.service.schema.enums.ValueSetEnum;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.CcdCodeService;
import com.scnsoft.eldermark.services.cda.templates.AbstractIgnorableParsableEntry;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.EntryRelationship;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.cda.Participant2;
import org.eclipse.mdht.uml.cda.PlayingEntity;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.PN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author phomal
 * Created on 4/24/2018.
 */
@Component
public class AllergyObservationFactory
        extends AbstractIgnorableParsableEntry<Observation>
        implements IgnorableParsableEntry<Observation> {

    private static final String VALUE_SET_ADVERSE_EVENT_TYPE_OID = "2.16.840.1.113883.3.88.12.3221.6.2";

    private final CcdCodeFactory ccdCodeFactory;

    private final DocumentTypeResolver documentTypeResolver;

    @Autowired
    private CcdCodeService ccdCodeService;

    @Autowired
    private ObservationFactory observationFactory;

    @Autowired
    public AllergyObservationFactory(CcdCodeFactory ccdCodeFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
        documentTypeResolver = new DocumentTypeResolverImpl();
    }

    @Override
    public boolean isEntryIgnored(Observation entry) {
        final EList<Participant2> participants = entry.getParticipants();
        return CollectionUtils.isEmpty(participants);
    }

    public AllergyObservation parse(Observation ccdObservation, Allergy allergy, Resident resident, String LEGACY_TABLE) {
        checkNotNull(ccdObservation);

        final AllergyObservation allergyObservation = new AllergyObservation();
        allergyObservation.setAllergy(allergy);
        allergyObservation.setDatabase(resident.getDatabase());
        allergyObservation.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdObservation.getIds()));

        //parse status code
        final Set<DocumentType> documentTypes = getDocumentTypeResolver().resolve(ccdObservation.getClinicalDocument());
        final Optional<CcdElementCodeSystemEnum> codeSystemByDocumentTypesOptional = CcdElementEnum.PROBLEM_ACT_STATUS.findCodeSystemByDocumentTypes(documentTypes);
        if (codeSystemByDocumentTypesOptional.isPresent()) {
            final ValueSetEnum valueSetEnum = codeSystemByDocumentTypesOptional.get().getValueSetEnum();
            final String statusCode = ccdObservation.getStatusCode().getCode();
            final CcdCode actStatus = ccdCodeService.findOrCreate(statusCode, StringUtils.capitalize(statusCode),
                    valueSetEnum.getCodeSystem(), valueSetEnum.getCodeSystemName());
            allergyObservation.setObservationStatusCode(actStatus);
        }

        Pair<Date, Date> obsEffectiveTime = CcdTransform.IVLTStoHighLowDateOrIVLTStoCenterDateOrTsToDate(ccdObservation.getEffectiveTime());
        if (obsEffectiveTime != null) {
            allergyObservation.setTimeHigh(obsEffectiveTime.getFirst());
            allergyObservation.setTimeLow(obsEffectiveTime.getSecond());
        }

        if (!CollectionUtils.isEmpty(ccdObservation.getValues()) && ccdObservation.getValues().get(0) instanceof CD) {
            // According to HL7 the value of “Observation / value” in an alert observation represents Alert Type
            CD ccdObservationValue = (CD) ccdObservation.getValues().get(0);
            allergyObservation.setAdverseEventTypeCode(ccdCodeFactory.convert(ccdObservationValue, VALUE_SET_ADVERSE_EVENT_TYPE_OID));
            allergyObservation.setAdverseEventTypeText(
                    CcdTransform.EDtoString(ccdObservationValue.getOriginalText(), allergyObservation.getAdverseEventTypeCode()));
        }

        if (!CollectionUtils.isEmpty(ccdObservation.getParticipants()) && ccdObservation.getParticipants().get(0).getParticipantRole() != null
                && ccdObservation.getParticipants().get(0).getParticipantRole().getPlayingEntity() != null) {
            PlayingEntity ccdPlayingEntity = ccdObservation.getParticipants().get(0).getParticipantRole().getPlayingEntity();
            if (!CollectionUtils.isEmpty(ccdPlayingEntity.getNames())) {
                PN name = ccdPlayingEntity.getNames().get(0);
                allergyObservation.setProductText(name.getText());
            } else {
                allergyObservation.setProductText(ccdPlayingEntity.getCode().getDisplayName());
            }
            allergyObservation.setProductCode(ccdCodeFactory.convert(ccdPlayingEntity.getCode()));
        }

        if (!CollectionUtils.isEmpty(ccdObservation.getEntryRelationships())) {
            Set<ReactionObservation> reactionObservations = new HashSet<>();
            com.scnsoft.eldermark.entity.SeverityObservation severityObservation = null;
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
                        if (severityObservation != null) {
                            continue;
                        }
                        severityObservation = observationFactory.parseSeverityObservation(
                                entryRelationship.getObservation(), resident, LEGACY_TABLE);
                        break;
                    case REFR:
                        try {
                            CD observationValue = ObservationFactory.getValue(entryRelationship.getObservation(), CD.class);
                            allergyObservation.setObservationStatusCode(ccdCodeFactory.convert(observationValue));
                        } catch (ClassCastException exc) {
                            exc.printStackTrace();
                        }
                        break;
                }
            }
            allergyObservation.setReactionObservations(reactionObservations);
            allergyObservation.setSeverityObservation(severityObservation);
        }

        return allergyObservation;
    }

    // TODO replace List<EntryRelationship> with List<Observation>
    public Set<AllergyObservation> parse(final List<EntryRelationship> ccdEntryRelationships,
                                         final Allergy allergy,
                                         final Resident resident,
                                         final String LEGACY_TABLE) {
        if (CollectionUtils.isEmpty(ccdEntryRelationships)) {
            return Collections.emptySet();
        }

        final Function<EntryRelationship, AllergyObservation> intoAllergyObservation = new Function<EntryRelationship, AllergyObservation>() {
            @Override
            public AllergyObservation apply(EntryRelationship input) {
                return parse(input.getObservation(), allergy, resident, LEGACY_TABLE);
            }
        };
        final Predicate<EntryRelationship> filter = new Predicate<EntryRelationship>() {
            @Override
            public boolean apply(EntryRelationship input) {
                if (input == null || input.getObservation() == null) {
                    return false;
                }
                return !isEntryIgnored(input.getObservation());
            }
        };
        return FluentIterable.from(ccdEntryRelationships)
                .filter(filter)
                .transform(intoAllergyObservation)
                .toSet();
    }

    public DocumentTypeResolver getDocumentTypeResolver() {
        return documentTypeResolver;
    }
}
