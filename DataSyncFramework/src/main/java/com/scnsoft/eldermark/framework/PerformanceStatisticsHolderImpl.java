package com.scnsoft.eldermark.framework;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class PerformanceStatisticsHolderImpl implements PerformanceStatisticsHolder {
    private Map<String, Map<DataSyncStep, Long>> fullStatisticsMap = new HashMap<String, Map<DataSyncStep, Long>>();
    private Map<DataSyncStep, Long> syncStepsStatisticsMap = new HashMap<DataSyncStep, Long>();
    private Map<String, Long> entitySyncStatisticsMap = new HashMap<String, Long>();

    @Override
    public void registerExecutionTime(String sourceEntityName, DataSyncStep dataSyncStep, long executionTime) {
        Map<DataSyncStep, Long> entityStatisticsMap = fullStatisticsMap.get(sourceEntityName);
        if (entityStatisticsMap == null) {
            entityStatisticsMap = new EnumMap<DataSyncStep, Long>(DataSyncStep.class);
            fullStatisticsMap.put(sourceEntityName, entityStatisticsMap);
        }

        Long executionTimeSum = entityStatisticsMap.get(dataSyncStep);
        if (executionTimeSum == null) {
            entityStatisticsMap.put(dataSyncStep, executionTime);
        } else {
            long increasedTime = executionTimeSum + executionTime;
            entityStatisticsMap.put(dataSyncStep, increasedTime);
        }

        Long stepExecutionTimeSum = syncStepsStatisticsMap.get(dataSyncStep);
        if (stepExecutionTimeSum == null) {
            syncStepsStatisticsMap.put(dataSyncStep, executionTime);
        } else {
            syncStepsStatisticsMap.put(dataSyncStep, executionTime + stepExecutionTimeSum);
        }

        Long entitySyncTimeSum = entitySyncStatisticsMap.get(sourceEntityName);
        if (entitySyncTimeSum == null) {
            entitySyncStatisticsMap.put(sourceEntityName, executionTime);
        } else {
            entitySyncStatisticsMap.put(sourceEntityName, executionTime + entitySyncTimeSum);
        }
    }

    @Override
    public long getExecutionTime(DataSyncStep dataSyncStep) {
        Long executionTime = syncStepsStatisticsMap.get(dataSyncStep);
        return executionTime != null ? executionTime : 0;
    }

    @Override
    public long getExecutionTime(String sourceEntityName) {
        Long executionTime = entitySyncStatisticsMap.get(sourceEntityName);
        return executionTime != null ? executionTime : 0;
    }

    @Override
    public long getExecutionTime(DataSyncStep dataSyncStep, String sourceEntityName) {
        Map<DataSyncStep, Long> entityMap = fullStatisticsMap.get(sourceEntityName);

        Long executionTime = 0L;
        if (entityMap != null) {
            executionTime = entityMap.get(dataSyncStep);
            if (executionTime == null) {
                executionTime = 0L;
            }
        }
        return executionTime;
    }

    @Override
    public Collection<String> getRegisteredEntityNames() {
        return entitySyncStatisticsMap.keySet();
    }
}
