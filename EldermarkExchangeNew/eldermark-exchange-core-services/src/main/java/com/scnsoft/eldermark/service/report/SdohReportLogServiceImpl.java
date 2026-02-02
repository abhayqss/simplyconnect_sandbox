package com.scnsoft.eldermark.service.report;

import com.scnsoft.eldermark.beans.reports.model.sdoh.SDoHReport;
import com.scnsoft.eldermark.beans.reports.model.sdoh.SDoHRow;
import com.scnsoft.eldermark.beans.reports.model.sdoh.SdohFieldDescriptor;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.dao.SdohReportLogDao;
import com.scnsoft.eldermark.dao.SdohReportRowDataDao;
import com.scnsoft.eldermark.dao.specification.SdohReportLogSpecificationGenerator;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.sdoh.SdohReportLog;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.service.report.sdoh.SDoHReportGenerator;
import com.scnsoft.eldermark.service.report.sdoh.SDoHWorkbookGenerator;
import com.scnsoft.eldermark.service.report.sdoh.SDoHWorkbookGeneratorImpl;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Transactional
public class SdohReportLogServiceImpl implements SdohReportLogService {

    private static final Logger logger = LoggerFactory.getLogger(SdohReportLogServiceImpl.class);

    private static final String FIELD_DELIMITER = "|";

    //clock is used for testing of new reports creation for previous month.
    //systemUTC is default value used in Instant.now() under the cut
    private static Clock CLOCK = Clock.systemUTC();

    private static final DateTimeFormatter FILE_NAME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyyMMdd'T'HHmmss");

    private static final DateTimeFormatter CTL_DATE_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd");

    private static final Charset ENCODING = StandardCharsets.UTF_8;

    @Autowired
    private SDoHReportGenerator sDoHReportGenerator;

    @Autowired
    private SDoHWorkbookGenerator sDoHWorkbookGenerator;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private SdohReportLogDao sdohReportLogDao;

    @Autowired
    private SdohReportLogSpecificationGenerator sdohReportLogSpecificationGenerator;

    @Autowired
    private SdohReportRowDataDao sdohReportRowDataDao;

    @Override
    public Page<SdohReportLog> find(Long organizationId, PermissionFilter permissionFilter, Pageable pageable) {
        updateForPreviousMonths(organizationId);

        var hasAccess = sdohReportLogSpecificationGenerator.hasAccess(permissionFilter);
        var byOrganizationId = sdohReportLogSpecificationGenerator.byOrganizationId(organizationId);
        //also check if enabled?
        return sdohReportLogDao.findAll(byOrganizationId.and(hasAccess), pageable);
    }

    public void updateForPreviousMonths(Long organizationId) {
        var organization = organizationDao.getOne(organizationId);
        var orgZone = getOrganizationZoneId(organization);

        var now = Instant.now(CLOCK);

        var previousMonthLastDay = now.atZone(orgZone)
                .withDayOfMonth(1).minusDays(1).toInstant();

        var prevMonthStart = DateTimeUtils.atStartOfMonth(previousMonthLastDay, orgZone);
        var prevMonthEnd = DateTimeUtils.atDatabaseEndOfDay(previousMonthLastDay, orgZone);

        sdohReportLogDao.findTopByOrganizationIdOrderByPeriodEndDesc(organizationId)
                .ifPresentOrElse(
                        lastReportLog -> {
                            while (lastReportLog.getPeriodStart().isBefore(prevMonthStart) && lastReportLog.getPeriodEnd().isBefore(prevMonthEnd)) {
                                var nextMonthLastDayTime = lastReportLog.getPeriodStart()
                                        .atZone(orgZone)
                                        .plusMonths(2)
                                        .minusDays(1)
                                        .toInstant();
                                lastReportLog = new SdohReportLog();
                                lastReportLog.setPeriodStart(DateTimeUtils.atStartOfMonth(nextMonthLastDayTime, orgZone));
                                lastReportLog.setPeriodEnd(DateTimeUtils.atDatabaseEndOfDay(nextMonthLastDayTime, orgZone));
                                lastReportLog.setOrganization(organization);
                                sdohReportLogDao.save(lastReportLog);
                            }
                        },
                        () -> {
                            var lastReportLog = new SdohReportLog();
                            lastReportLog.setPeriodStart(prevMonthStart);
                            lastReportLog.setPeriodEnd(prevMonthEnd);
                            lastReportLog.setOrganization(organization);
                            sdohReportLogDao.save(lastReportLog);
                        }
                );
    }

