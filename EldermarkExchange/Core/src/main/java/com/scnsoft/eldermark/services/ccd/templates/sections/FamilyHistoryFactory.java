package com.scnsoft.eldermark.services.ccd.templates.sections;

import com.scnsoft.eldermark.entity.CodeSystem;
import com.scnsoft.eldermark.entity.FamilyHistory;
import com.scnsoft.eldermark.entity.FamilyHistoryObservation;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.ObservationFactory;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.OptionalTemplateFactory;
import com.scnsoft.eldermark.services.cda.templates.ParsableSectionFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.ccd.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.scnsoft.eldermark.entity.CodeSystem.*;

/**
 * <h1>Family History</h1>
 * “This section contains data defining the patient’s genetic relatives in terms of possible or relevant
 * health risk factors that have a potential impact on the patient’s healthcare risk profile.” [CCD 3.6]
 *
 * <h2>Template ID = 2.16.840.1.113883.10.20.22.4.46</h2>
 * Family History Observations related to a particular family member are contained within a Family History Organizer.
 * The effectiveTime in the Family History Observation is the biologically or clinically relevant time of the observation.
 * The biologically or clinically relevant time is the time at which the observation holds (is effective) for
 * the family member (the subject of the observation).
 *
 * @see FamilyHistory
 * @see FamilyHistoryObservation
 * @see Resident
 */
@Component
public class FamilyHistoryFactory extends OptionalTemplateFactory implements ParsableSectionFactory<FamilyHistorySection, FamilyHistory> {

    @Autowired
    private CcdCodeFactory ccdCodeFactory;

    private static final Logger logger = LoggerFactory.getLogger(FamilyHistoryFactory.class);

