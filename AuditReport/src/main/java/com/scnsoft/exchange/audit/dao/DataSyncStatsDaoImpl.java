package com.scnsoft.exchange.audit.dao;

import com.scnsoft.exchange.audit.dao.mappers.DataSyncStatsRowMapper;
import com.scnsoft.exchange.audit.model.DataSyncStatsDto;
import com.scnsoft.exchange.audit.model.filters.FilterBy;
import com.scnsoft.exchange.audit.model.filters.ReportFilter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class DataSyncStatsDaoImpl extends BaseDataSyncReportDao implements DataSyncReportDao<DataSyncStatsDto> {

    @Override
    public List<DataSyncStatsDto> findAll(ReportFilter filter) {
        Date from = filter.getCriteria(FilterBy.DATE_FROM, Date.class);
        Date to = filter.getCriteria(FilterBy.DATE_TO, Date.class);
        Long companyId = filter.getCriteria(FilterBy.COMPANY_ID, Long.class);
        Boolean showTables = filter.getCriteria(FilterBy.SHOW_DETAILS, Boolean.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("	CASE WHEN s.iteration_number IS NOT NULL THEN s.iteration_number ELSE s.id END as 'iteration_number', ");
        sql.append("    s.started, d.name as 'database_name', s.completed, s.sync_service_name, ");
        sql.append("    CASE WHEN DATEDIFF(S, started, completed) / 3600 <> 0 THEN CAST(DATEDIFF(S, started, completed) / 3600 AS VARCHAR) + 'h ' ELSE '' END ");
        sql.append("    + CASE WHEN DATEDIFF(S, started, completed) / 60 % 60 <> 0 THEN CAST(DATEDIFF(S, started, completed) / 60 % 60 AS VARCHAR) + 'm ' ELSE '' END ");
        sql.append("    + CASE WHEN DATEDIFF(S, started, completed) % 60 <> 0 THEN CAST(DATEDIFF(S, started, completed) % 60 AS VARCHAR) + 's ' ELSE '' END as 'duration'");
        sql.append("FROM DataSyncStats as s ");
        sql.append("    LEFT JOIN SourceDatabase d on d.id = s.database_id ");
        sql.append("WHERE started BETWEEN ? AND DATEADD(DAY, 1, ?) ");

        List<Object> params = new ArrayList<Object>();
        params.add(from);
        params.add(to);

        if(companyId != null) {
            sql.append(" AND s.database_id = ? ");
            params.add(companyId);
        }

        if (Boolean.FALSE.equals(showTables)) {
            sql.append(" AND s.sync_service_name IS NULL ");
        }

        sql.append("ORDER BY s.started DESC");

        return getJdbcTemplate().query(sql.toString(), params.toArray(), new DataSyncStatsRowMapper());
    }

    @Override
    public List<DataSyncStatsDto> findAll(int offset, int limit, ReportFilter filter) {
        Date from = filter.getCriteria(FilterBy.DATE_FROM, Date.class);
        Date to = filter.getCriteria(FilterBy.DATE_TO, Date.class);
        Long companyId = filter.getCriteria(FilterBy.COMPANY_ID, Long.class);
        Boolean showTables = filter.getCriteria(FilterBy.SHOW_DETAILS, Boolean.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ( ");
        sql.append("SELECT ");
        sql.append("	CASE WHEN s.iteration_number IS NOT NULL THEN s.iteration_number ELSE s.id END as 'iteration_number', ");
        sql.append("    s.started, d.name as 'database_name', s.completed, s.sync_service_name, ");
        sql.append("    CASE WHEN DATEDIFF(S, started, completed) / 3600 <> 0 THEN CAST(DATEDIFF(S, started, completed) / 3600 AS VARCHAR) + 'h ' ELSE '' END ");
        sql.append("    + CASE WHEN DATEDIFF(S, started, completed) / 60 % 60 <> 0 THEN CAST(DATEDIFF(S, started, completed) / 60 % 60 AS VARCHAR) + 'm ' ELSE '' END ");
        sql.append("    + CASE WHEN DATEDIFF(S, started, completed) % 60 <> 0 THEN CAST(DATEDIFF(S, started, completed) % 60 AS VARCHAR) + 's ' ELSE '' END as 'duration', ");
        sql.append("	ROW_NUMBER() OVER (ORDER BY s.started DESC) AS RowNum ");
        sql.append("FROM DataSyncStats as s ");
        sql.append("    LEFT JOIN SourceDatabase d on d.id = s.database_id ");
        sql.append("WHERE started BETWEEN ? AND DATEADD(DAY, 1, ?) ");

        List<Object> params = new ArrayList<Object>();
        params.add(from);
        params.add(to);

        if(companyId != null) {
            sql.append(" AND s.database_id = ? ");
            params.add(companyId);
        }

        if (Boolean.FALSE.equals(showTables)) {
            sql.append(" AND s.sync_service_name IS NULL ");
        }

        sql.append(") AS enumeratedTable WHERE (enumeratedTable.RowNum BETWEEN ? AND DATEADD(DAY, 1, ?)) ");
        sql.append("ORDER BY enumeratedTable.RowNum ASC ");

        params.add(offset + 1);
        params.add(offset + limit);

        return getJdbcTemplate().query(sql.toString(), params.toArray(), new DataSyncStatsRowMapper());
    }

    @Override
    public int count(ReportFilter filter) {
        Date from = filter.getCriteria(FilterBy.DATE_FROM, Date.class);
        Date to = filter.getCriteria(FilterBy.DATE_TO, Date.class);
        Long companyId = filter.getCriteria(FilterBy.COMPANY_ID, Long.class);
        Boolean showTables = filter.getCriteria(FilterBy.SHOW_DETAILS, Boolean.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) ");
        sql.append("FROM DataSyncStats as s ");
        sql.append("    LEFT JOIN SourceDatabase d on d.id = s.database_id ");
        sql.append("WHERE started BETWEEN ? AND ? ");

        List<Object> params = new ArrayList<Object>();
        params.add(from);
        params.add(to);

        if(companyId != null) {
            sql.append(" AND s.database_id = ?");
            params.add(companyId);
        }

        if (Boolean.FALSE.equals(showTables)) {
            sql.append(" AND s.sync_service_name IS NULL ");
        }

        return getJdbcTemplate().queryForInt(sql.toString(), params.toArray());
    }
}
