package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.projection.ClientCommunityIdNameAware;
import com.scnsoft.eldermark.beans.projection.ClientIdNamesAware;
import com.scnsoft.eldermark.beans.projection.EmployeeIdNamesAware;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.staffcaseload.*;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ClientAssessmentDao;
import com.scnsoft.eldermark.dao.ClientCareTeamMemberDao;
import com.scnsoft.eldermark.dao.ServicePlanDao;
import com.scnsoft.eldermark.dao.specification.ClientAssessmentResultSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.ClientCareTeamMemberSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.ServicePlanSpecificationGenerator;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.assessment.ClientAndEmployeeAssessmentResultAware;
import com.scnsoft.eldermark.entity.serviceplan.ClientServicePlanScoringAware;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.ServicePlanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StaffCaseloadReportGenerator extends DefaultReportGenerator<StaffCaseloadReport> {

    @Autowired
    private ClientAssessmentDao clientAssessmentDao;

    @Autowired
    private ServicePlanDao servicePlanDao;

    @Autowired
    private ClientCareTeamMemberDao clientCareTeamMemberDao;

    @Autowired
    private ClientCareTeamMemberSpecificationGenerator clientCareTeamMemberSpecificationGenerator;

    @Autowired
    private ClientAssessmentResultSpecificationGenerator clientAssessmentSpecGenerator;

    @Autowired
    private ServicePlanSpecificationGenerator servicePlanSpecGenerator;

    @Override
    @Transactional(readOnly = true)
    public StaffCaseloadReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {

        var report = new StaffCaseloadReport();
        populateReportingCriteriaFields(filter, report);
        report.setStaffCaseload(extractStaffCaseLoad(filter, permissionFilter));
        report.setStaffCareTeams(extractStaffCareTeams(filter, permissionFilter));

        return report;
    }

    @Override
    public ReportType getReportType() {
        return ReportType.STAFF_CASELOAD;
    }

    private List<StaffCareTeamsReportItem> extractStaffCareTeams(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var hasAccess = clientCareTeamMemberSpecificationGenerator.hasAccess(permissionFilter);
        var excludePrsAndFmEmployees = clientCareTeamMemberSpecificationGenerator.byEmployeeSystemRoleNotIn(List.of(
                CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES,
                CareTeamRoleCode.ROLE_PARENT_GUARDIAN
        ));
        var inCommunities = clientCareTeamMemberSpecificationGenerator.byClientCommunityIds(
                CareCoordinationUtils.toIdsSet(filter.getAccessibleCommunityIdsAndNames())
        );

        var clientActiveOrDeactivatedInPeriod = clientCareTeamMemberSpecificationGenerator.isClientActiveInPeriod(filter.getInstantFrom(), filter.getInstantTo());

        var byEmployeeStatus = clientCareTeamMemberSpecificationGenerator.byEmployeeStatusIn(List.of(
                EmployeeStatus.ACTIVE,
                EmployeeStatus.INACTIVE
        ));

        var ctmList = clientCareTeamMemberDao.findAll(
                hasAccess.and(excludePrsAndFmEmployees.and(inCommunities.and(byEmployeeStatus.and(clientActiveOrDeactivatedInPeriod)))),
                ClientCareTeamReportDetails.class
        );

        var clientCommunities = new HashMap<Long, String>();
        var clientNames = new HashMap<Long, String>();
        var employeeNames = new HashMap<Long, String>();
        var employeeStatuses = new HashMap<Long, EmployeeStatus>();
        var employeeClients = new HashMap<Long, Set<Long>>();

        ctmList.forEach(ctm -> {
            clientNames.put(ctm.getClientId(), ctm.getClientFullName());
            clientCommunities.put(ctm.getClientId(), ctm.getClientCommunityName());
            employeeNames.put(ctm.getEmployeeId(), ctm.getEmployeeFullName());
            employeeStatuses.put(ctm.getEmployeeId(), ctm.getEmployeeStatus());
            employeeClients.computeIfAbsent(ctm.getEmployeeId(), k -> new HashSet<>())
                    .add(ctm.getClientId());
        });

        return employeeClients.entrySet().stream()
                .map(entry -> {
                    var employeeId = entry.getKey();
                    var clientIds = entry.getValue();

                    var employeeItem = new StaffCareTeamsReportItem();
                    employeeItem.setEmployeeName(employeeNames.get(employeeId));
                    employeeItem.setEmployeeStatus(employeeStatuses.get(employeeId));
                    employeeItem.setNumberOfCareTeams(clientIds.size());
                    employeeItem.setResidents(
                            clientIds.stream()
                                    .map(clientId -> {
                                        var clientItem = new ResidentStaffCareTeamItem();
                                        clientItem.setClientId(clientId);
                                        clientItem.setClientName(clientNames.get(clientId));
                                        clientItem.setCommunity(clientCommunities.get(clientId));
                                        return clientItem;
                                    })
                                    .sorted(Comparator.comparing(ResidentStaffCareTeamItem::getClientName))
                                    .collect(Collectors.toList())
                    );

                    return employeeItem;
                })
                .sorted(Comparator.comparing(StaffCareTeamsReportItem::getEmployeeName))
                .collect(Collectors.toList());
    }

    private List<StaffCaseloadReportItem> extractStaffCaseLoad(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var assessmentResults = getAssessmentResults(filter, permissionFilter);
        var servicePlanScoringList = getServicePlanScoringList(filter, permissionFilter);

        return constructReportItems(assessmentResults, servicePlanScoringList);
    }

    private List<StaffCaseloadReportItem> constructReportItems(
            List<ClientAndEmployeeAssessmentResultAware> assessmentResults,
            List<ClientServicePlanScoringAware> servicePlanScoringList
    ) {
        var communities = new HashMap<Long, String>();
        var residentNames = new HashMap<Long, String>();
        var employeeNames = new HashMap<Long, String>();
        var employeeClients = new HashMap<Long, Set<Long>>();
        var residentsScores = new HashMap<Long, List<Integer>>();

        assessmentResults.forEach(a -> {

            communities.put(a.getClientId(), a.getClientCommunityName());
            residentNames.put(a.getClientId(), a.getClientFullName());
            employeeNames.put(a.getEmployeeId(), a.getEmployeeFullName());

            employeeClients.computeIfAbsent(a.getEmployeeId(), k -> new HashSet<>())
                    .add(a.getClientId());

            residentsScores.computeIfAbsent(a.getClientId(), k -> new LinkedList<>());
        });

        servicePlanScoringList.forEach(s -> {

            var score = ServicePlanUtils.resolveScore(s, s.getServicePlanNeedType());
            if (score == null) return;

            var residentScores = residentsScores.get(s.getClientId());
            if (residentScores == null) return;

            residentScores.add(score);
        });

        return employeeClients.entrySet().stream()
                .map(employeeResidentScoresEntry -> {
                    var employeeId = employeeResidentScoresEntry.getKey();
                    var residents = employeeResidentScoresEntry.getValue();
                    var residentCount = residents.size();

                    var caseLoadItems = residents.stream()
                            .map(id -> {
                                var averageScore = residentsScores.get(id).stream()
                                        .mapToInt(Integer::intValue)
                                        .average()
                                        .stream()
                                        .boxed()
                                        .map(Double::floatValue)
                                        .findAny()
                                        .orElse(null);

                                return new ResidentStaffCaseLoadItem(
                                        id,
                                        residentNames.get(id),
                                        communities.get(id),
                                        averageScore
                                );
                            })
                            .sorted(Comparator.comparing(ResidentStaffCaseLoadItem::getClientName))
                            .collect(Collectors.toList());

                    return new StaffCaseloadReportItem(employeeNames.get(employeeId), residentCount, caseLoadItems);
                })
                .sorted(Comparator.comparing(StaffCaseloadReportItem::getEmployeeName))
                .collect(Collectors.toList());
    }

    private List<ClientServicePlanScoringAware> getServicePlanScoringList(
            InternalReportFilter filter,
            PermissionFilter permissionFilter
    ) {
        var hasAccess = servicePlanSpecGenerator.hasAccess(permissionFilter);
        var withinDate = servicePlanSpecGenerator.withinReportPeriod(filter.getInstantFrom(), filter.getInstantTo());
        var inCommunities = servicePlanSpecGenerator.ofCommunities(filter.getAccessibleCommunityIdsAndNames());
        var unarchived = servicePlanSpecGenerator.unarchived();
        var distinct = servicePlanSpecGenerator.distinct();

        return servicePlanDao.findAllClientAndEmployeeServicePlanScoring(
                distinct.and(unarchived.and(hasAccess.and(withinDate.and(inCommunities))))
        );
    }

    private List<ClientAndEmployeeAssessmentResultAware> getAssessmentResults(
            InternalReportFilter filter,
            PermissionFilter permissionFilter
    ) {
        var hasAccess = clientAssessmentSpecGenerator.hasAccess(permissionFilter);
        var withinReportPeriod = clientAssessmentSpecGenerator.withinReportPeriod(
                filter.getInstantFrom(),
                filter.getInstantTo()
        );
        var inCommunities = clientAssessmentSpecGenerator.ofCommunities(filter.getAccessibleCommunityIdsAndNames());

        return clientAssessmentDao.findAll(
                hasAccess.and(withinReportPeriod.and(inCommunities)),
                ClientAndEmployeeAssessmentResultAware.class
        );
    }

    private interface ClientCareTeamReportDetails extends EmployeeIdNamesAware, ClientIdNamesAware, ClientCommunityIdNameAware {
        EmployeeStatus getEmployeeStatus();
    }
}
