package com.scnsoft.eldermark.converter.fax;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.scnsoft.eldermark.dto.notification.note.NoteDetailsNotificationDto;
import com.scnsoft.eldermark.dto.notification.note.NoteEncounterMailDto;
import com.scnsoft.eldermark.dto.notification.note.NoteFaxNotificationDto;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class NoteDetailsFaxContentConverter extends AbstractITextPdfFaxContentConverter<NoteFaxNotificationDto> {

    private static final DateTimeFormatter TIME_FROM_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a").withZone(TimeZone.getTimeZone("CST6CDT").toZoneId());
    private static final DateTimeFormatter TIME_TO_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a z").withZone(TimeZone.getTimeZone("CST6CDT").toZoneId());

    @Override
    protected void createDocumentBody(Document document, NoteFaxNotificationDto faxDto) throws DocumentException {
        final float contentTableIndentationAfter = 12f;

        addIndentedTable(document, createFaxDetailsTable(faxDto), CONTENT_INDENTATION, 15f);
        document.add(new LineSeparator());
        document.add(Chunk.NEWLINE);

        document.add(headerParagraph("Note Details"));

        if (!faxDto.getDetails().isGroupNote()) {
            document.add(subHeaderParagraph("Client Info"));
            addIndentedTable(document, createPatientInfoTable(faxDto.getDetails().getClientInfo()), CONTENT_INDENTATION, 10f);

            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);
        }

//        document.add(headerParagraph("Summary", 5f));
        document.add(subHeaderParagraph("Summary"));
        addIndentedTable(document, createNoteSummaryTable(faxDto.getDetails()), CONTENT_INDENTATION, contentTableIndentationAfter);

        if (faxDto.getDetails().getEncounter() != null) {
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);

            document.add(subHeaderParagraph("Encounter"));
            addIndentedTable(document, createEncounterTable(faxDto.getDetails().getEncounter()), CONTENT_INDENTATION, contentTableIndentationAfter);
        }

        document.add(new LineSeparator());
        document.add(Chunk.NEWLINE);

        document.add(subHeaderParagraph("Description"));
        addIndentedTable(document, createNoteDescriptionTable(faxDto.getDetails()), CONTENT_INDENTATION, contentTableIndentationAfter);

        //todo discuss if history is needed
//        if (!org.apache.commons.collections.CollectionUtils.isEmpty(noteDto.getHistoryNotes())) {
//            document.add(subHeaderParagraph("History"));
//            addIndentedTable(document, createNoteHistoryTable(noteDto.getHistoryNotes()), CONTENT_INDENTATION, contentTableIndentationAfter);
//        }
    }

    private PdfPTable createNoteSummaryTable(NoteDetailsNotificationDto note) throws DocumentException {
        final PdfPTable table = createContentTable();

        addTableRow(table, "Type:", note.getTypeTitle());
        addTableRow(table, "Subtype:", note.getSubTypeTitle());
        addTableRow(table, "Note name:", note.getNoteName());
        addDateTimeTableRow(table, "Admit date:", note.getAdmitDate());
        addTableRow(table, "Status:", note.getStatusTitle());

        if (note.getEventId() != null) {
            addTableRow(table, "Event", CareCoordinationUtils.concat(" ", note.getEventTypeTitle(), formatDateTime(note.getEventDate())));
        }

        addDateTimeTableRow(table, note.isNew() ? "Date Created:" : "Last modified date:", note.getLastModified());

        addTableRow(table, "Person submitting note:", note.getAuthor());
        addTableRow(table, "Role:", note.getAuthorRoleTitle());
        return table;
    }

    private PdfPTable createEncounterTable(NoteEncounterMailDto encounter) throws DocumentException {
        final PdfPTable table = createContentTable();
        addTableRow(table, "Person Completing the Encounter:", StringUtils.isNotEmpty(encounter.getClinicianTitle()) ?
                encounter.getClinicianTitle() : encounter.getOtherClinician());
        addDateTimeTableRow(table, "Encounter date:", encounter.getFromDate());

        if (ObjectUtils.allNotNull(encounter.getFromDate(), encounter.getToDate())) {
            var time = TIME_FROM_FORMATTER.format(Instant.ofEpochMilli(encounter.getFromDate())) + " - " +
                    TIME_TO_FORMATTER.format(Instant.ofEpochMilli(encounter.getToDate()));
            addTableRow(table, "Encounter time:", time);
        }

        addTableRow(table, "Total time spent:", encounter.getTotalTime());
        addTableRow(table, "Range:", encounter.getRange());
        addTableRow(table, "Units:", encounter.getUnits());
        return table;
    }

    private PdfPTable createNoteDescriptionTable(NoteDetailsNotificationDto note) throws DocumentException {
        final PdfPTable table = createContentTable();
        addTableRow(table, "Subjective:", note.getSubjective());
        addTableRow(table, "Objective:", note.getObjective());
        addTableRow(table, "Assessment:", note.getAssessment());
        addTableRow(table, "Plan:", note.getPlan());
        return table;
    }

//    private PdfPTable createNoteHistoryTable(List<RelatedNoteItemDto> history) throws DocumentException {
//        final PdfPTable table = createContentTable();
//        for (RelatedNoteItemDto relatedNoteItemDto: history) {
//            addTableRow(table,relatedNoteItemDto.getStatus(),
//                    "By " + relatedNoteItemDto.getPersonSubmittingNote() + ", " + relatedNoteItemDto.getRole()
//                            + " on " + dateTimeFormatter.format(relatedNoteItemDto.getLastModifiedDate()));
//        }
//        return table;
//    }
}
