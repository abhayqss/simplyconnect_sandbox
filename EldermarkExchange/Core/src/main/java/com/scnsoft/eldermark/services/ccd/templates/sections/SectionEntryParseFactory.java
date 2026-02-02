package com.scnsoft.eldermark.services.ccd.templates.sections;

import com.scnsoft.eldermark.entity.Author;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.ObservationFactory;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.Performer2Factory;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.PersonFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.cda.Procedure;
import org.openhealthtools.mdht.uml.cda.ccd.AgeObservation;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.eclipse.mdht.uml.hl7.vocab.x_DocumentSubstanceMood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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

    public ProductInstance parseProductInstance(ParticipantRole sourceProduct, Resident resident) {
        ProductInstance targetInstance = new ProductInstance();
        targetInstance.setDatabase(resident.getDatabase());
        targetInstance.setLegacyId(CcdParseUtils.getFirstIdExtension(sourceProduct.getIds()));

        // TODO according to specs the UDI should be sent in the participantRole/id. Why is it ignored here?
        // see http://ccda.art-decor.org/ccda-html-20150727T182455/tmp-2.16.840.1.113883.10.20.22.4.37-2013-01-31T000000.html

        if (sourceProduct.getPlayingDevice() != null) {
            targetInstance.setDeviceCode(ccdCodeFactory.convert(sourceProduct.getPlayingDevice().getCode()));
        }

        final Entity scopingEntity = sourceProduct.getScopingEntity();
        if (scopingEntity != null) {
            II scopeEntityId = CcdParseUtils.getFirstNotEmptyValue(scopingEntity.getIds(), II.class);
            if (scopeEntityId != null) {
                // TODO The `root` alone may be the entire instance identifier. What if `extension` is null here?
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
                                                      Resident resident, Problem targetProblem) {
        if (!CcdParseUtils.hasContent(srcObservation) || resident == null) {
            return null;
        }
        checkNotNull(resident);

        ProblemObservation targetObservation = new ProblemObservation();
        targetObservation.setLegacyId(CcdParseUtils.getFirstIdExtension(srcObservation.getIds()));
        targetObservation.setDatabase(resident.getDatabase());

        targetObservation.setNegationInd(srcObservation.getNegationInd());
        targetObservation.setProblemType(ccdCodeFactory.convert(srcObservation.getCode()));

        Pair<Date, Date> observationEffectiveTime = CcdTransform.IVLTStoHighLowDate(srcObservation.getEffectiveTime());
        if (observationEffectiveTime != null) {
            targetObservation.setProblemDateTimeHigh(observationEffectiveTime.getFirst());
            targetObservation.setProblemDateTimeLow(observationEffectiveTime.getSecond());
        }

        for (Observation srcObservationEntry : srcObservation.getObservations()) {
            SectionTypeCode type = SectionTypeCode.getByCode(srcObservationEntry.getCode().getCode(), srcObservationEntry.getCode().getCodeSystem());
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
                        targetObservation.setProblemStatusText(CcdTransform.EDtoString(problemStatusText, targetObservation.getProblemStatusCode()));
                        break;
                    case HEALTH_STATUS_OBSERVATION:
                        CD healthStatusValue = ObservationFactory.getValue(srcObservationEntry, CD.class);
                        targetObservation.setHealthStatusCode(ccdCodeFactory.convert(healthStatusValue));
                        ED healthStatusText = srcObservationEntry.getText();
                        targetObservation.setHealthStatusObservationText(CcdTransform.EDtoString(healthStatusText, targetObservation.getHealthStatusCode()));
                }
            }
        }

        CD problemCode = ObservationFactory.getValue(srcObservation, CD.class);
        if (problemCode != null) {
            targetObservation.setProblemCode(ccdCodeFactory.convert(problemCode));
            targetObservation.setProblemName(CcdTransform.EDtoString(srcObservation.getText(), problemCode.getDisplayName()));

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

    public ServiceDeliveryLocation parseServiceDeliveryLocation(ParticipantRole ccdParticipantRole, Resident resident, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdParticipantRole) || resident == null) {
            return null;
        }
        checkNotNull(resident);

        ServiceDeliveryLocation serviceDeliveryLocation = new ServiceDeliveryLocation();
        serviceDeliveryLocation.setDatabase(resident.getDatabase());
        serviceDeliveryLocation.setCode(ccdCodeFactory.convert(ccdParticipantRole.getCode()));
        if (ccdParticipantRole.getPlayingEntity() != null && !CollectionUtils.isEmpty(ccdParticipantRole.getPlayingEntity().getNames())) {
            PN name = ccdParticipantRole.getPlayingEntity().getNames().get(0);
            serviceDeliveryLocation.setName(name.getText());
            ED desc = ccdParticipantRole.getPlayingEntity().getDesc();
            serviceDeliveryLocation.setDescription(CcdTransform.EDtoString(desc));
        }
        if (!CollectionUtils.isEmpty(ccdParticipantRole.getAddrs())) {
            List<OrganizationAddress> addresses = new ArrayList<OrganizationAddress>();
            for (AD ccdAddress : ccdParticipantRole.getAddrs()) {
                if (CcdParseUtils.hasContent(ccdAddress)) {
                    OrganizationAddress address = CcdParseUtils.createAddress(ccdAddress, resident.getDatabase(), resident.getFacility(), legacyTable);
                    addresses.add(address);
                }
            }
            serviceDeliveryLocation.setAddresses(addresses);
        }
        if (!CollectionUtils.isEmpty(ccdParticipantRole.getTelecoms())) {
            List<OrganizationTelecom> telecoms = new ArrayList<OrganizationTelecom>();
            for (TEL ccdTelecom : ccdParticipantRole.getTelecoms()) {
                if (CcdParseUtils.hasContent(ccdTelecom)) {
                    OrganizationTelecom telecom = CcdParseUtils.createTelecom(ccdTelecom, resident.getDatabase(), resident.getFacility(), legacyTable);
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

    public Indication parseIndication(Observation ccdObservation, Resident resident, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdObservation) || resident == null) {
            return null;
        }

        Indication indication = new Indication();
        indication.setDatabase(resident.getDatabase());
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

    public Author parseAuthor(org.eclipse.mdht.uml.cda.Author ccdAuthor, Resident resident, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdAuthor) || resident == null) {
            return null;
        }
        checkNotNull(resident);

        Author author = new Author();
        author.setResident(resident);
        author.setDatabase(resident.getDatabase());
        author.setLegacyTable(legacyTable);
        author.setLegacyId(0L);

        author.setTime(CcdParseUtils.convertTsToDate(ccdAuthor.getTime()));
        AssignedAuthor ccdAssignedAuthor = ccdAuthor.getAssignedAuthor();
        if (ccdAssignedAuthor.getRepresentedOrganization() != null) {
            Organization org = CcdTransform.toOrganization(ccdAssignedAuthor.getRepresentedOrganization(),
                    resident.getDatabase(), legacyTable);
            //TODO as for now, organization is set from resident since all nwhin entities will be assigned to single organization
            author.setOrganization(resident.getFacility());
        }
        if (ccdAssignedAuthor.getAssignedPerson() != null) {
            author.setPerson(personFactory.parse(ccdAssignedAuthor, resident.getDatabase(), legacyTable));
        }

        return author;
    }

    private MedicationPrecondition parseMedicationPrecondition(Precondition ccdPrecondition, Resident resident) {
        if (!CcdParseUtils.hasContent(ccdPrecondition) || resident == null || ccdPrecondition.getCriterion() == null) {
            return null;
        }

        MedicationPrecondition medicationPrecondition = new MedicationPrecondition();
        medicationPrecondition.setDatabase(resident.getDatabase());

        Criterion criterion = ccdPrecondition.getCriterion();
        medicationPrecondition.setCode(ccdCodeFactory.convert(criterion.getCode()));
        medicationPrecondition.setText(CcdTransform.EDtoString(criterion.getText(), medicationPrecondition.getCode()));
        if (criterion.getValue() instanceof CD) {
            medicationPrecondition.setValue(ccdCodeFactory.convert((CD) criterion.getValue()));
        }

        return medicationPrecondition;
    }

    public List<MedicationPrecondition> parsePreconditions(EList<Precondition> preconditions, Resident resident) {
        if (!CollectionUtils.isEmpty(preconditions)) {
            List<MedicationPrecondition> medicationPreconditions = new ArrayList<MedicationPrecondition>();
            for (Precondition ccdPrecondition : preconditions) {
                MedicationPrecondition medicationPrecondition = parseMedicationPrecondition(ccdPrecondition, resident);
                if (medicationPrecondition != null) {
                    medicationPreconditions.add(medicationPrecondition);
                }
            }
            return medicationPreconditions;
        }
        return null;
    }

    public SeverityObservation parseSeverityObservation(Observation observation, Resident resident, String legacyTable) {
        if (!CcdParseUtils.hasContent(observation) || resident == null) {
            return null;
        }
        checkNotNull(resident);

        SeverityObservation severityObservation = new SeverityObservation();
        severityObservation.setDatabase(resident.getDatabase());
        severityObservation.setLegacyTable(legacyTable);
        severityObservation.setLegacyId(CcdParseUtils.getFirstIdExtension(observation.getIds()));

        CD ccdObservationValue = ObservationFactory.getValue(observation, CD.class);
        severityObservation.setSeverityCode(ccdCodeFactory.convert(ccdObservationValue));
        severityObservation.setSeverityText(CcdTransform.EDtoString(observation.getText(), severityObservation.getSeverityCode()));

        return severityObservation;
    }

    public ReactionObservation parseReactionObservation(Observation observation, Resident resident, String legacyTable) {
        if (!CcdParseUtils.hasContent(observation) || resident == null) {
            return null;
        }
        checkNotNull(resident);

        ReactionObservation reactionObservation = new ReactionObservation();
        reactionObservation.setLegacyTable(legacyTable);
        reactionObservation.setLegacyId(CcdParseUtils.getFirstIdExtensionStr(observation.getIds()));
        reactionObservation.setDatabase(resident.getDatabase());

        Pair<Date, Date> effectiveTimes = CcdTransform.IVLTStoHighLowDate(observation.getEffectiveTime());
        if (effectiveTimes != null) {
            reactionObservation.setTimeHigh(effectiveTimes.getFirst());
            reactionObservation.setTimeLow(effectiveTimes.getSecond());
        }

        CD ccdObservationValue = ObservationFactory.getValue(observation, CD.class);
        reactionObservation.setReactionCode(ccdCodeFactory.convert(ccdObservationValue));
        reactionObservation.setReactionText(CcdTransform.EDtoString(observation.getText(), reactionObservation.getReactionCode()));

        if (!CollectionUtils.isEmpty(observation.getEntryRelationships())) {
            List<SeverityObservation> severityObservations = null;
            List<Medication> medications = null;
            List<ProcedureActivity> procedureActivities = null;
            for (EntryRelationship ccdEntryRelationship : observation.getEntryRelationships()) {
                if (ccdEntryRelationship.getObservation() != null) {
                    if (severityObservations == null) {
                        severityObservations = new ArrayList<SeverityObservation>();
                    }
                    severityObservations.add(parseSeverityObservation(ccdEntryRelationship.getObservation(), resident,
                            legacyTable));
                }
                if (ccdEntryRelationship.getSubstanceAdministration() != null) {
                    if (medications == null) {
                        medications = new ArrayList<Medication>();
                    }
                    medications.add(parseMedicationActivity(ccdEntryRelationship.getSubstanceAdministration(), resident,
                            legacyTable));
                }
                if (ccdEntryRelationship.getProcedure() != null) {
                    if (procedureActivities == null) {
                        procedureActivities = new ArrayList<ProcedureActivity>();
                    }
                    procedureActivities.add(parseProcedureActivity(ccdEntryRelationship.getProcedure(), resident, legacyTable));
                }

            }
            reactionObservation.setSeverityObservations(severityObservations);
            reactionObservation.setMedications(medications);
            reactionObservation.setProcedureActivities(procedureActivities);
        }

        return reactionObservation;
    }

    private DrugVehicle parseDrugVehicle(ParticipantRole participantRole, Resident resident) {
        if (!CcdParseUtils.hasContent(participantRole) || resident == null || participantRole.getPlayingEntity() == null) {
            return null;
        }
        checkNotNull(resident);

        DrugVehicle drugVehicle = new DrugVehicle();
        drugVehicle.setDatabase(resident.getDatabase());

        PlayingEntity ccdPlayingEntity = participantRole.getPlayingEntity();
        drugVehicle.setCode(ccdCodeFactory.convert(ccdPlayingEntity.getCode()));
        PN pn = CcdParseUtils.getFirstNotEmptyValue(ccdPlayingEntity.getNames(), PN.class);
        if (pn != null) {
            drugVehicle.setName(pn.getText());
        }

        return drugVehicle;
    }

    public List<DrugVehicle> parseDrugVehicles(EList<Participant2> participants, Resident resident) {
        if (!CollectionUtils.isEmpty(participants)) {
            List<DrugVehicle> drugVehicles = new ArrayList<DrugVehicle>();
            for (Participant2 ccdParticipant : participants) {
                DrugVehicle drugVehicle = parseDrugVehicle(ccdParticipant.getParticipantRole(), resident);
                if (drugVehicle != null) {
                    drugVehicles.add(drugVehicle);
                }
            }
            return drugVehicles;
        }
        return null;
    }

    //TODO Refactoring: extract common parts of parseProcedureObservation, parseProcedureAct, and parseProcedureActivity
    public ProcedureActivity parseProcedureObservation(Observation ccdObservation, Resident resident,
                                                       String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdObservation) || resident == null) {
            return null;
        }
        checkNotNull(resident);

        ProcedureActivity procedureActivity = new ProcedureActivity();
        procedureActivity.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdObservation.getIds()));
        procedureActivity.setDatabase(resident.getDatabase());

        if (ccdObservation.getMoodCode() != null) {
            procedureActivity.setMoodCode(ccdObservation.getMoodCode().getLiteral());
        }

        CD code = ccdObservation.getCode();
        if (code != null) {
            procedureActivity.setProcedureType(ccdCodeFactory.convert(code));
            procedureActivity.setProcedureTypeText(CcdTransform.EDtoString(code.getOriginalText(), procedureActivity.getProcedureType()));
        }
        procedureActivity.setStatusCode(CcdTransform.EDtoString(ccdObservation.getStatusCode() != null ?
                ccdObservation.getStatusCode().getOriginalText() : null));

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
            Set<Organization> organizations = new HashSet<Organization>();
            organizations.add(resident.getFacility());
            procedureActivity.setPerformers(organizations);
        }

        if (!CollectionUtils.isEmpty(ccdObservation.getParticipants())) {
            Set<ServiceDeliveryLocation> serviceDeliveryLocations = null;
            for (Participant2 ccdParticipant2 : ccdObservation.getParticipants()) {
                switch (ccdParticipant2.getTypeCode()) {
                    case LOC:
                        if (serviceDeliveryLocations == null) {
                            serviceDeliveryLocations = new HashSet<ServiceDeliveryLocation>();
                        }
                        serviceDeliveryLocations.add(parseServiceDeliveryLocation(ccdParticipant2.getParticipantRole(), resident, legacyTable));
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
                                    ccdEntryRelationship.getSubstanceAdministration(), resident, legacyTable));
                        } else {
                            if (encounterIds == null) {
                                encounterIds = new HashSet<String>();
                            }
                            if (ccdEntryRelationship.getEncounter() != null) {
                                //TODO check on real data
                                Pair<String, String> encounterId = CcdParseUtils.getFirstRootAndExt(
                                        ccdEntryRelationship.getEncounter().getIds());
                                if (encounterId != null) {
                                    encounterIds.add(encounterId.getSecond());
                                }
                            }
                        }
                        break;
                    case SUBJ:
                        procedureActivity.setInstructions(parseInstructions(ccdEntryRelationship.getAct(), resident));
                        break;
                    case RSON:
                        if (indications == null) {
                            indications = new HashSet<Indication>();
                        }
                        Indication indication = parseIndication(ccdEntryRelationship.getObservation(), resident, legacyTable);
                        indications.add(indication);
                        break;
                }
            }
            procedureActivity.setIndications(indications);
            procedureActivity.setEncounterIds(encounterIds);
        }

        return procedureActivity;
    }

    public ProcedureActivity parseProcedureAct(org.eclipse.mdht.uml.cda.Act ccdAct, Resident resident,
                                                    String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdAct) || resident == null) {
            return null;
        }
        checkNotNull(resident);

        ProcedureActivity procedureActivity = new ProcedureActivity();
        procedureActivity.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdAct.getIds()));
        procedureActivity.setDatabase(resident.getDatabase());

        if (ccdAct.getMoodCode() != null) {
            procedureActivity.setMoodCode(ccdAct.getMoodCode().getLiteral());
        }

        CD code = ccdAct.getCode();
        if (code != null) {
            procedureActivity.setProcedureType(ccdCodeFactory.convert(code));
            procedureActivity.setProcedureTypeText(CcdTransform.EDtoString(code.getOriginalText(), procedureActivity.getProcedureType()));
        }
        procedureActivity.setStatusCode(CcdTransform.EDtoString(ccdAct.getStatusCode() != null ?
                ccdAct.getStatusCode().getOriginalText() : null));

        Pair<Date, Date> effectiveTimes = CcdTransform.IVLTStoHighLowDate(ccdAct.getEffectiveTime());
        if (effectiveTimes != null) {
            procedureActivity.setProcedureStarted(effectiveTimes.getSecond());
            procedureActivity.setProcedureStopped(effectiveTimes.getFirst());
        }
        procedureActivity.setPriorityCode(ccdCodeFactory.convert(ccdAct.getPriorityCode()));

        procedureActivity.setValueText(CcdTransform.EDtoString(ccdAct.getText()));

        //TODO added default organization
        Set<Organization> organizations = new HashSet<Organization>();
        organizations.add(resident.getFacility());
        procedureActivity.setPerformers(organizations);

        if (!CollectionUtils.isEmpty(ccdAct.getParticipants())) {
            Set<ServiceDeliveryLocation> serviceDeliveryLocations = null;
            for (Participant2 ccdParticipant2 : ccdAct.getParticipants()) {
                switch (ccdParticipant2.getTypeCode()) {
                    case LOC:
                        if (serviceDeliveryLocations == null) {
                            serviceDeliveryLocations = new HashSet<ServiceDeliveryLocation>();
                        }
                        serviceDeliveryLocations.add(parseServiceDeliveryLocation(ccdParticipant2.getParticipantRole(),
                                resident, legacyTable));
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
                                    ccdEntryRelationship.getSubstanceAdministration(), resident, legacyTable));
                        } else {
                            if (encounterIds == null) {
                                encounterIds = new HashSet<String>();
                            }
                            if (ccdEntryRelationship.getEncounter() != null) {
                                //TODO check on real data
                                Pair<String, String> encounterId = CcdParseUtils.getFirstRootAndExt(
                                        ccdEntryRelationship.getEncounter().getIds());
                                if (encounterId != null) {
                                    encounterIds.add(encounterId.getSecond());
                                }
                            }
                        }
                        break;
                    case SUBJ:
                        procedureActivity.setInstructions(parseInstructions(ccdEntryRelationship.getAct(), resident));
                        break;
                    case RSON:
                        if (indications == null) {
                            indications = new HashSet<Indication>();
                        }
                        Indication indication = parseIndication(ccdEntryRelationship.getObservation(), resident, legacyTable);
                        if (indication != null) {
                            indications.add(indication);
                        }
                        break;

                }
            }
            procedureActivity.setIndications(indications);
            procedureActivity.setEncounterIds(encounterIds);
        }

        return procedureActivity;
    }

    public ProcedureActivity parseProcedureActivity(Procedure ccdProcedure, Resident resident, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdProcedure) || resident == null) {
            return null;
        }
        checkNotNull(resident);

        ProcedureActivity procedureActivity = new ProcedureActivity();
        procedureActivity.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdProcedure.getIds()));
        procedureActivity.setDatabase(resident.getDatabase());

        if (ccdProcedure.getMoodCode() != null) {
            procedureActivity.setMoodCode(ccdProcedure.getMoodCode().getLiteral());
        }

        CD code = ccdProcedure.getCode();
        if (code != null) {
            procedureActivity.setProcedureType(ccdCodeFactory.convert(code));
            procedureActivity.setProcedureTypeText(CcdTransform.EDtoString(code.getOriginalText(), procedureActivity.getProcedureType()));
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
                if (ccdSpecimen.getSpecimenRole() != null && !CollectionUtils.isEmpty(ccdSpecimen.getSpecimenRole().getIds())) {
                    //TODO check on real data
                    specimenIds.add(ccdSpecimen.getSpecimenRole().getIds().get(0).getExtension());
                }
            }
            specimenIds.remove(null);
            procedureActivity.setSpecimenIds(specimenIds);
        }

        //TODO added default organization
        Set<Organization> organizations = new HashSet<Organization>();
        organizations.add(resident.getFacility());
        procedureActivity.setPerformers(organizations);

        if (!CollectionUtils.isEmpty(ccdProcedure.getParticipants())) {
            Set<ProductInstance> productInstances = null;
            Set<ServiceDeliveryLocation> serviceDeliveryLocations = null;
            for (Participant2 ccdParticipant2 : ccdProcedure.getParticipants()) {
                switch (ccdParticipant2.getTypeCode()) {
                    case DEV:
                        if (productInstances == null) {
                            productInstances = new HashSet<ProductInstance>();
                        }
                        productInstances.add(parseProductInstance(ccdParticipant2.getParticipantRole(), resident));
                        break;
                    case LOC:
                        if (serviceDeliveryLocations == null) {
                            serviceDeliveryLocations = new HashSet<ServiceDeliveryLocation>();
                        }
                        serviceDeliveryLocations.add(parseServiceDeliveryLocation(ccdParticipant2.getParticipantRole(), resident, legacyTable));
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
                                    ccdEntryRelationship.getSubstanceAdministration(), resident, legacyTable));
                        } else {
                            if (encounterIds == null) {
                                encounterIds = new HashSet<String>();
                            }
                            if (ccdEntryRelationship.getEncounter() != null && !CollectionUtils.isEmpty(
                                    ccdEntryRelationship.getEncounter().getIds())) {
                                //TODO check on real data
                                String idExtension = ccdEntryRelationship.getEncounter().getIds().get(0).getExtension();
                                if (idExtension != null) {
                                    encounterIds.add(idExtension);
                                }
                            }
                        }
                        break;
                    case SUBJ:
                        procedureActivity.setInstructions(parseInstructions(ccdEntryRelationship.getAct(), resident));
                        break;
                    case RSON:
                        if (indications == null) {
                            indications = new HashSet<Indication>();
                        }
                        Indication indication = parseIndication(ccdEntryRelationship.getObservation(), resident, legacyTable);
                        if (indication != null) {
                            indications.add(indication);
                        }
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

    public Medication parseMedicationActivity(SubstanceAdministration substanceAdministration, Resident resident, String legacyTable) {
        if (!CcdParseUtils.hasContent(substanceAdministration) || resident == null) {
            return null;
        }
        checkNotNull(resident);

        Medication medication = new Medication();
        medication.setResident(resident);
        medication.setDatabase(resident.getDatabase());
        medication.setLegacyId(CcdParseUtils.getFirstIdExtension(substanceAdministration.getIds()));

        medication.setDeliveryMethod(ccdCodeFactory.convert(substanceAdministration.getCode()));
        // TODO construct freeTextSig from available information
        medication.setFreeTextSig(CcdTransform.EDtoString(substanceAdministration.getText()));
        medication.setStatusCode(substanceAdministration.getStatusCode() != null ? substanceAdministration.getStatusCode().getCode() : null);

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
        medication.setAdministrationUnitCode(ccdCodeFactory.convert(substanceAdministration.getAdministrationUnitCode()));

        if (substanceAdministration.getConsumable() != null) {
            medication.setMedicationInformation(parseMedicationInformation(
                    substanceAdministration.getConsumable().getManufacturedProduct(), resident, legacyTable));
        }

        medication.setDrugVehicles(parseDrugVehicles(substanceAdministration.getParticipants(), resident));

        if (!CollectionUtils.isEmpty(substanceAdministration.getEntryRelationships())) {
            List<Indication> indications = null;
            for (EntryRelationship ccdEntryRelationship : substanceAdministration.getEntryRelationships()) {
                switch (ccdEntryRelationship.getTypeCode()) {
                    case RSON:
                        if (indications == null) {
                            indications = new ArrayList<Indication>();
                        }
                        Indication indication = parseIndication(ccdEntryRelationship.getObservation(), resident, legacyTable);
                        if (indication != null) {
                            indications.add(indication);
                        }
                        break;
                    case SUBJ:
                        // [FIXME] entryRelationship with @typeCode = "SUBJ" may indicate either
                        // [FIXME] Patient Instruction (templateId: 2.16.840.1.113883.10.20.1.49) or Medication Series Number Observation (templateId: 2.16.840.1.113883.10.20.1.46)
                        medication.setInstructions(parseInstructions(ccdEntryRelationship.getAct(), resident));
                        break;
                    case REFR:
                        Supply ccdSupply = ccdEntryRelationship.getSupply();
                        if (ccdSupply == null) {
                            Observation ccdObservation = ccdEntryRelationship.getObservation();
                            // TODO Parse MedicationStatusObservation (templateId = 2.16.840.1.113883.10.20.1.47)
                        } else {
                            if (ccdSupply.getMoodCode() == x_DocumentSubstanceMood.EVN) {
                                medication.setMedicationDispenses(parseMedicationDispenses(ccdSupply, resident, legacyTable));
                            } else if (ccdSupply.getMoodCode() == x_DocumentSubstanceMood.INT) {
                                medication.setMedicationSupplyOrder(parseMedicationSupplyOrder(ccdSupply, resident, legacyTable));
                            } else {
                                logger.warn("parseMedicationActivity : Unknown EntryRelationship of type REFR and Supply moodCode = "
                                        + ccdSupply.getMoodCode());
                            }
                        }
                        break;
                    case CAUS:
                        medication.setReactionObservation(parseReactionObservation(ccdEntryRelationship.getObservation(),
                                resident, legacyTable));
                        break;
                }
            }
            medication.setIndications(indications);
        }

        medication.setPreconditions(parsePreconditions(substanceAdministration.getPreconditions(), resident));

        if (!CollectionUtils.isEmpty(substanceAdministration.getPerformers())) {
            Performer2 ccdPerformer2 = substanceAdministration.getPerformers().get(0);
            medication.setPerformer(performer2Factory.parsePerson(ccdPerformer2, resident.getDatabase(), legacyTable));
        }

        return medication;
    }

    // Supply moodCode = INT
    public MedicationSupplyOrder parseMedicationSupplyOrder(Supply ccdSupply, Resident resident, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdSupply) || resident == null) {
            return null;
        }
        checkNotNull(resident);

        MedicationSupplyOrder medicationSupplyOrder = new MedicationSupplyOrder();
        medicationSupplyOrder.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdSupply.getIds()));
        medicationSupplyOrder.setLegacyTable(legacyTable);
        medicationSupplyOrder.setDatabase(resident.getDatabase());

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
            medicationSupplyOrder.setMedicationInformation(parseMedicationInformation(ccdManufacturedProduct, resident,
                    legacyTable));
            medicationSupplyOrder.setImmunizationMedicationInformation(
                    parseImmunizationMedicationInformation(ccdManufacturedProduct, resident));
        }

        for (org.eclipse.mdht.uml.cda.Author ccdAuthor : ccdSupply.getAuthors()) {
            Author author = parseAuthor(ccdAuthor, resident, legacyTable);
            if (author != null) {
                medicationSupplyOrder.setAuthor(author);
                break;
            }
        }

        for (EntryRelationship entryRelationship : ccdSupply.getEntryRelationships()) {
            if (CcdParseUtils.hasContent(entryRelationship) &&
                    entryRelationship.getTypeCode() == x_ActRelationshipEntryRelationship.SUBJ) {
                Act ccdAct = entryRelationship.getAct();
                Instructions instructions = parseInstructions(ccdAct, resident);
                if (instructions != null) {
                    medicationSupplyOrder.setInstructions(instructions);
                    break;
                }
            }
        }

        return medicationSupplyOrder;
    }

    private List<MedicationDispense> parseMedicationDispenses(Supply ccdSupply, Resident resident, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdSupply) || resident == null) {
            return null;
        }
        checkNotNull(resident);

        List<MedicationDispense> medicationDispenses = new ArrayList<MedicationDispense>();
        for (EntryRelationship entryRelationship : ccdSupply.getEntryRelationships()) {
            if (CcdParseUtils.hasContent(entryRelationship) &&
                    entryRelationship.getTypeCode() == x_ActRelationshipEntryRelationship.REFR) {
                Supply ccdSupplyReference = entryRelationship.getSupply();
                MedicationDispense medicationDispense = parseMedicationDispense(ccdSupplyReference, resident, legacyTable);
                medicationDispenses.add(medicationDispense);
            }
        }

        return medicationDispenses;
    }

    // Supply moodCode = EVN
    public MedicationDispense parseMedicationDispense(Supply ccdSupply, Resident resident, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdSupply) || resident == null) {
            return null;
        }
        checkNotNull(resident);

        MedicationDispense medicationDispense = new MedicationDispense();
        medicationDispense.setDatabase(resident.getDatabase());
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
        medicationDispense.setQuantity(CcdTransform.PQtoInteger(ccdSupply.getQuantity()));

        if (ccdSupply.getProduct() != null) {
            ManufacturedProduct ccdManufacturedProduct = ccdSupply.getProduct().getManufacturedProduct();

            // TODO test that manufactured products don't overlap
            medicationDispense.setMedicationInformation(parseMedicationInformation(ccdManufacturedProduct, resident,
                    legacyTable));
            medicationDispense.setImmunizationMedicationInformation(
                    parseImmunizationMedicationInformation(ccdManufacturedProduct, resident));
        }

        for (Performer2 ccdPerformer : ccdSupply.getPerformers()) {
            AssignedEntity assignedEntity = ccdPerformer.getAssignedEntity();
            if (assignedEntity == null) {
                continue;
            }

            for (org.eclipse.mdht.uml.cda.Organization ccdOrganization : assignedEntity.getRepresentedOrganizations()) {
                if (CcdParseUtils.hasContent(ccdOrganization)) {
                    //Organization organization = CcdTransform.toOrganization(ccdOrganization, resident.getDatabase(), legacyTable);
                    //medicationDispense.setProvider(organization);

                    // TODO as for now, Organization is set from resident since all nwhin entities will be assigned to a single organization
                    medicationDispense.setProvider( resident.getFacility() );
                    break;
                }
            }
        }

        // inner reference to MedicationSupplyOrder
        for (EntryRelationship entryRelationship : ccdSupply.getEntryRelationships()) {
            if (CcdParseUtils.hasContent(entryRelationship) &&
                    entryRelationship.getTypeCode() == x_ActRelationshipEntryRelationship.REFR) {
                Supply ccdSupplyReference = entryRelationship.getSupply();
                MedicationSupplyOrder medicationSupplyOrder = parseMedicationSupplyOrder(ccdSupplyReference, resident,
                        legacyTable);
                if (medicationSupplyOrder != null) {
                    medicationDispense.setMedicationSupplyOrder(medicationSupplyOrder);
                    break;
                }
            }
        }

        return medicationDispense;
    }

    public Instructions parseInstructions(Act act, Resident resident) {
        if (!CcdParseUtils.hasContent(act) || resident == null) {
            return null;
        }
        checkNotNull(resident);

        Instructions instructions = new Instructions();
        instructions.setDatabase(resident.getDatabase());

        instructions.setCode(ccdCodeFactory.convert(act.getCode()));
        instructions.setText(CcdTransform.EDtoString(act.getText(), instructions.getCode()));

        return instructions;
    }

    public MedicationInformation parseMedicationInformation(ManufacturedProduct manufacturedProduct, Resident resident, String legacyTable) {
        if (!CcdParseUtils.hasContent(manufacturedProduct) || resident == null) {
            return null;
        }

        MedicationInformation medicationInformation = new MedicationInformation();
        medicationInformation.setDatabase(resident.getDatabase());
        medicationInformation.setLegacyTable(legacyTable);
        medicationInformation.setLegacyId(CcdParseUtils.getFirstIdExtension(manufacturedProduct.getIds()));

        if (manufacturedProduct.getManufacturedMaterial() != null) {
            CD code = manufacturedProduct.getManufacturedMaterial().getCode();
            EN name = manufacturedProduct.getManufacturedMaterial().getName();
            medicationInformation.setProductNameCode(ccdCodeFactory.convert(code));
            medicationInformation.setProductNameText(CcdTransform.EDtoString(code.getOriginalText(), code.getDisplayName()));
            if (medicationInformation.getProductNameText() == null && CcdParseUtils.hasContent(name)) {
                medicationInformation.setProductNameText(StringUtils.trim(name.getText()));
            }
        }

        if (manufacturedProduct.getManufacturerOrganization() != null) {
            Organization org = CcdTransform.toOrganization(manufacturedProduct.getManufacturerOrganization(),
                    resident.getDatabase(), legacyTable);
            //TODO default org is set
            medicationInformation.setManufactorer(resident.getFacility());
        }

        return medicationInformation;
    }

    public ImmunizationMedicationInformation parseImmunizationMedicationInformation(ManufacturedProduct ccdManufacturedProduct,
                                                                                    Resident resident) {
        if (!CcdParseUtils.hasContent(ccdManufacturedProduct) || resident == null) {
            return null;
        }
        checkNotNull(resident);

        ImmunizationMedicationInformation immunizationMedicationInformation = new ImmunizationMedicationInformation();
        immunizationMedicationInformation.setDatabase(resident.getDatabase());
        immunizationMedicationInformation.setLegacyId(CcdParseUtils.getFirstIdExtensionStr(ccdManufacturedProduct.getIds()));

        Material manufacturedMaterial = ccdManufacturedProduct.getManufacturedMaterial();
        if (CcdParseUtils.hasContent(manufacturedMaterial)) {
            CD code = manufacturedMaterial.getCode();
            immunizationMedicationInformation.setCode(ccdCodeFactory.convert(code));
            immunizationMedicationInformation.setText(CcdTransform.EDtoString(code.getOriginalText(), immunizationMedicationInformation.getCode()));
            immunizationMedicationInformation.setLotNumberText(CcdTransform.EDtoString(manufacturedMaterial.getLotNumberText()));
        }

        immunizationMedicationInformation.setManufactorer(resident.getFacility());

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
