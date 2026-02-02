package com.scnsoft.eldermark.service.document.templates.cda.factory.entries;

import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.basic.Telecom;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.entity.document.ccd.codes.DawProductSelectionCode;
import com.scnsoft.eldermark.entity.document.ccd.codes.MedicationEldermarkPharmacyType;
import com.scnsoft.eldermark.entity.document.ccd.codes.PrescriptionOriginCode;
import com.scnsoft.eldermark.entity.document.ccd.codes.SectionTypeCode;
import com.scnsoft.eldermark.entity.document.facesheet.AllergyObservation;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdht.uml.cda.Author;
import org.eclipse.mdht.uml.cda.Device;
import org.eclipse.mdht.uml.cda.Encounter;
import org.eclipse.mdht.uml.cda.Procedure;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static com.scnsoft.eldermark.entity.document.ccd.CodeSystem.*;
import static org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship.MFST;
import static org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship.SUBJ;

public abstract class CdaSectionEntryFactory {

    private static final Logger logger = LoggerFactory.getLogger(CdaSectionEntryFactory.class);

    public static String ALLERGY_TYPE_DEFAULT_CODE = "418038007";

    public static final CdaSectionEntryFactory INSTANCE = new CdaSectionEntryFactory() {
        @Override
        protected Observation createReactionObservation() {
            throw new NotImplementedException();
        }

        @Override
        protected Observation createSeverityObservation() {
            throw new NotImplementedException();
        }

        @Override
        protected Observation createProblemObservation() {
            throw new NotImplementedException();
        }

        @Override
        protected Observation createProblemStatusObservation() {
            throw new NotImplementedException();
        }

        @Override
        protected Observation createAgeObservation() {
            throw new NotImplementedException();
        }

        @Override
        protected void addHealthStatusObservation(Observation problemObservationCcd, ProblemObservation problemObservation) {

        }

        @Override
        protected Organizer createResultOrganizer() {
            throw new NotImplementedException();
        }

        @Override
        protected Observation createAllergyObservation() {
            throw new NotImplementedException();
        }

        @Override
        protected Observation createAllergyStatusObservation() {
            throw new NotImplementedException();
        }
    };

    protected CdaSectionEntryFactory() {
    }

    protected abstract Observation createReactionObservation();

