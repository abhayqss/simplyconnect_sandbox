package com.scnsoft.eldermark.framework.dao.source.operations;

import com.scnsoft.eldermark.framework.Utils;
import com.scnsoft.eldermark.framework.connector4d.ResultListParameters;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.dao.source.columnexpressions.SelectExpression;
import com.scnsoft.eldermark.framework.dao.source.filters.MaxIdFilter;
import com.scnsoft.eldermark.framework.dao.source.filters.SourceEntitiesFilter;
import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collections;
import java.util.List;

public class IdentifiableSourceEntityOperationsImpl<E extends IdentifiableSourceEntity<ID>, ID extends Comparable<ID>>
        extends SourceEntityOperationsImpl<E> implements IdentifiableSourceEntityOperations<E, ID> {
    private final static Logger logger = LoggerFactory.getLogger(IdentifiableSourceEntityOperationsImpl.class);

    private final String idColumnName;
    private final String syncStatusColumnName;
    //private final String oldSyncStatusColumnName = "exchange_sync_status";
    private final Class<ID> idClass;

    public IdentifiableSourceEntityOperationsImpl(String tableName, String idColumnName,
                                                  String syncStatusColumnName, Class<ID> idClass) {
        super(tableName);
        Utils.ensureNotNull(idColumnName, "idColumnName");
        Utils.ensureNotNull(syncStatusColumnName, "syncStatusColumnName");
        Utils.ensureNotNull(idClass, "idClass");

        this.idColumnName = idColumnName;
        this.idClass = idClass;
        this.syncStatusColumnName = syncStatusColumnName;
    }

    @Override
    public List<E> getEntities(Sql4DOperations sql4DOperations,
                                       List<SelectExpression> selectExpressions, ResultListParameters parameters, RowMapper<E> rowMapper,
                                       SourceEntitiesFilter<ID> filter) {
        logger.info("Loading records from table " + getTableName() + "...");
        Utils.ensureNotNull(sql4DOperations, "jdbcOperations");
        Utils.ensureNotNull(selectExpressions, "selectExpressions");
        Utils.ensureNotNull(rowMapper, "rowMapper");
        Utils.ensureNotNull(filter, "filter");

        StringBuilder sb = new StringBuilder();
        appendSelectClause(sb, selectExpressions);
        sb.append(" FROM ").append(getTableName());
        appendJoinSql(sb);
        appendFilterSql(sb, filter);

        String sqlQuery = sb.toString();
        logger.info("Executing query " + sqlQuery + "...");
        List<E> entities = sql4DOperations.query(sqlQuery, parameters, rowMapper);
        logger.info("Query " + sqlQuery + " has been executed.");
        return entities;
    }

    public ID getMaxId(Sql4DOperations sql4DOperations,
                               MaxIdFilter<ID> filter) {
        logger.info("Loading max id from table " + getTableName() + "...");
        Utils.ensureNotNull(sql4DOperations, "jdbcOperations");
        Utils.ensureNotNull(filter, "filter");
        Utils.ensureNotNull(filter.getLastSyncEpoch(), "lastSyncEpoch");
        Utils.ensureNotNull(filter.getCurrentSyncEpoch(), "currentSyncEpoch");

        Long lastSyncEpoch = filter.getLastSyncEpoch();
        Long currentSyncEpoch = filter.getCurrentSyncEpoch();

        ID idLowerBoundExclusive = filter.getIdLowerBoundExclusive();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT [").append(idColumnName).append("] FROM ").append(getTableName());


        sb.append(" WHERE ");

        sb.append(syncStatusColumnName).append(" >= ").append(lastSyncEpoch.toString());
        sb.append(" AND ");
        sb.append(syncStatusColumnName).append(" < ").append(currentSyncEpoch.toString());

        /*sb.append(" AND ");
        sb.append(oldSyncStatusColumnName).append(" NOT IN (");
        sb.append("'").append(SyncStatus.ORPHAN.getValue()).append("'");
        sb.append(", ");
        sb.append("'").append(SyncStatus.ARCHIVED.getValue()).append("'");
        sb.append(") ");*/


        if (idLowerBoundExclusive != null) {
            sb.append(" AND ");
            sb.append("[").append(idColumnName).append("] > ")
                    .append(buildIdSqlLiteral(idLowerBoundExclusive));
        }

        if (filter.isOrderById()) {
            sb.append(" ORDER BY [").append(idColumnName).append("]");
        }

        if (filter.getLimit() != null) {
            sb.append(" LIMIT ").append(filter.getLimit());
        }

        String query = sb.toString();
        logger.info("Executing query: " + query + "...");
        //Because of a limited support of subqueries in 4D, max id is found by means of application code
        List<ID> ids = sql4DOperations.queryForList(query, idClass);
        logger.info("Executed query " + query + "; result set size is " + ids.size());
        return ids.isEmpty() ? null : Collections.max(ids);
    }

    protected void appendJoinSql(StringBuilder sb) {

    }

    private void appendFilterSql(StringBuilder sb, SourceEntitiesFilter<ID> filter) {
        Long currentSyncEpoch = filter.getCurrentSyncEpoch();
        Long lastSyncEpoch = filter.getLastSyncEpoch();
        ID idLowerBoundExclusive = filter.getIdLowerBoundExclusive();
        ID idUpperBoundInclusive = filter.getIdUpperBoundInclusive();
        List<ID> excludedIds = filter.getExcludedIds();

        sb.append(" WHERE ");

        sb.append(syncStatusColumnName).append(" >= ").append(lastSyncEpoch.toString());
        sb.append(" AND ");
        sb.append(syncStatusColumnName).append(" < ").append(currentSyncEpoch.toString());

        /*sb.append(" AND ");
        sb.append(oldSyncStatusColumnName).append(" NOT IN (");
        sb.append("'").append(SyncStatus.ORPHAN.getValue()).append("'");
        sb.append(", ");
        sb.append("'").append(SyncStatus.ARCHIVED.getValue()).append("'");
        sb.append(") ");*/

        if (idLowerBoundExclusive != null) {
            sb.append(" AND ");
            sb.append("[").append(idColumnName).append("] > ").append(buildIdSqlLiteral(idLowerBoundExclusive));
        }

        if (idUpperBoundInclusive != null) {
            sb.append(" AND ");
            sb.append("[").append(idColumnName).append("] <= ").append(buildIdSqlLiteral(idUpperBoundInclusive));
        }

        if (!Utils.isNullOrEmpty(excludedIds)) {
            sb.append(" AND ");
            sb.append("[").append(idColumnName).append("] NOT IN (");
            for (int i = 0; i < excludedIds.size(); i++) {
                ID id = excludedIds.get(i);
                sb.append(buildIdSqlLiteral(id));
                if (i != excludedIds.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")");
        }

    }

    private <I> String buildIdSqlLiteral(I id) {
        if (id instanceof Number) {
            return id.toString();
        } else if (id instanceof String) {
            return "'" + id.toString().replaceAll("'", "''") + "'";
        } else {
            throw new IllegalArgumentException("Unsupported id class '" + id.getClass().getName() + "'");
        }
    }

}
