package com.scnsoft.eldermark.framework.dao.source;

import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.dao.source.filters.SourceEntitiesFilter;
import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;

import java.util.List;

/**
 * @param <E>  source entity type
 * @param <ID> type of source entity id
 */
public interface StandardSourceDao<E extends IdentifiableSourceEntity<ID>, ID extends Comparable<ID>>
        extends SourceDao<ID> {
    List<E> getEntities(Sql4DOperations sql4DOperations, SourceEntitiesFilter<ID> filter);
}
