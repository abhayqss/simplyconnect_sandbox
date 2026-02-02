package com.scnsoft.exchange.audit.controller;


import com.scnsoft.exchange.audit.model.SyncReportEntry;
import com.scnsoft.exchange.audit.model.filters.ReportFilterDto;
import com.scnsoft.exchange.audit.model.filters.SyncReportFilter;
import com.scnsoft.exchange.audit.model.validators.SyncReportFilterValidator;
import com.scnsoft.exchange.audit.service.ReportService;
import com.scnsoft.exchange.audit.service.DataSyncReportService;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@PreAuthorize("hasRole('ROLE_SUPER_MANAGER')")
@RequestMapping(value = "/dataSyncReport")
public class DataSyncReportController extends AbstractReportController<SyncReportEntry, SyncReportFilter> {
    private static final String VIEW_NAME = "dataSyncReport";

    @Autowired
    @Qualifier(value = "dataSyncReportService")
    private DataSyncReportService reportService;

    @Autowired
    private SyncReportFilterValidator validator;

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

        return filter;
    }

    @Override
    protected void updateModel(ReportFilterDto filter, Model model) {
        model.addAttribute("range", reportService.getRange(filter));
    }

    @Override
    protected ExcelView<SyncReportEntry> getReportExcelView() {
        return new ExcelView<SyncReportEntry>() {
            @Override
            public String getFileName() {
                return "exchange-data-sync-report.xls";
            }

            @Override
            public String getSheetName() {
                return "Data Sync Report";
            }

            @Override
            public String[] getColumnNames() {
                return new String[] {"database_name", "last_success_sync_date", "error_count"};
            }

            @Override
            public void write(HSSFSheet sheet, int i, SyncReportEntry dto) {
                setText(getCell(sheet, i, 0), dto.getDatabaseName());

                setText(getCell(sheet, i, 1), dto.getLastSyncDate());

                getCell(sheet, i, 2).setCellValue(dto.getErrorCount());
                getCell(sheet, i, 2).setCellType(Cell.CELL_TYPE_NUMERIC);
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
    protected ReportService<SyncReportEntry> getReportService() {
        return reportService;
    }
}
