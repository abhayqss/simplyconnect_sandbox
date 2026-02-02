package com.scnsoft.eldermark.services.cda.templates.sections.entries;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.mdht.uml.cda.EntryRelationship;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.cda.ReferenceRange;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author phomal
 * Created on 4/13/2018.
 */
@Component
public class ObservationFactory {

    private static final String VALUE_SET_PROBLEM_OID = "2.16.840.1.113883.3.88.12.3221.7.4";

    private final CcdCodeFactory ccdCodeFactory;

    @Autowired
    private SubstanceAdministrationFactory substanceAdministrationsFactory;
    @Autowired
    private ProcedureActivityFactory procedureActivityFactory;

    @Autowired
    public ObservationFactory(CcdCodeFactory ccdCodeFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
    }

    public SeverityObservation parseSeverityObservation(Observation observation, Resident resident, String legacyTable) {
        if (!CcdParseUtils.hasContent(observation) || resident == null) {
            return null;
        }

        final SeverityObservation severityObservation = new SeverityObservation();
        severityObservation.setDatabase(resident.getDatabase());
        severityObservation.setLegacyTable(legacyTable);
        severityObservation.setLegacyId(CcdParseUtils.getFirstIdExtension(observation.getIds()));

        final CD ccdObservationValue = getValue(observation, CD.class);
        severityObservation.setSeverityCode(ccdCodeFactory.convert(ccdObservationValue));
        severityObservation.setSeverityText(CcdTransform.EDtoString(observation.getText(), severityObservation.getSeverityCode()));

        return severityObservation;
    }

    public ReactionObservation parseReactionObservation(Observation observation, Resident resident, String legacyTable) {
        if (!CcdParseUtils.hasContent(observation) || resident == null) {
            return null;
        }

        final ReactionObservation reactionObservation = new ReactionObservation();
        reactionObservation.setLegacyTable(legacyTable);
        reactionObservation.setLegacyId(CcdParseUtils.getFirstIdExtensionStr(observation.getIds()));
        reactionObservation.setDatabase(resident.getDatabase());

        final Pair<Date, Date> effectiveTimes = CcdTransform.IVLTStoHighLowDateOrIVLTStoCenterDateOrTsToDate(observation.getEffectiveTime());
        if (effectiveTimes != null) {
            reactionObservation.setTimeHigh(effectiveTimes.getFirst());
            reactionObservation.setTimeLow(effectiveTimes.getSecond());
        }

        final CD ccdObservationValue = getValue(observation, CD.class);
        reactionObservation.setReactionCode(ccdCodeFactory.convert(ccdObservationValue));
        reactionObservation.setReactionText(CcdTransform.EDtoString(observation.getText(), reactionObservation.getReactionCode()));

        if (CollectionUtils.isNotEmpty(observation.getEntryRelationships())) {
            List<SeverityObservation> severityObservations = null;
            List<Medication> medications = null;
            List<ProcedureActivity> procedureActivities = null;
            for (EntryRelationship ccdEntryRelationship : observation.getEntryRelationships()) {
                if (ccdEntryRelationship.getObservation() != null) {
                    if (severityObservations == null) {
                        severityObservations = new ArrayList<>();
                    }
                    severityObservations.add(parseSeverityObservation(ccdEntryRelationship.getObservation(), resident,
                            legacyTable));
                }
                if (ccdEntryRelationship.getSubstanceAdministration() != null) {
                    if (medications == null) {
                        medications = new ArrayList<>();
                    }
                    medications.add(substanceAdministrationsFactory.parseMedicationActivity(ccdEntryRelationship.getSubstanceAdministration(), resident,
                            legacyTable));
                }
                if (ccdEntryRelationship.getProcedure() != null) {
                    if (procedureActivities == null) {
                        procedureActivities = new ArrayList<>();
                    }
                    procedureActivities.add(procedureActivityFactory.parseProcedureActivity(ccdEntryRelationship.getProcedure(), resident, legacyTable));
                }

            }
            reactionObservation.setSeverityObservations(severityObservations);
            reactionObservation.setMedications(medications);
            reactionObservation.setProcedureActivities(procedureActivities);
        }

        return reactionObservation;
    }

