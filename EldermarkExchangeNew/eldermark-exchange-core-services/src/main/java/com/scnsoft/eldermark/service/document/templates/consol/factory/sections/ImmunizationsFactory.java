package com.scnsoft.eldermark.service.document.templates.consol.factory.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.DrugVehicle;
import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.SectionFactory;
import com.scnsoft.eldermark.service.document.templates.consol.factory.entries.ConsolSectionEntryFactory;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.ImmunizationsSectionEntriesOptional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * <h1>Immunizations</h1> "This section defines a patient’s current immunization
 * status and pertinent immunization historyById." [CCD 3.11] <br/>
 * Immunizations section uses the same sample templates as Medications. <br/>
 * This section is optional, however it is strongly recommended that it be
 * present in cases of pediatric care and in other cases when such information
 * is available.
 *
 * @see Immunization
 * @see ImmunizationMedicationInformation
 * @see ImmunizationRefusalReason
 * @see Indication
 * @see MedicationPrecondition
 * @see DrugVehicle
 * @see Client
 */
@Component("consol.ImmunizationsFactory")
public class ImmunizationsFactory extends OptionalTemplateFactory
        implements SectionFactory<ImmunizationsSectionEntriesOptional, Immunization> {

    // private static final String LEGACY_TABLE = "Immunization_NWHIN";
    private static final String TEMPLATE_ID_STR = "2.16.840.1.113883.10.20.22.2.2";
    private static final String IMMUNIZATION_REFUSAL_REASON_TEMPLATE_ID = "2.16.840.1.113883.10.20.22.4.53";
    private static ConsolSectionEntryFactory consolSectionEntryFactory = ConsolSectionEntryFactory.INSTANCE;

    @Value("${section.immunizations.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    public ImmunizationsSectionEntriesOptional buildTemplateInstance(Collection<Immunization> immunizations) {
        final ImmunizationsSectionEntriesOptional section = ConsolFactory.eINSTANCE
                .createImmunizationsSectionEntriesOptional();
        section.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_STR));

        final CE sectionCode = DatatypesFactory.eINSTANCE.createCE();
        sectionCode.setCode("11369-6");
        sectionCode.setCodeSystem("2.16.840.1.113883.6.1");
        sectionCode.setDisplayName("History of immunizations");
        sectionCode.setCodeSystemName("LOINC");
        section.setCode(sectionCode);

        final ST title = DatatypesFactory.eINSTANCE.createST();
        title.addText("Immunizations");
        section.setTitle(title);

        section.createStrucDocText(buildSectionText(immunizations));

        if (CollectionUtils.isEmpty(immunizations)) {
            final Entry entry = CDAFactory.eINSTANCE.createEntry();
            entry.setSubstanceAdministration(buildNullImmunizationActivity());
            return section;
        }

        for (Immunization immunization : immunizations) {
            final Entry entry = CDAFactory.eINSTANCE.createEntry();

            final Set<Class<?>> entriesReferredToSectionText = new HashSet<>();
            entriesReferredToSectionText.add(ImmunizationMedicationInformation.class);

            entry.setSubstanceAdministration(buildImmunizationActivity(immunization, entriesReferredToSectionText));
            section.getEntries().add(entry);
        }

        return section;
    }

    private SubstanceAdministration buildImmunizationActivity(Immunization immunization,
                                                              Set<Class<?>> entriesReferredToSectionText) {
        final SubstanceAdministration substanceAdministration = CDAFactory.eINSTANCE.createSubstanceAdministration();
        substanceAdministration.setClassCode(ActClass.SBADM);

        if (immunization.getMoodCode() != null) {
            substanceAdministration.setMoodCode(x_DocumentSubstanceMood.valueOf(immunization.getMoodCode()));
        } else {
            substanceAdministration.setMoodCode(x_DocumentSubstanceMood.EVN);
        }

        if (immunization.getRefusal() != null) {
            substanceAdministration.setNegationInd(immunization.getRefusal());
        } else {
            substanceAdministration.setNegationInd(false);
        }

        II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.52");
        substanceAdministration.getTemplateIds().add(templateId);

        substanceAdministration.getIds().add(CcdUtils.getId(immunization.getId()));

        if (immunization.getCode() != null) {
            CD code = CcdUtils.createCD(immunization.getCode());
            // code system ?
            substanceAdministration.setCode(code);
        }

        if (immunization.getText() != null) {
            if (entriesReferredToSectionText.contains(Immunization.class)) {
                substanceAdministration.setText(
                        CcdUtils.createReferenceEntryText(Immunization.class.getSimpleName() + immunization.getId()));
            } else {
                substanceAdministration.setText(CcdUtils.createEntryText(immunization.getText()));
            }
        }

        CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        if (immunization.getStatusCode() != null) {
            statusCode.setCode(immunization.getStatusCode());
        } else {
            statusCode.setNullFlavor(NullFlavor.NI);
        }
        substanceAdministration.setStatusCode(statusCode);

        IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
        Date timeLow = immunization.getImmunizationStarted();
        Date timeHigh = immunization.getImmunizationStopped();
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

        if (immunization.getRepeatNumber() != null) {
            IVL_INT repeatNumber = DatatypesFactory.eINSTANCE.createIVL_INT();
            repeatNumber.setValue(BigInteger.valueOf(immunization.getRepeatNumber()));
            // how to set repeat number mood?
            substanceAdministration.setRepeatNumber(repeatNumber);
        }

        if (immunization.getRoute() != null) {
            substanceAdministration.setRouteCode(CcdUtils.createCEOrTranslation(immunization.getRoute(), CodeSystem.NCI_THESAURUS.getOid(), false));
        }

        if (immunization.getSite() != null) {
            substanceAdministration.getApproachSiteCodes()
                    .add(CcdUtils.createCE(immunization.getSite(), CodeSystem.SNOMED_CT.getOid()));
        }

        if (immunization.getDoseQuantity() != null) {
            IVL_PQ pq = DatatypesFactory.eINSTANCE.createIVL_PQ();
            pq.setValue(BigDecimal.valueOf(immunization.getDoseQuantity()));
            if (immunization.getDoseUnits() != null) {
                pq.setUnit(immunization.getDoseUnits());
            }
            substanceAdministration.setDoseQuantity(pq);
        }

        if (immunization.getAdministrationUnitCode() != null) {
            substanceAdministration.setAdministrationUnitCode(
                    CcdUtils.createCEOrTranslation(immunization.getAdministrationUnitCode(), CodeSystem.NCI_THESAURUS.getOid(), false));
        }

        Consumable consumable = CDAFactory.eINSTANCE.createConsumable();
        if (immunization.getImmunizationMedicationInformation() != null) {
            consumable.setManufacturedProduct(consolSectionEntryFactory.buildImmunizationMedicationInformation(
                    immunization.getImmunizationMedicationInformation(), entriesReferredToSectionText));
        } else {
            consumable.setManufacturedProduct(consolSectionEntryFactory.buildNullImmunizationMedicationInformation());
        }
        substanceAdministration.setConsumable(consumable);

        if (!CollectionUtils.isEmpty(immunization.getDrugVehicles())) {
            for (DrugVehicle drugVehicle : immunization.getDrugVehicles()) {
                Participant2 participant2 = CDAFactory.eINSTANCE.createParticipant2();
                participant2.setTypeCode(ParticipationType.CSM);
                participant2.setParticipantRole(consolSectionEntryFactory.buildDrugVehicle(drugVehicle));
                substanceAdministration.getParticipants().add(participant2);
            }
        }

        if (!CollectionUtils.isEmpty(immunization.getIndications())) {
            for (Indication indication : immunization.getIndications()) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.RSON);
                entryRelationship.setObservation(consolSectionEntryFactory.buildIndication(indication));
                substanceAdministration.getEntryRelationships().add(entryRelationship);
            }
        }

        if (immunization.getInstructions() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
            entryRelationship.setInversionInd(true);
            entryRelationship.setAct(consolSectionEntryFactory.buildInstructions(immunization.getInstructions(),
                    entriesReferredToSectionText));
            substanceAdministration.getEntryRelationships().add(entryRelationship);
        }

        if (immunization.getMedicationSupplyOrder() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.REFR);
            entryRelationship.setSupply(consolSectionEntryFactory
                    .buildMedicationSupplyOrder(immunization.getMedicationSupplyOrder(), entriesReferredToSectionText));
            substanceAdministration.getEntryRelationships().add(entryRelationship);
        }

        if (immunization.getMedicationDispense() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.REFR);
            entryRelationship.setSupply(consolSectionEntryFactory
                    .buildMedicationDispense(immunization.getMedicationDispense(), entriesReferredToSectionText));
            substanceAdministration.getEntryRelationships().add(entryRelationship);
        }

        if (immunization.getReactionObservation() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.CAUS);
            entryRelationship.setObservation(consolSectionEntryFactory
                    .buildReactionObservation(immunization.getReactionObservation(), entriesReferredToSectionText));
            substanceAdministration.getEntryRelationships().add(entryRelationship);
        }

        if (!CollectionUtils.isEmpty(immunization.getPreconditions())) {
            for (MedicationPrecondition precondition : immunization.getPreconditions()) {
                substanceAdministration.getPreconditions()
                        .add(consolSectionEntryFactory.buildMedicationPrecondition(precondition));
            }
        }

        if (immunization.getImmunizationRefusalReason() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.RSON);
            entryRelationship
                    .setObservation(buildImmunizationRefusalReason(immunization.getImmunizationRefusalReason()));
            substanceAdministration.getEntryRelationships().add(entryRelationship);
        }

        if (immunization.getPerformer() != null) {
            substanceAdministration.getPerformers()
                    .add(consolSectionEntryFactory.buildPerformer2(immunization.getPerformer()));
        }

        return substanceAdministration;
    }

    public SubstanceAdministration buildNullImmunizationActivity() {
        final SubstanceAdministration substanceAdministration = CDAFactory.eINSTANCE.createSubstanceAdministration();
        substanceAdministration.setClassCode(ActClass.SBADM);
        substanceAdministration.setMoodCode(x_DocumentSubstanceMood.EVN);

        final II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot("2.16.840.1.113883.10.20.22.4.52");
        substanceAdministration.getTemplateIds().add(templateId);

        substanceAdministration.getIds().add(CcdUtils.getNullId());

        final CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        statusCode.setNullFlavor(NullFlavor.NI);
        substanceAdministration.setStatusCode(statusCode);

        substanceAdministration.getEffectiveTimes().add(CcdUtils.getNullEffectiveTime());

        final Consumable consumable = CDAFactory.eINSTANCE.createConsumable();
        consumable.setManufacturedProduct(consolSectionEntryFactory.buildNullImmunizationMedicationInformation());
        substanceAdministration.setConsumable(consumable);

        return substanceAdministration;
    }

    private Observation buildImmunizationRefusalReason(ImmunizationRefusalReason refusalReason) {
        final Observation observation = CDAFactory.eINSTANCE.createObservation();
        observation.setClassCode(ActClassObservation.OBS);
        observation.setMoodCode(x_ActMoodDocumentObservation.EVN);

        final II templateId = DatatypesFactory.eINSTANCE.createII();
        templateId.setRoot(IMMUNIZATION_REFUSAL_REASON_TEMPLATE_ID);
        observation.getTemplateIds().add(templateId);

        observation.getIds().add(CcdUtils.getId(refusalReason.getId()));

        observation.setCode(CcdUtils.createCD(refusalReason.getCode(), "2.16.840.1.113883.5.8"));

        final CS statusCode = DatatypesFactory.eINSTANCE.createCS();
        statusCode.setCode("completed");
        observation.setStatusCode(statusCode);

        return observation;
    }

    private static String buildSectionText(Collection<Immunization> immunizations) {

        if (CollectionUtils.isEmpty(immunizations)) {
            return "No known immunizations.";
        }

        final StringBuilder sectionText = new StringBuilder("<table>" +
                "<thead>" +
                "<tr>" + "<th>Vaccine</th>" +
                "<th>Dates</th>" +
                "<th>Status</th>" +
                "</tr>" +
                "</thead>");

        var body = new StringBuilder();

        for (Immunization immunization : immunizations) {
            body.append("<tr>");

            body.append("<td>");
            ImmunizationMedicationInformation medInformation = immunization.getImmunizationMedicationInformation();
            if (medInformation != null) {
                String productName = medInformation.getText();
                if (productName != null) {
                    CcdUtils.addReferenceToSectionText(
                            ImmunizationMedicationInformation.class.getSimpleName() + medInformation.getId(),
                            productName, body);
                } else {
                    CcdUtils.addEmptyCellToSectionText(body);
                }
            } else {
                CcdUtils.addEmptyCellToSectionText(body);
            }
            body.append("</td>");

            body.append("<td>");
            if (immunization.getImmunizationStarted() != null || immunization.getImmunizationStopped() != null) {
                CcdUtils.addDateRangeToSectionText(immunization.getImmunizationStarted(),
                        immunization.getImmunizationStopped(), body);
            } else {
                CcdUtils.addEmptyCellToSectionText(body);
            }
            body.append("</td>");

            body.append("<td>");
            if (immunization.getStatusCode() != null) {
                body.append(StringEscapeUtils.escapeHtml(immunization.getStatusCode()));
            } else {
                CcdUtils.addEmptyCellToSectionText(body);
            }
            body.append("</td>");

            body.append("</tr>");
        }

        CcdUtils.addContent(sectionText, body, CcdUtils.ContentTag.TBODY);

        sectionText.append("</table>");

        return sectionText.toString();
    }

}