package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.ComprehensiveReport;
import com.scnsoft.eldermark.beans.reports.model.ComprehensiveReportRecord;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ClientAssessmentDao;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.beans.reports.enums.ReportType.COMPREHENSIVE;

@Service
@Transactional(readOnly = true)
public class ComprehensiveReportGenerator extends DefaultReportGenerator<ComprehensiveReport> {

    @Autowired
    private ClientAssessmentDao clientAssessmentDao;

    @Override
    public ComprehensiveReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new ComprehensiveReport();
        populateReportingCriteriaFields(filter, report);

        report.setComprehensiveReportRecords(comprehensiveReportRecordList(filter, permissionFilter));
        return report;
    }

    private Map<String, List<ComprehensiveReportRecord>> comprehensiveReportRecordList(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var latestAccessibleAssessmentWithinPeriod = latestAccessibleAssessmentWithinPeriod(filter, permissionFilter);
        var comprehensiveType = assessmentResultSpecifications.comprehensiveType();
        var norCalComprehensiveType = assessmentResultSpecifications.byType(Assessment.NOR_CAL_COMPREHENSIVE);

        var assessments = clientAssessmentDao.findAll(
                assessmentResultSpecifications.ofCommunities(filter.getAccessibleCommunityIdsAndNames())
                        .and(latestAccessibleAssessmentWithinPeriod)
                        .and(comprehensiveType.or(norCalComprehensiveType)),
                ClientAssessmentDao.ORDER_BY_CLIENT_ID_ASC);

        return assessments
                .stream()
                .map(clientAssessmentResult -> {
                    ComprehensiveReportRecord comprehensiveRecord = new ComprehensiveReportRecord();
                    comprehensiveRecord.setClientId(clientAssessmentResult.getClient().getId());
                    String responseJson = clientAssessmentResult.getResult();
                    comprehensiveRecord.setAssessmentResponse(addClientId(clientAssessmentResult.getClient().getId(), responseJson));
                    comprehensiveRecord.setCommunityName(clientAssessmentResult.getClient().getCommunity().getName());
                    comprehensiveRecord.setAssessmentStatus(clientAssessmentResult.getAssessmentStatus().getDisplayName());
                    return comprehensiveRecord;
                })
                .collect(Collectors.groupingBy(ComprehensiveReportRecord::getCommunityName));
    }

    private String addClientId(Long clientId, String responseJson) {
        var newSubstring = responseJson.length() > 2 ? "{\"Client ID\":" + clientId + "," : "{\"Client ID\":" + clientId;
        return responseJson.replaceFirst("\\{", newSubstring);
    }

    @Override
    public ReportType getReportType() {
        return COMPREHENSIVE;
    }
}
