package com.scnsoft.eldermark.service.report.generator;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static java.util.Comparator.nullsLast;
import static java.util.Comparator.reverseOrder;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import com.scnsoft.eldermark.beans.projection.ClientIntakeDetailsAware;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.ClientIntakesReport;
import com.scnsoft.eldermark.beans.reports.model.ClientIntakesReportRow;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.ClientHealthPlanDao;
import com.scnsoft.eldermark.dao.history.ClientHistoryDao;
import com.scnsoft.eldermark.dao.specification.ClientHistorySpecificationGenerator;
import com.scnsoft.eldermark.entity.client.ClientHealthPlan;
import com.scnsoft.eldermark.entity.client.report.ClientIntakesReportItem;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.history.ClientHistory_;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class ClientIntakesReportGenerator extends DefaultReportGenerator<ClientIntakesReport> {

    private final ClientHistorySpecificationGenerator clientHistorySpecificationGenerator;
    private final ClientHistoryDao clientHistoryDao;
    private final ClientDao clientDao;
    private final ClientHealthPlanDao healthPlanDao;
    private final Converter<ClientIntakeDetailsAware, ClientIntakesReportItem> clientIntakesReportItemClientConverter;

    @Autowired
    public ClientIntakesReportGenerator(
        ClientHistorySpecificationGenerator clientHistorySpecificationGenerator,
        ClientHistoryDao clientHistoryDao,
        ClientDao clientDao,
        ClientHealthPlanDao healthPlanDao,
        Converter<ClientIntakeDetailsAware, ClientIntakesReportItem> clientIntakesReportItemClientConverter
    ) {
        this.clientHistorySpecificationGenerator = clientHistorySpecificationGenerator;
        this.clientHistoryDao = clientHistoryDao;
        this.clientDao = clientDao;
        this.healthPlanDao = healthPlanDao;
        this.clientIntakesReportItemClientConverter = clientIntakesReportItemClientConverter;
    }

    @Override
    public ClientIntakesReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new ClientIntakesReport();
        populateReportingCriteriaFields(filter, report);
        fillReport(report, filter, permissionFilter);
        return report;
    }

    @Override
    public ReportType getReportType() {
        return ReportType.CLIENT_INTAKES;
    }

    private void fillReport(ClientIntakesReport report, InternalReportFilter filter, PermissionFilter permissionFilter) {
        var specification = clientHistorySpecificationGenerator.hasAccess(permissionFilter)
            .and(clientHistorySpecificationGenerator.byCommunities(filter.getAccessibleCommunityIdsAndNames()))
            .and(clientHistorySpecificationGenerator.byUpdatedDateTimeIn(filter.getInstantFrom(), filter.getInstantTo()));

        var historyItems = clientHistoryDao.findClientIntakesReportItems(specification, orderBy());
        var clientIds = historyItems.stream()
            .map(ClientIntakesReportItem::getId)
            .collect(Collectors.toSet());

        var allClientIntakes =
            clientDao.findByIdIn(clientIds, ClientIntakeDetailsAware.class).stream()
                .map(clientIntakesReportItemClientConverter::convert)
                .collect(Collectors.toList());

        historyItems.addAll(allClientIntakes);
        var comparator =
            Comparator.comparing(ClientIntakesReportItem::getCommunityName, nullsFirst(naturalOrder()))
            .thenComparing(date-> Objects.nonNull(date.getIntakeDate())
                ? DateTimeUtils.toLocalDate(date.getIntakeDate(), filter.getTimezoneOffset())
                : null, nullsLast(reverseOrder()))
            .thenComparing(ClientIntakesReportItem::getFirstName, nullsFirst(naturalOrder()))
            .thenComparing(ClientIntakesReportItem::getLastName, nullsFirst(naturalOrder()));

        var uniqueHistoryItems = historyItems.stream()
            .sorted(comparator)
            .collect(Collectors.toCollection(LinkedHashSet::new));

        var intakeWithinDatesRows = new ArrayList<ClientIntakesReportRow>();
        var allRows = new ArrayList<ClientIntakesReportRow>();
        convertAndSplitItemsToRows(uniqueHistoryItems, intakeWithinDatesRows, allRows, filter.getInstantFrom(), filter.getInstantTo());
        report.setIntakeWithinDatesRows(intakeWithinDatesRows);
        report.setAllRows(allRows);
    }

    private Sort orderBy() {
        return Sort.by(CareCoordinationUtils.concat(".", ClientHistory_.COMMUNITY, Community_.NAME))
            .and(Sort.by(Sort.Direction.DESC, ClientHistory_.INTAKE_DATE))
            .and(Sort.by(ClientHistory_.FIRST_NAME, ClientHistory_.LAST_NAME));
    }

    private void convertAndSplitItemsToRows(
        Set<ClientIntakesReportItem> items,
        List<ClientIntakesReportRow> intakeWithinDatesRows,
        List<ClientIntakesReportRow> allRows,
        Instant from,
        Instant to
    ) {
        var healthPlans = healthPlanDao.findAllByClientIdIn(
            items.stream()
                .map(ClientIntakesReportItem::getId)
                .collect(Collectors.toSet())
        )
            .stream()
            .collect(Collectors.groupingBy(
                clientHealthPlan -> clientHealthPlan.getClient().getId(),
                Collectors.mapping(ClientHealthPlan::getHealthPlanName, Collectors.toList())
                )
            );

        Stream.ofNullable(items)
            .flatMap(Set::stream)
            .forEach(item -> {
                var row = new ClientIntakesReportRow();
                row.setClientId(item.getId());
                row.setClientName(CareCoordinationUtils.concat(" ", item.getFirstName(), item.getLastName()));
                row.setCommunityName(item.getCommunityName());
                row.setIntakeDate(item.getIntakeDate());
                row.setExitDate(item.getExitDate());
                if (item.getActivationDate() != null && item.getDeactivationDate() != null) {
                    if ((item.getActivationDate().isAfter(item.getDeactivationDate()))) {
                        row.setIntakeComment(item.getComment());
                    } else {
                        row.setExitComment(item.getComment());
                    }
                } else if (item.getDeactivationDate() != null) {
                    row.setExitComment(item.getComment());
                }
                if (StringUtils.isEmpty(row.getExitComment())) {
                    row.setExitComment(item.getExitComment());
                }

                row.setDeactivatedDate(item.getDeactivationDate());
                row.setDeactivationReason(nonNull(item.getDeactivationReason()) ? item.getDeactivationReason().getTitle() : null);
                row.setStatus(item.getActive() ? "Active" : "Inactive");
                row.setBirthDate(item.getBirthDate());
                row.setCreatedDate(item.getCreatedDate());
                row.setGender(item.getGender());
                row.setRace(item.getRace());
                row.setCity(item.getCity());
                row.setInsuranceNetwork(item.getInsuranceNetwork());
                row.setHealthPlans(ofNullable(healthPlans.get(item.getId())).orElse(Collections.emptyList()));
                row.setInsurancePlan(item.getInsurancePlan());

                if (row.getIntakeDate() != null && !row.getIntakeDate().isBefore(from) && !row.getIntakeDate().isAfter(to)) {
                    intakeWithinDatesRows.add(row);
                }
                allRows.add(row);
            });
    }

}
