package com.scnsoft.eldermark.service.document.templates.cda.parser.entries;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.Indication;
import com.scnsoft.eldermark.entity.document.ccd.ProcedureActivity;
import com.scnsoft.eldermark.entity.document.ccd.ProductInstance;
import com.scnsoft.eldermark.entity.document.ccd.ServiceDeliveryLocation;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.EntryRelationship;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.cda.Participant2;
import org.eclipse.mdht.uml.cda.Specimen;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class ProcedureActivityFactory {

    private final CcdCodeFactory ccdCodeFactory;

    @Autowired
    private ParticipantRoleFactory participantRoleFactory;
    @Autowired
    private InstructionsFactory instructionsFactory;
    @Autowired
    private IndicationFactory indicationFactory;
    @Autowired
    private SubstanceAdministrationFactory substanceAdministrationsFactory;

    @Autowired
    public ProcedureActivityFactory(CcdCodeFactory ccdCodeFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
    }

    // TODO Refactoring: extract common parts of parseProcedureObservation,
    // parseProcedureAct, and parseProcedureActivity
    public ProcedureActivity parseProcedureObservation(Observation ccdObservation, Client client, String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdObservation) || client == null) {
            return null;
        }

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

        Pair<Date, Date> effectiveTimes = CcdTransform.IVLTStoHighLowDateOrTsToDate(ccdObservation.getEffectiveTime());
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
            Set<Community> communities = new HashSet<>();
            communities.add(client.getCommunity());
            procedureActivity.setPerformers(communities);
        }

        if (!CollectionUtils.isEmpty(ccdObservation.getParticipants())) {
            Set<ServiceDeliveryLocation> serviceDeliveryLocations = null;
            for (Participant2 ccdParticipant2 : ccdObservation.getParticipants()) {
                switch (ccdParticipant2.getTypeCode()) {
                case LOC:
                    if (serviceDeliveryLocations == null) {
                        serviceDeliveryLocations = new HashSet<>();
                    }
                    serviceDeliveryLocations.add(participantRoleFactory
                            .parseServiceDeliveryLocation(ccdParticipant2.getParticipantRole(), client, legacyTable));
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
                        procedureActivity.setMedication(substanceAdministrationsFactory.parseMedicationActivity(
                                ccdEntryRelationship.getSubstanceAdministration(), client, legacyTable));
                    } else {
                        if (encounterIds == null) {
                            encounterIds = new HashSet<>();
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
                    procedureActivity.setInstructions(
                            instructionsFactory.parseInstructions(ccdEntryRelationship.getAct(), client));
                    break;
                case RSON:
                    if (indications == null) {
                        indications = new HashSet<>();
                    }
                    Indication indication = indicationFactory.parseIndication(ccdEntryRelationship.getObservation(),
                            client, legacyTable);
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

        Pair<Date, Date> effectiveTimes = CcdTransform.IVLTStoHighLowDateOrTsToDate(ccdAct.getEffectiveTime());
        if (effectiveTimes != null) {
            procedureActivity.setProcedureStarted(effectiveTimes.getSecond());
            procedureActivity.setProcedureStopped(effectiveTimes.getFirst());
        }
        procedureActivity.setPriorityCode(ccdCodeFactory.convert(ccdAct.getPriorityCode()));

        procedureActivity.setValueText(CcdTransform.EDtoString(ccdAct.getText()));

        // TODO added default organization
        Set<Community> communities = new HashSet<>();
        communities.add(client.getCommunity());
        procedureActivity.setPerformers(communities);

        if (!CollectionUtils.isEmpty(ccdAct.getParticipants())) {
            Set<ServiceDeliveryLocation> serviceDeliveryLocations = null;
            for (Participant2 ccdParticipant2 : ccdAct.getParticipants()) {
                switch (ccdParticipant2.getTypeCode()) {
                case LOC:
                    if (serviceDeliveryLocations == null) {
                        serviceDeliveryLocations = new HashSet<>();
                    }
                    serviceDeliveryLocations.add(participantRoleFactory
                            .parseServiceDeliveryLocation(ccdParticipant2.getParticipantRole(), client, legacyTable));
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
                        procedureActivity.setMedication(substanceAdministrationsFactory.parseMedicationActivity(
                                ccdEntryRelationship.getSubstanceAdministration(), client, legacyTable));
                    } else {
                        if (encounterIds == null) {
                            encounterIds = new HashSet<>();
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
                    procedureActivity.setInstructions(
                            instructionsFactory.parseInstructions(ccdEntryRelationship.getAct(), client));
                    break;
                case RSON:
                    if (indications == null) {
                        indications = new HashSet<>();
                    }
                    Indication indication = indicationFactory.parseIndication(ccdEntryRelationship.getObservation(),
                            client, legacyTable);
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

    public ProcedureActivity parseProcedureActivity(org.eclipse.mdht.uml.cda.Procedure ccdProcedure, Client client,
            String legacyTable) {
        if (!CcdParseUtils.hasContent(ccdProcedure) || client == null) {
            return null;
        }

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

        Pair<Date, Date> effectiveTimes = CcdTransform.IVLTStoHighLowDateOrTsToDate(ccdProcedure.getEffectiveTime());
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
            Set<String> specimenIds = new HashSet<>();
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
        Set<Community> communities = new HashSet<>();
        communities.add(client.getCommunity());
        procedureActivity.setPerformers(communities);

        if (!CollectionUtils.isEmpty(ccdProcedure.getParticipants())) {
            Set<ProductInstance> productInstances = null;
            Set<ServiceDeliveryLocation> serviceDeliveryLocations = null;
            for (Participant2 ccdParticipant2 : ccdProcedure.getParticipants()) {
                switch (ccdParticipant2.getTypeCode()) {
                case DEV:
                    if (productInstances == null) {
                        productInstances = new HashSet<>();
                    }
                    productInstances.add(
                            participantRoleFactory.parseProductInstance(ccdParticipant2.getParticipantRole(), client));
                    break;
                case LOC:
                    if (serviceDeliveryLocations == null) {
                        serviceDeliveryLocations = new HashSet<>();
                    }
                    serviceDeliveryLocations.add(participantRoleFactory
                            .parseServiceDeliveryLocation(ccdParticipant2.getParticipantRole(), client, legacyTable));
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
                        procedureActivity.setMedication(substanceAdministrationsFactory.parseMedicationActivity(
                                ccdEntryRelationship.getSubstanceAdministration(), client, legacyTable));
                    } else {
                        if (encounterIds == null) {
                            encounterIds = new HashSet<>();
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
                    procedureActivity.setInstructions(
                            instructionsFactory.parseInstructions(ccdEntryRelationship.getAct(), client));
                    break;
                case RSON:
                    if (indications == null) {
                        indications = new HashSet<>();
                    }
                    Indication indication = indicationFactory.parseIndication(ccdEntryRelationship.getObservation(),
                            client, legacyTable);
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
            Set<CcdCode> bsCodes = new HashSet<>();
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

}