    public ProblemObservation parseProblemObservation(Observation srcObservation, Resident resident, Problem targetProblem) {
        if (!CcdParseUtils.hasContent(srcObservation) || resident == null) {
            return null;
        }

        final ProblemObservation targetObservation = new ProblemObservation();
        targetObservation.setLegacyId(CcdParseUtils.getFirstIdExtension(srcObservation.getIds()));
        targetObservation.setDatabase(resident.getDatabase());

        targetObservation.setNegationInd(srcObservation.getNegationInd());
        targetObservation.setProblemType(ccdCodeFactory.convert(srcObservation.getCode()));

        final Pair<Date, Date> observationEffectiveTime = CcdTransform.IVLTStoHighLowDateOrIVLTStoCenterDateOrTsToDate(srcObservation.getEffectiveTime());
        if (observationEffectiveTime != null) {
            targetObservation.setProblemDateTimeHigh(observationEffectiveTime.getFirst());
            targetObservation.setProblemDateTimeLow(observationEffectiveTime.getSecond());
        }

        for (Observation srcObservationEntry : srcObservation.getObservations()) {
            SectionTypeCode type = SectionTypeCode.getByCode(srcObservationEntry.getCode().getCode(), srcObservationEntry.getCode().getCodeSystem());
            if (type != null) {
                switch (type) {
                    case AGE_OBSERVATION:
                        final Pair<String, Integer> observation = parseAgeObservation(srcObservationEntry);
                        if (observation != null) {
                            targetObservation.setAgeObservationValue(observation.getSecond());
                            targetObservation.setAgeObservationUnit(observation.getFirst());
                        }
                        break;
                    case STATUS_OBSERVATION:
                        CD problemValue = getValue(srcObservationEntry, CD.class);
                        targetObservation.setProblemStatusCode(ccdCodeFactory.convert(problemValue));
                        ED problemStatusText = srcObservationEntry.getText();
                        targetObservation.setProblemStatusText(CcdTransform.EDtoString(problemStatusText, targetObservation.getProblemStatusCode()));
                        break;
                    case HEALTH_STATUS_OBSERVATION:
                        CD healthStatusValue = getValue(srcObservationEntry, CD.class);
                        targetObservation.setHealthStatusCode(ccdCodeFactory.convert(healthStatusValue));
                        ED healthStatusText = srcObservationEntry.getText();
                        targetObservation.setHealthStatusObservationText(CcdTransform.EDtoString(healthStatusText, targetObservation.getHealthStatusCode()));
                }
            }
        }

        final StringBuilder concatTranslationCodes = new StringBuilder();
        final StringBuilder concatTranslationCodeSets = new StringBuilder();
        final boolean saveTranslations;
        CD problemCode = getValue(srcObservation, CD.class);
        if (problemCode != null) {
            targetObservation.setProblemCode(ccdCodeFactory.convert(problemCode, VALUE_SET_PROBLEM_OID));
            targetObservation.setProblemName(CcdTransform.EDtoString(srcObservation.getText(), problemCode.getDisplayName()));

            concatTranslationCodes.append(problemCode.getCode()).append("\n");
            concatTranslationCodeSets.append(problemCode.getCodeSystemName()).append("\n");
            saveTranslations = true;
        } else {
            //shall contain exactly one value (CONF:9058)
            problemCode = (CD) srcObservation.getValues().get(0);

            //save translations if nullFlavour is OTH (CONF:10142)
            saveTranslations = problemCode.getNullFlavor() == NullFlavor.OTH;
        }

        if (saveTranslations && CollectionUtils.isNotEmpty(problemCode.getTranslations())) {
            final Set<CcdCode> translations = new HashSet<>();
            String problemName = null;
            for (CD item : problemCode.getTranslations()) {
                if (CcdParseUtils.hasContent(item)) {
                    CcdCode translationCode = ccdCodeFactory.convert(item);
                    if (translationCode != null) {
                        translations.add(translationCode);
                        problemName = StringUtils.isNotEmpty(problemName) ? problemName : translationCode.getDisplayName();
                    }
                    concatTranslationCodes.append(item.getCode()).append("\n");
                    concatTranslationCodeSets.append(item.getCodeSystemName()).append("\n");
                }
            }
            targetObservation.setTranslations(translations);
            targetObservation.setProblemIcdCode(concatTranslationCodes
                    .deleteCharAt(concatTranslationCodes.length() - 1)
                    .toString());
            targetObservation.setProblemIcdCodeSet(concatTranslationCodeSets
                    .deleteCharAt(concatTranslationCodeSets.length() - 1)
                    .toString());
            if (StringUtils.isEmpty(targetObservation.getProblemName())) {
                targetObservation.setProblemName(problemName);
            }
        }

        targetObservation.setProblem(targetProblem);
        return targetObservation;
    }

