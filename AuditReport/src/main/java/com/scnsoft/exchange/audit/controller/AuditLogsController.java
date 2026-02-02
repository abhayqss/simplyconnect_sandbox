package com.scnsoft.exchange.audit.controller;


import com.scnsoft.exchange.audit.model.CompanyDto;
import com.scnsoft.exchange.audit.model.LogDto;
import com.scnsoft.exchange.audit.model.filters.AuditLogsReportFilter;
import com.scnsoft.exchange.audit.model.filters.ReportFilterDto;
import com.scnsoft.exchange.audit.model.validators.AuditLogsReportFilterValidator;
import com.scnsoft.exchange.audit.service.CompaniesService;
import com.scnsoft.exchange.audit.service.ReportService;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_SUPER_MANAGER')")
@RequestMapping(value = "/logs")
public class AuditLogsController extends AbstractReportController<LogDto, AuditLogsReportFilter> {
    private static final String VIEW_NAME = "logs";

    @Autowired
    private CompaniesService companiesService;

    @Autowired
    private AuditLogsReportFilterValidator validator;

    @Autowired
    @Qualifier(value = "auditLogsReportService")
    private ReportService<LogDto> reportService;

    @Override
    protected ReportFilterDto initializeReportFilter() {
        AuditLogsReportFilter filter = new AuditLogsReportFilter();

        filter.setFromAsMonthAgo();
        filter.setToAsToday();
        filter.setCompany(null);

        return filter;
    }

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        if(binder.getTarget() != null) {
            if(validator.supports(binder.getTarget().getClass())){
                binder.addValidators(validator);
            }
        }
    }

    @ModelAttribute("companies")
    public List<CompanyDto> getAllCompanies() {
        return companiesService.getCompanies();
    }

    @RequestMapping(value = "/exportToExcel/for-prev-month", method = RequestMethod.GET)
    public ModelAndView forPrevMonth(@ModelAttribute("logsFilter") AuditLogsReportFilter filter,
                                     RedirectAttributes redirectAttributes) {
        filter.setFromAsFirstDayOfPrevMonth();
        filter.setToAsLastDayOfPrevMonth();

        redirectAttributes.addFlashAttribute("logsFilter", filter);
        return new ModelAndView("redirect:/logs/exportToExcel");
    }


    @RequestMapping(value = "/exportToExcel/for-all-time", method = RequestMethod.GET)
    public ModelAndView forAllTime(@ModelAttribute("logsFilter") AuditLogsReportFilter filter,
                                   RedirectAttributes redirectAttributes) {
        filter.setFrom(null);
        filter.setToAsToday();

        redirectAttributes.addFlashAttribute("logsFilter", filter);
        return new ModelAndView("redirect:/logs/exportToExcel");
    }

    @Override
    protected String getViewName() {
        return VIEW_NAME;
    }

    @Override
    protected ReportService<LogDto> getReportService() {
        return reportService;
    }

    @Override
    protected ExcelView<LogDto> getReportExcelView() {
        return new ExcelView<LogDto>() {
            @Override
            public String getFileName() {
                return "exchange-audit-logs.xls";
            }

            @Override
            public String getSheetName() {
                return "Audit Logs";
            }

            @Override
            public String[] getColumnNames() {
                return new String[]{
                        "id", "time", "action", "employee_id", "employee_login", "employee_ip_address", "resident_id",
                        "resident_first_name", "resident_last_name", "document_id", "document_title", "company_name"};
            }

            @Override
            public void write(HSSFSheet sheet, int rowNumber, LogDto log) {
                if (log.getId() != null) {
                    getCell(sheet, rowNumber, 0).setCellValue(log.getId());
                    getCell(sheet, rowNumber, 0).setCellType(Cell.CELL_TYPE_NUMERIC);
                }

                setText(getCell(sheet, rowNumber, 1), log.getDate());
                setText(getCell(sheet, rowNumber, 2), log.getAction());

                if (log.getEmployeeId() != null) {
                    getCell(sheet, rowNumber, 3).setCellValue(log.getEmployeeId());
                    getCell(sheet, rowNumber, 3).setCellType(Cell.CELL_TYPE_NUMERIC);
                }
                getCell(sheet, rowNumber, 3).setCellType(Cell.CELL_TYPE_NUMERIC);

                setText(getCell(sheet, rowNumber, 4), log.getEmployeeLogin());
                setText(getCell(sheet, rowNumber, 5), log.getIpAddress());

                if (log.getResidentId() != null) {
                    getCell(sheet, rowNumber, 6).setCellValue(log.getResidentId());
                    getCell(sheet, rowNumber, 6).setCellType(Cell.CELL_TYPE_NUMERIC);
                }

                setText(getCell(sheet, rowNumber, 7), log.getResidentFirstName());

                setText(getCell(sheet, rowNumber, 8), log.getResidentLastName());

                if (log.getDocumentId() != null) {
                    getCell(sheet, rowNumber, 9).setCellValue(log.getDocumentId());
                    getCell(sheet, rowNumber, 9).setCellType(Cell.CELL_TYPE_NUMERIC);
                }

                setText(getCell(sheet, rowNumber, 10), log.getDocumentTitle());

                setText(getCell(sheet, rowNumber, 11), log.getDatabaseName());
            }
        };
    }
}
