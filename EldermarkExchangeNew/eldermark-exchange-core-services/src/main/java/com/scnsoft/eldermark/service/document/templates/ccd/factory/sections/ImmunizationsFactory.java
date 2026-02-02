package com.scnsoft.eldermark.service.document.templates.ccd.factory.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.DrugVehicle;
import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.service.document.templates.ccd.factory.entries.CcdSectionEntryFactory;
import com.scnsoft.eldermark.service.document.templates.ccd.parser.entries.SectionEntryParseFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.Performer2Factory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSectionFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.ccd.CCDFactory;
import org.openhealthtools.mdht.uml.cda.ccd.ImmunizationsSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <h1>Immunizations</h1> "This section defines a patient’s current immunization
 * status and pertinent immunization history." [CCD 3.11] <br/>
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
@Component
public class ImmunizationsFactory extends OptionalTemplateFactory
        implements ParsableSectionFactory<ImmunizationsSection, Immunization> {

    private static CcdSectionEntryFactory ccdSectionEntryFactory = CcdSectionEntryFactory.INSTANCE;

    @Autowired
    private CcdCodeFactory ccdCodeFactory;

    @Autowired
    private Performer2Factory performer2Factory;

    @Autowired
    private SectionEntryParseFactory sectionEntryParseFactory;

    private static final Logger logger = LoggerFactory.getLogger(ImmunizationsFactory.class);
    private static final String LEGACY_TABLE = "Immunization_NWHIN";
    private static final String IMMUNIZATION_REFUSAL_REASON_TEMPLATE_ID = "2.16.840.1.113883.10.20.22.4.53";

    @Value("${section.immunizations.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    public ImmunizationsSection buildTemplateInstance(Collection<Immunization> immunizations) {
        final ImmunizationsSection section = CCDFactory.eINSTANCE.createImmunizationsSection();
        section.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.1.6"));

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
            substanceAdministration
                    .setRouteCode(CcdUtils.createCEOrTranslation(immunization.getRoute(), CodeSystem.NCI_THESAURUS.getOid(), false));
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
            consumable.setManufacturedProduct(ccdSectionEntryFactory.buildImmunizationMedicationInformation(
                    immunization.getImmunizationMedicationInformation(), entriesReferredToSectionText));
        } else {
            consumable.setManufacturedProduct(ccdSectionEntryFactory.buildNullImmunizationMedicationInformation());
        }
        substanceAdministration.setConsumable(consumable);

        if (!CollectionUtils.isEmpty(immunization.getDrugVehicles())) {
            for (DrugVehicle drugVehicle : immunization.getDrugVehicles()) {
                Participant2 participant2 = CDAFactory.eINSTANCE.createParticipant2();
                participant2.setTypeCode(ParticipationType.CSM);
                participant2.setParticipantRole(ccdSectionEntryFactory.buildDrugVehicle(drugVehicle));
                substanceAdministration.getParticipants().add(participant2);
            }
        }

        if (!CollectionUtils.isEmpty(immunization.getIndications())) {
            for (Indication indication : immunization.getIndications()) {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.RSON);
                entryRelationship.setObservation(ccdSectionEntryFactory.buildIndication(indication));
                substanceAdministration.getEntryRelationships().add(entryRelationship);
            }
        }

        if (immunization.getInstructions() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
            entryRelationship.setInversionInd(true);
            entryRelationship.setAct(ccdSectionEntryFactory.buildInstructions(immunization.getInstructions(),
                    entriesReferredToSectionText));
            substanceAdministration.getEntryRelationships().add(entryRelationship);
        }

        if (immunization.getMedicationSupplyOrder() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.REFR);
            entryRelationship.setSupply(ccdSectionEntryFactory
                    .buildMedicationSupplyOrder(immunization.getMedicationSupplyOrder(), entriesReferredToSectionText));
            substanceAdministration.getEntryRelationships().add(entryRelationship);
        }

        if (immunization.getMedicationDispense() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.REFR);
            entryRelationship.setSupply(ccdSectionEntryFactory
                    .buildMedicationDispense(immunization.getMedicationDispense(), entriesReferredToSectionText));
            substanceAdministration.getEntryRelationships().add(entryRelationship);
        }

        if (immunization.getReactionObservation() != null) {
            EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
            entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.CAUS);
            entryRelationship.setObservation(ccdSectionEntryFactory
                    .buildReactionObservation(immunization.getReactionObservation(), entriesReferredToSectionText));
            substanceAdministration.getEntryRelationships().add(entryRelationship);
        }

        if (!CollectionUtils.isEmpty(immunization.getPreconditions())) {
            for (MedicationPrecondition precondition : immunization.getPreconditions()) {
                substanceAdministration.getPreconditions()
                        .add(ccdSectionEntryFactory.buildMedicationPrecondition(precondition));
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
                    .add(ccdSectionEntryFactory.buildPerformer2(immunization.getPerformer()));
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
        consumable.setManufacturedProduct(ccdSectionEntryFactory.buildNullImmunizationMedicationInformation());
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

        if (org.apache.commons.collections4.CollectionUtils.isEmpty(immunizations)) {
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

    private Immunization parseImmunizationActivity(SubstanceAdministration substanceAdministration, Client client) {
        if (!CcdParseUtils.hasContent(substanceAdministration) || client == null) {
            return null;
        }
        checkNotNull(client);

        Immunization immunization = new Immunization();
        immunization.setClient(client);
        immunization.setOrganization(client.getOrganization());
        immunization.setLegacyId(CcdParseUtils.getFirstIdExtensionStr(substanceAdministration.getIds()));

        CD code = substanceAdministration.getCode();
        immunization.setCode(ccdCodeFactory.convert(code));
        immunization.setText(CcdTransform.EDtoString(substanceAdministration.getText(), immunization.getCode()));
        if (substanceAdministration.getStatusCode() != null) {
            immunization.setStatusCode(substanceAdministration.getStatusCode().getCode());
        }

        for (SXCM_TS sxcm_ts : substanceAdministration.getEffectiveTimes()) {
            if (sxcm_ts instanceof IVL_TS) {
                Pair<Date, Date> effectiveTimes = CcdTransform.IVLTStoHighLowDate((IVL_TS) sxcm_ts);
                if (effectiveTimes != null) {
                    immunization.setImmunizationStarted(effectiveTimes.getSecond());
                    immunization.setImmunizationStopped(effectiveTimes.getFirst());
                }
            } else {
                immunization.setImmunizationStarted(CcdParseUtils.parseDate(sxcm_ts.getValue()));
                logger.warn("Ambiguous TS value in Immunizations section: " + sxcm_ts.getValue()
                        + ". This time will be stored as immunization started date.");
            }
        }

        immunization.setRepeatNumber(CcdTransform.INTtoInteger(substanceAdministration.getRepeatNumber()));
        // TODO how to get repeat number mood?
        if (substanceAdministration.isSetMoodCode()) {
            immunization.setMoodCode(substanceAdministration.getMoodCode().getLiteral());
        }

        immunization.setRoute(ccdCodeFactory.convert(substanceAdministration.getRouteCode()));
        CD siteValue = CcdParseUtils.getFirstNotEmptyValue(substanceAdministration.getApproachSiteCodes(), CD.class);
        immunization.setSite(ccdCodeFactory.convert(siteValue));

        if (CcdParseUtils.hasContent(substanceAdministration.getDoseQuantity())) {
            immunization.setDoseQuantity(CcdTransform.PQtoInteger(substanceAdministration.getDoseQuantity()));
            immunization.setDoseUnits(substanceAdministration.getDoseQuantity().getUnit());
        }
        immunization
                .setAdministrationUnitCode(ccdCodeFactory.convert(substanceAdministration.getAdministrationUnitCode()));

        if (substanceAdministration.getConsumable() != null) {
            immunization.setImmunizationMedicationInformation(
                    sectionEntryParseFactory.parseImmunizationMedicationInformation(
                            substanceAdministration.getConsumable().getManufacturedProduct(), client));
        }

        immunization.setDrugVehicles(
                sectionEntryParseFactory.parseDrugVehicles(substanceAdministration.getParticipants(), client));

        if (!CollectionUtils.isEmpty(substanceAdministration.getEntryRelationships())) {
            List<Indication> indications = null;
            for (EntryRelationship ccdEntryRelationship : substanceAdministration.getEntryRelationships()) {
                switch (ccdEntryRelationship.getTypeCode()) {
                    case RSON:
                        Observation observation = ccdEntryRelationship.getObservation();
                        II id = CcdParseUtils.getFirstNotEmptyValue(observation.getTemplateIds(), II.class);
                        // In order to distinguish between Indication and ImmunizationRefusalReason we
                        // check templateId
                        if (id != null && IMMUNIZATION_REFUSAL_REASON_TEMPLATE_ID.equals(id.getRoot())) {
                            immunization.setImmunizationRefusalReason(
                                    parseImmunizationRefusalReason(ccdEntryRelationship.getObservation(), client));
                        } else {
                            if (indications == null) {
                                indications = new ArrayList<>();
                            }
                            Indication indication = sectionEntryParseFactory.parseIndication(observation, client,
                                    LEGACY_TABLE);
                            indications.add(indication);
                        }
                        break;
                    case SUBJ:
                        immunization.setInstructions(
                                sectionEntryParseFactory.parseInstructions(ccdEntryRelationship.getAct(), client));
                        break;
                    case REFR:
                        Supply ccdSupply = ccdEntryRelationship.getSupply();
                        if (ccdSupply.getMoodCode() == x_DocumentSubstanceMood.EVN) {
                            immunization.setMedicationDispense(
                                    sectionEntryParseFactory.parseMedicationDispense(ccdSupply, client, LEGACY_TABLE));
                        } else if (ccdSupply.getMoodCode() == x_DocumentSubstanceMood.INT) {
                            immunization.setMedicationSupplyOrder(
                                    sectionEntryParseFactory.parseMedicationSupplyOrder(ccdSupply, client, LEGACY_TABLE));
                        } else {
                            logger.warn(
                                    "parseMedicationActivity : Unknown EntryRelationship of type REFR and Supply moodCode = "
                                            + ccdSupply.getMoodCode());
                        }
                        break;
                    case CAUS:
                        immunization.setReactionObservation(sectionEntryParseFactory
                                .parseReactionObservation(ccdEntryRelationship.getObservation(), client, LEGACY_TABLE));
                        break;
                    default:
                        break;
                }
            }
            immunization.setIndications(indications);
        }

        immunization.setPreconditions(
                sectionEntryParseFactory.parsePreconditions(substanceAdministration.getPreconditions(), client));

        if (!CollectionUtils.isEmpty(substanceAdministration.getPerformers())) {
            Performer2 ccdPerformer2 = substanceAdministration.getPerformers().get(0);
            immunization
                    .setPerformer(performer2Factory.parsePerson(ccdPerformer2, client.getOrganization(), LEGACY_TABLE));
        }

        return immunization;
    }

    private ImmunizationRefusalReason parseImmunizationRefusalReason(Observation ccdObservation, Client client) {
        if (!CcdParseUtils.hasContent(ccdObservation) || client == null) {
            return null;
        }
        checkNotNull(client);

        ImmunizationRefusalReason immunizationRefusalReason = new ImmunizationRefusalReason();
        immunizationRefusalReason.setOrganization(client.getOrganization());
        immunizationRefusalReason.setCode(ccdCodeFactory.convert(ccdObservation.getCode()));

        return immunizationRefusalReason;
    }

    @Override
    public List<Immunization> parseSection(Client client, ImmunizationsSection immunizationsSection) {
        if (!CcdParseUtils.hasContent(immunizationsSection)
                || CollectionUtils.isEmpty(immunizationsSection.getSubstanceAdministrations())) {
            return Collections.emptyList();
        }
        checkNotNull(client);

        final List<Immunization> immunizations = new ArrayList<>();
        for (SubstanceAdministration ccdImmunizationSubstanceAdministration : immunizationsSection
                .getSubstanceAdministrations()) {
            immunizations.add(parseImmunizationActivity(ccdImmunizationSubstanceAdministration, client));
        }

        return immunizations;
    }

}