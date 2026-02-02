package com.scnsoft.eldermark.services.ccd.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.Author;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.ObservationFactory;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.ParsableSectionFactory;
import com.scnsoft.eldermark.services.cda.templates.RequiredTemplateFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.ccd.*;
import org.openhealthtools.mdht.uml.cda.ccd.ProblemObservation;
import org.openhealthtools.mdht.uml.cda.ccd.ResultObservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.scnsoft.eldermark.entity.CodeSystem.LOINC;
import static com.scnsoft.eldermark.entity.CodeSystem.SNOMED_CT;

/**
 * <h1>Functional Status</h1>
 * “This section contains information on the “normal functioning” of the patient at the time the record
 * is created and provides an extensive list of examples.” Further, it states that deviation from normal
 * and limitations and improvements should be included here. [CCD 3.4]
 * <br/><br/>
 * Functional Statuses can be expressed in 3 different forms. They can occur as a Problem, a Result or as text.
 * Text can be employed if and only if the Functional Status is neither a Problem nor a Result.
 * Functional Statuses expressed as Problems include relevant clinical conditions, diagnoses, symptoms and findings.
 * Results are the interpretation or conclusion derived from a clinical assessment or test battery, such as
 * the Instrumental Activities of Daily Living (IADL) scale or the Functional Status Index (FSI).
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
 * @see Resident
 * @see CcdCode
 */
@Component
public class FunctionalStatusFactory extends RequiredTemplateFactory implements ParsableSectionFactory<FunctionalStatusSection, FunctionalStatus> {

    private static final Logger logger = LoggerFactory.getLogger(FunctionalStatusFactory.class);

    @Autowired
    private CcdCodeFactory ccdCodeFactory;
    @Autowired
    private ObservationFactory observationFactory;

    @Autowired
    private SectionEntryParseFactory sectionEntryParseFactory;

    private static final String FUNCTIONAL_STATUS_RESULT_TEMPLATE_ID = "2.16.840.1.113883.10.20.1.32";
    private static final String FUNCTIONAL_STATUS_RESULT_OBSERVATION_TEMPLATE_ID = "2.16.840.1.113883.10.20.1.31";
    private static final String FUNCTIONAL_STATUS_PROBLEM_OBSERVATION_TEMPLATE_ID = "2.16.840.1.113883.10.20.1.28";
    private static final String COGNITIVE_STATUS_PROBLEM_OBSERVATION_TEMPLATE_ID = "2.16.840.1.113883.10.20.1.28";
    private static final String COGNITIVE_STATUS_RESULT_OBSERVATION_TEMPLATE_ID = "2.16.840.1.113883.10.20.1.31";
    private static final String COGNITIVE_STATUS_RESULT_TEMPLATE_ID = "2.16.840.1.113883.10.20.1.32";

