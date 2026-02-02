package com.scnsoft.eldermark.service.excel;

public interface EntityExportExcelGenerator<ENTITY, FILTER, EXPORT_DTO extends EntityExcelExportDto> {
    EXPORT_DTO exportToExcel(ENTITY entity, FILTER filter, Integer timeZoneOffset);
}
