package com.scnsoft.eldermark.service.report.converter;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.Report;
import com.scnsoft.eldermark.service.report.workbook.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Component
public class ReportToWorkbookConverter implements Converter<Report, Workbook> {

    private Map<ReportType, WorkbookGenerator> generatorsMap;

    @Autowired
    ReportToWorkbookConverter(List<WorkbookGenerator> generators) {
        this.generatorsMap = generators.stream().collect(toMap(WorkbookGenerator::generatedReportType, Function.identity()));
    }

    @Override
    public Workbook convert(Report report) {
        Workbook workbook = generatorsMap.get(report.getReportType()).generateWorkbook(report);
        return workbook;
    }

}
