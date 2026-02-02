package com.scnsoft.eldermark.converter.fax;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.notification.BaseFaxNotificationDto;
import com.scnsoft.eldermark.dto.notification.event.ClientInfoNotificationDto;
import com.scnsoft.eldermark.exception.ApplicationException;
import com.scnsoft.eldermark.util.DataUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

public abstract class AbstractITextPdfFaxContentConverter<T extends BaseFaxNotificationDto> implements Converter<T, byte[]> {

    protected static final float BOTTOM_PADDING = 10f;
    protected static final float CONTENT_INDENTATION = 30f;
    private static final Logger logger = LoggerFactory.getLogger(AbstractITextPdfFaxContentConverter.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a z")
            .withZone(TimeZone.getTimeZone("CST6CDT").toZoneId());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(TimeZone.getTimeZone("CST6CDT").toZoneId());
    protected static Font HELVETICA_8_ITALIC = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC);
    protected static Font HELVETICA_12 = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    protected static Font HELVETICA_12_BOLD = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    protected static Font HELVETICA_14_BOLD = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    protected static Font HELVETICA_16_BOLD = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    protected static Font HELVETICA_17_BOLD = new Font(Font.FontFamily.HELVETICA, 17, Font.BOLD);
    protected static Font HELVETICA_37_BOLD = new Font(Font.FontFamily.HELVETICA, 37, Font.BOLD);
    private static String UNKNOWN_VALUE = "n/a";
    @Value("classpath:images/fax_header.png")
    private Resource header;

    private byte[] headerByteArray;

    @PostConstruct
    void postConstruct() throws IOException {
        headerByteArray = IOUtils.toByteArray(header.getInputStream());
    }


    @Override
    public byte[] convert(T faxDto) {
        try {
            return generateFaxContent(faxDto);
        } catch (DocumentException | IOException e) {
            logger.warn("Couldn't generate fax content", e);
            throw new ApplicationException("Couldn't generate fax content", e);
        }
    }

    private byte[] generateFaxContent(T faxDto) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4, 30, 30, 95, 40);

        // First pass: create document
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, buffer);

        document.open();
        document.add(createHeaderTable());
        createDocumentBody(document, faxDto);
        document.add(createFooterWarning());
        document.close();

        // Second pass: add the header and the footer
        PdfReader reader = new PdfReader(buffer.toByteArray());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, out);

        int n = reader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {
            createDocumentHeader(document, stamper.getOverContent(i));
            createDocumentFooter(document, stamper.getOverContent(i), i, n);
        }

        stamper.close();
        reader.close();

        //testing only
