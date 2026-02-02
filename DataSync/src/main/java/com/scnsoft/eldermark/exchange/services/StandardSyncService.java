package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.dao.source.filters.MaxIdFilter;
import com.scnsoft.eldermark.framework.dao.source.filters.SourceEntitiesFilter;
import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;

import java.util.List;

public abstract class StandardSyncService<E extends IdentifiableSourceEntity<LegacyId>, LegacyId extends Comparable<LegacyId>, FK>
        extends BaseSyncService<E, LegacyId, FK> {

    protected abstract StandardSourceDao<E, LegacyId> getSourceDao();

    @Override
    protected final List<E> getSourceEntities(DatabaseSyncContext syncContext, SourceEntitiesFilter<LegacyId> filter) {
        return getSourceDao().getEntities(syncContext.getSql4dOperations(), filter);
    }

    @Override
    protected final LegacyId getSourceEntitiesMaxId(DatabaseSyncContext syncContext, MaxIdFilter<LegacyId> filter) {
        return getSourceDao().getMaxId(syncContext.getSql4dOperations(), filter);
    }

}
