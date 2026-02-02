package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.FunctionalStatus;
import com.scnsoft.eldermark.entity.assessment.AssessmentScaleObservation;
import com.scnsoft.eldermark.entity.assessment.AssessmentScaleSupportingObservation;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.StatusProblemObservation;
import com.scnsoft.eldermark.entity.document.ccd.Author;
import com.scnsoft.eldermark.entity.document.ccd.NonMedicinalSupplyActivity;
import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.AuthorFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.ObservationFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.ParticipantRoleFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.AbstractParsableSection;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSection;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.ParticipationType;
import org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.openhealthtools.mdht.uml.cda.consol.HighestPressureUlcerStage;
import org.openhealthtools.mdht.uml.cda.consol.NumberOfPressureUlcersObservation;
import org.openhealthtools.mdht.uml.cda.consol.PressureUlcerObservation;
import org.openhealthtools.mdht.uml.cda.consol.ProblemObservation;
import org.openhealthtools.mdht.uml.cda.consol.ResultObservation;
import org.openhealthtools.mdht.uml.cda.consol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <h1>Functional Status</h1>
 * “This section contains information on the “normal functioning” of the patient at the time the record
 * is created and provides an extensive list of examples.” Further, it states that deviation from normal
 * and limitations and improvements should be included here. [CCD 3.4]
 *
 * @see FunctionalStatus
 * @see StatusProblemObservation
 * @see StatusResultObservation
 * @see StatusResultOrganizer
 * @see HighestPressureUlcerStage
 * @see PressureUlcerObservation
 * @see NumberOfPressureUlcersObservation
 * @see NonMedicinalSupplyActivity
 * @see TargetSiteCode
 * @see CaregiverCharacteristic
 * @see AssessmentScaleObservation
 * @see AssessmentScaleSupportingObservation
 * @see Author
 * @see Client
 * @see CcdCode
 */
@Component("consol.FunctionalStatusParser")
public class FunctionalStatusParser extends AbstractParsableSection<FunctionalStatusResultOrganizer, FunctionalStatusSection, FunctionalStatus> implements ParsableSection<FunctionalStatusSection, FunctionalStatus> {

    private static final Logger logger = LoggerFactory.getLogger(FunctionalStatusParser.class);

    private final CcdCodeFactory ccdCodeFactory;
    private final ParticipantRoleFactory participantRoleFactory;
    private final ObservationFactory observationFactory;
    private final AuthorFactory authorFactory;

    @Autowired
    public FunctionalStatusParser(CcdCodeFactory ccdCodeFactory, ParticipantRoleFactory participantRoleFactory,
                                  ObservationFactory observationFactory, AuthorFactory authorFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
        this.participantRoleFactory = participantRoleFactory;
        this.observationFactory = observationFactory;
        this.authorFactory = authorFactory;
    }

    @Override
    public boolean isSectionIgnored(FunctionalStatusSection functionalStatusSection) {
        return !CcdParseUtils.hasContent(functionalStatusSection);
    }

    @Override
    public List<FunctionalStatus> doParseSection(Client resident, FunctionalStatusSection functionalStatusSection) {
        Objects.requireNonNull(resident);

        final FunctionalStatus functionalStatus = new FunctionalStatus();
        functionalStatus.setClient(resident);
        functionalStatus.setOrganization(resident.getOrganization());

        // TODO Iterate functionalStatusSection.getCognitiveStatusResultOrganizers() and functionalStatusSection.getFunctionalStatusResultOrganizers() instead ?
        // So there would be no need to check templateId below
        var functionalStatusResultOrganizers = functionalStatusSection.getFunctionalStatusResultOrganizers().stream()
                .map(ccdOrganizer -> parseResultOrganizer(ccdOrganizer, resident))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        functionalStatus.setFunctionalStatusResultOrganizers(functionalStatusResultOrganizers);

        var cognitiveStatusResultOrganizers = functionalStatusSection.getCognitiveStatusResultOrganizers().stream()
                .map(ccdOrganizer -> parseResultOrganizer(ccdOrganizer, resident))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        functionalStatus.setCognitiveStatusResultOrganizers(cognitiveStatusResultOrganizers);

        var functionalStatusProblemObservations = functionalStatusSection.getFunctionalStatusProblemObservations().stream()
                .map(ccdObservation -> parseProblemObservation(ccdObservation, resident))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        functionalStatus.setFunctionalStatusProblemObservations(functionalStatusProblemObservations);

        final List<StatusProblemObservation> cognitiveStatusProblemObservations = functionalStatusSection.getCognitiveStatusProblemObservations().stream()
                .map(ccdObservation -> parseProblemObservation(ccdObservation, resident))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        functionalStatus.setCognitiveStatusProblemObservations(cognitiveStatusProblemObservations);

        return new ArrayList<>(Collections.singleton(functionalStatus));
    }

