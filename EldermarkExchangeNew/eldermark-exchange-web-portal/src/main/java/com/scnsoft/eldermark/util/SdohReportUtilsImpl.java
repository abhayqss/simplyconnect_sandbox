package com.scnsoft.eldermark.util;

import com.scnsoft.eldermark.dao.SdohReportLogDao;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.sdoh.SdohReportLog;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SdohReportUtilsImpl implements SdohReportUtils {

    @Autowired
    private SdohReportLogDao sdohReportLogDao;

    @Override
    public void generateMissingReportPeriods() {

        var allReportLogs = sdohReportLogDao.findAll();
        allReportLogs.stream()
                .collect(Collectors.groupingBy(SdohReportLog::getOrganizationId))
                .forEach((organizationId, organizationReports) -> {

                    var sdohReportLogsToAdd = new ArrayList<SdohReportLog>();

                    var organization = organizationReports.get(0).getOrganization();
                    var zoneId = getZoneId(organization);

                    var periods = organizationReports.stream()
                            .map(it -> Pair.of(it.getPeriodStart(), it.getPeriodEnd()))
                            .sorted(Comparator.comparing(Pair::getFirst))
                            .collect(Collectors.toList());

                    var firstPeriod = periods.get(0);
                    var lastPeriod = periods.get(periods.size() - 1);

                    var period = firstPeriod;

                    while (!Objects.equals(period = nextMonthPeriod(period, zoneId), lastPeriod)) {
                        if (!periods.contains(period)) {
                            var newSdohReportLog = new SdohReportLog();
                            newSdohReportLog.setOrganization(organization);
                            newSdohReportLog.setPeriodStart(period.getFirst());
                            newSdohReportLog.setPeriodEnd(period.getSecond());
                            sdohReportLogsToAdd.add(newSdohReportLog);
                        }
                    }

                    if (!sdohReportLogsToAdd.isEmpty()) {
                        sdohReportLogDao.saveAll(sdohReportLogsToAdd);
                    }
                });
    }

    private ZoneId getZoneId(Organization organization) {
        return Optional.ofNullable(organization.getSdohZoneId())
                .map(ZoneId::of)
                .orElse(ZoneId.systemDefault());
    }

    public Pair<Instant, Instant> nextMonthPeriod(Pair<Instant, Instant> period, ZoneId zoneId) {

        var nextMonthStart = period.getFirst().atZone(zoneId).plusMonths(1).toInstant();
        var nextMonthLastDay = nextMonthStart.atZone(zoneId).plusMonths(1).minusDays(1).toInstant();
        var nextMonthEnd = DateTimeUtils.atDatabaseEndOfDay(nextMonthLastDay, zoneId);

        return Pair.of(nextMonthStart, nextMonthEnd);
    }
}
