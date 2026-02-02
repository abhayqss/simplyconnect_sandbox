package com.scnsoft.exchange.audit.dao;

import com.scnsoft.exchange.audit.dao.mappers.FacilitiesRowMapper;
import com.scnsoft.exchange.audit.model.FacilityDto;
import com.scnsoft.exchange.audit.model.filters.FilterBy;
import com.scnsoft.exchange.audit.model.filters.ReportFilter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class FacilitiesReportDaoImpl extends BaseDao implements ReportDao<FacilityDto> {

    @Override
    public List<FacilityDto> findAll(ReportFilter filter) {
        String state = filter.getCriteria(FilterBy.STATE, String.class);
        Long companyId = filter.getCriteria(FilterBy.COMPANY_ID, Long.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT d.[id] AS 'company_id', d.[name] AS 'company_name', ");
        sql.append("	[Organization].[name] AS 'facility_name', [OrganizationAddress].[state] AS 'facility_state', ");
        sql.append("	[Organization].[testing_training] AS 'facility_testing_training', [Organization].[sales_region] AS 'facility_sales_region', ");
        sql.append("	COALESCE(residentCount.resident_count, 0) AS 'resident_number', d.last_success_sync_date ");
        sql.append("FROM [SourceDatabase] d ");
        sql.append("	LEFT JOIN [Organization] ON d.[id] = [Organization].[database_id] ");
        sql.append("	    LEFT JOIN [OrganizationAddress] ON [OrganizationAddress].[org_id] = [Organization].[id] ");
        sql.append("	LEFT JOIN ( ");
        sql.append("		SELECT [facility_id], COUNT([id]) AS 'resident_count'");
        sql.append("		FROM [Resident] ");
        sql.append("		GROUP BY [Resident].[facility_id] ");
        sql.append("	) AS residentCount ON residentCount.[facility_id] = [Organization].[id] ");
        sql.append("WHERE ");
        sql.append("	([Organization].[legacy_table] = 'Company' OR [Organization].[legacy_table] IS NULL) ");
        sql.append("	AND ([Organization].[testing_training] = 0 OR [Organization].[testing_training] IS NULL) ");

        List<Object> params = new ArrayList<Object>();

        if(state != null && !"all".equals(state)) {
            if ("null".equals(state)) {
                sql.append("AND [OrganizationAddress].[state] IS NULL ");
            } else {
                sql.append("AND [OrganizationAddress].[state] = ? ");
                params.add(state);
            }
        }

        if(companyId != null) {
            sql.append("AND d.[id] = ? ");
            params.add(companyId);
        }

        sql.append("ORDER BY d.[name] ASC ");

        return getJdbcTemplate().query(sql.toString(), params.toArray(), new FacilitiesRowMapper());
    }

    @Override
    public List<FacilityDto> findAll(int offset, int limit, ReportFilter filter) {
        String state = filter.getCriteria(FilterBy.STATE, String.class);
        Long companyId = filter.getCriteria(FilterBy.COMPANY_ID, Long.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT company_id, company_name, ");
        sql.append("       facility_name, facility_state, facility_testing_training, facility_sales_region, ");
        sql.append("       resident_number, last_success_sync_date FROM (");
        sql.append("    SELECT d.[id] AS 'company_id', d.[name] AS 'company_name', ");
        sql.append("	    [Organization].[name] AS 'facility_name', [OrganizationAddress].[state] AS 'facility_state', ");
        sql.append("	    [Organization].[testing_training] AS 'facility_testing_training', [Organization].[sales_region] AS 'facility_sales_region', ");
        sql.append("	    COALESCE(residentCount.resident_count, 0) AS 'resident_number', d.last_success_sync_date, ");
        sql.append("        ROW_NUMBER() OVER (ORDER BY d.[name] ASC) AS RowNum ");
        sql.append("    FROM [SourceDatabase] d ");
        sql.append("	    LEFT JOIN [Organization] ON d.[id] = [Organization].[database_id] ");
        sql.append("	        LEFT JOIN [OrganizationAddress] ON [OrganizationAddress].[org_id] = [Organization].[id] ");
        sql.append("	    LEFT JOIN ( ");
        sql.append("		    SELECT [facility_id], COUNT([id]) AS 'resident_count'");
        sql.append("		    FROM [Resident] ");
        sql.append("		    GROUP BY [Resident].[facility_id] ");
        sql.append("	    ) AS residentCount ON residentCount.[facility_id] = [Organization].[id] ");
        sql.append("    WHERE ");
        sql.append("	    ([Organization].[legacy_table] = 'Company' OR [Organization].[legacy_table] IS NULL) ");
        sql.append("	    AND ([Organization].[testing_training] = 0 OR [Organization].[testing_training] IS NULL) ");

        List<Object> params = new ArrayList<Object>();

        if(state != null && !"all".equals(state)) {
            if ("null".equals(state)) {
                sql.append("AND [OrganizationAddress].[state] IS NULL ");
            } else {
                sql.append("AND [OrganizationAddress].[state] = ? ");
                params.add(state);
            }
        }

        if(companyId != null) {
            sql.append("AND d.[id] = ? ");
            params.add(companyId);
        }

        sql.append(") AS enumeratedTable ");
        sql.append("WHERE enumeratedTable.RowNum BETWEEN ? AND ? ");

        params.add(offset + 1);
        params.add(offset + limit);

        return getJdbcTemplate().query(sql.toString(), params.toArray(), new FacilitiesRowMapper());
    }

    @Override
    public int count(ReportFilter filter) {
        String state = filter.getCriteria(FilterBy.STATE, String.class);
        Long companyId = filter.getCriteria(FilterBy.COMPANY_ID, Long.class);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) ");
        sql.append("FROM [SourceDatabase] d");
        sql.append("	LEFT JOIN [Organization] ON d.[id] = [Organization].[database_id] ");
        sql.append("	    LEFT JOIN [OrganizationAddress] ON [OrganizationAddress].[org_id] = [Organization].[id] ");
        sql.append("WHERE ");
        sql.append("   ([Organization].[legacy_table] = 'Company' OR [Organization].[legacy_table] IS NULL) ");
        sql.append("   AND ([Organization].[testing_training] = 0 OR [Organization].[testing_training] IS NULL) ");

        List<Object> params = new ArrayList<Object>();

        if(state != null && !"all".equals(state)) {
            if ("null".equals(state)) {
                sql.append("AND [OrganizationAddress].[state] IS NULL ");
            } else {
                sql.append("AND [OrganizationAddress].[state] = ? ");
                params.add(state);
            }
        }

        if(companyId != null) {
            sql.append("AND d.[id] = ? ");
            params.add(companyId);
        }

        return getJdbcTemplate().queryForInt(sql.toString(), params.toArray());
    }
}
