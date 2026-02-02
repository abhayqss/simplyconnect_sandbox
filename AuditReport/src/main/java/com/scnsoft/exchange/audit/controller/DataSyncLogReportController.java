package com.scnsoft.exchange.audit.controller;


import com.scnsoft.exchange.audit.model.CompanyDto;
import com.scnsoft.exchange.audit.model.DataSyncLogDto;
import com.scnsoft.exchange.audit.model.filters.ReportFilterDto;
import com.scnsoft.exchange.audit.model.filters.SyncReportFilter;
import com.scnsoft.exchange.audit.model.validators.DataSyncLogReportFilterValidator;
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
@RequestMapping(value = "/dataSyncLogReport")
public class DataSyncLogReportController extends AbstractReportController<DataSyncLogDto, SyncReportFilter> {
    private static final String VIEW_NAME = "dataSyncLogReport";

    @Autowired
    @Qualifier(value = "dataSyncLogReportService")
    private DataSyncReportService reportService;

    @Autowired
    private CompaniesService companiesService;

    @Autowired
    private DataSyncLogReportFilterValidator validator;

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
        SyncReportFilter filter = new SyncReportFilter();

        filter.setFrom(new Date());
        filter.setTo(new Date());
        filter.setCompany(null);

        return filter;
    }

    @ModelAttribute("companies")
    public List<CompanyDto> getAllCompanies() {
        return companiesService.getCompanies();
    }

    @Override
    protected void updateModel(ReportFilterDto filter, Model model) {
        model.addAttribute("range", reportService.getRange(filter));
    }

    @Override
    protected ExcelView<DataSyncLogDto> getReportExcelView() {
        return new ExcelView<DataSyncLogDto>() {
            @Override
            public String getFileName() {
                return "exchange-data-sync-log-report.xls";
            }

            @Override
            public String getSheetName() {
                return "Data Sync Log Report";
            }

            @Override
            public String[] getColumnNames() {
                return new String[] {
                        "log_id", "log_date", "log_type", "description", "table_name", "stack_trace",
                        "iteration_number", "database_name"};
            }

            @Override
            public void write(HSSFSheet sheet, int i, DataSyncLogDto dto) {
                getCell(sheet, i, 0).setCellValue(dto.getId());
                getCell(sheet, i, 0).setCellType(Cell.CELL_TYPE_NUMERIC);

                setText(getCell(sheet, i, 1), dto.getDate());

                setText(getCell(sheet, i, 2), dto.getType());
                setText(getCell(sheet, i, 3), dto.getDescription());
                setText(getCell(sheet, i, 4), dto.getTableName());
                setText(getCell(sheet, i, 5), dto.getStackTrace());

                getCell(sheet, i, 6).setCellValue(dto.getIterationNumber());
                getCell(sheet, i, 6).setCellType(Cell.CELL_TYPE_NUMERIC);

                setText(getCell(sheet, i, 8), dto.getDatabaseName());
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
    protected ReportService<DataSyncLogDto> getReportService() {
        return reportService;
    }
}
