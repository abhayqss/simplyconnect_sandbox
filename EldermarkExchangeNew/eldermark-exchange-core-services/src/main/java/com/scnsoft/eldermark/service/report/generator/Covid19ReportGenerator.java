package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.Covid19Report;
import com.scnsoft.eldermark.beans.reports.model.Covid19ReportRow;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.EventNotificationDao;
import com.scnsoft.eldermark.dao.LabResearchOrderDao;
import com.scnsoft.eldermark.dao.specification.EventNotificationSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.LabResearchOrderSpecificationGenerator;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder_;
import com.scnsoft.eldermark.entity.lab.report.LabResearchOrderReportListItem;
import com.scnsoft.eldermark.entity.lab.report.LabResearchOrderResultCodeValue;
import com.scnsoft.eldermark.entity.lab.report.LabResearchOrderResultWithClient;
import com.scnsoft.eldermark.service.LabResearchOrderService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class Covid19ReportGenerator extends DefaultReportGenerator<Covid19Report> {

    private static final String POSITIVE = "Positive";

    @Autowired
    private LabResearchOrderSpecificationGenerator labResearchOrderSpecificationGenerator;

    @Autowired
    private EventNotificationSpecificationGenerator eventNotificationSpecificationGenerator;

    @Autowired
    private LabResearchOrderDao labResearchOrderDao;

    @Autowired
    private EventNotificationDao eventNotificationDao;

    @Override
    public Covid19Report generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new Covid19Report();
        populateReportingCriteriaFields(filter, report);
        var labResearchOrders = findLabResearchOrders(filter, permissionFilter);
        fillReportRows(report, labResearchOrders);
        return report;
    }

    @Override
    public ReportType getReportType() {
        return ReportType.COVID_19_LOG;
    }

    private List<LabResearchOrderReportListItem> findLabResearchOrders(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var hasAccess = labResearchOrderSpecificationGenerator.hasAccess(permissionFilter);
        var byCommunities = labResearchOrderSpecificationGenerator.byCommunities(filter.getAccessibleCommunityIdsAndNames());
        var isCovid19 = labResearchOrderSpecificationGenerator.isCovid19();
        var isSuccessTestResult = labResearchOrderSpecificationGenerator.isSuccessTestResult();
        var betweenSpecimenDates = labResearchOrderSpecificationGenerator.betweenSpecimenDates(filter.getInstantFrom(), filter.getInstantTo());
        var ordersWithResults = labResearchOrderDao.findResultsWithOrders(hasAccess.and(byCommunities.and(isCovid19.and(isSuccessTestResult.and(betweenSpecimenDates)))), orderBy());

        return ordersWithResults.stream().map(item -> Pair.of(
                    new LabResearchOrderResultWithClient(item.getId(), item.getReason(), item.getSpecimenDate(), item.getClientId(), item.getClientFirstName(), item.getClientLastName(), item.getClientCommunityName(), item.getOruReceivedDatetime()),
                    item.getResultCode() != null ? new LabResearchOrderResultCodeValue(item.getResultCode(), item.getResultValue()) : null))
                .collect(Collectors.groupingBy(Pair::getFirst, Collectors.mapping(Pair::getSecond, Collectors.toList())))
                .entrySet().stream()
                .map(labEntry -> new LabResearchOrderReportListItem(labEntry.getKey(), labEntry.getValue().stream().filter(Objects::nonNull).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    private Sort orderBy() {
        return Sort.by(CareCoordinationUtils.concat(".", LabResearchOrder_.CLIENT, Client_.COMMUNITY, Community_.NAME))
                .and(Sort.by(Sort.Direction.DESC, LabResearchOrder_.SPECIMEN_DATE))
                .and(Sort.by(CareCoordinationUtils.concat(".", LabResearchOrder_.CLIENT, Client_.LAST_NAME),
                        CareCoordinationUtils.concat(".", LabResearchOrder_.CLIENT, Client_.FIRST_NAME)));
    }

    private void fillReportRows(Covid19Report report, List<LabResearchOrderReportListItem> labResearchOrders) {
        var rows = labResearchOrders.stream()
                .map(order -> {
                    var row = new Covid19ReportRow();
                    row.setSpecimenDate(order.getSpecimenDate());
                    row.setCommunityName(order.getClientCommunityName());
                    row.setReason(order.getReason().getValue());
                    row.setClientName(CareCoordinationUtils.concat(", ", order.getClientLastName(), order.getClientFirstName()));
                    row.setResultDate(order.getOruReceivedDatetime());
                    row.setResult(getTestResult(order.getResults()));
                    row.setNotifiedDate(findNotifiedDate(order));
                    if (POSITIVE.equals(row.getResult()) && hasEarlierOrder(order)) {
                        row.setComment("Retest");
                    }
                    return row;
                })
                .collect(Collectors.toList());
        report.setRows(rows);
    }

    private Instant findNotifiedDate(LabResearchOrderReportListItem order) {
        var byLabOrder = eventNotificationSpecificationGenerator.byLabResearchOrderId(order.getId());
        var sentDatetimeNotNull = eventNotificationSpecificationGenerator.sentDatetimeIsNotNull();
        return eventNotificationDao.findFirstSentDatetime(byLabOrder.and(sentDatetimeNotNull)).orElse(null);
    }

    private String getTestResult(List<LabResearchOrderResultCodeValue> observationResults) {
        return CollectionUtils.emptyIfNull(observationResults).stream()
                .filter(r -> LabResearchOrderService.COVID_CODE.equals(r.getCode()))
                .map(LabResearchOrderResultCodeValue::getValue)
                .filter(Objects::nonNull)
                .findFirst()
                .map(this::convertResult)
                .orElse(null);

    }

    private String convertResult(String result) {
        if ("Not Detected".equals(result)) {
            return "Negative";
        }
        if ("Detected".equals(result)) {
            return POSITIVE;
        }
        return result;
    }

    private boolean hasEarlierOrder(LabResearchOrderReportListItem order) {
        return labResearchOrderDao.findFirstByClientIdAndSpecimenDateBeforeAndIsCovid19IsTrue(order.getClientId(), order.getSpecimenDate()).isPresent();
    }
}
