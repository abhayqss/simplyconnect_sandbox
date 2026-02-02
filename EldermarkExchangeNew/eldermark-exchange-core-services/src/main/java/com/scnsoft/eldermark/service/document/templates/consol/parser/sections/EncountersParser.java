package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.Encounter;
import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.IndicationFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.ObservationFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.ParticipantRoleFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.Performer2Factory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.AbstractParsableSection;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSection;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * <h1>Encounters</h1> “This section is used to list and describe any healthcare
 * encounters pertinent to the patient’s current health status or historical
 * health history.” [CCD 3.15]
 *
 * @see Encounter
 * @see Indication
 * @see ProblemObservation
 * @see ServiceDeliveryLocation
 * @see Client
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
        return !CcdParseUtils.hasContent(section) || CollectionUtils.isEmpty(section.getEntries());
    }

    @Override
    public boolean isEntryIgnored(Entry entry) {
        var encounter = entry.getEncounter();
        var code = encounter.getCode();
        final NullFlavor nullFlavorCode = code.getNullFlavor();

        return NullFlavor.UNK.equals(nullFlavorCode);
    }

    @Override
    public List<Encounter> doParseSection(Client resident, EncountersSection encountersSection) {
        Objects.requireNonNull(resident);

        // TODO Test parsing of fully populated EncounterActivities (contains 0..*
        // Indication, 0..* EncounterDiagnosis, 0..* ServiceDeliveryLocation)
        // TODO Iterate encountersSection.getConsolEncounterActivitiess() instead?
        final List<Encounter> encounters = new ArrayList<>();
        for (var ccdEncounterEntry : encountersSection.getEntries()) {
            if (isEntryIgnored(ccdEncounterEntry)) {
                continue;
            }
            if (ccdEncounterEntry.getEncounter() != null) {
                var ccdEncounter = ccdEncounterEntry.getEncounter();
                Encounter encounter = new Encounter();
                encounter.setClient(resident);
                encounter.setOrganization(resident.getOrganization());
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
                    var serviceDeliveryLocations = new ArrayList<ServiceDeliveryLocation>();
                    for (Participant2 ccdParticipant : ccdEncounter.getParticipants()) {
                        ParticipantRole ccdParticipantRole = ccdParticipant.getParticipantRole();
                        var deliveryLocation = participantRoleFactory
                                .parseServiceDeliveryLocation(ccdParticipantRole, resident, LEGACY_TABLE);
                        if (deliveryLocation != null) {
                            serviceDeliveryLocations.add(deliveryLocation);
                        }
                    }
                    encounter.setServiceDeliveryLocations(serviceDeliveryLocations);
                }

                if (CollectionUtils.isNotEmpty(ccdEncounter.getEntryRelationships())) {
                    var indications = new ArrayList<Indication>();
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
                    encounter.setIndications(indications.stream().filter(Objects::nonNull).collect(Collectors.toList()));
                }

                List<EncounterPerformer> encounterPerformers = new ArrayList<>();
                if (!CollectionUtils.isEmpty(ccdEncounter.getPerformers())) {
                    for (Performer2 ccdPerformer : ccdEncounter.getPerformers()) {
                        final Person person = performer2Factory.parsePerson(ccdPerformer, resident.getOrganization(),
                                LEGACY_TABLE);
                        EncounterPerformer encounterPerformer = new EncounterPerformer();
                        if (person != null) {
                            encounterPerformer.setPerformer(person);
                            if (person.getCode() != null) {
                                encounterPerformer.setProviderCode(person.getCode());
                            }
                        }
                        encounterPerformer.setOrganization(resident.getOrganization());
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

    private List<ServiceDeliveryLocation> filterEncounterServiceDeliveryLocation(List<ServiceDeliveryLocation> serviceDeliveryLocations) {
        return serviceDeliveryLocations.stream()
                .filter(serviceDeliveryLocation -> StringUtils.isNotEmpty(serviceDeliveryLocation.getName()))
                .collect(Collectors.toList());
    }

    private ProblemObservation parseEncounterDiagnosis(Act act, Client resident) {
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
