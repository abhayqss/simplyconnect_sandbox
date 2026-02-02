package com.scnsoft.exchange.audit.controller;


import com.scnsoft.exchange.audit.model.CompanyDto;
import com.scnsoft.exchange.audit.model.FacilityDto;
import com.scnsoft.exchange.audit.model.StateDto;
import com.scnsoft.exchange.audit.model.filters.FacilitiesReportFilter;
import com.scnsoft.exchange.audit.model.filters.ReportFilterDto;
import com.scnsoft.exchange.audit.service.CompaniesService;
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
@RequestMapping(value = "/" + FacilityController.VIEW_NAME)
public class FacilityController extends AbstractReportController<FacilityDto, FacilitiesReportFilter> {
    public static final String VIEW_NAME = "facilities";

    @Autowired
    @Qualifier(value = "facilitiesReportService")
    private ReportService<FacilityDto> reportService;

    @Autowired
    private CompaniesService companiesService;

    @Autowired
    private StatesService statesService;

    @Override
    protected ReportFilterDto initializeReportFilter() {
        FacilitiesReportFilter filter = new FacilitiesReportFilter();

        filter.setCompany(null);
        filter.setState("all");

        return filter;
    }

    @ModelAttribute("companies")
    public List<CompanyDto> getAllCompanies() {
        return companiesService.getCompanies();
    }

    @ModelAttribute("states")
    public List<StateDto> getAllStates() {
        return statesService.getStates();
    }

    @Override
    protected String getViewName() {
        return VIEW_NAME;
    }

    @Override
    protected ReportService<FacilityDto> getReportService() {
        return reportService;
    }

    @Override
    protected ExcelView<FacilityDto> getReportExcelView() {
        return new ExcelView<FacilityDto>() {
            @Override
            public String getFileName() {
                return "exchange-trading-organizations.xls";
            }

            @Override
            public String getSheetName() {
                return "Trading Organizations";
            }

            @Override
            public String[] getColumnNames() {
                return new String[] {
                        "company_name", "facility_name", "facility_state", "facility_testing_training",
                        "facility_sales_region", "resident_count", "last_success_sync_date" };
            }

            @Override
            public void write(HSSFSheet sheet, int i, FacilityDto facility) {
                setText(getCell(sheet, i, 0), facility.getCompanyName());
                setText(getCell(sheet, i, 1), facility.getName());
                setText(getCell(sheet, i, 2), facility.getState());
                setText(getCell(sheet, i, 3), facility.getTestingTraining());
                setText(getCell(sheet, i, 4), facility.getSalesRegion());

                if(facility.getResidentNumber() != null) {
                    getCell(sheet, i, 5).setCellValue(facility.getResidentNumber());
                    getCell(sheet, i, 5).setCellType(Cell.CELL_TYPE_NUMERIC);
                }

                setText(getCell(sheet, i, 6), facility.getLastSyncDate());
            }
        };
    }
}
