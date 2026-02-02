package com.scnsoft.eldermark.framework;

public final class EntityMetadata {
    private final String tableName;
    private final String idColumnName;
    private final Class<?> entityClass;

    public EntityMetadata(String tableName, String idColumnName,
                          Class<?> entityClass) {
        this.tableName = tableName;
        this.idColumnName = idColumnName;
        this.entityClass = entityClass;
    }

    public String getTableName() {
        return tableName;
    }

    public String getIdColumnName() {
        return idColumnName;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }
}
