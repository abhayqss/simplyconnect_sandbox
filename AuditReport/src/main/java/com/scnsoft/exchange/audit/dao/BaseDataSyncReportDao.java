package com.scnsoft.exchange.audit.dao;

import com.scnsoft.exchange.audit.dao.mappers.SyncReportRangeRowMapper;
import com.scnsoft.exchange.audit.model.SyncRange;
import com.scnsoft.exchange.audit.model.filters.FilterBy;
import com.scnsoft.exchange.audit.model.filters.ReportFilter;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Date;

public abstract class BaseDataSyncReportDao extends BaseDao {

    public SyncRange getReportRange(ReportFilter filter) {
        Date date = filter.getCriteria(FilterBy.DATE_FROM, Date.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOP 1 started, completed ");
        sql.append("    FROM DataSyncStats ");
        sql.append("WHERE iteration_number IS NULL AND started BETWEEN ? AND DATEADD(DAY, 1, ?) AND completed IS NOT NULL ");
        sql.append("ORDER BY id DESC");

        try {
            return getJdbcTemplate().queryForObject(sql.toString(), new Object[]{date, date}, new SyncReportRangeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Date getMinDate() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT MIN(started) ");
        sql.append("    FROM DataSyncStats ");
        sql.append("    WHERE iteration_number IS NULL AND completed IS NOT NULL ");

        return getJdbcTemplate().queryForObject(sql.toString(), Date.class);
    }
}
