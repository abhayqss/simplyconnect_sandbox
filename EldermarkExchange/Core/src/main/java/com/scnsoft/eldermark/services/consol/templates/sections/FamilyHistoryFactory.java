package com.scnsoft.eldermark.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.FamilyHistoryObservation;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.templates.OptionalTemplateFactory;
import com.scnsoft.eldermark.services.cda.templates.SectionFactory;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.consol.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

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
@Component("consol.FamilyHistoryFactory")
public class FamilyHistoryFactory extends OptionalTemplateFactory implements SectionFactory<FamilyHistorySection, FamilyHistory> {

    private static final String TEMPLATE_ID_STR = "2.16.840.1.113883.10.20.22.2.15";

    @Value("${section.familyHistory.enabled}")
    private boolean isTemplateIncluded;

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }

    @Override
    public FamilyHistorySection buildTemplateInstance(Collection<FamilyHistory> familyHistories) {
        final FamilyHistorySection familyHistorySection = ConsolFactory.eINSTANCE.createFamilyHistorySection();
        familyHistorySection.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_STR));

        final CE sectionCode = CcdUtils.createCE("10157-6", "Family History", CodeSystem.LOINC);
        familyHistorySection.setCode(sectionCode);

        final ST title = DatatypesFactory.eINSTANCE.createST("Family history");
        familyHistorySection.setTitle(title);

        familyHistorySection.createStrucDocText(buildSectionText(familyHistories));

        if (CollectionUtils.isEmpty(familyHistories)) {
            return familyHistorySection;
        }

        for (FamilyHistory familyHistory : familyHistories) {
            FamilyHistoryOrganizer familyHistoryOrganizer = ConsolFactory.eINSTANCE.createFamilyHistoryOrganizer();
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
        org.openhealthtools.mdht.uml.cda.consol.FamilyHistoryObservation ccdObservation = ConsolFactory.eINSTANCE.createFamilyHistoryObservation();
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
            AgeObservation ageObservation = buildAgeObservation(
                    familyHistoryObservation.getAgeObservationUnit(), familyHistoryObservation.getAgeObservationValue());
            ccdObservation.addObservation(ageObservation);
            ((EntryRelationship) ageObservation.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
            ((EntryRelationship) ageObservation.eContainer()).setInversionInd(Boolean.TRUE);
        }

        if (Boolean.TRUE.equals(familyHistoryObservation.getDeceased())) {
            final FamilyHistoryDeathObservation deathObservation = ConsolFactory.eINSTANCE.createFamilyHistoryDeathObservation();
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
        org.openhealthtools.mdht.uml.cda.consol.FamilyHistoryObservation ccdObservation = ConsolFactory.eINSTANCE.createFamilyHistoryObservation();
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

                if (familyHistory.getRelatedSubjectCode() != null) {
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

    // TODO refactoring: DRY, move to shared utils
    public static AgeObservation buildAgeObservation(String unit, Integer value) {
        final AgeObservation ageObservation = ConsolFactory.eINSTANCE.createAgeObservation();

        ageObservation.setClassCode(ActClassObservation.OBS);
        ageObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);

        ageObservation.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.31"));

        CD ageObservationCode = CcdUtils.createCD(SectionTypeCode.AGE_OBSERVATION);
        ageObservation.setCode(ageObservationCode);

        CS ageObservationStatusCode = DatatypesFactory.eINSTANCE.createCS();
        ageObservationStatusCode.setCode("completed");
        ageObservation.setStatusCode(ageObservationStatusCode);

        PQ ageValue = DatatypesFactory.eINSTANCE.createPQ();
        if (unit != null) {
            ageValue.setUnit(unit);
        } else {
            ageValue.setUnit("a");
        }
        ageValue.setValue(new BigDecimal(value));
        ageObservation.getValues().add(ageValue);

        return ageObservation;
    }

}