    private StatusProblemObservation parseProblemObservation(ProblemObservation ccdObservation, Client resident) {
        if (!CcdParseUtils.hasContent(ccdObservation)) {
            return null;
        }
        checkNotNull(resident);

        final StatusProblemObservation statusProblemObservation = new StatusProblemObservation();
        statusProblemObservation.setOrganization(resident.getOrganization());

        final Pair<Date, Date> effectiveTimePair = CcdTransform.IVLTStoHighLowDateOrIVLTStoCenterDateOrTsToDate(ccdObservation.getEffectiveTime());
        if (effectiveTimePair != null) {
            statusProblemObservation.setTimeHigh(effectiveTimePair.getFirst());
            statusProblemObservation.setTimeLow(effectiveTimePair.getSecond());
        }
        statusProblemObservation.setValue(observationFactory.parseValueAsCode(ccdObservation));
        final ED observationText = ccdObservation.getText();
        statusProblemObservation.setText(CcdTransform.EDtoString(observationText, statusProblemObservation.getValue()));
        statusProblemObservation.setMethodCode(observationFactory.parseMethod(ccdObservation));
        statusProblemObservation.setNegationInd(ccdObservation.getNegationInd());
        // TODO check `resolved` on real data
        statusProblemObservation.setResolved(statusProblemObservation.getTimeHigh() != null);
        var activities = parseNonMedicalSupplyActivities(ccdObservation.getSupplies(), resident);
        statusProblemObservation.setNonMedicinalSupplyActivities(activities);

        if (!CollectionUtils.isEmpty(ccdObservation.getObservations())) {
            final List<AssessmentScaleObservation> assessmentScaleObservations = new ArrayList<>();
            final List<CaregiverCharacteristic> caregiverCharacteristics = new ArrayList<>();

            for (Observation observation : ccdObservation.getObservations()) {
                x_ActRelationshipEntryRelationship typeCode = ((EntryRelationship) observation.eContainer()).getTypeCode();
                switch (typeCode) {
                    case REFR:
                        caregiverCharacteristics.add(parseCaregiverCharacteristic(observation, resident));
                        break;
                    case COMP:
                        assessmentScaleObservations.add(parseAssessmentScaleObservation(observation, resident));
                        break;
                    default:
                        logger.warn("parseProblemObservation : StatusProblemObservation has unsupported " +
                                "EntryRelationship typeCode = " + typeCode + " and will be ignored.");
                }
            }

            statusProblemObservation.setAssessmentScaleObservations(assessmentScaleObservations);
            statusProblemObservation.setCaregiverCharacteristics(caregiverCharacteristics);
        }

        return statusProblemObservation;
    }

    private StatusResultOrganizer parseResultOrganizer(ResultOrganizer ccdOrganizer, Client resident) {
        if (!CcdParseUtils.hasContent(ccdOrganizer)) {
            return null;
        }
        checkNotNull(resident);

        StatusResultOrganizer statusResultOrganizer = new StatusResultOrganizer();
        statusResultOrganizer.setOrganization(resident.getOrganization());

        statusResultOrganizer.setCode(ccdCodeFactory.convert(ccdOrganizer.getCode()));
        if (CollectionUtils.isNotEmpty(ccdOrganizer.getObservations())) {
            final List<StatusResultObservation> statusResultObservations = new ArrayList<>();
            for (ResultObservation ccdObservation : ccdOrganizer.getResultObservations()) {
                final StatusResultObservation statusResultObservation = parseStatusResultObservation(ccdObservation, resident);
                if (statusResultObservation != null) {
                    statusResultObservations.add(statusResultObservation);
                }
            }
            statusResultOrganizer.setStatusResultObservations(statusResultObservations);
        }

        return statusResultOrganizer;
    }

