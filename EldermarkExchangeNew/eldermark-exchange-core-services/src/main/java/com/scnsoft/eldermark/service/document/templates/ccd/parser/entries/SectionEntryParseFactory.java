package com.scnsoft.eldermark.service.document.templates.ccd.parser.entries;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.CommunityAddress;
import com.scnsoft.eldermark.entity.community.CommunityTelecom;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.Author;
import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.entity.document.ccd.codes.SectionTypeCode;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.ObservationFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.Performer2Factory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.PersonFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.Procedure;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.eclipse.mdht.uml.hl7.vocab.x_DocumentSubstanceMood;
import org.openhealthtools.mdht.uml.cda.ccd.AgeObservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by averazub on 1/24/2017.
 */
@Component
public class SectionEntryParseFactory {

    @Autowired
    private CcdCodeFactory ccdCodeFactory;

    @Autowired
    private PersonFactory personFactory;

    @Autowired
    private Performer2Factory performer2Factory;

    private static final Logger logger = LoggerFactory.getLogger(SectionEntryParseFactory.class);

    public ProductInstance parseProductInstance(ParticipantRole sourceProduct, Client client) {
        ProductInstance targetInstance = new ProductInstance();
        targetInstance.setOrganization(client.getOrganization());
        targetInstance.setLegacyId(CcdParseUtils.getFirstIdExtension(sourceProduct.getIds()));

        // TODO according to specs the UDI should be sent in the participantRole/id. Why
        // is it ignored here?
        // see
        // http://ccda.art-decor.org/ccda-html-20150727T182455/tmp-2.16.840.1.113883.10.20.22.4.37-2013-01-31T000000.html

        if (sourceProduct.getPlayingDevice() != null) {
            targetInstance.setDeviceCode(ccdCodeFactory.convert(sourceProduct.getPlayingDevice().getCode()));
        }

        final Entity scopingEntity = sourceProduct.getScopingEntity();
        if (scopingEntity != null) {
            II scopeEntityId = CcdParseUtils.getFirstNotEmptyValue(scopingEntity.getIds(), II.class);
            if (scopeEntityId != null) {
                // TODO The `root` alone may be the entire instance identifier. What if
                // `extension` is null here?
                targetInstance.setScopingEntityId(scopeEntityId.getExtension());
            }
            if (CcdParseUtils.hasContent(scopingEntity.getDesc())) {
                targetInstance.setScopingEntityDescription(scopingEntity.getDesc().getText());
            }
            if (CcdParseUtils.hasContent(scopingEntity.getCode())) {
                final CcdCode scopingEntityCode = ccdCodeFactory.convert(scopingEntity.getCode());
                targetInstance.setScopingEntityCode(scopingEntityCode);
            }
        }

        return targetInstance;
    }

    public ProblemObservation parseProblemObservation(org.eclipse.mdht.uml.cda.Observation srcObservation,
                                                      Client client, Problem targetProblem) {
        if (!CcdParseUtils.hasContent(srcObservation) || client == null) {
            return null;
        }
        checkNotNull(client);

        ProblemObservation targetObservation = new ProblemObservation();
        targetObservation.setLegacyId(CcdParseUtils.getFirstIdExtension(srcObservation.getIds()));
        targetObservation.setOrganization(client.getOrganization());

        targetObservation.setNegationInd(srcObservation.getNegationInd());
        targetObservation.setProblemType(ccdCodeFactory.convert(srcObservation.getCode()));

        Pair<Date, Date> observationEffectiveTime = CcdTransform.IVLTStoHighLowDate(srcObservation.getEffectiveTime());
        if (observationEffectiveTime != null) {
            targetObservation.setProblemDateTimeHigh(observationEffectiveTime.getFirst());
            targetObservation.setProblemDateTimeLow(observationEffectiveTime.getSecond());
        }

        for (Observation srcObservationEntry : srcObservation.getObservations()) {
            SectionTypeCode type = SectionTypeCode.getByCode(srcObservationEntry.getCode().getCode(),
                    srcObservationEntry.getCode().getCodeSystem());
            if (type != null) {
                switch (type) {
                    case AGE_OBSERVATION:
                        Pair<String, Integer> observation = parseAgeObservation((AgeObservation) srcObservationEntry);
                        if (observation != null) {
                            targetObservation.setAgeObservationValue(observation.getSecond());
                            targetObservation.setAgeObservationUnit(observation.getFirst());
                        }
                        break;
                    case STATUS_OBSERVATION:
                        CD problemValue = ObservationFactory.getValue(srcObservationEntry, CD.class);
                        targetObservation.setProblemStatusCode(ccdCodeFactory.convert(problemValue));
                        ED problemStatusText = srcObservationEntry.getText();
                        targetObservation.setProblemStatusText(
                                CcdTransform.EDtoString(problemStatusText, targetObservation.getProblemStatusCode()));
                        break;
                    case HEALTH_STATUS_OBSERVATION:
                        CD healthStatusValue = ObservationFactory.getValue(srcObservationEntry, CD.class);
                        targetObservation.setHealthStatusCode(ccdCodeFactory.convert(healthStatusValue));
                        ED healthStatusText = srcObservationEntry.getText();
                        targetObservation.setHealthStatusObservationText(
                                CcdTransform.EDtoString(healthStatusText, targetObservation.getHealthStatusCode()));
                }
            }
        }

        CD problemCode = ObservationFactory.getValue(srcObservation, CD.class);
        if (problemCode != null) {
            targetObservation.setProblemCode(ccdCodeFactory.convert(problemCode));
            targetObservation
                    .setProblemName(CcdTransform.EDtoString(srcObservation.getText(), problemCode.getDisplayName()));

            StringBuilder concatTranslationCodes = new StringBuilder();
            concatTranslationCodes.append(problemCode.getCode());
            StringBuilder concatTranslationCodeSets = new StringBuilder();
            concatTranslationCodeSets.append(problemCode.getCodeSystemName());

            Set<CcdCode> translations = new HashSet<CcdCode>();
            if (!CollectionUtils.isEmpty(problemCode.getTranslations())) {
                for (CD item : problemCode.getTranslations()) {
                    if (CcdParseUtils.hasContent(item)) {
                        CcdCode translationCode = ccdCodeFactory.convert(item);
                        if (translationCode != null) {
                            translations.add(translationCode);
                        }
                        concatTranslationCodes.append("\n").append(item.getCode());
                        concatTranslationCodeSets.append("\n").append(item.getCodeSystemName());
                    }
                }
            }

            targetObservation.setTranslations(translations);
            targetObservation.setProblemIcdCode(concatTranslationCodes.toString());
            targetObservation.setProblemIcdCodeSet(concatTranslationCodeSets.toString());
        }

        targetObservation.setProblem(targetProblem);
        return targetObservation;
    }