//        byte[] res = out.toByteArray();
//        FileOutputStream fos = new FileOutputStream("C:\\result\\result.pdf");
//        reader = new PdfReader(new ByteArrayInputStream(res));
//        stamper = new PdfStamper(reader, fos);
//        stamper.close();
//        fos.close();
//        return res;
        //end

        return out.toByteArray();
    }

    protected abstract void createDocumentBody(Document document, T faxDto) throws DocumentException, IOException;

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

    protected Paragraph subSubHeaderParagraph(String text) {
        final Paragraph p = new Paragraph();
        p.setFont(HELVETICA_12_BOLD);
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
        table.setWidths(new float[]{2f, 7f});
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
            p.add(new Phrase(showUnknownIfBlank(value), HELVETICA_12));

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
        return org.apache.commons.lang3.StringUtils.isEmpty(str) ? UNKNOWN_VALUE : str;
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

    protected void addDateTableRow(PdfPTable table, String label, TemporalAccessor date) {
        if (date != null) {
            addTableRow(table, label, DATE_FORMATTER.format(date), getBottomPadding());
        }
    }

    protected void addDateTableRow(PdfPTable table, String label, Long date) {
        if (date != null) {
            addDateTableRow(table, label, Instant.ofEpochMilli(date));
        }
    }

    protected void addDateTimeTableRow(PdfPTable table, String label, TemporalAccessor date) {
        if (date != null) {
            addTableRow(table, label, DATE_TIME_FORMATTER.format(date), getBottomPadding());
        }
    }

    protected void addDateTimeTableRow(PdfPTable table, String label, Long date) {
        if (date != null) {
            addDateTimeTableRow(table, label, Instant.ofEpochMilli(date));
        }
    }

    protected String formatDateTime(Long date) {
        if (date != null) {
            return DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(date));
        }
        return null;
    }

    protected void addTableRow(PdfPTable table, String label, Number text) {
        if (text != null) {
            addTableRow(table, label, text.toString(), getBottomPadding());
        }
    }

    protected void addTableRow(PdfPTable table, String label, String text) {
        addTableRow(table, label, text, getBottomPadding());
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

    protected PdfPTable createFaxDetailsTable(BaseFaxNotificationDto faxDto) {
        final PdfPTable table = createFullWidthTable(2);
        table.addCell(createCellWithTextField("TO:", faxDto.getReceiverFullName()));
        table.addCell(createCellWithTextField("FROM:", faxDto.getFrom()));
        table.addCell(createCellWithTextField("FAX:", faxDto.getFaxNumber()));
        table.addCell(createCellWithTextField("PHONE:", faxDto.getMobilePhone()));
        table.addCell(createCellWithTextField("DATE:", DATE_TIME_FORMATTER.format(faxDto.getDate()), 2));
        table.addCell(createCellWithTextField("RE:", faxDto.getSubject(), 2));
        return table;
    }


    protected void createDocumentHeader(Document document, PdfContentByte pdfContentByte) throws IOException, DocumentException {
        final Rectangle page = document.getPageSize();

        final PdfPTable header = new PdfPTable(1);
        header.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
        header.setLockedWidth(true);
        header.addCell(createImageCell(Arrays.copyOf(headerByteArray, headerByteArray.length)));

        header.writeSelectedRows(0, -1, document.leftMargin(), page.getHeight() - document.topMargin() + header.getTotalHeight() + 5, pdfContentByte);
    }

    protected void createDocumentFooter(Document document, PdfContentByte pdfContentByte, int currentPage, int totalPages) {
        Rectangle page = document.getPageSize();

        PdfPTable footer = new PdfPTable(1);
        PdfPCell nextPageCell = new PdfPCell(new Phrase(String.format("%s  (%d/%d)",
                currentPage == totalPages ? "END OF DOCUMENT" : "CONTINUED ON NEXT PAGE", currentPage, totalPages),
                HELVETICA_8_ITALIC));
        nextPageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        nextPageCell.setBorder(Rectangle.NO_BORDER);
        footer.addCell(nextPageCell);

        footer.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
        footer.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(), pdfContentByte);
    }

    protected PdfPTable createPatientInfoTable(ClientInfoNotificationDto info) throws DocumentException {
        final PdfPTable table = createContentTable();

        addTableRow(table, "Client name:", info.getFullName());
        addList(table, "Client alias", info.getAliases());
        addList(table, "Client Identifier", info.getIdentifiers());
        addTableRow(table, "Social Security Number:", info.getSsn());
        addTableRow(table, "Date of Birth:", info.getBirthDate());
        addTableRow(table, "Gender:", info.getGender());
        addTableRow(table, "Marital Status:", info.getMaritalStatus());
        addTableRow(table, "Primary Language:", info.getPrimaryLanguage());
        addTableRow(table, "Client Account Number:", info.getClientAccountNumber());
        addTableRow(table, "Race:", info.getRace());
        addTableRow(table, "Ethnic Group:", info.getEthnicGroup());
        addTableRow(table, "Nationality:", info.getNationality());
        addTableRow(table, "Religion:", info.getReligion());
        addList(table,"Citizenship", info.getCitizenships());
        addTableRow(table, "Veterans Military Status:", info.getVeteranStatus());
        addTableRow(table, "Home Phone Number:", info.getHomePhone());
        addTableRow(table, "Business Phone Number:", info.getBusinessPhone());
        addAddressTableRow(table, "Address:", info.getAddress());
        addTableRow(table, "Organization:", info.getOrganizationTitle());
        addTableRow(table, "Community:", info.getCommunityTitle());
        addDateTimeTableRow(table, "Death Date and Time:", info.getDeathDate());

        return table;
    }


    protected void addAddressTableRow(PdfPTable table, String label, AddressDto address) {
        if (address != null) {
            addTableRow(table, label, address.getDisplayAddress(), getBottomPadding());
        }
    }

    protected void addAddressList(PdfPTable table, String label, List<AddressDto> addresses) {
        if (!DataUtils.hasData(addresses)) {
            return;
        }
        var count = 1;
        for (var address : addresses) {
            addAddressTableRow(table, label + " #" + count++ + ":", address);
        }
    }

    protected float getBottomPadding() {
        return BOTTOM_PADDING;
    }

    protected void addList(PdfPTable table, String label, List<String> textList) {
        if (!DataUtils.hasData(textList)) {
            return;
        }
        var count = 1;
        for (var text : textList) {
            addTableRow(table, label + " #" + count++ + ":", text);
        }
    }
}
