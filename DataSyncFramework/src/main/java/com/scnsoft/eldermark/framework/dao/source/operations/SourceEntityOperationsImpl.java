package com.scnsoft.eldermark.framework.dao.source.operations;

import com.scnsoft.eldermark.framework.Utils;
import com.scnsoft.eldermark.framework.connector4d.ResultListParameters;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.dao.source.columnexpressions.SelectExpression;
import com.scnsoft.eldermark.framework.model.source.SourceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

public class SourceEntityOperationsImpl<E extends SourceEntity> implements SourceEntityOperations<E> {
    private static final Logger logger = LoggerFactory.getLogger(SourceEntityOperationsImpl.class);
    private final String tableName;

    public SourceEntityOperationsImpl(String tableName) {
        Utils.ensureNotNull(tableName, "tableName");
        this.tableName = tableName;
    }

    protected String getTableName() {
        return tableName;
    }

    @Override
    public List<E> getEntities(Sql4DOperations sql4DOperations, List<SelectExpression> selectExpressions, ResultListParameters parameters,  RowMapper<E> rowMapper) {
        logger.info("Loading records from table " + tableName + "...");
        Utils.ensureNotNull(sql4DOperations, "jdbcOperations");
        Utils.ensureNotNull(selectExpressions, "selectExpressions");
        Utils.ensureNotNull(rowMapper, "rowMapper");

        StringBuilder sb = new StringBuilder();
        appendSelectClause(sb, selectExpressions);
        sb.append(" FROM ").append(tableName);

        String sqlQuery = sb.toString();
        logger.info("Executing query " + sqlQuery + "...");
        List<E> entities = sql4DOperations.query(sqlQuery, parameters, rowMapper);
        logger.info("Query " + sqlQuery + " has been executed.");
        return entities;
    }

    protected void appendSelectClause(StringBuilder sb, List<SelectExpression> selectExpressions) {
        sb.append("SELECT ");
        for (int i = 0; i < selectExpressions.size(); i++) {
            SelectExpression selectExpression = selectExpressions.get(i);
            if (selectExpression.isEscapingNeeded()) {
                sb.append("[");
            }
            sb.append(selectExpression.getValue());
            if (selectExpression.isEscapingNeeded()) {
                sb.append("]");
            }
            if (i != selectExpressions.size() - 1) {
                sb.append(", ");
            }
        }
    }
}
