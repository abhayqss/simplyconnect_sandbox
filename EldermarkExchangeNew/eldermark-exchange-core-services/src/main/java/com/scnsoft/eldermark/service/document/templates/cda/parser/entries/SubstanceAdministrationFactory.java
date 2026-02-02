package com.scnsoft.eldermark.service.document.templates.cda.parser.entries;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Instructions;
import com.scnsoft.eldermark.entity.MedicationDispense;
import com.scnsoft.eldermark.entity.MedicationSupplyOrder;
import com.scnsoft.eldermark.entity.document.ccd.ImmunizationMedicationInformation;
import com.scnsoft.eldermark.entity.document.ccd.Indication;
import com.scnsoft.eldermark.entity.document.ccd.MedicationPrecondition;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.eclipse.mdht.uml.hl7.vocab.x_DocumentSubstanceMood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SubstanceAdministrationFactory {

    private static final Logger logger = LoggerFactory.getLogger(SubstanceAdministrationFactory.class);

    private final CcdCodeFactory ccdCodeFactory;
    private final IndicationFactory indicationFactory;
    private final InstructionsFactory instructionsFactory;
    private final ParticipantRoleFactory participantFactory;
    private final Performer2Factory performer2Factory;
    private final AuthorFactory authorFactory;

    @Autowired
    private ObservationFactory observationsFactory;

    @Autowired
    public SubstanceAdministrationFactory(CcdCodeFactory ccdCodeFactory, IndicationFactory indicationFactory,
                                          InstructionsFactory instructionsFactory, ParticipantRoleFactory participantFactory,
                                          Performer2Factory performer2Factory, AuthorFactory authorFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
        this.indicationFactory = indicationFactory;
        this.instructionsFactory = instructionsFactory;
        this.participantFactory = participantFactory;
        this.performer2Factory = performer2Factory;
        this.authorFactory = authorFactory;
    }

    public Medication parseMedicationActivity(SubstanceAdministration substanceAdministration, Client client,
                                              String legacyTable) {
        if (!CcdParseUtils.hasContent(substanceAdministration) || client == null) {
            return null;
        }

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
            // FIXME dose quantity may be non-integer, e.g. "1 1/2 of tablet"
            // possible solution: store non-integer values as plain text in doseQuantity >
            // translation > displayName
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

        medication.setDrugVehicles(
                participantFactory.parseDrugVehicles(substanceAdministration.getParticipants(), client));

        if (!CollectionUtils.isEmpty(substanceAdministration.getEntryRelationships())) {
            List<Indication> indications = null;
            for (EntryRelationship ccdEntryRelationship : substanceAdministration.getEntryRelationships()) {
                switch (ccdEntryRelationship.getTypeCode()) {
                    case RSON:
                        if (indications == null) {
                            indications = new ArrayList<>();
                        }
                        Indication indication = indicationFactory.parseIndication(ccdEntryRelationship.getObservation(),
                                client, legacyTable);
                        if (indication != null) {
                            indications.add(indication);
                        }
                        break;
                    case SUBJ:
                        // [FIXME] entryRelationship with @typeCode = "SUBJ" may indicate either
                        // [FIXME] Patient Instruction (templateId: 2.16.840.1.113883.10.20.1.49) or
                        // Medication Series Number Observation (templateId:
                        // 2.16.840.1.113883.10.20.1.46)
                        medication.setInstructions(
                                instructionsFactory.parseInstructions(ccdEntryRelationship.getAct(), client));
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
                        medication.setReactionObservation(observationsFactory
                                .parseReactionObservation(ccdEntryRelationship.getObservation(), client, legacyTable));
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
            //todo - check template id and parse either as MedicationInformation or as ImmunizationMedicationInformation
            //todo - check LSSI data and delete either MedicationInformation or ImmunizationMedicationInformation for supply order
            medicationSupplyOrder
                    .setMedicationInformation(parseMedicationInformation(ccdManufacturedProduct, client, legacyTable));
            medicationSupplyOrder.setImmunizationMedicationInformation(
                    parseImmunizationMedicationInformation(ccdManufacturedProduct, client));
        }

        for (org.eclipse.mdht.uml.cda.Author ccdAuthor : ccdSupply.getAuthors()) {
            com.scnsoft.eldermark.entity.document.ccd.Author author = authorFactory.parseAuthor(ccdAuthor, client, legacyTable);
            if (author != null) {
                medicationSupplyOrder.setAuthor(author);
                break;
            }
        }

        for (EntryRelationship entryRelationship : ccdSupply.getEntryRelationships()) {
            if (CcdParseUtils.hasContent(entryRelationship)
                    && entryRelationship.getTypeCode() == x_ActRelationshipEntryRelationship.SUBJ) {
                Act ccdAct = entryRelationship.getAct();
                Instructions instructions = instructionsFactory.parseInstructions(ccdAct, client);
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

        List<MedicationDispense> medicationDispenses = new ArrayList<>();
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

        MedicationDispense medicationDispense = new MedicationDispense();
        medicationDispense.setOrganization(client.getOrganization());
        medicationDispense.setLegacyTable(legacyTable);
        medicationDispense.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdSupply.getIds()));

        if (CcdParseUtils.hasContent(ccdSupply.getStatusCode())) {
            medicationDispense.setStatusCode(ccdSupply.getStatusCode().getCode());
        }

        SXCM_TS effectiveTime = CcdParseUtils.getFirstNotEmptyValue(ccdSupply.getEffectiveTimes(), SXCM_TS.class);
        if (effectiveTime instanceof IVL_TS) {
            Pair<Date, Date> effectiveTimes = CcdTransform.IVLTStoHighLowDateOrTsToDate((IVL_TS) effectiveTime);
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
                    // Organization organization = ccdTransform.toOrganization(ccdOrganization,
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
            CcdTransform.toCommunity(manufacturedProduct.getManufacturerOrganization(), client.getOrganization(),
                    legacyTable);
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
            List<MedicationPrecondition> medicationPreconditions = new ArrayList<>();
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

}
