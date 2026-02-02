package com.scnsoft.eldermark.framework;

public class DataSyncConfiguration {
    private final int loadBatchSize;
    private final int idMappingBatchSize;

    public DataSyncConfiguration(int loadBatchSize, int idMappingBatchSize) {
        this.loadBatchSize = loadBatchSize;
        this.idMappingBatchSize = idMappingBatchSize;

        Utils.ensurePositive(loadBatchSize, "loadBatchSize");
        Utils.ensurePositive(idMappingBatchSize, "idMappingBatchSize");

        if (idMappingBatchSize > loadBatchSize) {
            throw new IllegalArgumentException("idMappingBatchSize cannot exceed loadBatchSize");
        }
    }

    public int getLoadBatchSize() {
        return loadBatchSize;
    }

    public int getIdMappingBatchSize() {
        return idMappingBatchSize;
    }
}
