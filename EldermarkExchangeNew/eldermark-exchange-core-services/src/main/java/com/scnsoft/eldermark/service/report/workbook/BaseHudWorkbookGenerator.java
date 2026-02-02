package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.HudReport;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Map;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;

public abstract class BaseHudWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<HudReport> {

    private static final Map<ReportType, String> coverPageProgramMapping = Map.of(
            ReportType.HUD_MFSC, "MFSC",
            ReportType.HUD, "Other"
    );
    protected static int SKIP_INPUT_COLS = 2; //C
    protected static int SKIP_INPUT_ROWS = 7; //8
    protected static int NUMBER_OF_COLUMNS = 142;
    private static String COVER_PAGE_SHEET = "CoverPage";
    private static String INPUT_SHEET = "Input";
    private static int COVERPAGE_PROGRAM_ROW = 2; //C
    private static int COVERPAGE_PROGRAM_COL = 2; //3


    @Value("classpath:reports/HUD_template_cleaned.xlsx")
    private Resource hudTemplate;

    @Override
    public Workbook generateWorkbook(HudReport report) {
        XSSFWorkbook workbook = openTemplateWorkbook();

        var styles = createStyles(workbook);

        addHudSpecificStyles(styles, workbook);

        addHudReportCriteria(workbook, styles, report);
        addHudInputTab(workbook, styles, report);

        return workbook;
    }

    private void addHudSpecificStyles(Map<String, CellStyle> styles, Workbook workbook) {
        var inputSheet = workbook.getSheet(INPUT_SHEET);

        var exampleRow = inputSheet.getRow(SKIP_INPUT_ROWS);

        for (int colIdx = SKIP_INPUT_COLS; colIdx < exampleRow.getLastCellNum(); ++colIdx) {
            styles.put(getCellStyleKey(colIdx), exampleRow.getCell(colIdx).getCellStyle());
        }
    }

    private static String getCellStyleKey(int colIdx) {
        return "hud_cell-" + colIdx;
    }

    private XSSFWorkbook openTemplateWorkbook() {
        try {
            return (XSSFWorkbook) WorkbookFactory.create(hudTemplate.getInputStream());
        } catch (IOException e) {
            throw new InternalServerException(InternalServerExceptionType.HUD_IO_ERROR);
        }
    }


    public void addHudReportCriteria(Workbook wb, Map<String, CellStyle> styles, HudReport report) {
        Sheet sheet = wb.getSheet(COVER_PAGE_SHEET);

        var programValue = coverPageProgramMapping.get(report.getReportType());

        sheet.getRow(COVERPAGE_PROGRAM_ROW).getCell(COVERPAGE_PROGRAM_COL).setCellValue(programValue);

    }

