package com.scnsoft.eldermark.converter.assessment;

import com.scnsoft.eldermark.dto.assessment.hmis.intake.HmisAdultChildIntakeAssessmentDefaultsDto;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.service.ClientAssessmentResultService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class HmisAdultAndChildIntakeDefaultDtoAggregator implements AssessmentResultDefaultDtoAggregator<HmisAdultChildIntakeAssessmentDefaultsDto> {

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ClientAssessmentResultService clientAssessmentResultService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    public String getAssessmentShortName() {
        return Assessment.HMIS_ADULT_CHILD_INTAKE;
    }

    @Override
    public HmisAdultChildIntakeAssessmentDefaultsDto aggregateDefaults(Client client, Long parentAssessmentResultId) {
        if (Objects.nonNull(parentAssessmentResultId)){
            throw new BusinessException(BusinessExceptionType.ASSESSMENT_CREATE_NOT_AVAILABLE);
        }
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var norCalComprehensiveAssessmentOptional =  clientAssessmentResultService.findLatestInProgressOrCompletedNorCalComprehensiveByClientId(client.getId(), permissionFilter);
        HmisAdultChildIntakeAssessmentDefaultsDto result = new HmisAdultChildIntakeAssessmentDefaultsDto();
        HmisAdultChildIntakeAssessmentDefaultsDto.ProgramData programData = new HmisAdultChildIntakeAssessmentDefaultsDto.ProgramData();
        programData.setCaseManager(loggedUserService.getCurrentEmployee().getFullName());
        result.setProgramData(programData);
        HmisAdultChildIntakeAssessmentDefaultsDto.IndividualMember individualMember = new HmisAdultChildIntakeAssessmentDefaultsDto.IndividualMember();
        individualMember.setSsnLastFourDigits(client.getSsnLastFourDigits());
        individualMember.setBirthDate(DateTimeUtils.formatLocalDate(client.getBirthDate()));
        individualMember.setFirstName(client.getFirstName());
        individualMember.setLastName(client.getLastName());
        individualMember.setZipCodeOfLastPermanentAddress(CollectionUtils.isNotEmpty(client.getPerson().getAddresses()) ? client.getPerson().getAddresses().get(0).getPostalCode() : null);
        if (norCalComprehensiveAssessmentOptional.isPresent()) {
            var assessment = norCalComprehensiveAssessmentOptional.orElseThrow();
            individualMember.setQualityOfDOB(assessment.getQualityOfDOB());
            individualMember.setAlias(assessment.getAlias());
            individualMember.setSuffix(assessment.getSuffix());
            individualMember.setQualityOfName(assessment.getQualityOfName());
            individualMember.setGenderAssignedAtBirth(assessment.getGenderAssignedAtBirth());
            individualMember.setSexualOrientation(assessment.getSexualOrientation());
            individualMember.setQualityOfZipCode(assessment.getQualityOfZipCode());
            individualMember.setPrimaryLanguage(assessment.getPrimaryLanguage());
            individualMember.setIsUSCitizen(assessment.getIsUSCitizen());
            individualMember.setIsVeteran(assessment.getIsVeteran());
            individualMember.setWorldWarIIOperations(assessment.getWorldWarIIOperations());
            individualMember.setKoreanWarOperations(assessment.getKoreanWarOperations());
            individualMember.setVietnamWarOperations(assessment.getVietnamWarOperations());
            individualMember.setPersianGulfWarOperations(assessment.getPersianGulfWarOperations());
            individualMember.setAfghanistanWarOperations(assessment.getAfghanistanWarOperations());
            individualMember.setIraqFreedomWarOperations(assessment.getIraqFreedomWarOperations());
            individualMember.setIraqNewDawnWarOperations(assessment.getIraqNewDawnWarOperations());
            individualMember.setOtherWarOperations(assessment.getOtherWarOperations());
            individualMember.setMilitaryBranch(assessment.getMilitaryBranch());
            individualMember.setDischargeStatus(assessment.getDischargeStatus());
            individualMember.setDischargeStatusVerified(assessment.getDischargeStatusVerified());
        }
        result.setIndividualMember(individualMember);
        return result;
    }
}
