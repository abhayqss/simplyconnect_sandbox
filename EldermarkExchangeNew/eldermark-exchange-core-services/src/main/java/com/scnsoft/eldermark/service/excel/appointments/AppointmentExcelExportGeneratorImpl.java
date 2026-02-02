package com.scnsoft.eldermark.service.excel.appointments;

import com.scnsoft.eldermark.beans.ClientAppointmentFilter;
import com.scnsoft.eldermark.beans.projection.NamesAware;
import com.scnsoft.eldermark.dto.appointment.*;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentNotificationMethod;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentReminder;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentServiceCategory;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.excel.EntityExportExcelGenerator;
import com.scnsoft.eldermark.service.report.appointment.AppointmentWorkBookGenerator;
import com.scnsoft.eldermark.service.report.workbook.ExcelUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.utils.NameUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AppointmentExcelExportGeneratorImpl
        implements EntityExportExcelGenerator<List<ClientAppointmentExportAware>, ClientAppointmentFilter, AppointmentExcelExportDto> {

    @Autowired
    private AppointmentWorkBookGenerator workBookGenerator;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public AppointmentExcelExportDto exportToExcel(
            List<ClientAppointmentExportAware> clientAppointments, ClientAppointmentFilter filter, Integer timeZoneOffset
    ) {
        var rows = createAppointmentRows(clientAppointments);

        var appointmentDto = new ClientAppointmentExportDto();
        appointmentDto.setRows(rows);
        appointmentDto.setTimeZoneOffset(timeZoneOffset);

        var wb = workBookGenerator.generateWorkBook(appointmentDto);
        var excelExportDto = new AppointmentExcelExportDto();
        excelExportDto.setFileName(buildFileName(filter, timeZoneOffset));
        excelExportDto.setFile(ExcelUtils.createExcel(wb));
        return excelExportDto;
    }

    private List<ClientAppointmentExportRow> createAppointmentRows(List<ClientAppointmentExportAware> clientAppointments) {
        var clientAppointmentByOrganization = new LinkedHashMap<Long, List<ClientAppointmentExportAware>>();

        clientAppointments.forEach(appointment -> clientAppointmentByOrganization.computeIfAbsent(
                        appointment.getClientOrganizationId(),
                        organizationId -> new ArrayList<>()
                )
                .add(appointment));

        return clientAppointmentByOrganization.values().stream()
                .map(this::createAppointmentRow)
                .collect(Collectors.toList());
    }

    private ClientAppointmentExportRow createAppointmentRow(List<ClientAppointmentExportAware> clientAppointments) {
        var row = new ClientAppointmentExportRow();
        row.setOrganizationName(clientAppointments.get(0).getClientOrganizationName());

        var appointmentsByCommunity = new LinkedHashMap<Long, List<ClientAppointmentExportAware>>();
        clientAppointments.forEach(appointment -> appointmentsByCommunity.computeIfAbsent(
                        appointment.getClientCommunityId(),
                        communityId -> new ArrayList<>()
                )
                .add(appointment));

        var communityRows = appointmentsByCommunity.values().stream()
                .map(this::createAppointmentCommunityRow)
                .collect(Collectors.toList());

        row.setCommunityRows(communityRows);
        return row;
    }

    private ClientAppointmentExportCommunityRow createAppointmentCommunityRow(List<ClientAppointmentExportAware> clientAppointments) {
        var row = new ClientAppointmentExportCommunityRow();
        row.setCommunityName(clientAppointments.get(0).getClientCommunityName());
        var clientRows = clientAppointments.stream()
                .map(this::createAppointmentClientRow)
                .collect(Collectors.toList());
        row.setClientRows(clientRows);
        return row;
    }

    private ClientAppointmentExportClientRow createAppointmentClientRow(ClientAppointmentExportAware clientAppointment) {
        var clientRow = new ClientAppointmentExportClientRow();

        clientRow.setClientName(clientAppointment.getClientFullName());
        var clientReminders = Optional.ofNullable(clientAppointment.getReminders())
                .map(Collection::stream)
                .orElse(Stream.empty())
                .filter(Objects::nonNull)
                .map(ClientAppointmentReminder::getDisplayName)
                .collect(Collectors.joining(", "));

        clientRow.setClientReminders(clientReminders);
        clientRow.setCreator(
                NameUtils.getFullName(clientAppointment.getCreatorFirstName(), clientAppointment.getCreatorLastName())
        );
        clientRow.setAppointmentDate(clientAppointment.getDateFrom());
        clientRow.setAppointmentStatus(clientAppointment.getStatus().getDisplayName());
        clientRow.setAppointmentTitle(clientAppointment.getTitle());
        clientRow.setAppointmentType(clientAppointment.getType().getDisplayName());
        clientRow.setCellPhone(clientAppointment.getPhone());
        clientRow.setDateFrom(clientAppointment.getDateFrom());
        clientRow.setDateTo(clientAppointment.getDateTo());
        clientRow.setEmail(clientAppointment.getEmail());
        clientRow.setLocation(clientAppointment.getLocation());
        clientRow.setNotes(clientAppointment.getNotes());
        var notificationMethods = Optional.ofNullable(clientAppointment.getNotificationMethods())
                .map(Collection::stream)
                .orElse(Stream.empty())
                .filter(Objects::nonNull)
                .map(ClientAppointmentNotificationMethod::getDisplayName)
                .collect(Collectors.joining(", "));
        clientRow.setNotificationMethods(notificationMethods);
        clientRow.setDirectionsInstructions(clientAppointment.getDirectionsInstructions());
        clientRow.setReferralSource(clientAppointment.getReferralSource());
        clientRow.setStartTime(clientAppointment.getDateFrom());
        clientRow.setEndTime(clientAppointment.getDateTo());
        clientRow.setServiceCategory(Optional.of(clientAppointment)
                .map(ClientAppointmentExportAware::getServiceCategory)
                .map(ClientAppointmentServiceCategory::getDisplayName)
                .orElse(null));

        var serviceProviders = employeeService.findAllById(clientAppointment.getServiceProviderIds(), NamesAware.class);
        List<String> serviceProviderNames = new ArrayList<>();
        if (BooleanUtils.isTrue(clientAppointment.getIsExternalProviderServiceProvider())) {
            serviceProviderNames.add("External Provider");
        }
        serviceProviderNames.addAll(
                Optional.ofNullable(serviceProviders)
                        .map(Collection::stream)
                        .orElse(Stream.empty())
                        .filter(Objects::nonNull)
                        .map(NamesAware::getFullName)
                        .collect(Collectors.toList()));

        clientRow.setServiceProviders(String.join(", ", serviceProviderNames));
        clientRow.setReasonForVisit(clientAppointment.getReasonForVisit());
        clientRow.setClientStatus(resolveClientStatus(clientAppointment.getClientActive()));

        return clientRow;
    }

    private String resolveClientStatus(Boolean clientActive) {
        if (clientActive == null) {
            return "Unknown";
        }
        return BooleanUtils.isTrue(clientActive) ? "Active" : "Inactive";
    }

    public String buildFileName(ClientAppointmentFilter filter, Integer timeZoneOffset) {
        return "Appointments_" + DateTimeUtils.formatDate(filter.getDateFrom(), timeZoneOffset)
                + "--" + DateTimeUtils.formatDate(filter.getDateTo(), timeZoneOffset) + ".xlsx";
    }
}
