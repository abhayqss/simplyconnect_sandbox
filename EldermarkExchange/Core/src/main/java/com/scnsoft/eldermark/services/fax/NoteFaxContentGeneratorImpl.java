package com.scnsoft.eldermark.services.fax;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.scnsoft.eldermark.facades.carecoordination.PatientFacade;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteDto;
import com.scnsoft.eldermark.shared.carecoordination.notes.RelatedNoteItemDto;
import com.scnsoft.eldermark.shared.carecoordination.service.FaxDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service()
public class NoteFaxContentGeneratorImpl extends AbstractITextPdfFaxContentGenerator<NoteDto> implements NoteFaxContentGenerator {

    @Autowired
    private PatientFacade patientFacade;

    private static final float CONTENT_INDENTATION = 30f;

    protected void createDocumentBody(Document document, FaxDto faxDto, NoteDto noteDto) throws DocumentException, IOException {
        final float contentTableIndentationAfter = 12f;

        document.add(createHeaderTable());

        addIndentedTable(document, createFaxDetailsTable(faxDto), CONTENT_INDENTATION, 15f);
        document.add(new LineSeparator());
        document.add(Chunk.NEWLINE);

        document.add(headerParagraph("Patient Info"));
        addIndentedTable(document, createPatientInfoTable(noteDto.getPatientId()), CONTENT_INDENTATION, 10f);

        document.add(new LineSeparator());
        document.add(Chunk.NEWLINE);

        document.add(headerParagraph("Note Details", 5f));
        document.add(subHeaderParagraph("Summary"));
        addIndentedTable(document, createNoteSummaryTable(noteDto), CONTENT_INDENTATION, contentTableIndentationAfter);

        document.add(subHeaderParagraph("Description"));
        addIndentedTable(document, createNoteDescriptionTable(noteDto), CONTENT_INDENTATION, contentTableIndentationAfter);

        if (!org.apache.commons.collections.CollectionUtils.isEmpty(noteDto.getHistoryNotes())) {
            document.add(subHeaderParagraph("History"));
            addIndentedTable(document, createNoteHistoryTable(noteDto.getHistoryNotes()), CONTENT_INDENTATION, contentTableIndentationAfter);
        }

        document.add(createFooterWarning());
    }


    private PdfPTable createPatientInfoTable(Long patientId) throws DocumentException {
        return super.createPatientInfoTable(patientFacade.getPatientDto(patientId, true, false));
    }

    private PdfPTable createNoteSummaryTable(NoteDto note) throws DocumentException {
        final PdfPTable table = createContentTable();
        addTableRow(table,"Type:", note.getType());
        addTableRow(table,"Subtype:", note.getSubType().getLabel());
        addTableRow(table,"Status:", note.getStatus());
        if ("Created".equals(note.getStatus())) {
            addDateTimeTableRow(table,"Date Created:", note.getLastModifiedDate());
        }
        else {
            addDateTimeTableRow(table,"Modified Date:", note.getLastModifiedDate());
        }
        addTableRow(table,"Submitted by:", note.getPersonSubmittingNote());
        addTableRow(table,"Role:", note.getRole());
        return table;
    }

    private PdfPTable createNoteDescriptionTable(NoteDto note) throws DocumentException {
        final PdfPTable table = createContentTable();
        addTableRow(table,"Subjective:", note.getSubjective());
        addTableRow(table,"Objective:", note.getObjective());
        addTableRow(table,"Assesment:", note.getAssessment());
        addTableRow(table,"Plan:", note.getPlan());
        return table;
    }

    private PdfPTable createNoteHistoryTable(List<RelatedNoteItemDto> history) throws DocumentException {
        final PdfPTable table = createContentTable();
        for (RelatedNoteItemDto relatedNoteItemDto: history) {
            addTableRow(table,relatedNoteItemDto.getStatus(),
                    "By " + relatedNoteItemDto.getPersonSubmittingNote() + ", " + relatedNoteItemDto.getRole()
                            + " on " + dateTimeFormatter.format(relatedNoteItemDto.getLastModifiedDate()));
        }
        return table;
    }
}
