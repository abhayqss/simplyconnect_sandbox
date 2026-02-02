package com.scnsoft.eldermark.services.cda.templates.sections.entries;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
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
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author phomal
 * Created on 4/13/2018.
 */
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
    public SubstanceAdministrationFactory(CcdCodeFactory ccdCodeFactory,
                                          IndicationFactory indicationFactory,
                                          InstructionsFactory instructionsFactory,
                                          ParticipantRoleFactory participantFactory,
                                          Performer2Factory performer2Factory,
                                          AuthorFactory authorFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
        this.indicationFactory = indicationFactory;
        this.instructionsFactory = instructionsFactory;
        this.participantFactory = participantFactory;
        this.performer2Factory = performer2Factory;
        this.authorFactory = authorFactory;
    }

    public Medication parseMedicationActivity(SubstanceAdministration substanceAdministration, Resident resident, String legacyTable) {
        if (!CcdParseUtils.hasContent(substanceAdministration) || resident == null) {
            return null;
        }

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
            // FIXME dose quantity may be non-integer, e.g. "1 1/2 of tablet"
            // possible solution: store non-integer values as plain text in doseQuantity > translation > displayName
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

        medication.setDrugVehicles(participantFactory.parseDrugVehicles(substanceAdministration.getParticipants(), resident));

        if (!CollectionUtils.isEmpty(substanceAdministration.getEntryRelationships())) {
            List<Indication> indications = null;
            for (EntryRelationship ccdEntryRelationship : substanceAdministration.getEntryRelationships()) {
                switch (ccdEntryRelationship.getTypeCode()) {
                    case RSON:
                        if (indications == null) {
                            indications = new ArrayList<>();
                        }
                        Indication indication = indicationFactory.parseIndication(ccdEntryRelationship.getObservation(), resident, legacyTable);
                        if (indication != null) {
                            indications.add(indication);
                        }
                        break;
                    case SUBJ:
                        // [FIXME] entryRelationship with @typeCode = "SUBJ" may indicate either
                        // [FIXME] Patient Instruction (templateId: 2.16.840.1.113883.10.20.1.49) or Medication Series Number Observation (templateId: 2.16.840.1.113883.10.20.1.46)
                        medication.setInstructions(instructionsFactory.parseInstructions(ccdEntryRelationship.getAct(), resident));
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
                        medication.setReactionObservation(observationsFactory.parseReactionObservation(ccdEntryRelationship.getObservation(),
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
            com.scnsoft.eldermark.entity.Author author = authorFactory.parseAuthor(ccdAuthor, resident, legacyTable);
            if (author != null) {
                medicationSupplyOrder.setAuthor(author);
                break;
            }
        }

        for (EntryRelationship entryRelationship : ccdSupply.getEntryRelationships()) {
            if (CcdParseUtils.hasContent(entryRelationship) &&
                    entryRelationship.getTypeCode() == x_ActRelationshipEntryRelationship.SUBJ) {
                Act ccdAct = entryRelationship.getAct();
                Instructions instructions = instructionsFactory.parseInstructions(ccdAct, resident);
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

        List<MedicationDispense> medicationDispenses = new ArrayList<>();
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

        MedicationDispense medicationDispense = new MedicationDispense();
        medicationDispense.setDatabase(resident.getDatabase());
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
                    //Organization organization = ccdTransform.toOrganization(ccdOrganization, resident.getDatabase(), legacyTable);
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
            com.scnsoft.eldermark.entity.Organization org = CcdTransform.toOrganization(manufacturedProduct.getManufacturerOrganization(),
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
            List<MedicationPrecondition> medicationPreconditions = new ArrayList<>();
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

}
