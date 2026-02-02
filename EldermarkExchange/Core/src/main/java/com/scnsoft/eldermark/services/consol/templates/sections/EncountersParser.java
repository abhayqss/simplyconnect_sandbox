package com.scnsoft.eldermark.services.consol.templates.sections;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.Encounter;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.templates.AbstractParsableSection;
import com.scnsoft.eldermark.services.cda.templates.ParsableSection;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.IndicationFactory;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.ObservationFactory;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.ParticipantRoleFactory;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.Performer2Factory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.openhealthtools.mdht.uml.cda.consol.EncountersSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <h1>Encounters</h1> “This section is used to list and describe any healthcare
 * encounters pertinent to the patient’s current health status or historical
 * health history.” [CCD 3.15]
 *
 * @see Encounter
 * @see Indication
 * @see ProblemObservation
 * @see ServiceDeliveryLocation
 * @see Resident
 * @see CcdCode
 */
@Component("consol.EncountersParser")
public class EncountersParser extends AbstractParsableSection<Entry, EncountersSection, Encounter>
        implements ParsableSection<EncountersSection, Encounter> {

    private final CcdCodeFactory ccdCodeFactory;
    private final ObservationFactory observationFactory;
    private final ParticipantRoleFactory participantRoleFactory;
    private final IndicationFactory indicationFactory;
    private final Performer2Factory performer2Factory;

    @Autowired
    public EncountersParser(CcdCodeFactory ccdCodeFactory, ObservationFactory observationFactory,
            ParticipantRoleFactory participantRoleFactory, IndicationFactory indicationFactory,
            Performer2Factory performer2Factory) {
        this.ccdCodeFactory = ccdCodeFactory;
        this.observationFactory = observationFactory;
        this.participantRoleFactory = participantRoleFactory;
        this.indicationFactory = indicationFactory;
        this.performer2Factory = performer2Factory;
    }

    private static final String LEGACY_TABLE = "Encounter_NWHIN";

    @Override
    public boolean isSectionIgnored(EncountersSection section) {
        return super.isSectionIgnored(section);
    }

    @Override
    public boolean isEntryIgnored(Entry entry) {
        final org.eclipse.mdht.uml.cda.Encounter encounter = entry.getEncounter();
        final CD code = encounter.getCode();
        final NullFlavor nullFlavorCode = code.getNullFlavor();
        if (nullFlavorCode != null) {
            if ("UNK".equals(nullFlavorCode.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Encounter> doParseSection(Resident resident, EncountersSection encountersSection) {
        if (!CcdParseUtils.hasContent(encountersSection) || CollectionUtils.isEmpty(encountersSection.getEntries())) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        // TODO Test parsing of fully populated EncounterActivities (contains 0..*
        // Indication, 0..* EncounterDiagnosis, 0..* ServiceDeliveryLocation)
        // TODO Iterate encountersSection.getConsolEncounterActivitiess() instead?
        final List<Encounter> encounters = new ArrayList<>();
        for (Entry ccdEncounterEntry : encountersSection.getEntries()) {
            if (isEntryIgnored(ccdEncounterEntry)) {
                continue;
            }
            if (ccdEncounterEntry.getEncounter() != null) {
                final org.eclipse.mdht.uml.cda.Encounter ccdEncounter = ccdEncounterEntry.getEncounter();
                Encounter encounter = new Encounter();
                encounter.setResident(resident);
                encounter.setDatabase(resident.getDatabase());
                encounter.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdEncounter.getIds()));

                encounter.setEncounterType(ccdCodeFactory.convert(ccdEncounter.getCode()));
                if (encounter.getEncounterType() == null
                        && !CollectionUtils.isEmpty(ccdEncounter.getCode().getTranslations())) {
                    final CD cdTranslation = ccdEncounter.getCode().getTranslations().get(0);
                    encounter.setEncounterType(ccdCodeFactory.convert(cdTranslation));
                }
                encounter.setEncounterTypeText(
                        CcdTransform.EDtoString(ccdEncounter.getText(), encounter.getEncounterType()));
                encounter.setEffectiveTime(CcdParseUtils.parseCenterTime(ccdEncounter.getEffectiveTime()));
                if (encounter.getEffectiveTime() == null) {
                    Pair<Date, Date> effectiveTime = CcdTransform.IVLTStoHighLowDate(ccdEncounter.getEffectiveTime());
                    if (effectiveTime != null) {
                        encounter.setEffectiveTime(effectiveTime.getSecond());
                    }
                }
                if (encounter.getEffectiveTime() == null) {
                    encounter.setEffectiveTime(CcdParseUtils.convertTsToDate(ccdEncounter.getEffectiveTime()));
                }

                if (!CollectionUtils.isEmpty(ccdEncounter.getParticipants())) {
                    final List<ServiceDeliveryLocation> serviceDeliveryLocations = new ArrayList<>();
                    for (Participant2 ccdParticipant : ccdEncounter.getParticipants()) {
                        ParticipantRole ccdParticipantRole = ccdParticipant.getParticipantRole();
                        serviceDeliveryLocations.add(participantRoleFactory
                                .parseServiceDeliveryLocation(ccdParticipantRole, resident, LEGACY_TABLE));
                    }
                    serviceDeliveryLocations.remove(null);
                    encounter.setServiceDeliveryLocations(serviceDeliveryLocations);
                }

                if (!CollectionUtils.isEmpty(ccdEncounter.getEntryRelationships())) {
                    final List<Indication> indications = new ArrayList<>();
                    for (EntryRelationship ccdEntryRelationship : ccdEncounter.getEntryRelationships()) {
                        switch (ccdEntryRelationship.getTypeCode()) {
                        case RSON:
                            Observation ccdObservation = ccdEntryRelationship.getObservation();
                            indications.add(indicationFactory.parseIndication(ccdObservation, resident, LEGACY_TABLE));
                            break;
                        case COMP:
                            Act act = ccdEntryRelationship.getAct();
                            encounter.setProblemObservation(parseEncounterDiagnosis(act, resident));
                            break;
                        }
                    }
                    indications.remove(null);
                    encounter.setIndications(indications);
                }
                
                List<EncounterPerformer> encounterPerformers = new ArrayList<>();
                if (!CollectionUtils.isEmpty(ccdEncounter.getPerformers())) {
                    for (Performer2 ccdPerformer : ccdEncounter.getPerformers()) {
                        final Person person = performer2Factory.parsePerson(ccdPerformer, resident.getDatabase(),
                                LEGACY_TABLE);
                        EncounterPerformer encounterPerformer = new EncounterPerformer();
                        if (person != null) {
                            encounterPerformer.setPerformer(person);
                            if (person.getCode() != null) {
                                encounterPerformer.setProviderCode(person.getCode());
                            }
                        }
                        encounterPerformer.setDatabase(resident.getDatabase());
                        encounterPerformer.setEncounter(encounter);
                        encounterPerformers.add(encounterPerformer);
                    }
                }
                encounter.setEncounterPerformers(encounterPerformers);

                if (StringUtils.isNotEmpty(encounter.getEncounterTypeText())
                        && encounter.getServiceDeliveryLocations() != null) {
                    encounter.setServiceDeliveryLocations(
                            filterEncounterServiceDeliveryLocation(encounter.getServiceDeliveryLocations()));
                    encounters.add(encounter);
                }
            }
        }
        return encounters;
    }

    private List<ServiceDeliveryLocation> filterEncounterServiceDeliveryLocation(
            List<ServiceDeliveryLocation> serviceDeliveryLocations) {
        return FluentIterable.from(serviceDeliveryLocations).filter(new Predicate<ServiceDeliveryLocation>() {
            @Override
            public boolean apply(ServiceDeliveryLocation serviceDeliveryLocation) {
                return StringUtils.isNotEmpty(serviceDeliveryLocation.getName());
            }
        }).toList();
    }

    private ProblemObservation parseEncounterDiagnosis(Act act, Resident resident) {
        if (!CcdParseUtils.hasContent(act) || CollectionUtils.isEmpty(act.getEntryRelationships())) {
            return null;
        }
        ProblemObservation problemObservation = null;
        for (EntryRelationship ccdEntryRelationship : act.getEntryRelationships()) {
            if (ccdEntryRelationship.getTypeCode() == x_ActRelationshipEntryRelationship.SUBJ) {
                problemObservation = observationFactory.parseProblemObservation(ccdEntryRelationship.getObservation(),
                        resident, null);
            }
        }

        return problemObservation;
    }

}
