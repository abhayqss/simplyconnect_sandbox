package com.scnsoft.eldermark.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.cda.Encounter;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.consol.AgeObservation;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.ProblemStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.scnsoft.eldermark.entity.CodeSystem.HEALTHCARE_SERVICE_LOCATION;
import static com.scnsoft.eldermark.entity.CodeSystem.HL7_ACT_CODE;

public class SectionEntryFactory {
    public static org.openhealthtools.mdht.uml.cda.consol.ReactionObservation buildReactionObservation(
            ReactionObservation reactionObservation, Set<Class> entriesReferredToSectionText) {
        org.openhealthtools.mdht.uml.cda.consol.ReactionObservation ccdReactionObservation = ConsolFactory.eINSTANCE.createReactionObservation();
        ccdReactionObservation.setClassCode(ActClassObservation.OBS);
        ccdReactionObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);
        II ccdReactionObservationTemplateId = DatatypesFactory.eINSTANCE.createII();
        ccdReactionObservationTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.9");
        ccdReactionObservation.getTemplateIds().add(ccdReactionObservationTemplateId);
        CD ccdReactionObservationCode = DatatypesFactory.eINSTANCE.createCD();
        ccdReactionObservationCode.setNullFlavor(NullFlavor.NI);
        ccdReactionObservation.setCode(ccdReactionObservationCode);
        CS ccdReactionObservationStatusCode = DatatypesFactory.eINSTANCE.createCS();
        ccdReactionObservationStatusCode.setCode("completed");
        ccdReactionObservation.setStatusCode(ccdReactionObservationStatusCode);

        ccdReactionObservation.getIds().add(CcdUtils.getId(reactionObservation.getId()));

        ccdReactionObservation.setEffectiveTime(CcdUtils.convertEffectiveTime(reactionObservation.getTimeLow(), reactionObservation.getTimeHigh()));

        if (reactionObservation.getReactionText() != null) {
            if (entriesReferredToSectionText.contains(ReactionObservation.class)) {
                ccdReactionObservation.setText(CcdUtils.createReferenceEntryText(ReactionObservation.class.getSimpleName() + reactionObservation.getId()));
            } else {
                ccdReactionObservation.setText(CcdUtils.createEntryText(reactionObservation.getReactionText()));
            }
        }

        CE code = CcdUtils.createCEWithDefaultDisplayName(reactionObservation.getReactionCode(),
                reactionObservation.getReactionText(), CodeSystem.SNOMED_CT.getOid());

        ccdReactionObservation.getValues().add(code);

        List<SeverityObservation> reactionSeverities = reactionObservation.getSeverityObservations();
        if (reactionSeverities != null) {
            for (SeverityObservation severityObservation : reactionSeverities) {
                EntryRelationship entryRelationship1 = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship1.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
                entryRelationship1.setInversionInd(true);
                entryRelationship1.setObservation(buildSeverityObservation(severityObservation, entriesReferredToSectionText));
                ccdReactionObservation.getEntryRelationships().add(entryRelationship1);
            }
        }

        List<Medication> medications = reactionObservation.getMedications();
        if (medications != null) {
            for (Medication medication : medications) {
                EntryRelationship entryRelationship1 = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship1.setTypeCode(x_ActRelationshipEntryRelationship.RSON);
                entryRelationship1.setInversionInd(true);
                entryRelationship1.setSubstanceAdministration(buildMedicationActivity(medication, entriesReferredToSectionText));
                ccdReactionObservation.getEntryRelationships().add(entryRelationship1);
            }
        }

        List<ProcedureActivity> procedureActivities = reactionObservation.getProcedureActivities();
        if (medications != null) {
            for (ProcedureActivity procedureActivity : procedureActivities) {
                EntryRelationship entryRelationship1 = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship1.setTypeCode(x_ActRelationshipEntryRelationship.RSON);
                entryRelationship1.setInversionInd(true);
                entryRelationship1.setProcedure(buildProcedureActivity(procedureActivity, entriesReferredToSectionText));
                ccdReactionObservation.getEntryRelationships().add(entryRelationship1);
            }
        }

