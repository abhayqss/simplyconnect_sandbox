package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.DomainRow;
import com.scnsoft.eldermark.beans.reports.model.GoalRow;
import com.scnsoft.eldermark.beans.reports.model.ServicePlanReport;
import com.scnsoft.eldermark.beans.reports.model.ServicePlanRow;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ServicePlanDao;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoal;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoalNeed;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.scnsoft.eldermark.beans.reports.enums.ReportType.SERVICE_PLANS;
import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
public class ServicePlanReportGenerator extends DefaultReportGenerator<ServicePlanReport> {

    @Autowired
    private ServicePlanDao servicePlanDao;

    @Override
    public ServicePlanReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new ServicePlanReport();

        populateReportingCriteriaFields(filter, report);

        List<ServicePlanRow> servicePlanRowList = getServicePlanList(filter, permissionFilter)
                .stream()
                .map(this::convertToServicePlanRow)
                .collect(toList());

        report.setServicePlanRowList(servicePlanRowList);
        return report;
    }

    @Override
    public ReportType getReportType() {
        return SERVICE_PLANS;
    }

    private List<ServicePlan> getServicePlanList(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var latestAccessibleSpWithinPeriod = latestAccessibleSpWithinPeriod(filter, permissionFilter);

        return servicePlanDao.findAll(servicePlanSpecifications.byClientCommunities(filter.getAccessibleCommunityIdsAndNames())
                .and(latestAccessibleSpWithinPeriod));
    }

    private ServicePlanRow convertToServicePlanRow(ServicePlan sp) {
        ServicePlanRow servicePlanRow = new ServicePlanRow();
        servicePlanRow.setCommunityName(sp.getClient().getCommunity().getName());
        servicePlanRow.setClientName(sp.getClient().getFullName());
        servicePlanRow.setClientId(sp.getClient().getId());
        servicePlanRow.setServiceCoordinator(sp.getEmployee().getFullName());
        servicePlanRow.setDateCompleted(sp.getDateCompleted());
        servicePlanRow.setServicePlanStatus(sp.getServicePlanStatus().getDisplayName());

        List<DomainRow> domainRowList = sp.getNeeds().stream()
                .map(this::convertToDomainRow)
                .collect(toList());
        servicePlanRow.setDomainRows(domainRowList);
        servicePlanRow.setTotalNumberOfDomains(countTotalNumberOfDomains(domainRowList));
        servicePlanRow.setTotalNumberOfGoals(countTotalNumberOfGoals(domainRowList));
        servicePlanRow.setTotalNumberOfResources(countNotEmptyResources(domainRowList));
        return servicePlanRow;
    }

    private DomainRow convertToDomainRow(ServicePlanNeed spNeed) {
        DomainRow domainRow = new DomainRow();
        domainRow.setDomainName(spNeed.getDomain().getDisplayName());
        if (spNeed instanceof ServicePlanGoalNeed) {
            List<ServicePlanGoal> spGoals = ((ServicePlanGoalNeed) spNeed).getGoals();
            domainRow.setGoalList(spGoals.stream().map(this::convertToGoalRow).collect(toList()));
        } else {
            domainRow.setGoalList(Collections.emptyList());
        }
        return domainRow;
    }

    private GoalRow convertToGoalRow(ServicePlanGoal goal) {
        GoalRow goalRow = new GoalRow();
        goalRow.setGoalName(goal.getGoal());
        goalRow.setResourceName(goal.getResourceName());
        goalRow.setGoalStatus(goal.getGoalCompletion());
        return goalRow;
    }

    private long countTotalNumberOfDomains(List<DomainRow> domainRows) {
        return domainRows.stream()
                .map(DomainRow::getDomainName)
                .distinct()
                .count();
    }

    private long countTotalNumberOfGoals(List<DomainRow> domainRows) {
        return domainRows.stream()
                .mapToInt(domainRow -> domainRow.getGoalList().size())
                .sum();
    }

    private long countNotEmptyResources(List<DomainRow> domainRows) {
        return domainRows.stream()
                .mapToLong(domainRow -> domainRow.getGoalList().stream()
                        .map(GoalRow::getResourceName)
                        .filter(Objects::nonNull)
                        .count())
                .sum();
    }

}
