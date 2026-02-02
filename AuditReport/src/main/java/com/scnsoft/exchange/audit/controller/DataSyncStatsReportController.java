package com.scnsoft.exchange.audit.controller;


import com.scnsoft.exchange.audit.model.CompanyDto;
import com.scnsoft.exchange.audit.model.DataSyncStatsDto;
import com.scnsoft.exchange.audit.model.filters.ReportFilterDto;
import com.scnsoft.exchange.audit.model.filters.SyncStatsReportFilter;
import com.scnsoft.exchange.audit.model.validators.DataSyncStatsFilterValidator;
import com.scnsoft.exchange.audit.security.AuditUser;
import com.scnsoft.exchange.audit.security.SecurityUtils;
import com.scnsoft.exchange.audit.service.CompaniesService;
import com.scnsoft.exchange.audit.service.DataSyncReportService;
import com.scnsoft.exchange.audit.service.ReportService;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@PreAuthorize("hasRole('ROLE_SUPER_MANAGER')")
@RequestMapping(value = "/dataSyncStats")
public class DataSyncStatsReportController extends AbstractReportController<DataSyncStatsDto, SyncStatsReportFilter> {
    private static final String VIEW_NAME = "dataSyncStats";

    @Autowired
    private CompaniesService companiesService;

    @Autowired
    @Qualifier(value = "dataSyncStatsReportService")
    private DataSyncReportService reportService;

    @Autowired
    private DataSyncStatsFilterValidator validator;

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        if(binder.getTarget() != null) {
            if(validator.supports(binder.getTarget().getClass())){
                binder.addValidators(validator);
            }
        }
    }

    @Override
    protected ReportFilterDto initializeReportFilter() {
        SyncStatsReportFilter filter = new SyncStatsReportFilter();

        filter.setFrom(new Date());
        filter.setTo(new Date());
        AuditUser currentUser = SecurityUtils.getUserDetails();
        filter.setCompany(currentUser.getCompanyId());
        filter.setShowDetails(Boolean.FALSE);

        return filter;
    }

    @Override
    protected ExcelView<DataSyncStatsDto> getReportExcelView() {
        return new ExcelView<DataSyncStatsDto>() {
            @Override
            public String getFileName() {
                return "exchange-data-sync-stats-report.xls";
            }

            @Override
            public String getSheetName() {
                return "Data Sync Stats Report";
            }

            @Override
            public String[] getColumnNames() {
                return new String[] {"iteration_number", "database_name", "syncservice_name", "sync_started", "sync_completed", "duration"};
            }

            @Override
            public void write(HSSFSheet sheet, int i, DataSyncStatsDto dto) {
                getCell(sheet, i, 0).setCellValue(dto.getIterationNumber());
                getCell(sheet, i, 0).setCellType(Cell.CELL_TYPE_NUMERIC);

                setText(getCell(sheet, i, 1), dto.getDatabaseName());

                setText(getCell(sheet, i, 2), dto.getSyncServiceName());

                setText(getCell(sheet, i, 3), dto.getStarted());
                setText(getCell(sheet, i, 4), dto.getCompleted());

                setText(getCell(sheet, i, 5), dto.getDuration());
            }
        };
    }

    @RequestMapping(value = "/minDate", method = RequestMethod.GET)
    @ResponseBody
    public String minDate() {
        Date reportMinDate = reportService.getReportMinValidDate();
        if (reportMinDate != null)
            return reportMinDate.toString();
        return null;
    }

    @Override
    protected String getViewName() {
        return VIEW_NAME;
    }

    @Override
    protected ReportService<DataSyncStatsDto> getReportService() {
        return reportService;
    }

    @ModelAttribute("companies")
    public List<CompanyDto> getAllCompanies() {
        return companiesService.getCompanies();
    }
}