    public void addHudInputTab(XSSFWorkbook wb, Map<String, CellStyle> styles, HudReport report) {
        var rowCount = SKIP_INPUT_ROWS;
        var sheet = wb.getSheet(INPUT_SHEET);

        for (var hudSecondTab : report.getHudSecondTab()) {
            var row = sheet.createRow(rowCount++);

            var colCount = SKIP_INPUT_COLS;

            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getParticipantStatusCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getPersonIdentifier());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHouseHoldIdentifier());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getIntakeDate());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getAge());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getGenderCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getEthnicityCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getRaceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHeadOfHouseholdCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getClientCensusTract());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getClientCensusTractInfoNotCollected());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getVeteranStatusCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getYearsInHousingNumber());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getYearsInHousingNumberInfoNotCollected());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getDisabilityStatusCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getDisabilityCategoryCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getDisabilityRequiresAssistanceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHardToHouseCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getReturningCitizenCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getEarnedIncomeTaxCreditCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getFinancialAccountCreationCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getSnapCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getTanfCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getSsiCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getSsdiCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getSubstanceAbuseTreatmentCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getAidsHiv());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getAdlCount());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getAdlCountInfoNotCollected());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getIadlCount());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getIadlCountInfoNotCollected());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getServiceStartDate());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getServiceStartDateInfoNotCollected());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getServiceEndDate());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getServiceEndDateInfoNotCollected());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getOpportunityAreaCensusTract());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHousingStatusCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getPrimaryHealthCareProviderCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHealthCoverageCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getMedicalExaminationStatusCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHighestEducationLevelCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getEnrollmentInEduOrVocProgram());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getLicenseAttainmentCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getDegreeAttainmentCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getEmpStatusCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getEmpTypeStatusCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getEmpDate());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getEmpDateInfoNotCollected());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getOccupationCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getMonthlyPaidEarningsAmount());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getMonthlyPaidEarningsAmountInfoNotCollected());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHouseHoldAnnualGrossAmount());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHouseHoldAnnualGrossAmountInfoNotCollected());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHomelessStatusCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getWeeksHomelessCount());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getWeeksHomelessCountInfoNotCollected());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getChronicHomelessStatusCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getPriorNightClientCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getIntermediateHousingStatusCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHouseHoldHousingCostAmount());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHouseHoldHousingCostAmountInfoNotCollected());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHouseHoldTransporationCostAmount());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHouseHoldTransporationCostAmountInfoNotCollected());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getAsthmaConditionCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getEmergencyRoomVisitCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getEmergencyRoomVisitCodeNumberTotal());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getEmergencyRoomVisitCodeAsthma());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getBloodLeadTestCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getBloodLeadTestResult());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getAdultBasicEduServiceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getAdultBasicEduNumberOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getEslClassServiceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getEslServiceCodeNumberOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getCareerGuidanceServiceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getCareerGuidanceNumOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getSelfDirectedJobSearchAssistCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getSelfDirectedJobSearchAssistCodeNumberOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getWorkReadinessAssistanceServiceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getWorkReadinessNumberOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getOstServiceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getOstServiceCodeNumbOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getJobDevelopmentServiceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getJobDevelopmentCodeNumbOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getJobRetentionCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getJobRetentionCodeNumbOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getFairHousingServiceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getFairHousingCodeNumbOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getTaxPreparationCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getTaxPreparationCodeNumbOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getFinancialAccountCreationServiceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getFinancialAccountCreationServiceCodeNumbOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getLegalAssistanceServiceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getLegalAssistanceServiceCodeNumbOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getLegalAssistanceTypeServiceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getFinancialEducationServiceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getFinancialEducationServiceCodeNumOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getPrehousingServiceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getPrehousingServiceCodeNumbOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getPostHousingCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getPostHousingCodeNumOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getFoodAndNutritionCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getFoodAndNutritionCodeNumbOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getConflictResolutionCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getConflictResolutionCodeNumOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getInterpretationServiceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getInterpretationServiceCodeNumOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHousingRetentionCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHousingRetentionCodeNumOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHouseHoldSkillsCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHouseHoldSkillsCodeNumbOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getNeedsAssessmentServiceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getServiceCoordinationCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getServiceCoordinationCodeNumbOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getParentingSkillsCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getParentingSkillsCodeNumOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getChildhoodEducationCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHighSchoolCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHighSchoolCodeNumberOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getPostSecondaryEducationCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getPostSecondaryEducationCodeNumbOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getShelterPlacementCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getShelterPlacementCodeNumOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getTempHousingPlacementCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getTempHousingPlacementCodeNumOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getPermanentHousingPlacementCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getPermanentHousingPlacementDate());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getPermanentHousingPlacementInfoNotCollected());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getIndependentLivingServiceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getIndependentLivingServiceCodeNumOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getTransportationServiceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getTransportationServiceCodeNumberOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHivAidsCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getHivAidsCodeNumOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getAdultPersonalAssistanceCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getAdultPersonalAssistanceCodeNumOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getMedicalCareCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getMedicalCareNumOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getMentalHealthCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getMentalHealthCodeNumOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getSubstanceAbuseCode());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getSubstanceAbuseCodeNumOfTimes());
            writeToCellEmptyIfNa(styles, row.createCell(colCount++), hudSecondTab.getiRemsNumber());
        }

        autosizeWidth(sheet, NUMBER_OF_COLUMNS + SKIP_INPUT_COLS + 1);

        //apply styling
        for (int rowIdx = SKIP_INPUT_ROWS; rowIdx < rowCount; ++rowIdx) {
            var row = sheet.getRow(rowIdx);
            for (int colIdx = SKIP_INPUT_COLS; colIdx < row.getLastCellNum(); ++colIdx) {
                row.getCell(colIdx).setCellStyle(styles.get(getCellStyleKey(colIdx)));
            }
        }

        if (rowCount > SKIP_INPUT_ROWS) {
            var table = sheet.getTables().get(0);
            var newTableArea = new AreaReference(
                    table.getArea().getFirstCell(),
                    new CellReference(rowCount - 1, sheet.getRow(rowCount - 1).getLastCellNum() - 1),
                    wb.getSpreadsheetVersion()
            );
            table.setArea(newTableArea);
        }
    }

}
