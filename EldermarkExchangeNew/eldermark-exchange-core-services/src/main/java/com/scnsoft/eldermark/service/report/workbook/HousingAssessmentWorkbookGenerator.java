package com.scnsoft.eldermark.service.report.workbook;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.assessment.housing.HousingAssessmentReport;
import com.scnsoft.eldermark.beans.reports.model.staffcaseload.StaffCaseloadReport;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;

@Service
public class HousingAssessmentWorkbookGenerator extends BaseWorkbookGenerator implements WorkbookGenerator<HousingAssessmentReport> {

    private static final String HOUSING_ASSESSMENT_TAB_TITLE = "Housing Assessment";
    private static final List<String> HOUSING_ASSESSMENT_HEADERS = List.of(
            "Community Name",
            "Client ID",
            "Client Status",
            "Client Name",
            "Assessment date",
            "Program",
            "Assessment type",
            "Have you ever been on a lease before or recently applied for one?",
            "As an adult have you lived in subsidized housing before? (Section 8, voucher, etc.)",
            "Do you currently have a Housing Voucher?",
            "Do you have any evictions in the last ten years?",
            "Do you have any accessibility needs that need to be considered?",
            "Do you have any pets?",
            "Do you currently receive income?",
            "Do you have any savings for moving? (ex. Deposit, first month’s rent, moving truck)",
            "Credit Status: (Good, Fair, Poor, No Credit?)",
            "Do you owe any utilities, eviction costs, or child support?",
            "Do you have Pending Legal Case?",
            "Do you have Criminal Convictions?",
            "Do you have Open Legal Case?",
            "Do you have Registered 290?",
            "Bathe, as in washing your face and body in the bath or shower.",
            "Dress and groom, as in selecting clothes, putting them on and adequately managing your personal appearance.",
            "Toileting, as in getting to and from the toilet, using it appropriately, and cleaning yourself.",
            "Eating, as in being able to get food from a plate into one’s mouth.",
            "Medications: which covers obtaining medications and taking them as directed.",
            "Housekeeping. This means cleaning kitchens after eating, keeping one’s living space reasonably clean and tidy, and keeping up with home maintenance.",
            "Meal Preparation/ Cooking.",
            "Laundry.",
            "Telephone.",
            "Shopping.",
            "Finances, such as paying bills and managing financial assets",
            "Transportation, either via driving or by organizing other means of transport.",
            "Make Medical Appointments:",
            "Mobility: walking inside/outside home.",
            "Do you have a family member or other caregiver assisting you?"
    );

    @Override
    public Workbook generateWorkbook(HousingAssessmentReport report) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addReportingCriteriaTab(workbook, styles, report);
        addHousingAssessmentTab(workbook, styles, report);
        return workbook;
    }

    public void addHousingAssessmentTab(Workbook wb, Map<String, CellStyle> styles, HousingAssessmentReport report) {
        Sheet sheet = createSheetWithHeader(wb, styles, HOUSING_ASSESSMENT_TAB_TITLE, HOUSING_ASSESSMENT_HEADERS, 0);

        var rowCount = 1;
        var reportItems = report.getItems();
        for (var item : reportItems) {
            var row = sheet.createRow(rowCount++);
            var colCount = 0;
            writeToCell(styles, row.createCell(colCount++), item.getClientCommunityName());

            for (var residentItem : item.getClients()) {
                var resColCount = colCount;
                if (row == null) row = sheet.createRow(rowCount++);
                writeToCell(styles, row.createCell(resColCount++), residentItem.getClientId());
                writeToCell(styles, row.createCell(resColCount++), residentItem.isClientActive() ? "Active" : "Inactive");
                writeToCell(styles, row.createCell(resColCount++), residentItem.getClientName());
                writeToCell(styles, row.createCell(resColCount++), formatToDate(residentItem.getAssessmentDate(), residentItem.getTimeZoneOffset()));
                writeToCell(styles, row.createCell(resColCount++), residentItem.getProgram());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getAssessmentType());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getLeaseQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getSubsidizedHousingQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getHousingVoucherQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getEvictionsQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getAccessibilityNeedsQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getPetsQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getIncomeQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getSavingsQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getCreditStatus());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getOwingQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getPendingLegalCaseQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getCriminalConvictionsQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getOpenLegalCaseQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getRegistered290Question());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getBatheQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getDressAndGroomQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getToiletingQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getEatingQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getMedicationQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getHousekeepingQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getCookingQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getLaundryQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getTelephoneQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getShoppingQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getFinancesQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getTransportationQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getMakeMedicalAppointmentsQuestion());
                writeToCell(styles, row.createCell(resColCount++), residentItem.getMobilityQuestion());
                writeToCell(styles, row.createCell(resColCount), residentItem.getFamilyMemberQuestion());
                row = null;
            }
        }

        autosizeWidth(sheet, HOUSING_ASSESSMENT_HEADERS.size() + 1);
    }

    @Override
    public ReportType generatedReportType() {
        return ReportType.HOUSING_ASSESSMENT;
    }
}