    @Value("${section.familyHistory.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    @Override
    public FamilyHistorySection buildTemplateInstance(Collection<FamilyHistory> familyHistories) {
        final FamilyHistorySection familyHistorySection = CCDFactory.eINSTANCE.createFamilyHistorySection();
        familyHistorySection.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.1.4"));

        final CE sectionCode = CcdUtils.createCE("10157-6", "Family History", CodeSystem.LOINC);
        familyHistorySection.setCode(sectionCode);

        final ST title = DatatypesFactory.eINSTANCE.createST("Family history");
        familyHistorySection.setTitle(title);

        familyHistorySection.createStrucDocText(buildSectionText(familyHistories));

        if (CollectionUtils.isEmpty(familyHistories)) {
            return familyHistorySection;
        }

        for (FamilyHistory familyHistory : familyHistories) {
            FamilyHistoryOrganizer familyHistoryOrganizer = CCDFactory.eINSTANCE.createFamilyHistoryOrganizer();
            familyHistorySection.addOrganizer(familyHistoryOrganizer);

            familyHistoryOrganizer.setClassCode(x_ActClassDocumentEntryOrganizer.CLUSTER);
            familyHistoryOrganizer.setMoodCode(ActMood.EVN);


            II fhTemplateId = DatatypesFactory.eINSTANCE.createII();
            fhTemplateId.setRoot("2.16.840.1.113883.10.20.22.4.45");
            familyHistoryOrganizer.getTemplateIds().add(fhTemplateId);

            familyHistoryOrganizer.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

            Subject subject = CDAFactory.eINSTANCE.createSubject();
            familyHistoryOrganizer.setSubject(subject);

            RelatedSubject relatedSubject = CDAFactory.eINSTANCE.createRelatedSubject();
            subject.setRelatedSubject(relatedSubject);

            relatedSubject.setClassCode(x_DocumentSubject.PRS);

            relatedSubject.setCode(CcdUtils.createCE(familyHistory.getRelatedSubjectCode(), ROLE_CODE.getOid()));

            if (familyHistory.getAdministrativeGenderCode() != null) {
                SubjectPerson personInfoSubject = CDAFactory.eINSTANCE.createSubjectPerson();
                relatedSubject.setSubject(personInfoSubject);

                personInfoSubject.setAdministrativeGenderCode(CcdUtils.createCE(familyHistory.getAdministrativeGenderCode(), ADMINISTRATIVE_GENDER.getOid()));

                if (familyHistory.getBirthTime() != null) {
                    personInfoSubject.setBirthTime(DatatypesFactory.eINSTANCE.createTS(CcdUtils.formatSimpleDate(familyHistory.getBirthTime())));
                }

//              if (familyHistory.getPersonInformationId() != null) {
//                  personInfoSubject.getSDTCIds().add(CcdUtils.getId(familyHistory.getPersonInformationId().toString()));
//              }
//              if (familyHistory.getDeceasedInd() != null) {
//                  personInfoSubject.setSDTCDeceasedInd(DatatypesFactory.eINSTANCE.createBL(familyHistory.getDeceasedInd()));
//              }
//              if (familyHistory.getDeceasedTime() != null) {
//                  personInfoSubject.setSDTCDeceasedTime(DatatypesFactory.eINSTANCE.createTS(CcdUtils.formatSimpleDate(familyHistory.getDeceasedTime())));
//              }
            }

            List<FamilyHistoryObservation> familyHistoryObservations = familyHistory.getFamilyHistoryObservations();
            if (!CollectionUtils.isEmpty(familyHistoryObservations)) {
                for (FamilyHistoryObservation familyHistoryObservation : familyHistoryObservations) {
                    familyHistoryOrganizer.addObservation(buildFamilyHistoryObservation(familyHistoryObservation));
                }
            } else {
                familyHistoryOrganizer.addObservation(buildNullFamilyHistoryObservation());
            }
        }

        return familyHistorySection;
    }

    private Observation buildFamilyHistoryObservation(FamilyHistoryObservation familyHistoryObservation) {
        org.openhealthtools.mdht.uml.cda.ccd.FamilyHistoryObservation ccdObservation = CCDFactory.eINSTANCE.createFamilyHistoryObservation();
        ccdObservation.setClassCode(ActClassObservation.OBS);
        ccdObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);

        ccdObservation.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.46"));

        ccdObservation.getIds().add(CcdUtils.getId(familyHistoryObservation.getId()));

        // [CONF-8589] SHALL contain exactly one [1..1] code, which SHOULD be selected from ValueSet Problem Type 2.16.840.1.113883.3.88.12.3221.7.2 STATIC 2012-06-01
        ccdObservation.setCode(CcdUtils.createCE(familyHistoryObservation.getProblemTypeCode(), CodeSystem.SNOMED_CT.getOid()));

        ccdObservation.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

        if (familyHistoryObservation.getEffectiveTime() != null) {
            ccdObservation.setEffectiveTime(CcdUtils.convertEffectiveTime(familyHistoryObservation.getEffectiveTime()));
        }

        // [CONF-8591] SHALL contain exactly one [1..1] value with @xsi:type="CD", where the code SHALL be selected from ValueSet Problem 2.16.840.1.113883.3.88.12.3221.7.4 DYNAMIC.
        CD cdValue;
        if (familyHistoryObservation.getProblemValue() != null) {
            cdValue = CcdUtils.createCD(familyHistoryObservation.getProblemValue(), SNOMED_CT.getOid());
        } else if (!StringUtils.isEmpty(familyHistoryObservation.getFreeTextProblemValue())) {
            // TODO: try to lookup SNOMED-CT code by display name (something like this - startsWith(getFreeTextProblemValue().toLowerCase())) in code dictionary
            cdValue = CcdUtils.createOtherCode(familyHistoryObservation.getFreeTextProblemValue(), CodeSystem.SNOMED_CT);
        } else {
            cdValue = CcdUtils.createNillCode();
        }
        ccdObservation.getValues().add(cdValue);

        // Age Observation
        if (familyHistoryObservation.getAgeObservationValue() != null) {
            AgeObservation ageObservation = SectionEntryFactory.buildAgeObservation(
                    familyHistoryObservation.getAgeObservationUnit(), familyHistoryObservation.getAgeObservationValue());
            ccdObservation.addObservation(ageObservation);
            ((EntryRelationship) ageObservation.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
            ((EntryRelationship) ageObservation.eContainer()).setInversionInd(Boolean.TRUE);
        }

        if (familyHistoryObservation.getDeceased() != null && familyHistoryObservation.getDeceased()) {

            FamilyHistoryCauseOfDeathObservation deathObservation = CCDFactory.eINSTANCE.createFamilyHistoryCauseOfDeathObservation();
            ccdObservation.addObservation(deathObservation);
            ((EntryRelationship) deathObservation.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.CAUS);

            deathObservation.setClassCode(ActClassObservation.OBS);
            deathObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);
            deathObservation.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.47"));
            deathObservation.setCode(DatatypesFactory.eINSTANCE.createCE("ASSERTION", HL7_ACT_CODE.getOid()));
            deathObservation.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

            CD dValue = CcdUtils.createCD("419099009", "Dead", CodeSystem.SNOMED_CT);
            deathObservation.getValues().add(dValue);
        }

        return ccdObservation;
    }

