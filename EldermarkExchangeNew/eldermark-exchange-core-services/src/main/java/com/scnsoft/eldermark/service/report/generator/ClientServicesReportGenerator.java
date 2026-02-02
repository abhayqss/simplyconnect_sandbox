package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.ClientDomainRow;
import com.scnsoft.eldermark.beans.reports.model.ClientGoalRow;
import com.scnsoft.eldermark.beans.reports.model.ClientServicePlanRow;
import com.scnsoft.eldermark.beans.reports.model.ClientServicesReport;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ServicePlanDao;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoal;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoalNeed;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeed;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.scnsoft.eldermark.beans.reports.enums.ReportType.CLIENT_SERVICES;
import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
public class ClientServicesReportGenerator extends DefaultReportGenerator<ClientServicesReport> {

    @Autowired
    private ServicePlanDao servicePlanDao;

    @Override
    public ClientServicesReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new ClientServicesReport();

        populateReportingCriteriaFields(filter, report);

        //Report should contain one record group per client and it is ensured by the fact, that client can have
        //only one 'In development' service plan at time (no need to group multiple service plans per client
        //because the result is always a single service plan).
        var inDevelopmentServicePlans = getInDevelopmentServicePlansWithinPeriod(filter, permissionFilter);

        var rows = inDevelopmentServicePlans.stream()
                .map(this::convertToClientServicePlanRow)
                .collect(toList());

        report.setServicePlanRows(rows);
        return report;
    }

    @Override
    public ReportType getReportType() {
        return CLIENT_SERVICES;
    }

    private List<ServicePlan> getInDevelopmentServicePlansWithinPeriod(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var byClientCommunities = servicePlanSpecifications.byClientCommunities(filter.getAccessibleCommunityIdsAndNames());
        var hasAccess = servicePlanSpecifications.hasAccess(permissionFilter);
        var inDevelopmentTillDate = servicePlanSpecifications.inDevelopmentTillDate(filter.getInstantTo());
        var latest = servicePlanSpecifications.leaveLatest(filter.getInstantTo());

        return servicePlanDao.findAll(byClientCommunities.and(hasAccess).and(inDevelopmentTillDate).and(latest));
    }

    private ClientServicePlanRow convertToClientServicePlanRow(ServicePlan sp) {
        ClientServicePlanRow servicePlanRow = new ClientServicePlanRow();
        servicePlanRow.setCommunityName(sp.getClient().getCommunity().getName());
        servicePlanRow.setClientName(sp.getClient().getFullName());
        servicePlanRow.setClientId(sp.getClientId());
        servicePlanRow.setCoordinatorName(sp.getEmployee().getFullName());

        List<ClientDomainRow> domainRowList = sp.getNeeds().stream()
                .filter(need -> need instanceof ServicePlanGoalNeed)
                .map(this::convertToClientDomainRow)
                .collect(toList());
        servicePlanRow.setDomainRows(domainRowList);
        servicePlanRow.setTotalNumberOfServices(countTotalNumberOfServices(sp));
        return servicePlanRow;
    }

    private ClientDomainRow convertToClientDomainRow(ServicePlanNeed spNeed) {
        ClientDomainRow domainRow = new ClientDomainRow();
        domainRow.setDomainName(spNeed.getDomain().getDisplayName());
        var spGoals = ((ServicePlanGoalNeed) spNeed).getGoals().stream()
                .filter(g -> StringUtils.isNotEmpty(g.getResourceName()));

        domainRow.setGoalRows(spGoals.map(this::convertToClientGoalRow).collect(toList()));
        return domainRow;
    }

    private ClientGoalRow convertToClientGoalRow(ServicePlanGoal servicePlanGoal) {
        ClientGoalRow goalRow = new ClientGoalRow();
        goalRow.setGoalStatus(servicePlanGoal.getGoalCompletion());
        goalRow.setCompletionDate(servicePlanGoal.getCompletionDate());
        goalRow.setResourceName(servicePlanGoal.getResourceName());
        goalRow.setTargetCompletionDate(servicePlanGoal.getTargetCompletionDate());
        return goalRow;
    }

    private long countTotalNumberOfServices(ServicePlan sp) {
        return sp.getNeeds().stream()
                .filter(spNeed -> spNeed instanceof ServicePlanGoalNeed)
                .flatMap(spNeed -> ((ServicePlanGoalNeed) spNeed).getGoals().stream())
                .map(ServicePlanGoal::getResourceName)
                .filter(StringUtils::isNotEmpty)
                .count();
    }

}
