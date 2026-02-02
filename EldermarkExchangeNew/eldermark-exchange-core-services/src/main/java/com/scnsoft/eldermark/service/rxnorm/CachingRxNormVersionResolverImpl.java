package com.scnsoft.eldermark.service.rxnorm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class CachingRxNormVersionResolverImpl implements CachingRxNormVersionResolver {

   private final Duration cachingPeriod = Duration.ofDays(1);


    private volatile Instant lastRxNormVersionCheck;
    private volatile String rxNormDatasetVersion;

    @Autowired
    private RxNormApiGateway rxNormApiGateway;

    @Override
    public String getRxNormVersion() {
        if (lastRxNormVersionCheck == null || lastRxNormVersionCheck.isBefore(Instant.now().minus(cachingPeriod))) {
            rxNormDatasetVersion = rxNormApiGateway.getVersion();
            lastRxNormVersionCheck = Instant.now();
        }
        return rxNormDatasetVersion;
    }

    Instant getLastRxNormVersionCheck() {
        return lastRxNormVersionCheck;
    }

    void setLastRxNormVersionCheck(Instant lastRxNormVersionCheck) {
        this.lastRxNormVersionCheck = lastRxNormVersionCheck;
    }

    String getRxNormDatasetVersion() {
        return rxNormDatasetVersion;
    }

    void setRxNormDatasetVersion(String rxNormDatasetVersion) {
        this.rxNormDatasetVersion = rxNormDatasetVersion;
    }
}
