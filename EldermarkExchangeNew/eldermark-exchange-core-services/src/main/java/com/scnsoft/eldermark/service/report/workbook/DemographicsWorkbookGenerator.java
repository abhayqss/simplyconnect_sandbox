package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.beans.reports.enums.ReportType.DEMOGRAPHICS;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;
import static java.util.Arrays.asList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Service
public class DemographicsWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<DemographicsReport> {

    private static final Logger logger = LoggerFactory.getLogger(DemographicsWorkbookGenerator.class);

    @Override
    public Workbook generateWorkbook(DemographicsReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addAssessmentsOverall(workbook, styles, report);
        addGad7PHQ9Scoring(workbook, styles, report.getGad7PHQ9ScoringList());
        addComprehensiveDetails(workbook, styles, report.getComprehensiveDetailList());
        addSpGeneral(workbook, styles, report);
        addSpIndividuals(workbook, styles, report.getSpIndividuals());
        addSpDetails(workbook, styles, report.getSpDetailList());
        return workbook;
    }

    @Override
    public ReportType generatedReportType() {
        return DEMOGRAPHICS;
    }

    private void addAssessmentsOverall(Workbook wb, Map<String, CellStyle> styles, DemographicsReport report) {
        String sheetName = "Assessments - overall";
        var headerValues = asList("Community name", "# of assessments completed (GAD-7)", "# of assessments completed (PHQ-9)", "# of assessments completed (Comprehensive)","# of assessments completed (Nor Cal Comprehensive)", "# of assessments completed");
        Sheet sheet = createSheetWithHeader(wb, styles, sheetName, headerValues, 0);

        var rowCount = 1;
        List<AssessmentsGeneral> assessmentList = report.getAssessmentsGeneralList();
        for (var assessment : assessmentList) {
            var row = sheet.createRow(rowCount++);

            var colCount = 0;

            writeToCell(styles, row.createCell(colCount++), assessment.getCommunityName());
            writeToCell(styles, row.createCell(colCount++), assessment.getGad7Completed());
            writeToCell(styles, row.createCell(colCount++), assessment.getPhq9Completed());
            writeToCell(styles, row.createCell(colCount++), assessment.getComprehensiveCompleted());
            writeToCell(styles, row.createCell(colCount++), assessment.getNorCalComprehensiveCompleted());
            writeToCell(styles, row.createCell(colCount++), assessment.getGad7Completed() + assessment.getPhq9Completed() + assessment.getComprehensiveCompleted() + assessment.getNorCalComprehensiveCompleted());
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

    private void addGad7PHQ9Scoring(Workbook wb, Map<String, CellStyle> styles, List<GAD7PHQ9Scoring> gad7PHQ9ScoringList) {
        var sheet = wb.createSheet("GAD-7 & PHQ-9 scoring");

        var rowCount = 0;
        Row headerRow = sheet.createRow(rowCount++);
        var headerValues = Arrays.asList("Community Name", "Client ID", "Resident/Client Name", "GAD-7 score", "PHQ-9 score");

        writeHeader(headerRow, styles, headerValues);

        for (var value : gad7PHQ9ScoringList) {

            if (isNotEmpty(value.getGad7scores()) || isNotEmpty(value.getPhq9Scores())) {

                var row = sheet.createRow(rowCount++);

                var colCount = 0;
                writeToCell(styles, row.createCell(colCount++), value.getCommunity());
                writeToCell(styles, row.createCell(colCount++), value.getClientId());
                writeToCell(styles, row.createCell(colCount++), value.getFirstName() + " " + value.getLastName());

                if (value.getGad7scores().size() == 1) {
                    writeToCell(styles, row.createCell(colCount++), value.getGad7scores().get(0)); //store as number
                } else {
                    withNa(row.createCell(colCount++), styles.get("right_align"), StringUtils.join(value.getGad7scores(), ", "));
                }

                if (value.getPhq9Scores().size() == 1) {
                    writeToCell(styles, row.createCell(colCount++), value.getPhq9Scores().get(0)); //store as number
                } else {
                    withNa(row.createCell(colCount++), styles.get("right_align"), StringUtils.join(value.getPhq9Scores(), ", "));
                }
            }
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

    private void addComprehensiveDetails(XSSFWorkbook wb, Map<String, CellStyle> styles, List<ComprehensiveDetail> comprehensiveDetailList) {
        var sheet = wb.createSheet("Comprehensive Assessment");

        var rowCount = 0;
        Row headerRow = sheet.createRow(rowCount++);
        var headerValues = Arrays.asList("Community Name", "Resident/Client Name ", "Resident/Client ID", "Time to complete (mins)", "Gender (record)", "Gender (assessment)",
                "Race (record)", "Race (assessment)", "Age", "Income, $", "Insurance Network", "Insurance Plan");

        writeHeader(headerRow, styles, headerValues);

        for (var value : comprehensiveDetailList) {
            var row = sheet.createRow(rowCount++);

            var colCount = 0;
            writeToCell(styles, row.createCell(colCount++), value.getCommunity());
            writeToCell(styles, row.createCell(colCount++), value.getPatientName());
            writeToCell(styles, row.createCell(colCount++), value.getPatientId());
            writeToCell(styles, row.createCell(colCount++), value.getTimeToCompleteInMinutes());
            writeToCell(styles, row.createCell(colCount++), value.getGenderFormClient());
            writeToCell(styles, row.createCell(colCount++), value.getGenderFromAssessment());
            writeToCell(styles, row.createCell(colCount++), value.getRaceFromClient());
            writeToCell(styles, row.createCell(colCount++), value.getRaceFromAssessment());
            writeToCell(styles, row.createCell(colCount++), value.getAge());
            writeToCell(styles, row.createCell(colCount++), value.getIncome());
            writeToCell(styles, row.createCell(colCount++), value.getInsuranceNetwork());
            writeToCell(styles, row.createCell(colCount++), value.getInsurancePlan());
        }

        autosizeWidth(sheet, headerValues.size());
    }

    private void addSpGeneral(Workbook wb, Map<String, CellStyle> styles, DemographicsReport report) {
        var sheet = wb.createSheet("SP - overall");

        var rowCount = 0;
        Row headerRow = sheet.createRow(rowCount++);
        var headerValues = Arrays.asList("Community Name", "# of clients", "# of service plans opened", "average # of service plans per client", "# of service plans closed",
                "total # of needs", "total # of goals", "average # of needs per client", "average # of goals per client", "# of accomplished goals", "% of accomplished goals");

        writeHeader(headerRow, styles, headerValues);

        List<SPGeneral> spGeneralList = report.getSpGeneralList();
        for (var spGeneral : spGeneralList) {
            var row = sheet.createRow(rowCount++);

            var colCount = 0;
            writeToCell(styles, row.createCell(colCount++), spGeneral.getCommunityName());
            writeToCell(styles, row.createCell(colCount++), spGeneral.getNumberOfPatients());
            writeToCell(styles, row.createCell(colCount++), spGeneral.getNumberOfSPopened());
            writeToCell(styles, row.createCell(colCount++), spGeneral.getAverageNumberOfServicePlansPerClient());
            writeToCell(styles, row.createCell(colCount++), spGeneral.getNumberOfSpClosed());
            writeToCell(styles, row.createCell(colCount++), spGeneral.getTotalNumberOfNeeds());
            writeToCell(styles, row.createCell(colCount++), spGeneral.getTotalNumberOfGoals());

            writeToCell(styles, row.createCell(colCount++), spGeneral.getAverageNumberOfNeedsPerClient());
            writeToCell(styles, row.createCell(colCount++), spGeneral.getAverageNumberOfGoalsPerClient());
            writeToCell(styles, row.createCell(colCount++), spGeneral.getNumberOfAccomplishedGoals());
            writeToCell(styles, row.createCell(colCount++), spGeneral.getPercentOfAccomplishedGoals());
        }

        autosizeWidth(sheet, headerValues.size());
    }

    private void addSpDetails(Workbook wb, Map<String, CellStyle> styles, List<SPDetails> spDetailsList) {
        var sheet = wb.createSheet("SP - Details");

        var rowCount = 0;
        var colCount = 0;
        Row headerRow = sheet.createRow(rowCount++);
        var headerValues = Arrays.asList("Community Name", "Resident/Client Name ", "Resident/Client ID", "Service Plan Status", "Number of days to complete", "Events count (ER, Hospitalization)", "Goals count");

        writeHeader(headerRow, styles, headerValues);

        for (var value : spDetailsList) {
            var row = sheet.createRow(rowCount++);

            colCount = 0;
            writeToCell(styles, row.createCell(colCount++), value.getCommunity());
            writeToCell(styles, row.createCell(colCount++), value.getPatientName());
            writeToCell(styles, row.createCell(colCount++), value.getPatientId());
            writeToCell(styles, row.createCell(colCount++), value.getStatus());
            writeToCell(styles, row.createCell(colCount++), value.getDaysToComplete());
            writeToCell(styles, row.createCell(colCount++), value.getEventsCount());
            writeToCell(styles, row.createCell(colCount++), value.getGoalsCount());

        }
        autosizeWidth(sheet, headerValues.size());
    }

    private void addSpIndividuals(XSSFWorkbook wb, Map<String, CellStyle> styles, List<SPIndividualTab> spIndividuals) {
        var sheet = wb.createSheet("SP - Individuals");

        var rowCount = 0;
        var colCount = 0;
        Row headerRow = sheet.createRow(rowCount++);
        var headerValues = Arrays.asList("Community name", "Resident/Client Name ", "Resident/Client ID", "Domain", "Score of tied to Service Plan domain", "Resource Name");
        writeHeader(headerRow, styles, headerValues);

        String clientName = "";
        Integer startClientRowCount = null;
        Integer endClientRowCount = null;

        for (var spIndividual : spIndividuals) {
            var row = sheet.createRow(rowCount++);
            colCount = 0;
            writeToCell(styles, row.createCell(colCount++), spIndividual.getCommunity());
            var spIndividualNum = 0;
            for (var spClient : spIndividual.getSpIndividualClients()) {
                if (spIndividualNum != 0) {
                    row = sheet.createRow(rowCount++);
                }
                if (!clientName.equals(spClient.getName())) {
                    startClientRowCount = rowCount;
                    clientName = spClient.getName();
                }
                colCount = 1;
                writeToCell(styles, row.createCell(colCount++), spClient.getName());
                writeToCell(styles, row.createCell(colCount++), spClient.getId());
                var spIndividualDomains = CollectionUtils.emptyIfNull(spClient.getSpIndividualDomains());
                var spDomainNum = 0;
                for (var spDomain : spIndividualDomains) {
                    if (spDomainNum != 0) {
                        row = sheet.createRow(rowCount++);
                    }
                    if (spIndividualDomains.size() > 1 && spDomainNum == spIndividualDomains.size() - 1) {
                        endClientRowCount = rowCount;
                        if (!endClientRowCount.equals(startClientRowCount)) {
                            sheet.groupRow(startClientRowCount, endClientRowCount);
                            sheet.setRowGroupCollapsed(endClientRowCount, true);
                            sheet.setRowSumsBelow(false);
                            logger.info("[REPORTS] [DEMOGRAPHICS] Grouped rows {} - {}", startClientRowCount, endClientRowCount + spIndividualDomains.size());
                        }
                    }
                    var innerColCount = colCount;
                    writeToCell(styles, row.createCell(innerColCount++), spDomain.getDomainName());
                    writeToCell(styles, row.createCell(innerColCount++), spDomain.getScoreOfTiedToServicePlanNeed());
                    writeToCell(styles, row.createCell(innerColCount++), CollectionUtils.emptyIfNull(spDomain.getResourceNames()).stream().collect(Collectors.joining(", ")));
                    spDomainNum++;
                }
                spIndividualNum++;
            }
        }
        autosizeWidth(sheet, headerValues.size());
    }

}
