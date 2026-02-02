package com.scnsoft.exchange.audit.dao;

import com.scnsoft.exchange.audit.dao.mappers.DataSyncLogReportRowMapper;
import com.scnsoft.exchange.audit.model.DataSyncLogDto;
import com.scnsoft.exchange.audit.model.filters.FilterBy;
import com.scnsoft.exchange.audit.model.filters.ReportFilter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class DataSyncLogReportDaoImpl extends BaseDataSyncReportDao implements DataSyncReportDao<DataSyncLogDto> {

    @Override
    public List<DataSyncLogDto> findAll(ReportFilter filter) {
        Date dateFrom = filter.getCriteria(FilterBy.DATE_FROM, Date.class);
        Date dateTo = filter.getCriteria(FilterBy.DATE_TO, Date.class);
        Long companyId = filter.getCriteria(FilterBy.COMPANY_ID, Long.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("	l.id, l.date, type.name as 'type_name', l.description, l.table_name, l.stack_trace, d.name as 'database_name', l.iteration_number ");
        sql.append("FROM DataSyncLog as l ");
        sql.append("    LEFT JOIN SourceDatabase d on d.id = l.database_id ");
        sql.append("    LEFT JOIN DataSyncLogType type on type.id = l.type_id ");
        sql.append("        WHERE date BETWEEN ? AND ? ");

        List<Object> params = new ArrayList<Object>();
        params.add(dateFrom);
        params.add(dateTo);

        if(companyId != null) {
            sql.append(" AND l.database_id = ? ");
            params.add(companyId);
        }

        sql.append("ORDER BY l.date DESC");

        return getJdbcTemplate().query(sql.toString(), params.toArray(), new DataSyncLogReportRowMapper());
    }

    @Override
    public List<DataSyncLogDto> findAll(int offset, int limit, ReportFilter filter) {
        Date dateFrom = filter.getCriteria(FilterBy.DATE_FROM, Date.class);
        Date dateTo = filter.getCriteria(FilterBy.DATE_TO, Date.class);
        Long companyId = filter.getCriteria(FilterBy.COMPANY_ID, Long.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ( ");
        sql.append("    SELECT ");
        sql.append("	    l.id, l.date, type.name as 'type_name', l.description, l.table_name, l.stack_trace, d.name as 'database_name', l.iteration_number, ");
        sql.append("        ROW_NUMBER() OVER (ORDER BY l.date DESC) AS RowNum ");
        sql.append("    FROM DataSyncLog as l ");
        sql.append("        LEFT JOIN SourceDatabase d on d.id = l.database_id ");
        sql.append("        LEFT JOIN DataSyncLogType type on type.id = l.type_id ");
        sql.append("        WHERE date BETWEEN ? AND ? ");

        List<Object> params = new ArrayList<Object>();
        params.add(dateFrom);
        params.add(dateTo);

        if(companyId != null) {
            sql.append(" AND l.database_id = ? ");
            params.add(companyId);
        }

        sql.append(") AS enumeratedTable WHERE (enumeratedTable.RowNum BETWEEN ? AND ?) ");
        sql.append("ORDER BY enumeratedTable.RowNum ASC ");

        params.add(offset + 1);
        params.add(offset + limit);

        return getJdbcTemplate().query(sql.toString(), params.toArray(), new DataSyncLogReportRowMapper());
    }

    @Override
    public int count(ReportFilter filter) {
        Date dateFrom = filter.getCriteria(FilterBy.DATE_FROM, Date.class);
        Date dateTo = filter.getCriteria(FilterBy.DATE_TO, Date.class);
        Long companyId = filter.getCriteria(FilterBy.COMPANY_ID, Long.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT (*) ");
        sql.append("FROM DataSyncLog as l ");
        sql.append("        WHERE date BETWEEN ? AND ? ");

        List<Object> params = new ArrayList<Object>();
        params.add(dateFrom);
        params.add(dateTo);

        if(companyId != null) {
            sql.append(" AND l.database_id = ?");
            params.add(companyId);
        }

        return getJdbcTemplate().queryForInt(sql.toString(), params.toArray());
    }
}
