package com.scnsoft.eldermark.services.carecoordination.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.scnsoft.eldermark.shared.carecoordination.PatientDto;
import com.scnsoft.eldermark.shared.carecoordination.service.FaxDto;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

public abstract class PdfGenerator {
    protected static Font HELVETICA_8_ITALIC = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC);
    protected static Font HELVETICA_8 = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
    protected static Font HELVETICA_12 = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    protected static Font HELVETICA_12_BOLD = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    protected static Font HELVETICA_12_BOLD_BLUE = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(70, 79, 129));
    protected static Font HELVETICA_14_BOLD = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    protected static Font HELVETICA_16_BOLD = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    protected static Font HELVETICA_16_BOLD_BLUE = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, new BaseColor(70, 79, 129));
    protected static Font HELVETICA_18_BOLD_BLUE = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(70, 79, 129));
    protected static Font HELVETICA_17_BOLD = new Font(Font.FontFamily.HELVETICA, 17, Font.BOLD);
    protected static Font HELVETICA_37_BOLD = new Font(Font.FontFamily.HELVETICA, 37, Font.BOLD);

    protected static String UNKNOWN_VALUE = "n/a";

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
    protected static SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a (z)");
    static{
        dateTimeFormatter.setTimeZone(TimeZone.getTimeZone("CST"));
    }

    protected static final float CONTENT_INDENTATION = 30f;

    protected Paragraph headerParagraph(String text) {
        return headerParagraph(text, 10f);
    }

    protected Paragraph headerParagraph(String text, float spacingAfter) {
        final Paragraph p = new Paragraph();
        p.setFont(HELVETICA_16_BOLD);
        p.add(text);

        p.setSpacingAfter(spacingAfter);
        return p;
    }

    protected Paragraph subHeaderParagraph(String text) {
        final Paragraph p = new Paragraph();
        p.setFont(HELVETICA_14_BOLD);
        p.add(text);
        p.setSpacingAfter(10f);
        return p;
    }

    protected PdfPTable createFullWidthTable(int numColumns) {
        final PdfPTable table = new PdfPTable(numColumns);
        table.setWidthPercentage(100);
        return table;
    }

    protected PdfPTable createContentTable() throws DocumentException {
        final PdfPTable table = createFullWidthTable(2);
        table.setWidths(new float[] {1f, 4f});
        return table;
    }

    protected void addIndentedTable(Document document, PdfPTable table, float leftIndentation, float spacingAfter) throws DocumentException {
        final Paragraph p = new Paragraph();
        p.add(table);
        p.setIndentationLeft(leftIndentation);
        p.setSpacingAfter(spacingAfter);
        document.add(p);
    }

    protected PdfPCell createImageCell(byte[] image) throws DocumentException, IOException {
        final PdfPCell cell = new PdfPCell(Image.getInstance(image), true);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    protected PdfPCell createCellWithTextField(String label, String value) {
        return createCellWithTextField(label, value, HELVETICA_12_BOLD);
    }

    protected PdfPCell createCellWithTextField(String label, String value, int columnSpan) {
        final PdfPCell cell = createCellWithTextField(label, value);
        cell.setColspan(columnSpan);
        return cell;
    }

    protected PdfPCell createCellWithTextField(String label, String value, Font labelFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setMinimumHeight(15);

        Paragraph p = new Paragraph();
        if (label != null && !label.isEmpty()) {
            Chunk labelChunk = new Chunk(label + "  ", labelFont);
            p.add(labelChunk);
            if (value != null) {
                p.add(new Phrase(showUnknownIfBlank(value), HELVETICA_12));
            }
            float indentation = labelChunk.getWidthPoint();
            p.setIndentationLeft(indentation);
            p.setFirstLineIndent(-indentation);
        } else {
            p.add(new Phrase(showUnknownIfBlank(value), HELVETICA_12));
        }
        cell.addElement(p);
        cell.setPaddingTop(5f);
        return cell;
    }

    protected String showUnknownIfBlank(String str) {
        return StringUtils.isEmpty(str) ? UNKNOWN_VALUE : str;
    }

    protected PdfPCell createLicenseCell() {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPaddingTop(20f);
        cell.setPaddingBottom(20f);

        Phrase p = new Phrase();
        p.add("Health Care information is personal and sensitive. This is being faxed to you after appropriate " +
                "authorization from the patient or under circumstances that do not require patient authorization. " +
                "You, the recipient, are obligated to maintain it in a safe, secure, and confidential manner. " +
                "Re-disclosure without additional consent as permitted by law is prohibited. Unauthorized re-disclosure " +
                "or failure to maintain confidentiality could subject you to penalties described in federal or state law.");
        cell.setPhrase(p);
        return cell;
    }

    protected Paragraph createFooterWarning() {
        final Paragraph p = new Paragraph();
        p.add(new Chunk("IMPORTANT WARNING: ", HELVETICA_12_BOLD));
        p.add(new Chunk("These documents are intended for the use of the person or entity to " +
                "which it is addressed and may contain information that is privileged and confidential, the " +
                "disclosure of which is governed by applicable law. If the reader of this message is not the " +
                "intended recipient, or the employee or agent responsible to deliver it to the intended recipient, you " +
                "are hereby notified that any dissemination, distribution, copying of this information is STRICTLY " +
                "PROHIBITED. If you have received this communication in error, please immediately notify us by " +
                "telephone and return this original message or destroy it.", HELVETICA_12));
        p.setSpacingBefore(10f);
        return p;
    }

    protected void addDateTableRow(PdfPTable table, String label, Date date) {
        if (date != null) {
            addTableRow(table, label, dateFormatter.format(date), 5f);
        }
    }

    protected void addDateTimeTableRow(PdfPTable table, String label, Date date) {
        if (date != null) {
            addTableRow(table, label, dateTimeFormatter.format(date), 5f);
        }
    }


    protected void addTableRow(PdfPTable table, String label, String text) {
        addTableRow(table, label, text, 5f);
    }

    protected void addTableRow(PdfPTable table, String label, String text, float bottomPadding) {
        if (StringUtils.isEmpty(text)) {
            return;
        }
        final PdfPCell labelCell = new PdfPCell(new Phrase(label, HELVETICA_12_BOLD));
        final PdfPCell textCell = new PdfPCell(new Phrase(text, HELVETICA_12));
        labelCell.setPaddingBottom(bottomPadding);
        textCell.setPaddingBottom(bottomPadding);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        labelCell.setBorder(Rectangle.NO_BORDER);
        textCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);
        table.addCell(textCell);
    }

    protected void addBooleanTableRow(PdfPTable table, String label, Boolean value) {
        if (value == null) {
            return;
        }
        addTableRow(table, label, value ? "Yes" : "No");
    }

    protected PdfPTable createHeaderTable() {
        PdfPTable table = createFullWidthTable(1);
        table.addCell(createLicenseCell());
        table.addCell(createFaxHeaderTextCell());
        return table;
    }

    private PdfPCell createFaxHeaderTextCell() {
        final PdfPCell faxHeaderTextCell = new PdfPCell();
        faxHeaderTextCell.setBorder(Rectangle.NO_BORDER);
        faxHeaderTextCell.setVerticalAlignment(Element.ALIGN_CENTER);
        faxHeaderTextCell.setPaddingBottom(10f);

        final Phrase faxHeaderTextPhrase = new Phrase();
        faxHeaderTextPhrase.add(new Phrase("FAX", HELVETICA_37_BOLD));
        faxHeaderTextPhrase.add(new Phrase("  —  Confidential Health Information Enclosed", HELVETICA_17_BOLD));
        faxHeaderTextCell.setPhrase(faxHeaderTextPhrase);

        return faxHeaderTextCell;
    }

    protected PdfPTable createFaxDetailsTable(FaxDto faxDto) {
        final PdfPTable table = createFullWidthTable(2);
        table.addCell(createCellWithTextField("TO:", faxDto.getTo()));
        table.addCell(createCellWithTextField("FROM:", faxDto.getFrom()));
        table.addCell(createCellWithTextField("FAX:", faxDto.getFax()));
        table.addCell(createCellWithTextField("PHONE:", faxDto.getPhone()));
        table.addCell(createCellWithTextField("DATE:", faxDto.getDate(), 2));
        table.addCell(createCellWithTextField("RE:", faxDto.getSubject(), 2));
        return table;
    }


    /*protected void createDocumentHeader(Document document, PdfContentByte pdfContentByte) throws IOException, DocumentException {
        final Rectangle page = document.getPageSize();

        final PdfPTable header = new PdfPTable(1);
        header.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
        header.setLockedWidth(true);
        header.addCell(createImageCell(Arrays.copyOf(headerByteArray, headerByteArray.length)));

        header.writeSelectedRows(0, -1, document.leftMargin(), page.getHeight() - document.topMargin() + header.getTotalHeight() + 5, pdfContentByte);
    }*/


    protected PdfPTable createPatientInfoTable(PatientDto patientDto) throws DocumentException {
        final PdfPTable table = createContentTable();

        addTableRow(table, "Patient Name:", patientDto.getDisplayName());
        addTableRow(table,"SSN:", patientDto.getSsn());
        addDateTableRow(table,"Date Birth:", patientDto.getBirthDate());
        addTableRow(table,"Gender:", patientDto.getGender());
        addTableRow(table,"Marital Status:", patientDto.getMaritalStatus());
        addTableRow(table,"Address:", patientDto.getAddress() != null ? patientDto.getAddress().getDisplayAddress() : null);
        addTableRow(table,"Organization:", patientDto.getOrganization());
        addTableRow(table,"Community:", patientDto.getCommunity());
        return table;
    }
}