    private Observation buildNullFamilyHistoryObservation() {
        org.openhealthtools.mdht.uml.cda.ccd.FamilyHistoryObservation ccdObservation = CCDFactory.eINSTANCE.createFamilyHistoryObservation();
        ccdObservation.setClassCode(ActClassObservation.OBS);
        ccdObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);

        ccdObservation.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.46"));

        ccdObservation.getIds().add(CcdUtils.getNullId());

        ccdObservation.setCode(CcdUtils.createNillCode());

        ccdObservation.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

        return ccdObservation;
    }

    private static String buildSectionText(Collection<FamilyHistory> familyHistories) {

        if (CollectionUtils.isEmpty(familyHistories)) {
            return "No known family history";
        }

        StringBuilder sectionText = new StringBuilder();

        for (FamilyHistory familyHistory : familyHistories) {

            List<FamilyHistoryObservation> familyHistoryObservations = familyHistory.getFamilyHistoryObservations();
            if (!CollectionUtils.isEmpty(familyHistoryObservations)) {

                if (familyHistory.getRelatedSubjectCode()!=null) {
                    sectionText.append("<paragraph>")
                            .append(familyHistory.getRelatedSubjectCode().getDisplayName());
                }
                if (familyHistory.getDeceasedInd() == Boolean.TRUE) {
                    sectionText.append(" (deceased)");
                }
                sectionText.append("</paragraph>");

                sectionText.append("<table>");
                sectionText.append("<thead>");
                sectionText.append("<tr>");
                sectionText.append("<th>Diagnosis</th>");
                sectionText.append("<th>Age At Onset</th>");
                sectionText.append("</tr>");
                sectionText.append("</thead>");
                sectionText.append("<tbody>");

                for (FamilyHistoryObservation familyHistoryObservation : familyHistoryObservations) {

                    if (familyHistoryObservation.getProblemValue() != null ||
                            !StringUtils.isEmpty(familyHistoryObservation.getFreeTextProblemValue())) {
                        sectionText.append("<tr>");
                        String diagnosis = (familyHistoryObservation.getProblemValue() != null) ?
                                familyHistoryObservation.getProblemValue().getDisplayName() : familyHistoryObservation.getFreeTextProblemValue();
                        if (familyHistoryObservation.getDeceased() == Boolean.TRUE) {
                            diagnosis += " (cause of death)";
                        }
                        CcdUtils.addCellToSectionText(diagnosis, sectionText);
                        if (familyHistoryObservation.getAgeObservationValue() != null) {
                            CcdUtils.addCellToSectionText(familyHistoryObservation.getAgeObservationValue().toString(),
                                    sectionText);
                        } else {
                            CcdUtils.addEmptyCell(sectionText);
                        }
                        sectionText.append("</tr>");
                    }
                }
            }
            sectionText.append("</tbody>");
            sectionText.append("</table>");
        }

        return sectionText.toString();
    }

    @Override
    public List<FamilyHistory> parseSection(Resident resident, FamilyHistorySection familyHistorySection) {
        if (!CcdParseUtils.hasContent(familyHistorySection) ||
                (CollectionUtils.isEmpty(familyHistorySection.getFamilyHistoryOrganizers()) && CollectionUtils.isEmpty(familyHistorySection.getObservations()))) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        // parse organized family history observations
        List<FamilyHistory> familyHistories = new ArrayList<>();
        for (FamilyHistoryOrganizer ccdFamilyHistoryOrganizer : familyHistorySection.getFamilyHistoryOrganizers()) {
            FamilyHistory familyHistory = new FamilyHistory();
            familyHistory.setDatabase(resident.getDatabase());
            familyHistory.setResident(resident);
            if (ccdFamilyHistoryOrganizer.getSubject() != null &&
                    ccdFamilyHistoryOrganizer.getSubject().getRelatedSubject() != null) {
                RelatedSubject ccdRelatedSubject = ccdFamilyHistoryOrganizer.getSubject().getRelatedSubject();
                familyHistory.setRelatedSubjectCode(ccdCodeFactory.convert(ccdRelatedSubject.getCode()));
                if (ccdRelatedSubject.getSubject() != null) {
                    SubjectPerson ccdSubjectPerson = ccdRelatedSubject.getSubject();
                    familyHistory.setAdministrativeGenderCode(ccdCodeFactory.convert(
                            ccdSubjectPerson.getAdministrativeGenderCode()));
                    familyHistory.setBirthTime(CcdParseUtils.convertTsToDate(ccdSubjectPerson.getBirthTime()));
                    if (!CollectionUtils.isEmpty(ccdFamilyHistoryOrganizer.getObservations())) {
                        List<FamilyHistoryObservation> familyHistoryObservations = new ArrayList<>();
                        for (Observation ccdObservation : ccdFamilyHistoryOrganizer.getObservations()) {
                            FamilyHistoryObservation familyHistoryObservation = parseFamilyHistoryObservation(ccdObservation, resident);
                            if (familyHistoryObservation != null) {
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

        if (CollectionUtils.isEmpty(familyHistorySection.getFamilyHistoryOrganizers()) &&
                !CollectionUtils.isEmpty(familyHistorySection.getObservations())) {
            // parse non-organized family history observations
            for (Observation observation : familyHistorySection.getObservations()) {
                FamilyHistory familyHistory = new FamilyHistory();
                familyHistory.setDatabase(resident.getDatabase());
                familyHistory.setResident(resident);
                RelatedSubject ccdRelatedSubject = observation.getSubject().getRelatedSubject();
                familyHistory.setRelatedSubjectCode(ccdCodeFactory.convert(ccdRelatedSubject.getCode()));

                if (observation.getSubject() != null && observation.getSubject().getRelatedSubject() != null) {
                    if (ccdRelatedSubject.getSubject() != null) {
                        SubjectPerson ccdSubjectPerson = ccdRelatedSubject.getSubject();
                        familyHistory.setAdministrativeGenderCode(ccdCodeFactory.convert(
                                ccdSubjectPerson.getAdministrativeGenderCode()));
                        familyHistory.setBirthTime(CcdParseUtils.convertTsToDate(ccdSubjectPerson.getBirthTime()));
                    }
                    final FamilyHistoryObservation familyHistoryObservation = parseFamilyHistoryObservation(observation, resident);
                    if (familyHistoryObservation != null) {
                        familyHistoryObservation.setFamilyHistory(familyHistory);
                        familyHistory.setFamilyHistoryObservations(Arrays.asList(familyHistoryObservation));
                    }
                }

                familyHistories.add(familyHistory);
            }
        }

        return familyHistories;
    }

    private FamilyHistoryObservation parseFamilyHistoryObservation(Observation ccdObservation, Resident resident) {
        FamilyHistoryObservation familyHistoryObservation = new FamilyHistoryObservation();
        familyHistoryObservation.setDatabase(resident.getDatabase());

        familyHistoryObservation.setProblemTypeCode(ccdCodeFactory.convert(ccdObservation.getCode()));
        familyHistoryObservation.setEffectiveTime(CcdParseUtils.convertTsToDate(ccdObservation.getEffectiveTime()));

        try {
            CD ccdObservationValue = ObservationFactory.getValue(ccdObservation, CD.class);
            familyHistoryObservation.setProblemValue(ccdCodeFactory.convert(ccdObservationValue));
        } catch (ClassCastException exc) {
            exc.printStackTrace();

            ST ccdObservationValue = ObservationFactory.getValue(ccdObservation, ST.class);
            familyHistoryObservation.setFreeTextProblemValue(ccdObservationValue.getText());
        }

        if (!CollectionUtils.isEmpty(ccdObservation.getObservations())) {
            for (Observation ageOrDeathObservation : ccdObservation.getObservations()) {
                if (ageOrDeathObservation instanceof AgeObservation) {
                    Pair<String, Integer> observation = SectionEntryParseFactory.parseAgeObservation((AgeObservation) ageOrDeathObservation);
                    if (observation != null) {
                        familyHistoryObservation.setAgeObservationValue(observation.getSecond());
                        familyHistoryObservation.setAgeObservationUnit(observation.getFirst());
                    }
                } else if (ageOrDeathObservation instanceof FamilyHistoryCauseOfDeathObservation) {
                    familyHistoryObservation.setDeceased(Boolean.TRUE);
                }
            }
        }

        return familyHistoryObservation;
    }

}