    private ZoneId getOrganizationZoneId(Organization organization) {
        return Optional.ofNullable(organization.getSdohZoneId())
                .map(ZoneId::of)
                .orElse(ZoneId.systemDefault());
    }

    @Override
    public Pair<String, byte[]> getZip(Long sdohReportLogId, PermissionFilter permissionFilter) {
        var reportLog = sdohReportLogDao.getOne(sdohReportLogId);

        if (isSentToUHC(reportLog)) {
            return generateSentZip(reportLog, permissionFilter);
        } else {
            return generateNewZip(reportLog, permissionFilter);
        }
    }

    private Pair<String, byte[]> generateSentZip(SdohReportLog reportLog, PermissionFilter permissionFilter) {
        var restoredReport = sDoHReportGenerator.restoreSentReport(reportLog, permissionFilter);
        var zip = generateZip(restoredReport, reportLog.getLastZipDownloadAt(), reportLog);
        return zip;
    }

    private Pair<String, byte[]> generateNewZip(SdohReportLog reportLog, PermissionFilter permissionFilter) {
        var report = sDoHReportGenerator.generateReport(reportLog, permissionFilter);

        if (!isReportDataValid(report)) {
            throw new BusinessException(BusinessExceptionType.SDOH_DATA_ERROR);
        }

        var now = Instant.now();
        var zip = generateZip(report, now, reportLog);

        reportLog.setLastZipDownloadAt(now);
        reportLog.setLastZipDownloadSubmitterName(report.getSubmitterName());

        sdohReportLogDao.save(reportLog);

        saveRowData(reportLog, report);

        return zip;
    }

    private Pair<String, byte[]> generateZip(SDoHReport report, Instant when, SdohReportLog reportLog) {
        var organizationZoneId = getOrganizationZoneId(reportLog.getOrganization());

        var wb = sDoHWorkbookGenerator.generateWorkbook(report, organizationZoneId);

        var zonedWhen = when.atZone(organizationZoneId);
        var zip = new Pair<>(buildTotalZipFilename(report.getSubmitterName(), zonedWhen), generateZip(report, wb, zonedWhen));

        return zip;
    }

    private void saveRowData(SdohReportLog reportLog, SDoHReport report) {
        var rowData = sDoHReportGenerator.createRowData(report);
        rowData.forEach(data -> data.setSdohReportLog(reportLog));

        sdohReportRowDataDao.deleteAllBySdohReportLogId(reportLog.getId());
        sdohReportRowDataDao.saveAll(rowData);
    }

    @Override
    public Pair<String, byte[]> getExcel(Long sdohReportLogId, PermissionFilter permissionFilter) {
        var reportLog = sdohReportLogDao.getOne(sdohReportLogId);

        if (isSentToUHC(reportLog)) {
            return generateSentExcel(reportLog, permissionFilter);
        } else {
            return generateNewExcel(reportLog, permissionFilter);
        }
    }

    private Pair<String, byte[]> generateSentExcel(SdohReportLog reportLog, PermissionFilter permissionFilter) {
        var restoredReport = sDoHReportGenerator.restoreSentReport(reportLog, permissionFilter);
        return generateExcel(restoredReport, reportLog.getLastZipDownloadAt(), reportLog);
    }

