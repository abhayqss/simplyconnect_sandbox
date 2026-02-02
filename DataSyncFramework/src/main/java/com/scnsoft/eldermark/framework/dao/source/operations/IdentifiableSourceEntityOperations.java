package com.scnsoft.eldermark.framework.dao.source.operations;

import com.scnsoft.eldermark.framework.connector4d.ResultListParameters;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.dao.source.columnexpressions.SelectExpression;
import com.scnsoft.eldermark.framework.dao.source.filters.MaxIdFilter;
import com.scnsoft.eldermark.framework.dao.source.filters.SourceEntitiesFilter;
import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

public interface IdentifiableSourceEntityOperations<E extends IdentifiableSourceEntity<ID>, ID extends Comparable<ID>>
        extends SourceEntityOperations<E> {
    List<E> getEntities(Sql4DOperations sql4DOperations, List<SelectExpression> selectExpressions, ResultListParameters resultListParameters, RowMapper<E> rowMapper,
                        SourceEntitiesFilter<ID> filter);

    ID getMaxId(Sql4DOperations sql4DOperations, MaxIdFilter<ID> filter);
}
