package com.scnsoft.eldermark.service.excel.appointments;

import com.scnsoft.eldermark.service.excel.EntityExcelExportDto;

public class AppointmentExcelExportDto extends EntityExcelExportDto {
    private String fileName;
    private byte[] file;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