    @Override
    public FunctionalStatusSection buildTemplateInstance(Collection<FunctionalStatus> functionalStatuses) {
        if (CollectionUtils.isEmpty(functionalStatuses)) {
            return null;
        }

        final FunctionalStatusSection functionalStatusSection = CCDFactory.eINSTANCE.createFunctionalStatusSection();
        functionalStatusSection.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.1.5"));
        functionalStatusSection.setCode(CcdUtils.createCE("47420-5", "Functional Status Assessment", LOINC));

        functionalStatusSection.setTitle(DatatypesFactory.eINSTANCE.createST("Functional Status"));

        functionalStatusSection.createStrucDocText(buildSectionText(functionalStatuses));

        for (FunctionalStatus functionalStatus : functionalStatuses) {
            // FIXME this code for functional status section generation is not verified using HL7 CCD specification. It's highly likely that it produces a bad Functional Status section.
            // TODO validate templateIds and structure
            if (functionalStatus.getFunctionalStatusResultOrganizers() != null) {
                for (StatusResultOrganizer functionalStatusResultOrganizer : functionalStatus.getFunctionalStatusResultOrganizers()) {
                    functionalStatusSection.addOrganizer(createResultOrganizer(functionalStatusResultOrganizer, true));
                }
            }
            if (functionalStatus.getCognitiveStatusResultOrganizers() != null) {
                for (StatusResultOrganizer cognitiveStatusResultOrganizer : functionalStatus.getCognitiveStatusResultOrganizers()) {
                    functionalStatusSection.addOrganizer(createResultOrganizer(cognitiveStatusResultOrganizer, false));
                }
            }

            if (functionalStatus.getFunctionalStatusResultObservations() != null) {
                for (StatusResultObservation functionalStatusResultObservation : functionalStatus.getFunctionalStatusResultObservations()) {
                    functionalStatusSection.addObservation(createStatusResultObservation(functionalStatusResultObservation, true));
                }
            }

            if (functionalStatus.getCognitiveStatusResultObservations() != null) {
                for (StatusResultObservation cognitiveStatusResultObservation : functionalStatus.getCognitiveStatusResultObservations()) {
                    functionalStatusSection.addObservation(createStatusResultObservation(cognitiveStatusResultObservation, false));
                }
            }

            if (functionalStatus.getFunctionalStatusProblemObservations() != null) {
                for (StatusProblemObservation functionalStatusProblemObservation : functionalStatus.getFunctionalStatusProblemObservations()) {
                    functionalStatusSection.addObservation(createStatusProblemObservation(functionalStatusProblemObservation, true));
                }
            }

            if (functionalStatus.getCognitiveStatusProblemObservations() != null) {
                for (StatusProblemObservation cognitiveStatusProblemObservation : functionalStatus.getCognitiveStatusProblemObservations()) {
                    functionalStatusSection.addObservation(createStatusProblemObservation(cognitiveStatusProblemObservation, false));
                }
            }

            if (functionalStatus.getNonMedicinalSupplyActivities() != null) {
                for (NonMedicinalSupplyActivity nonMedicinalSupplyActivity : functionalStatus.getNonMedicinalSupplyActivities()) {
                    SupplyActivity supplyActivity = createNonMedicalSupplyActivity(nonMedicinalSupplyActivity);
                    functionalStatusSection.addSupply(supplyActivity);
                }
            }

            if (functionalStatus.getCaregiverCharacteristics() != null) {
                for (CaregiverCharacteristic caregiverCharacteristic : functionalStatus.getCaregiverCharacteristics()) {
                    Observation caregiverCharacteristicCcd = createCaregiverCharacteristic(caregiverCharacteristic);
                    functionalStatusSection.addObservation(caregiverCharacteristicCcd);
                }
            }

            if (functionalStatus.getAssessmentScaleObservations() != null) {
                for (AssessmentScaleObservation assessmentScaleObservation : functionalStatus.getAssessmentScaleObservations()) {
                    Observation assessmentScaleObservationCcd = createAssessmentScaleObservation(assessmentScaleObservation);
                    functionalStatusSection.addObservation(assessmentScaleObservationCcd);
                }
            }

            if (functionalStatus.getPressureUlcerObservations() != null) {
                for (PressureUlcerObservation pressureUlcerObservation : functionalStatus.getPressureUlcerObservations()) {
                    functionalStatusSection.addObservation(createPressureUlcerObservation(pressureUlcerObservation));
                }
            }


            if (functionalStatus.getNumberOfPressureUlcersObservations() != null) {
                for (NumberOfPressureUlcersObservation numberOfPressureUlcersObservation : functionalStatus.getNumberOfPressureUlcersObservations()) {
                    Observation numberOfPressureUlcersObservationCcd = createObservation();
                    functionalStatusSection.addObservation(numberOfPressureUlcersObservationCcd);

                    numberOfPressureUlcersObservationCcd.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.76"));
                    numberOfPressureUlcersObservationCcd.getIds().add(CcdUtils.getId(numberOfPressureUlcersObservation.getId()));
                    numberOfPressureUlcersObservationCcd.setCode(CcdUtils.createCD("2264892003", "number of pressure ulcers", SNOMED_CT));
                    numberOfPressureUlcersObservationCcd.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));
                    numberOfPressureUlcersObservationCcd.setEffectiveTime(CcdUtils.convertEffectiveTime(numberOfPressureUlcersObservation.getEffectiveTime()));

                    INT intVal = DatatypesFactory.eINSTANCE.createINT();
                    if (numberOfPressureUlcersObservation.getValue() != null) {
                        intVal.setValue(numberOfPressureUlcersObservation.getValue());
                    } else {
                        intVal.setNullFlavor(NullFlavor.NI);
                    }
                    numberOfPressureUlcersObservationCcd.getValues().add(intVal);

                    if (numberOfPressureUlcersObservation.getAuthor() != null) {
                        numberOfPressureUlcersObservationCcd.getAuthors().add(SectionEntryFactory.buildAuthor(numberOfPressureUlcersObservation.getAuthor()));
                    }
                    Observation observation = createObservation();
                    numberOfPressureUlcersObservationCcd.addObservation(observation);
                    ((EntryRelationship) observation.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
                    observation.setCode(CcdUtils.createNillCode()); // Added to pass validation on https://www.lantanagroup.com/validator
                    observation.getValues().add(CcdUtils.createCD(numberOfPressureUlcersObservation.getObservationValue(), SNOMED_CT.getOid()));
                }
            }

            if (functionalStatus.getHighestPressureUlcerStages() != null) {
                for (HighestPressureUlcerStage highestPressureUlcerStage : functionalStatus.getHighestPressureUlcerStages()) {
                    Observation highestPressureUlcerStageObservationCcd = createObservation();
                    functionalStatusSection.addObservation(highestPressureUlcerStageObservationCcd);
                    highestPressureUlcerStageObservationCcd.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.77"));
                    highestPressureUlcerStageObservationCcd.getIds().add(CcdUtils.getId(highestPressureUlcerStage.getId()));
                    highestPressureUlcerStageObservationCcd.setCode(CcdUtils.createCD("420905001", "Highest Pressure Ulcer Stage", SNOMED_CT));
                    highestPressureUlcerStageObservationCcd.getValues().add(CcdUtils.createCD(highestPressureUlcerStage.getValue(), SNOMED_CT.getOid()));
                }
            }
        }

