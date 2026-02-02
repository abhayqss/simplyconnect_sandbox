package com.scnsoft.exchange.audit.dao;

import com.scnsoft.exchange.audit.dao.mappers.SyncReportRowMapper;
import com.scnsoft.exchange.audit.model.SyncReportEntry;
import com.scnsoft.exchange.audit.model.filters.FilterBy;
import com.scnsoft.exchange.audit.model.filters.ReportFilter;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class DataSyncReportDaoImpl extends BaseDataSyncReportDao implements DataSyncReportDao<SyncReportEntry> {

    @Override
    public List<SyncReportEntry> findAll(ReportFilter filter) {
        Date date = filter.getCriteria(FilterBy.DATE_FROM, Date.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("    db.name as 'database_name', COALESCE(summary.error_count, 0) as error_count, ");
        sql.append("    CASE WHEN status.result is not null THEN 1 ELSE 0 END as 'result', db.last_success_sync_date ");
        sql.append("FROM SourceDatabase db ");
        sql.append("    LEFT JOIN ( ");
        sql.append("        SELECT ");
        sql.append("            l.database_id, COUNT(l.id) as 'error_count' ");
        sql.append("        FROM DataSyncLog as l ");
        sql.append("        WHERE l.iteration_number IN ( ");
        sql.append("            SELECT MAX(id) ");
        sql.append("            FROM DataSyncStats ");
        sql.append("            WHERE iteration_number IS NULL AND started BETWEEN ? AND DATEADD(DAY, 1, ?) AND completed IS NOT NULL ");
        sql.append("        ) AND l.type_id = 1 AND l.table_name IS NOT NULL ");
        sql.append("        GROUP BY l.database_id ");
        sql.append("    ) AS summary ON summary.database_id = db.id ");
        sql.append("    LEFT JOIN ( ");
        sql.append("        SELECT ");
        sql.append("            l.database_id, COUNT(1) as result ");
        sql.append("        FROM DataSyncLog as l ");
        sql.append("        WHERE l.iteration_number IN ( ");
        sql.append("            SELECT MAX(id) ");
        sql.append("            FROM DataSyncStats ");
        sql.append("            WHERE iteration_number IS NULL AND started BETWEEN ? AND DATEADD(DAY, 1, ?) AND completed IS NOT NULL ");
        sql.append("        ) AND l.type_id = 2 AND l.table_name IS NULL ");
        sql.append("        GROUP BY l.database_id ");
        sql.append("    ) AS status ON status.database_id = db.id ");
        sql.append("WHERE db.is_service = 0 ");
        sql.append("ORDER BY result ASC, summary.error_count DESC, db.name ASC ");

        return getJdbcTemplate().query(sql.toString(),  new Object[]{date, date, date, date}, new SyncReportRowMapper());
    }

    @Override
    public List<SyncReportEntry> findAll(int offset, int limit, ReportFilter filter) {
        Date date = filter.getCriteria(FilterBy.DATE_FROM, Date.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ( ");
        sql.append("    SELECT ");
        sql.append("        db.name as 'database_name', COALESCE(summary.error_count, 0) as 'error_count', ");
        sql.append("        CASE WHEN status.result is not null THEN 1 ELSE 0 END as 'result', db.last_success_sync_date, ");
        sql.append("        ROW_NUMBER() OVER (ORDER BY result ASC, summary.[error_count] DESC, db.name ASC) AS RowNum ");
        sql.append("    FROM SourceDatabase db ");
        sql.append("        LEFT JOIN ( ");
        sql.append("            SELECT ");
        sql.append("                l.database_id, COUNT(l.id) as 'error_count' ");
        sql.append("            FROM DataSyncLog as l ");
        sql.append("            WHERE l.iteration_number IN ( ");
        sql.append("                SELECT MAX(id) ");
        sql.append("                FROM DataSyncStats ");
        sql.append("                WHERE iteration_number IS NULL AND started BETWEEN ? AND DATEADD(DAY, 1, ?) AND completed IS NOT NULL ");
        sql.append("            ) AND l.type_id = 1 AND l.table_name IS NOT NULL ");
        sql.append("            GROUP BY l.database_id ");
        sql.append("        ) AS summary ON summary.database_id = db.id ");
        sql.append("        LEFT JOIN ( ");
        sql.append("            SELECT ");
        sql.append("                l.database_id, COUNT(1) as result ");
        sql.append("            FROM DataSyncLog as l ");
        sql.append("            WHERE l.iteration_number IN ( ");
        sql.append("                SELECT MAX(id) ");
        sql.append("                FROM DataSyncStats ");
        sql.append("                WHERE iteration_number IS NULL AND started BETWEEN ? AND DATEADD(DAY, 1, ?) AND completed IS NOT NULL ");
        sql.append("            ) AND l.type_id = 2 AND l.table_name IS NULL ");
        sql.append("            GROUP BY l.database_id ");
        sql.append("        ) AS status ON status.database_id = db.id ");
        sql.append("    WHERE db.is_service = 0 ");
        sql.append(") AS enumeratedTable WHERE (enumeratedTable.RowNum BETWEEN ? AND ?) ");
        sql.append("ORDER BY enumeratedTable.RowNum ASC ");

        return getJdbcTemplate().query(sql.toString(), new Object[]{date, date, date, date, offset + 1, offset + limit}, new SyncReportRowMapper());
    }

    @Override
    public int count(ReportFilter filter) {
        Date date = filter.getCriteria(FilterBy.DATE_FROM, Date.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) ");
        sql.append("    FROM SourceDatabase db ");
        sql.append("    WHERE db.is_service = 0 ");

        return getJdbcTemplate().queryForInt(sql.toString());
    }
}
