package com.scnsoft.eldermark.util;

import com.scnsoft.eldermark.dao.SdohReportLogDao;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.sdoh.SdohReportLog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
class SdohReportUtilsImplTest {

    public static final String NEW_YORK_TIMEZONE = "America/New_York";
    @Mock
    private SdohReportLogDao dao;

    @InjectMocks
    private SdohReportUtilsImpl utils;

    @Test
    void testGenerateMissingReportPeriodsWhenReportsDoNotExist() {
        // Given
        Mockito.when(dao.findAll()).thenReturn(List.of());
        // Then
        utils.generateMissingReportPeriods();
        // When
        Mockito.verify(dao, Mockito.never()).saveAll(Mockito.any());
    }

    @Test
    void testGenerateMissingReportPeriodsWhenMissingReportsDoNotExist() {
        // Given
        var org = testOrganization();
        var firstPeriod = testSdohReportLog(
                org,
                LocalDateTime.of(2020, 1, 1, 1, 0, 0, 0),
                LocalDateTime.of(2020, 1, 31, 23, 59, 59, 999999900)
        );
        var secondPeriod = testSdohReportLog(
                org,
                LocalDateTime.of(2020, 2, 1, 1, 0, 0, 0),
                LocalDateTime.of(2020, 2, 29, 23, 59, 59, 999999900)
        );

        Mockito.when(dao.findAll()).thenReturn(List.of(firstPeriod, secondPeriod));
        // Then
        utils.generateMissingReportPeriods();
        // When
        Mockito.verify(dao, Mockito.never()).saveAll(Mockito.any());
    }

    @Test
    void testGenerateMissingReportPeriodsWhenMissingPeriodsExist() {
        // Given
        var org = testOrganization();

        Mockito.when(dao.findAll()).thenReturn(List.of(
                testSdohReportLog(
                        org,
                        LocalDateTime.of(2020, 1, 1, 1, 0, 0, 0),
                        LocalDateTime.of(2020, 1, 31, 23, 59, 59, 999999900)
                ),
                testSdohReportLog(
                        org,
                        LocalDateTime.of(2020, 3, 1, 1, 0, 0, 0),
                        LocalDateTime.of(2020, 3, 31, 23, 59, 59, 999999900)
                ),
                testSdohReportLog(
                        org,
                        LocalDateTime.of(2020, 5, 1, 1, 0, 0, 0),
                        LocalDateTime.of(2020, 5, 31, 23, 59, 59, 999999900)
                )
        ));
        // Then
        utils.generateMissingReportPeriods();
        // When
        Mockito.verify(dao)
                .saveAll(Mockito.argThat(arg -> {
                    var list = (List<SdohReportLog>) arg;

                    if (list.size() != 2) return false;

                    if (!hasPeriod(
                            list.get(0),
                            LocalDateTime.of(2020, 2, 1, 1, 0, 0, 0),
                            LocalDateTime.of(2020, 2, 29, 23, 59, 59, 999999900)
                    )) return false;

                    return hasPeriod(
                            list.get(1),
                            LocalDateTime.of(2020, 4, 1, 1, 0, 0, 0),
                            LocalDateTime.of(2020, 4, 30, 23, 59, 59, 999999900)
                    );
                }));
    }

    private boolean hasPeriod(SdohReportLog log, LocalDateTime start, LocalDateTime end) {
        return Objects.equals(log.getPeriodStart(), start.atZone(ZoneId.of(NEW_YORK_TIMEZONE)).toInstant())
                && Objects.equals(log.getPeriodEnd(), end.atZone(ZoneId.of(NEW_YORK_TIMEZONE)).toInstant());
    }

    private Organization testOrganization() {
        var org = new Organization();
        org.setId(1L);
        org.setSdohZoneId(NEW_YORK_TIMEZONE);
        return org;
    }

    private SdohReportLog testSdohReportLog(
            Organization org,
            LocalDateTime start,
            LocalDateTime end
    ) {
        var log = new SdohReportLog();
        log.setOrganization(org);
        log.setOrganizationId(org.getId());
        log.setPeriodStart(start.atZone(ZoneId.of(NEW_YORK_TIMEZONE)).toInstant());
        log.setPeriodEnd(end.atZone(ZoneId.of(NEW_YORK_TIMEZONE)).toInstant());
        return log;
    }
}