    protected Observation buildReactionObservation(ReactionObservation reactionObservation, Set<Class<?>> entriesReferredToSectionText) {
        var ccdReactionObservation = createReactionObservation();
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

        //todo should be notEmpty instead of null?
        if (reactionObservation.getReactionText() != null) {
            if (entriesReferredToSectionText.contains(ReactionObservation.class)) {
                ccdReactionObservation.setText(CcdUtils.createReferenceEntryText(ReactionObservation.class.getSimpleName() + reactionObservation.getId()));
            } else {
                ccdReactionObservation.setText(CcdUtils.createEntryText(reactionObservation.getReactionText()));
            }
        }

        CD code = CcdUtils.createCDWithDefaultDisplayName(reactionObservation.getReactionCode(),
                reactionObservation.getReactionText(), CodeSystem.SNOMED_CT.getOid());
        ccdReactionObservation.getValues().add(code);

        List<SeverityObservation> reactionSeverities = reactionObservation.getSeverityObservations();
        if (CollectionUtils.isNotEmpty(reactionSeverities)) {
            for (SeverityObservation severityObservation : reactionSeverities) {
                EntryRelationship entryRelationship1 = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship1.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
                entryRelationship1.setInversionInd(true);
                entryRelationship1.setObservation(buildSeverityObservation(severityObservation, entriesReferredToSectionText));
                ccdReactionObservation.getEntryRelationships().add(entryRelationship1);
            }
        }

        List<Medication> medications = reactionObservation.getMedications();
        if (CollectionUtils.isNotEmpty(medications)) {
            for (Medication medication : medications) {
                EntryRelationship entryRelationship1 = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship1.setTypeCode(x_ActRelationshipEntryRelationship.RSON);
                entryRelationship1.setInversionInd(true);
                entryRelationship1.setSubstanceAdministration(buildMedicationActivity(medication, entriesReferredToSectionText));
                ccdReactionObservation.getEntryRelationships().add(entryRelationship1);
            }
        }

        List<ProcedureActivity> procedureActivities = reactionObservation.getProcedureActivities();
        if (CollectionUtils.isNotEmpty(medications)) {
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

    protected abstract Observation createSeverityObservation();

    protected Observation buildSeverityObservation(SeverityObservation severityObservation, Set<Class<?>> entriesReferredToSectionText) {
        var ccdSeverity = createSeverityObservation();
        ccdSeverity.setClassCode(ActClassObservation.OBS);
        ccdSeverity.setMoodCode(x_ActMoodDocumentObservation.EVN);
        II severityTemplateId = DatatypesFactory.eINSTANCE.createII();
        severityTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.8");
        ccdSeverity.getTemplateIds().add(severityTemplateId);
        CS severityStatusCode = DatatypesFactory.eINSTANCE.createCS();
        severityStatusCode.setCode("completed");
        ccdSeverity.setStatusCode(severityStatusCode);
        // Indicates a subjective evaluation of the criticality associated with another observation.
        CE severityCode = CcdUtils.createCE("SEV", null, HL7_ACT_CODE);
        ccdSeverity.setCode(severityCode);

        //todo should be notEmpty instead of null?
        if (severityObservation.getSeverityText() != null) {
            if (entriesReferredToSectionText.contains(SeverityObservation.class)) {
                ccdSeverity.setText(CcdUtils.createReferenceEntryText(SeverityObservation.class.getSimpleName() + severityObservation.getId()));
            } else {
                ccdSeverity.setText(CcdUtils.createEntryText(severityObservation.getSeverityText()));
            }
        }

        var severityValue = severityObservation.getSeverityCode();

        CD code = CcdUtils.createCDFromValueSetOrTranslation(severityValue, ValueSetEnum.PROBLEM_SEVERITY, false);
        ccdSeverity.getValues().add(code);

        return ccdSeverity;
    }

    protected abstract Observation createProblemObservation();

    protected abstract Observation createProblemStatusObservation();

    protected Observation buildNullProblemObservation() {
        var problemObservationCcd = createProblemObservation();

        problemObservationCcd.setClassCode(ActClassObservation.OBS);
        problemObservationCcd.setMoodCode(x_ActMoodDocumentObservation.EVN);

        II alertObservationTemplateId = DatatypesFactory.eINSTANCE.createII();
        alertObservationTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.4");
        problemObservationCcd.getTemplateIds().add(alertObservationTemplateId);

        problemObservationCcd.getIds().add(CcdUtils.getNullId());

        CD problemType = DatatypesFactory.eINSTANCE.createCD();
        problemType.setNullFlavor(NullFlavor.NI);
        /* todo [ccd] review below
                problemType.setCode("409586006");
        problemType.setCodeSystem("2.16.840.1.113883.6.96");
        problemType.setDisplayName("Complaint");
         */
        problemObservationCcd.setCode(problemType);

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        statusCode.setCode("completed");
        problemObservationCcd.setStatusCode(statusCode);

        CD problemCode = DatatypesFactory.eINSTANCE.createCD();
        problemCode.setNullFlavor(NullFlavor.UNK);
        problemObservationCcd.getValues().add(problemCode);

        return problemObservationCcd;
    }

    protected Observation buildProblemObservation(ProblemObservation problemObservation, Set<Class<?>> entriesReferredToSectionText) {
        var problemObservationCcd = createProblemObservation();

        problemObservationCcd.setClassCode(ActClassObservation.OBS);
        problemObservationCcd.setMoodCode(x_ActMoodDocumentObservation.EVN);

        II alertObservationTemplateId = DatatypesFactory.eINSTANCE.createII();
        alertObservationTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.4");
        problemObservationCcd.getTemplateIds().add(alertObservationTemplateId);

        problemObservationCcd.getIds().add(CcdUtils.getId(problemObservation.getId()));
        CcdUtils.addConsanaId(problemObservationCcd.getIds(), problemObservation.getConsanaId());

        if (problemObservation.getNegationInd() != null) {
            problemObservationCcd.setNegationInd(problemObservation.getNegationInd());
        }

        problemObservationCcd.setCode(CcdUtils.createCD(problemObservation.getProblemType(), CodeSystem.SNOMED_CT.getOid()));

        //todo [ccd] review below
//        if(problemObservation.getProblemType().getCode().equals("ASSERTION")) {
//            problemObservationCcd.setCode(CcdUtils.createCD(SectionEntryFactory.buildNullProblemType(), CodeSystem.SNOMED_CT.getOid()));
//        }
//        else {
//            problemObservationCcd.setCode(CcdUtils.createCD(problemObservation.getProblemType(), CodeSystem.SNOMED_CT.getOid()));
//        }


        //todo should be notEmpty instead of null?
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

        if (problemObservation.getAuthor() != null) {
            problemObservationCcd.getAuthors().add(buildAuthor(problemObservation.getAuthor()));
        }

        problemObservationCcd.setEffectiveTime(CcdUtils.convertEffectiveTime(problemObservation.getProblemDateTimeLow(),
                problemObservation.getProblemDateTimeHigh(), true, false));

        // Problem Observation value
        CD ccdCode = CcdUtils.createCDWithDefaultDisplayName(problemObservation.getProblemCode(),
                problemObservation.getProblemName(), CodeSystem.SNOMED_CT.getOid());

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
            var ageObservation = buildAgeObservation(problemObservation.getAgeObservationUnit(), problemObservation.getAgeObservationValue());
            problemObservationCcd.addObservation(ageObservation);
            ((EntryRelationship) ageObservation.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
            ((EntryRelationship) ageObservation.eContainer()).setInversionInd(Boolean.TRUE);
        }

        // Problem Status
        if (problemObservation.getProblemStatusCode() != null || !StringUtils.isEmpty(problemObservation.getProblemStatusText())) {
            var problemStatusObservation = createProblemStatusObservation();
            problemObservationCcd.addObservation(problemStatusObservation);
            ((EntryRelationship) problemStatusObservation.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.REFR);

            problemStatusObservation.setClassCode(ActClassObservation.OBS);
            problemStatusObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);

            II problemStatusObservationTemplateId = DatatypesFactory.eINSTANCE.createII();
            problemStatusObservationTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.6");
            problemStatusObservation.getTemplateIds().add(problemStatusObservationTemplateId);

            CD code = CcdUtils.createCD(SectionTypeCode.STATUS_OBSERVATION);
            problemStatusObservation.setCode(code);

            //todo should be notEmpty instead of null?
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

            CD problemStatusObservationValue = CcdUtils.createCDFromValueSetOrTranslationDefaultDisplayName(
                    problemObservation.getProblemStatusCode(), ValueSetEnum.PROBLEM_STATUS,
                    problemObservation.getProblemStatusText());
            problemStatusObservation.getValues().add(problemStatusObservationValue);
        }

        // Health Status Observation
        if (problemObservation.getHealthStatusCode() != null || !StringUtils.isEmpty(problemObservation.getHealthStatusObservationText())) {
            addHealthStatusObservation(problemObservationCcd, problemObservation);
        }

        return problemObservationCcd;
    }

    protected abstract Observation createAgeObservation();

    protected Observation buildAgeObservation(String unit, Integer value) {
        var ageObservation = createAgeObservation();

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

    protected abstract void addHealthStatusObservation(Observation problemObservationCcd, ProblemObservation problemObservation);

    public SubstanceAdministration buildMedicationActivity(Medication medication, Set<Class<?>> entriesReferredToSectionText) {
        SubstanceAdministration substanceAdministration = CDAFactory.eINSTANCE.createSubstanceAdministration();
        substanceAdministration.setClassCode(ActClass.SBADM);

        //todo should be notEmpty instead of null?
        if (medication.getMoodCode() != null) {
            substanceAdministration.setMoodCode(x_DocumentSubstanceMood.valueOf(medication.getMoodCode()));
        } else {
            substanceAdministration.setMoodCode(x_DocumentSubstanceMood.INT);
        }

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.16");
        substanceAdministration.getTemplateIds().add(templateId);

        substanceAdministration.getIds().add(CcdUtils.getId(medication.getId()));
        CcdUtils.addConsanaId(substanceAdministration.getIds(), medication.getConsanaId());

        CD deliveryMethodCode = CcdUtils.createCD(medication.getDeliveryMethod());
        substanceAdministration.setCode(deliveryMethodCode);

        //todo should be notEmpty instead of null?
        if (medication.getFreeTextSig() != null) {
            if (entriesReferredToSectionText.contains(Medication.class)) {
                substanceAdministration.setText(CcdUtils.createReferenceEntryText(Medication.class.getSimpleName() + medication.getId()));
            } else {
                substanceAdministration.setText(CcdUtils.createEntryText(medication.getFreeTextSig()));
            }
        }

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        //todo should be notEmpty instead of null?
        if (medication.getStatusCode() != null) {
            statusCode.setCode(medication.getStatusCode());
        } else {
            statusCode.setNullFlavor(NullFlavor.NI);
        }
        substanceAdministration.setStatusCode(statusCode);

        IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
        Date timeLow = medication.getMedicationStarted();
        Date timeHigh = medication.getMedicationStopped();
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
        substanceAdministration.getEffectiveTimes().add(effectiveTime);

        //if getAdministrationTimingValue is not empty non-TS string - add is as free text (one more html column)?
        if (CcdUtils.isTS(medication.getAdministrationTimingValue())) {
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

        if (medication.getRoute() != null) {
            CE codeCE = CcdUtils.createCEOrTranslation(medication.getRoute(), NCI_THESAURUS.getOid(), true);
            substanceAdministration.setRouteCode(codeCE);
        }

        if (medication.getSite() != null) {
            var codeCE = CcdUtils.createCE(medication.getSite(), CodeSystem.SNOMED_CT.getOid());
            substanceAdministration.getApproachSiteCodes().add(codeCE);
        }

        if (medication.getDoseQuantity() != null) {
            IVL_PQ pq = DatatypesFactory.eINSTANCE.createIVL_PQ();
            pq.setValue(BigDecimal.valueOf(medication.getDoseQuantity()));
            if (medication.getDoseUnits() != null) {
                pq.setUnit(medication.getDoseUnits());
            }
            substanceAdministration.setDoseQuantity(pq);
        }

        if (medication.getRateQuantity() != null && StringUtils.isNotEmpty(medication.getRateUnits())) {
            IVL_PQ pq = DatatypesFactory.eINSTANCE.createIVL_PQ();
            pq.setValue(BigDecimal.valueOf(medication.getRateQuantity()));
            pq.setUnit(medication.getRateUnits());
            substanceAdministration.setRateQuantity(pq);
        }

        if (medication.getAdministrationUnitCode() != null) {
            var codeCE = CcdUtils.createCEOrTranslation(medication.getAdministrationUnitCode(), NCI_THESAURUS.getOid(), false);
            substanceAdministration.setAdministrationUnitCode(codeCE);
        }

        Consumable consumable = CDAFactory.eINSTANCE.createConsumable();
        if (medication.getMedicationInformation() != null) {
            consumable.setManufacturedProduct(buildMedicationInformation(medication.getMedicationInformation(), entriesReferredToSectionText));
            CcdUtils.addConsanaId(consumable.getManufacturedProduct().getIds(), medication.getConsanaId());
        } else {
            consumable.setManufacturedProduct(buildNullMedicationInformation());
        }
        substanceAdministration.setConsumable(consumable);

        if (CollectionUtils.isNotEmpty(medication.getDrugVehicles())) {
            for (DrugVehicle drugVehicle : medication.getDrugVehicles()) {
                Participant2 participant2 = CDAFactory.eINSTANCE.createParticipant2();
                participant2.setTypeCode(ParticipationType.CSM);
                participant2.setParticipantRole(buildDrugVehicle(drugVehicle));
                substanceAdministration.getParticipants().add(participant2);
            }
        }

        if (CollectionUtils.isNotEmpty(medication.getIndications())) {
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

        if (CollectionUtils.isNotEmpty(medication.getMedicationDispenses())) {
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
            entryRelationship.setObservation(buildReactionObservation(medication.getReactionObservation(), entriesReferredToSectionText));
            substanceAdministration.getEntryRelationships().add(entryRelationship);
        }

        if (CollectionUtils.isNotEmpty(medication.getPreconditions())) {
            for (MedicationPrecondition precondition : medication.getPreconditions()) {
                substanceAdministration.getPreconditions().add(buildMedicationPrecondition(precondition));
            }
        }

        if (medication.getPerformer() != null) {
            substanceAdministration.getPerformers().add(buildPerformer2(medication.getPerformer()));
        }

        if (medication.getPharmacy() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.REFR);
            entryRelationship.setOrganizer(buildMedicationPharmacyOrganizer(medication.getPharmacy(),
                    MedicationEldermarkPharmacyType.PHARMACY));
            substanceAdministration.getEntryRelationships().add(entryRelationship);
        }

        if (medication.getDispensingPharmacy() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.REFR);
            entryRelationship.setOrganizer(buildMedicationPharmacyOrganizer(medication.getDispensingPharmacy(),
                    MedicationEldermarkPharmacyType.DISPENSING_PHARMACY));
            substanceAdministration.getEntryRelationships().add(entryRelationship);
        }

        return substanceAdministration;
    }

    //our cda customization
    public Organizer buildMedicationPharmacyOrganizer(Community community, MedicationEldermarkPharmacyType type) {
        var organizer = CDAFactory.eINSTANCE.createOrganizer();
        organizer.setClassCode(x_ActClassDocumentEntryOrganizer.BATTERY);
        organizer.setMoodCode(ActMood.EVN);
        organizer.setStatusCode(CcdUtils.createCS("completed"));
        organizer.getTemplateIds().add(
                DatatypesFactory.eINSTANCE.createII(CdaConstants.MEDICATION_ELDERMARK_PHARMACY_TEMPLATE_ID));

        var performer = CDAFactory.eINSTANCE.createPerformer2();
        organizer.getPerformers().add(performer);

        var assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
        performer.setAssignedEntity(assignedEntity);

        assignedEntity.getIds().add(CcdUtils.getId(community.getId()));
        CcdUtils.addEldermarkLegacyId(assignedEntity.getIds(), community.getLegacyId());

        assignedEntity.setCode(CcdUtils.createCE(type));

        var organization = buildOrganization(community, false);
        CcdUtils.addConvertedAddresses(community.getAddresses(), organization.getAddrs(), false);
        CcdUtils.addConvertedTelecoms(CareCoordinationUtils.wrapIfNonNull(community.getTelecom()), organization.getTelecoms(), false);
        assignedEntity.getRepresentedOrganizations().add(organization);

        return organizer;
    }

    public SubstanceAdministration buildNullMedicationActivity() {
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

        IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
        IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();

        low.setNullFlavor(NullFlavor.NI);
        high.setNullFlavor(NullFlavor.NI);

        IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
        effectiveTime.setLow(low);
        effectiveTime.setHigh(high);
        substanceAdministration.getEffectiveTimes().add(effectiveTime);

        Consumable consumable = CDAFactory.eINSTANCE.createConsumable();
        consumable.setManufacturedProduct(buildNullMedicationInformation());
        substanceAdministration.setConsumable(consumable);

        return substanceAdministration;
    }

    public Precondition buildMedicationPrecondition(MedicationPrecondition precondition) {
        Precondition p = CDAFactory.eINSTANCE.createPrecondition();
        p.setTypeCode(ActRelationshipType.PRCN);

        Criterion criterion = CDAFactory.eINSTANCE.createCriterion();
        criterion.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.25"));

        CD code = CcdUtils.createCD(precondition.getCode());
        criterion.setCode(code);

        //todo should be notEmpty instead of null?
        if (StringUtils.isNotEmpty(precondition.getText())) {
            ED text = DatatypesFactory.eINSTANCE.createED();
            text.addText(precondition.getText());
            criterion.setText(text);
        }

        CD value = CcdUtils.createCD(precondition.getValue());
        criterion.setValue(value);

        p.setCriterion(criterion);

        return p;
    }

    public Supply buildMedicationDispense(MedicationDispense medicationDispense, Set<Class<?>> entriesReferredToSectionText) {
        Supply supply = CDAFactory.eINSTANCE.createSupply();
        supply.setClassCode(ActClassSupply.SPLY);
        supply.setMoodCode(x_DocumentSubstanceMood.EVN);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.18");
        supply.getTemplateIds().add(templateId);

        supply.getIds().add(CcdUtils.getId(medicationDispense.getId()));
        CcdUtils.addMedicationPrescriptionNumber(supply.getIds(), medicationDispense.getPrescriptionNumber());

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        //todo should be notEmpty instead of null?
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
            quantity.setValue(medicationDispense.getQuantity());
            if (StringUtils.isNotEmpty(medicationDispense.getQuantityQualifierCode())) {
                quantity.setUnit(medicationDispense.getQuantityQualifierCode());
            }
            supply.setQuantity(quantity);
        }

        supply.setProduct(buildOneOfProducts(medicationDispense.getMedicationInformation(),
                medicationDispense.getImmunizationMedicationInformation(),
                entriesReferredToSectionText));

        if (medicationDispense.getProvider() != null) {
            Performer2 performer2 = CDAFactory.eINSTANCE.createPerformer2();
            AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
            assignedEntity.getIds().add(CcdUtils.getNullId());
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

    public ManufacturedProduct buildMedicationInformation(MedicationInformation medicationInformation, Set<Class<?>> entriesReferredToSectionText) {
        ManufacturedProduct manufacturedProduct = CDAFactory.eINSTANCE.createManufacturedProduct();
        manufacturedProduct.setClassCode(RoleClassManufacturedProduct.MANU);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.23");
        manufacturedProduct.getTemplateIds().add(templateId);

        manufacturedProduct.getIds().add(CcdUtils.getId(medicationInformation.getId()));

        Material material = CDAFactory.eINSTANCE.createMaterial();

        CE code = CcdUtils.createCEWithDefaultDisplayName(medicationInformation.getProductNameCode(),
                medicationInformation.getProductNameText(), "2.16.840.1.113883.6.88");
        CollectionUtils.emptyIfNull(medicationInformation.getTranslationProductCodes()).forEach(translationCode ->
                code.getTranslations().add(CcdUtils.createCE(translationCode))
        );
        material.setCode(code);

        //todo should be notEmpty instead of null?
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

    public ManufacturedProduct buildNullMedicationInformation() {
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

    public Act buildProcedureAct(ProcedureActivity procedure, Set<Class<?>> entriesReferredToSectionText) {
        Act act = CDAFactory.eINSTANCE.createAct();

        act.setClassCode(x_ActClassDocumentEntryAct.ACT);
        //todo should be notEmpty instead of null?
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

        addPerformers(act.getPerformers(), procedure.getPerformers());

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
            entryRelationship.setAct(buildInstructions(procedure.getInstructions(), entriesReferredToSectionText));
            act.getEntryRelationships().add(entryRelationship);
        }

        if (!CollectionUtils.isEmpty(procedure.getIndications())) {
            for (Indication indication : procedure.getIndications()) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.RSON);
                entryRelationship.setObservation(buildIndication(indication));
                act.getEntryRelationships().add(entryRelationship);
            }
        }

        if (procedure.getMedication() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
            entryRelationship.setSubstanceAdministration(buildMedicationActivity(procedure.getMedication(), entriesReferredToSectionText));
            act.getEntryRelationships().add(entryRelationship);
        }

        return act;
    }

    public Procedure buildProcedureActivity(ProcedureActivity procedure, Set<Class<?>> entriesReferredToSectionText) {
        Procedure p = CDAFactory.eINSTANCE.createProcedure();

        p.setClassCode(ActClass.PROC);
        //todo should be notEmpty instead of null?
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

        if (CollectionUtils.isNotEmpty(procedure.getBodySiteCodes())) {
            for (CcdCode bodySite : procedure.getBodySiteCodes()) {
                p.getTargetSiteCodes().add(CcdUtils.createCD(bodySite, CodeSystem.SNOMED_CT.getOid()));
            }
        }

        if (CollectionUtils.isNotEmpty(procedure.getSpecimenIds())) {
            for (String specimenId : procedure.getSpecimenIds()) {
                Specimen specimen = CDAFactory.eINSTANCE.createSpecimen();
                SpecimenRole specimenRole = CDAFactory.eINSTANCE.createSpecimenRole();
                specimenRole.getIds().add(CcdUtils.getId(specimenId));
                specimen.setSpecimenRole(specimenRole);
                p.getSpecimens().add(specimen);
            }
        }

        addPerformers(p.getPerformers(), procedure.getPerformers());

        if (CollectionUtils.isNotEmpty(procedure.getProductInstances())) {
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
            entryRelationship.setAct(buildInstructions(procedure.getInstructions(), entriesReferredToSectionText));
            p.getEntryRelationships().add(entryRelationship);
        }

        if (!CollectionUtils.isEmpty(procedure.getIndications())) {
            for (Indication indication : procedure.getIndications()) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.RSON);
                entryRelationship.setObservation(buildIndication(indication));
                p.getEntryRelationships().add(entryRelationship);
            }
        }

        if (procedure.getMedication() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
            entryRelationship.setSubstanceAdministration(buildMedicationActivity(procedure.getMedication(), entriesReferredToSectionText));
            p.getEntryRelationships().add(entryRelationship);
        }

        return p;
    }

