package com.scnsoft.eldermark.service.report.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.beans.projection.*;
import com.scnsoft.eldermark.beans.reports.constants.ArizonaMatrixConstants;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.arizona.ArizonaMatrixReport;
import com.scnsoft.eldermark.beans.reports.model.arizona.ArizonaMatrixReportRow;
import com.scnsoft.eldermark.beans.reports.model.arizona.ArizonaMatrixReportRowAssessment;
import com.scnsoft.eldermark.beans.reports.model.arizona.ArizonaMatrixReportRowClient;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ClientAssessmentDao;
import com.scnsoft.eldermark.dao.specification.ClientAssessmentResultSpecificationGenerator;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult_;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.service.AssessmentScoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ArizonaMatrixReportGenerator extends DefaultReportGenerator<ArizonaMatrixReport> {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientAssessmentDao clientAssessmentDao;

    @Autowired
    private ClientAssessmentResultSpecificationGenerator clientAssessmentResultSpecificationGenerator;

    @Autowired
    private AssessmentScoringService assessmentScoringService;

    @Override
    public ReportType getReportType() {
        return ReportType.ARIZONA_MATRIX;
    }

    @Override
    public ArizonaMatrixReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var hasAccess = clientAssessmentResultSpecificationGenerator.hasAccess(permissionFilter);
        var ofCommunities = clientAssessmentResultSpecificationGenerator.ofCommunities(filter.getAccessibleCommunityIdsAndNames());
        var completedWithinPeriod = clientAssessmentResultSpecificationGenerator.completedWithinPeriod(filter.getInstantFrom(), filter.getInstantTo());
        var arizonaType = clientAssessmentResultSpecificationGenerator.byType(Assessment.ARIZONA_SSM);
        var unarchived = clientAssessmentResultSpecificationGenerator.isUnarchived();

        var assessments = clientAssessmentDao.findAll(hasAccess.and(ofCommunities.and(completedWithinPeriod.and(arizonaType.and(unarchived)))), AssessmentDataProjection.class, Sort.by(ClientAssessmentResult_.DATE_COMPLETED));

        var rowsMapByCommunityId = new HashMap<Long, ArizonaMatrixReportRow>();
        var clientRowsMap = new HashMap<Long, ArizonaMatrixReportRowClient>();

        assessments.forEach(assessment -> {
            var row = rowsMapByCommunityId.computeIfAbsent(assessment.getClientCommunityId(), communityId -> createRow(assessment));
            var client = clientRowsMap.computeIfAbsent(assessment.getClientId(), clientId -> createRowClient(row, assessment));
            createRowAssessment(client, assessment);
        });

        var report = new ArizonaMatrixReport();
        populateReportingCriteriaFields(filter, report);
        report.setRows(rowsMapByCommunityId.values().stream().sorted(Comparator.comparing(ArizonaMatrixReportRow::getCommunityName)).peek(row -> row.getClients().sort(Comparator.comparing(ArizonaMatrixReportRowClient::getClientName))).collect(Collectors.toList()));

        return report;
    }

    private ArizonaMatrixReportRow createRow(AssessmentDataProjection assessment) {
        var row = new ArizonaMatrixReportRow();
        row.setCommunityName(assessment.getClientCommunityName());
        row.setClients(new ArrayList<>());
        return row;
    }

    private ArizonaMatrixReportRowClient createRowClient(ArizonaMatrixReportRow row, AssessmentDataProjection assessment) {
        var client = new ArizonaMatrixReportRowClient();
        client.setClientId(assessment.getClientId());
        client.setClientName(assessment.getClientFullName());
        client.setDateOfBirth(assessment.getClientBirthDate());
        client.setAssessments(new ArrayList<>());
        row.getClients().add(client);
        return client;
    }

    private void createRowAssessment(ArizonaMatrixReportRowClient client, AssessmentDataProjection assessment) {
        var rowAssessment = new ArizonaMatrixReportRowAssessment();
        rowAssessment.setCaseManagerName(assessment.getEmployeeFullName());
        rowAssessment.setAssessmentDate(assessment.getDateStarted());

        try {
            var result = objectMapper.readValue(assessment.getResult(), new TypeReference<HashMap<String, Object>>() {
            });
            rowAssessment.setProgramName(extractStringListField(result, ArizonaMatrixConstants.PROGRAM_NAME));
            rowAssessment.setAssessmentType(extractStringField(result, ArizonaMatrixConstants.ASSESSMENT_TYPE));
            rowAssessment.setSurveyFrequency(extractStringField(result, ArizonaMatrixConstants.SURVEY_FREQUENCY));
            rowAssessment.setAssessmentTotalScore(assessmentScoringService.calculateScore(assessment.getAssessmentId(), result));
            rowAssessment.setIncome(extractStringField(result, ArizonaMatrixConstants.INCOME));
            rowAssessment.setCreditStatus(extractStringField(result, ArizonaMatrixConstants.CREDIT_STATUS));
            rowAssessment.setEmployment(extractStringField(result, ArizonaMatrixConstants.EMPLOYMENT));
            rowAssessment.setShelter(extractStringField(result, ArizonaMatrixConstants.SHELTER));
            rowAssessment.setFood(extractStringField(result, ArizonaMatrixConstants.FOOD));
            rowAssessment.setChildCare(extractStringField(result, ArizonaMatrixConstants.CHILD_CARE));
            rowAssessment.setKindsOfChildCare(extractStringListField(result, ArizonaMatrixConstants.KINDS_OF_CHILD_CARE));
            rowAssessment.setChildrenEducation(extractStringField(result, ArizonaMatrixConstants.CHILDREN_EDUCATION));
            rowAssessment.setAdultEducation(extractStringField(result, ArizonaMatrixConstants.ADULT_EDUCATION));
            rowAssessment.setHighestGrade(extractStringField(result, ArizonaMatrixConstants.HIGHEST_GRADE));
            rowAssessment.setLegal(extractStringField(result, ArizonaMatrixConstants.LEGAL));
            rowAssessment.setConvictedOf(extractStringListField(result, ArizonaMatrixConstants.CONVICTED_OF));
            rowAssessment.setConvictedAndChargedWith(extractStringListField(result, ArizonaMatrixConstants.CONVICTED_AND_CHARGED_WITH));
            rowAssessment.setIs290Registrant(extractStringField(result, ArizonaMatrixConstants.IS_290_REGISTRANT));
            rowAssessment.setHealthCareCoverage(extractStringField(result, ArizonaMatrixConstants.HEALTH_CARE_COVERAGE));
            rowAssessment.setLifeSkills(extractStringField(result, ArizonaMatrixConstants.LIFE_SKILLS));
            rowAssessment.setMentalHealth(extractStringField(result, ArizonaMatrixConstants.MENTAL_HEALTH));
            rowAssessment.setSubstanceAbuse(extractStringField(result, ArizonaMatrixConstants.SUBSTANCE_ABUSE));
            rowAssessment.setFamilyAndSocialRelations(extractStringField(result, ArizonaMatrixConstants.FAMILY_AND_SOCIAL_RELATIONS));
            rowAssessment.setTransportation(extractStringField(result, ArizonaMatrixConstants.TRANSPORTATION));
            rowAssessment.setCommunityInvolvement(extractStringField(result, ArizonaMatrixConstants.COMMUNITY_INVOLVEMENT));
            rowAssessment.setSafety(extractStringField(result, ArizonaMatrixConstants.SAFETY));
            rowAssessment.setGangAffiliation(extractStringField(result, ArizonaMatrixConstants.GANG_AFFILIATION));
            rowAssessment.setParentingSkills(extractStringField(result, ArizonaMatrixConstants.PARENTING_SKILLS));
            rowAssessment.setActiveCpsCase(extractStringField(result, ArizonaMatrixConstants.ACTIVE_CPS_CASE));
            rowAssessment.setPreviousCpsInvolvement(extractStringField(result, ArizonaMatrixConstants.PREVIOUS_CPS_INVOLVEMENT));
            rowAssessment.setDisabilities(extractStringField(result, ArizonaMatrixConstants.DISABILITIES));
        } catch (JsonProcessingException e) {
            throw new InternalServerException(InternalServerExceptionType.ASSESSMENT_EXPORT_FAILURE);
        }
        client.getAssessments().add(rowAssessment);
    }

    private String extractStringField(Map<String, Object> json, String fieldName) {
        var field = json.get(fieldName);
        return field != null ? field.toString() : null;
    }

    private List<String> extractStringListField(Map<String, Object> json, String fieldName) {
        var field = json.get(fieldName);
        if (field instanceof List) {
            var listField = (List<?>) field;
            return listField.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    private interface AssessmentDataProjection extends ClientIdNamesAware, EmployeeNamesAware, ClientCommunityIdNameAware {
        LocalDate getClientBirthDate();
        String getResult();
        Instant getDateStarted();
        Long getAssessmentId();
    }
}
