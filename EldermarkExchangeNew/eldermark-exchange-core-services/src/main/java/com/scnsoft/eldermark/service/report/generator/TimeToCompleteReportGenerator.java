package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.TimeToCompleteReport;
import com.scnsoft.eldermark.beans.reports.model.TimeToCompleteResult;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ClientAssessmentDao;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.beans.reports.enums.ReportType.TIME_TO_COMPLETE_ASSESSMENT;

@Service
@Transactional(readOnly = true)
public class TimeToCompleteReportGenerator extends DefaultReportGenerator<TimeToCompleteReport> {

    @Autowired
    private ClientAssessmentDao clientAssessmentDao;

    @Override
    public TimeToCompleteReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new TimeToCompleteReport();
        populateReportingCriteriaFields(filter, report);
        report.setTimeToCompleteResultList(timeToCompleteResultList(filter, permissionFilter));
        return report;
    }

    @Override
    public ReportType getReportType() {
        return TIME_TO_COMPLETE_ASSESSMENT;
    }

    private List<TimeToCompleteResult> timeToCompleteResultList(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var latestAccessibleAssessmentWithinPeriodAccess = latestAccessibleAssessmentWithinPeriod(filter, permissionFilter);

        var byComprehensiveType = assessmentResultSpecifications.byType(Assessment.COMPREHENSIVE);
        var byNorCalComprehensiveType = assessmentResultSpecifications.byType(Assessment.NOR_CAL_COMPREHENSIVE);
        var assessments = clientAssessmentDao.findAll(
                assessmentResultSpecifications.completedOfCommunitiesByType(
                                filter.getAccessibleCommunityIdsAndNames(), byComprehensiveType.or(byNorCalComprehensiveType)
                        )
                        .and(latestAccessibleAssessmentWithinPeriodAccess));

        var timeToCompleteResultList = assessments.stream()
                .map(clientAssessmentResult -> {
                    TimeToCompleteResult timeToCompleteResult = new TimeToCompleteResult();
                    timeToCompleteResult.setCommunityName(clientAssessmentResult.getClient().getCommunity().getName());
                    timeToCompleteResult.setClientId(clientAssessmentResult.getClient().getId());
                    timeToCompleteResult.setClientName(clientAssessmentResult.getClient().getFullName());
                    timeToCompleteResult.setAssessmentStartDate(clientAssessmentResult.getDateStarted());
                    timeToCompleteResult.setAssessmentEndDate(clientAssessmentResult.getDateCompleted());
                    timeToCompleteResult.setCompletedBy(clientAssessmentResult.getClient().getFullName());

                    Long minutes = DateTimeUtils.millisToMinutes(clientAssessmentResult.getTimeToComplete());
                    timeToCompleteResult.setTimeToComplete(minutes != null && minutes == 0L ? Long.valueOf(1L) : minutes);

                    return timeToCompleteResult;
                })
                .collect(Collectors.toList());

        return timeToCompleteResultList;
    }
}
