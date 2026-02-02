package com.scnsoft.eldermark.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.DocumentReport;
import com.scnsoft.eldermark.entity.document.DocumentType;
import com.scnsoft.eldermark.entity.referral.ReferralRequest;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.MimeTypeConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
public class ReferralRequestPdfGenerationServiceImpl implements ReferralRequestPdfGenerationService {

    private static final Font TIMES_ROMAN_10 = new Font(Font.FontFamily.TIMES_ROMAN, 10f);
    private static final Font TIMES_ROMAN_10_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.BOLD);

    private static final Font TIMES_ROMAN_11 = new Font(Font.FontFamily.TIMES_ROMAN, 11);
    private static final Font TIMES_ROMAN_11_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);

    private static final Font TIMES_ROMAN_14 = new Font(Font.FontFamily.TIMES_ROMAN, 14f);
    private static final Font TIMES_ROMAN_14_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 14f, Font.BOLD);
    private static final Font TIMES_ROMAN_14_UNDERLINE = new Font(Font.FontFamily.TIMES_ROMAN, 14f, Font.UNDERLINE);

    private static final Font TIMES_ROMAN_16_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD);

    private static final DateTimeFormatter DATE_FORMATTER_MM_DD_YYYY_WITH_HYPHEN = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private static final float MARGIN_LEFT = 36f;
    private static final float MARGIN_RIGHT = 36f;
    private static final float MARGIN_TOP = 80f;
    private static final float MARGIN_BOTTOM = 170f;
    private static final float TOTAL_PAGES_BOTTOM_PADDING = 130f;
    private static final float MAX_LOGO_HEIGHT = 35f;

    @Value("classpath:images/simply-connect-logo.png")
    private Resource defaultLogo;

    @Autowired
    private ReferralAttachmentService referralAttachmentService;

    @Autowired
    private LogoService logoService;

    @Autowired
    private PdfService pdfService;

    private PdfPTable coverSheetContentTable = null;
    private float fieldWidth;

    @Override
    public DocumentReport generatePdfReport(ReferralRequest referralRequest, ZoneId zoneId) throws DocumentException, IOException {
        var document = new Document(PageSize.A4, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_TOP, MARGIN_BOTTOM);
        var baos = new ByteArrayOutputStream();
        var writer = PdfWriter.getInstance(document, baos);
        var rectangle = new Rectangle(MARGIN_LEFT, MARGIN_TOP, PageSize.A4.getRight() - MARGIN_RIGHT, PageSize.A4.getBottom() - MARGIN_BOTTOM);
        writer.setBoxSize("rectangle", rectangle);
        document.open();
        createDocumentBody(document, referralRequest, zoneId, writer);
        document.close();

        var reader = new PdfReader(baos.toByteArray());
        var out = new ByteArrayOutputStream();
        var stamper = new PdfStamper(reader, out);
        int totalPages = reader.getNumberOfPages();
        var community = referralRequest.getReferral().getClient().getCommunity();
        var logo = Optional.ofNullable(logoService.getLogoBytes(community)).orElse(IOUtils.toByteArray(defaultLogo.getInputStream()));
        for (int page = 1; page <= totalPages; page++) {
            addLogo(document, stamper.getOverContent(page), logo);
            addConfidentialInfo(document, stamper.getOverContent(page));
            ColumnText.showTextAligned(stamper.getOverContent(page), Element.ALIGN_RIGHT,
                    new Phrase(String.format("Page %d of %d", page, totalPages), TIMES_ROMAN_10), document.right(), document.bottom() - TOTAL_PAGES_BOTTOM_PADDING, 0);
            if (page == 1) {
                ColumnText.showTextAligned(stamper.getOverContent(page), Element.ALIGN_RIGHT,
                        new Phrase(String.valueOf(totalPages), TIMES_ROMAN_14_UNDERLINE), document.left() + fieldWidth, document.top() - coverSheetContentTable
                                .calculateHeights() + 2, 0);
            }
        }
        stamper.close();
        reader.close();

        var report = new DocumentReport();
        report.setDocumentTitle(getPdfFileName(referralRequest));
        report.setInputStream(new ByteArrayInputStream(out.toByteArray()));
        report.setMimeType(MediaType.APPLICATION_PDF_VALUE);
        report.setDocumentType(DocumentType.CUSTOM);
        return report;
    }

    private String getPdfFileName(ReferralRequest referralRequest) {
        return "Referral request for " + referralRequest.getReferral().getClient().getFullName() + ".pdf";
    }

    private void createDocumentBody(Document document, ReferralRequest referralRequest, ZoneId zoneId, PdfWriter writer) throws DocumentException, IOException {
        createCoverSheet(document, referralRequest, zoneId);
        createMainSheet(document, referralRequest, zoneId);
        addAttachments(document, referralRequest, writer);
    }

    private void createCoverSheet(Document document, ReferralRequest referralRequest, ZoneId zoneId) throws DocumentException {
        var contentTable = new PdfPTable(1);
        contentTable.setWidthPercentage(100f);
        var contentCell = new PdfPCell();
        contentCell.setBorder(Rectangle.NO_BORDER);
        contentCell.addElement(getPageTitle("Simply Connect Referral"));
        var coverSheet = new Paragraph("FAX Cover Sheet", TIMES_ROMAN_16_BOLD);
        coverSheet.setAlignment(Element.ALIGN_LEFT);
        coverSheet.setSpacingAfter(15f);
        contentCell.addElement(coverSheet);
        contentCell.addElement(getCoverSheetFieldWithContent("Date: ", DATE_FORMATTER_MM_DD_YYYY_WITH_HYPHEN.withZone(zoneId)
                .format(referralRequest.getReferral().getRequestDatetime()), false));
        contentCell.addElement(getCoverSheetFieldWithContent("To: ", referralRequest.getCommunity().getOrganization().getName() + ", " + referralRequest.getCommunity()
                .getName(), false));
        contentCell.addElement(getCoverSheetFieldWithContent("Fax Number: ", referralRequest.getSharedFax(), false));
        contentCell.addElement(getCoverSheetFieldWithContent("From: ", referralRequest.getReferral().getRequestingEmployee().getFullName() + ", " + referralRequest.getReferral()
                .getRequestingEmployee().getCommunity().getName(), false));
        var numberOfPages = "Number of pages (including cover sheet) ";
        contentCell.addElement(getCoverSheetFieldWithContent(numberOfPages, "", false));
        fieldWidth = new Chunk(numberOfPages, TIMES_ROMAN_14_BOLD).getWidthPoint();
        contentTable.addCell(contentCell);
        document.add(contentTable);
        coverSheetContentTable = contentTable;
        var phone = getCoverSheetFieldWithContent("If you do not receive a legible copy, please call ", referralRequest.getSharedPhone(), true);
        phone.setSpacingBefore(15f);
        phone.setSpacingAfter(30f);
        document.add(phone);
        var commentTable = new PdfPTable(1);
        commentTable.setWidthPercentage(100f);
        var comment = new Paragraph(referralRequest.getSharedFaxComment() != null ? referralRequest.getSharedFaxComment() : "", TIMES_ROMAN_14_BOLD);
        comment.setAlignment(Element.ALIGN_JUSTIFIED);
        var cell = new PdfPCell();
        cell.setMinimumHeight(300f);
        cell.addElement(comment);
        commentTable.addCell(cell);
        document.add(commentTable);
    }

    private void createMainSheet(Document document, ReferralRequest referralRequest, ZoneId zoneId) throws DocumentException {
        document.newPage();
        document.add(getPageTitle("Simply Connect Referral"));
        var mainTable = new PdfPTable(new float[]{1, 1});
        mainTable.setWidthPercentage(100f);
        var referral = referralRequest.getReferral();
        var pageWidth = document.right() - document.left();
        addTwoSplitCells(pageWidth, mainTable, "Request date", DateTimeUtils.formatDate(referral.getRequestDatetime(), zoneId),
                "Priority", referral.getPriority().getDisplayName(), 0);
        addOneMergedCell(pageWidth, mainTable, "Service", referral.getServiceName(), 0);
        addOneMergedCell(pageWidth, mainTable, "Client name", referral.getClient().getFullName(), 0);
        addOneMergedCell(pageWidth, mainTable, "Organization", referral.getClient().getOrganization().getName(), 0);
        addOneMergedCell(pageWidth, mainTable, "Community", referral.getClient().getCommunity().getName(), 0);
        addTwoSplitCells(pageWidth, mainTable, "Client location", referral.getClientLocation(),
                "Location phone", referral.getLocationPhone(), 0);
        addOneMergedCell(pageWidth, mainTable, "Location address", CareCoordinationUtils
                .concat(", ", referral.getAddress(), referral.getCity(), referral.getState() != null ? referral.getState().getName() : "", referral.getZipCode()), 0);
        addOneMergedCell(pageWidth, mainTable, "Insurer network", referral.getInNetworkInsurance(), 0);
        addOneMergedCell(pageWidth, mainTable, "Referring individual", referral.getReferringIndividual(), 0);
        addOneMergedCell(pageWidth, mainTable, "Referring community", referral.getRequestingEmployee().getCommunity().getName(), 0);
        addTwoSplitCells(pageWidth, mainTable, "Phone #", referral.getRequestingOrganizationPhone(),
                "Email", referral.getRequestingOrganizationEmail(), 0);
        addOneMergedCell(pageWidth, mainTable, "Referral instructions", referral.getReferralInstructions(), 5);
        document.add(mainTable);
    }

    private void addLogo(Document document, PdfContentByte overContent, byte[] logo) throws IOException, DocumentException {
        var header = new PdfPTable(1);
        var logoImage = Image.getInstance(logo);
        var scale = logoImage.getHeight() / MAX_LOGO_HEIGHT;
        logoImage.scaleToFit(logoImage.getWidth() / scale, logoImage.getHeight() / scale);
        logoImage.setAlignment(Element.ALIGN_CENTER);
        var cell = new PdfPCell();
        cell.addElement(logoImage);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        header.addCell(cell);
        header.setTotalWidth(document.right() - document.left());
        header.writeSelectedRows(0, -1, document.leftMargin(), document.top() + header.calculateHeights() + 10, overContent);
    }

    private Paragraph getPageTitle(String title) {
        var pageTitle = new Paragraph(title, TIMES_ROMAN_16_BOLD);
        pageTitle.setAlignment(Element.ALIGN_CENTER);
        pageTitle.setSpacingBefore(40f);
        pageTitle.setSpacingAfter(30f);
        return pageTitle;
    }

    private void addConfidentialInfo(Document document, PdfContentByte overContent) {
        var footer = new PdfPTable(1);
        footer.setPaddingTop(10f);
        var cell = new PdfPCell();
        cell.addElement(getConfidentialInfo());
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        footer.addCell(cell);
        footer.setTotalWidth(document.right() - document.left());
        footer.writeSelectedRows(0, -1, document.leftMargin(), document.bottom(), overContent);
    }

    private Paragraph getConfidentialInfo() {
        var notice = new Paragraph("Confidentiality Notice", TIMES_ROMAN_10_BOLD);
        notice.setAlignment(Element.ALIGN_LEFT);
        var content1 = new Chunk("The documents accompanying this transmission contain ", TIMES_ROMAN_10);
        var content2 = new Chunk("confidential", TIMES_ROMAN_10_BOLD);
        var content3 = new Chunk(" information, belonging to the sender that is legally privileged.  This information is intended only for the use of the individual or entity named above.  The authorized recipient of this information is prohibited from disclosing this information to any other party and is required to destroy the information after its stated need has been fulfilled, unless otherwise required by state or federal law.  If you are not the intended recipient, you are hereby notified that any disclosure, copying, distribution, or action taken in reliance on the contents of these documents is strictly prohibited.  If you have received these documents in error, please notify the sender.", TIMES_ROMAN_10);
        var content = new Paragraph();
        content.setAlignment(Element.ALIGN_JUSTIFIED);
        content.add(content1);
        content.add(content2);
        content.add(content3);
        var paragraph = new Paragraph();
        paragraph.add(notice);
        paragraph.add(content);
        return paragraph;
    }

    private Paragraph getCoverSheetFieldWithContent(String name, String content, boolean isBold) {
        var fieldName = new Chunk(name, isBold ? TIMES_ROMAN_14_BOLD : TIMES_ROMAN_14);
        var fieldContent = new Chunk(content != null ? content : "", isBold ? TIMES_ROMAN_14_BOLD : TIMES_ROMAN_14);
        var field = new Paragraph();
        field.setAlignment(Element.ALIGN_JUSTIFIED);
        field.add(fieldName);
        field.add(fieldContent);
        return field;
    }

    private void addTwoSplitCells(float pageWidth, PdfPTable table, String firstText, String firstData, String secondText, String secondData, int additionalRowCount) {
        addCellWithBottomBorder(table, getPhraseWithTitle(firstText, firstData));

        var needAddRowCount = additionalRowCount;
        var phrase = getPhraseWithTitle(secondText, secondData);
        var width = ColumnText.getWidth(phrase);
        if (pageWidth / 2 < width) {
            var phrases = splitDataByWidth(phrase, pageWidth / 2, secondText);
            needAddRowCount -= phrases.size() + 1;
            IntStream.range(0, phrases.size()).forEach(i -> {
                if (i == 0) {
                    addCellWithLeftAndBottomBorder(table, phrases.get(i));
                } else {
                    addMergedCellWithBottomBorder(table, phrases.get(i));
                }
            });
        } else {
            addCellWithLeftAndBottomBorder(table, getPhraseWithTitle(secondText, secondData));
        }

        addEmptyCells(table, needAddRowCount);
    }

    private void addCellWithBottomBorder(PdfPTable table, Phrase phrase) {
        var cell = new PdfPCell(phrase);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingBottom(5f);
        table.addCell(cell);
    }

    private Phrase getPhraseWithTitle(String title, String data) {
        var phrase = new Phrase();
        if (org.apache.commons.lang.StringUtils.isNotEmpty(title)) {
            phrase.add(new Chunk(title + ": ", TIMES_ROMAN_11_BOLD));
        }
        if (StringUtils.isNotEmpty(data)) {
            phrase.add(new Chunk(data, TIMES_ROMAN_11));
        }
        return phrase;
    }

    private List<Phrase> splitDataByWidth(Phrase phrase, float width, String data) {
        var startWidth = isPhraseWithTitle(phrase) ? phrase.getChunks().get(0).getWidthPoint() : 0;
        var words = phrase.getChunks().get(phrase.getChunks().size() - 1).getContent().split(" ");
        var result = new StringBuilder();
        var phrases = new ArrayList<Phrase>();
        for (var word : words) {
            var chunk = new Chunk(result + (result.length() > 0 ? " " : "") + word, TIMES_ROMAN_11);
            if (width >= (startWidth + chunk.getWidthPoint())) {
                result.append(result.length() > 0 ? " " : "").append(word);
            } else {
                if (phrases.isEmpty()) {
                    phrases.add(getPhraseWithTitle(isPhraseWithTitle(phrase) ? data : null, result.toString()));
                } else {
                    phrases.add(new Phrase(chunk));
                }
                startWidth = 0;
                result.delete(0, result.length());
                result.append(word);
            }
        }
        phrases.add(new Phrase(new Chunk(result.toString(), TIMES_ROMAN_11)));
        return phrases;
    }

    private boolean isPhraseWithTitle(Phrase phrase) {
        return phrase.getChunks().size() > 1 && phrase.getChunks().get(0).getFont().equals(TIMES_ROMAN_11_BOLD);
    }

    private void addCellWithLeftAndBottomBorder(PdfPTable table, Phrase phrase) {
        var cell = new PdfPCell(phrase);
        cell.setBorder(Rectangle.BOTTOM | Rectangle.LEFT);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingBottom(5f);
        table.addCell(cell);
    }

    private void addMergedCellWithBottomBorder(PdfPTable table, Phrase phrase) {
        var cell = new PdfPCell(phrase);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingBottom(5f);
        cell.setColspan(2);
        table.addCell(cell);
    }

    private void addEmptyCells(PdfPTable table, int needAddRowCount) {
        if (needAddRowCount > 0) {
            IntStream.range(0, needAddRowCount).forEach(i -> addMergedCellWithBottomBorder(table, new Phrase(" ", TIMES_ROMAN_11)));
        }
    }

    private void addOneMergedCell(float pageWidth, PdfPTable table, String text, String data, int additionalRowCount) {
        var needAddRowCount = additionalRowCount;
        var lines = Optional.ofNullable(data).map(d -> d.split("\n")).orElse(null);
        if (lines != null && lines.length > 1) {
            IntStream.range(0, lines.length).forEach(i -> {
                var title = i == 0 ? text : null;
                addOneMergedCell(pageWidth, table, title, lines[i], 0);
            });
            needAddRowCount -= lines.length + 1;
        } else {
            var phrase = getPhraseWithTitle(text, data);
            var width = ColumnText.getWidth(phrase);
            if (pageWidth < width) {
                var phrases = splitDataByWidth(phrase, pageWidth, text);
                needAddRowCount -= phrases.size() + 1;
                for (var ph : phrases) {
                    addMergedCellWithBottomBorder(table, ph);
                }
            } else {
                addMergedCellWithBottomBorder(table, phrase);
            }
        }

        addEmptyCells(table, needAddRowCount);
    }

    private void addAttachments(Document document, ReferralRequest referralRequest, PdfWriter writer) throws IOException, DocumentException {
        var attachments = referralRequest.getReferral().getAttachments();
        if (CollectionUtils.isEmpty(attachments)) {
            return;
        }

        for (var position = 0; position < attachments.size(); position++) {
            var attachment = referralAttachmentService.findByIdWithContent(attachments.get(position).getId());
            var appendix = "Simply Connect Referral: Appendix " + (char) (position + 65);
            if (MediaType.APPLICATION_PDF_VALUE.equals(attachment.getMimeType())) {
                addPdfAttachment(document, attachment.getContent(), writer, appendix);
            } else if (MimeTypeConstants.MS_WORD_DOC.equals(attachment.getMimeType()) ||
                    MimeTypeConstants.MS_WORD_DOCX.equals(attachment.getMimeType())) {
                addPdfAttachment(document, pdfService.convertWordToPdf(attachment.getContent()), writer, appendix);
            } else {
                addImageAttachment(document, attachment.getContent(), appendix);
            }
        }
    }

    private void addImageAttachment(Document document, byte[] bytes, String appendix) throws IOException, DocumentException {
        document.newPage();
        document.add(getPageTitle(appendix));
        var image = Image.getInstance(bytes);
        var maxImageWidth = document.right() - document.left();
        var maxImageHeight = document.top() - 70f - document.bottom();
        if (image.getWidth() > maxImageWidth || image.getHeight() > maxImageHeight) {
            image.scaleToFit(maxImageWidth, maxImageHeight);
        } else {
            image.setScaleToFitLineWhenOverflow(false);
        }
        document.add(image);
    }

    private void addPdfAttachment(Document document, byte[] bytes, PdfWriter writer, String appendix) throws IOException, DocumentException {
        var reader = new PdfReader(bytes);
        for (var pageNum = 1; pageNum <= reader.getNumberOfPages(); pageNum++) {
            document.newPage();
            document.add(getPageTitle(appendix));
            var page = writer.getImportedPage(reader, pageNum);
            var pageSize = reader.getPageSize(pageNum);
            var scale = getScale(document, pageSize.getWidth(), pageSize.getHeight());
            var cb = writer.getDirectContent();
            cb.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED), 10f);
            cb.addTemplate(page, scale, 0, 0, scale, 25f, MARGIN_BOTTOM);
        }
    }

    private float getScale(Document document, float width, float height) {
        float scaleX = (document.right() - document.left()) / width;
        float scaleY = (document.top() - 35f - document.bottom()) / height;
        return Math.min(scaleX, scaleY);
    }
}