    public AdvanceDirective parseAdvanceDirective(Observation srcObservation, Resident resident, String LEGACY_TABLE) {
        final AdvanceDirective advanceDirective = new AdvanceDirective();
        advanceDirective.setResident(resident);
        advanceDirective.setDatabase(resident.getDatabase());
        advanceDirective.setLegacyId(CcdParseUtils.getFirstIdExtension(srcObservation.getIds()));
        advanceDirective.setLegacyTable(LEGACY_TABLE);
        advanceDirective.setType(ccdCodeFactory.convert(srcObservation.getCode()));
        if (advanceDirective.getType() != null) {
            advanceDirective.setTextType(advanceDirective.getType().getDisplayName());
        }
        advanceDirective.setValue(ccdCodeFactory.convert(getValue(srcObservation, CD.class)));
        if (advanceDirective.getValue() != null) {
            advanceDirective.setTextValue(advanceDirective.getValue().getDisplayName());
        }

        Pair<Date, Date> effectiveTimePair = CcdTransform.IVLTStoHighLowDateOrIVLTStoCenterDateOrTsToDate(srcObservation.getEffectiveTime());
        if (effectiveTimePair != null) {
            advanceDirective.setTimeHigh(effectiveTimePair.getFirst());
            advanceDirective.setTimeLow(effectiveTimePair.getSecond());
        }

        final Participant2Factory.ParticipantWrapper participantWrapper =
                Participant2Factory.parse(srcObservation.getParticipants(), resident, LEGACY_TABLE);
        advanceDirective.setVerifiers(participantWrapper.getVerifiers());
        advanceDirective.setCustodian(participantWrapper.getCustodian());

        final List<AdvanceDirectiveDocument> documents = AdvanceDirectiveDocumentFactory.parseReferenceDocuments(
                srcObservation.getReferences(), advanceDirective, resident);
        advanceDirective.setReferenceDocuments(documents);

        return advanceDirective;
    }

    /**
     * Parse age observation to get a pair of values, that represent Unit and Age.
     *
     * @return Unit + Age.
     */
    public static Pair<String, Integer> parseAgeObservation(Observation ageObservation) {
        if (!CcdParseUtils.hasContent(ageObservation) || CollectionUtils.isEmpty(ageObservation.getValues())) {
            return null;
        }

        String unit = null;
        Integer age = null;

        ANY any = ageObservation.getValues().get(0);
        if (any instanceof PQ) {
            final PQ ageValue = getValue(ageObservation, PQ.class);
            if (CcdParseUtils.hasContent(ageValue)) {
                age = ageValue.getValue().intValue();
            }
            unit = ageValue.getUnit();
        } else if (any instanceof INT) {
            final INT ageValue = getValue(ageObservation, INT.class);
            age = ageValue.getValue().intValue();
        }

        return new Pair<>(unit, age);
    }

    public static List<String> parseReferenceRanges(List<ReferenceRange> ccdReferenceRanges) {
        List<String> refRanges = null;
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(ccdReferenceRanges)) {
            refRanges = new ArrayList<>();
            for (ReferenceRange referenceRange : ccdReferenceRanges) {
                if (referenceRange.getObservationRange() != null) {
                    String text = CcdTransform.EDtoString(referenceRange.getObservationRange().getText());
                    if (text != null) {
                        refRanges.add(text);
                    }
                }
            }
        }

        return refRanges;
    }

    /**
     * Get the first non-empty observation > value.
     *
     * @throws ClassCastException Handle this exception if you think that the provided observation may contain values of an unexpected type.
     */
    public static <T extends ANY> T getValue(Observation observation, Class<T> returnClass) {
        return CcdParseUtils.getFirstNotEmptyValue(observation.getValues(), returnClass);
    }

    /**
     * Get the first non-empty observation > method code.
     */
    public static CE getMethod(Observation observation) {
        return CcdParseUtils.getFirstNotEmptyValue(observation.getMethodCodes(), CE.class);
    }

    /**
     * Get the first non-empty observation > target site code.
     */
    public static CD getTargetSite(Observation observation) {
        return CcdParseUtils.getFirstNotEmptyValue(observation.getTargetSiteCodes(), CD.class);
    }

    /**
     * Parse the first non-empty observation > value.
     *
     * @throws ClassCastException if the provided observation contains values of an unexpected type (not CD).
     * @see CD
     */
    public CcdCode parseValueAsCode(Observation observation) {
        final CD value = CcdParseUtils.getFirstNotEmptyValue(observation.getValues(), CD.class);
        return ccdCodeFactory.convert(value);
    }

    /**
     * Parse the first non-empty observation > method code.
     */
    public CcdCode parseMethod(Observation observation) {
        final CE method = CcdParseUtils.getFirstNotEmptyValue(observation.getMethodCodes(), CE.class);
        return ccdCodeFactory.convert(method);
    }

    /**
     * Parse the first non-empty observation > target site code.
     */
    public CcdCode parseTargetSite(Observation observation) {
        final CD targetSite = CcdParseUtils.getFirstNotEmptyValue(observation.getTargetSiteCodes(), CD.class);
        return ccdCodeFactory.convert(targetSite);
    }

    /* TODO delete if not needed

    public List<AdvanceDirective> parseAdvanceDirectives(EList<Observation> observations, Resident resident, String legacyTable) {
        if (CollectionUtils.isEmpty(observations) || resident == null) {
            return null;
        }

        final List<AdvanceDirective> advanceDirectives = new ArrayList<>();
        for (Observation observation : observations) {
            final AdvanceDirective advanceDirective = parseAdvanceDirective(observation, resident, legacyTable);
            advanceDirectives.add(advanceDirective);
        }

        return advanceDirectives;
    }*/
}
