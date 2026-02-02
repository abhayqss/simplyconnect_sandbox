package com.scnsoft.eldermark.service.report.appointment;

import com.scnsoft.eldermark.dto.appointment.ClientAppointmentExportDto;
import com.scnsoft.eldermark.service.report.workbook.BaseWorkbookGenerator;
import com.scnsoft.eldermark.service.report.workbook.ExcelUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.autosizeWidth;
import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.writeToCell;

@Service
public class AppointmentWorkBookGeneratorImpl extends BaseWorkbookGenerator implements AppointmentWorkBookGenerator {

    private final static List<String> EXPORT_CRITERIA_HEADERS = List.of(
            "Organization",
            "Community",
            "Date from",
            "Date to",
            "Service Provider",
            "Creator",
            "Client",
            "Client Status",
            "Appointment type",
            "Appointment status"
    );

    private final static List<String> APPOINTMENTS_HEADERS = List.of(
            "Organization",
            "Community",
            "Appointment date",
            "Start time",
            "End time",
            "Appointment status",
            "Client name",
            "Creator",
            "Service Provider",
            "Appointment title",
            "Location",
            "Appointment type",
            "Service category",
            "Referral Source",
            "Reason for Visit",
            "Appointment Directions & Instructions",
            "Notes",
            "Client Reminder",
            "Notification Method",
            "Cell phone #",
            "Email"
    );

    @Override
    public Workbook generateWorkBook(ClientAppointmentExportDto dto) {
        var wb = new XSSFWorkbook();
        var styles = ExcelUtils.createStyles(wb);
        addExportCriteriaTab(wb, styles, dto);
        addAppointmentsTab(wb, styles, dto);
        return wb;
    }

    private void addExportCriteriaTab(XSSFWorkbook wb, Map<String, CellStyle> styles, ClientAppointmentExportDto dto) {
        var sheetName = "Export Criteria";
        Sheet sheet = createSheetWithHeader(wb, styles, sheetName, EXPORT_CRITERIA_HEADERS, 0);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, EXPORT_CRITERIA_HEADERS.size() - 1));

        var rowCount = 1;
        var rows = dto.getRows();
        for (var organizationRow : rows) {
            var colCount = 0;
            var row = sheet.createRow(rowCount);
            writeToCell(styles, row.createCell(colCount++), organizationRow.getOrganizationName());
            for (var communityRow : organizationRow.getCommunityRows()) {
                row = getOrCreateRow(sheet, rowCount);
                var communityColCount = colCount;
                writeToCell(styles, row.createCell(communityColCount++), communityRow.getCommunityName());
                for (var clientRow : communityRow.getClientRows()) {
                    row = getOrCreateRow(sheet, rowCount++);
                    var clientColCount = communityColCount;
                    writeToCell(styles, row.createCell(clientColCount++), formatToDateTime(clientRow.getDateFrom(), dto.getTimeZoneOffset()));
                    writeToCell(styles, row.createCell(clientColCount++), formatToDateTime(clientRow.getDateTo(), dto.getTimeZoneOffset()));
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getServiceProviders());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getCreator());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getClientName());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getClientStatus());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getAppointmentType());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getAppointmentStatus());
                }

                autosizeWidth(sheet, EXPORT_CRITERIA_HEADERS.size() + 1);
            }
        }
    }

    private void addAppointmentsTab(XSSFWorkbook
                                            wb, Map<String, CellStyle> styles, ClientAppointmentExportDto dto) {
        var sheetName = "Appointments";
        Sheet sheet = createSheetWithHeader(wb, styles, sheetName, APPOINTMENTS_HEADERS, 0);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, APPOINTMENTS_HEADERS.size() - 1));

        var rowCount = 1;
        var rows = dto.getRows();
        for (var organizationRow : rows) {
            var colCount = 0;
            var row = sheet.createRow(rowCount);
            writeToCell(styles, row.createCell(colCount++), organizationRow.getOrganizationName());
            for (var communityRow : organizationRow.getCommunityRows()) {
                row = getOrCreateRow(sheet, rowCount);
                var communityColCount = colCount;
                writeToCell(styles, row.createCell(communityColCount++), communityRow.getCommunityName());
                for (var clientRow : communityRow.getClientRows()) {
                    row = getOrCreateRow(sheet, rowCount++);
                    var clientColCount = communityColCount;
                    writeToCell(styles, row.createCell(clientColCount++), formatToDate(clientRow.getDateFrom(), dto.getTimeZoneOffset()));
                    writeToCell(styles, row.createCell(clientColCount++), formatToTime(clientRow.getStartTime(), dto.getTimeZoneOffset()));
                    writeToCell(styles, row.createCell(clientColCount++), formatToTime(clientRow.getEndTime(), dto.getTimeZoneOffset()));
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getAppointmentStatus());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getClientName());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getCreator());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getServiceProviders());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getAppointmentTitle());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getLocation());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getAppointmentType());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getServiceCategory());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getReferralSource());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getReasonForVisit());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getDirectionsInstructions());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getNotes());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getClientReminders());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getNotificationMethods());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getCellPhone());
                    writeToCell(styles, row.createCell(clientColCount++), clientRow.getEmail());
                }
            }
        }

        autosizeWidth(sheet, APPOINTMENTS_HEADERS.size() + 1);
    }
}
