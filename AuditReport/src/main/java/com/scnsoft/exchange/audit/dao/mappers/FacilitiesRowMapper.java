package com.scnsoft.exchange.audit.dao.mappers;

import com.scnsoft.exchange.audit.model.FacilityDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FacilitiesRowMapper implements RowMapper<FacilityDto> {

    @Override
    public FacilityDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        FacilityDto facility = new FacilityDto();

        facility.setState(rs.getString("facility_state"));
        facility.setCompanyName(rs.getString("company_name"));
        facility.setName(rs.getString("facility_name"));
        facility.setTestingTraining(rs.getString("facility_testing_training"));
        facility.setSalesRegion(rs.getString("facility_sales_region"));
        facility.setLastSyncDate(rs.getTimestamp("last_success_sync_date"));

        Long tmp = rs.getLong("company_id");
        if(!rs.wasNull()) facility.setCompanyId(tmp);
        tmp = rs.getLong("resident_number");
        if(!rs.wasNull()) facility.setResidentNumber(tmp);

        return facility;
    }
}
