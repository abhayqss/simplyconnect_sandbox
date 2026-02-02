package com.scnsoft.eldermark.framework;

import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;

import java.util.List;

/**
 * Represents operations for syncing any concrete table in source database with corresponding table (tables) in target database.
 * Services are invoked by DataSyncFramework in correct order according to dependencies defined
 * by {@link SyncService#dependsOn} method. For deletion sync order is reverse. Each sync service may either support:
 * <ul>
 * <li>syncing of new/existing and deleted records</li>
 * <li>syncing only new/existing records</li>
 * </ul>
 * (see {@link SyncService#getDeletionRelatedOperations} for details).
 */
public interface SyncService {
    /**
     * Specifies which sync services should be invoked by DataSyncFramework prior to invoking this sync service.
     * If null or empty list is returned, DataSyncFramework assumes that this sync service doesn't depend on any other
     * sync services.
     *
     * @return a list of dependency sync services
     */
    List<Class<? extends SyncService>> dependsOn();

    /**
     * Synchronizes new and existing entities.
     *
     * @param syncContext                 sync context of currently synchronized database (can be used to share data between sync services)
     * @param performanceStatisticsHolder an object for registration execution time
     */
    void syncNewAndUpdated(DatabaseSyncContext syncContext, PerformanceStatisticsHolder performanceStatisticsHolder);

    /**
     * Invoked by DataSyncFramework directly after new and existing entities have been synchronized.
     * Can be used for example, for loading id mappings, creation of id resolvers and sharing them
     * with other sync services for later usage.
     *
     * @param context sync context of currently synchronized database (can be used to share data between sync services)
     */
    void afterNewAndUpdatedSynced(DatabaseSyncContext context);

    /**
     * Returns operations set required to perform sync of deleted entities
     *
     * @return an instance of {@link com.scnsoft.eldermark.framework.SyncService.DeletionRelatedOperations} if sync of deleted entities is necessary
     *         or null if entity cannot be ever deleted and thus sync of deleted entities isn't necessary
     */
    DeletionRelatedOperations getDeletionRelatedOperations();

    /**
     * Provides a set of operations used internally by DataSyncFramework for syncing deleted entities
     */
    interface DeletionRelatedOperations {
        /**
         * Provides metadata about source entity used internally by DataSyncFramework for syncing deleted entities
         *
         * @return source entity metadata
         */
        EntityMetadata provideSourceEntityMetadata();


        /**
         * Invoked when a deleted entity is found in source database.
         *
         * @param syncContext    sync context of currently synchronized database (can be used to share data between sync services)
         * @param legacyIdString string representation of entity legacy id
         */
        void onDeletedEntityFound(DatabaseSyncContext syncContext, String legacyIdString);
    }
}
