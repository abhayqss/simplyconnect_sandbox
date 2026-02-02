package com.scnsoft.eldermark.service.report.appointment;

import com.scnsoft.eldermark.dto.appointment.ClientAppointmentExportDto;
import org.apache.poi.ss.usermodel.Workbook;


public interface AppointmentWorkBookGenerator {
    Workbook generateWorkBook(ClientAppointmentExportDto dto);
}
