package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.InPersonTimeReport;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.note.NoteSubType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.beans.reports.enums.ReportType.IN_PERSON_TIME;

@Service
@Transactional(readOnly = true)
public class InPersonTimeReportGenerator extends DefaultReportGenerator<InPersonTimeReport> {

    @Override
    public InPersonTimeReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new InPersonTimeReport();

        populateReportingCriteriaFields(filter, report);

        var encounterNotes = findEncounterNotes(filter, permissionFilter,
                NoteSubType.EncounterCode.FACE_TO_FACE_ENCOUNTER, Collections.singletonList(FACE_TO_FACE_VISIT_NOTE_TYPE_CODE));
        var nonFaceToFaceNotes = findEncounterNotes(filter, permissionFilter,
                NoteSubType.EncounterCode.NON_FACE_TO_FACE_ENCOUNTER, NON_FACE_TO_FACE_CODE_TYPES);

        var clientIdsByNoteIdMap = getClientIdsByEncounterNoteIdMap(Stream.concat(encounterNotes.stream(), nonFaceToFaceNotes.stream()).collect(Collectors.toList()));
        var clientNameAndCommunityByIdMap = getClientNameAndCommunityByIdMap(clientIdsByNoteIdMap);
        report.setFirstTabList(generateEncNoteFirstTabList(encounterNotes, clientNameAndCommunityByIdMap, clientIdsByNoteIdMap));
        report.setSecondTabList(generateEncNoteSecondTabList(encounterNotes, clientNameAndCommunityByIdMap, clientIdsByNoteIdMap));

        encounterNotes.addAll(nonFaceToFaceNotes);
        report.setTotalClientsTabList(generateTotalClientsTab(encounterNotes, clientNameAndCommunityByIdMap, clientIdsByNoteIdMap));
        report.setTotalServiceCoordinatorsTabList(generateTotalServiceCoordinatorsTab(encounterNotes, clientNameAndCommunityByIdMap, clientIdsByNoteIdMap));
        return report;
    }

    @Override
    public ReportType getReportType() {
        return IN_PERSON_TIME;
    }

}
