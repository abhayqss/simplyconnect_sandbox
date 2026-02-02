package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.HospitalizationEventRow;
import com.scnsoft.eldermark.beans.reports.model.HospitalizationsReport;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ClientAssessmentDao;
import com.scnsoft.eldermark.dao.EventDao;
import com.scnsoft.eldermark.dao.specification.EventSpecificationGenerator;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.beans.reports.enums.ReportType.HOSPITALIZATIONS;
import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
public class HospitalizationsReportGenerator extends DefaultReportGenerator<HospitalizationsReport> {

    private static final String HOSPITALIZATION_CODE = "H";

    @Autowired
    private EventDao eventDao;

    @Autowired
    private EventSpecificationGenerator eventSpecificationGenerator;

    @Autowired
    private ClientAssessmentDao assessmentResultDao;

    @Override
    public HospitalizationsReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new HospitalizationsReport();

        populateReportingCriteriaFields(filter, report);

        var rows = new ArrayList<>(eventRows(filter, permissionFilter));
        rows.addAll(caRows(filter, permissionFilter));
        rows.sort(Comparator.comparing(HospitalizationEventRow::getClientName));

        report.setEventRows(rows);
        return report;
    }

    @Override
    public ReportType getReportType() {
        return HOSPITALIZATIONS;
    }

    private List<HospitalizationEventRow> eventRows(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var hasAccess = eventSpecificationGenerator.hasAccessIgnoringNotViewable(permissionFilter);
        var byHospitalizationType = eventSpecificationGenerator.byEventType(HOSPITALIZATION_CODE);
        var betweenDates = eventSpecificationGenerator.betweenDates(filter.getInstantFrom(), filter.getInstantTo());
        return eventDao.findAll(eventSpecificationGenerator.byClientCommunities(filter.getAccessibleCommunityIdsAndNames()).and(betweenDates).and(byHospitalizationType).and(hasAccess))
                .stream()
                .map(event -> {
                    HospitalizationEventRow eventRow = new HospitalizationEventRow();
                    eventRow.setCommunityName(event.getClient().getCommunity().getName());
                    eventRow.setClientId(event.getClient().getId());
                    eventRow.setClientName(event.getClient().getFullName());
                    eventRow.setDateOfInstitutionalization(event.getEventDateTime());
                    eventRow.setLocation(event.getLocation());
                    eventRow.setSituation(event.getSituation());
                    eventRow.setBackground(event.getBackground());
                    eventRow.setAssessment(event.getAssessment());
                    eventRow.setInjury(event.getIsInjury());
                    eventRow.setFollowup(event.getIsFollowup());
                    eventRow.setSource(HospitalizationEventRow.Source.EVENT);

                    return eventRow;
                }).collect(toList());
    }

    private List<HospitalizationEventRow> caRows(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var comprehensiveType = assessmentResultSpecifications.comprehensiveType();
        var norCalComprehensiveType = assessmentResultSpecifications.byType(Assessment.NOR_CAL_COMPREHENSIVE);
        var withinPeriod = assessmentResultSpecifications.withinReportPeriod(filter.getInstantFrom(), filter.getInstantTo());
        var accessible = assessmentResultSpecifications.hasAccess(permissionFilter);
        var latest = assessmentResultSpecifications.leaveLatest(filter.getInstantTo());
        var inCommunities = assessmentResultSpecifications.ofCommunities(filter.getAccessibleCommunityIdsAndNames());

        return assessmentResultDao.findAll((comprehensiveType.or(norCalComprehensiveType)).and(withinPeriod).and(accessible).and(latest).and(inCommunities)).stream()
                .flatMap(assessment -> {
                    var parsed = parseComprehensive(assessment);
                    var lastHospitalizationOpt = parseAssessmentDate(parsed.getLastHospitalAdmissionDate());
                    if (lastHospitalizationOpt
                            .filter(l -> !l.isBefore(filter.getInstantFrom()) && !l.isAfter(filter.getInstantTo()))
                            .isEmpty()) {
                        return Stream.empty();
                    }

                    var eventRow = new HospitalizationEventRow();
                    eventRow.setCommunityName(assessment.getClient().getCommunity().getName());
                    eventRow.setClientId(assessment.getClient().getId());
                    eventRow.setClientName(assessment.getClient().getFullName());
                    eventRow.setDateOfInstitutionalization(lastHospitalizationOpt.get());
                    eventRow.setSituation(parsed.getReasonForAdmission());
                    var shortAssessmentName = assessment.getAssessment().getShortName();
                    eventRow.setSource(HospitalizationEventRow.Source.of(shortAssessmentName));

                    return Stream.of(eventRow);
                })
                .collect(Collectors.toList());
    }

}
