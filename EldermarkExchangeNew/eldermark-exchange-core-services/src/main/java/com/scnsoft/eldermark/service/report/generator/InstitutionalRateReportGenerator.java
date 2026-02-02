package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.InstitutionalRateReport;
import com.scnsoft.eldermark.beans.reports.model.InstitutionalRateReportRow;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.EventDao;
import com.scnsoft.eldermark.dao.history.ClientHistoryDao;
import com.scnsoft.eldermark.dao.specification.ClientHistorySpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.ClientSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.EventSpecificationGenerator;
import com.scnsoft.eldermark.entity.client.ActivityClientAware;
import com.scnsoft.eldermark.entity.client.report.EventCountByCommunityAndEventTypeItem;
import com.scnsoft.eldermark.entity.history.ActivityClientHistoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class InstitutionalRateReportGenerator extends DefaultReportGenerator<InstitutionalRateReport> {

    private static final String ER_EVENT_TYPE_CODE = "ERV";
    private static final String SNF_EVENT_TYPE_CODE = "NURSFI";
    private static final String HOSPITALIZATION_EVENT_TYPE_CODE = "H";
    private static final List<String> NEEDED_EVENT_TYPE_CODES = List.of(
        ER_EVENT_TYPE_CODE,
        SNF_EVENT_TYPE_CODE,
        HOSPITALIZATION_EVENT_TYPE_CODE
    );

    @Autowired
    private EventDao eventDao;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private ClientHistoryDao clientHistoryDao;

    @Autowired
    private EventSpecificationGenerator eventSpecGenerator;

    @Autowired
    private ClientHistorySpecificationGenerator clientHistorySpecGenerator;

    @Autowired
    private ClientSpecificationGenerator clientSpecGenerator;

    @Override
    public ReportType getReportType() {
        return ReportType.INSTITUTIONAL_RATE;
    }

    @Override
    public InstitutionalRateReport generateReport(
        InternalReportFilter filter,
        PermissionFilter permissionFilter
    ) {
        var residents = clientDao.findAll(
            clientSpecGenerator.hasDetailsAccess(permissionFilter)
                .and(clientSpecGenerator.byCommunities(filter.getAccessibleCommunityIdsAndNames()))
                .and(clientSpecGenerator.lastUpdatedBeforeOrEqual(filter.getInstantTo())),
            ActivityClientAware.class
        );

        var residentHistory = clientHistoryDao.findAll(
            clientHistorySpecGenerator.hasAccess(permissionFilter)
                .and(clientHistorySpecGenerator.byCommunities(filter.getAccessibleCommunityIdsAndNames()))
                .and(
                    clientHistorySpecGenerator.byUpdatedDateTimeIn(filter.getInstantFrom(), filter.getInstantTo())
                        .or(clientHistorySpecGenerator.latestForDate(filter.getInstantFrom()))
                ),
            ActivityClientHistoryAware.class
        );

        var maxActiveClientsPerCommunity = calcMaxActiveClientCountPerCommunity(residentHistory, residents);

        var eventCounts = eventDao.countsByEventTypeAndCommunity(
            eventSpecGenerator.hasAccess(permissionFilter)
                .and(eventSpecGenerator.byEventTypeCodeIn(NEEDED_EVENT_TYPE_CODES))
                .and(eventSpecGenerator.byEventDateTimeIn(filter.getInstantFrom(), filter.getInstantTo()))
                .and(eventSpecGenerator.byClientCommunities(filter.getAccessibleCommunityIdsAndNames()))
        );

        var erMap = extractResultByEventType(eventCounts, ER_EVENT_TYPE_CODE);
        var snfMap = extractResultByEventType(eventCounts, SNF_EVENT_TYPE_CODE);
        var hospitalizationMap = extractResultByEventType(eventCounts, HOSPITALIZATION_EVENT_TYPE_CODE);

        var rows = filter.getAccessibleCommunityIdsAndNames().stream()
            .flatMap(community -> {

                var activeResidentCount = maxActiveClientsPerCommunity.getOrDefault(community.getId(), 0L);
                if (activeResidentCount == 0) return Stream.empty();

                var erCount = erMap.getOrDefault(community.getId(), 0L);
                var snfCount = snfMap.getOrDefault(community.getId(), 0L);
                var hospitalizationCount = hospitalizationMap.getOrDefault(community.getId(), 0L);

                var erRate = 100f * erCount / activeResidentCount;
                var snfRate = 100f * snfCount / activeResidentCount;
                var hospitalizationRate = 100f * hospitalizationCount / activeResidentCount;

                var institutionalRate = 100f * (erCount + snfCount + hospitalizationCount) / activeResidentCount;

                return Stream.of(
                    new InstitutionalRateReportRow(
                        community.getName(),
                        activeResidentCount,
                        erCount,
                        snfCount,
                        hospitalizationCount,
                        institutionalRate,
                        erRate,
                        snfRate,
                        hospitalizationRate
                    )
                );
            })
            .sorted(Comparator.comparing(InstitutionalRateReportRow::getCommunity))
            .collect(Collectors.toList());

        var report = new InstitutionalRateReport();

        report.setRows(rows);
        populateReportingCriteriaFields(filter, report);

        return report;
    }

    private static class HistoryItem {
        Long clientId;
        Long communityId;
        Boolean active;
        Instant date;

        public HistoryItem(Long clientId, Long communityId, Boolean active, Instant date) {
            this.clientId = clientId;
            this.communityId = communityId;
            this.active = active;
            this.date = date;
        }
    }

    private HashMap<Long, Long> calcMaxActiveClientCountPerCommunity(
        List<ActivityClientHistoryAware> residentHistory,
        List<ActivityClientAware> residents
    ) {
        var maxActiveClientsPerCommunityMap = new HashMap<Long, Long>();
        var activeClientsPerCommunityMap = new HashMap<Long, Set<Long>>();

        var residentItems = residents.stream()
            .map(r -> new HistoryItem(r.getId(), r.getCommunityId(), r.getActive(), r.getLastUpdated()));

        var historyItems = residentHistory.stream()
            .map(r -> new HistoryItem(r.getClientId(), r.getCommunityId(), r.getActive(), r.getUpdatedDatetime()));

        Stream.of(residentItems, historyItems)
            .flatMap(Function.identity())
            .sorted(Comparator.comparing(r -> r.date))
            .forEach(client -> {
                if (client.active) {
                    addActiveClient(maxActiveClientsPerCommunityMap, activeClientsPerCommunityMap, client);
                } else {
                    removeActiveClient(activeClientsPerCommunityMap, client);
                }
            });

        return maxActiveClientsPerCommunityMap;
    }

    private void removeActiveClient(HashMap<Long, Set<Long>> activeClientsMap, HistoryItem item) {
        var set = activeClientsMap.get(item.communityId);
        if (set != null) {
            set.remove(item.clientId);
        }
    }

    private void addActiveClient(
        HashMap<Long, Long> maxActiveCountMap,
        HashMap<Long, Set<Long>> activeClientsMap,
        HistoryItem client
    ) {
        var activeClientSet = activeClientsMap.computeIfAbsent(client.communityId, (k) -> new HashSet<>());
        activeClientSet.add(client.clientId);
        maxActiveCountMap.put(
            client.communityId,
            Math.max(maxActiveCountMap.getOrDefault(client.communityId, 0L), activeClientSet.size())
        );
    }

    private Map<Long, Long> extractResultByEventType(
        List<EventCountByCommunityAndEventTypeItem> result,
        String eventTypeCode
    ) {
        return result.stream()
            .filter(r -> r.getEventTypeCode().equals(eventTypeCode))
            .collect(
                Collectors.toMap(
                    EventCountByCommunityAndEventTypeItem::getCommunityId,
                    EventCountByCommunityAndEventTypeItem::getCount
                )
            );
    }
}

