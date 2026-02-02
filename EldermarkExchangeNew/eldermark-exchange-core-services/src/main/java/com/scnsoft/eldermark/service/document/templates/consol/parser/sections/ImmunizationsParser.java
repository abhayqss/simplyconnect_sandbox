package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.DrugVehicle;
import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.*;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.AbstractParsableSection;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSection;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import org.apache.commons.lang.StringUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.II;
import org.eclipse.mdht.uml.hl7.datatypes.IVL_TS;
import org.eclipse.mdht.uml.hl7.datatypes.SXCM_TS;
import org.eclipse.mdht.uml.hl7.vocab.x_DocumentSubstanceMood;
import org.openhealthtools.mdht.uml.cda.consol.ImmunizationsSectionEntriesOptional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
@Component("consol.ImmunizationsParser")
public class ImmunizationsParser
        extends AbstractParsableSection<SubstanceAdministration, ImmunizationsSectionEntriesOptional, Immunization>
        implements ParsableSection<ImmunizationsSectionEntriesOptional, Immunization> {

    private static final Logger logger = LoggerFactory.getLogger(ImmunizationsParser.class);
    private static final String LEGACY_TABLE = "Immunization_NWHIN";
    private static final String IMMUNIZATION_REFUSAL_REASON_TEMPLATE_ID = "2.16.840.1.113883.10.20.22.4.53";

    private final CcdCodeFactory ccdCodeFactory;
    private final SubstanceAdministrationFactory substanceAdministrationFactory;
    private final ObservationFactory observationFactory;
    private final IndicationFactory indicationFactory;
    private final InstructionsFactory instructionsFactory;
    private final ParticipantRoleFactory participantRoleFactory;
    private final Performer2Factory performer2Factory;

    @Autowired
    public ImmunizationsParser(CcdCodeFactory ccdCodeFactory,
            SubstanceAdministrationFactory substanceAdministrationFactory, ObservationFactory observationFactory,
            IndicationFactory indicationFactory, InstructionsFactory instructionsFactory,
            ParticipantRoleFactory participantRoleFactory, Performer2Factory performer2Factory) {
        this.ccdCodeFactory = ccdCodeFactory;
        this.substanceAdministrationFactory = substanceAdministrationFactory;
        this.observationFactory = observationFactory;
        this.indicationFactory = indicationFactory;
        this.instructionsFactory = instructionsFactory;
        this.participantRoleFactory = participantRoleFactory;
        this.performer2Factory = performer2Factory;
    }

    private Immunization parseImmunizationActivity(SubstanceAdministration substanceAdministration, Client resident) {
        if (!CcdParseUtils.hasContent(substanceAdministration)) {
            return null;
        }
        checkNotNull(resident);

        Immunization immunization = new Immunization();
        immunization.setClient(resident);
        immunization.setOrganization(resident.getOrganization());
        immunization.setLegacyId(CcdParseUtils.getFirstIdExtensionStr(substanceAdministration.getIds()));

        CD code = substanceAdministration.getCode();
        immunization.setCode(ccdCodeFactory.convert(code));
        immunization.setText(CcdTransform.EDtoString(substanceAdministration.getText(), immunization.getCode()));
        if (substanceAdministration.getStatusCode() != null) {
            immunization.setStatusCode(substanceAdministration.getStatusCode().getCode());
        }

        for (SXCM_TS sxcm_ts : substanceAdministration.getEffectiveTimes()) {
            if (sxcm_ts instanceof IVL_TS) {
                Pair<Date, Date> effectiveTimes = CcdTransform.IVLTStoHighLowDateOrTsToDate((IVL_TS) sxcm_ts);
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
                    substanceAdministrationFactory.parseImmunizationMedicationInformation(
                            substanceAdministration.getConsumable().getManufacturedProduct(), resident));
        }

        immunization.setDrugVehicles(
                participantRoleFactory.parseDrugVehicles(substanceAdministration.getParticipants(), resident));

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
                                parseImmunizationRefusalReason(ccdEntryRelationship.getObservation(), resident));
                    } else {
                        if (indications == null) {
                            indications = new ArrayList<>();
                        }
                        Indication indication = indicationFactory.parseIndication(observation, resident, LEGACY_TABLE);
                        indications.add(indication);
                    }
                    break;
                case SUBJ:
                    immunization.setInstructions(
                            instructionsFactory.parseInstructions(ccdEntryRelationship.getAct(), resident));
                    break;
                case REFR:
                    Supply ccdSupply = ccdEntryRelationship.getSupply();
                    if (ccdSupply.getMoodCode() == x_DocumentSubstanceMood.EVN) {
                        immunization.setMedicationDispense(substanceAdministrationFactory
                                .parseMedicationDispense(ccdSupply, resident, LEGACY_TABLE));
                    } else if (ccdSupply.getMoodCode() == x_DocumentSubstanceMood.INT) {
                        immunization.setMedicationSupplyOrder(substanceAdministrationFactory
                                .parseMedicationSupplyOrder(ccdSupply, resident, LEGACY_TABLE));
                    } else {
                        logger.warn(
                                "parseMedicationActivity : Unknown EntryRelationship of type REFR and Supply moodCode = "
                                        + ccdSupply.getMoodCode());
                    }
                    break;
                case CAUS:
                    immunization.setReactionObservation(observationFactory
                            .parseReactionObservation(ccdEntryRelationship.getObservation(), resident, LEGACY_TABLE));
                    break;
                }
            }
            immunization.setIndications(indications);
        }

        immunization.setPreconditions(substanceAdministrationFactory
                .parsePreconditions(substanceAdministration.getPreconditions(), resident));

        if (!CollectionUtils.isEmpty(substanceAdministration.getPerformers())) {
            Performer2 ccdPerformer2 = substanceAdministration.getPerformers().get(0);
            immunization
                    .setPerformer(performer2Factory.parsePerson(ccdPerformer2, resident.getOrganization(), LEGACY_TABLE));
        }

        return immunization;
    }

    private ImmunizationRefusalReason parseImmunizationRefusalReason(Observation ccdObservation, Client resident) {
        if (!CcdParseUtils.hasContent(ccdObservation) || resident == null) {
            return null;
        }

        ImmunizationRefusalReason immunizationRefusalReason = new ImmunizationRefusalReason();
        immunizationRefusalReason.setOrganization(resident.getOrganization());
        immunizationRefusalReason.setCode(ccdCodeFactory.convert(ccdObservation.getCode()));

        return immunizationRefusalReason;
    }

    @Override
    public boolean isSectionIgnored(ImmunizationsSectionEntriesOptional immunizationsSection) {
        return !CcdParseUtils.hasContent(immunizationsSection)
                || CollectionUtils.isEmpty(immunizationsSection.getSubstanceAdministrations());
    }

    @Override
    public List<Immunization> doParseSection(Client resident,
                                             ImmunizationsSectionEntriesOptional immunizationsSection) {
        Objects.requireNonNull(resident);

        var immunizations = immunizationsSection.getSubstanceAdministrations().stream()
                .map(ccdImmunizationSubstanceAdministration -> parseImmunizationActivity(ccdImmunizationSubstanceAdministration,
                        resident))
                .filter(this::filterImmunization)
                .collect(Collectors.toList());

        return immunizations;
    }

    private boolean filterImmunization(Immunization immunization) {
        return (immunization != null && immunization.getImmunizationMedicationInformation() != null
                && StringUtils.isNotEmpty(immunization.getImmunizationMedicationInformation().getText()));
    }
}
