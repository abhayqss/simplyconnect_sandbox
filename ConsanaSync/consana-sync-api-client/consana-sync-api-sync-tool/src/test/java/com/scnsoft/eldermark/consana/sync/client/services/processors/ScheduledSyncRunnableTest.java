package com.scnsoft.eldermark.consana.sync.client.services.processors;

import com.scnsoft.eldermark.consana.sync.client.beans.ApplicationSyncContext;
import com.scnsoft.eldermark.consana.sync.client.entities.Database;
import com.scnsoft.eldermark.consana.sync.client.entities.Organization;
import com.scnsoft.eldermark.consana.sync.client.services.CommunityService;
import com.scnsoft.eldermark.consana.sync.client.services.SyncSender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledSyncRunnableTest {

    @Mock
    private SyncSender syncSender;

    @Mock
    private CommunityService communityService;

    @InjectMocks
    private ScheduledSyncRunnable runnable;

    @Captor
    ArgumentCaptor<List<Long>> listArgumentCaptor;

    @Test
    public void run_whenFirstSync_pushAllAndPopulateMap() {
        var communityIds = Arrays.asList(1L, 2L);
        var dataSyncUpdateTimes = new ConcurrentHashMap<Long, Instant>();

        var syncTime1 = Instant.now();

        var community1 = prepareCommunity(1L, syncTime1);
        var community2 = prepareCommunity(2L, syncTime1);
        var communityList = Arrays.asList(community1, community2);

        var syncContext = new ApplicationSyncContext();
        syncContext.setCommunityIds(communityIds);
        syncContext.setSchedule(1);
        syncContext.setCheckDataSyncStatus(true);

        runnable.setSyncContext(syncContext);
        runnable.setDataSyncUpdateTimes(dataSyncUpdateTimes);

        when(communityService.findAllByIds(communityIds)).thenReturn(communityList);
        doNothing().when(syncSender).sendSyncNotifications(listArgumentCaptor.capture());

        runnable.run();

        Assertions.assertThat(dataSyncUpdateTimes).containsAllEntriesOf(Map.of(1L, syncTime1, 2L, syncTime1));
        Assertions.assertThat(listArgumentCaptor.getValue()).containsExactlyElementsOf(communityIds);
    }

    @Test
    public void run_whenSyncTimeNotUpdated_DontPush() {
        var communityIds = Arrays.asList(1L, 2L);

        var syncTime1 = Instant.now();
        var syncTime2 = syncTime1.plus(1, ChronoUnit.HOURS);

        var community1 = prepareCommunity(1L, syncTime1);
        var community2 = prepareCommunity(2L, syncTime2);
        var communityList = Arrays.asList(community1, community2);

        var syncContext = new ApplicationSyncContext();
        syncContext.setCommunityIds(communityIds);
        syncContext.setSchedule(1);
        syncContext.setCheckDataSyncStatus(true);

        var dataSyncUpdateTimes = new ConcurrentHashMap<Long, Instant>();
        dataSyncUpdateTimes.put(1L, syncTime1);
        dataSyncUpdateTimes.put(2L, syncTime2);

        runnable.setSyncContext(syncContext);
        runnable.setDataSyncUpdateTimes(dataSyncUpdateTimes);

        when(communityService.findAllByIds(communityIds)).thenReturn(communityList);
        doNothing().when(syncSender).sendSyncNotifications(listArgumentCaptor.capture());

        runnable.run();

        Assertions.assertThat(dataSyncUpdateTimes).containsAllEntriesOf(Map.of(1L, syncTime1, 2L, syncTime2));
        Assertions.assertThat(listArgumentCaptor.getValue()).isEmpty();
    }

    @Test
    public void run_whenSyncTimeUpdated_PushUpdatedAndPopulateMap() {
        var communityIds = Arrays.asList(1L, 2L);

        var syncTime1 = Instant.now();
        var syncTime2 = syncTime1.plus(1, ChronoUnit.HOURS);

        var community1 = prepareCommunity(1L, syncTime1);
        var community2 = prepareCommunity(2L, syncTime2);
        var communityList = Arrays.asList(community1, community2);

        var syncContext = new ApplicationSyncContext();
        syncContext.setCommunityIds(communityIds);
        syncContext.setSchedule(1);
        syncContext.setCheckDataSyncStatus(true);

        var dataSyncUpdateTimes = new ConcurrentHashMap<Long, Instant>();
        dataSyncUpdateTimes.put(1L, syncTime1);
        dataSyncUpdateTimes.put(2L, syncTime1);

        runnable.setSyncContext(syncContext);
        runnable.setDataSyncUpdateTimes(dataSyncUpdateTimes);

        when(communityService.findAllByIds(communityIds)).thenReturn(communityList);
        doNothing().when(syncSender).sendSyncNotifications(listArgumentCaptor.capture());

        runnable.run();

        Assertions.assertThat(dataSyncUpdateTimes).containsAllEntriesOf(Map.of(1L, syncTime1, 2L, syncTime2));
        Assertions.assertThat(listArgumentCaptor.getValue()).containsOnlyOnce(2L);
    }


    private Organization prepareCommunity(Long id, Instant lastSync) {
        var community = new Organization();
        community.setId(id);
        community.setDatabase(prepareOrganization(lastSync));

        return community;
    }

    private Database prepareOrganization(Instant lastSync) {
        var org = new Database();
        org.setLastSyncSuccessDate(lastSync);
        return org;
    }


}