package com.scnsoft.eldermark.dump.service.writer;

import com.scnsoft.eldermark.dump.model.AssessmentsGeneral;
import com.scnsoft.eldermark.dump.model.DumpType;
import com.scnsoft.eldermark.dump.model.SPGeneral;
import com.scnsoft.eldermark.dump.model.TotalForOrganizationDump;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@Component
public class TotalForOrganizationExcelDumpWriter extends ExcelDumpWriter implements DumpWriter<TotalForOrganizationDump> {

    @Override
    public DumpType getDumpType() {
        return DumpType.TOTAL_FOR_ORGANIZATION;
    }

    @Override
    public void writeDump(TotalForOrganizationDump dump) {
        writeToConsole(dump);
        Workbook workbook = generateWorkBook(dump);
        writeToFile(workbook, dump);
    }

    Workbook generateWorkBook(TotalForOrganizationDump dump) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addAssessmentsGeneral(workbook, styles, dump.getAssessmentsGeneral());
        addGad7PHQ9Scoring(workbook, styles, dump.getGad7PHQ9ScoringList());
        addComprehensiveDetails(workbook, styles, dump.getComprehensiveDetailList());
        addSpGeneral(workbook, styles, dump.getSpGeneral());
        addSpIndividuals(workbook, styles, dump.getSpIndividualList());
        addSpDetails(workbook, styles, dump.getSpDetailList());

        return workbook;
    }

    void addAssessmentsGeneral(Workbook wb, Map<String, CellStyle> styles, AssessmentsGeneral assessmentsGeneral) {
        var sheet = wb.createSheet("Assessments - General");

        var rowCount = 0;
        Row headerRow = sheet.createRow(rowCount++);
        var headerValues = Arrays.asList("Field", "Result");

        writeHeader(headerRow, styles, headerValues);

        writeGeneralRow(sheet, rowCount++, styles, "# of assessments completed (GAD-7)", assessmentsGeneral.getGad7Completed());
        writeGeneralRow(sheet, rowCount++, styles, "# of assessments completed (PHQ-9)", assessmentsGeneral.getPhq9Completed());
        writeGeneralRow(sheet, rowCount++, styles, "# of assessments completed (Comprehensive)", assessmentsGeneral.getComprehensiveCompleted());
        writeGeneralRow(sheet, rowCount++, styles, "# of assessments completed", assessmentsGeneral.getGad7Completed() + assessmentsGeneral.getPhq9Completed() + assessmentsGeneral.getComprehensiveCompleted());

        sheet.setColumnWidth(0, CHAR_LENGTH * 50);
        sheet.setColumnWidth(1, CHAR_LENGTH * 25);
    }

    void addSpGeneral(Workbook wb, Map<String, CellStyle> styles, SPGeneral spGeneral) {
        var sheet = wb.createSheet("SP - General");

        var rowCount = 0;
        Row headerRow = sheet.createRow(rowCount++);
        var headerValues = Arrays.asList("Field", "Result");

        writeHeader(headerRow, styles, headerValues);

        writeGeneralRow(sheet, rowCount++, styles, "# of patients", spGeneral.getNumberOfPatients());
        writeGeneralRow(sheet, rowCount++, styles, "# of service plans opened", spGeneral.getNumberOfSPopened());

        writeGeneralRowFormula(sheet, rowCount++, styles, "Average # of service plans per resident", "CEILING(((B5+B3)/B2), 0.01)");

        writeGeneralRow(sheet, rowCount++, styles, "# of service plans closed", spGeneral.getNumberOfSpClosed());
        writeGeneralRow(sheet, rowCount++, styles, "total # of needs", spGeneral.getTotalNumberOfNeeds());
        writeGeneralRow(sheet, rowCount++, styles, "total # of goals", spGeneral.getTotalNumberOfGoals());

        writeGeneralRowFormula(sheet, 7, styles, "average # of needs per resident", "CEILING((B6/B2), 0.01)");
        writeGeneralRowFormula(sheet, 8, styles, "average # of goals per resident", "CEILING((B7/B2), 0.01)");

        writeGeneralRow(sheet, 9, styles, "# of accomplished goals", spGeneral.getNumberOfAccomplishedGoals());

        writeGeneralRowFormula(sheet, 10, styles, "% of accomplished goals", "CEILING((B10/B7), 0.001)*100");


        sheet.setColumnWidth(0, CHAR_LENGTH * 50);
        sheet.setColumnWidth(1, CHAR_LENGTH * 25);
    }


}