        return functionalStatusSection;
    }

    private static String buildSectionText(Collection<FunctionalStatus> functionalStatuses) {

        if (CollectionUtils.isEmpty(functionalStatuses)) {
            return "Functional and Cognitive Assessment";
        }

        StringBuilder sectionText = new StringBuilder();

        sectionText.append("<table>");
        sectionText.append("<thead>");
        sectionText.append("<tr>");
        sectionText.append("<th>Functional and Cognitive Assessment</th>");
        sectionText.append("<th>Date</th>");
        sectionText.append("<th>Value</th>");
        sectionText.append("</tr>");
        sectionText.append("</thead>");
        sectionText.append("<tbody>");
        for (FunctionalStatus functionalStatus : functionalStatuses) {
            if (functionalStatus.getFunctionalStatusResultOrganizers() != null) {
                for (StatusResultOrganizer statusResultOrganizer : functionalStatus.getFunctionalStatusResultOrganizers()) {
                    addStatusResultObservationTable(statusResultOrganizer.getStatusResultObservations(), sectionText);
                }
            }

            if (functionalStatus.getCognitiveStatusResultOrganizers() != null) {
                for (StatusResultOrganizer statusResultOrganizer : functionalStatus.getCognitiveStatusResultOrganizers()) {
                    addStatusResultObservationTable(statusResultOrganizer.getStatusResultObservations(), sectionText);
                }
            }

            addStatusResultObservationTable(functionalStatus.getFunctionalStatusResultObservations(), sectionText);
            addStatusResultObservationTable(functionalStatus.getCognitiveStatusResultObservations(), sectionText);

            addStatusProblemObservationTable(functionalStatus.getFunctionalStatusProblemObservations(), sectionText);
            addStatusProblemObservationTable(functionalStatus.getCognitiveStatusProblemObservations(), sectionText);

            if (functionalStatus.getPressureUlcerObservations() != null) {
                for (PressureUlcerObservation pressureUlcerObservation : functionalStatus.getPressureUlcerObservations()) {
                    CcdUtils.addReferenceCell(PressureUlcerObservation.class.getSimpleName() + pressureUlcerObservation.getId(),
                            pressureUlcerObservation.getText(), sectionText);
                    CcdUtils.addDateCell(pressureUlcerObservation.getEffectiveTime(), sectionText);
                    CcdUtils.addCellToSectionText(pressureUlcerObservation.getValue(), sectionText);
                }
            }
        }

        sectionText.append("</tbody>");
        sectionText.append("</table>");

        return sectionText.toString();
    }

    private static void addStatusProblemObservationTable(List<StatusProblemObservation> statusProblemObservations, StringBuilder sectionText) {
        if (statusProblemObservations != null) {
            for (StatusProblemObservation statusProblemObservation : statusProblemObservations) {
                sectionText.append("<tr>");
                CcdUtils.addReferenceCell(StatusProblemObservation.class.getSimpleName() + statusProblemObservation.getId(),
                        statusProblemObservation.getText(), sectionText);
                CcdUtils.addDateRangeCell(statusProblemObservation.getTimeLow(), statusProblemObservation.getTimeHigh(), sectionText);
                CcdUtils.addCellToSectionText(statusProblemObservation.getValue(), sectionText);
                sectionText.append("</tr>");
            }
        }
    }

    private static void addStatusResultObservationTable(List<StatusResultObservation> statusResultObservations, StringBuilder sectionText) {
        if (statusResultObservations != null) {
            for (StatusResultObservation statusResultObservation : statusResultObservations) {
                sectionText.append("<tr>");
                CcdUtils.addReferenceCell(StatusResultObservation.class.getSimpleName() + statusResultObservation.getId(),
                        statusResultObservation.getText(), sectionText);
                CcdUtils.addDateCell(statusResultObservation.getEffectiveTime(), sectionText);
                if (statusResultObservation.getValueCode() != null) {
                    CcdUtils.addCellToSectionText(statusResultObservation.getValueCode().getDisplayName(), sectionText);
                } else {
                    CcdUtils.addCellToSectionText(statusResultObservation.getValue(), sectionText);
                }
                sectionText.append("</tr>");
            }
        }
    }


    private static Observation createObservation() {
        final Observation observation = CDAFactory.eINSTANCE.createObservation();
        observation.setClassCode(ActClassObservation.OBS);
        observation.setMoodCode(x_ActMoodDocumentObservation.EVN);
        return observation;
    }


    private ResultOrganizer createResultOrganizer(StatusResultOrganizer functionalStatusResultOrganizer, boolean functional) {
        ResultOrganizer resultOrganizerCcd = CCDFactory.eINSTANCE.createResultOrganizer();
        resultOrganizerCcd.setClassCode(x_ActClassDocumentEntryOrganizer.CLUSTER);
        resultOrganizerCcd.setMoodCode(ActMood.EVN);
        String templateId = functional ? FUNCTIONAL_STATUS_RESULT_TEMPLATE_ID : COGNITIVE_STATUS_RESULT_TEMPLATE_ID;
        resultOrganizerCcd.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(templateId));
        resultOrganizerCcd.getIds().add(CcdUtils.getId(functionalStatusResultOrganizer.getId()));

        resultOrganizerCcd.setCode(CcdUtils.createCD(functionalStatusResultOrganizer.getCode()));

        resultOrganizerCcd.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

        List<StatusResultObservation> resultObservations = functionalStatusResultOrganizer.getStatusResultObservations();

        if (!CollectionUtils.isEmpty(resultObservations)) {
            for (StatusResultObservation resultObservation : resultObservations) {
                resultOrganizerCcd.addObservation(createStatusResultObservation(resultObservation, functional));
            }
        } else {
            resultOrganizerCcd.addObservation(createNullStatusResultObservation(functional));
        }

        return resultOrganizerCcd;
    }

    private ProblemObservation createStatusProblemObservation(StatusProblemObservation problemObservation, boolean functional) {
        ProblemObservation problemObservationCcd = CCDFactory.eINSTANCE.createProblemObservation();
        problemObservationCcd.setClassCode(ActClassObservation.OBS);
        problemObservationCcd.setMoodCode(x_ActMoodDocumentObservation.EVN);

        if (problemObservation.isNegationInd() != null && problemObservation.isNegationInd()) {
            problemObservationCcd.setNegationInd(problemObservation.isNegationInd());
        }
        String templateId = functional ? FUNCTIONAL_STATUS_PROBLEM_OBSERVATION_TEMPLATE_ID : COGNITIVE_STATUS_PROBLEM_OBSERVATION_TEMPLATE_ID;
        problemObservationCcd.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(templateId));
        problemObservationCcd.getIds().add(CcdUtils.getId(problemObservation.getId()));

        if (functional) {
            problemObservationCcd.setCode(CcdUtils.createCD("248536006", "Finding of Functional Performance and activity", CodeSystem.SNOMED_CT));
        } else {
            problemObservationCcd.setCode(CcdUtils.createCD("373930000", "Cognitive function finding", CodeSystem.SNOMED_CT));
        }

        if (problemObservation.getText() != null) {
            problemObservationCcd.setText(CcdUtils.createText(StatusProblemObservation.class.getSimpleName(), problemObservation.getId()));
        }

        problemObservationCcd.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));


        if (problemObservation.getTimeLow() != null || problemObservation.getTimeHigh() != null || problemObservation.isResolved()) {
            IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
            if (problemObservation.getTimeLow() != null) {
                IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
                low.setValue(CcdUtils.formatSimpleDate(problemObservation.getTimeLow()));
                effectiveTime.setLow(low);
            }
            if (problemObservation.getTimeHigh() != null || problemObservation.isResolved()) {
                IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
                if (problemObservation.getTimeHigh() != null) {
                    high.setValue(CcdUtils.formatSimpleDate(problemObservation.getTimeHigh()));
                } else {
                    high.setNullFlavor(NullFlavor.UNK);
                }
                effectiveTime.setHigh(high);
            }
            problemObservationCcd.setEffectiveTime(effectiveTime);
        }

        CD valueCode;
        if (problemObservation.getValue() != null) {
            valueCode = CcdUtils.createCD(problemObservation.getValue());
            // (CONF:10142)
            // If the code is something other than SNOMED, @nullFlavor SHOULD be “OTH” and the other code SHOULD be placed in the translation element
            if (SNOMED_CT.getOid().equals(valueCode.getCodeSystem()) || !functional) {
                problemObservationCcd.getValues().add(valueCode);
            } else {
                CD othCode = DatatypesFactory.eINSTANCE.createCD();
                othCode.getTranslations().add(valueCode);
                othCode.setNullFlavor(NullFlavor.OTH);
                problemObservationCcd.getValues().add(othCode);
            }
        } else {
            valueCode = DatatypesFactory.eINSTANCE.createCD();
            valueCode.setNullFlavor(NullFlavor.UNK);
            problemObservationCcd.getValues().add(valueCode);
        }

        if (problemObservation.getMethodCode() != null) {
            problemObservationCcd.getMethodCodes().add(CcdUtils.createCE(problemObservation.getMethodCode()));
        }

        if (problemObservation.getNonMedicinalSupplyActivities() != null) {
            for (NonMedicinalSupplyActivity nonMedicinalSupplyActivity : problemObservation.getNonMedicinalSupplyActivities()) {
                SupplyActivity supplyActivity = createNonMedicalSupplyActivity(nonMedicinalSupplyActivity);
                problemObservationCcd.addSupply(supplyActivity);
                ((EntryRelationship) supplyActivity.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.REFR);
            }
        }

        if (problemObservation.getCaregiverCharacteristics() != null) {
            for (CaregiverCharacteristic caregiverCharacteristic : problemObservation.getCaregiverCharacteristics()) {
                Observation caregiverCharacteristicCcd = createCaregiverCharacteristic(caregiverCharacteristic);
                problemObservationCcd.addObservation(caregiverCharacteristicCcd);
                ((EntryRelationship) caregiverCharacteristicCcd.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.REFR);
            }
        }

        if (problemObservation.getAssessmentScaleObservations() != null) {
            for (AssessmentScaleObservation assessmentScaleObservation : problemObservation.getAssessmentScaleObservations()) {
                Observation assessmentScaleObservationCcd = createAssessmentScaleObservation(assessmentScaleObservation);
                problemObservationCcd.addObservation(assessmentScaleObservationCcd);
                ((EntryRelationship) assessmentScaleObservationCcd.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.COMP);
            }
        }


        return problemObservationCcd;
    }

    private static ResultObservation createStatusResultObservation(StatusResultObservation resultObservation, boolean functional) {
        ResultObservation resultObservationCcd = CCDFactory.eINSTANCE.createResultObservation();
        resultObservationCcd.setClassCode(ActClassObservation.OBS);
        resultObservationCcd.setMoodCode(x_ActMoodDocumentObservation.EVN);
        String templateId = functional ? FUNCTIONAL_STATUS_RESULT_OBSERVATION_TEMPLATE_ID : COGNITIVE_STATUS_RESULT_OBSERVATION_TEMPLATE_ID;
        resultObservationCcd.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(templateId));
        resultObservationCcd.getIds().add(CcdUtils.getId(resultObservation.getId()));

        String codeSystem = functional ? LOINC.getOid() : SNOMED_CT.getOid();
        resultObservationCcd.setCode(CcdUtils.createCE(resultObservation.getCode(), codeSystem));

        if (functional) {
            resultObservationCcd.setCode(CcdUtils.createCE(resultObservation.getCode(), LOINC.getOid()));
        } else {
            resultObservationCcd.setCode(CcdUtils.createCD("373930000", "Cognitive function finding", CodeSystem.SNOMED_CT));
        }


        if (resultObservation.getText() != null) {
            resultObservationCcd.setText(CcdUtils.createText(StatusResultObservation.class.getSimpleName(), resultObservation.getId()));
        }

        resultObservationCcd.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

        resultObservationCcd.setEffectiveTime(CcdUtils.convertEffectiveTime(resultObservation.getEffectiveTime()));


        if (resultObservation.getValueCode() != null) {
            resultObservationCcd.getValues().add(CcdUtils.createCD(resultObservation.getValueCode(), SNOMED_CT.getOid()));
        } else if (resultObservation.getValue() != null) {
            try {
                resultObservationCcd.getValues().add(DatatypesFactory.eINSTANCE.createPQ(
                        Double.parseDouble(resultObservation.getValue()), resultObservation.getValueUnit()));
            } catch (NumberFormatException e) {
                logger.error("Value of Result Observation ID=" + resultObservation.getId() + " has wrong format", e);
                resultObservationCcd.getValues().add(CcdUtils.createNillCode());
            }
        } else {
            resultObservationCcd.getValues().add(CcdUtils.createNillCode());
        }

        CcdUtils.setInterpretationCodes(resultObservationCcd, resultObservation.getInterpretationCodes());

        if (resultObservation.getMethodCode() != null) {
            resultObservationCcd.getMethodCodes().add(CcdUtils.createCE(resultObservation.getMethodCode()));
        }

        if (resultObservation.getTargetSiteCode() != null) {
            resultObservationCcd.getTargetSiteCodes().add(CcdUtils.createCE(resultObservation.getTargetSiteCode()));
        }


        if (resultObservation.getAuthor() != null) {
            resultObservationCcd.getAuthors().add(SectionEntryFactory.buildAuthor(resultObservation.getAuthor()));
        }

        SectionEntryFactory.initReferenceRanges(resultObservationCcd, resultObservation.getReferenceRanges());//, "sro" + resultObservation.getId());

        if (resultObservation.getNonMedicinalSupplyActivities() != null) {
            for (NonMedicinalSupplyActivity nonMedicinalSupplyActivity : resultObservation.getNonMedicinalSupplyActivities()) {
                SupplyActivity supplyActivity = createNonMedicalSupplyActivity(nonMedicinalSupplyActivity);
                resultObservationCcd.addSupply(supplyActivity);
                ((EntryRelationship) supplyActivity.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.REFR);
            }
        }

        if (resultObservation.getCaregiverCharacteristics() != null) {
            for (CaregiverCharacteristic caregiverCharacteristic : resultObservation.getCaregiverCharacteristics()) {
                Observation caregiverCharacteristicCcd = createCaregiverCharacteristic(caregiverCharacteristic);
                resultObservationCcd.addObservation(caregiverCharacteristicCcd);
                ((EntryRelationship) caregiverCharacteristicCcd.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.REFR);
            }
        }

        if (resultObservation.getAssessmentScaleObservations() != null) {
            for (AssessmentScaleObservation assessmentScaleObservation : resultObservation.getAssessmentScaleObservations()) {
                Observation assessmentScaleObservationCcd = createAssessmentScaleObservation(assessmentScaleObservation);
                resultObservationCcd.addObservation(assessmentScaleObservationCcd);
                ((EntryRelationship) assessmentScaleObservationCcd.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.COMP);
            }
        }

        return resultObservationCcd;
    }

    private static ResultObservation createNullStatusResultObservation(boolean functional) {
        ResultObservation resultObservationCcd = CCDFactory.eINSTANCE.createResultObservation();
        resultObservationCcd.setClassCode(ActClassObservation.OBS);
        resultObservationCcd.setMoodCode(x_ActMoodDocumentObservation.EVN);
        String templateId = functional ? FUNCTIONAL_STATUS_RESULT_OBSERVATION_TEMPLATE_ID : COGNITIVE_STATUS_RESULT_OBSERVATION_TEMPLATE_ID;
        resultObservationCcd.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(templateId));

        resultObservationCcd.getIds().add(CcdUtils.getNullId());
        if (functional) {
            resultObservationCcd.setCode(CcdUtils.createNillCode());
        } else {
            resultObservationCcd.setCode(CcdUtils.createCD("373930000", "Cognitive function finding", CodeSystem.SNOMED_CT));
        }

        resultObservationCcd.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));
        resultObservationCcd.setEffectiveTime(CcdUtils.getNullEffectiveTime());
        resultObservationCcd.getValues().add(CcdUtils.createNillCode());

        return resultObservationCcd;
    }

    private static Observation createAssessmentScaleObservation(AssessmentScaleObservation assessmentScaleObservation) {
        Observation assessmentScaleObservationCcd = CDAFactory.eINSTANCE.createObservation();
        assessmentScaleObservationCcd.setClassCode(ActClassObservation.OBS);
        assessmentScaleObservationCcd.setMoodCode(x_ActMoodDocumentObservation.EVN);
        assessmentScaleObservationCcd.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.69"));
        assessmentScaleObservationCcd.getIds().add(CcdUtils.getId(assessmentScaleObservation.getId()));
        assessmentScaleObservationCcd.setCode(CcdUtils.createCD(assessmentScaleObservation.getCode()));

        if (assessmentScaleObservation.getDerivationExpr() != null) {
            assessmentScaleObservationCcd.setDerivationExpr(DatatypesFactory.eINSTANCE.createST(assessmentScaleObservation.getDerivationExpr()));
        }
        assessmentScaleObservationCcd.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

        assessmentScaleObservationCcd.setEffectiveTime(CcdUtils.convertEffectiveTime(assessmentScaleObservation.getEffectiveTime()));

        INT assessmentValue = DatatypesFactory.eINSTANCE.createINT();
        if (assessmentScaleObservation.getValue() != null) {
            assessmentValue.setValue(assessmentScaleObservation.getValue());
        } else {
            assessmentValue.setNullFlavor(NullFlavor.NI);
        }
        assessmentScaleObservationCcd.getValues().add(assessmentValue);

        CcdUtils.setInterpretationCodes(assessmentScaleObservationCcd, assessmentScaleObservation.getInterpretationCodes());


        if (assessmentScaleObservation.getAuthors() != null) {
            for (Author author : assessmentScaleObservation.getAuthors()) {
                assessmentScaleObservationCcd.getAuthors().add(SectionEntryFactory.buildAuthor(author));
            }
        }

        if (assessmentScaleObservation.getAssessmentScaleSupportingObservations() != null) {
            for (AssessmentScaleSupportingObservation assessmentScaleSupportingObservation : assessmentScaleObservation.getAssessmentScaleSupportingObservations()) {
                Observation assessmentScaleSupportingObservationCcd = CDAFactory.eINSTANCE.createObservation();
                assessmentScaleObservationCcd.addObservation(assessmentScaleSupportingObservationCcd);
                ((EntryRelationship) assessmentScaleSupportingObservationCcd.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.COMP);

                assessmentScaleSupportingObservationCcd.setClassCode(ActClassObservation.OBS);
                assessmentScaleSupportingObservationCcd.setMoodCode(x_ActMoodDocumentObservation.EVN);
                assessmentScaleSupportingObservationCcd.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.86"));
                assessmentScaleSupportingObservationCcd.getIds().add(CcdUtils.getId(assessmentScaleSupportingObservation.getId()));
                assessmentScaleSupportingObservationCcd.setCode(CcdUtils.createCD(assessmentScaleSupportingObservation.getCode()));
                assessmentScaleSupportingObservationCcd.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

                if (assessmentScaleSupportingObservation.getValueCode() != null) {
                    assessmentScaleSupportingObservationCcd.getValues().add(CcdUtils.createCD(assessmentScaleSupportingObservation.getValueCode()));
                } else if (assessmentScaleSupportingObservation.getIntValue() != null) {
                    INT value = DatatypesFactory.eINSTANCE.createINT();
                    value.setValue(assessmentScaleSupportingObservation.getIntValue());
                    assessmentScaleSupportingObservationCcd.getValues().add(value);
                } else {
                    assessmentScaleSupportingObservationCcd.getValues().add(CcdUtils.createNillCode());
                }


            }
        }
        SectionEntryFactory.initReferenceRanges(assessmentScaleObservationCcd, assessmentScaleObservation.getObservationRanges());//, "aso" + assessmentScaleObservation.getId());

        return assessmentScaleObservationCcd;
    }

    private static Observation createCaregiverCharacteristic(CaregiverCharacteristic caregiverCharacteristic) {
        Observation caregiverCharacteristicCcd = CDAFactory.eINSTANCE.createObservation();
        caregiverCharacteristicCcd.setClassCode(ActClassObservation.OBS);
        caregiverCharacteristicCcd.setMoodCode(x_ActMoodDocumentObservation.EVN);
        caregiverCharacteristicCcd.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.72"));
        caregiverCharacteristicCcd.getIds().add(CcdUtils.getId(caregiverCharacteristic.getId()));

        caregiverCharacteristicCcd.setCode(CcdUtils.createCD(caregiverCharacteristic.getCode(), CodeSystem.HL7_ACT_CODE.getOid()));

        caregiverCharacteristicCcd.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

        caregiverCharacteristicCcd.getValues().add(CcdUtils.createCD(caregiverCharacteristic.getValue()));

        if (caregiverCharacteristic.getParticipantTimeLow() != null || caregiverCharacteristic.getParticipantTimeHigh() != null || caregiverCharacteristic.getParticipantRoleCode() != null) {

            Participant2 caregiverParticipant = CDAFactory.eINSTANCE.createParticipant2();
            caregiverParticipant.setTypeCode(ParticipationType.IND);
            caregiverCharacteristicCcd.getParticipants().add(caregiverParticipant);
            if (caregiverCharacteristic.getParticipantTimeLow() != null || caregiverCharacteristic.getParticipantTimeHigh() != null) {
                IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
                IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
                if (caregiverCharacteristic.getParticipantTimeLow() != null) {
                    low.setValue(CcdUtils.formatSimpleDate(caregiverCharacteristic.getParticipantTimeLow()));
                } else {
                    low.setNullFlavor(NullFlavor.NI);
                }
                effectiveTime.setLow(low);
                if (caregiverCharacteristic.getParticipantTimeHigh() != null) {
                    IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
                    high.setValue(CcdUtils.formatSimpleDate(caregiverCharacteristic.getParticipantTimeHigh()));
                    effectiveTime.setHigh(high);
                }
                caregiverParticipant.setTime(effectiveTime);
            }

            ParticipantRole participantRole = CDAFactory.eINSTANCE.createParticipantRole();
            caregiverParticipant.setParticipantRole(participantRole);
            participantRole.setClassCode(RoleClassRoot.CAREGIVER);

            if (caregiverCharacteristic.getParticipantRoleCode() != null) {
                participantRole.setCode(CcdUtils.createCE(caregiverCharacteristic.getParticipantRoleCode(), "2.16.840.1.113883.5.111"));
            }
        }

        return caregiverCharacteristicCcd;
    }

    private static SupplyActivity createNonMedicalSupplyActivity(NonMedicinalSupplyActivity nonMedicinalSupplyActivity) {
        SupplyActivity supplyActivity = CCDFactory.eINSTANCE.createSupplyActivity();

        supplyActivity.setClassCode(ActClassSupply.SPLY);
        supplyActivity.setMoodCode(x_DocumentSubstanceMood.get(nonMedicinalSupplyActivity.getMoodCode()));
        supplyActivity.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.50"));
        supplyActivity.getIds().add(CcdUtils.getId(nonMedicinalSupplyActivity.getId()));

        CS supplyStatus = DatatypesFactory.eINSTANCE.createCS();
        if (nonMedicinalSupplyActivity.getStatusCode() != null) {
            supplyStatus.setCode(nonMedicinalSupplyActivity.getStatusCode());
        } else {
            supplyStatus.setNullFlavor(NullFlavor.NI);
        }
        supplyActivity.setStatusCode(supplyStatus);

        if (nonMedicinalSupplyActivity.getEffectiveTimeHigh() != null) {
            IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
            IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
            high.setValue(CcdUtils.formatSimpleDate(nonMedicinalSupplyActivity.getEffectiveTimeHigh()));
            effectiveTime.setHigh(high);
            supplyActivity.getEffectiveTimes().add(effectiveTime);
        }

        if (nonMedicinalSupplyActivity.getQuantity() != null) {
            PQ quantity = DatatypesFactory.eINSTANCE.createPQ();
            quantity.setValue(nonMedicinalSupplyActivity.getQuantity());
            supplyActivity.setQuantity(quantity);
        }

        if (nonMedicinalSupplyActivity.getProductInstance() != null) {
            Participant2 participant2 = CDAFactory.eINSTANCE.createParticipant2();
            participant2.setTypeCode(ParticipationType.PRD);
            participant2.setParticipantRole(SectionEntryFactory.buildProductInstance(nonMedicinalSupplyActivity.getProductInstance()));
            supplyActivity.getParticipants().add(participant2);
        }
        return supplyActivity;

    }

    private static Observation createPressureUlcerObservation(PressureUlcerObservation pressureUlcerObservation) {
        Observation pressureUlcerObservationCcd = CDAFactory.eINSTANCE.createObservation();
        pressureUlcerObservationCcd.setClassCode(ActClassObservation.OBS);
        pressureUlcerObservationCcd.setMoodCode(x_ActMoodDocumentObservation.EVN);

        if (pressureUlcerObservation.isNegationInd() != null && pressureUlcerObservation.isNegationInd()) {
            pressureUlcerObservationCcd.setNegationInd(pressureUlcerObservation.isNegationInd());
        }
        pressureUlcerObservationCcd.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.70"));
        pressureUlcerObservationCcd.getIds().add(CcdUtils.getId(pressureUlcerObservation.getId()));

        pressureUlcerObservationCcd.setCode(CcdUtils.createCD("ASSERTION", "Assertion", CodeSystem.HL7_ACT_CODE));

        if (pressureUlcerObservation.getText() != null) {
            pressureUlcerObservationCcd.setText(CcdUtils.createText(PressureUlcerObservation.class.getSimpleName(), pressureUlcerObservation.getId()));
        }
        pressureUlcerObservationCcd.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));
        pressureUlcerObservationCcd.setEffectiveTime(CcdUtils.convertEffectiveTime(pressureUlcerObservation.getEffectiveTime()));

        CD valueCode;
        if (pressureUlcerObservation.getValue() != null) {
            valueCode = CcdUtils.createCD(pressureUlcerObservation.getValue());
            // (CONF:14396)
            // If the code is something other than SNOMED, @nullFlavor SHOULD be “OTH” and the other code SHOULD be placed in the translation element
            if (SNOMED_CT.getOid().equals(valueCode.getCodeSystem())) {
                pressureUlcerObservationCcd.getValues().add(valueCode);
            } else {
                CD othCode = DatatypesFactory.eINSTANCE.createCD();
                othCode.getTranslations().add(valueCode);
                othCode.setNullFlavor(NullFlavor.OTH);
                pressureUlcerObservationCcd.getValues().add(othCode);
            }
        } else {
            valueCode = DatatypesFactory.eINSTANCE.createCD();
            valueCode.setNullFlavor(NullFlavor.UNK);
            pressureUlcerObservationCcd.getValues().add(valueCode);
        }

        if (!CollectionUtils.isEmpty(pressureUlcerObservation.getTargetSiteCodes())) {
            for (TargetSiteCode targetSiteCode : pressureUlcerObservation.getTargetSiteCodes()) {

                CD tsCode = CcdUtils.createCD(targetSiteCode.getCode(), SNOMED_CT.getOid());

                if (targetSiteCode.getValue() != null) {
                    CR qualifier = DatatypesFactory.eINSTANCE.createCR();

                    CV cName = CcdUtils.createCV("272741003", "Laterality", SNOMED_CT);
                    qualifier.setName(cName);

                    qualifier.setValue(CcdUtils.createCD(targetSiteCode.getValue(), SNOMED_CT.getOid()));

                    tsCode.getQualifiers().add(qualifier);
                }
                pressureUlcerObservationCcd.getTargetSiteCodes().add(tsCode);
            }

        } else {
            pressureUlcerObservationCcd.getTargetSiteCodes().add(CcdUtils.createNillCode());
        }

        if (pressureUlcerObservation.getLengthOfWoundValue() != null) {
            Observation lengthOfWoundObservation = createObservation();
            pressureUlcerObservationCcd.addObservation(lengthOfWoundObservation);
            ((EntryRelationship) lengthOfWoundObservation.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.COMP);

            lengthOfWoundObservation.setCode(CcdUtils.createCD("401238003", "Length of Wound", SNOMED_CT));

            PQ value = DatatypesFactory.eINSTANCE.createPQ();
            value.setValue(pressureUlcerObservation.getLengthOfWoundValue());
            lengthOfWoundObservation.getValues().add(value);
        }


        if (pressureUlcerObservation.getWidthOfWoundValue() != null) {

            Observation widthOfWoundObservation = createObservation();
            pressureUlcerObservationCcd.addObservation(widthOfWoundObservation);
            ((EntryRelationship) widthOfWoundObservation.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.COMP);

            widthOfWoundObservation.setCode(CcdUtils.createCD("401239006", "Width of Wound", SNOMED_CT));

            PQ value = DatatypesFactory.eINSTANCE.createPQ();
            value.setValue(pressureUlcerObservation.getWidthOfWoundValue());
            widthOfWoundObservation.getValues().add(value);
        }

        if (pressureUlcerObservation.getDepthOfWoundValue() != null) {
            Observation widthOfWoundObservation = createObservation();
            pressureUlcerObservationCcd.addObservation(widthOfWoundObservation);
            ((EntryRelationship) widthOfWoundObservation.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.COMP);

            widthOfWoundObservation.setCode(CcdUtils.createCD("425094009","Depth of Wound", SNOMED_CT));

            PQ value = DatatypesFactory.eINSTANCE.createPQ();
            value.setValue(pressureUlcerObservation.getDepthOfWoundValue());
            widthOfWoundObservation.getValues().add(value);
        }

        return pressureUlcerObservationCcd;
    }

    @Override
    public List<FunctionalStatus> parseSection(Resident resident, FunctionalStatusSection functionalStatusSection) {
        if (!CcdParseUtils.hasContent(functionalStatusSection)) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final FunctionalStatus functionalStatus = new FunctionalStatus();
        functionalStatus.setResident(resident);
        functionalStatus.setDatabase(resident.getDatabase());

        final List<StatusResultOrganizer> functionalStatusResultOrganizers = new ArrayList<>();
        final List<StatusResultOrganizer> cognitiveStatusResultOrganizers = new ArrayList<>();
        for (Organizer ccdOrganizer : functionalStatusSection.getOrganizers()) {
            final StatusResultOrganizer statusResultOrganizer = parseResultOrganizer(ccdOrganizer, resident);
            // Can not distinguish Functional Status Result Organizer from Cognitive Status Result Organizer in HL7 CCD -> storing all Organizers as Functional Status Result Organizers
            functionalStatusResultOrganizers.add(statusResultOrganizer);
        }
        functionalStatus.setCognitiveStatusResultOrganizers(cognitiveStatusResultOrganizers);
        functionalStatus.setFunctionalStatusResultOrganizers(functionalStatusResultOrganizers);

        final List<StatusProblemObservation> functionalStatusProblemObservations = new ArrayList<>();
        final List<StatusProblemObservation> cognitiveStatusProblemObservations = new ArrayList<>();
        for (Act ccdAct : functionalStatusSection.getActs()) {
            final IVL_TS actEffectiveTime = ccdAct.getEffectiveTime();
            // TODO store actEffectiveTime if needed
            // [HL7 CCD Final] CONF-150: A problem act MAY contain exactly one Act / effectiveTime, to indicate the timing of the concern (e.g. the interval of time for which the problem is a concern).
            for (Observation ccdObservation : ccdAct.getObservations()) {
                if (ccdObservation instanceof ProblemObservation) {
                    final StatusProblemObservation statusProblemObservation = parseProblemObservation((ProblemObservation) ccdObservation, resident);
                    // Can not distinguish Functional Status Result Observation from Cognitive Status Result Observation in HL7 CCD -> storing all Observations as Functional Status Result Observations
                    functionalStatusProblemObservations.add(statusProblemObservation);
                }
            }
        }
        functionalStatus.setCognitiveStatusProblemObservations(cognitiveStatusProblemObservations);
        functionalStatus.setFunctionalStatusProblemObservations(functionalStatusProblemObservations);

        return new ArrayList<>(Collections.singleton(functionalStatus));
    }

    private StatusProblemObservation parseProblemObservation(ProblemObservation ccdObservation, Resident resident) {
        final StatusProblemObservation statusProblemObservation = new StatusProblemObservation();
        statusProblemObservation.setDatabase(resident.getDatabase());

        final Pair<Date, Date> effectiveTimePair = CcdTransform.IVLTStoHighLowDate(ccdObservation.getEffectiveTime());
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
        final List<NonMedicinalSupplyActivity> activities = parseNonMedicalSupplyActivities(ccdObservation.getSupplies(), resident);
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

    private StatusResultOrganizer parseResultOrganizer(Organizer ccdOrganizer, Resident resident) {
        final StatusResultOrganizer statusResultOrganizer = new StatusResultOrganizer();
        statusResultOrganizer.setDatabase(resident.getDatabase());

        statusResultOrganizer.setCode(ccdCodeFactory.convert(ccdOrganizer.getCode()));
        if (!CollectionUtils.isEmpty(ccdOrganizer.getObservations())) {
            List<StatusResultObservation> statusResultObservations = new ArrayList<>();
            for (Observation ccdObservation : ccdOrganizer.getObservations()) {
                if (ccdObservation instanceof ResultObservation) {
                    StatusResultObservation statusResultObservation = parseStatusResultObservation(
                            (ResultObservation)ccdObservation, resident);
                    statusResultObservations.add(statusResultObservation);
                }
            }
            statusResultOrganizer.setStatusResultObservations(statusResultObservations);
        }

        return statusResultOrganizer;
    }

    private StatusResultObservation parseStatusResultObservation(ResultObservation ccdResultObservation, Resident resident) {
        StatusResultObservation statusResultObservation = new StatusResultObservation();
        statusResultObservation.setDatabase(resident.getDatabase());

        CD code = ccdResultObservation.getCode();
        statusResultObservation.setCode(ccdCodeFactory.convert(code));
        statusResultObservation.setText(CcdTransform.EDtoString(ccdResultObservation.getText(), ccdResultObservation.getStatusCode().getCode()));

        statusResultObservation.setEffectiveTime(CcdParseUtils.convertTsToDate(ccdResultObservation.getEffectiveTime()));

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
            statusResultObservation.setAuthor(sectionEntryParseFactory.parseAuthor(ccdResultObservation.getAuthors().get(0),
                    resident, "FunctionalStatus_NWHIN"));
        }

        statusResultObservation.setReferenceRanges(SectionEntryParseFactory.parseReferenceRanges(
                ccdResultObservation.getReferenceRanges()));

        final List<NonMedicinalSupplyActivity> activities = parseNonMedicalSupplyActivities(ccdResultObservation.getSupplies(), resident);
        statusResultObservation.setNonMedicinalSupplyActivities(activities);

        if (!CollectionUtils.isEmpty(ccdResultObservation.getObservations())) {
            final List<AssessmentScaleObservation> assessmentScaleObservations = new ArrayList<>();
            final List<CaregiverCharacteristic> caregiverCharacteristics = new ArrayList<>();

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

    private AssessmentScaleObservation parseAssessmentScaleObservation(Observation ccdObservation, Resident resident) {
        if (!CcdParseUtils.hasContent(ccdObservation) || resident == null) {
            return null;
        }

        AssessmentScaleObservation assessmentScaleObservation = new AssessmentScaleObservation();
        assessmentScaleObservation.setDatabase(resident.getDatabase());
        assessmentScaleObservation.setCode(ccdCodeFactory.convert(ccdObservation.getCode()));

        assessmentScaleObservation.setDerivationExpr(CcdTransform.EDtoString(ccdObservation.getDerivationExpr(), assessmentScaleObservation.getCode()));

        IVL_TS observationEffectiveTime = ccdObservation.getEffectiveTime();
        assessmentScaleObservation.setEffectiveTime(CcdParseUtils.convertTsToDate(observationEffectiveTime));

        INT assessmentValue = CcdParseUtils.getFirstNotEmptyValue(ccdObservation.getValues(), INT.class);
        assessmentScaleObservation.setValue(CcdTransform.INTtoInteger(assessmentValue));

        assessmentScaleObservation.setInterpretationCodes(ccdCodeFactory.convertInterpretationCodes(ccdObservation));

        if (!CollectionUtils.isEmpty(ccdObservation.getAuthors())) {
            final List<Author> authors = new ArrayList<>();
            for (org.eclipse.mdht.uml.cda.Author author : ccdObservation.getAuthors()) {
                authors.add(sectionEntryParseFactory.parseAuthor(author, resident, "FunctionalStatus_NWHIN"));
            }
            assessmentScaleObservation.setAuthors(authors);
        }

        if (ccdObservation.getObservations() != null) {
            final List<AssessmentScaleSupportingObservation> supportingObservations = new ArrayList<>();
            for (Observation ccdSupportingObservation : ccdObservation.getObservations()) {
                AssessmentScaleSupportingObservation assessmentScaleSupportingObservation = new AssessmentScaleSupportingObservation();
                assessmentScaleSupportingObservation.setAssessmentScaleObservation(assessmentScaleObservation);
                assessmentScaleSupportingObservation.setDatabase(resident.getDatabase());
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

        assessmentScaleObservation.setObservationRanges(SectionEntryParseFactory.parseReferenceRanges(
                ccdObservation.getReferenceRanges()));

        return assessmentScaleObservation;
    }

    private CaregiverCharacteristic parseCaregiverCharacteristic(Observation ccdObservation, Resident resident) {
        if (!CcdParseUtils.hasContent(ccdObservation)) {
            return null;
        }
        checkNotNull(resident);

        final CaregiverCharacteristic caregiverCharacteristic = new CaregiverCharacteristic();
        caregiverCharacteristic.setDatabase(resident.getDatabase());
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

    private List<NonMedicinalSupplyActivity> parseNonMedicalSupplyActivities(EList<Supply> supplies, Resident resident) {
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

    private NonMedicinalSupplyActivity parseNonMedicalSupplyActivity(Supply ccdSupply, Resident resident) {
        if (!CcdParseUtils.hasContent(ccdSupply)) {
            return null;
        }
        checkNotNull(resident);

        final NonMedicinalSupplyActivity nonMedicinalSupplyActivity = new NonMedicinalSupplyActivity();
        nonMedicinalSupplyActivity.setDatabase(resident.getDatabase());

        nonMedicinalSupplyActivity.setStatusCode(ccdSupply.getCode() != null ? ccdSupply.getCode().getCode() : null);
        if (!CollectionUtils.isEmpty(ccdSupply.getEffectiveTimes())) {
            nonMedicinalSupplyActivity.setEffectiveTimeHigh(CcdParseUtils.convertTsToDate(ccdSupply.getEffectiveTimes().get(0)));
        }
        nonMedicinalSupplyActivity.setQuantity(ccdSupply.getQuantity() != null ? ccdSupply.getQuantity().getValue() : null);
        if (!CollectionUtils.isEmpty(ccdSupply.getParticipants())) {
            ParticipantRole ccdParticipantRole = ccdSupply.getParticipants().get(0).getParticipantRole();
            nonMedicinalSupplyActivity.setProductInstance(sectionEntryParseFactory.parseProductInstance(ccdParticipantRole, resident));
        }
        return nonMedicinalSupplyActivity;
    }

}