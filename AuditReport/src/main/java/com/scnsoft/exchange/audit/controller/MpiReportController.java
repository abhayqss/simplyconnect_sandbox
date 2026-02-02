package com.scnsoft.exchange.audit.controller;


import com.scnsoft.exchange.audit.model.MpiReportEntry;
import com.scnsoft.exchange.audit.model.StateDto;
import com.scnsoft.exchange.audit.model.filters.MpiReportFilter;
import com.scnsoft.exchange.audit.model.filters.ReportFilterDto;
import com.scnsoft.exchange.audit.service.ReportService;
import com.scnsoft.exchange.audit.service.StatesService;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@PreAuthorize("hasRole('ROLE_SUPER_MANAGER')")
@RequestMapping(value = "/mpiReport")
public class MpiReportController extends AbstractReportController<MpiReportEntry, MpiReportFilter> {
    private static final String VIEW_NAME = "mpiReport";

    @Autowired
    private StatesService statesService;

    @Autowired
    @Qualifier(value = "mpiReportService")
    private ReportService<MpiReportEntry> reportService;


    @ModelAttribute("states")
    public List<StateDto> getAllStates() {
        return statesService.getStates();
    }

    @Override
    protected ReportFilterDto initializeReportFilter() {
        MpiReportFilter filter = new MpiReportFilter();

        filter.setState(null);

        return filter;
    }

    @Override
    protected String getViewName() {
        return VIEW_NAME;
    }

    @Override
    protected ReportService<MpiReportEntry> getReportService() {
        return reportService;
    }

    @Override
    protected ExcelView<MpiReportEntry> getReportExcelView() {
        return new ExcelView<MpiReportEntry>() {
            @Override
            public String getFileName() {
                return "exchange-MPI.xls";
            }

            @Override
            public String getSheetName() {
                return "MPI Report";
            }

            @Override
            public String[] getColumnNames() {
                return new String[] {"state", "resident_count", "RLS_queries_count", "clinical_transactions_count"};
            }

            @Override
            public void write(HSSFSheet sheet, int i, MpiReportEntry dto) {
                setText(getCell(sheet, i, 0), dto.getStateName());

                if(dto.getResidentNumber() != null) {
                    getCell(sheet, i, 1).setCellValue(dto.getResidentNumber());
                    getCell(sheet, i, 1).setCellType(Cell.CELL_TYPE_NUMERIC);
                }

                if(dto.getPatientDiscoveryNumber() != null) {
                    getCell(sheet, i, 2).setCellValue(dto.getPatientDiscoveryNumber());
                    getCell(sheet, i, 2).setCellType(Cell.CELL_TYPE_NUMERIC);
                }
                if(dto.getGeneratedCCDNumber() != null) {
                    getCell(sheet, i, 3).setCellValue(dto.getGeneratedCCDNumber());
                    getCell(sheet, i, 3).setCellType(Cell.CELL_TYPE_NUMERIC);
                }
            }
        };
    }
}
