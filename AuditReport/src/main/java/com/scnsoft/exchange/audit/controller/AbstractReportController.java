package com.scnsoft.exchange.audit.controller;


import com.scnsoft.exchange.audit.model.ReportDto;
import com.scnsoft.exchange.audit.model.filters.ReportFilterDto;
import com.scnsoft.exchange.audit.security.AuditUser;
import com.scnsoft.exchange.audit.security.SecurityUtils;
import com.scnsoft.exchange.audit.service.ReportService;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

public abstract class AbstractReportController<T extends ReportDto, F extends ReportFilterDto> {

    protected abstract String getViewName();

    protected abstract ReportFilterDto initializeReportFilter();

    protected abstract ReportService<T> getReportService();

    protected abstract ExcelView<T> getReportExcelView();


    @RequestMapping(method = RequestMethod.GET)
    public String index(RedirectAttributes redirectAttributes, Model model) {
        redirectAttributes.addFlashAttribute("reportFilter", initializeReportFilter());

        return String.format("redirect:/%s/1", getViewName());
    }

    @RequestMapping(value = "/{pageNumber}", method = RequestMethod.GET)
    public String index(@PathVariable Integer pageNumber,
                        @Valid @ModelAttribute("reportFilter") F filter, BindingResult errors,
                        Model model) {
        if(errors.hasErrors()) {
            model.addAttribute("reportUrl", getViewName());
            return getViewName();
        }

        if(!SecurityUtils.hasRole("ROLE_SUPER_MANAGER")) {
            AuditUser currentUser = SecurityUtils.getUserDetails();
            filter.setCompany(currentUser.getCompanyId());
        }

        filter.fixTimePart();

        Page<T> page = getReportService().generate(pageNumber, filter);

        int show = 7;

        int current = page.getNumber() + 1;
        int begin = Math.max(1, current - show / 2);
        int end = Math.min(current + show / 2, page.getTotalPages());

        if (current - begin <= show / 2) {
            end = Math.min(begin + show, page.getTotalPages());
        }

        if (end - current <= show / 2) {
            begin = Math.max(1, end - show);
        }

        model.addAttribute("reportFilter", filter);

        model.addAttribute("report", page);
        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current);
        model.addAttribute("reportUrl", getViewName());

        updateModel(filter, model);

        return getViewName();
    }

    @RequestMapping(value = "/exportToExcel", method = RequestMethod.GET)
    public ModelAndView exportToExcel(@Valid @ModelAttribute("reportFilter") final F filter,
                               BindingResult errors) {

        ModelAndView modelAndView = new ModelAndView();

        if(errors.hasErrors()) {
            modelAndView.setViewName(getViewName());
            return modelAndView;
        }

        View view = new AbstractExcelView() {
            @Override
            protected void buildExcelDocument(Map<String, Object> stringObjectMap, HSSFWorkbook workbook,
                                              HttpServletRequest httpServletRequest, HttpServletResponse response) throws Exception {

                response.addHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", getReportExcelView().getFileName()));

                HSSFSheet sheet = workbook.createSheet(getReportExcelView().getSheetName());

                String[] columnNames = getReportExcelView().getColumnNames();
                for(int i = 0; i < columnNames.length; i++) {
                    setText(getCell(sheet, 0, i), columnNames[i]);
                }

                List<T> report = getReportService().generate(filter);
                for (int i = 1; i < report.size() + 1; i++) {
                    getReportExcelView().write(sheet, i, report.get(i - 1));
                }

                for(int i = 0; i < columnNames.length; i++) {
                    sheet.autoSizeColumn(i);
                }
            }
        };

        modelAndView.setView(view);

        return modelAndView;
    }

    /**
     * Successors could override this method to update a model returned by method
     * @RequestMapping(value = "/{pageNumber}", method = RequestMethod.GET)
     */
    protected void updateModel(ReportFilterDto filter, Model model) {

    }
}
