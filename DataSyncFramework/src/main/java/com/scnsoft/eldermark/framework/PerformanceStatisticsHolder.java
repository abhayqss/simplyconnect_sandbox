package com.scnsoft.eldermark.framework;

import java.util.Collection;

public interface PerformanceStatisticsHolder {
    void registerExecutionTime(String sourceEntityName, DataSyncStep dataSyncStep, long executionTime);

    long getExecutionTime(DataSyncStep dataSyncStep);

    long getExecutionTime(String sourceEntityName);

    long getExecutionTime(DataSyncStep dataSyncStep, String sourceEntityName);

    Collection<String> getRegisteredEntityNames();
}
