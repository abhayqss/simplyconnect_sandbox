package com.scnsoft.eldermark.service.rxnorm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachingRxNormVersionResolverImplTest {

    @Mock
    private RxNormApiGateway rxNormApiGateway;

    @InjectMocks
    private CachingRxNormVersionResolverImpl instance;

    @Test
    void getRxNormVersion_firstTimeVersionCheck_loadsVersionFromApiAndCaches() {
        var rxNormVersion = "version1";
        instance.setLastRxNormVersionCheck(null);
        instance.setRxNormDatasetVersion(null);

        when(rxNormApiGateway.getVersion()).thenReturn(rxNormVersion);

        var actual = instance.getRxNormVersion();

        assertEquals(rxNormVersion, actual);
        assertEquals(rxNormVersion, instance.getRxNormVersion());
        assertNotNull(instance.getLastRxNormVersionCheck());

        verifyNoMoreInteractions(rxNormApiGateway);
    }

    @Test
    void getRxNormVersion_cachedWithinOneDay_returnsCachedVersion() {
        var rxNormVersion = "version1";
        instance.setLastRxNormVersionCheck(Instant.now().minusSeconds(5000L));
        instance.setRxNormDatasetVersion(rxNormVersion);

        var actual = instance.getRxNormVersion();

        assertEquals(rxNormVersion, actual);
        assertEquals(rxNormVersion, instance.getRxNormVersion());
        assertNotNull(instance.getLastRxNormVersionCheck());

        verifyNoInteractions(rxNormApiGateway);
    }

    @Test
    void getRxNormVersion_cachedMoreThanOneDayAgo_loadsVersionFromApiAndCaches() {
        var rxNormVersion = "version1";
        var oldCacheDate = Instant.now().minus(1, ChronoUnit.DAYS).minusSeconds(5L);
        instance.setLastRxNormVersionCheck(oldCacheDate);
        instance.setRxNormDatasetVersion("old version");

        when(rxNormApiGateway.getVersion()).thenReturn(rxNormVersion);

        var actual = instance.getRxNormVersion();

        assertEquals(rxNormVersion, actual);
        assertEquals(rxNormVersion, instance.getRxNormVersion());
        assertTrue(instance.getLastRxNormVersionCheck().isAfter(oldCacheDate));

        verifyNoMoreInteractions(rxNormApiGateway);
    }

}