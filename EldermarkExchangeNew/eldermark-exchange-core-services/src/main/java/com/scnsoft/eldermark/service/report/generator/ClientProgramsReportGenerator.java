package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.ClientProgramsReport;
import com.scnsoft.eldermark.beans.reports.model.ClientProgramsRow;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.client.ClientNameAndCommunityAware;
import com.scnsoft.eldermark.entity.note.ClientProgramNoteAware;
import com.scnsoft.eldermark.service.ClientProgramNoteService;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ClientProgramsReportGenerator extends DefaultReportGenerator<ClientProgramsReport> {

    @Autowired
    private ClientProgramNoteService clientProgramNoteService;

    @Autowired
    private ClientService clientService;

    @Override
    public ClientProgramsReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new ClientProgramsReport();
        populateReportingCriteriaFields(filter, report);
        fillReport(filter, permissionFilter, report);
        return report;
    }

    @Override
    public ReportType getReportType() {
        return ReportType.CLIENT_PROGRAMS;
    }

    private void fillReport(InternalReportFilter filter, PermissionFilter permissionFilter, ClientProgramsReport report) {
        var notes = clientProgramNoteService.findAll(filter, permissionFilter);
        var rows = StreamUtils.stream(notes)
                .map(this::createClientProgramsRow)
                .flatMap(List::stream)
                .sorted(Comparator.comparing(ClientProgramsRow::getCommunityName).thenComparing(ClientProgramsRow::getClientName))
                .collect(Collectors.toList());
        report.setClientProgramsRows(rows);
    }

    private List<ClientProgramsRow> createClientProgramsRow(ClientProgramNoteAware note) {
        if (note.getClientId() != null) {
            var row = createBaseClientProgramsRow(note);
            row.setClientId(note.getClientId());
            row.setClientName(CareCoordinationUtils.getFullName(note.getClientFirstName(), note.getClientLastName()));
            row.setCommunityName(note.getClientCommunityName());
            return List.of(row);
        } else {
            var clients = clientService.findAllById(note.getNoteClientIds(), ClientNameAndCommunityAware.class);
            return StreamUtils.stream(clients)
                    .map(client -> {
                        var row = createBaseClientProgramsRow(note);
                        row.setClientId(client.getId());
                        row.setClientName(client.getFullName());
                        row.setCommunityName(client.getCommunityName());
                        return row;
                    })
                    .collect(Collectors.toList());
        }
    }

    private ClientProgramsRow createBaseClientProgramsRow(ClientProgramNoteAware note) {
        var row = new ClientProgramsRow();
        row.setProgramName(note.getClientProgramNoteTypeDescription());
        row.setServiceProvider(note.getServiceProvider());
        row.setStartDate(note.getStartDate());
        row.setEndDate(note.getEndDate());
        return row;
    }
}
