package com.scnsoft.eldermark.framework.dao.source.operations;

import com.scnsoft.eldermark.framework.connector4d.ResultListParameters;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.dao.source.columnexpressions.SelectExpression;
import com.scnsoft.eldermark.framework.model.source.SourceEntity;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

public interface SourceEntityOperations<E extends SourceEntity> {
    List<E> getEntities(Sql4DOperations sql4DOperations, List<SelectExpression> selectExpressions, ResultListParameters parameters, RowMapper<E> rowMapper);
}
