package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.FamilyHistory;
import com.scnsoft.eldermark.entity.document.ccd.FamilyHistoryObservation;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.ObservationFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.AbstractParsableSection;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSection;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import org.apache.commons.lang.StringUtils;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.cda.RelatedSubject;
import org.eclipse.mdht.uml.cda.SubjectPerson;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.ST;
import org.openhealthtools.mdht.uml.cda.consol.AgeObservation;
import org.openhealthtools.mdht.uml.cda.consol.FamilyHistoryDeathObservation;
import org.openhealthtools.mdht.uml.cda.consol.FamilyHistoryOrganizer;
import org.openhealthtools.mdht.uml.cda.consol.FamilyHistorySection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <h1>Family History</h1> “This section contains data defining the patient’s
 * genetic relatives in terms of possible or relevant health risk factors that
 * have a potential impact on the patient’s healthcare risk profile.” [CCD 3.6]
 *
 * <h2>Template ID = 2.16.840.1.113883.10.20.22.4.46</h2> Family History
 * Observations related to a particular family member are contained within a
 * Family History Organizer. The effectiveTime in the Family History Observation
 * is the biologically or clinically relevant time of the observation. The
 * biologically or clinically relevant time is the time at which the observation
 * holds (is effective) for the family member (the subject of the observation).
 *
 * @see FamilyHistory
 * @see FamilyHistoryObservation
 * @see Client
 */
