package com.scnsoft.eldermark.framework;

import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.CurrentTimestampDao;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class DataSyncImpl implements DataSync {
    private static final Logger logger = LoggerFactory.getLogger(DataSyncImpl.class);

    @Autowired
    private DeletedRecordsSyncService deletedRecordsSyncService;

    @Autowired
    private DataSyncListenersManager listenersManager;

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Autowired
    private SyncOrderResolver syncOrderResolver;

    @Autowired
    private CurrentTimestampDao currentTimestampDaoImpl;

    @Autowired
    private ExecutorService executorService;

    private AtomicBoolean isSyncInProgress = new AtomicBoolean(false);

    private List<DatabasesWithSyncServices> databasesWithSyncServices;

    private boolean isSyncDeleteRecord = true;

    private Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping;

    public void setDatabasesWithSyncServices(List<Pair<List<DatabaseInfo>, Set<SyncService>>> databasesWithSyncServicesList) {
        this.databasesWithSyncServices = new ArrayList<DatabasesWithSyncServices>();
        for (Pair<List<DatabaseInfo>, Set<SyncService>> databasesWithSyncServicesPair : databasesWithSyncServicesList) {
            List<SyncService> syncServices = syncOrderResolver.order(databasesWithSyncServicesPair.getValue());
            List<SyncService> syncServicesReversed = new ArrayList<SyncService>();
            for (SyncService syncService : syncServices) {
                if (syncService.getDeletionRelatedOperations() != null) {
                    if (syncServicesReversed.isEmpty()) {
                        syncServicesReversed.add(syncService);
                    } else {
                        syncServicesReversed.add(0, syncService);
                    }
                }
            }
            Set<DatabaseInfo> databaseInfoSet = new HashSet<DatabaseInfo>(databasesWithSyncServicesPair.getKey());
            List<DatabaseInfo> databaseInfos = new ArrayList<DatabaseInfo>(databaseInfoSet);
            DatabasesWithSyncServices databasesWithSyncServices = new DatabasesWithSyncServices(databaseInfos, syncServices, syncServicesReversed);
            this.databasesWithSyncServices.add(databasesWithSyncServices);
        }
    }

    public List<DatabasesWithSyncServices> getDatabasesWithSyncServices() {
        return databasesWithSyncServices;
    }

    @Override
    public void addListener(DataSyncListener listener) {
        listenersManager.register(listener);
    }

    @Override
    public void removeListener(DataSyncListener listener) {
        listenersManager.unregister(listener);
    }

    @Override
    public void setStatsListener(DataSyncStatsListener listener) {
        listenersManager.registerIterationListener(listener);
    }
    
    @Override
	public void setIsSyncDeleteRecord(boolean isSyncDeleteRecord) {
    	this.isSyncDeleteRecord = isSyncDeleteRecord;
    }

    @Override
    public void run() {
        try {
            ensureConfigured();

            //Prevents multiple sync executions at the same time either for running by hand or by scheduler
            if (!isSyncInProgress.compareAndSet(false, true)) {
                return;
            }

            listenersManager.notifyAboutIterationStarted();
            logger.info("Starting data synchronization...");

            for (DatabasesWithSyncServices dbsWithSyncServices : databasesWithSyncServices) {
                List<DatabaseInfo> databases = dbsWithSyncServices.getDatabases();
                for (DatabaseInfo database : syncOrderResolver.order(databases)) {
                    sync(database, dbsWithSyncServices.getSyncServices(), dbsWithSyncServices.getSyncServicesReversed());
                }
            }

            listenersManager.notifyAboutIterationCompleted();
            logger.info("Data synchronization has completed.");

            isSyncInProgress.set(false);

        } catch (Exception e) {
            logger.error("Data sync error: " + e.getMessage(), e);
        }
    }

    private void sync(DatabaseInfo database, List<SyncService> syncServices, List<SyncService> syncServicesReversed) {
        logger.info("Synchronizing database " + database.getUrl() + "...");
        listenersManager.notifyAboutDatabaseStarted(database);

        PerformanceStatisticsHolder performanceStatisticsHolder = new PerformanceStatisticsHolderImpl();

        Date syncRevision = new Date();
        BasicDataSource dataSource = null;
        try {
            dataSource = dataSourceFactory.createDatasource(database);
            DatabaseSyncContext syncContext = null;
            Boolean isXmlSync = Boolean.TRUE.equals(database.getIsXmlSync());
            if (isXmlSync) {
                syncContext = DatabaseSyncContext.createXmlDatabaseSyncContext(database, executorService, targetOrganizationsIdMapping);
            } else {
                syncContext = DatabaseSyncContext.createOdbcDatabaseSyncContext(database, new JdbcTemplate(dataSource), executorService, targetOrganizationsIdMapping);
            }
            
            if(database.getIsInitialSync() && database.getLastSyncedTime() != 0) {
            	database.setCurrentSyncTime(database.getLastSyncedTime());
            	database.setLastSyncedTime(0L);
            }
            else {
            	 Long currentDbTime = currentTimestampDaoImpl.getCurrentTimeStamp(syncContext.getSql4dOperations());
                 database.setCurrentSyncTime(currentDbTime);
            }
            
            List<DataSyncStatsInfo> syncStatsInfos = null;
            if(database.getIsInitialSync()) {
            	listenersManager.notifyAboutInitialSyncStarted(database);
            	syncStatsInfos = listenersManager
            			.getDataSyncStatsListener().getLastSyncStatInfo(syncContext.getDatabase());
            }
            
            for (SyncService syncService : syncServices) {
                listenersManager.notifyAboutSyncServiceStarted(database, syncService.getClass());
                if(!isSyncServiceCompleted(syncContext, syncService, syncStatsInfos)) {
	                syncService.syncNewAndUpdated(syncContext, performanceStatisticsHolder);
                }
                syncService.afterNewAndUpdatedSynced(syncContext);
                listenersManager.notifyAboutSyncServiceCompleted();
            }
            
            if(!database.getIsInitialSync() && isSyncDeleteRecord) {
            	deletedRecordsSyncService.sync(syncContext, syncServicesReversed, performanceStatisticsHolder);
            }

            // printStatistics(database, performanceStatisticsHolder);
            reportSuccess(database, syncRevision);
            listenersManager.notifyAboutDatabaseCompleted();
        } catch (Exception e) {
            reportError(database, e);
        } finally {
            close(dataSource);
        }
    }
    
    public boolean isSyncServiceCompleted(DatabaseSyncContext syncContext, SyncService syncService
    		,List<DataSyncStatsInfo> syncStatsInfos) {
    	if(syncStatsInfos == null || syncStatsInfos.isEmpty()) {
    		return false;
    	}
    	
    	boolean isSyncServiceCompleted = false;
    	for(DataSyncStatsInfo syncStatInfo : syncStatsInfos) {
    		if(syncStatInfo.getSyncServiceName().equals(syncService.getClass().getSimpleName())) {
    			if(syncStatInfo.getCompleted() == null) {
    				syncContext.setServiceReSync(true);
    			}
    			else {
    				isSyncServiceCompleted = true;
    			}
    			break;
    		}
    	}
    	return isSyncServiceCompleted;
    }

    public void setTargetOrganizationsIdMapping(Map<Long, DatabaseIdWithId> organizationsIdMapping) {
        this.targetOrganizationsIdMapping = organizationsIdMapping;
    }

    private void ensureConfigured() {
        if (CollectionUtils.isEmpty(databasesWithSyncServices)) {
            throw new IllegalStateException("databases aren't set");
        }
        for (DatabasesWithSyncServices db : databasesWithSyncServices) {
            if (db.getDatabases() == null) {
                throw new IllegalStateException("databases aren't set");
            }

            if (db.getSyncServices() == null) {
                throw new IllegalStateException("syncServices aren't set");
            }
        }
    }

    private void close(BasicDataSource dataSource) {
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException e) {
                logger.error("Failed to close datasource for database", e);
            }
        }
    }

    private void reportSuccess(DatabaseInfo database, Date syncRevision) {
        String text = "Successfully synchronized database " + database.getUrl();

        SuccessMessage message = new SuccessMessage();
        message.setDate(new Date());
        message.setSyncRevision(syncRevision);
        message.setDatabase(database);
        message.setText(text);

        listenersManager.notifyAboutSuccess(message);
        logger.info(text);
    }

    private void reportError(DatabaseInfo database, Exception e) {
        String text = "An error occured during synchronization of database '" + database.getUrl() +
                "'. Skipped this database, because it's not possible to continue. Error details: '"
                + e.getMessage() + "'";

        ErrorMessage message = new ErrorMessage();
        message.setDate(new Date());
        message.setDatabase(database);
        message.setText(text);
        message.setStackTrace(Utils.getStacktraceAsString(e));

        listenersManager.notifyAboutErrors(Collections.singletonList(message));
        logger.error(text, e);
    }

    private void printStatistics(DatabaseInfo database, PerformanceStatisticsHolder performanceStatisticsHolder) {
        logger.info("\n\nData sync statistics for database " + database.getUrl());
        for (DataSyncStep syncStep : DataSyncStep.values()) {
            logger.info("{}->{} ms", syncStep.toString(), performanceStatisticsHolder.getExecutionTime(syncStep));
        }

        logger.info("\n");
        for (String sourceEntityName : performanceStatisticsHolder.getRegisteredEntityNames()) {
            logger.info("{}->{} ms", sourceEntityName, performanceStatisticsHolder.getExecutionTime(sourceEntityName));
            for (DataSyncStep syncStep : DataSyncStep.values()) {
                logger.info("\t{}.{}->{} ms", sourceEntityName, syncStep,
                        performanceStatisticsHolder.getExecutionTime(syncStep, sourceEntityName));
            }
            logger.info("\n");
        }
    }

    private class DatabasesWithSyncServices {
        private List<DatabaseInfo> databases;
        private List<SyncService> syncServices;
        private List<SyncService> syncServicesReversed;

        public DatabasesWithSyncServices(List<DatabaseInfo> databases, List<SyncService> syncServices, List<SyncService> syncServicesReversed) {
            this.databases = databases;
            this.syncServices = syncServices;
            this.syncServicesReversed = syncServicesReversed;
        }

        public List<DatabaseInfo> getDatabases() {
            return databases;
        }

        public List<SyncService> getSyncServices() {
            return syncServices;
        }

        public List<SyncService> getSyncServicesReversed() {
            return syncServicesReversed;
        }
    }
}