        return ccdReactionObservation;
    }

    public static org.openhealthtools.mdht.uml.cda.consol.SeverityObservation buildSeverityObservation(
            SeverityObservation severityObservation, Set<Class> entriesReferredToSectionText) {
        org.openhealthtools.mdht.uml.cda.consol.SeverityObservation ccdSeverity = ConsolFactory.eINSTANCE.createSeverityObservation();
        ccdSeverity.setClassCode(ActClassObservation.OBS);
        ccdSeverity.setMoodCode(x_ActMoodDocumentObservation.EVN);
        II severityTemplateId = DatatypesFactory.eINSTANCE.createII();
        severityTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.8");
        ccdSeverity.getTemplateIds().add(severityTemplateId);
        CS severityStatusCode = DatatypesFactory.eINSTANCE.createCS();
        severityStatusCode.setCode("completed");
        ccdSeverity.setStatusCode(severityStatusCode);
        // Indicates a subjective evaluation of the criticality associated with another observation.
        CD severityCode = CcdUtils.createCD("SEV", null, HL7_ACT_CODE);
        ccdSeverity.setCode(severityCode);

        if (severityObservation.getSeverityText() != null) {
            if (entriesReferredToSectionText.contains(SeverityObservation.class)) {
                ccdSeverity.setText(CcdUtils.createReferenceEntryText(SeverityObservation.class.getSimpleName() + severityObservation.getId()));
            } else {
                ccdSeverity.setText(CcdUtils.createEntryText(severityObservation.getSeverityText()));
            }
        }

        CE code = CcdUtils.createCE(severityObservation.getSeverityCode(), CodeSystem.SNOMED_CT.getOid());
        ccdSeverity.getValues().add(code);

        return ccdSeverity;
    }

    public static org.eclipse.mdht.uml.cda.Author buildAuthor(com.scnsoft.eldermark.entity.Author author) {
        org.eclipse.mdht.uml.cda.Author ccdAuthor = CDAFactory.eINSTANCE.createAuthor();

        Date time = author.getTime();
        TS effectiveTime = DatatypesFactory.eINSTANCE.createTS();
        if (time != null) {
            effectiveTime.setValue(CcdUtils.formatSimpleDate(time));
            ccdAuthor.setTime(effectiveTime);
        } else {
            effectiveTime.setNullFlavor(NullFlavor.NI);
        }
        ccdAuthor.setTime(effectiveTime);

        Organization ccdOrganization = author.getOrganization();
        Person ccdPerson = author.getPerson();

        AssignedAuthor assignedAuthor = CDAFactory.eINSTANCE.createAssignedAuthor();
        if (ccdOrganization != null) {

            II id = DatatypesFactory.eINSTANCE.createII();
            id.setNullFlavor(NullFlavor.NA);
            assignedAuthor.getIds().add(id);

            if (ccdOrganization.getAddresses() != null) {
                for (OrganizationAddress address : ccdOrganization.getAddresses()) {
                    CcdUtils.addConvertedAddress(assignedAuthor.getAddrs(), address);
                }
            } else {
                CcdUtils.addConvertedAddress(assignedAuthor.getAddrs(), null);
            }
            if (ccdOrganization.getTelecom() != null) {
                CcdUtils.addConvertedTelecom(assignedAuthor.getTelecoms(), ccdOrganization.getTelecom());
            } else {
                assignedAuthor.getTelecoms().add(CcdUtils.getNullTelecom());
            }
            org.eclipse.mdht.uml.cda.Organization organization = CDAFactory.eINSTANCE.createOrganization();
            ON on = DatatypesFactory.eINSTANCE.createON();
            if (ccdOrganization.getName() != null) {
                on.addText(ccdOrganization.getName());
            } else {
                on.setNullFlavor(NullFlavor.NI);
            }
            organization.getNames().add(on);
            assignedAuthor.setRepresentedOrganization(organization);
        } else if (ccdPerson != null) {
            II id = DatatypesFactory.eINSTANCE.createII();
            if (ccdPerson.getId() != null) {
                id.setRoot("2.16.840.1.113883.4.6");
                id.setExtension(ccdPerson.getId().toString());
            } else {
                id.setNullFlavor(NullFlavor.NI);
            }
            assignedAuthor.getIds().add(id);
            if (ccdPerson.getCode() != null) {
                assignedAuthor.setCode(CcdUtils.createCE(ccdPerson.getCode(), CodeSystem.NUCC_PROVIDER_CODES.getOid()));
            }
            if (ccdPerson.getAddresses() != null) {
                for (PersonAddress address : ccdPerson.getAddresses()) {
                    CcdUtils.addConvertedAddress(assignedAuthor.getAddrs(), address);
                }
            } else {
                assignedAuthor.getAddrs().add(CcdUtils.getNullAddress());
            }
            if (ccdPerson.getTelecoms() != null) {
                for (PersonTelecom telecom : ccdPerson.getTelecoms()) {
                    CcdUtils.addConvertedTelecom(assignedAuthor.getTelecoms(), telecom);
                }
            } else {
                assignedAuthor.getTelecoms().add(CcdUtils.getNullTelecom());
            }
            org.eclipse.mdht.uml.cda.Person person = buildPerson(ccdPerson);
            assignedAuthor.setAssignedPerson(person);
        } else {
            assignedAuthor.setNullFlavor(NullFlavor.NI);
        }
        ccdAuthor.setAssignedAuthor(assignedAuthor);

        return ccdAuthor;
    }

    public static org.eclipse.mdht.uml.cda.Person buildPerson(Person person) {
        org.eclipse.mdht.uml.cda.Person cdaPerson = CDAFactory.eINSTANCE.createPerson();
        if (person.getNames() != null) {
            for (Name name : person.getNames()) {
                CcdUtils.addConvertedName(cdaPerson.getNames(), name);
            }
        } else {
            cdaPerson.getNames().add(CcdUtils.getNullName());
        }
        return cdaPerson;
    }

    public static Performer2 buildPerformer2(Person person) {
        Performer2 performer = CDAFactory.eINSTANCE.createPerformer2();
        performer.setTypeCode(ParticipationPhysicalPerformer.PRF);
        AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
        II id = DatatypesFactory.eINSTANCE.createII();
        if (person.getId() != null) {
            id.setRoot("2.16.840.1.113883.4.6");
            id.setExtension(person.getId().toString());
        } else {
            id.setNullFlavor(NullFlavor.NI);
        }
        assignedEntity.getIds().add(id);
        if (person.getCode() != null) {
            assignedEntity.setCode(CcdUtils.createCE(person.getCode(), CodeSystem.NUCC_PROVIDER_CODES.getOid()));
        }
        if (person.getAddresses() != null) {
            for (PersonAddress address : person.getAddresses()) {
                CcdUtils.addConvertedAddress(assignedEntity.getAddrs(), address);
            }
        } else {
            assignedEntity.getAddrs().add(CcdUtils.getNullAddress());
        }
        if (person.getTelecoms() != null) {
            for (PersonTelecom telecom : person.getTelecoms()) {
                CcdUtils.addConvertedTelecom(assignedEntity.getTelecoms(), telecom);
            }
        } else {
            assignedEntity.getTelecoms().add(CcdUtils.getNullTelecom());
        }
        org.eclipse.mdht.uml.cda.Person assignedPerson = CDAFactory.eINSTANCE.createPerson();
        if (person.getNames() != null) {
            for (Name name : person.getNames()) {
                CcdUtils.addConvertedName(assignedPerson.getNames(), name);
            }
        } else {
            assignedPerson.getNames().add(CcdUtils.getNullName());
        }
        assignedEntity.setAssignedPerson(assignedPerson);
        performer.setAssignedEntity(assignedEntity);

        return performer;
    }


    public static SubstanceAdministration buildMedicationActivity(Medication medication, Set<Class> entriesReferredToSectionText) {
        SubstanceAdministration substanceAdministration = CDAFactory.eINSTANCE.createSubstanceAdministration();
        substanceAdministration.setClassCode(ActClass.SBADM);

        if (medication.getMoodCode() != null) {
            substanceAdministration.setMoodCode(x_DocumentSubstanceMood.valueOf(medication.getMoodCode()));
        } else {
            substanceAdministration.setMoodCode(x_DocumentSubstanceMood.INT);
        }

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.16");
        substanceAdministration.getTemplateIds().add(templateId);
        CcdUtils.addConsanaId(substanceAdministration.getIds(), medication.getConsanaId());

        substanceAdministration.getIds().add(CcdUtils.getId(medication.getId()));

        CD deliveryMethodCode = CcdUtils.createCD(medication.getDeliveryMethod());
        substanceAdministration.setCode(deliveryMethodCode);

        if (medication.getFreeTextSig() != null) {
            if (entriesReferredToSectionText.contains(Medication.class)) {
                substanceAdministration.setText(CcdUtils.createReferenceEntryText(Medication.class.getSimpleName() + medication.getId()));
            } else {
                substanceAdministration.setText(CcdUtils.createEntryText(medication.getFreeTextSig()));
            }
        }

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        if (medication.getStatusCode() != null) {
            statusCode.setCode(medication.getStatusCode());
        } else {
            statusCode.setNullFlavor(NullFlavor.NI);
        }
        substanceAdministration.setStatusCode(statusCode);

        IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
        Date timeLow = medication.getMedicationStarted();
        Date timeHigh = medication.getMedicationStopped();
        if (timeLow != null || timeHigh != null) {
            IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
            IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
            if (timeLow != null) {
                low.setValue(CcdUtils.formatSimpleDate(timeLow));
            } else {
                low.setNullFlavor(NullFlavor.NI);
            }
            if (timeHigh != null) {
                high.setValue(CcdUtils.formatSimpleDate(timeHigh));
            } else {
                high.setNullFlavor(NullFlavor.NI);
            }
            effectiveTime.setLow(low);
            effectiveTime.setHigh(high);
        } else {
            effectiveTime.setNullFlavor(NullFlavor.NI);
        }
        substanceAdministration.getEffectiveTimes().add(effectiveTime);

        if (medication.getAdministrationTimingValue() != null) {
            PIVL_TS effectiveTime1 = DatatypesFactory.eINSTANCE.createPIVL_TS();
            effectiveTime1.setOperator(SetOperator.A);
            effectiveTime1.setValue(medication.getAdministrationTimingValue());
            substanceAdministration.getEffectiveTimes().add(effectiveTime1);
        } else if (medication.getAdministrationTimingPeriod() != null && medication.getAdministrationTimingUnit() != null) {
            PIVL_TS effectiveTime1 = DatatypesFactory.eINSTANCE.createPIVL_TS();
            effectiveTime1.setOperator(SetOperator.A);
            PQ pq = DatatypesFactory.eINSTANCE.createPQ();
            pq.setValue(BigDecimal.valueOf(medication.getAdministrationTimingPeriod()));
            pq.setUnit(medication.getAdministrationTimingUnit());
            effectiveTime1.setPeriod(pq);
            substanceAdministration.getEffectiveTimes().add(effectiveTime1);
        }

        if (medication.getRepeatNumber() != null) {
            IVL_INT repeatNumber = DatatypesFactory.eINSTANCE.createIVL_INT();
            repeatNumber.setValue(BigInteger.valueOf(medication.getRepeatNumber()));
            substanceAdministration.setRepeatNumber(repeatNumber);
        }

        CE codeCE = CcdUtils.createCE(medication.getRoute(), "2.16.840.1.113883.3.26.1.1");
        substanceAdministration.setRouteCode(codeCE);

        codeCE = CcdUtils.createCE(medication.getSite(), CodeSystem.SNOMED_CT.getOid());
        substanceAdministration.getApproachSiteCodes().add(codeCE);

        if (medication.getDoseQuantity() != null) {
            IVL_PQ pq = DatatypesFactory.eINSTANCE.createIVL_PQ();
            pq.setValue(BigDecimal.valueOf(medication.getDoseQuantity()));
            if (medication.getDoseUnits() != null) {
                pq.setUnit(medication.getDoseUnits());
            }
            substanceAdministration.setDoseQuantity(pq);
        }

        if (medication.getRateQuantity() != null && medication.getRateUnits() != null) {
            IVL_PQ pq = DatatypesFactory.eINSTANCE.createIVL_PQ();
            pq.setValue(BigDecimal.valueOf(medication.getRateQuantity()));
            pq.setUnit(medication.getRateUnits());
            substanceAdministration.setRateQuantity(pq);
        }

        codeCE = CcdUtils.createCE(medication.getAdministrationUnitCode(), "2.16.840.1.113883.3.26.1.1");
        substanceAdministration.setAdministrationUnitCode(codeCE);

        Consumable consumable = CDAFactory.eINSTANCE.createConsumable();
        if (medication.getMedicationInformation() != null) {
            consumable.setManufacturedProduct(buildMedicationInformation(medication.getMedicationInformation(), entriesReferredToSectionText));
            CcdUtils.addConsanaId(consumable.getManufacturedProduct().getIds(), medication.getConsanaId());
        } else {
            consumable.setManufacturedProduct(buildNullMedicationInformation());
        }
        substanceAdministration.setConsumable(consumable);

        if (medication.getDrugVehicles() != null) {
            for (DrugVehicle drugVehicle : medication.getDrugVehicles()) {
                Participant2 participant2 = CDAFactory.eINSTANCE.createParticipant2();
                participant2.setTypeCode(ParticipationType.CSM);
                participant2.setParticipantRole(buildDrugVehicle(drugVehicle));
                substanceAdministration.getParticipants().add(participant2);
            }
        }

        if (medication.getIndications() != null) {
            for (Indication indication : medication.getIndications()) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.RSON);
                entryRelationship.setObservation(buildIndication(indication));
                substanceAdministration.getEntryRelationships().add(entryRelationship);
            }
        }

        if (medication.getInstructions() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
            entryRelationship.setInversionInd(true);
            entryRelationship.setAct(buildInstructions(medication.getInstructions(), entriesReferredToSectionText));
            substanceAdministration.getEntryRelationships().add(entryRelationship);
        }

        if (medication.getMedicationSupplyOrder() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.REFR);
            entryRelationship.setSupply(buildMedicationSupplyOrder(medication.getMedicationSupplyOrder(), entriesReferredToSectionText));
            substanceAdministration.getEntryRelationships().add(entryRelationship);
        }

        if (medication.getMedicationDispenses() != null) {
            for (MedicationDispense medicationDispense : medication.getMedicationDispenses()) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.REFR);
                entryRelationship.setSupply(buildMedicationDispense(medicationDispense, entriesReferredToSectionText));
                substanceAdministration.getEntryRelationships().add(entryRelationship);
            }
        }

        if (medication.getReactionObservation() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.CAUS);
            entryRelationship.setObservation(SectionEntryFactory.buildReactionObservation(medication.getReactionObservation(), entriesReferredToSectionText));
            substanceAdministration.getEntryRelationships().add(entryRelationship);
        }

        if (medication.getPreconditions() != null) {
            for (MedicationPrecondition precondition : medication.getPreconditions()) {
                substanceAdministration.getPreconditions().add(buildMedicationPrecondition(precondition));
            }
        }

        if (medication.getPerformer() != null) {
            substanceAdministration.getPerformers().add(buildPerformer2(medication.getPerformer()));
        }

        return substanceAdministration;
    }

    public static SubstanceAdministration buildNullMedicationActivity() {
        SubstanceAdministration substanceAdministration = CDAFactory.eINSTANCE.createSubstanceAdministration();
        substanceAdministration.setClassCode(ActClass.SBADM);
        substanceAdministration.setMoodCode(x_DocumentSubstanceMood.EVN);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.16");
        substanceAdministration.getTemplateIds().add(templateId);

        substanceAdministration.getIds().add(CcdUtils.getNullId());

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        statusCode.setNullFlavor(NullFlavor.NI);
        substanceAdministration.setStatusCode(statusCode);

        IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
        effectiveTime.setNullFlavor(NullFlavor.NI);
        substanceAdministration.getEffectiveTimes().add(effectiveTime);

        Consumable consumable = CDAFactory.eINSTANCE.createConsumable();
        consumable.setManufacturedProduct(buildNullMedicationInformation());
        substanceAdministration.setConsumable(consumable);

        return substanceAdministration;
    }

    public static Supply buildMedicationDispense(MedicationDispense medicationDispense, Set<Class> entriesReferredToSectionText) {
        Supply supply = CDAFactory.eINSTANCE.createSupply();
        supply.setClassCode(ActClassSupply.SPLY);
        supply.setMoodCode(x_DocumentSubstanceMood.EVN);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.18");
        supply.getTemplateIds().add(templateId);

        supply.getIds().add(CcdUtils.getId(medicationDispense.getId()));

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        if (medicationDispense.getStatusCode() != null) {
            statusCode.setCode(medicationDispense.getStatusCode());
        } else {
            statusCode.setNullFlavor(NullFlavor.NI);
        }
        supply.setStatusCode(statusCode);

        Date timeLow = medicationDispense.getDispenseDateLow();
        Date timeHigh = medicationDispense.getDispenseDateHigh();
        if (timeLow != null || timeHigh != null) {
            IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
            if (timeLow != null) {
                IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
                low.setValue(CcdUtils.formatSimpleDate(timeLow));
                effectiveTime.setLow(low);
            }
            if (timeHigh != null) {
                IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
                high.setValue(CcdUtils.formatSimpleDate(timeHigh));
                effectiveTime.setHigh(high);
            }
            supply.getEffectiveTimes().add(effectiveTime);
        }

        if (medicationDispense.getFillNumber() != null) {
            IVL_INT repeatNumber = DatatypesFactory.eINSTANCE.createIVL_INT();
            repeatNumber.setValue(medicationDispense.getFillNumber());
            supply.setRepeatNumber(repeatNumber);
        }

        if (medicationDispense.getQuantity() != null) {
            PQ quantity = DatatypesFactory.eINSTANCE.createIVL_PQ();
            quantity.setValue(BigDecimal.valueOf(medicationDispense.getQuantity()));
            supply.setQuantity(quantity);
        }

        if (medicationDispense.getMedicationInformation() != null) {
            Product product = CDAFactory.eINSTANCE.createProduct();
            product.setManufacturedProduct(buildMedicationInformation(medicationDispense.getMedicationInformation(), entriesReferredToSectionText));
            supply.setProduct(product);
        }

        if (medicationDispense.getImmunizationMedicationInformation() != null) {
            Product product = CDAFactory.eINSTANCE.createProduct();
            product.setManufacturedProduct(buildImmunizationMedicationInformation(medicationDispense.getImmunizationMedicationInformation(), entriesReferredToSectionText));
            supply.setProduct(product);
        }

        if (medicationDispense.getProvider() != null) {
            Performer2 performer2 = CDAFactory.eINSTANCE.createPerformer2();
            AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
            assignedEntity.getIds().add(DatatypesFactory.eINSTANCE.createII()); // validator needs id
            assignedEntity.getRepresentedOrganizations().add(buildOrganization(medicationDispense.getProvider(), true));
            performer2.setAssignedEntity(assignedEntity);
            supply.getPerformers().add(performer2);
        }

        if (medicationDispense.getMedicationSupplyOrder() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.REFR);
            entryRelationship.setSupply(buildMedicationSupplyOrder(medicationDispense.getMedicationSupplyOrder(), entriesReferredToSectionText));
            supply.getEntryRelationships().add(entryRelationship);
        }

        return supply;
    }

    public static Supply buildMedicationSupplyOrder(MedicationSupplyOrder medicationSupplyOrder, Set<Class> entriesReferredToSectionText) {
        Supply supply = CDAFactory.eINSTANCE.createSupply();
        supply.setClassCode(ActClassSupply.SPLY);
        supply.setMoodCode(x_DocumentSubstanceMood.INT);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.17");
        supply.getTemplateIds().add(templateId);

        supply.getIds().add(CcdUtils.getId(medicationSupplyOrder.getId()));

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        if (medicationSupplyOrder.getStatusCode() != null) {
            statusCode.setCode(medicationSupplyOrder.getStatusCode());
        } else {
            statusCode.setNullFlavor(NullFlavor.NI);
        }
        supply.setStatusCode(statusCode);

        if (medicationSupplyOrder.getTimeHigh() != null) {
            IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
            IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
            high.setValue(CcdUtils.formatSimpleDate(medicationSupplyOrder.getTimeHigh()));
            effectiveTime.setHigh(high);
            supply.getEffectiveTimes().add(effectiveTime);
        }

        if (medicationSupplyOrder.getRepeatNumber() != null) {
            IVL_INT repeatNumber = DatatypesFactory.eINSTANCE.createIVL_INT();
            repeatNumber.setValue(medicationSupplyOrder.getRepeatNumber());
            supply.setRepeatNumber(repeatNumber);
        }

        if (medicationSupplyOrder.getQuantity() != null) {
            PQ quantity = DatatypesFactory.eINSTANCE.createIVL_PQ();
            quantity.setValue(BigDecimal.valueOf(medicationSupplyOrder.getQuantity()));
            supply.setQuantity(quantity);
        }

        if (medicationSupplyOrder.getMedicationInformation() != null) {
            Product product = CDAFactory.eINSTANCE.createProduct();
            product.setManufacturedProduct(buildMedicationInformation(medicationSupplyOrder.getMedicationInformation(), entriesReferredToSectionText));
            supply.setProduct(product);
        }

        if (medicationSupplyOrder.getImmunizationMedicationInformation() != null) {
            Product product = CDAFactory.eINSTANCE.createProduct();
            product.setManufacturedProduct(buildImmunizationMedicationInformation(medicationSupplyOrder.getImmunizationMedicationInformation(), entriesReferredToSectionText));
            supply.setProduct(product);
        }

        if (medicationSupplyOrder.getAuthor() != null) {
            supply.getAuthors().add(SectionEntryFactory.buildAuthor(medicationSupplyOrder.getAuthor()));
        }

        if (medicationSupplyOrder.getInstructions() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
            entryRelationship.setInversionInd(true);
            entryRelationship.setAct(buildInstructions(medicationSupplyOrder.getInstructions(), entriesReferredToSectionText));
            supply.getEntryRelationships().add(entryRelationship);
        }

        return supply;
    }

    public static ManufacturedProduct buildImmunizationMedicationInformation(ImmunizationMedicationInformation immunizationMedicationInformation, Set<Class> entriesReferredToSectionText) {
        ManufacturedProduct manufacturedProduct = CDAFactory.eINSTANCE.createManufacturedProduct();
        manufacturedProduct.setClassCode(RoleClassManufacturedProduct.MANU);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.54");
        manufacturedProduct.getTemplateIds().add(templateId);

        manufacturedProduct.getIds().add(CcdUtils.getId(immunizationMedicationInformation.getId()));

        Material material = CDAFactory.eINSTANCE.createMaterial();

        CE code = CcdUtils.createCE(immunizationMedicationInformation.getCode(), "2.16.840.1.113883.12.292");
        material.setCode(code);

        if (immunizationMedicationInformation.getText() != null) {
            if (entriesReferredToSectionText.contains(ImmunizationMedicationInformation.class)) {
                code.setOriginalText(CcdUtils.createReferenceEntryText(ImmunizationMedicationInformation.class.getSimpleName() + immunizationMedicationInformation.getId()));
            } else {
                code.setOriginalText(CcdUtils.createEntryText(immunizationMedicationInformation.getText()));
            }
        }

        if (immunizationMedicationInformation.getLotNumberText() != null) {
            ST st = DatatypesFactory.eINSTANCE.createST();
            st.addText(immunizationMedicationInformation.getLotNumberText());
            material.setLotNumberText(st);
        }

        manufacturedProduct.setManufacturedMaterial(material);

        if (immunizationMedicationInformation.getManufactorer() != null) {
            org.eclipse.mdht.uml.cda.Organization organization = buildOrganization(immunizationMedicationInformation.getManufactorer(), true);
            manufacturedProduct.setManufacturerOrganization(organization);
        }

        return manufacturedProduct;
    }

    public static ManufacturedProduct buildNullImmunizationMedicationInformation() {
        ManufacturedProduct manufacturedProduct = CDAFactory.eINSTANCE.createManufacturedProduct();
        manufacturedProduct.setClassCode(RoleClassManufacturedProduct.MANU);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.54");
        manufacturedProduct.getTemplateIds().add(templateId);

        manufacturedProduct.getIds().add(CcdUtils.getNullId());

        Material material = CDAFactory.eINSTANCE.createMaterial();
        CE code = DatatypesFactory.eINSTANCE.createCE();
        code.setNullFlavor(NullFlavor.NI);
        material.setCode(code);
        manufacturedProduct.setManufacturedMaterial(material);

        return manufacturedProduct;
    }

    public static ManufacturedProduct buildMedicationInformation(MedicationInformation medicationInformation, Set<Class> entriesReferredToSectionText) {
        ManufacturedProduct manufacturedProduct = CDAFactory.eINSTANCE.createManufacturedProduct();
        manufacturedProduct.setClassCode(RoleClassManufacturedProduct.MANU);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.23");
        manufacturedProduct.getTemplateIds().add(templateId);

        manufacturedProduct.getIds().add(CcdUtils.getId(medicationInformation.getId()));

        Material material = CDAFactory.eINSTANCE.createMaterial();

        CE code = CcdUtils.createCEWithDefaultDisplayName(medicationInformation.getProductNameCode(),
                medicationInformation.getProductNameText(), "2.16.840.1.113883.6.88");
        material.setCode(code);

        if (medicationInformation.getProductNameText() != null) {
            if (entriesReferredToSectionText.contains(MedicationInformation.class)) {
                code.setOriginalText(CcdUtils.createReferenceEntryText(MedicationInformation.class.getSimpleName() + medicationInformation.getId()));
            } else {
                code.setOriginalText(CcdUtils.createEntryText(medicationInformation.getProductNameText()));
            }
        }

        manufacturedProduct.setManufacturedMaterial(material);

        if (medicationInformation.getManufactorer() != null) {
            org.eclipse.mdht.uml.cda.Organization organization = buildOrganization(medicationInformation.getManufactorer(), true);
            manufacturedProduct.setManufacturerOrganization(organization);
        }

        return manufacturedProduct;
    }

    public static ManufacturedProduct buildNullMedicationInformation() {
        ManufacturedProduct manufacturedProduct = CDAFactory.eINSTANCE.createManufacturedProduct();
        manufacturedProduct.setClassCode(RoleClassManufacturedProduct.MANU);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.23");
        manufacturedProduct.getTemplateIds().add(templateId);

        manufacturedProduct.getIds().add(CcdUtils.getNullId());

        Material material = CDAFactory.eINSTANCE.createMaterial();
        CE code = DatatypesFactory.eINSTANCE.createCE();
        code.setNullFlavor(NullFlavor.NI);
        material.setCode(code);
        manufacturedProduct.setManufacturedMaterial(material);

        return manufacturedProduct;
    }

    public static Act buildInstructions(Instructions instructions, Set<Class> entriesReferredToSectionText) {
        Act act = CDAFactory.eINSTANCE.createAct();
        act.setClassCode(x_ActClassDocumentEntryAct.ACT);
        act.setMoodCode(x_DocumentActMood.INT);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.20");
        act.getTemplateIds().add(templateId);

        CD code = CcdUtils.createCD(instructions.getCode(), CodeSystem.SNOMED_CT.getOid());
        act.setCode(code);

        if (instructions.getText() != null) {
            if (entriesReferredToSectionText.contains(Instructions.class)) {
                act.setText(CcdUtils.createReferenceEntryText(Instructions.class.getSimpleName() + instructions.getId()));
            } else {
                act.setText(CcdUtils.createEntryText(instructions.getText()));
            }
        }

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        statusCode.setCode("completed");
        act.setStatusCode(statusCode);

        return act;
    }

    public static Observation buildIndication(Indication indication) {
        Observation observation = CDAFactory.eINSTANCE.createObservation();
        observation.setClassCode(ActClassObservation.OBS);
        observation.setMoodCode(x_ActMoodDocumentObservation.EVN);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.19");
        observation.getTemplateIds().add(templateId);

        observation.getIds().add(CcdUtils.getId(indication.getId()));

        CD code = CcdUtils.createCD(indication.getCode(), CodeSystem.SNOMED_CT.getOid());
        observation.setCode(code);

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        statusCode.setCode("completed");
        observation.setStatusCode(statusCode);

        if (indication.getTimeLow() != null || indication.getTimeHigh() != null) {
            observation.setEffectiveTime(CcdUtils.convertEffectiveTime(indication.getTimeLow(), indication.getTimeHigh()));
        }

        CD value = CcdUtils.createCD(indication.getValue(), CodeSystem.SNOMED_CT.getOid());
        observation.getValues().add(value);

        return observation;
    }

    public static ParticipantRole buildDrugVehicle(DrugVehicle drugVehicle) {
        ParticipantRole participantRole = CDAFactory.eINSTANCE.createParticipantRole();
        participantRole.setClassCode(RoleClassRoot.MANU);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.24");
        participantRole.getTemplateIds().add(templateId);

        CE code = CcdUtils.createCE("412307009", "Drug vehicle (substance)", CodeSystem.SNOMED_CT);
        participantRole.setCode(code);

        PlayingEntity playingEntity = CDAFactory.eINSTANCE.createPlayingEntity();
        playingEntity.setClassCode(EntityClassRoot.MMAT);

        CE code1 = CcdUtils.createCE(drugVehicle.getCode());
        playingEntity.setCode(code1);

        PN name = DatatypesFactory.eINSTANCE.createPN();
        if (drugVehicle.getName() != null) {
            name.addText(drugVehicle.getName());
        } else {
            name.setNullFlavor(NullFlavor.NI);
        }
        playingEntity.getNames().add(name);

        participantRole.setPlayingEntity(playingEntity);

        return participantRole;
    }

    public static Precondition buildMedicationPrecondition(MedicationPrecondition precondition) {
        Precondition p = CDAFactory.eINSTANCE.createPrecondition();

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.25");
        p.getTemplateIds().add(templateId);

        Criterion criterion = CDAFactory.eINSTANCE.createCriterion();

        CD code = CcdUtils.createCD(precondition.getCode());
        criterion.setCode(code);

        if (precondition.getText() != null) {
            ED text = DatatypesFactory.eINSTANCE.createED();
            text.addText(precondition.getText());
            criterion.setText(text);
        }

        CD value = CcdUtils.createCD(precondition.getValue());
        criterion.setValue(value);

        p.setCriterion(criterion);

        return p;
    }

    public static org.eclipse.mdht.uml.cda.Organization buildOrganization(Organization organization, boolean full) {
        org.eclipse.mdht.uml.cda.Organization ccdOrganization = CDAFactory.eINSTANCE.createOrganization();

        if (full) {
            ccdOrganization.getIds().add(CcdUtils.getId(organization.getId()));

            if (organization.getAddresses() != null) {
                for (OrganizationAddress address : organization.getAddresses()) {
                    CcdUtils.addConvertedAddress(ccdOrganization.getAddrs(), address);
                }
            } else {
                CcdUtils.addConvertedAddress(ccdOrganization.getAddrs(), null);
            }

            if (organization.getTelecom() != null) {
                CcdUtils.addConvertedTelecom(ccdOrganization.getTelecoms(), organization.getTelecom());
            } else {
                ccdOrganization.getTelecoms().add(CcdUtils.getNullTelecom());
            }
        }

        ON on = DatatypesFactory.eINSTANCE.createON();
        if (organization.getName() != null) {
            on.addText(organization.getName());
        } else {
            on.setNullFlavor(NullFlavor.NI);
        }
        ccdOrganization.getNames().add(on);

        return ccdOrganization;
    }

    public static org.openhealthtools.mdht.uml.cda.consol.ProblemObservation buildProblemObservation(
            ProblemObservation problemObservation, Set<Class> entriesReferredToSectionText) {
        org.openhealthtools.mdht.uml.cda.consol.ProblemObservation problemObservationCcd = ConsolFactory.eINSTANCE.createProblemObservation();

        problemObservationCcd.setClassCode(ActClassObservation.OBS);
        problemObservationCcd.setMoodCode(x_ActMoodDocumentObservation.EVN);

        II alertObservationTemplateId = DatatypesFactory.eINSTANCE.createII();
        alertObservationTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.4");
        problemObservationCcd.getTemplateIds().add(alertObservationTemplateId);

        problemObservationCcd.getIds().add(CcdUtils.getId(problemObservation.getId()));

        if (problemObservation.getNegationInd() != null) {
            problemObservationCcd.setNegationInd(problemObservation.getNegationInd());
        }

        problemObservationCcd.setCode(CcdUtils.createCD(problemObservation.getProblemType(), CodeSystem.SNOMED_CT.getOid()));

        if (problemObservation.getProblemName() != null) {
            if (entriesReferredToSectionText.contains(ProblemObservation.class)) {
                problemObservationCcd.setText(CcdUtils.createReferenceEntryText(ProblemObservation.class.getSimpleName() + problemObservation.getId()));
            } else {
                problemObservationCcd.setText(CcdUtils.createEntryText(problemObservation.getProblemName()));
            }
        }

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        statusCode.setCode("completed");
        problemObservationCcd.setStatusCode(statusCode);

        problemObservationCcd.setEffectiveTime(CcdUtils.convertEffectiveTime(problemObservation.getProblemDateTimeLow(), problemObservation.getProblemDateTimeHigh()));

        // Problem Observation value
        CD ccdCode = CcdUtils.createCD(problemObservation.getProblemCode(), CodeSystem.SNOMED_CT.getOid());
        if (problemObservation.getProblemCode() == null)
            ccdCode.setNullFlavor(NullFlavor.OTH);

        // Problem Observation value translations
        if (!CollectionUtils.isEmpty(problemObservation.getTranslations())) {
            for (CcdCode translationCode : problemObservation.getTranslations()) {
                CD translation = CcdUtils.createCD(translationCode);
                ccdCode.getTranslations().add(translation);
            }
        }
        problemObservationCcd.getValues().add(ccdCode);

        // Age Observation
        if (problemObservation.getAgeObservationValue() != null) {
            AgeObservation ageObservation = SectionEntryFactory.buildAgeObservation(
                    problemObservation.getAgeObservationUnit(), problemObservation.getAgeObservationValue());
            problemObservationCcd.addObservation(ageObservation);
            ((EntryRelationship) ageObservation.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
            ((EntryRelationship) ageObservation.eContainer()).setInversionInd(Boolean.TRUE);
        }

        // Problem Status
        if (problemObservation.getProblemStatusCode() != null || !StringUtils.isEmpty(problemObservation.getProblemStatusText())) {
            ProblemStatus problemStatusObservation = ConsolFactory.eINSTANCE.createProblemStatus();
            problemObservationCcd.addObservation(problemStatusObservation);
            ((EntryRelationship) problemStatusObservation.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.REFR);

            problemStatusObservation.setClassCode(ActClassObservation.OBS);
            problemStatusObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);

            II problemStatusObservationTemplateId = DatatypesFactory.eINSTANCE.createII();
            problemStatusObservationTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.6");
            problemStatusObservation.getTemplateIds().add(problemStatusObservationTemplateId);

            CD code = CcdUtils.createCD(SectionTypeCode.STATUS_OBSERVATION);
            problemStatusObservation.setCode(code);

            if (problemObservation.getProblemStatusText() != null) {
                String refId = ProblemObservation.class.getSimpleName() + "Status" + problemObservation.getId();
                ED originalText = DatatypesFactory.eINSTANCE.createED();
                TEL ref = DatatypesFactory.eINSTANCE.createTEL();
                ref.setValue("#" + refId);
                originalText.setReference(ref);
                problemStatusObservation.setText(originalText);
            }

            CS problemStatusObservationStatusCode = DatatypesFactory.eINSTANCE.createCS("completed");
            problemStatusObservation.setStatusCode(problemStatusObservationStatusCode);

            CD problemStatusObservationValue = CcdUtils.createCD(problemObservation.getProblemStatusCode(), CodeSystem.SNOMED_CT.getOid());
            problemStatusObservation.getValues().add(problemStatusObservationValue);
        }

        // Health Status Observation
        if (problemObservation.getHealthStatusCode() != null || !StringUtils.isEmpty(problemObservation.getHealthStatusObservationText())) {
            // FIXME
            /*
            ProblemHealthStatusObservation healthStatusObservation = ConsolFactory.eINSTANCE.createProblemHealthStatusObservation();
            problemObservationCcd.addObservation(healthStatusObservation);
            ((EntryRelationship) healthStatusObservation.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.REFR);

            healthStatusObservation.setClassCode(ActClassObservation.OBS);
            healthStatusObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);

            II templateId = DatatypesFactory.eINSTANCE.createII();
            templateId.setRoot("2.16.840.1.113883.10.20.22.4.5");
            healthStatusObservation.getTemplateIds().add(templateId);

            CD code = CcdUtils.createCD(SectionTypeCode.HEALTH_STATUS_OBSERVATION);
            healthStatusObservation.setCode(code);

            if (problemObservation.getHealthStatusObservationText() != null) {
                ED text = DatatypesFactory.eINSTANCE.createED();
                text.addText(problemObservation.getHealthStatusObservationText());
                healthStatusObservation.setText(text);
            }

            CS statusCode1 = DatatypesFactory.eINSTANCE.createCS("completed");
            healthStatusObservation.setStatusCode(statusCode1);

            CD value = CcdUtils.createCD(problemObservation.getHealthStatusCode(), CodeSystem.SNOMED_CT.getOid());
            healthStatusObservation.getValues().add(value);
            */
        }

        return problemObservationCcd;
    }

    public static org.openhealthtools.mdht.uml.cda.consol.ProblemObservation buildNullProblemObservation() {
        org.openhealthtools.mdht.uml.cda.consol.ProblemObservation problemObservationCcd = ConsolFactory.eINSTANCE.createProblemObservation();

        problemObservationCcd.setClassCode(ActClassObservation.OBS);
        problemObservationCcd.setMoodCode(x_ActMoodDocumentObservation.EVN);

        II alertObservationTemplateId = DatatypesFactory.eINSTANCE.createII();
        alertObservationTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.4");
        problemObservationCcd.getTemplateIds().add(alertObservationTemplateId);

        problemObservationCcd.getIds().add(CcdUtils.getNullId());

        CD problemType = DatatypesFactory.eINSTANCE.createCD();
        problemType.setNullFlavor(NullFlavor.NI);
        problemObservationCcd.setCode(problemType);

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        statusCode.setCode("completed");
        problemObservationCcd.setStatusCode(statusCode);

        CD problemCode = DatatypesFactory.eINSTANCE.createCD();
        problemCode.setNullFlavor(NullFlavor.UNK);
        problemObservationCcd.getValues().add(problemCode);

        return problemObservationCcd;
    }

    public static org.eclipse.mdht.uml.cda.Procedure buildProcedureActivity(ProcedureActivity procedure, Set<Class> entriesReferredToSectionText) {
        org.eclipse.mdht.uml.cda.Procedure p = CDAFactory.eINSTANCE.createProcedure();

        p.setClassCode(ActClass.PROC);
        if (procedure.getMoodCode() != null) {
            p.setMoodCode(x_DocumentProcedureMood.valueOf(procedure.getMoodCode()));
        }

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.14");
        p.getTemplateIds().add(templateId);

        p.getIds().add(CcdUtils.getId(procedure.getId()));

        if (!StringUtils.isEmpty(procedure.getMoodCode())) {
            p.setMoodCode(x_DocumentProcedureMood.get(procedure.getMoodCode()));
        }

        p.setCode(createProcedureTypeCode(procedure, entriesReferredToSectionText.contains(ProcedureActivity.class)));

        p.setStatusCode(createStatusCode(procedure.getStatusCode()));

        p.setEffectiveTime(CcdUtils.convertEffectiveTime(procedure.getProcedureStarted(), procedure.getProcedureStopped()));

        p.setPriorityCode(CcdUtils.createCE(procedure.getPriorityCode(), "2.16.840.1.113883.5.7"));

        if (procedure.getMethodCode() != null) {
            p.getMethodCodes().add(CcdUtils.createCE(procedure.getMethodCode()));
        }

        if (procedure.getBodySiteCodes() != null) {
            for (CcdCode bodySite : procedure.getBodySiteCodes()) {
                p.getTargetSiteCodes().add(CcdUtils.createCD(bodySite, CodeSystem.SNOMED_CT.getOid()));
            }
        }

        if (procedure.getSpecimenIds() != null) {
            for (String specimenId : procedure.getSpecimenIds()) {
                Specimen specimen = CDAFactory.eINSTANCE.createSpecimen();
                SpecimenRole specimenRole = CDAFactory.eINSTANCE.createSpecimenRole();
                specimenRole.getIds().add(CcdUtils.getId(specimenId));
                specimen.setSpecimenRole(specimenRole);
                p.getSpecimens().add(specimen);
            }
        }

        if (!CollectionUtils.isEmpty(procedure.getPerformers())) {
            for (Organization organization : procedure.getPerformers()) {
                Performer2 performer2 = CDAFactory.eINSTANCE.createPerformer2();
                AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
                assignedEntity.getRepresentedOrganizations().add(SectionEntryFactory.buildOrganization(organization, true));
                performer2.setAssignedEntity(assignedEntity);
                p.getPerformers().add(performer2);
            }
        }

        if (procedure.getProductInstances() != null) {
            for (ProductInstance productInstance : procedure.getProductInstances()) {
                Participant2 participant2 = CDAFactory.eINSTANCE.createParticipant2();
                participant2.setTypeCode(ParticipationType.DEV);
                participant2.setParticipantRole(buildProductInstance(productInstance));
                p.getParticipants().add(participant2);
            }
        }

        if (!CollectionUtils.isEmpty(procedure.getServiceDeliveryLocations())) {
            for (ServiceDeliveryLocation serviceDeliveryLocation : procedure.getServiceDeliveryLocations()) {
                Participant2 participant2 = CDAFactory.eINSTANCE.createParticipant2();
                participant2.setTypeCode(ParticipationType.LOC);
                participant2.setParticipantRole(buildServiceDeliveryLocation(serviceDeliveryLocation));
                p.getParticipants().add(participant2);
            }
        }

        if (!CollectionUtils.isEmpty(procedure.getEncounterIds())) {
            for (String encounterId : procedure.getEncounterIds()) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
                entryRelationship.setInversionInd(true);
                Encounter encounter = CDAFactory.eINSTANCE.createEncounter();
                encounter.setClassCode(ActClass.ENC);
                encounter.setMoodCode(x_DocumentEncounterMood.EVN);
                encounter.getIds().add(CcdUtils.getId(encounterId));
                entryRelationship.setEncounter(encounter);
                p.getEntryRelationships().add(entryRelationship);
            }
        }

        if (procedure.getInstructions() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
            entryRelationship.setInversionInd(true);
            entryRelationship.setAct(SectionEntryFactory.buildInstructions(procedure.getInstructions(), entriesReferredToSectionText));
            p.getEntryRelationships().add(entryRelationship);
        }

        if (!CollectionUtils.isEmpty(procedure.getIndications())) {
            for (Indication indication : procedure.getIndications()) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.RSON);
                entryRelationship.setObservation(SectionEntryFactory.buildIndication(indication));
                p.getEntryRelationships().add(entryRelationship);
            }
        }

        if (procedure.getMedication() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
            entryRelationship.setSubstanceAdministration(SectionEntryFactory.buildMedicationActivity(procedure.getMedication(), entriesReferredToSectionText));
            p.getEntryRelationships().add(entryRelationship);
        }

        return p;
    }

    public static org.eclipse.mdht.uml.cda.Procedure buildNullProcedureActivity() {
        org.eclipse.mdht.uml.cda.Procedure p = CDAFactory.eINSTANCE.createProcedure();

        p.setClassCode(ActClass.PROC);
        // what mood code?
        p.setMoodCode(x_DocumentProcedureMood.EVN);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.14");
        p.getTemplateIds().add(templateId);

        p.getIds().add(CcdUtils.getNullId());

        CE code = DatatypesFactory.eINSTANCE.createCE();
        code.setNullFlavor(NullFlavor.NI);
        p.setCode(code);

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        statusCode.setNullFlavor(NullFlavor.NI);
        p.setStatusCode(statusCode);

        return p;
    }

    public static Act buildProcedureAct(ProcedureActivity procedure, Set<Class> entriesReferredToSectionText) {
        Act act = CDAFactory.eINSTANCE.createAct();

        act.setClassCode(x_ActClassDocumentEntryAct.ACT);
        if (procedure.getMoodCode() != null) {
            act.setMoodCode(x_DocumentActMood.valueOf(procedure.getMoodCode()));
        }

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.12");
        act.getTemplateIds().add(templateId);

        act.getIds().add(CcdUtils.getId(procedure.getId()));

        if (!StringUtils.isEmpty(procedure.getMoodCode())) {
            act.setMoodCode(x_DocumentActMood.get(procedure.getMoodCode()));
        }

        act.setCode(createProcedureTypeCode(procedure, entriesReferredToSectionText.contains(ProcedureActivity.class)));

        act.setStatusCode(createStatusCode(procedure.getStatusCode()));

        act.setEffectiveTime(CcdUtils.convertEffectiveTime(procedure.getProcedureStarted(), procedure.getProcedureStopped()));

        act.setPriorityCode(CcdUtils.createCE(procedure.getPriorityCode(), "2.16.840.1.113883.5.7"));


        if (!CollectionUtils.isEmpty(procedure.getPerformers())) {
            for (Organization organization : procedure.getPerformers()) {
                Performer2 performer2 = CDAFactory.eINSTANCE.createPerformer2();
                AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
                assignedEntity.getRepresentedOrganizations().add(SectionEntryFactory.buildOrganization(organization, true));
                performer2.setAssignedEntity(assignedEntity);
                act.getPerformers().add(performer2);
            }
        }

        if (!CollectionUtils.isEmpty(procedure.getServiceDeliveryLocations())) {
            for (ServiceDeliveryLocation serviceDeliveryLocation : procedure.getServiceDeliveryLocations()) {
                Participant2 participant2 = CDAFactory.eINSTANCE.createParticipant2();
                participant2.setTypeCode(ParticipationType.LOC);
                participant2.setParticipantRole(buildServiceDeliveryLocation(serviceDeliveryLocation));
                act.getParticipants().add(participant2);
            }
        }

        if (!CollectionUtils.isEmpty(procedure.getEncounterIds())) {
            for (String encounterId : procedure.getEncounterIds()) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
                entryRelationship.setInversionInd(true);
                Encounter encounter = CDAFactory.eINSTANCE.createEncounter();
                encounter.setClassCode(ActClass.ENC);
                encounter.setMoodCode(x_DocumentEncounterMood.EVN);
                encounter.getIds().add(CcdUtils.getId(encounterId));
                entryRelationship.setEncounter(encounter);
                act.getEntryRelationships().add(entryRelationship);
            }
        }

        if (procedure.getInstructions() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
            entryRelationship.setInversionInd(true);
            entryRelationship.setAct(SectionEntryFactory.buildInstructions(procedure.getInstructions(), entriesReferredToSectionText));
            act.getEntryRelationships().add(entryRelationship);
        }

        if (!CollectionUtils.isEmpty(procedure.getIndications())) {
            for (Indication indication : procedure.getIndications()) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.RSON);
                entryRelationship.setObservation(SectionEntryFactory.buildIndication(indication));
                act.getEntryRelationships().add(entryRelationship);
            }
        }

        if (procedure.getMedication() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
            entryRelationship.setSubstanceAdministration(SectionEntryFactory.buildMedicationActivity(procedure.getMedication(), entriesReferredToSectionText));
            act.getEntryRelationships().add(entryRelationship);
        }

        return act;
    }


    private static CD createProcedureTypeCode(ProcedureActivity procedure, boolean key) {
        CD code = CcdUtils.createCD(procedure.getProcedureType());
        if (procedure.getProcedureTypeText() != null) {
            if (key) {
                code.setOriginalText(CcdUtils.createReferenceEntryText(ProcedureActivity.class.getSimpleName() + procedure.getId()));
            } else {
                code.setOriginalText(CcdUtils.createEntryText(procedure.getProcedureTypeText()));
            }
        }
        return code;
    }


    public static Observation buildProcedureObservation(ProcedureActivity procedure, Set<Class> entriesReferredToSectionText) {
        Observation obs = CDAFactory.eINSTANCE.createObservation();

        obs.setClassCode(ActClassObservation.OBS);
        if (procedure.getMoodCode() != null) {
            obs.setMoodCode(x_ActMoodDocumentObservation.get(procedure.getMoodCode()));
        }

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.13");
        obs.getTemplateIds().add(templateId);

        obs.getIds().add(CcdUtils.getId(procedure.getId()));


        obs.setCode(createProcedureTypeCode(procedure, entriesReferredToSectionText.contains(ProcedureActivity.class)));

        obs.setStatusCode(createStatusCode(procedure.getStatusCode()));

        obs.setEffectiveTime(CcdUtils.convertEffectiveTime(procedure.getProcedureStarted(), procedure.getProcedureStopped()));

        if (procedure.getValue() != null) {
            obs.getValues().add(CcdUtils.createCD(procedure.getValue()));
        }

        obs.setPriorityCode(CcdUtils.createCE(procedure.getPriorityCode(), "2.16.840.1.113883.5.7"));

        if (procedure.getMethodCode() != null) {
            obs.getMethodCodes().add(CcdUtils.createCE(procedure.getMethodCode()));
        }

        if (procedure.getBodySiteCodes() != null) {
            for (CcdCode bodySite : procedure.getBodySiteCodes()) {
                obs.getTargetSiteCodes().add(CcdUtils.createCD(bodySite, CodeSystem.SNOMED_CT.getOid()));
            }
        }

        if (!CollectionUtils.isEmpty(procedure.getPerformers())) {
            for (Organization organization : procedure.getPerformers()) {
                Performer2 performer2 = CDAFactory.eINSTANCE.createPerformer2();
                AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
                assignedEntity.getRepresentedOrganizations().add(SectionEntryFactory.buildOrganization(organization, true));
                performer2.setAssignedEntity(assignedEntity);
                obs.getPerformers().add(performer2);
            }
        }

        if (!CollectionUtils.isEmpty(procedure.getServiceDeliveryLocations())) {
            for (ServiceDeliveryLocation serviceDeliveryLocation : procedure.getServiceDeliveryLocations()) {
                Participant2 participant2 = CDAFactory.eINSTANCE.createParticipant2();
                participant2.setTypeCode(ParticipationType.LOC);
                participant2.setParticipantRole(buildServiceDeliveryLocation(serviceDeliveryLocation));
                obs.getParticipants().add(participant2);
            }
        }

        if (!CollectionUtils.isEmpty(procedure.getEncounterIds())) {
            for (String encounterId : procedure.getEncounterIds()) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
                entryRelationship.setInversionInd(true);
                Encounter encounter = CDAFactory.eINSTANCE.createEncounter();
                encounter.setClassCode(ActClass.ENC);
                encounter.setMoodCode(x_DocumentEncounterMood.EVN);
                encounter.getIds().add(CcdUtils.getId(encounterId));
                entryRelationship.setEncounter(encounter);
                obs.getEntryRelationships().add(entryRelationship);
            }
        }

        if (procedure.getInstructions() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
            entryRelationship.setInversionInd(true);
            entryRelationship.setAct(SectionEntryFactory.buildInstructions(procedure.getInstructions(), entriesReferredToSectionText));
            obs.getEntryRelationships().add(entryRelationship);
        }

        if (!CollectionUtils.isEmpty(procedure.getIndications())) {
            for (Indication indication : procedure.getIndications()) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.RSON);
                entryRelationship.setObservation(SectionEntryFactory.buildIndication(indication));
                obs.getEntryRelationships().add(entryRelationship);
            }
        }

        if (procedure.getMedication() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
            entryRelationship.setSubstanceAdministration(SectionEntryFactory.buildMedicationActivity(procedure.getMedication(), entriesReferredToSectionText));
            obs.getEntryRelationships().add(entryRelationship);
        }

        return obs;
    }

    public static ParticipantRole buildProductInstance(ProductInstance productInstance) {
        ParticipantRole participantRole = CDAFactory.eINSTANCE.createParticipantRole();
        participantRole.setClassCode(RoleClassRoot.MANU);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.37");
        participantRole.getTemplateIds().add(templateId);

        participantRole.getIds().add(CcdUtils.getId(productInstance.getId()));

        Device device = CDAFactory.eINSTANCE.createDevice();
        if (productInstance.getDeviceCode() != null) {
            device.setCode(CcdUtils.createCE(productInstance.getDeviceCode()));
        }
//        else {
//            device.setNullFlavor(NullFlavor.NI);
//        }
        participantRole.setPlayingDevice(device);

        Entity entity = CDAFactory.eINSTANCE.createEntity();
        entity.getIds().add(CcdUtils.getId(productInstance.getScopingEntityId()));
        participantRole.setScopingEntity(entity);

        return participantRole;
    }

    public static ParticipantRole buildServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation) {
        ParticipantRole participantRole = CDAFactory.eINSTANCE.createParticipantRole();
        participantRole.setClassCode(RoleClassRoot.SDLOC);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.32");
        participantRole.getTemplateIds().add(templateId);

        participantRole.setCode(CcdUtils.createCE(serviceDeliveryLocation.getCode(), HEALTHCARE_SERVICE_LOCATION.getOid()));

        if (!StringUtils.isEmpty(serviceDeliveryLocation.getName())) {
            PlayingEntity playingEntity = CDAFactory.eINSTANCE.createPlayingEntity();
            playingEntity.setClassCode(EntityClassRoot.PLC);
            PN name = DatatypesFactory.eINSTANCE.createPN();
            name.addText(serviceDeliveryLocation.getName());
            playingEntity.getNames().add(name);
            if (!StringUtils.isEmpty(serviceDeliveryLocation.getDescription())) {
                playingEntity.setDesc(CcdUtils.createEntryText(serviceDeliveryLocation.getDescription()));
            }
            participantRole.setPlayingEntity(playingEntity);
        }

        if (serviceDeliveryLocation.getAddresses() != null) {
            for (Address address : serviceDeliveryLocation.getAddresses()) {
                CcdUtils.addConvertedAddress(participantRole.getAddrs(), address);
            }
        } else {
            participantRole.getAddrs().add(CcdUtils.getNullAddress());
        }

        if (serviceDeliveryLocation.getTelecoms() != null) {
            for (Telecom telecom : serviceDeliveryLocation.getTelecoms()) {
                CcdUtils.addConvertedTelecom(participantRole.getTelecoms(), telecom);
            }
        } else {
            participantRole.getTelecoms().add(CcdUtils.getNullTelecom());
        }

        return participantRole;
    }

    public static AgeObservation buildAgeObservation(String unit, Integer value) {
        AgeObservation ageObservation = ConsolFactory.eINSTANCE.createAgeObservation();

        ageObservation.setClassCode(ActClassObservation.OBS);
        ageObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);

        ageObservation.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.31"));

        CD ageObservationCode = CcdUtils.createCD(SectionTypeCode.AGE_OBSERVATION);
        ageObservation.setCode(ageObservationCode);

        CS ageObservationStatusCode = DatatypesFactory.eINSTANCE.createCS();
        ageObservationStatusCode.setCode("completed");
        ageObservation.setStatusCode(ageObservationStatusCode);

        PQ ageValue = DatatypesFactory.eINSTANCE.createPQ();
        if (unit != null) {
            ageValue.setUnit(unit);
        } else {
            ageValue.setUnit("a");
        }
        ageValue.setValue(new BigDecimal(value));
        ageObservation.getValues().add(ageValue);

        return ageObservation;
    }

    /**
     * Template ID = 2.16.840.1.113883.10.20.22.4.50 (Non-MedicinalSupplyActivity)
     * This template records non-medicinal supplies provided, such as medical equipment
     */
    public static Supply buildNonMedicalActivity(MedicalEquipment medicalEquipment) {
        Supply supply = CDAFactory.eINSTANCE.createSupply();
        supply.setClassCode(ActClassSupply.SPLY);

        if (medicalEquipment.getMoodCode() != null) {
            supply.setMoodCode(x_DocumentSubstanceMood.valueOf(medicalEquipment.getMoodCode()));
        } else {
            supply.setMoodCode(x_DocumentSubstanceMood.EVN);
        }

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.50");
        supply.getTemplateIds().add(templateId);

        supply.getIds().add(CcdUtils.getId(medicalEquipment.getId()));

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        if (medicalEquipment.getStatusCode() != null) {
            statusCode.setCode(medicalEquipment.getStatusCode());
        } else {
            statusCode.setNullFlavor(NullFlavor.NI);
        }
        supply.setStatusCode(statusCode);

        // [CONF-15498] SHOULD contain zero or one [0..1] effectiveTime.
        // The effectiveTime, if present, SHOULD contain zero or one [0..1] high.
        if (medicalEquipment.getEffectiveTimeHigh() != null) {
            IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
            IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
            high.setValue(CcdUtils.formatSimpleDate(medicalEquipment.getEffectiveTimeHigh()));
            effectiveTime.setHigh(high);
            supply.getEffectiveTimes().add(effectiveTime);
        }

        if (medicalEquipment.getQuantity() != null) {
            PQ quantity = DatatypesFactory.eINSTANCE.createIVL_PQ();
            quantity.setValue(BigDecimal.valueOf(medicalEquipment.getQuantity()));
            supply.setQuantity(quantity);
        }

        if (medicalEquipment.getProductInstance() != null) {
            Participant2 participant2 = CDAFactory.eINSTANCE.createParticipant2();
            // typeCode="PRD" always for this template
            participant2.setTypeCode(ParticipationType.PRD);
            participant2.setParticipantRole(buildProductInstance(medicalEquipment.getProductInstance()));
            supply.getParticipants().add(participant2);
        }

        return supply;
    }

    private static CS createStatusCode(String statusCode) {
        CS statusCodeCcd = DatatypesFactory.eINSTANCE.createCS();
        if (!StringUtils.isEmpty(statusCode)) {
            statusCodeCcd.setCode(statusCode);
//            if (statusCode.getDisplayName()!=null)   {
//                statusCodeCcd.setDisplayName(statusCode.getDisplayName());
//            }
        } else {
            statusCodeCcd.setNullFlavor(NullFlavor.NI);
        }
        return statusCodeCcd;
    }

    public static void initReferenceRanges(Observation observation, List<String> referenceRanges) {
        if (referenceRanges != null) {
            for (String referenceRange : referenceRanges) {
                ReferenceRange ref = CDAFactory.eINSTANCE.createReferenceRange();
                ObservationRange observationRange = CDAFactory.eINSTANCE.createObservationRange();
                ED text = DatatypesFactory.eINSTANCE.createED();
                text.addText(referenceRange);
                observationRange.setText(text);
                ref.setObservationRange(observationRange);
                observation.getReferenceRanges().add(ref);
            }
        }
    }
}