    public Procedure buildNullProcedureActivity() {
        Procedure p = CDAFactory.eINSTANCE.createProcedure();

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

    public ParticipantRole buildServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation) {
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

        CcdUtils.addConvertedAddresses(serviceDeliveryLocation.getAddresses(), participantRole.getAddrs(), true);
        CcdUtils.addConvertedTelecoms(serviceDeliveryLocation.getTelecoms(), participantRole.getTelecoms(), true);

        return participantRole;
    }

    /**
     * Template ID = 2.16.840.1.113883.10.20.22.4.50 (Non-MedicinalSupplyActivity)
     * This template records non-medicinal supplies provided, such as medical
     * equipment
     */
    public Supply buildNonMedicalActivity(MedicalEquipment medicalEquipment) {
        Supply supply = CDAFactory.eINSTANCE.createSupply();
        supply.setClassCode(ActClassSupply.SPLY);

        //todo should be notEmpty instead of null?
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
        //todo should be notEmpty instead of null?
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

    public Observation buildProcedureObservation(ProcedureActivity procedure, Set<Class<?>> entriesReferredToSectionText) {
        Observation obs = CDAFactory.eINSTANCE.createObservation();

        obs.setClassCode(ActClassObservation.OBS);
        //todo should be notEmpty instead of null?
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

        if (CollectionUtils.isNotEmpty(procedure.getBodySiteCodes())) {
            for (CcdCode bodySite : procedure.getBodySiteCodes()) {
                obs.getTargetSiteCodes().add(CcdUtils.createCD(bodySite, CodeSystem.SNOMED_CT.getOid()));
            }
        }

        //unify below code with the same code from buildProcedureAct?
        addPerformers(obs.getPerformers(), procedure.getPerformers());

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
            entryRelationship.setAct(buildInstructions(procedure.getInstructions(), entriesReferredToSectionText));
            obs.getEntryRelationships().add(entryRelationship);
        }

        if (!CollectionUtils.isEmpty(procedure.getIndications())) {
            for (Indication indication : procedure.getIndications()) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.RSON);
                entryRelationship.setObservation(buildIndication(indication));
                obs.getEntryRelationships().add(entryRelationship);
            }
        }

        if (procedure.getMedication() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
            entryRelationship.setSubstanceAdministration(buildMedicationActivity(procedure.getMedication(), entriesReferredToSectionText));
            obs.getEntryRelationships().add(entryRelationship);
        }

        return obs;
    }

    public Act buildInstructions(Instructions instructions, Set<Class<?>> entriesReferredToSectionText) {
        Act act = CDAFactory.eINSTANCE.createAct();
        act.setClassCode(x_ActClassDocumentEntryAct.ACT);
        act.setMoodCode(x_DocumentActMood.INT);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.20");
        act.getTemplateIds().add(templateId);

        CD code = CcdUtils.createCD(instructions.getCode(), CodeSystem.SNOMED_CT.getOid());
        act.setCode(code);

        //todo should be notEmpty instead of null?
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

    public Observation buildIndication(Indication indication) {
        Observation observation = CDAFactory.eINSTANCE.createObservation();
        observation.setClassCode(ActClassObservation.OBS);
        observation.setMoodCode(x_ActMoodDocumentObservation.EVN);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.19");
        observation.getTemplateIds().add(templateId);

        observation.getIds().add(CcdUtils.getId(indication.getId()));

        CD code = CcdUtils.createCD(indication.getCode(), CodeSystem.SNOMED_CT.getOid());

        /* todo [ccd] review below
         CD code = DatatypesFactory.eINSTANCE.createCD();
        code.setCode("404684003");
        code.setCodeSystem("2.16.840.1.113883.6.96");
        code.setDisplayName("Finding");
        code.setCodeSystemName("SNOMED CT");
         */

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

    public ParticipantRole buildProductInstance(ProductInstance productInstance) {
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

    public ParticipantRole buildDrugVehicle(DrugVehicle drugVehicle) {
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
        //todo should be notEmpty instead of null?
        if (drugVehicle.getName() != null) {
            name.addText(drugVehicle.getName());
        } else {
            name.setNullFlavor(NullFlavor.NI);
        }
        playingEntity.getNames().add(name);

        participantRole.setPlayingEntity(playingEntity);

        return participantRole;
    }

    private Author buildMedicationPrescriber(MedicationSupplyOrder supplyOrder) {
        var author = supplyOrder.getAuthor();
        var prof = supplyOrder.getMedicalProfessional();

        return buildAuthor(author, prof);
    }

    public Author buildAuthor(com.scnsoft.eldermark.entity.document.ccd.Author author) {
        return buildAuthor(author, null);
    }

    private Author buildAuthor(com.scnsoft.eldermark.entity.document.ccd.Author author, MedicalProfessional professional) {
        Author ccdAuthor = CDAFactory.eINSTANCE.createAuthor();

        Date time = author.getTime();
        TS effectiveTime = DatatypesFactory.eINSTANCE.createTS();
        if (time != null) {
            effectiveTime.setValue(CcdUtils.formatSimpleDate(time));
        } else {
            effectiveTime.setNullFlavor(NullFlavor.NI);
        }
        ccdAuthor.setTime(effectiveTime);

        Community ccdCommunity = author.getCommunity();
        Person ccdPerson = author.getPerson();
        List<? extends Address> addresses = null;
        List<? extends Telecom> telecoms = null;

        boolean noData = true;
        AssignedAuthor assignedAuthor = CDAFactory.eINSTANCE.createAssignedAuthor();
        org.eclipse.mdht.uml.cda.Organization representedOrganization = null;

        if (ccdPerson != null) {
            noData = false;

            if (professional != null && StringUtils.isNotEmpty(professional.getNpi())) {
                assignedAuthor.getIds().add(CcdUtils.getNpiId(professional.getNpi()));
            }
            assignedAuthor.getIds().add(CcdUtils.getId(ccdPerson.getId()));

            CE code = CcdUtils.createCE(ccdPerson.getCode(), CodeSystem.NUCC_PROVIDER_CODES.getOid());
            if (professional == null) {
                if (ccdPerson.getCode() != null) {
                    assignedAuthor.setCode(code);
                }
            } else {
                if (StringUtils.isNotEmpty(professional.getSpeciality())) {
                    var translation = DatatypesFactory.eINSTANCE.createCD();
                    translation.setDisplayName(professional.getSpeciality());
                    translation.setCodeSystem(CodeSystem.MEDICATION_PRESCRIBER_ELDERMARK_SPECIALITY.getOid());
                    translation.setCodeSystemName(CodeSystem.MEDICATION_PRESCRIBER_ELDERMARK_SPECIALITY.getDisplayName());
                    code.getTranslations().add(translation);
                }
                assignedAuthor.setCode(code);
            }

            if (professional != null &&
                    StringUtils.isNotEmpty(professional.getOrganizationName()) &&
                    StringUtils.isNotEmpty(professional.getExtPharmacyId()) &&
                    professional.getCommunity() != null
            ) {
                var organization = buildOrganization(professional.getOrganizationName());
                if (StringUtils.isNotEmpty(professional.getExtPharmacyId())) {
                    organization.getIds().add(CcdUtils.getExtPharmacyId(professional.getExtPharmacyId()));
                }
                var community = buildOrganization(professional.getCommunity(), true);

                var partOfOrganization = CDAFactory.eINSTANCE.createOrganizationPartOf();
                partOfOrganization.setWholeOrganization(organization);
                community.setAsOrganizationPartOf(partOfOrganization);

                representedOrganization = community;
            }

            var person = buildPerson(ccdPerson);
            assignedAuthor.setAssignedPerson(person);

            addresses = ccdPerson.getAddresses();
            telecoms = ccdPerson.getTelecoms();
        } else if (ccdCommunity != null) {
            noData = false;

            II id = DatatypesFactory.eINSTANCE.createII();
            id.setNullFlavor(NullFlavor.NA);
            assignedAuthor.getIds().add(id);

            representedOrganization = buildOrganization(ccdCommunity, false);

            addresses = ccdCommunity.getAddresses();
            telecoms = CareCoordinationUtils.wrapIfNonNull(ccdCommunity.getTelecom());
        }

        if (representedOrganization != null) {
            assignedAuthor.setRepresentedOrganization(representedOrganization);
        }

        if (noData) {
            assignedAuthor.setNullFlavor(NullFlavor.NI);
        } else {
            CcdUtils.addConvertedAddresses(addresses, assignedAuthor.getAddrs(), true);
            CcdUtils.addConvertedTelecoms(telecoms, assignedAuthor.getTelecoms(), true);
        }
        ccdAuthor.setAssignedAuthor(assignedAuthor);

        return ccdAuthor;
    }

    public org.eclipse.mdht.uml.cda.Organization buildOrganization(Community community, boolean full) {
        if (community == null) {
            return buildOrganization(null);
        }

        org.eclipse.mdht.uml.cda.Organization ccdOrganization = buildOrganization(community.getName());

        if (full) {
            ccdOrganization.getIds().add(CcdUtils.getId(community.getId()));
            if (StringUtils.isNotEmpty(community.getProviderNpi())) {
                ccdOrganization.getIds().add(CcdUtils.getNpiId(community.getProviderNpi()));
            }

            CcdUtils.addHpClaimBillingProviderRefId(
                    ccdOrganization.getIds(),
                    community.getHealthPartnersBillingProviderRef()
            );


            CcdUtils.addConvertedAddresses(community.getAddresses(), ccdOrganization.getAddrs(), true);
            CcdUtils.addConvertedTelecoms(CareCoordinationUtils.wrapIfNonNull(community.getTelecom()), ccdOrganization.getTelecoms(), true);
        }

        return ccdOrganization;
    }

    private org.eclipse.mdht.uml.cda.Organization buildOrganization(String name) {
        org.eclipse.mdht.uml.cda.Organization ccdOrganization = CDAFactory.eINSTANCE.createOrganization();
        ON on = DatatypesFactory.eINSTANCE.createON();
        //todo should be notEmpty instead of null?
        if (name != null) {
            on.addText(name);
        } else {
            on.setNullFlavor(NullFlavor.NI);
        }
        ccdOrganization.getNames().add(on);

        return ccdOrganization;
    }

    public Supply buildMedicationSupplyOrder(MedicationSupplyOrder medicationSupplyOrder, Set<Class<?>> entriesReferredToSectionText) {
        Supply supply = CDAFactory.eINSTANCE.createSupply();
        supply.setClassCode(ActClassSupply.SPLY);
        supply.setMoodCode(x_DocumentSubstanceMood.INT);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.17");
        supply.getTemplateIds().add(templateId);

        supply.getIds().add(CcdUtils.getId(medicationSupplyOrder.getId()));

        CcdUtils.addMedicationPrescriptionNumber(supply.getIds(), medicationSupplyOrder.getPrescriptionNumber());

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        //todo should be notEmpty instead of null?
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

        supply.setProduct(buildOneOfProducts(medicationSupplyOrder.getMedicationInformation(),
                medicationSupplyOrder.getImmunizationMedicationInformation(),
                entriesReferredToSectionText));

        if (medicationSupplyOrder.getAuthor() != null) {
            supply.getAuthors().add(buildMedicationPrescriber(medicationSupplyOrder));
        }

        if (medicationSupplyOrder.getInstructions() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
            entryRelationship.setInversionInd(true);
            entryRelationship.setAct(buildInstructions(medicationSupplyOrder.getInstructions(), entriesReferredToSectionText));
            supply.getEntryRelationships().add(entryRelationship);
        }

        if (StringUtils.isNotEmpty(medicationSupplyOrder.getDAWProductSelectionCode())) {
            var code = medicationSupplyOrder.getDAWProductSelectionCode();
            var displayName = DawProductSelectionCode.fromCode(code)
                    .map(ConceptDescriptor::getDisplayName)
                    .orElse(null);

            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
            entryRelationship.setOrganizer(
                    buildWrapperOrganizerForCode(
                            CdaConstants.MEDICATION_DAW_CODE_WRAPPER_TEMPLATE_ID,
                            code,
                            displayName,
                            DAW_PRODUCT_SELECTION
                    )
            );
            supply.getEntryRelationships().add(entryRelationship);
        }

        if (StringUtils.isNotEmpty(medicationSupplyOrder.getPrescriptionOriginCode())) {
            var code = medicationSupplyOrder.getPrescriptionOriginCode();
            var displayName = PrescriptionOriginCode.fromCode(code)
                    .map(ConceptDescriptor::getDisplayName)
                    .orElse(null);

            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
            entryRelationship.setOrganizer(
                    buildWrapperOrganizerForCode(
                            CdaConstants.MEDICATION_RX_ORIGIN_CODE_WRAPPER_TEMPLATE_ID,
                            code,
                            displayName,
                            PRESCRIPTION_ORIGIN
                    )
            );
            supply.getEntryRelationships().add(entryRelationship);
        }

        return supply;
    }

    private Organizer buildWrapperOrganizerForCode(String templateId,
                                                   String code,
                                                   String displayName,
                                                   CodeSystem codeSystem) {
        var organizer = CDAFactory.eINSTANCE.createOrganizer();
        organizer.setClassCode(x_ActClassDocumentEntryOrganizer.BATTERY);
        organizer.setMoodCode(ActMood.EVN);
        organizer.setStatusCode(CcdUtils.createCS("completed"));

        organizer.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(templateId));
        organizer.setCode(CcdUtils.createCD(code, displayName, codeSystem));

        return organizer;
    }

    private Product buildOneOfProducts(MedicationInformation medicationInformation,
                                       ImmunizationMedicationInformation immunizationMedicationInformation,
                                       Set<Class<?>> entriesReferredToSectionText) {
        Product product = CDAFactory.eINSTANCE.createProduct();
        ManufacturedProduct manufacturedProduct;

        if (medicationInformation != null && immunizationMedicationInformation != null) {
            //if both are not null - pick one that matches context better, i.e immunization for immunization section and medication for medication section
            if (entriesReferredToSectionText.contains(ImmunizationMedicationInformation.class)) {
                manufacturedProduct = buildImmunizationMedicationInformation(immunizationMedicationInformation, entriesReferredToSectionText);
            } else {
                //medication section by default
                manufacturedProduct = buildMedicationInformation(medicationInformation, entriesReferredToSectionText);
            }

        } else if (medicationInformation != null) {
            manufacturedProduct = buildMedicationInformation(medicationInformation, entriesReferredToSectionText);

        } else if (immunizationMedicationInformation != null) {
            manufacturedProduct = buildImmunizationMedicationInformation(immunizationMedicationInformation, entriesReferredToSectionText);

        } else {
            manufacturedProduct = buildNullMedicationInformation();
        }

        product.setManufacturedProduct(manufacturedProduct);
        return product;
    }

    public ManufacturedProduct buildImmunizationMedicationInformation(ImmunizationMedicationInformation immunizationMedicationInformation, Set<Class<?>> entriesReferredToSectionText) {
        ManufacturedProduct manufacturedProduct = CDAFactory.eINSTANCE.createManufacturedProduct();
        manufacturedProduct.setClassCode(RoleClassManufacturedProduct.MANU);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.54");
        manufacturedProduct.getTemplateIds().add(templateId);

        manufacturedProduct.getIds().add(CcdUtils.getId(immunizationMedicationInformation.getId()));

        Material material = CDAFactory.eINSTANCE.createMaterial();

        CE code = CcdUtils.createCEOrTranslation(immunizationMedicationInformation.getCode(), CodeSystem.CVX.getOid(), false);
        material.setCode(code);

        //todo should be notEmpty instead of null?
        if (immunizationMedicationInformation.getText() != null) {
            if (entriesReferredToSectionText.contains(ImmunizationMedicationInformation.class)) {
                code.setOriginalText(CcdUtils.createReferenceEntryText(ImmunizationMedicationInformation.class.getSimpleName() + immunizationMedicationInformation.getId()));
            } else {
                code.setOriginalText(CcdUtils.createEntryText(immunizationMedicationInformation.getText()));
            }
        }

        if (StringUtils.isNotEmpty(immunizationMedicationInformation.getLotNumberText())) {
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

    public ManufacturedProduct buildNullImmunizationMedicationInformation() {
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

    public org.eclipse.mdht.uml.cda.Person buildPerson(Person person) {
        org.eclipse.mdht.uml.cda.Person cdaPerson = CDAFactory.eINSTANCE.createPerson();
        CcdUtils.addConvertedNames(person.getNames(), cdaPerson.getNames(), true);
        return cdaPerson;
    }

    public org.eclipse.mdht.uml.cda.Person buildNullPerson() {
        org.eclipse.mdht.uml.cda.Person cdaPerson = CDAFactory.eINSTANCE.createPerson();
        cdaPerson.getNames().add(CcdUtils.getNullName());
        return cdaPerson;
    }

    public Performer2 buildPerformer2(Person person) {
        Performer2 performer = CDAFactory.eINSTANCE.createPerformer2();
        performer.setTypeCode(ParticipationPhysicalPerformer.PRF);
        AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
        II id;
        if (person.getId() != null) {
            //todo not npi
            id = CcdUtils.getNpiId(person.getId().toString());
        } else {
            id = CcdUtils.getNullId();
        }
        assignedEntity.getIds().add(id);
        if (person.getCode() != null) {
            assignedEntity.setCode(CcdUtils.createCE(person.getCode(), CodeSystem.NUCC_PROVIDER_CODES.getOid()));
        }

        CcdUtils.addConvertedAddresses(person.getAddresses(), assignedEntity.getAddrs(), true);
        CcdUtils.addConvertedTelecoms(person.getTelecoms(), assignedEntity.getTelecoms(), true);

        var assignedPerson = buildPerson(person);
        assignedEntity.setAssignedPerson(assignedPerson);
        performer.setAssignedEntity(assignedEntity);

        return performer;
    }

    private CD createProcedureTypeCode(ProcedureActivity procedure, boolean addAsReference) {
        CD code = CcdUtils.createCD(procedure.getProcedureType());
        if (StringUtils.isNotEmpty(procedure.getProcedureTypeText())) {
            if (addAsReference) {
                code.setOriginalText(CcdUtils.createReferenceEntryText(ProcedureActivity.class.getSimpleName() + procedure.getId()));
            } else {
                code.setOriginalText(CcdUtils.createEntryText(procedure.getProcedureTypeText()));
            }
        }
//        if (StringUtils.isEmpty(procedure.getProcedureTypeText())) {
//            code.setDisplayName(null);
//        }
        return code;
    }

    public Observation buildResultObservation(ResultObservation resultObservation) {
        Observation o = CDAFactory.eINSTANCE.createObservation();

        o.setClassCode(ActClassObservation.OBS);
        o.setMoodCode(x_ActMoodDocumentObservation.EVN);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.2");
        o.getTemplateIds().add(templateId);

        o.getIds().add(CcdUtils.getId(resultObservation.getId()));

        CE code = CcdUtils.createCE(resultObservation.getResultTypeCode());
        //todo should be notEmpty instead of null?
        if (resultObservation.getText() != null) {
            String refId = ResultObservation.class.getSimpleName() + resultObservation.getId();
            ED originalText = DatatypesFactory.eINSTANCE.createED();
            TEL ref = DatatypesFactory.eINSTANCE.createTEL();
            ref.setValue("#" + refId);
            originalText.setReference(ref);
            code.setOriginalText(originalText);
        }
        o.setCode(code);

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        if (StringUtils.isNotEmpty(resultObservation.getStatusCode())) {
            //aborted, active, cancelled, completed, held, suspended
            statusCode.setCode(resultObservation.getStatusCode().toLowerCase());
        } else {
            statusCode.setCode("completed");
            statusCode.setNullFlavor(NullFlavor.UNK);
        }
        o.setStatusCode(statusCode);

        o.setEffectiveTime(CcdUtils.createCenterTime(resultObservation.getEffectiveTime()));

        if (resultObservation.getMethodCode() != null) {
            CE methodCode = CcdUtils.createCE(resultObservation.getMethodCode());
            o.getMethodCodes().add(methodCode);
        }

        if (resultObservation.getTargetSiteCode() != null) {
            CD siteCode = CcdUtils.createCE(resultObservation.getTargetSiteCode());
            o.getTargetSiteCodes().add(siteCode);
        }

        if (resultObservation.getAuthor() != null) {
            o.getAuthors().add(buildAuthor(resultObservation.getAuthor()));
        }

        if (resultObservation.getInterpretationCodes() != null) {
            for (CcdCode interpretationCode : resultObservation.getInterpretationCodes()) {
                CE code1 = CcdUtils.createCE(interpretationCode);
                o.getInterpretationCodes().add(code1);
            }
        }

        initReferenceRanges(o, resultObservation.getReferenceRanges());

        //todo should be notEmpty instead of null?
        if (resultObservation.getValue() != null && resultObservation.getValueUnit() != null) {
            PQ code1 = DatatypesFactory.eINSTANCE.createPQ();
            code1.setValue(BigDecimal.valueOf(resultObservation.getValue()));
            code1.setUnit(resultObservation.getValueUnit());
            // Value type is "PQ", from example
            o.getValues().add(code1);
        } else {
            o.getValues().add(CcdUtils.createNillCode());
        }

        return o;
    }

    public Observation buildNullResultObservation() {
        Observation o = CDAFactory.eINSTANCE.createObservation();

        o.setClassCode(ActClassObservation.OBS);
        o.setMoodCode(x_ActMoodDocumentObservation.EVN);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.2");
        o.getTemplateIds().add(templateId);

        o.getIds().add(CcdUtils.getNullId());

        CE code = DatatypesFactory.eINSTANCE.createCE();
        code.setNullFlavor(NullFlavor.NI);
        o.setCode(code);

        // what statusCode?
        CS statusCode = DatatypesFactory.eINSTANCE.createCS("completed");
        statusCode.setNullFlavor(NullFlavor.UNK);
        o.setStatusCode(statusCode);

        o.setEffectiveTime(CcdUtils.getNullEffectiveTime());

        PQ code1 = DatatypesFactory.eINSTANCE.createPQ();
        code1.setNullFlavor(NullFlavor.NI);
        o.getValues().add(code1);

        return o;
    }

    protected abstract Organizer createResultOrganizer();

    public Organizer buildResultOrganizer(Result result) {
        Organizer organizer = createResultOrganizer();

        // (CONF-7121, CONF-7165) Attribute 'classCode' must appear on element
        // 'organizer'
        // (CONF-7165) The 'classCode' attribute of the 'organizer' element can take one
        // of two values:
        // * “BATTERY” – Used when the contents of the organizer are a group of related
        // clinical acts (typically, observations)
        // in a flat list, as would be the case in a battery of tests.
        // * “CLUSTER” – Used for more complex organizer constructs with nested
        // organizer
        organizer.setClassCode(x_ActClassDocumentEntryOrganizer.BATTERY); //default value
        if (StringUtils.isNotEmpty(result.getClassCode())) {
            try {
                organizer.setClassCode(x_ActClassDocumentEntryOrganizer.valueOf(result.getClassCode()));
            } catch (IllegalArgumentException e) {
                logger.info("Unknown Result Organizer classCode: " + result.getClassCode());
            }
        }

        organizer.setMoodCode(ActMood.EVN);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.1");
        organizer.getTemplateIds().add(templateId);

        organizer.getIds().add(CcdUtils.getId(result.getId()));

        CE code = CcdUtils.createCE(result.getCode());
        organizer.setCode(code);

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        if (StringUtils.isNotEmpty(result.getStatusCode())) {
//            aborted, active, cancelled, completed, held, suspended
            statusCode.setCode(result.getStatusCode().toLowerCase());

        } else {
            statusCode.setCode("completed");
            statusCode.setNullFlavor(NullFlavor.UNK);
        }
        organizer.setStatusCode(statusCode);

        if (!CollectionUtils.isEmpty(result.getObservations())) {
            for (ResultObservation resultObservation : result.getObservations()) {
                Component4 component4 = CDAFactory.eINSTANCE.createComponent4();
                component4.setObservation(buildResultObservation(resultObservation));
                organizer.getComponents().add(component4);
            }
        } else {
            Component4 component4 = CDAFactory.eINSTANCE.createComponent4();
            component4.setObservation(buildNullResultObservation());
            organizer.getComponents().add(component4);
        }

        return organizer;
    }

    public Organizer buildNullResultOrganizer() {
        var organizer = createResultOrganizer();

        // Read the above comment for @classCode
        organizer.setClassCode(x_ActClassDocumentEntryOrganizer.BATTERY);
        organizer.setMoodCode(ActMood.EVN);

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.1");
        organizer.getTemplateIds().add(templateId);

        organizer.getIds().add(CcdUtils.getNullId());

        CE code = DatatypesFactory.eINSTANCE.createCE();
        code.setNullFlavor(NullFlavor.NI);
        organizer.setCode(code);

        CS statusCode = DatatypesFactory.eINSTANCE.createCS("completed");
        statusCode.setNullFlavor(NullFlavor.UNK);
        organizer.setStatusCode(statusCode);

        Component4 component4 = CDAFactory.eINSTANCE.createComponent4();
        component4.setObservation(buildNullResultObservation());
        organizer.getComponents().add(component4);

        return organizer;
    }

    public AuthoringDevice buildNullAuthoringDevice() {
        org.eclipse.mdht.uml.cda.AuthoringDevice device = CDAFactory.eINSTANCE.createAuthoringDevice();
        device.setNullFlavor(NullFlavor.NI);

        var sc = DatatypesFactory.eINSTANCE.createSC();
        sc.setNullFlavor(NullFlavor.NI);
        device.setSoftwareName(sc);

        sc = DatatypesFactory.eINSTANCE.createSC();
        sc.setNullFlavor(NullFlavor.NI);
        device.setManufacturerModelName(sc);

        return device;
    }

    protected abstract Observation createAllergyObservation();

    protected abstract Observation createAllergyStatusObservation();

    public Observation buildAllergyObservation(AllergyObservation allergyObservation) {
        if (allergyObservation == null) {
            return buildNullAllergyObservation();
        }

        var alertObservation = createAllergyObservation();
        alertObservation.setClassCode(ActClassObservation.OBS);
        alertObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);
        alertObservation.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.7"));
        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        statusCode.setCode("completed");
        alertObservation.setStatusCode(statusCode);
        CD code = DatatypesFactory.eINSTANCE.createCD();
        code.setCode("ASSERTION");
        code.setCodeSystem(HL7_ACT_CODE.getOid());
        alertObservation.setCode(code);

        alertObservation.getIds().add(CcdUtils.getId(allergyObservation.getId()));
        CcdUtils.addConsanaId(alertObservation.getIds(), allergyObservation.getConsanaId());

        Date timeLow = allergyObservation.getTimeLow();
        Date timeHigh = allergyObservation.getTimeHigh();
        if (timeLow == null && timeHigh == null) {
            alertObservation.setEffectiveTime(CcdUtils.getNullEffectiveTime());
        } else {
            IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
            alertObservation.setEffectiveTime(effectiveTime);
            if (timeLow != null) {
                IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
                low.setValue(CcdUtils.formatSimpleDate(timeLow));
                effectiveTime.setLow(low);
            } else {
                IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
                low.setNullFlavor(NullFlavor.UNK);
                effectiveTime.setLow(low);
            }
            if (timeHigh != null) {
                IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
                high.setValue(CcdUtils.formatSimpleDate(timeHigh));
                effectiveTime.setHigh(high);
            }
        }

        CD allergyTypeValue = CcdUtils.createCDFromValueSetOrTranslationDefaultDisplayName(
                allergyObservation.getAdverseEventTypeCode(), ValueSetEnum.ADVERSE_EVENT_TYPE,
                allergyObservation.getAdverseEventTypeText());

        if (allergyObservation.getAdverseEventTypeCode() == null) {
            //adjust to meet cda CONF:9139 requirement - value/@code is required
            allergyTypeValue.setNullFlavor(NullFlavor.UNK);
            allergyTypeValue.setCode(ALLERGY_TYPE_DEFAULT_CODE);
            allergyTypeValue.setDisplayName(null);
        }

        if (allergyObservation.getAdverseEventTypeText() != null &&
                !allergyObservation.getAdverseEventTypeText().equals(allergyTypeValue.getDisplayName())) {
            ED originalText = DatatypesFactory.eINSTANCE.createED();
            originalText.addText(allergyObservation.getAdverseEventTypeText());
            allergyTypeValue.setOriginalText(originalText);
        }
        alertObservation.getValues().add(allergyTypeValue);

        //todo should be notEmpty instead of null?
        if (allergyObservation.getProductText() != null || allergyObservation.getProductCode() != null) {
            Participant2 participant2 = CDAFactory.eINSTANCE.createParticipant2();
            participant2.setTypeCode(ParticipationType.CSM);
            ParticipantRole participantRole = CDAFactory.eINSTANCE.createParticipantRole();
            participantRole.setClassCode(RoleClassRoot.MANU);
            PlayingEntity playingEntity = CDAFactory.eINSTANCE.createPlayingEntity();
            playingEntity.setClassCode(EntityClassRoot.MMAT);

            if (allergyObservation.getProductText() != null) {
                PN productName = DatatypesFactory.eINSTANCE.createPN();
                productName.addText(allergyObservation.getProductText());
                playingEntity.getNames().add(productName);
            }

            playingEntity.setCode(CcdUtils.createCEWithDefaultDisplayName(allergyObservation.getProductCode(),
                    allergyObservation.getProductText()));

            participantRole.setPlayingEntity(playingEntity);
            participant2.setParticipantRole(participantRole);
            alertObservation.getParticipants().add(participant2);
        }

        if (allergyObservation.getObservationStatusCode() != null) {
            var alertStatusObservation = createAllergyStatusObservation();
            alertObservation.addObservation(alertStatusObservation);

            var entryRelationship = (EntryRelationship) alertStatusObservation.eContainer();
            entryRelationship.setTypeCode(SUBJ);
            entryRelationship.setInversionInd(Boolean.TRUE);

            alertStatusObservation.setClassCode(ActClassObservation.OBS);
            alertStatusObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);
            alertStatusObservation.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.28"));
            alertStatusObservation.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));
            alertStatusObservation.setCode(CcdUtils.createCE(SectionTypeCode.STATUS_OBSERVATION));

            var value = CcdUtils.createCE(allergyObservation.getObservationStatusCode(), CodeSystem.SNOMED_CT.getOid());
            alertStatusObservation.getValues().add(value);
        }

        Collection<ReactionObservation> reactionObservations = allergyObservation.getReactionObservations();
        if (!CollectionUtils.isEmpty(reactionObservations)) {
            for (ReactionObservation reactionObservation : reactionObservations) {
                EntryRelationship entryRelationship1 = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship1.setTypeCode(MFST);
                entryRelationship1.setInversionInd(true);

                Set<Class<?>> entriesReferredToSectionText = new HashSet<>();
                entriesReferredToSectionText.add(ReactionObservation.class);
                entriesReferredToSectionText.add(SeverityObservation.class);

                entryRelationship1.setObservation(buildReactionObservation(reactionObservation, entriesReferredToSectionText));

                alertObservation.getEntryRelationships().add(entryRelationship1);
            }
        }

        SeverityObservation severityObservation = allergyObservation.getSeverityObservation();
        if (severityObservation != null) {
            EntryRelationship entryRelationship1 = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship1.setTypeCode(SUBJ);
            entryRelationship1.setInversionInd(true);

            Set<Class<?>> entriesReferredToSectionText = new HashSet<>();
            entryRelationship1.setObservation(buildSeverityObservation(severityObservation, entriesReferredToSectionText));

            alertObservation.getEntryRelationships().add(entryRelationship1);
        }

        return alertObservation;
    }

    public Observation buildNullAllergyObservation() {
        var alertObservation = createAllergyObservation();
        alertObservation.setNullFlavor(NullFlavor.UNK);

        alertObservation.setClassCode(ActClassObservation.OBS);
        alertObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);

        II alertObservationTemplateId = DatatypesFactory.eINSTANCE.createII();
        alertObservationTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.7");
        alertObservation.getTemplateIds().add(alertObservationTemplateId);

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        statusCode.setCode("completed");
        alertObservation.setStatusCode(statusCode);

        CD code = DatatypesFactory.eINSTANCE.createCD();
        code.setCode("ASSERTION");
        code.setCodeSystem(HL7_ACT_CODE.getOid());
        alertObservation.setCode(code);

        alertObservation.getIds().add(CcdUtils.getNullId());

        alertObservation.setEffectiveTime(CcdUtils.getNullEffectiveTime());

        CD allergyTypeValue = DatatypesFactory.eINSTANCE.createCD();
        allergyTypeValue.setNullFlavor(NullFlavor.UNK);
        allergyTypeValue.setCode(ALLERGY_TYPE_DEFAULT_CODE); //Propensity to adverse reactions
        alertObservation.getValues().add(allergyTypeValue);

        return alertObservation;
    }

    private CS createStatusCode(String statusCode) {
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

    public void initReferenceRanges(Observation observation, List<String> referenceRanges) {
        if (CollectionUtils.isNotEmpty(referenceRanges)) {
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

    private void addPerformers(List<Performer2> cdaPerformers, Collection<Community> performers) {
        if (!CollectionUtils.isEmpty(performers)) {
            for (Community community : performers) {
                Performer2 performer2 = CDAFactory.eINSTANCE.createPerformer2();
                AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();

                assignedEntity.getIds().add(CcdUtils.getId(community.getId()));
                CcdUtils.addConvertedAddresses(community.getAddresses(), assignedEntity.getAddrs(), true);
                CcdUtils.addConvertedTelecoms(CareCoordinationUtils.wrapIfNonNull(community.getTelecom()), assignedEntity.getTelecoms(), true);

                assignedEntity.getRepresentedOrganizations().add(buildOrganization(community, true));

                performer2.setAssignedEntity(assignedEntity);
                cdaPerformers.add(performer2);
            }
        }
    }
}
