package com.scnsoft.eldermark.framework.dao.source;

import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.dao.source.filters.MaxIdFilter;

/**
 * @param <ID> type of source entity id
 */
public interface SourceDao<ID extends Comparable<ID>> {
    ID getMaxId(Sql4DOperations sql4DOperations, MaxIdFilter<ID> filter);
}
