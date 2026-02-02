package com.scnsoft.eldermark.service.report.generator;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.filter.InternalReportFilter;
import com.scnsoft.eldermark.beans.reports.model.PhoneCallTimeReport;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.note.EncounterNote;
import com.scnsoft.eldermark.entity.note.NoteSubType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.beans.reports.enums.ReportType.PHONE_CALL_TIME;

@Service
@Transactional(readOnly = true)
public class PhoneCallTimeReportGenerator extends DefaultReportGenerator<PhoneCallTimeReport> {

    private static final NoteSubType.EncounterCode encounterCode = NoteSubType.EncounterCode.NON_FACE_TO_FACE_ENCOUNTER;

    public PhoneCallTimeReport generateReport(InternalReportFilter filter, PermissionFilter permissionFilter) {
        var report = new PhoneCallTimeReport();

        populateReportingCriteriaFields(filter, report);

        List<EncounterNote> encounterNoteList = findEncounterNotes(filter, permissionFilter, encounterCode, NON_FACE_TO_FACE_CODE_TYPES);
        List<EncounterNote> faceToFaceEncounterNotes = findEncounterNotes(filter, permissionFilter, NoteSubType.EncounterCode.FACE_TO_FACE_ENCOUNTER, Collections.singletonList(FACE_TO_FACE_VISIT_NOTE_TYPE_CODE));
        var clientIdsByNoteIdMap = getClientIdsByEncounterNoteIdMap(Stream.concat(encounterNoteList.stream(), faceToFaceEncounterNotes.stream()).collect(Collectors.toList()));
        var clientNameAndCommunityByIdMap = getClientNameAndCommunityByIdMap(clientIdsByNoteIdMap);
        report.setFirstTabList(generateEncNoteFirstTabList(encounterNoteList, clientNameAndCommunityByIdMap, clientIdsByNoteIdMap));
        report.setSecondTabList(generateEncNoteSecondTabList(encounterNoteList, clientNameAndCommunityByIdMap, clientIdsByNoteIdMap));

        encounterNoteList.addAll(faceToFaceEncounterNotes);
        report.setTotalClientsTabList(generateTotalClientsTab(encounterNoteList, clientNameAndCommunityByIdMap, clientIdsByNoteIdMap));
        report.setTotalServiceCoordinatorsTabList(generateTotalServiceCoordinatorsTab(encounterNoteList, clientNameAndCommunityByIdMap, clientIdsByNoteIdMap));
        return report;
    }

    @Override
    public ReportType getReportType() {
        return PHONE_CALL_TIME;
    }

}
