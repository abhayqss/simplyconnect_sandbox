package com.scnsoft.eldermark.consana.sync.client.services;

import com.scnsoft.eldermark.consana.sync.client.dao.ResidentDao;
import com.scnsoft.eldermark.consana.sync.client.entities.Resident;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateType;
import com.scnsoft.eldermark.consana.sync.client.services.producers.ResidentUpdateQueueProducer;
import com.scnsoft.eldermark.consana.sync.common.services.db.SqlServerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.EnumSet;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncSenderImplTest {


    @Mock
    private ResidentUpdateQueueProducer residentUpdateQueueProducer;

    @Mock
    private SqlServerService sqlServerService;

    @Mock
    private ResidentDao residentDao;

    @InjectMocks
    private SyncSenderImpl instance;

    private static final Long community1 = 101L;
    private static final Long community2 = 105L;

    @Test
    void sendInitialSyncNotifications_whenCalled_ShouldCallQueueService() {
        var communities = Arrays.asList(community1, community2);

        var resident1 = buildResident(1L, community1);
        var resident2 = buildResident(2L, community1);
        var resident3 = buildResident(3L, community2);
        var resident4 = buildResident(4L, community2);

        var allUpdateType = EnumSet.allOf(ResidentUpdateType.class);

        lenient().when(residentDao.findAllByFacilityId(community1)).thenReturn(Arrays.asList(resident1, resident2));
        lenient().when(residentDao.findAllByFacilityId(community2)).thenReturn(Arrays.asList(resident3, resident4));

        instance.sendSyncNotifications(communities);

        verify(residentUpdateQueueProducer).putToResidentUpdateQueue(resident1.getId(), allUpdateType);
        verify(residentUpdateQueueProducer).putToResidentUpdateQueue(resident2.getId(), allUpdateType);
        verify(residentUpdateQueueProducer).putToResidentUpdateQueue(resident3.getId(), allUpdateType);
        verify(residentUpdateQueueProducer).putToResidentUpdateQueue(resident4.getId(), allUpdateType);
    }

    private Resident buildResident(Long id, Long communityId) {
        var resident = new Resident();

        resident.setId(id);
        resident.setFacilityId(communityId);

        return resident;
    }

}