    private StatusResultObservation parseStatusResultObservation(ResultObservation ccdResultObservation, Client resident) {
        if (!CcdParseUtils.hasContent(ccdResultObservation)) {
            return null;
        }
        checkNotNull(resident);

        StatusResultObservation statusResultObservation = new StatusResultObservation();
        statusResultObservation.setOrganization(resident.getOrganization());

        CD code = ccdResultObservation.getCode();
        statusResultObservation.setCode(ccdCodeFactory.convert(code));
        statusResultObservation.setText(CcdTransform.EDtoString(ccdResultObservation.getText(), ccdResultObservation.getStatusCode().getCode()));

        statusResultObservation.setEffectiveTime(CcdTransform.IVLTStoCenterDateOrTsToDate(ccdResultObservation.getEffectiveTime()));

        final ANY ccdObservationValue = ObservationFactory.getValue(ccdResultObservation, ANY.class);
        if (ccdObservationValue instanceof CD) {
            statusResultObservation.setValueCode(ccdCodeFactory.convert((CD) ccdObservationValue));
        } else if (ccdObservationValue instanceof PQ) {
            PQ pqValue = (PQ) ccdObservationValue;
            statusResultObservation.setValue(pqValue.getValue() != null ? pqValue.getValue().toString() : null);
        }

        statusResultObservation.setInterpretationCodes(ccdCodeFactory.convertInterpretationCodes(ccdResultObservation));

        statusResultObservation.setMethodCode(observationFactory.parseMethod(ccdResultObservation));
        statusResultObservation.setTargetSiteCode(observationFactory.parseTargetSite(ccdResultObservation));
        if (!CollectionUtils.isEmpty(ccdResultObservation.getAuthors())) {
            statusResultObservation.setAuthor(authorFactory.parseAuthor(ccdResultObservation.getAuthors().get(0),
                    resident, "FunctionalStatus_NWHIN"));
        }

        statusResultObservation.setReferenceRanges(ObservationFactory.parseReferenceRanges(
                ccdResultObservation.getReferenceRanges()));

        final List<NonMedicinalSupplyActivity> activities = parseNonMedicalSupplyActivities(ccdResultObservation.getSupplies(), resident);
        statusResultObservation.setNonMedicinalSupplyActivities(activities);

        if (!CollectionUtils.isEmpty(ccdResultObservation.getObservations())) {
            List<AssessmentScaleObservation> assessmentScaleObservations = new ArrayList<>();
            List<CaregiverCharacteristic> caregiverCharacteristics = new ArrayList<>();

            for (Observation observation : ccdResultObservation.getObservations()) {
                x_ActRelationshipEntryRelationship typeCode = ((EntryRelationship) observation.eContainer()).getTypeCode();
                switch (typeCode) {
                    case REFR:
                        caregiverCharacteristics.add(parseCaregiverCharacteristic(observation, resident));
                        break;
                    case COMP:
                        assessmentScaleObservations.add(parseAssessmentScaleObservation(observation, resident));
                        break;
                    default:
                        logger.warn("parseStatusResultObservation : StatusResultObservation has unsupported " +
                                "EntryRelationship typeCode = " + typeCode + " and will be ignored.");
                }
            }

            statusResultObservation.setAssessmentScaleObservations(assessmentScaleObservations);
            statusResultObservation.setCaregiverCharacteristics(caregiverCharacteristics);
        }

        return statusResultObservation;
    }

    private AssessmentScaleObservation parseAssessmentScaleObservation(Observation ccdObservation, Client resident) {
        if (!CcdParseUtils.hasContent(ccdObservation) || resident == null) {
            return null;
        }

        AssessmentScaleObservation assessmentScaleObservation = new AssessmentScaleObservation();
        assessmentScaleObservation.setOrganization(resident.getOrganization());
        assessmentScaleObservation.setCode(ccdCodeFactory.convert(ccdObservation.getCode()));

        assessmentScaleObservation.setDerivationExpr(CcdTransform.EDtoString(ccdObservation.getDerivationExpr(), assessmentScaleObservation.getCode()));

        IVL_TS observationEffectiveTime = ccdObservation.getEffectiveTime();
        assessmentScaleObservation.setEffectiveTime(CcdTransform.IVLTStoCenterDateOrTsToDate(observationEffectiveTime));

        INT assessmentValue = CcdParseUtils.getFirstNotEmptyValue(ccdObservation.getValues(), INT.class);
        assessmentScaleObservation.setValue(CcdTransform.INTtoInteger(assessmentValue));

        assessmentScaleObservation.setInterpretationCodes(ccdCodeFactory.convertInterpretationCodes(ccdObservation));

        if (CollectionUtils.isNotEmpty(ccdObservation.getAuthors())) {
            var authors = ccdObservation.getAuthors()
                    .stream()
                    .map(author -> authorFactory.parseAuthor(author, resident, "FunctionalStatus_NWHIN"))
                    .collect(Collectors.toList());
            assessmentScaleObservation.setAuthors(authors);
        }

        if (ccdObservation.getObservations() != null) {
            List<AssessmentScaleSupportingObservation> supportingObservations = new ArrayList<>();
            for (Observation ccdSupportingObservation : ccdObservation.getObservations()) {
                AssessmentScaleSupportingObservation assessmentScaleSupportingObservation = new AssessmentScaleSupportingObservation();
                assessmentScaleSupportingObservation.setAssessmentScaleObservation(assessmentScaleObservation);
                assessmentScaleSupportingObservation.setOrganization(resident.getOrganization());
                assessmentScaleSupportingObservation.setCode(ccdCodeFactory.convert(ccdSupportingObservation.getCode()));

                CD observationValue = ObservationFactory.getValue(ccdSupportingObservation, CD.class);
                if (observationValue != null) {
                    assessmentScaleSupportingObservation.setValueCode(ccdCodeFactory.convert(observationValue));
                } else {
                    INT intValue = ObservationFactory.getValue(ccdSupportingObservation, INT.class);
                    assessmentScaleSupportingObservation.setIntValue(CcdTransform.INTtoInteger(intValue));
                }
            }

            assessmentScaleObservation.setAssessmentScaleSupportingObservations(supportingObservations);
        }

        assessmentScaleObservation.setObservationRanges(ObservationFactory.parseReferenceRanges(
                ccdObservation.getReferenceRanges()));

        return assessmentScaleObservation;
    }