    public ServiceDeliveryLocation parseServiceDeliveryLocation(ParticipantRole ccdParticipantRole, Client client,
                                                                String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdParticipantRole) || client == null) {
            return null;
        }
        checkNotNull(client);

        ServiceDeliveryLocation serviceDeliveryLocation = new ServiceDeliveryLocation();
        serviceDeliveryLocation.setOrganization(client.getOrganization());
        serviceDeliveryLocation.setCode(ccdCodeFactory.convert(ccdParticipantRole.getCode()));
        if (ccdParticipantRole.getPlayingEntity() != null
                && !CollectionUtils.isEmpty(ccdParticipantRole.getPlayingEntity().getNames())) {
            PN name = ccdParticipantRole.getPlayingEntity().getNames().get(0);
            serviceDeliveryLocation.setName(name.getText());
            ED desc = ccdParticipantRole.getPlayingEntity().getDesc();
            serviceDeliveryLocation.setDescription(CcdTransform.EDtoString(desc));
        }
        if (!CollectionUtils.isEmpty(ccdParticipantRole.getAddrs())) {
            List<CommunityAddress> addresses = new ArrayList<CommunityAddress>();
            for (AD ccdAddress : ccdParticipantRole.getAddrs()) {
                if (CcdParseUtils.hasContent(ccdAddress)) {
                    CommunityAddress address = CcdParseUtils.createAddress(ccdAddress, client.getOrganization(),
                            client.getCommunity(), legacyTable);
                    addresses.add(address);
                }
            }
            serviceDeliveryLocation.setAddresses(addresses);
        }
        if (!CollectionUtils.isEmpty(ccdParticipantRole.getTelecoms())) {
            List<CommunityTelecom> telecoms = new ArrayList<CommunityTelecom>();
            for (TEL ccdTelecom : ccdParticipantRole.getTelecoms()) {
                if (CcdParseUtils.hasContent(ccdTelecom)) {
                    CommunityTelecom telecom = CcdParseUtils.createTelecom(ccdTelecom, client.getOrganization(),
                            client.getCommunity(), legacyTable);
                    telecoms.add(telecom);
                }
            }
            serviceDeliveryLocation.setTelecoms(telecoms);
        }

        return serviceDeliveryLocation;
    }

    public static Pair<String, Integer> parseAgeObservation(AgeObservation ageObservation) {
        if (!CcdParseUtils.hasContent(ageObservation) || CollectionUtils.isEmpty(ageObservation.getValues())) {
            return null;
        }

        String unit = null;
        Integer age = null;

        ANY any = ageObservation.getValues().get(0);
        if (any instanceof PQ) {
            PQ ageValue = ObservationFactory.getValue(ageObservation, PQ.class);
            if (CcdParseUtils.hasContent(ageValue)) {
                age = ageValue.getValue().intValue();
            }
            unit = ageValue.getUnit();
        } else if (any instanceof INT) {
            INT ageValue = ObservationFactory.getValue(ageObservation, INT.class);
            age = ageValue.getValue().intValue();
        }

        return new Pair<String, Integer>(unit, age);
    }

    public Indication parseIndication(Observation ccdObservation, Client client, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdObservation) || client == null) {
            return null;
        }

        Indication indication = new Indication();
        indication.setOrganization(client.getOrganization());
        // TODO: inbound ID type is String
        indication.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdObservation.getIds()));
        indication.setLegacyTable(legacyTable);
        indication.setCode(ccdCodeFactory.convert(ccdObservation.getCode()));

        Pair<Date, Date> highLowTime = CcdTransform.IVLTStoHighLowDate(ccdObservation.getEffectiveTime());
        if (highLowTime != null) {
            indication.setTimeHigh(highLowTime.getFirst());
            indication.setTimeLow(highLowTime.getSecond());
        }

        if (!CollectionUtils.isEmpty(ccdObservation.getValues()) && ccdObservation.getValues().get(0) instanceof CD) {
            indication.setValue(ccdCodeFactory.convert((CD) ccdObservation.getValues().get(0)));
        }

        return indication;
    }

    public Author parseAuthor(org.eclipse.mdht.uml.cda.Author ccdAuthor, Client client, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdAuthor) || client == null) {
            return null;
        }
        checkNotNull(client);

        Author author = new Author();
        author.setClient(client);
        author.setOrganization(client.getOrganization());
        author.setLegacyTable(legacyTable);
        author.setLegacyId(0L);

        author.setTime(CcdParseUtils.convertTsToDate(ccdAuthor.getTime()));
        AssignedAuthor ccdAssignedAuthor = ccdAuthor.getAssignedAuthor();
        if (ccdAssignedAuthor.getRepresentedOrganization() != null) {
            Community community = CcdTransform.toCommunity(ccdAssignedAuthor.getRepresentedOrganization(),
                    client.getOrganization(), legacyTable);
            // TODO as for now, organization is set from resident since all nwhin entities
            // will be assigned to single organization
            author.setCommunity(client.getCommunity());
        }
        if (ccdAssignedAuthor.getAssignedPerson() != null) {
            author.setPerson(personFactory.parse(ccdAssignedAuthor, client.getOrganization(), legacyTable));
        }

        return author;
    }

    private MedicationPrecondition parseMedicationPrecondition(Precondition ccdPrecondition, Client client) {
        if (!CcdParseUtils.hasContent(ccdPrecondition) || client == null || ccdPrecondition.getCriterion() == null) {
            return null;
        }

        MedicationPrecondition medicationPrecondition = new MedicationPrecondition();
        medicationPrecondition.setOrganization(client.getOrganization());

        Criterion criterion = ccdPrecondition.getCriterion();
        medicationPrecondition.setCode(ccdCodeFactory.convert(criterion.getCode()));
        medicationPrecondition.setText(CcdTransform.EDtoString(criterion.getText(), medicationPrecondition.getCode()));
        if (criterion.getValue() instanceof CD) {
            medicationPrecondition.setValue(ccdCodeFactory.convert((CD) criterion.getValue()));
        }

        return medicationPrecondition;
    }

    public List<MedicationPrecondition> parsePreconditions(EList<Precondition> preconditions, Client client) {
        if (!CollectionUtils.isEmpty(preconditions)) {
            List<MedicationPrecondition> medicationPreconditions = new ArrayList<MedicationPrecondition>();
            for (Precondition ccdPrecondition : preconditions) {
                MedicationPrecondition medicationPrecondition = parseMedicationPrecondition(ccdPrecondition, client);
                if (medicationPrecondition != null) {
                    medicationPreconditions.add(medicationPrecondition);
                }
            }
            return medicationPreconditions;
        }
        return null;
    }

    public SeverityObservation parseSeverityObservation(Observation observation, Client client, String legacyTable) {
        if (!CcdParseUtils.hasContent(observation) || client == null) {
            return null;
        }
        checkNotNull(client);

        SeverityObservation severityObservation = new SeverityObservation();
        severityObservation.setOrganization(client.getOrganization());
        severityObservation.setLegacyTable(legacyTable);
        severityObservation.setLegacyId(CcdParseUtils.getFirstIdExtension(observation.getIds()));

        CD ccdObservationValue = ObservationFactory.getValue(observation, CD.class);
        severityObservation.setSeverityCode(ccdCodeFactory.convert(ccdObservationValue));
        severityObservation
                .setSeverityText(CcdTransform.EDtoString(observation.getText(), severityObservation.getSeverityCode()));

        return severityObservation;
    }

    public ReactionObservation parseReactionObservation(Observation observation, Client client, String legacyTable) {
        if (!CcdParseUtils.hasContent(observation) || client == null) {
            return null;
        }
        checkNotNull(client);

        ReactionObservation reactionObservation = new ReactionObservation();
        reactionObservation.setLegacyTable(legacyTable);
        reactionObservation.setLegacyId(CcdParseUtils.getFirstIdExtensionStr(observation.getIds()));
        reactionObservation.setOrganization(client.getOrganization());

        Pair<Date, Date> effectiveTimes = CcdTransform.IVLTStoHighLowDate(observation.getEffectiveTime());
        if (effectiveTimes != null) {
            reactionObservation.setTimeHigh(effectiveTimes.getFirst());
            reactionObservation.setTimeLow(effectiveTimes.getSecond());
        }

        CD ccdObservationValue = ObservationFactory.getValue(observation, CD.class);
        reactionObservation.setReactionCode(ccdCodeFactory.convert(ccdObservationValue));
        reactionObservation
                .setReactionText(CcdTransform.EDtoString(observation.getText(), reactionObservation.getReactionCode()));

        if (!CollectionUtils.isEmpty(observation.getEntryRelationships())) {
            List<SeverityObservation> severityObservations = null;
            List<Medication> medications = null;
            List<ProcedureActivity> procedureActivities = null;
            for (EntryRelationship ccdEntryRelationship : observation.getEntryRelationships()) {
                if (ccdEntryRelationship.getObservation() != null) {
                    if (severityObservations == null) {
                        severityObservations = new ArrayList<SeverityObservation>();
                    }
                    severityObservations
                            .add(parseSeverityObservation(ccdEntryRelationship.getObservation(), client, legacyTable));
                }
                if (ccdEntryRelationship.getSubstanceAdministration() != null) {
                    if (medications == null) {
                        medications = new ArrayList<Medication>();
                    }
                    medications.add(parseMedicationActivity(ccdEntryRelationship.getSubstanceAdministration(), client,
                            legacyTable));
                }
                if (ccdEntryRelationship.getProcedure() != null) {
                    if (procedureActivities == null) {
                        procedureActivities = new ArrayList<ProcedureActivity>();
                    }
                    procedureActivities
                            .add(parseProcedureActivity(ccdEntryRelationship.getProcedure(), client, legacyTable));
                }

            }
            reactionObservation.setSeverityObservations(severityObservations);
            reactionObservation.setMedications(medications);
            reactionObservation.setProcedureActivities(procedureActivities);
        }

        return reactionObservation;
    }

    private DrugVehicle parseDrugVehicle(ParticipantRole participantRole, Client client) {
        if (!CcdParseUtils.hasContent(participantRole) || client == null
                || participantRole.getPlayingEntity() == null) {
            return null;
        }
        checkNotNull(client);

        DrugVehicle drugVehicle = new DrugVehicle();
        drugVehicle.setOrganization(client.getOrganization());

        PlayingEntity ccdPlayingEntity = participantRole.getPlayingEntity();
        drugVehicle.setCode(ccdCodeFactory.convert(ccdPlayingEntity.getCode()));
        PN pn = CcdParseUtils.getFirstNotEmptyValue(ccdPlayingEntity.getNames(), PN.class);
        if (pn != null) {
            drugVehicle.setName(pn.getText());
        }

        return drugVehicle;
    }

    public List<DrugVehicle> parseDrugVehicles(EList<Participant2> participants, Client client) {
        if (!CollectionUtils.isEmpty(participants)) {
            List<DrugVehicle> drugVehicles = new ArrayList<DrugVehicle>();
            for (Participant2 ccdParticipant : participants) {
                DrugVehicle drugVehicle = parseDrugVehicle(ccdParticipant.getParticipantRole(), client);
                if (drugVehicle != null) {
                    drugVehicles.add(drugVehicle);
                }
            }
            return drugVehicles;
        }
        return null;
    }

    // TODO Refactoring: extract common parts of parseProcedureObservation,
    // parseProcedureAct, and parseProcedureActivity
    public ProcedureActivity parseProcedureObservation(Observation ccdObservation, Client client, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdObservation) || client == null) {
            return null;
        }
        checkNotNull(client);

        ProcedureActivity procedureActivity = new ProcedureActivity();
        procedureActivity.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdObservation.getIds()));
        procedureActivity.setOrganization(client.getOrganization());

        if (ccdObservation.getMoodCode() != null) {
            procedureActivity.setMoodCode(ccdObservation.getMoodCode().getLiteral());
        }

        CD code = ccdObservation.getCode();
        if (code != null) {
            procedureActivity.setProcedureType(ccdCodeFactory.convert(code));
            procedureActivity.setProcedureTypeText(
                    CcdTransform.EDtoString(code.getOriginalText(), procedureActivity.getProcedureType()));
        }
        procedureActivity.setStatusCode(CcdTransform.EDtoString(
                ccdObservation.getStatusCode() != null ? ccdObservation.getStatusCode().getOriginalText() : null));

        Pair<Date, Date> effectiveTimes = CcdTransform.IVLTStoHighLowDate(ccdObservation.getEffectiveTime());
        if (effectiveTimes != null) {
            procedureActivity.setProcedureStarted(effectiveTimes.getSecond());
            procedureActivity.setProcedureStopped(effectiveTimes.getFirst());
        }
        procedureActivity.setPriorityCode(ccdCodeFactory.convert(ccdObservation.getPriorityCode()));

        CD observationValue = ObservationFactory.getValue(ccdObservation, CD.class);
        procedureActivity.setValue(ccdCodeFactory.convert(observationValue));
        procedureActivity.setValueText(CcdTransform.EDtoString(ccdObservation.getText()));

        if (!CollectionUtils.isEmpty(ccdObservation.getMethodCodes())) {
            procedureActivity.setMethodCode(ccdCodeFactory.convert(ccdObservation.getMethodCodes().get(0)));
        }

        procedureActivity.setBodySiteCodes(parseBodySiteCodes(ccdObservation.getTargetSiteCodes()));

        if (!CollectionUtils.isEmpty(ccdObservation.getPerformers())) {
            // TODO added default organization
            Set<Community> communities = new HashSet<Community>();
            communities.add(client.getCommunity());
            procedureActivity.setPerformers(communities);
        }

        if (!CollectionUtils.isEmpty(ccdObservation.getParticipants())) {
            Set<ServiceDeliveryLocation> serviceDeliveryLocations = null;
            for (Participant2 ccdParticipant2 : ccdObservation.getParticipants()) {
                switch (ccdParticipant2.getTypeCode()) {
                    case LOC:
                        if (serviceDeliveryLocations == null) {
                            serviceDeliveryLocations = new HashSet<ServiceDeliveryLocation>();
                        }
                        serviceDeliveryLocations.add(
                                parseServiceDeliveryLocation(ccdParticipant2.getParticipantRole(), client, legacyTable));
                        break;
                    default:
                        break;
                }
                procedureActivity.setServiceDeliveryLocations(serviceDeliveryLocations);
            }
        }

        if (!CollectionUtils.isEmpty(ccdObservation.getEntryRelationships())) {
            Set<Indication> indications = null;
            Set<String> encounterIds = null;
            for (EntryRelationship ccdEntryRelationship : ccdObservation.getEntryRelationships()) {
                switch (ccdEntryRelationship.getTypeCode()) {
                    case COMP:
                        if (CcdParseUtils.hasContent(ccdEntryRelationship.getSubstanceAdministration())) {
                            procedureActivity.setMedication(parseMedicationActivity(
                                    ccdEntryRelationship.getSubstanceAdministration(), client, legacyTable));
                        } else {
                            if (encounterIds == null) {
                                encounterIds = new HashSet<String>();
                            }
                            if (ccdEntryRelationship.getEncounter() != null) {
                                // TODO check on real data
                                Pair<String, String> encounterId = CcdParseUtils
                                        .getFirstRootAndExt(ccdEntryRelationship.getEncounter().getIds());
                                if (encounterId != null) {
                                    encounterIds.add(encounterId.getSecond());
                                }
                            }
                        }
                        break;
                    case SUBJ:
                        procedureActivity.setInstructions(parseInstructions(ccdEntryRelationship.getAct(), client));
                        break;
                    case RSON:
                        if (indications == null) {
                            indications = new HashSet<Indication>();
                        }
                        Indication indication = parseIndication(ccdEntryRelationship.getObservation(), client, legacyTable);
                        indications.add(indication);
                        break;
                    default:
                        break;
                }
            }
            procedureActivity.setIndications(indications);
            procedureActivity.setEncounterIds(encounterIds);
        }

        return procedureActivity;
    }

    public ProcedureActivity parseProcedureAct(org.eclipse.mdht.uml.cda.Act ccdAct, Client client, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdAct) || client == null) {
            return null;
        }
        checkNotNull(client);

        ProcedureActivity procedureActivity = new ProcedureActivity();
        procedureActivity.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdAct.getIds()));
        procedureActivity.setOrganization(client.getOrganization());

        if (ccdAct.getMoodCode() != null) {
            procedureActivity.setMoodCode(ccdAct.getMoodCode().getLiteral());
        }

        CD code = ccdAct.getCode();
        if (code != null) {
            procedureActivity.setProcedureType(ccdCodeFactory.convert(code));
            procedureActivity.setProcedureTypeText(
                    CcdTransform.EDtoString(code.getOriginalText(), procedureActivity.getProcedureType()));
        }
        procedureActivity.setStatusCode(CcdTransform
                .EDtoString(ccdAct.getStatusCode() != null ? ccdAct.getStatusCode().getOriginalText() : null));

        Pair<Date, Date> effectiveTimes = CcdTransform.IVLTStoHighLowDate(ccdAct.getEffectiveTime());
        if (effectiveTimes != null) {
            procedureActivity.setProcedureStarted(effectiveTimes.getSecond());
            procedureActivity.setProcedureStopped(effectiveTimes.getFirst());
        }
        procedureActivity.setPriorityCode(ccdCodeFactory.convert(ccdAct.getPriorityCode()));

        procedureActivity.setValueText(CcdTransform.EDtoString(ccdAct.getText()));

        // TODO added default organization
        Set<Community> communities = new HashSet<Community>();
        communities.add(client.getCommunity());
        procedureActivity.setPerformers(communities);

        if (!CollectionUtils.isEmpty(ccdAct.getParticipants())) {
            Set<ServiceDeliveryLocation> serviceDeliveryLocations = null;
            for (Participant2 ccdParticipant2 : ccdAct.getParticipants()) {
                switch (ccdParticipant2.getTypeCode()) {
                    case LOC:
                        if (serviceDeliveryLocations == null) {
                            serviceDeliveryLocations = new HashSet<ServiceDeliveryLocation>();
                        }
                        serviceDeliveryLocations.add(
                                parseServiceDeliveryLocation(ccdParticipant2.getParticipantRole(), client, legacyTable));
                        break;
                    default:
                        break;
                }
                procedureActivity.setServiceDeliveryLocations(serviceDeliveryLocations);
            }
        }

        if (!CollectionUtils.isEmpty(ccdAct.getEntryRelationships())) {
            Set<Indication> indications = null;
            Set<String> encounterIds = null;
            for (EntryRelationship ccdEntryRelationship : ccdAct.getEntryRelationships()) {
                switch (ccdEntryRelationship.getTypeCode()) {
                    case COMP:
                        if (CcdParseUtils.hasContent(ccdEntryRelationship.getSubstanceAdministration())) {
                            procedureActivity.setMedication(parseMedicationActivity(
                                    ccdEntryRelationship.getSubstanceAdministration(), client, legacyTable));
                        } else {
                            if (encounterIds == null) {
                                encounterIds = new HashSet<String>();
                            }
                            if (ccdEntryRelationship.getEncounter() != null) {
                                // TODO check on real data
                                Pair<String, String> encounterId = CcdParseUtils
                                        .getFirstRootAndExt(ccdEntryRelationship.getEncounter().getIds());
                                if (encounterId != null) {
                                    encounterIds.add(encounterId.getSecond());
                                }
                            }
                        }
                        break;
                    case SUBJ:
                        procedureActivity.setInstructions(parseInstructions(ccdEntryRelationship.getAct(), client));
                        break;
                    case RSON:
                        if (indications == null) {
                            indications = new HashSet<Indication>();
                        }
                        Indication indication = parseIndication(ccdEntryRelationship.getObservation(), client, legacyTable);
                        if (indication != null) {
                            indications.add(indication);
                        }
                        break;
                    default:
                        break;

                }
            }
            procedureActivity.setIndications(indications);
            procedureActivity.setEncounterIds(encounterIds);
        }

        return procedureActivity;
    }

    public ProcedureActivity parseProcedureActivity(Procedure ccdProcedure, Client client, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdProcedure) || client == null) {
            return null;
        }
        checkNotNull(client);

        ProcedureActivity procedureActivity = new ProcedureActivity();
        procedureActivity.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdProcedure.getIds()));
        procedureActivity.setOrganization(client.getOrganization());

        if (ccdProcedure.getMoodCode() != null) {
            procedureActivity.setMoodCode(ccdProcedure.getMoodCode().getLiteral());
        }

        CD code = ccdProcedure.getCode();
        if (code != null) {
            procedureActivity.setProcedureType(ccdCodeFactory.convert(code));
            procedureActivity.setProcedureTypeText(
                    CcdTransform.EDtoString(code.getOriginalText(), procedureActivity.getProcedureType()));
        }
        procedureActivity.setStatusCode(CcdTransform.EDtoString(
                ccdProcedure.getStatusCode() != null ? ccdProcedure.getStatusCode().getOriginalText() : null));

        Pair<Date, Date> effectiveTimes = CcdTransform.IVLTStoHighLowDate(ccdProcedure.getEffectiveTime());
        if (effectiveTimes != null) {
            procedureActivity.setProcedureStarted(effectiveTimes.getSecond());
            procedureActivity.setProcedureStopped(effectiveTimes.getFirst());
        }
        procedureActivity.setPriorityCode(ccdCodeFactory.convert(ccdProcedure.getPriorityCode()));

        procedureActivity.setValueText(CcdTransform.EDtoString(ccdProcedure.getText()));

        if (!CollectionUtils.isEmpty(ccdProcedure.getMethodCodes())) {
            procedureActivity.setMethodCode(ccdCodeFactory.convert(ccdProcedure.getMethodCodes().get(0)));
        }

        procedureActivity.setBodySiteCodes(parseBodySiteCodes(ccdProcedure.getTargetSiteCodes()));

        if (!CollectionUtils.isEmpty(ccdProcedure.getSpecimens())) {
            Set<String> specimenIds = new HashSet<String>();
            for (Specimen ccdSpecimen : ccdProcedure.getSpecimens()) {
                if (ccdSpecimen.getSpecimenRole() != null
                        && !CollectionUtils.isEmpty(ccdSpecimen.getSpecimenRole().getIds())) {
                    // TODO check on real data
                    specimenIds.add(ccdSpecimen.getSpecimenRole().getIds().get(0).getExtension());
                }
            }
            specimenIds.remove(null);
            procedureActivity.setSpecimenIds(specimenIds);
        }

        // TODO added default organization
        Set<Community> communities = new HashSet<Community>();
        communities.add(client.getCommunity());
        procedureActivity.setPerformers(communities);

        if (!CollectionUtils.isEmpty(ccdProcedure.getParticipants())) {
            Set<ProductInstance> productInstances = null;
            Set<ServiceDeliveryLocation> serviceDeliveryLocations = null;
            for (Participant2 ccdParticipant2 : ccdProcedure.getParticipants()) {
                switch (ccdParticipant2.getTypeCode()) {
                    case DEV:
                        if (productInstances == null) {
                            productInstances = new HashSet<ProductInstance>();
                        }
                        productInstances.add(parseProductInstance(ccdParticipant2.getParticipantRole(), client));
                        break;
                    case LOC:
                        if (serviceDeliveryLocations == null) {
                            serviceDeliveryLocations = new HashSet<ServiceDeliveryLocation>();
                        }
                        serviceDeliveryLocations.add(
                                parseServiceDeliveryLocation(ccdParticipant2.getParticipantRole(), client, legacyTable));
                        break;
                    default:
                        break;
                }
                procedureActivity.setProductInstances(productInstances);
                procedureActivity.setServiceDeliveryLocations(serviceDeliveryLocations);
            }
        }

        if (!CollectionUtils.isEmpty(ccdProcedure.getEntryRelationships())) {
            Set<Indication> indications = null;
            Set<String> encounterIds = null;
            for (EntryRelationship ccdEntryRelationship : ccdProcedure.getEntryRelationships()) {
                switch (ccdEntryRelationship.getTypeCode()) {
                    case COMP:
                        if (CcdParseUtils.hasContent(ccdEntryRelationship.getSubstanceAdministration())) {
                            procedureActivity.setMedication(parseMedicationActivity(
                                    ccdEntryRelationship.getSubstanceAdministration(), client, legacyTable));
                        } else {
                            if (encounterIds == null) {
                                encounterIds = new HashSet<String>();
                            }
                            if (ccdEntryRelationship.getEncounter() != null
                                    && !CollectionUtils.isEmpty(ccdEntryRelationship.getEncounter().getIds())) {
                                // TODO check on real data
                                String idExtension = ccdEntryRelationship.getEncounter().getIds().get(0).getExtension();
                                if (idExtension != null) {
                                    encounterIds.add(idExtension);
                                }
                            }
                        }
                        break;
                    case SUBJ:
                        procedureActivity.setInstructions(parseInstructions(ccdEntryRelationship.getAct(), client));
                        break;
                    case RSON:
                        if (indications == null) {
                            indications = new HashSet<Indication>();
                        }
                        Indication indication = parseIndication(ccdEntryRelationship.getObservation(), client, legacyTable);
                        if (indication != null) {
                            indications.add(indication);
                        }
                        break;
                    default:
                        break;

                }
            }
            procedureActivity.setIndications(indications);
            procedureActivity.setEncounterIds(encounterIds);
        }

        return procedureActivity;
    }

    private Set<CcdCode> parseBodySiteCodes(EList<CD> targetSiteCodes) {
        if (!CollectionUtils.isEmpty(targetSiteCodes)) {
            Set<CcdCode> bsCodes = new HashSet<CcdCode>();
            for (CD ccdCode : targetSiteCodes) {
                CcdCode code = ccdCodeFactory.convert(ccdCode);
                if (code != null) {
                    bsCodes.add(code);
                }
            }
            return bsCodes;
        }
        return null;
    }

    public Medication parseMedicationActivity(SubstanceAdministration substanceAdministration, Client client,
                                              String legacyTable) {
        if (!CcdParseUtils.hasContent(substanceAdministration) || client == null) {
            return null;
        }
        checkNotNull(client);

        Medication medication = new Medication();
        medication.setClient(client);
        medication.setOrganization(client.getOrganization());
        medication.setLegacyId(CcdParseUtils.getFirstIdExtension(substanceAdministration.getIds()));

        medication.setDeliveryMethod(ccdCodeFactory.convert(substanceAdministration.getCode()));
        // TODO construct freeTextSig from available information
        medication.setFreeTextSig(CcdTransform.EDtoString(substanceAdministration.getText()));
        medication.setStatusCode(
                substanceAdministration.getStatusCode() != null ? substanceAdministration.getStatusCode().getCode()
                        : null);

        for (SXCM_TS sxcm_ts : substanceAdministration.getEffectiveTimes()) {
            if (sxcm_ts instanceof IVL_TS) {
                Pair<Date, Date> effectiveTimes = CcdTransform.IVLTStoHighLowDate((IVL_TS) sxcm_ts);
                if (effectiveTimes != null) {
                    medication.setMedicationStarted(effectiveTimes.getSecond());
                    medication.setMedicationStopped(effectiveTimes.getFirst());
                }
            } else if (sxcm_ts instanceof PIVL_TS) {
                PIVL_TS pivl_ts = (PIVL_TS) sxcm_ts;
                if (CcdParseUtils.hasContent(pivl_ts)) {
                    medication.setAdministrationTimingValue(pivl_ts.getValue());
                    PQ period = pivl_ts.getPeriod();
                    if (CcdParseUtils.hasContent(period)) {
                        medication.setAdministrationTimingPeriod(period.getValue().intValue());
                        medication.setAdministrationTimingUnit(period.getUnit());
                    }
                }
            }
        }

        medication.setRepeatNumber(CcdTransform.INTtoInteger(substanceAdministration.getRepeatNumber()));

        medication.setRoute(ccdCodeFactory.convert(substanceAdministration.getRouteCode()));
        CD siteValue = CcdParseUtils.getFirstNotEmptyValue(substanceAdministration.getApproachSiteCodes(), CD.class);
        medication.setSite(ccdCodeFactory.convert(siteValue));

        // TODO parse maxDoseQuantity
        if (CcdParseUtils.hasContent(substanceAdministration.getDoseQuantity())) {
            medication.setDoseQuantity(CcdTransform.PQtoInteger(substanceAdministration.getDoseQuantity()));
            medication.setDoseUnits(substanceAdministration.getDoseQuantity().getUnit());
        }
        if (CcdParseUtils.hasContent(substanceAdministration.getRateQuantity())) {
            medication.setRateQuantity(CcdTransform.PQtoInteger(substanceAdministration.getRateQuantity()));
            medication.setRateUnits(substanceAdministration.getRateQuantity().getUnit());
        }
        medication
                .setAdministrationUnitCode(ccdCodeFactory.convert(substanceAdministration.getAdministrationUnitCode()));

        if (substanceAdministration.getConsumable() != null) {
            medication.setMedicationInformation(parseMedicationInformation(
                    substanceAdministration.getConsumable().getManufacturedProduct(), client, legacyTable));
        }

        medication.setDrugVehicles(parseDrugVehicles(substanceAdministration.getParticipants(), client));

        if (!CollectionUtils.isEmpty(substanceAdministration.getEntryRelationships())) {
            List<Indication> indications = null;
            for (EntryRelationship ccdEntryRelationship : substanceAdministration.getEntryRelationships()) {
                switch (ccdEntryRelationship.getTypeCode()) {
                    case RSON:
                        if (indications == null) {
                            indications = new ArrayList<Indication>();
                        }
                        Indication indication = parseIndication(ccdEntryRelationship.getObservation(), client, legacyTable);
                        if (indication != null) {
                            indications.add(indication);
                        }
                        break;
                    case SUBJ:
                        // [FIXME] entryRelationship with @typeCode = "SUBJ" may indicate either
                        // [FIXME] Patient Instruction (templateId: 2.16.840.1.113883.10.20.1.49) or
                        // Medication Series Number Observation (templateId:
                        // 2.16.840.1.113883.10.20.1.46)
                        medication.setInstructions(parseInstructions(ccdEntryRelationship.getAct(), client));
                        break;
                    case REFR:
                        Supply ccdSupply = ccdEntryRelationship.getSupply();
                        if (ccdSupply == null) {
                            Observation ccdObservation = ccdEntryRelationship.getObservation();
                            // TODO Parse MedicationStatusObservation (templateId =
                            // 2.16.840.1.113883.10.20.1.47)
                        } else {
                            if (ccdSupply.getMoodCode() == x_DocumentSubstanceMood.EVN) {
                                medication.setMedicationDispenses(parseMedicationDispenses(ccdSupply, client, legacyTable));
                                CollectionUtils.emptyIfNull(medication.getMedicationDispenses())
                                        .forEach(medicationDispense -> medicationDispense.setMedication(medication));
                            } else if (ccdSupply.getMoodCode() == x_DocumentSubstanceMood.INT) {
                                medication.setMedicationSupplyOrder(
                                        parseMedicationSupplyOrder(ccdSupply, client, legacyTable));
                            } else {
                                logger.warn(
                                        "parseMedicationActivity : Unknown EntryRelationship of type REFR and Supply moodCode = "
                                                + ccdSupply.getMoodCode());
                            }
                        }
                        break;
                    case CAUS:
                        medication.setReactionObservation(
                                parseReactionObservation(ccdEntryRelationship.getObservation(), client, legacyTable));
                        break;
                    default:
                        break;
                }
            }
            medication.setIndications(indications);
        }

        medication.setPreconditions(parsePreconditions(substanceAdministration.getPreconditions(), client));

        if (!CollectionUtils.isEmpty(substanceAdministration.getPerformers())) {
            Performer2 ccdPerformer2 = substanceAdministration.getPerformers().get(0);
            medication
                    .setPerformer(performer2Factory.parsePerson(ccdPerformer2, client.getOrganization(), legacyTable));
        }

        return medication;
    }

    // Supply moodCode = INT
    public MedicationSupplyOrder parseMedicationSupplyOrder(Supply ccdSupply, Client client, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdSupply) || client == null) {
            return null;
        }
        checkNotNull(client);

        MedicationSupplyOrder medicationSupplyOrder = new MedicationSupplyOrder();
        medicationSupplyOrder.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdSupply.getIds()));
        medicationSupplyOrder.setLegacyTable(legacyTable);
        medicationSupplyOrder.setOrganization(client.getOrganization());

        if (CcdParseUtils.hasContent(ccdSupply.getStatusCode())) {
            medicationSupplyOrder.setStatusCode(ccdSupply.getStatusCode().getCode());
        }

        Pair<Date, Date> effectiveTime = CcdTransform.SXCM_TStoHighLowDate(
                CcdParseUtils.getFirstNotEmptyValue(ccdSupply.getEffectiveTimes(), SXCM_TS.class));
        if (effectiveTime != null) {
            medicationSupplyOrder.setTimeHigh(effectiveTime.getFirst());
        }

        medicationSupplyOrder.setRepeatNumber(CcdTransform.INTtoInteger(ccdSupply.getRepeatNumber()));
        medicationSupplyOrder.setQuantity(CcdTransform.PQtoInteger(ccdSupply.getQuantity()));

        if (ccdSupply.getProduct() != null) {
            ManufacturedProduct ccdManufacturedProduct = ccdSupply.getProduct().getManufacturedProduct();

            // TODO test that manufactured products don't overlap
            medicationSupplyOrder
                    .setMedicationInformation(parseMedicationInformation(ccdManufacturedProduct, client, legacyTable));
            medicationSupplyOrder.setImmunizationMedicationInformation(
                    parseImmunizationMedicationInformation(ccdManufacturedProduct, client));
        }

        for (org.eclipse.mdht.uml.cda.Author ccdAuthor : ccdSupply.getAuthors()) {
            Author author = parseAuthor(ccdAuthor, client, legacyTable);
            if (author != null) {
                medicationSupplyOrder.setAuthor(author);
                break;
            }
        }

        for (EntryRelationship entryRelationship : ccdSupply.getEntryRelationships()) {
            if (CcdParseUtils.hasContent(entryRelationship)
                    && entryRelationship.getTypeCode() == x_ActRelationshipEntryRelationship.SUBJ) {
                Act ccdAct = entryRelationship.getAct();
                Instructions instructions = parseInstructions(ccdAct, client);
                if (instructions != null) {
                    medicationSupplyOrder.setInstructions(instructions);
                    break;
                }
            }
        }

        return medicationSupplyOrder;
    }

    private List<MedicationDispense> parseMedicationDispenses(Supply ccdSupply, Client client, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdSupply) || client == null) {
            return null;
        }
        checkNotNull(client);

        List<MedicationDispense> medicationDispenses = new ArrayList<MedicationDispense>();
        for (EntryRelationship entryRelationship : ccdSupply.getEntryRelationships()) {
            if (CcdParseUtils.hasContent(entryRelationship)
                    && entryRelationship.getTypeCode() == x_ActRelationshipEntryRelationship.REFR) {
                Supply ccdSupplyReference = entryRelationship.getSupply();
                MedicationDispense medicationDispense = parseMedicationDispense(ccdSupplyReference, client,
                        legacyTable);
                medicationDispenses.add(medicationDispense);
            }
        }

        return medicationDispenses;
    }

    // Supply moodCode = EVN
    public MedicationDispense parseMedicationDispense(Supply ccdSupply, Client client, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdSupply) || client == null) {
            return null;
        }
        checkNotNull(client);

        MedicationDispense medicationDispense = new MedicationDispense();
        medicationDispense.setOrganization(client.getOrganization());
        medicationDispense.setLegacyTable(legacyTable);
        medicationDispense.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdSupply.getIds()));

        if (CcdParseUtils.hasContent(ccdSupply.getStatusCode())) {
            medicationDispense.setStatusCode(ccdSupply.getStatusCode().getCode());
        }

        SXCM_TS effectiveTime = CcdParseUtils.getFirstNotEmptyValue(ccdSupply.getEffectiveTimes(), SXCM_TS.class);
        if (effectiveTime instanceof IVL_TS) {
            Pair<Date, Date> effectiveTimes = CcdTransform.IVLTStoHighLowDate((IVL_TS) effectiveTime);
            if (effectiveTimes != null) {
                medicationDispense.setDispenseDateHigh(effectiveTimes.getFirst());
                medicationDispense.setDispenseDateLow(effectiveTimes.getSecond());
            }
        }

        medicationDispense.setFillNumber(CcdTransform.INTtoInteger(ccdSupply.getRepeatNumber()));
        medicationDispense.setQuantity(CcdTransform.PQtoBigDecimal(ccdSupply.getQuantity()));

        if (ccdSupply.getProduct() != null) {
            ManufacturedProduct ccdManufacturedProduct = ccdSupply.getProduct().getManufacturedProduct();

            // TODO test that manufactured products don't overlap
            medicationDispense
                    .setMedicationInformation(parseMedicationInformation(ccdManufacturedProduct, client, legacyTable));
            medicationDispense.setImmunizationMedicationInformation(
                    parseImmunizationMedicationInformation(ccdManufacturedProduct, client));
        }

        for (Performer2 ccdPerformer : ccdSupply.getPerformers()) {
            AssignedEntity assignedEntity = ccdPerformer.getAssignedEntity();
            if (assignedEntity == null) {
                continue;
            }

            for (org.eclipse.mdht.uml.cda.Organization ccdOrganization : assignedEntity.getRepresentedOrganizations()) {
                if (CcdParseUtils.hasContent(ccdOrganization)) {
                    // Organization organization = CcdTransform.toOrganization(ccdOrganization,
                    // resident.getDatabase(), legacyTable);
                    // medicationDispense.setProvider(organization);

                    // TODO as for now, Organization is set from resident since all nwhin entities
                    // will be assigned to a single organization
                    medicationDispense.setProvider(client.getCommunity());
                    break;
                }
            }
        }

        // inner reference to MedicationSupplyOrder
        for (EntryRelationship entryRelationship : ccdSupply.getEntryRelationships()) {
            if (CcdParseUtils.hasContent(entryRelationship)
                    && entryRelationship.getTypeCode() == x_ActRelationshipEntryRelationship.REFR) {
                Supply ccdSupplyReference = entryRelationship.getSupply();
                MedicationSupplyOrder medicationSupplyOrder = parseMedicationSupplyOrder(ccdSupplyReference, client,
                        legacyTable);
                if (medicationSupplyOrder != null) {
                    medicationDispense.setMedicationSupplyOrder(medicationSupplyOrder);
                    break;
                }
            }
        }

        return medicationDispense;
    }

    public Instructions parseInstructions(Act act, Client client) {
        if (!CcdParseUtils.hasContent(act) || client == null) {
            return null;
        }
        checkNotNull(client);

        Instructions instructions = new Instructions();
        instructions.setOrganization(client.getOrganization());

        instructions.setCode(ccdCodeFactory.convert(act.getCode()));
        instructions.setText(CcdTransform.EDtoString(act.getText(), instructions.getCode()));

        return instructions;
    }

    public MedicationInformation parseMedicationInformation(ManufacturedProduct manufacturedProduct, Client client,
                                                            String legacyTable) {
        if (!CcdParseUtils.hasContent(manufacturedProduct) || client == null) {
            return null;
        }

        MedicationInformation medicationInformation = new MedicationInformation();
        medicationInformation.setOrganization(client.getOrganization());
        medicationInformation.setLegacyTable(legacyTable);
        medicationInformation.setLegacyId(CcdParseUtils.getFirstIdExtension(manufacturedProduct.getIds()));

        if (manufacturedProduct.getManufacturedMaterial() != null) {
            CD code = manufacturedProduct.getManufacturedMaterial().getCode();
            EN name = manufacturedProduct.getManufacturedMaterial().getName();
            medicationInformation.setProductNameCode(ccdCodeFactory.convert(code));
            medicationInformation
                    .setProductNameText(CcdTransform.EDtoString(code.getOriginalText(), code.getDisplayName()));
            if (medicationInformation.getProductNameText() == null && CcdParseUtils.hasContent(name)) {
                medicationInformation.setProductNameText(StringUtils.trim(name.getText()));
            }
        }

        if (manufacturedProduct.getManufacturerOrganization() != null) {
            Community community = CcdTransform.toCommunity(manufacturedProduct.getManufacturerOrganization(),
                    client.getOrganization(), legacyTable);
            // TODO default org is set
            medicationInformation.setManufactorer(client.getCommunity());
        }

        return medicationInformation;
    }

    public ImmunizationMedicationInformation parseImmunizationMedicationInformation(
            ManufacturedProduct ccdManufacturedProduct, Client client) {
        if (!CcdParseUtils.hasContent(ccdManufacturedProduct) || client == null) {
            return null;
        }
        checkNotNull(client);

        ImmunizationMedicationInformation immunizationMedicationInformation = new ImmunizationMedicationInformation();
        immunizationMedicationInformation.setOrganization(client.getOrganization());
        immunizationMedicationInformation
                .setLegacyId(CcdParseUtils.getFirstIdExtensionStr(ccdManufacturedProduct.getIds()));

        Material manufacturedMaterial = ccdManufacturedProduct.getManufacturedMaterial();
        if (CcdParseUtils.hasContent(manufacturedMaterial)) {
            CD code = manufacturedMaterial.getCode();
            //todo apply retired vaccination code translation
            //todo parse translation
            immunizationMedicationInformation.setCode(ccdCodeFactory.convert(code));
            immunizationMedicationInformation.setText(
                    CcdTransform.EDtoString(code.getOriginalText(), immunizationMedicationInformation.getCode()));
            immunizationMedicationInformation
                    .setLotNumberText(CcdTransform.EDtoString(manufacturedMaterial.getLotNumberText()));
        }

        immunizationMedicationInformation.setManufactorer(client.getCommunity());

        return immunizationMedicationInformation;
    }

    public static List<String> parseReferenceRanges(List<ReferenceRange> ccdReferenceRanges) {
        List<String> refRanges = null;
        if (!CollectionUtils.isEmpty(ccdReferenceRanges)) {
            refRanges = new ArrayList<String>();
            for (ReferenceRange referenceRange : ccdReferenceRanges) {
                if (referenceRange.getObservationRange() != null) {
                    String text = CcdTransform.EDtoString(referenceRange.getObservationRange().getText());
                    if (text != null) {
                        refRanges.add(text);
                    }
                }
            }
        }

        return refRanges;
    }
}
