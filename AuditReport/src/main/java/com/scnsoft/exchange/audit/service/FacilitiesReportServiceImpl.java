package com.scnsoft.exchange.audit.service;

import com.scnsoft.exchange.audit.dao.FacilitiesReportDaoImpl;
import com.scnsoft.exchange.audit.model.FacilityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "facilitiesReportService")
public class FacilitiesReportServiceImpl extends BaseReportService<FacilityDto> implements ReportService<FacilityDto> {

    @Autowired
    public FacilitiesReportServiceImpl(FacilitiesReportDaoImpl facilitiesDao) {
        super(facilitiesDao);
    }
}