    private Pair<String, byte[]> generateNewExcel(SdohReportLog reportLog, PermissionFilter permissionFilter) {
        var report = sDoHReportGenerator.generateReport(reportLog, permissionFilter);
        return generateExcel(report, Instant.now(), reportLog);
    }

    private Pair<String, byte[]> generateExcel(SDoHReport report, Instant when, SdohReportLog reportLog) {
        var orgZoneId = getOrganizationZoneId(reportLog.getOrganization());
        var wb = sDoHWorkbookGenerator.generateWorkbook(report, orgZoneId);

        return new Pair<>(buildExcelFilename(report.getSubmitterName(), when.atZone(orgZoneId)),
                createExcel(wb));
    }

    @Override
    public void markAsSentToUhc(Long sdohReportLogId, PermissionFilter permissionFilter) {
        var reportLog = sdohReportLogDao.findById(sdohReportLogId).orElseThrow();
        validateCanMarkAsSentToUhc(sdohReportLogId, permissionFilter);

        reportLog.setSentToUhcDatetime(Instant.now());
        sdohReportLogDao.save(reportLog);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateCanMarkAsSentToUhc(Long sdohReportLogId, PermissionFilter permissionFilter) {
        var reportLog = sdohReportLogDao.findById(sdohReportLogId).orElseThrow();
        validateCanMarkAsSentToUhc(reportLog, permissionFilter);
    }

    private void validateCanMarkAsSentToUhc(SdohReportLog reportLog, PermissionFilter permissionFilter) {
        if (isSentToUHC(reportLog)) {
            throw new BusinessException(BusinessExceptionType.SDOH_ALREADY_SENT);
        }

        var report = sDoHReportGenerator.generateReport(reportLog, permissionFilter);
        if (!isReportDataValid(report)) {
            throw new BusinessException(BusinessExceptionType.SDOH_DATA_ERROR);
        }

        if (reportLog.getLastZipDownloadAt() == null) {
            throw new BusinessException(BusinessExceptionType.SDOH_ZIP_NOT_DOWNLOADED);
        }
    }

    private boolean isSentToUHC(SdohReportLog reportLog) {
        return reportLog.getSentToUhcDatetime() != null;
    }

    private boolean isReportDataValid(SDoHReport report) {
        for (var row : report.getRows()) {
            if (!isReportRowValid(row)) {
                return false;
            }
        }
        return true;
    }

    private boolean isReportRowValid(SDoHRow row) {
        var descriptor = row.getRowDescriptor();

        if (!isValidValue(descriptor.getSubmitterName(), row.getSubmitterName())) {
            return false;
        }
        if (!isValidValue(descriptor.getMemberLastName(), row.getMemberLastName())) {
            return false;
        }
        if (!isValidValue(descriptor.getMemberFirstName(), row.getMemberFirstName())) {
            return false;
        }
        if (!isValidValue(descriptor.getMemberMiddleName(), row.getMemberMiddleName())) {
            return false;
        }
        if (!isValidValue(descriptor.getMemberDateOfBirth(), row.getMemberDateOfBirth())) {
            return false;
        }
        if (!isValidValue(descriptor.getMemberGender(), row.getMemberGender())) {
            return false;
        }
        if (!isValidValue(descriptor.getMemberAddress(), row.getMemberAddress())) {
            return false;
        }
        if (!isValidValue(descriptor.getMemberCity(), row.getMemberCity())) {
            return false;
        }
        if (!isValidValue(descriptor.getMemberState(), row.getMemberState())) {
            return false;
        }
        if (!isValidValue(descriptor.getMemberZipCode(), row.getMemberZipCode())) {
            return false;
        }
        if (!isValidValue(descriptor.getMemberHicn(), row.getMemberHicn())) {
            return false;
        }
        if (!isValidValue(descriptor.getMemberCardId(), row.getMemberCardId())) {
            return false;
        }
        if (!isValidValue(descriptor.getServiceDate(), row.getServiceDate())) {
            return false;
        }
        if (!isValidValue(descriptor.getIdentificationReferralFulfillment(), row.getIdentificationReferralFulfillment())) {
            return false;
        }
        if (!isValidValue(descriptor.getIcdOrMbrAttributionCode(), row.getIcdOrMbrAttributionCode())) {
            return false;
        }
        if (!isValidValue(descriptor.getReferralFulfillmentProgramAddress(), row.getReferralFulfillmentProgramAddress())) {
            return false;
        }
        if (!isValidValue(descriptor.getReferralFulfillmentProgramPhone(), row.getReferralFulfillmentProgramPhone())) {
            return false;
        }
        if (!isValidValue(descriptor.getRefFulProgramType(), row.getRefFulProgramType())) {
            return false;
        }
        if (!isValidValue(descriptor.getRefFulProgramSubtype(), row.getRefFulProgramSubtype())) {
            return false;
        }
        return true;
    }

    private boolean isValidValue(SdohFieldDescriptor fieldDescriptor, String value) {
        return !(fieldDescriptor.isRequired() && StringUtils.isEmpty(value));
    }

    private boolean isValidValue(SdohFieldDescriptor fieldDescriptor, Object value) {
        return !(fieldDescriptor.isRequired() && value == null);
    }

    private byte[] generateZip(SDoHReport report, Workbook wb, ZonedDateTime now) {
        try (
                var baos = new ByteArrayOutputStream();
                var zipOut = new ZipOutputStream(baos)
        ) {
            writeTrg(report.getSubmitterName(), zipOut);
            writeControl(report, zipOut, now);
            writeTxtZipData(report.getSubmitterName(), wb, zipOut, now);

            zipOut.close();
            return baos.toByteArray();
        } catch (IOException e) {
            logger.warn("Failed to create zip archive for UHC", e);
            throw new InternalServerException(InternalServerExceptionType.SDOH_IO_ERROR, e);
        }
    }

    //    private byte[] generateZip(SDoHReport report, Workbook wb, ZonedDateTime now) {
    //        var baos = new ByteArrayOutputStream();
    //        var zipOut = new ZipOutputStream(baos);
    //
    //        try {
    //            writeTrg(report.getSubmitterName(), zipOut);
    //            writeControl(report, zipOut, now);
    //            writeTxtZipData(report.getSubmitterName(), wb, zipOut, now);
    //
    //            zipOut.close();
    //            baos.close();
    //        } catch (IOException e) {
    //            logger.warn("Failed to create zip archive for UHC", e);
    //            throw new InternalServerException(InternalServerExceptionType.SDOH_IO_ERROR, e);
    //        }
    //
    //        return baos.toByteArray();
    //    }
    private void writeTrg(String submitterName, ZipOutputStream zipOut) throws IOException {
        ZipEntry zipEntry = new ZipEntry(buildTrgFilename(submitterName));
        zipOut.putNextEntry(zipEntry);

        writeString(zipOut, " ");
    }

    private void writeControl(SDoHReport report, ZipOutputStream zipOut, ZonedDateTime now) throws IOException {
        ZipEntry zipEntry = new ZipEntry(buildControlFilename(report.getSubmitterName(), now));
        zipOut.putNextEntry(zipEntry);

        //example - "lsa|LSA_SDOH_20200526T010101.txt|2020-05-26|20||||||"
        var controlData = report.getSubmitterName().toLowerCase() +
                FIELD_DELIMITER +
                buildTxtDataFilename(report.getSubmitterName(), now) +
                FIELD_DELIMITER +
                CTL_DATE_FORMATTER.format(now) +
                FIELD_DELIMITER +
                (report.getRows() == null ? 0 : report.getRows().size()) +
                FIELD_DELIMITER +
                FIELD_DELIMITER +
                FIELD_DELIMITER +
                FIELD_DELIMITER +
                FIELD_DELIMITER +
                FIELD_DELIMITER;

        writeString(zipOut, controlData);
    }

    private void writeString(OutputStream zipOut, String string) throws IOException {
        var bytes = string.getBytes(ENCODING);
        zipOut.write(bytes, 0, bytes.length);
    }

    private void writeTxtZipData(String submitterName, Workbook wb, ZipOutputStream zipOut, ZonedDateTime now) throws IOException {
        ZipEntry zipEntry = new ZipEntry(buildDataZipFilename(submitterName, now));
        zipOut.putNextEntry(zipEntry);

        var buffer = new ByteArrayOutputStream();
        var dataZipOut = new ZipOutputStream(buffer);
        writeTxtData(submitterName, wb, dataZipOut, now);
        buffer.close();
        dataZipOut.close();

        var bytes = buffer.toByteArray();
        zipOut.write(bytes, 0, bytes.length);
    }

    private void writeTxtData(String submitterName, Workbook wb, ZipOutputStream zipOut, ZonedDateTime now) throws IOException {
        ZipEntry zipEntry = new ZipEntry(buildTxtDataFilename(submitterName, now));
        zipOut.putNextEntry(zipEntry);

        var sheet = wb.getSheet(SDoHWorkbookGeneratorImpl.ALL_SHEET);
        writeAsCsv(sheet, zipOut);
    }

    private byte[] createExcel(Workbook wb) {
        try (
                var baos = new ByteArrayOutputStream()
        ) {
            wb.write(baos);

            return baos.toByteArray();
        } catch (IOException e) {
            logger.warn("Failed to create SDoH excel", e);
            throw new InternalServerException(InternalServerExceptionType.SDOH_IO_ERROR, e);
        }
    }

    private void writeAsCsv(Sheet sheet, OutputStream out) throws IOException {
        try (
                var osWriter = new OutputStreamWriter(out, ENCODING);
                var bufWriter = new BufferedWriter(osWriter)
        ) {
            var dataFormatter = new DataFormatter();

            var colCount = resolveColCountFromHeader(sheet);
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                var row = sheet.getRow(i);
                for (int j = 0; j < colCount - 1; j++) {
                    var value = getValue(row.getCell(j), dataFormatter);
                    bufWriter.append(value);
                    bufWriter.append(FIELD_DELIMITER);
                }

                if (colCount > 0) {
                    bufWriter.append(getValue(row.getCell(colCount - 1), dataFormatter));
                }
                bufWriter.append("\n");
            }

        }
    }