    private CaregiverCharacteristic parseCaregiverCharacteristic(Observation ccdObservation, Client resident) {
        if (!CcdParseUtils.hasContent(ccdObservation) || resident == null) {
            return null;
        }

        CaregiverCharacteristic caregiverCharacteristic = new CaregiverCharacteristic();
        caregiverCharacteristic.setOrganization(resident.getOrganization());
        caregiverCharacteristic.setCode(ccdCodeFactory.convert(ccdObservation.getCode()));

        CD observationValue = ObservationFactory.getValue(ccdObservation, CD.class);
        caregiverCharacteristic.setValue(ccdCodeFactory.convert(observationValue));

        for (Participant2 participant2 : ccdObservation.getParticipants()) {
            if (participant2.getTypeCode() == ParticipationType.IND) {
                Pair<Date, Date> effectiveTime = CcdTransform.IVLTStoHighLowDate(participant2.getTime());
                if (effectiveTime != null && participant2.getParticipantRole() != null) {
                    caregiverCharacteristic.setParticipantTimeHigh(effectiveTime.getFirst());
                    caregiverCharacteristic.setParticipantTimeLow(effectiveTime.getSecond());
                    caregiverCharacteristic.setParticipantRoleCode(ccdCodeFactory.convert(participant2.getParticipantRole().getCode()));
                    break;
                }
            }
        }

        return caregiverCharacteristic;
    }

    private List<NonMedicinalSupplyActivity> parseNonMedicalSupplyActivities(EList<Supply> supplies, Client resident) {
        if (CollectionUtils.isEmpty(supplies)) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final List<NonMedicinalSupplyActivity> nonMedicinalSupplyActivities = new ArrayList<>();
        for (Supply ccdSupply : supplies) {
            final NonMedicinalSupplyActivity nonMedicinalSupplyActivity = parseNonMedicalSupplyActivity(ccdSupply, resident);
            nonMedicinalSupplyActivities.add(nonMedicinalSupplyActivity);
        }
        return nonMedicinalSupplyActivities;
    }

    private NonMedicinalSupplyActivity parseNonMedicalSupplyActivity(Supply ccdSupply, Client resident) {
        if (!CcdParseUtils.hasContent(ccdSupply)) {
            return null;
        }
        checkNotNull(resident);

        final NonMedicinalSupplyActivity nonMedicinalSupplyActivity = new NonMedicinalSupplyActivity();
        nonMedicinalSupplyActivity.setOrganization(resident.getOrganization());

        nonMedicinalSupplyActivity.setStatusCode(ccdSupply.getCode() != null ? ccdSupply.getCode().getCode() : null);
        if (!CollectionUtils.isEmpty(ccdSupply.getEffectiveTimes())) {
            nonMedicinalSupplyActivity.setEffectiveTimeHigh(CcdParseUtils.convertTsToDate(ccdSupply.getEffectiveTimes().get(0)));
        }
        nonMedicinalSupplyActivity.setQuantity(ccdSupply.getQuantity() != null ? ccdSupply.getQuantity().getValue() : null);
        if (!CollectionUtils.isEmpty(ccdSupply.getParticipants())) {
            ParticipantRole ccdParticipantRole = ccdSupply.getParticipants().get(0).getParticipantRole();
            nonMedicinalSupplyActivity.setProductInstance(participantRoleFactory.parseProductInstance(ccdParticipantRole, resident));
        }
        return nonMedicinalSupplyActivity;
    }

}