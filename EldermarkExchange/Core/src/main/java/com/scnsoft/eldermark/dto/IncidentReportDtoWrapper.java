package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.entity.Employee;

/**
 * This class is wrapper for IncidentReportDto to add eventId because it is path variable and not present
 * in IncidentReportDto itself and employee is currently authenticated user
 */
public class IncidentReportDtoWrapper {

    private IncidentReportDto incidentReportDto;
    private Long eventId;
    private Employee employee;

    public IncidentReportDtoWrapper() {
    }

    public IncidentReportDtoWrapper(IncidentReportDto incidentReportDto, Long eventId, Employee employee) {
        this.incidentReportDto = incidentReportDto;
        this.eventId = eventId;
        this.employee = employee;
    }

    public IncidentReportDto getIncidentReportDto() {
        return incidentReportDto;
    }

    public void setIncidentReportDto(IncidentReportDto incidentReportDto) {
        this.incidentReportDto = incidentReportDto;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
