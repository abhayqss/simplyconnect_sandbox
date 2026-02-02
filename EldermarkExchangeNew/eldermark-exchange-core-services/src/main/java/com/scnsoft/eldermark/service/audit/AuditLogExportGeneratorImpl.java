package com.scnsoft.eldermark.service.audit;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.beans.audit.AuditLogFilterDto;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.projection.NameAware;
import com.scnsoft.eldermark.beans.projection.NamesAware;
import com.scnsoft.eldermark.dao.AuditLogDao;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogRelation;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.service.report.workbook.ExcelUtils.*;

@Component
@Transactional(readOnly = true)
public class AuditLogExportGeneratorImpl implements AuditLogExportGenerator {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private AuditLogConverterService auditLogConverterService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AuditLogDao auditLogDao;

    @Override
    public Workbook generate(Collection<AuditLog> auditLogs, AuditLogFilterDto filter) {
        var workbook = new XSSFWorkbook();
        var styles = createStyles(workbook);
        addCriteriaTab(workbook, styles, filter);
        addDataTab(workbook, styles, auditLogs, filter.getZoneId());
        return workbook;
    }

    public void addCriteriaTab(Workbook workbook, Map<String, CellStyle> styles, AuditLogFilterDto filter) {
        var sheet = workbook.createSheet("Report criteria");

        var orgName = organizationService.findById(filter.getOrganizationId(), NameAware.class).getName();
        Stream<String> names;
        if (CollectionUtils.isNotEmpty(filter.getCommunityIds())) {
            names = communityService.findAllById(filter.getCommunityIds(), NameAware.class).stream().map(NameAware::getName);
        } else {
            names = communityService.findAllByOrgId(filter.getOrganizationId()).stream().map(IdNameAware::getName);
        }
        var communityNames = names.filter(org.apache.commons.lang.StringUtils::isNotBlank).collect(Collectors.toList());

        var rowCount = 0;
        var headerRow = sheet.createRow(rowCount++);
        var headerValues = Arrays.asList("Organization", communityNames.size() == 1 ? "Community" : "Communities", "From", "To");

        writeHeader(headerRow, styles, headerValues);

        sheet.setColumnWidth(0, CHAR_LENGTH * 50);
        sheet.setColumnWidth(1, CHAR_LENGTH * 25);

        for (var i = 0; i < communityNames.size(); i++) {
            var row = sheet.createRow(rowCount++);
            var colCount = 0;
            if (i == 0) {
                writeToCell(row.createCell(colCount++), styles, orgName);
                writeToCell(row.createCell(colCount++), styles, communityNames.get(i));
                writeToCell(row.createCell(colCount++), styles, DateTimeUtils.formatDate(Instant.ofEpochMilli(filter.getFromDate()), filter.getZoneId()));
                writeToCell(row.createCell(colCount), styles, DateTimeUtils.formatDate(Instant.ofEpochMilli(filter.getToDate()), filter.getZoneId()));
            } else {
                colCount++;
                writeToCell(row.createCell(colCount), styles, communityNames.get(i));
            }
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

    private void addDataTab(Workbook workbook, Map<String, CellStyle> styles, Collection<AuditLog> auditLogs, ZoneId zoneId) {
        var sheet = workbook.createSheet("Audit logs");
        var rowCount = 0;
        var headerRow = sheet.createRow(rowCount++);
        var headerValues = Arrays.asList("Activity", "Client Name", "User", "Date", "Notes");

        writeHeader(headerRow, styles, headerValues);

        for (var auditLog : auditLogs) {
            var row = sheet.createRow(rowCount++);
            var colCount = 0;
            var relatedIds = Optional.ofNullable(auditLog.getAuditLogRelation()).map(AuditLogRelation::getRelatedIds).orElse(null);
            var relatedAdditionalFields = Optional.ofNullable(auditLog.getAuditLogRelation()).map(AuditLogRelation::getAdditionalFields).orElse(null);
            var converterType = Optional.ofNullable(auditLog.getAuditLogRelation()).map(AuditLogRelation::getConverterType).orElse(null);
            var logActivity = auditLogConverterService.convertToAuditLogActivity(converterType, auditLog, relatedIds);
            writeToCell(row.createCell(colCount++), styles, logActivity != null ? logActivity.getDisplayName() : "");
            writeToCell(row.createCell(colCount++), styles, getClientNames(auditLog));
            writeToCell(row.createCell(colCount++), styles, getEmployeeName(auditLog));
            writeToCell(row.createCell(colCount++), styles, DateTimeUtils.formatDateTime(auditLog.getDate(), zoneId));
            writeToCell(row.createCell(colCount), styles, getNotes(logActivity, auditLog, relatedIds, relatedAdditionalFields, zoneId));
        }

        autosizeWidth(sheet, headerValues.size() + 1);
    }

    private String getClientNames(AuditLog auditLog) {
        return CareCoordinationUtils.concat(", ", clientService.findAllById(auditLog.getClientIds(), NamesAware.class).stream().map(NamesAware::getFullName));
    }

    private String getEmployeeName(AuditLog auditLog) {
        return employeeService.findById(auditLog.getEmployeeId(), NamesAware.class).getFullName();
    }

    private String getNotes(AuditLogActivity activity, AuditLog auditLog, List relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        var converterType = Optional.ofNullable(auditLog.getAuditLogRelation()).map(AuditLogRelation::getConverterType).orElse(null);
        return CareCoordinationUtils.concat(", ", auditLogConverterService.convertNotes(converterType, activity, auditLog, relatedIds, relatedAdditionalFields, zoneId));
    }

    public void writeToCell(Cell cell, Map<String, CellStyle> styles, String value) {
        cell.setCellStyle(styles.get("wrapped_text"));

        cell.setCellValue(StringUtils.firstNonEmpty(value, ""));
    }
}