    private int resolveColCountFromHeader(Sheet sheet) {
        if (sheet.getLastRowNum() > 0) {
            return sheet.getRow(0).getLastCellNum();
        }
        return 0;
    }

    private String getValue(Cell cell, DataFormatter df) {
        return StringUtils.defaultString(df.formatCellValue(cell));
    }

    private String buildTotalZipFilename(String submitterName, ZonedDateTime now) {
        return buildBaseFileName(submitterName, now) + "_unzip_me.zip";
    }

    private String buildDataZipFilename(String submitterName, ZonedDateTime now) {
        return buildBaseFileName(submitterName, now) + ".zip";
    }

    private String buildTxtDataFilename(String submitterName, ZonedDateTime when) {
        return buildBaseFileName(submitterName, when) + ".txt";
    }

    private String buildExcelFilename(String submitterName, ZonedDateTime when) {
        return buildBaseFileName(submitterName, when) + ".xlsx";
    }

    private String buildControlFilename(String submitterName, ZonedDateTime when) {
        return buildBaseFileName(submitterName, when) + ".ctl";
    }

    private String buildBaseFileName(String submitterName, ZonedDateTime when) {
        return submitterName + "_SDOH_" + FILE_NAME_FORMATTER.format(when);
    }

    private String buildTrgFilename(String submitterName) {
        return submitterName + "_SDOH.trg";
    }

    public static void setCLOCK(Clock CLOCK) {
        SdohReportLogServiceImpl.CLOCK = CLOCK;
    }
}
