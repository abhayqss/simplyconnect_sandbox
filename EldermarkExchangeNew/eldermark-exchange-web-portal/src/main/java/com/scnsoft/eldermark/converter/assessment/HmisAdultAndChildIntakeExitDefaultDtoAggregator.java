package com.scnsoft.eldermark.converter.assessment;

import static java.util.Objects.nonNull;

import com.scnsoft.eldermark.beans.reports.model.assessment.hmis.FamilyMember;
import com.scnsoft.eldermark.dto.assessment.hmis.intake.HmisAdultChildIntakeExitDefaultsDto;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.service.ClientAssessmentResultService;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class HmisAdultAndChildIntakeExitDefaultDtoAggregator
    implements AssessmentResultDefaultDtoAggregator<HmisAdultChildIntakeExitDefaultsDto> {

    @Autowired
    private ClientAssessmentResultService clientAssessmentResultService;

    @Override
    public String getAssessmentShortName() {
        return Assessment.HMIS_ADULT_CHILD_INTAKE_EXIT;
    }

    @Override
    public HmisAdultChildIntakeExitDefaultsDto aggregateDefaults(Client client, Long parentAssessmentResultId) {
        if (Objects.isNull(parentAssessmentResultId)) {
            throw new BusinessException(BusinessExceptionType.ASSESSMENT_DEFAULT_NOT_AVAILABLE);
        }
        var hmisAdultChildIntakeAssessmentResultOptional =
            clientAssessmentResultService.findHmisAdultChildIntakeAssessmentById(parentAssessmentResultId);
        var result = new HmisAdultChildIntakeExitDefaultsDto();
        if (hmisAdultChildIntakeAssessmentResultOptional.isPresent()) {
            var assessment = hmisAdultChildIntakeAssessmentResultOptional.orElseThrow();

            var programData = new HmisAdultChildIntakeExitDefaultsDto.ProgramData();

            programData.setCaseManager(assessment.getCaseManager());
            result.setProgramData(programData);

            if (nonNull(assessment.getFamilyMember())) {
                result.setFamilyMember(convertFamilyMember(assessment.getFamilyMember(), client,true));
            }

            if (nonNull(assessment.getFamilyMember1())) {
                result.setFamilyMember1(convertFamilyMember(assessment.getFamilyMember1(), client));
            }
            if (nonNull(assessment.getFamilyMember2())) {
                result.setFamilyMember2(convertFamilyMember(assessment.getFamilyMember2(), client));
            }
            if (nonNull(assessment.getFamilyMember3())) {
                result.setFamilyMember3(convertFamilyMember(assessment.getFamilyMember3(), client));
            }
            if (nonNull(assessment.getFamilyMember4())) {
                result.setFamilyMember4(convertFamilyMember(assessment.getFamilyMember4(), client));
            }
            if (nonNull(assessment.getFamilyMember5())) {
                result.setFamilyMember5(convertFamilyMember(assessment.getFamilyMember5(), client));
            }
        }
        return result;
    }

    private HmisAdultChildIntakeExitDefaultsDto.FamilyMember convertFamilyMember(
        FamilyMember dto, Client client, boolean isIndividualApplication
    ) {
        var familyMember = new HmisAdultChildIntakeExitDefaultsDto.FamilyMember();
        var demographics = new HmisAdultChildIntakeExitDefaultsDto.FamilyMember.Demographics();
        var disability = new HmisAdultChildIntakeExitDefaultsDto.FamilyMember.Disability();
        var benefits = new HmisAdultChildIntakeExitDefaultsDto.FamilyMember.Benefits();
        var insurance = new HmisAdultChildIntakeExitDefaultsDto.FamilyMember.Insurance();
        var education = new HmisAdultChildIntakeExitDefaultsDto.FamilyMember.Education();
        var forwardingAddress = new HmisAdultChildIntakeExitDefaultsDto.FamilyMember.ForwardingAddress();

        demographics.setSsnLastFourDigits(dto.getSsn());
        demographics.setFirstName(dto.getFirstName());
        demographics.setLastName(dto.getLastName());
        demographics.setMiddleName(dto.getMiddleName());
        familyMember.setDemographics(demographics);

        disability.setHasPhysicalDisability(dto.getHasPhysicalDisability());
        disability.setIsReceivingServicesForPhysicalDisability(dto.getHasServicesPhysicalDisability());
        disability.setLongTermImpairs(dto.getImpairs());
        disability.setDevelopmentalDisability(dto.getDevDisability());
        disability.setReceivingServicesForDevDisability(dto.getHasServicesDevDisability());
        disability.setChronicHealthCondition(dto.getChronicHealthCondition());
        disability.setReceivingServicesForCHC(dto.getHasServicesCHC());
        disability.setLongTermImpairs2(dto.getImpairs2());
        disability.setHivAids(dto.getHivAids());
        disability.setReceivingServicesForHIVAIDS(dto.getHasServicesHivAids());
        disability.setMentalHealth(dto.getMentalHealth());
        disability.setReceivingServicesForMH(dto.getHasServicesMH());
        disability.setLongTermImpairsAbility(dto.getImpairsAbility());
        disability.setSubstanceAbuse(dto.getSubstanceAbuse());
        disability.setReceivingServicesForSA(dto.getHasServicesSA());
        disability.setLongTermImpairsAbility2(dto.getImpairsAbility2());
        disability.setDisablingCondition(dto.getDisablingCondition());
        disability.setDomesticViolence(dto.getDomesticViolence());
        disability.setAreYouCurrentlyFleeing(dto.getFleeing());
        disability.setIsPregnant(dto.getPregnant());
        disability.setDueDate(dto.getDueDate());
        disability.setLastDateOfDV(dto.getDomesticViolenceDate());
        familyMember.setDisability(disability);

        benefits.setCashIncome(dto.getCashIncome());
        benefits.setSourceOfCashIncome(dto.getCashIncomeSource());
        benefits.setEmploymentIncomeAmount(dto.getEmploymentIncomeAmount());
        benefits.setUnemploymentInsAmount(dto.getUnemploymentInsAmount());
        benefits.setWorkersCompAmount(dto.getWorkersCompAmount());
        benefits.setPrivateDisabilityInsAmount(dto.getPrivateDisabilityInsAmount());
        benefits.setVaDisabilityAmount(dto.getVaDisabilityAmount());
        benefits.setSsdiAmount(dto.getSsdiAmount());
        benefits.setSsiAmount(dto.getSsiAmount());
        benefits.setSsaAmount(dto.getSsaAmount());
        benefits.setVaPensionAmount(dto.getVaPensionAmount());
        benefits.setPensionFromFormerFobAmount(dto.getPensionFromFormerFobAmount());
        benefits.setTanfCalWorksAmount(dto.getTanfCalWorksAmount());
        benefits.setGaAmount(dto.getGaAmount());
        benefits.setAlimonySpousalSupportAmount(dto.getAlimonySpousalSupportAmount());
        benefits.setChildSupportAmount(dto.getChildSupportAmount());
        benefits.setOtherKidsAmount(dto.getOtherKidsAmount());
        benefits.setTotalAmount(dto.getTotalAmount());
        benefits.setNonCashBenefits(dto.getNonCashBenefits());
        benefits.setSourcesOfNonCashBenefits(dto.getSourcesNonCashBenefits());
        benefits.setOtherTANFBenefits(dto.getOtherTanfBenefits());
        benefits.setOtherExIHSSParatransitPG(dto.getOtherExIHSSParatransitPG());
        familyMember.setBenefits(benefits);

        insurance.setHealthInsurance(dto.getHealthInsurance());
        insurance.setIfCoveredSourceOfHealthInsurance(dto.getCoveredSourceHealthInsurance());
        insurance.setOtherSourceOfHealthInsurance(dto.getOtherSourceHealthInsurance());
        insurance.setEmployed(dto.getEmployed());
        insurance.setEmploymentTenure(dto.getEmploymentTenure());
        insurance.setHoursWorkedLastWeek(dto.getHoursWorked());
        insurance.setIfUnemployedSeeking(dto.getUnemployedSeeking());
        familyMember.setInsurance(insurance);

        education.setSchoolCurrentlyEnrolled(dto.getSchoolEnrolled());
        education.setVocationalCurrentlyEnrolled(dto.getVocationalEnrolled());
        education.setIfNotEnrolledLastDateOfEnrollment(dto.getIfNotEnrolled());
        education.setHighestLevelOfSchoolCompletedORCurrentGradeEnrolled(dto.getHighestLevel());
        education.setBarriersToEnrollingChildInSchool(dto.getBarriersToEnrollingChildInSchool());
        education.setBarriersToEnrollingChildInSchoolOther(dto.getBarriersToEnrollingChildInSchoolOther());
        education.setHighestDegreeEarned(dto.getHighestDegree());
        education.setSchoolName(dto.getSchoolName());
        education.setHudHomelessLiaison(dto.getHudHomeless());
        education.setTypeOfSchool(dto.getTypeOfSchool());
        familyMember.setEducation(education);

        if (isIndividualApplication) {
            var person = client.getPerson();
            forwardingAddress.setEmail(PersonTelecomUtils.findValue(person, PersonTelecomCode.EMAIL).orElse(null));
            forwardingAddress.setPhone(PersonTelecomUtils.findValue(person, PersonTelecomCode.MC).orElse(null));

            var addresses = person.getAddresses();
            Stream.ofNullable(addresses)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(address -> {
                    forwardingAddress.setStreetAddress(address.getStreetAddress());
                    forwardingAddress.setCity(address.getCity());
                    forwardingAddress.setState(address.getState());
                    forwardingAddress.setZipCode(address.getPostalCode());
                });
            familyMember.setForwardingAddress(forwardingAddress);
        }


        return familyMember;
    }

    private HmisAdultChildIntakeExitDefaultsDto.FamilyMember convertFamilyMember(
        FamilyMember dto, Client client
    ) {
        return convertFamilyMember(dto, client, false);
    }
}
