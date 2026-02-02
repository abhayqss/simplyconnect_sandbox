package com.scnsoft.eldermark.service.report.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.beans.projection.ClientCommunityIdNameAware;
import com.scnsoft.eldermark.beans.projection.ClientIdNamesAware;
import com.scnsoft.eldermark.beans.projection.EmployeeNamesAware;
import com.scnsoft.eldermark.beans.reports.constants.ArizonaMatrixConstants;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.arizona.ArizonaMatrixMonthlyReport;
import com.scnsoft.eldermark.beans.reports.model.arizona.ArizonaMatrixMonthlyReportRow;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ClientAssessmentDao;
import com.scnsoft.eldermark.dao.specification.ClientAssessmentResultSpecificationGenerator;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult_;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.service.AssessmentScoringService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ArizonaMatrixMonthlyReportGenerator extends DefaultReportGenerator<ArizonaMatrixMonthlyReport> {

    private static final Sort ASSESSMENT_RESULTS_SORT = Sort.by(
        String.join(".", ClientAssessmentResult_.CLIENT, Client_.COMMUNITY_ID),
        String.join(".", ClientAssessmentResult_.CLIENT, Client_.COMMUNITY, Community_.NAME),
        String.join(".", ClientAssessmentResult_.CLIENT, Client_.FIRST_NAME),
        String.join(".", ClientAssessmentResult_.CLIENT, Client_.LAST_NAME),
        ClientAssessmentResult_.DATE_STARTED
    );

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
        return ReportType.ARIZONA_MATRIX_MONTHLY;
    }

    @Override
    public ArizonaMatrixMonthlyReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new ArizonaMatrixMonthlyReport();
        populateReportingCriteriaFields(filter, report);

        var data = extractData(filter, permissionFilter);

        var parsedData = data.stream()
            .flatMap(assessment -> {
                var parsedResult = parseAssessmentResult(assessment);
                var followUp = parsedResult.get(ArizonaMatrixConstants.SURVEY_FREQUENCY).toString();

                return getFollowUpMonthCount(followUp)
                    .map(followUpMonthCount -> {
                        var row = new ArizonaMatrixMonthlyReportRow();
                        row.setClientId(assessment.getClientId());
                        row.setClientName(assessment.getClientFullName());
                        row.setCommunityName(assessment.getClientCommunityName());
                        row.setAssessmentDate(assessment.getDateStarted());
                        row.setCompletedBy(assessment.getEmployeeFullName());
                        row.setFollowUp(followUp);
                        row.setTotalScore(assessmentScoringService.calculateScore(assessment.getAssessmentId(), parsedResult));
                        row.setFollowUpDate(DateTimeUtils.plusMonths(assessment.getDateStarted(), followUpMonthCount));
                        return row;
                    })
                    .stream();
            })
            .filter(it -> it.getFollowUpDate().isBefore(filter.getInstantTo()))
            .collect(Collectors.toList());

        var now = Instant.now();
        report.setMissing(
            parsedData.stream()
                .filter(it -> it.getFollowUpDate().isBefore(now))
                .collect(Collectors.toList())
        );
        report.setUpcoming(
            parsedData.stream()
                .filter(it -> it.getFollowUpDate().isAfter(now) && it.getFollowUpDate().isAfter(filter.getInstantFrom()))
                .collect(Collectors.toList())
        );

        return report;
    }

    private Optional<Integer> getFollowUpMonthCount(String followUp) {
        var matcher = ArizonaMatrixConstants.FOLLOW_UP_PATTERN.matcher(followUp);
        if (matcher.matches()) {
            return Optional.of(Integer.parseInt(matcher.group(1)));
        } else {
            return Optional.empty();
        }
    }

    private Map<String, Object> parseAssessmentResult(AssessmentData item) {
        try {
            return objectMapper.readValue(item.getResult(), new TypeReference<HashMap<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new InternalServerException(InternalServerExceptionType.ASSESSMENT_EXPORT_FAILURE);
        }
    }

    private List<AssessmentData> extractData(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var hasAccess = clientAssessmentResultSpecificationGenerator.hasAccess(permissionFilter);
        var ofCommunities = clientAssessmentResultSpecificationGenerator.ofCommunities(filter.getAccessibleCommunityIdsAndNames());
        var byType = clientAssessmentResultSpecificationGenerator.byType(Assessment.ARIZONA_SSM);
        var completed = clientAssessmentResultSpecificationGenerator.completed();
        var unarchived = clientAssessmentResultSpecificationGenerator.isUnarchived();
        var withMaxStartDatePerClient = clientAssessmentResultSpecificationGenerator
            .byMaxDateStartedPerClient(byType.and(completed.and(unarchived)));

        var spec = hasAccess.and(ofCommunities.and(byType.and(completed.and(unarchived.and(withMaxStartDatePerClient)))));

        return clientAssessmentDao.findAll(spec, AssessmentData.class, ASSESSMENT_RESULTS_SORT);
    }

    interface AssessmentData extends ClientCommunityIdNameAware, ClientIdNamesAware, EmployeeNamesAware {
        Long getAssessmentId();
        Instant getDateStarted();
        String getResult();
    }
}
