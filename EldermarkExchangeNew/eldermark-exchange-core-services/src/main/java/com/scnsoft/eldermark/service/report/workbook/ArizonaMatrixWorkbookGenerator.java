package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.constants.ArizonaMatrixConstants;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.arizona.ArizonaMatrixReport;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;

@Service
public class ArizonaMatrixWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<ArizonaMatrixReport> {

    public static final String REPORT_SHEET_NAME = "Arizona Matrix";
    public static final List<String> REPORT_HEADERS = List.of(
        ArizonaMatrixConstants.COMMUNITY_NAME,
        ArizonaMatrixConstants.CLIENT_ID,
        ArizonaMatrixConstants.CLIENT_NAME,
        ArizonaMatrixConstants.DATE_OF_BIRTH,
        ArizonaMatrixConstants.CASE_MANAGER,
        ArizonaMatrixConstants.PROGRAM_NAME,
        ArizonaMatrixConstants.ASSESSMENT_TYPE,
        ArizonaMatrixConstants.SURVEY_FREQUENCY,
        ArizonaMatrixConstants.ASSESSMENT_DATE,
        ArizonaMatrixConstants.ASSESSMENT_TOTAL_SCORE,
        ArizonaMatrixConstants.INCOME,
        ArizonaMatrixConstants.CREDIT_STATUS,
        ArizonaMatrixConstants.EMPLOYMENT,
        ArizonaMatrixConstants.SHELTER,
        ArizonaMatrixConstants.FOOD,
        ArizonaMatrixConstants.CHILD_CARE,
        ArizonaMatrixConstants.KINDS_OF_CHILD_CARE,
        ArizonaMatrixConstants.CHILDREN_EDUCATION,
        ArizonaMatrixConstants.ADULT_EDUCATION,
        ArizonaMatrixConstants.HIGHEST_GRADE,
        ArizonaMatrixConstants.LEGAL,
        ArizonaMatrixConstants.CONVICTED_OF,
        ArizonaMatrixConstants.CONVICTED_AND_CHARGED_WITH,
        ArizonaMatrixConstants.IS_290_REGISTRANT,
        ArizonaMatrixConstants.HEALTH_CARE_COVERAGE,
        ArizonaMatrixConstants.LIFE_SKILLS,
        ArizonaMatrixConstants.MENTAL_HEALTH,
        ArizonaMatrixConstants.SUBSTANCE_ABUSE,
        ArizonaMatrixConstants.FAMILY_AND_SOCIAL_RELATIONS,
        ArizonaMatrixConstants.TRANSPORTATION,
        ArizonaMatrixConstants.COMMUNITY_INVOLVEMENT,
        ArizonaMatrixConstants.SAFETY,
        ArizonaMatrixConstants.GANG_AFFILIATION,
        ArizonaMatrixConstants.PARENTING_SKILLS,
        ArizonaMatrixConstants.ACTIVE_CPS_CASE,
        ArizonaMatrixConstants.PREVIOUS_CPS_INVOLVEMENT,
        ArizonaMatrixConstants.DISABILITIES
    );

    @Override
    public Workbook generateWorkbook(ArizonaMatrixReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addAssessmentResultsTab(workbook, styles, report);
        return workbook;
    }

    @Override
    public ReportType generatedReportType() {
        return ReportType.ARIZONA_MATRIX;
    }

    private void addAssessmentResultsTab(Workbook wb, Map<String, CellStyle> styles, ArizonaMatrixReport report) {
        Sheet sheet = createSheetWithHeader(wb, styles, REPORT_SHEET_NAME, REPORT_HEADERS, 0);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, REPORT_HEADERS.size() - 1));

        var rowCount = 1;
        for (var reportRow : report.getRows()) {
            var colCount = 0;
            var row = getOrCreateRow(sheet, rowCount);
            writeToCell(styles, row.createCell(colCount++), reportRow.getCommunityName());

            for (var rowClient: reportRow.getClients()) {
                var clientColCount = colCount;
                row = getOrCreateRow(sheet, rowCount);
                writeToCell(styles, row.createCell(clientColCount++), rowClient.getClientId());
                writeToCell(styles, row.createCell(clientColCount++), rowClient.getClientName());
                writeToCell(styles, row.createCell(clientColCount++), formatToDate(rowClient.getDateOfBirth()));

                for (var rowAssessment: rowClient.getAssessments()) {
                    row = getOrCreateRow(sheet, rowCount++);
                    var assessmentColCount = clientColCount;
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getCaseManagerName());
                    writeToCell(styles, row.createCell(assessmentColCount++), listToString(rowAssessment.getProgramName()));
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getAssessmentType());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getSurveyFrequency());
                    writeToCell(styles, row.createCell(assessmentColCount++), formatToDate(rowAssessment.getAssessmentDate(), report.getTimeZoneOffset()));
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getAssessmentTotalScore());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getIncome());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getCreditStatus());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getEmployment());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getShelter());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getFood());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getChildCare());
                    writeToCell(styles, row.createCell(assessmentColCount++), listToString(rowAssessment.getKindsOfChildCare()));
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getChildrenEducation());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getAdultEducation());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getHighestGrade());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getLegal());
                    writeToCell(styles, row.createCell(assessmentColCount++), listToString(rowAssessment.getConvictedOf()));
                    writeToCell(styles, row.createCell(assessmentColCount++), listToString(rowAssessment.getConvictedAndChargedWith()));
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getIs290Registrant());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getHealthCareCoverage());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getLifeSkills());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getMentalHealth());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getSubstanceAbuse());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getFamilyAndSocialRelations());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getTransportation());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getCommunityInvolvement());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getSafety());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getGangAffiliation());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getParentingSkills());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getActiveCpsCase());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getPreviousCpsInvolvement());
                    writeToCell(styles, row.createCell(assessmentColCount++), rowAssessment.getDisabilities());
                }
            }
        }

        autosizeWidth(sheet, REPORT_HEADERS.size() + 1);
    }

    private String listToString(List<String> list) {
        return list != null ? String.join(", ", list) : null;
    }
}