@Component("consol.FamilyHistoryParser")
public class FamilyHistoryParser
        extends AbstractParsableSection<FamilyHistoryOrganizer, FamilyHistorySection, FamilyHistory>
        implements ParsableSection<FamilyHistorySection, FamilyHistory> {

    private final CcdCodeFactory ccdCodeFactory;

    @Autowired
    public FamilyHistoryParser(CcdCodeFactory ccdCodeFactory) {
        this.ccdCodeFactory = ccdCodeFactory;
    }

    private static final Logger logger = LoggerFactory.getLogger(FamilyHistoryParser.class);

    @Override
    public boolean isSectionIgnored(FamilyHistorySection familyHistorySection) {
        return !CcdParseUtils.hasContent(familyHistorySection)
                || (CollectionUtils.isEmpty(familyHistorySection.getFamilyHistories())
                && CollectionUtils.isEmpty(familyHistorySection.getObservations()));
    }

    @Override
    public List<FamilyHistory> doParseSection(Client resident, FamilyHistorySection familyHistorySection) {

        Objects.requireNonNull(resident);

        // The Family History Organizer associates a set of observations with a family
        // member. For example, the Family History Organizer can group a set of
        // observations about the patient’s father.
        // parse organized family history observations
        List<FamilyHistory> familyHistories = new ArrayList<>();
        for (FamilyHistoryOrganizer ccdFamilyHistoryOrganizer : familyHistorySection.getFamilyHistories()) {
            final FamilyHistory familyHistory = new FamilyHistory();
            familyHistory.setOrganization(resident.getOrganization());
            familyHistory.setClient(resident);
            if (ccdFamilyHistoryOrganizer.getSubject() != null
                    && ccdFamilyHistoryOrganizer.getSubject().getRelatedSubject() != null) {
                RelatedSubject ccdRelatedSubject = ccdFamilyHistoryOrganizer.getSubject().getRelatedSubject();
                familyHistory.setRelatedSubjectCode(ccdCodeFactory.convert(ccdRelatedSubject.getCode()));
                if (ccdRelatedSubject.getSubject() != null) {
                    SubjectPerson ccdSubjectPerson = ccdRelatedSubject.getSubject();
                    familyHistory.setAdministrativeGenderCode(
                            ccdCodeFactory.convert(ccdSubjectPerson.getAdministrativeGenderCode()));
                    familyHistory.setBirthTime(CcdParseUtils.convertTsToDate(ccdSubjectPerson.getBirthTime()));
                    // TODO Iterate ccdFamilyHistoryOrganizer.getFamilyHistoryObservations()
                    // instead?
                    if (!CollectionUtils.isEmpty(ccdFamilyHistoryOrganizer.getFamilyHistoryObservations())) {
                        List<FamilyHistoryObservation> familyHistoryObservations = new ArrayList<>();
                        for (org.openhealthtools.mdht.uml.cda.consol.FamilyHistoryObservation ccdObservation : ccdFamilyHistoryOrganizer
                                .getFamilyHistoryObservations()) {
                            FamilyHistoryObservation familyHistoryObservation = parseFamilyHistoryObservation(
                                    ccdObservation, resident);
                            if (filterFamilyHistoryObservation(familyHistoryObservation)) {
                                familyHistoryObservation.setFamilyHistory(familyHistory);
                                familyHistoryObservations.add(familyHistoryObservation);
                            }
                        }
                        familyHistory.setFamilyHistoryObservations(familyHistoryObservations);
                    }
                }
            }
            familyHistories.add(familyHistory);
        }

        if (CollectionUtils.isEmpty(familyHistorySection.getFamilyHistories())
                && !CollectionUtils.isEmpty(familyHistorySection.getObservations())) {
            // TODO : is it possible to have non-organized observations in C-CDA R1.1 ?
            // parse non-organized family history observations
            for (Observation observation : familyHistorySection.getObservations()) {
                final FamilyHistory familyHistory = new FamilyHistory();
                familyHistory.setOrganization(resident.getOrganization());
                familyHistory.setClient(resident);
                final RelatedSubject ccdRelatedSubject = observation.getSubject().getRelatedSubject();
                familyHistory.setRelatedSubjectCode(ccdCodeFactory.convert(ccdRelatedSubject.getCode()));

                if (observation.getSubject() != null && observation.getSubject().getRelatedSubject() != null) {
                    if (ccdRelatedSubject.getSubject() != null) {
                        SubjectPerson ccdSubjectPerson = ccdRelatedSubject.getSubject();
                        familyHistory.setAdministrativeGenderCode(
                                ccdCodeFactory.convert(ccdSubjectPerson.getAdministrativeGenderCode()));
                        familyHistory.setBirthTime(CcdParseUtils.convertTsToDate(ccdSubjectPerson.getBirthTime()));
                    }
                    if (observation instanceof org.openhealthtools.mdht.uml.cda.consol.FamilyHistoryObservation) {
                        final FamilyHistoryObservation familyHistoryObservation = parseFamilyHistoryObservation(
                                (org.openhealthtools.mdht.uml.cda.consol.FamilyHistoryObservation) observation,
                                resident);
                        if (filterFamilyHistoryObservation(familyHistoryObservation)) {
                            familyHistoryObservation.setFamilyHistory(familyHistory);
                            familyHistory
                                    .setFamilyHistoryObservations(Collections.singletonList(familyHistoryObservation));
                        }
                    }
                }
                familyHistories.add(familyHistory);
            }
        }

        return familyHistories;
    }

    private boolean filterFamilyHistoryObservation(FamilyHistoryObservation familyHistoryObservation) {
        return (familyHistoryObservation != null && (StringUtils
                .isNotEmpty(familyHistoryObservation.getFreeTextProblemValue())
                || (familyHistoryObservation.getProblemValue() != null
                        && StringUtils.isNotEmpty(familyHistoryObservation.getProblemValue().getDisplayName()))));
    }

    private FamilyHistoryObservation parseFamilyHistoryObservation(
            org.openhealthtools.mdht.uml.cda.consol.FamilyHistoryObservation ccdObservation, Client resident) {
        final FamilyHistoryObservation familyHistoryObservation = new FamilyHistoryObservation();
        familyHistoryObservation.setOrganization(resident.getOrganization());

        familyHistoryObservation.setProblemTypeCode(ccdCodeFactory.convert(ccdObservation.getCode()));
        familyHistoryObservation
                .setEffectiveTime(CcdTransform.IVLTStoCenterDateOrTsToDate(ccdObservation.getEffectiveTime()));

        try {
            CD ccdObservationValue = ObservationFactory.getValue(ccdObservation, CD.class);
            familyHistoryObservation.setProblemValue(ccdCodeFactory.convert(ccdObservationValue));
        } catch (ClassCastException exc) {
            exc.printStackTrace();

            ST ccdObservationValue = ObservationFactory.getValue(ccdObservation, ST.class);
            familyHistoryObservation.setFreeTextProblemValue(ccdObservationValue.getText());
        }

        // TODO test on real examples
        final AgeObservation ageObservation = ccdObservation.getAgeObservation();
        final FamilyHistoryDeathObservation deathObservation = ccdObservation.getFamilyHistoryDeathObservation();
        if (CcdParseUtils.hasContent(ageObservation)) {
            final Pair<String, Integer> observation = ObservationFactory.parseAgeObservation(ageObservation);
            if (observation != null) {
                familyHistoryObservation.setAgeObservationValue(observation.getSecond());
                familyHistoryObservation.setAgeObservationUnit(observation.getFirst());
            }
        }
        if (CcdParseUtils.hasContent(deathObservation)) {
            // This clinical statement records whether the family member is deceased.
            familyHistoryObservation.setDeceased(Boolean.TRUE);
        }

        return familyHistoryObservation;
    }

}
