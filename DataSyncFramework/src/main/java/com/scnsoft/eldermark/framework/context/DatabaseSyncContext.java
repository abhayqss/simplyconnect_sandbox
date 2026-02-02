package com.scnsoft.eldermark.framework.context;

import com.scnsoft.eldermark.framework.DatabaseIdWithId;
import com.scnsoft.eldermark.framework.DatabaseInfo;
import com.scnsoft.eldermark.framework.SourceAndTargetId;
import com.scnsoft.eldermark.framework.Utils;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.connector4d.sql4dConnector.Jdbc4DOperations;
import com.scnsoft.eldermark.framework.connector4d.xml4Dconnector.template.Xml4DOperationsFactory;
import com.scnsoft.eldermark.framework.dao.source.TimeoutProxyFactory;
import org.springframework.jdbc.core.JdbcOperations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public final class DatabaseSyncContext {
    private final DatabaseInfo database;
    private Sql4DOperations sql4DOperations;
    private boolean isServiceReSync;
    private Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping;
    private Map<Long, DatabaseIdWithId> mappedResidentIds;

    private final Map<Class<?>, Object> sharedObjectsMap = new HashMap<Class<?>, Object>();

    public static DatabaseSyncContext createOdbcDatabaseSyncContext(DatabaseInfo database, JdbcOperations jdbcOperations, ExecutorService executorService, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        return new DatabaseSyncContext(database, jdbcOperations, executorService, targetOrganizationsIdMapping);
    }
    public static DatabaseSyncContext createXmlDatabaseSyncContext(DatabaseInfo database, ExecutorService executorService, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        return new DatabaseSyncContext(database, executorService, targetOrganizationsIdMapping);
    }

    private DatabaseSyncContext(DatabaseInfo database, JdbcOperations jdbcOperations, ExecutorService executorService, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        Utils.ensureNotNull(database, "database");
        Utils.ensureNotNull(jdbcOperations, "jdbcOperations");
        this.database = database;
        JdbcOperations jdbcOperationsTimeoutProxy = TimeoutProxyFactory.getProxy(JdbcOperations.class, jdbcOperations, executorService);
        this.sql4DOperations = new Jdbc4DOperations(jdbcOperationsTimeoutProxy);
        this.targetOrganizationsIdMapping = targetOrganizationsIdMapping;
        this.mappedResidentIds = new HashMap<Long, DatabaseIdWithId>();
    }

    private DatabaseSyncContext(DatabaseInfo database, ExecutorService executorService, Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        Utils.ensureNotNull(database, "database");
        this.database = database;
        this.sql4DOperations = new Xml4DOperationsFactory().create(database.getRemoteHost(), database.getRemotePort(), database.getRemoteUsername(), database.getRemotePassword(), Boolean.TRUE.equals(database.getRemoteUseSsl()));
        this.targetOrganizationsIdMapping = targetOrganizationsIdMapping;
        this.mappedResidentIds = new HashMap<Long, DatabaseIdWithId>();
    }

    public long getDatabaseId() {
        return database.getId();
    }

    public String getDatabaseUrl() {
        return database.getUrl();
    }

    public DatabaseInfo getDatabase() {
        return database;
    }

    public Sql4DOperations getSql4dOperations() {
        return sql4DOperations;
    }

    public <T> void putSharedObject(Class<T> valueType, T value) {
        sharedObjectsMap.put(valueType, value);
    }

    public <T> T getSharedObject(Class<T> valueType) {
        return valueType.cast(sharedObjectsMap.get(valueType));
    }

	public boolean isServiceReSync() {
		return isServiceReSync;
	}

	public void setServiceReSync(boolean isServiceReSync) {
		this.isServiceReSync = isServiceReSync;
	}

    public Map<Long, DatabaseIdWithId> getTargetOrganizationsIdMapping() {
        return targetOrganizationsIdMapping;
    }

    public void setTargetOrganizationsIdMapping(Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        this.targetOrganizationsIdMapping = targetOrganizationsIdMapping;
    }

    public Map<Long, DatabaseIdWithId> getMappedResidentIds() {
        return mappedResidentIds;
    }

    public void setMappedResidentIds(Map<Long, DatabaseIdWithId> mappedResidentIds) {
        this.mappedResidentIds = mappedResidentIds;
    }
}
