package com.scnsoft.eldermark.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.scnsoft.eldermark.dao.incident.*;
import com.scnsoft.eldermark.entity.incident.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Service
public class IncidentReportPdfGenerationServiceImpl implements IncidentReportPdfGenerationService {

    private static Font HELVETICA_10 = new Font(Font.FontFamily.HELVETICA, 10);
    private static Font HELVETICA_10_BOLD = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static Font HELVETICA_10_BOLD_WHITE = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
    private static Font HELVETICA_10_UNDERLINE = new Font(Font.FontFamily.HELVETICA, 10, Font.UNDERLINE);

    private static Font HELVETICA_9 = new Font(Font.FontFamily.HELVETICA, 9);
    private static Font HELVETICA_9_BOLD = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);

    private static SimpleDateFormat DATE_FORMATTER_MM_DD_YYYY_WITH_HYPHEN = new SimpleDateFormat("MM-dd-YYYY");
    private static SimpleDateFormat DATE_FORMATTER_MM_DD_YYYY = new SimpleDateFormat("MM/dd/YYYY");
    private static SimpleDateFormat TIME_FORMATTER_HH_mm_ss = new SimpleDateFormat("hh:mm aa");

    @Autowired
    private ClassMemberTypeDao classMemberTypeDao;

    @Autowired
    private IncidentPlaceTypeDao incidentPlaceTypeDao;

    @Autowired
    private IncidentTypeDao incidentTypeDao;

    @Autowired
    private IncidentReportDao incidentReportDao;

    @Autowired
    private IncidentTypeHelpDao incidentTypeHelpDao;

    @Autowired
    private IncidentReportAdditionalDataService incidentReportAdditionalDataService;

    @Override
    public void generatePdfReport(HttpServletResponse response, Long eventId, TimeZone timeZone)
            throws DocumentException, IOException {
        IncidentReport incidentReport = incidentReportDao.getByEvent_IdAndArchivedIsFalse(eventId);

        generatePdfReport(response, incidentReport, timeZone);
    }

    @Override
    public void generatePdfReport(HttpServletResponse response, IncidentReport incidentReport,
                                  TimeZone timeZone) throws DocumentException, IOException {
        setTimeZoneToFormatter(timeZone);
        Document document = new Document(PageSize.A4, 70, 70, 80, 48);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        Rectangle rectangle = new Rectangle(30, 30, 550, 800);
        writer.setBoxSize("rectangle", rectangle);
        IRHeaderAndFooterPdfPageEventHelper headerAndFooter = new IRHeaderAndFooterPdfPageEventHelper();
        writer.setPageEvent(headerAndFooter);
        document.open();
        createDocumentBody(document, incidentReport);
        document.close();
        setResponseContent(response, incidentReport, baos);

    }

    private void setTimeZoneToFormatter(TimeZone timeZone) {
        //todo MODIFICATION OF STATIC FIELD PER REQUEST, CONCURRENCY ISSUES! REIMPLEMENT PROPERLY.
        DATE_FORMATTER_MM_DD_YYYY_WITH_HYPHEN.setTimeZone(timeZone);
        DATE_FORMATTER_MM_DD_YYYY.setTimeZone(timeZone);
        TIME_FORMATTER_HH_mm_ss.setTimeZone(timeZone);
    }

    @Override
    public byte[] getPdfBytes(IncidentReport incidentReport) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4, 70, 70, 80, 48);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        Rectangle rectangle = new Rectangle(30, 30, 550, 800);
        writer.setBoxSize("rectangle", rectangle);
        IRHeaderAndFooterPdfPageEventHelper headerAndFooter = new IRHeaderAndFooterPdfPageEventHelper();
        writer.setPageEvent(headerAndFooter);
        document.open();
        createDocumentBody(document, incidentReport);
        document.close();
        return baos.toByteArray();
    }

    private void setResponseContent(HttpServletResponse response, IncidentReport incidentReport,
                                    ByteArrayOutputStream baos) throws IOException {
        String documentTitle = getPdfFileName(incidentReport);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment" + ";filename=\"" + documentTitle + "\"");
        FileCopyUtils.copy(new ByteArrayInputStream(baos.toByteArray()), response.getOutputStream());
    }

    @Override
    public String getPdfFileName(IncidentReport incidentReport) {
        return "Incident reporting form for " + incidentReport.getFirstName() + " " +
                incidentReport.getLastName() + " " + DATE_FORMATTER_MM_DD_YYYY_WITH_HYPHEN.format(incidentReport.getReportDate()) + ".pdf";
    }

    private void createDocumentBody(Document document, IncidentReport incidentReport)
            throws DocumentException, IOException {
        Paragraph paragraph = new Paragraph("Check one:", HELVETICA_10);
        document.add(paragraph);
        addClassMemberTypesContent(document, incidentReport);
        addIncidentReportBasicContent(document, incidentReport);
        addDiagnosisDetails(document, incidentReport);
        addMedicationsDetails(document, incidentReport);
        addParagraphLine(document);
        addAgencyContent(document, incidentReport);
        addParagraphLine(document);
        addIncidentInfomation(document, incidentReport);
        addIncidentPlaceDetails(document, incidentReport);

        document.newPage();

        addIndividualDetails(document, incidentReport);
        addIncidentEventDetails(document, incidentReport);
        addReportSummary(document, incidentReport);
    }

    private void addDiagnosisDetails(Document document, IncidentReport incidentReport) throws DocumentException {
        document.add(getParagraph("Current Diagnoses:", ""));
        java.util.List<String> diagnosisStrings = incidentReportAdditionalDataService.listProblemObservationStrings(incidentReport);

        for (int i = 0; i < diagnosisStrings.size(); ++i) {
            document.add(getParagraph(Integer.toString(i + 1), diagnosisStrings.get(i), ". "));
        }
    }

    private void addReportSummary(Document document, IncidentReport incidentReport)
            throws IOException, DocumentException {
        Paragraph paragraph = new Paragraph();
        Chunk chunk = new Chunk(
                "Was this incident caused by, or related to, the member's substance use or substance abuse disorder diagnosis?  ",
                HELVETICA_10);
        paragraph.add(chunk);
        chunk = new Chunk(getSelectedCheckboxInstance(incidentReport.getWasIncidentCausedBySubstance()), 0, 0);
        paragraph.add(chunk);
        chunk = new Chunk("Yes  ", HELVETICA_9);
        paragraph.add(chunk);
        chunk = new Chunk(getSelectedCheckboxInstance(incidentReport.getWasIncidentCausedBySubstance() != null
                ? !incidentReport.getWasIncidentCausedBySubstance()
                : Boolean.TRUE), 0, 0);
        paragraph.add(chunk);
        chunk = new Chunk("No", HELVETICA_9);
        paragraph.add(chunk);
        paragraph.setSpacingAfter(5f);
        document.add(paragraph);
        document.add(getParagraph("Narrative:", incidentReport.getNarrative()));
        document.add(getParagraph("Agency’s Response to Incident:",
                incidentReport.getAgencyResponseToIncident() != null ? incidentReport.getAgencyResponseToIncident()
                        : ""));
        addParagraphLine(document);

        document.add(getParagraph("Name/Title of Person Completing Report: " + incidentReport.getReportAuthor(),""));
        document.add(getParagraph("Date Completed:",  DATE_FORMATTER_MM_DD_YYYY.format(incidentReport.getReportCompletedDate())));

    }

    private void addIncidentEventDetails(Document document, IncidentReport incidentReport)
            throws IOException, DocumentException {
        PdfPTable table2 = new PdfPTable(new float[] { 26, 89, 17, 30 });
        table2.setSplitLate(false);
        table2.setWidthPercentage(116);
        table2.setSpacingAfter(7f);

        PdfPCell cell_t2 = new PdfPCell(new Paragraph("INCIDENT LEVELS AND EVENTS", HELVETICA_10_BOLD_WHITE));
        cell_t2.setColspan(4);
        cell_t2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell_t2.setBackgroundColor(BaseColor.BLACK);
        table2.addCell(cell_t2);
        cell_t2 = new PdfPCell();
        Paragraph paragraph = new Paragraph("Critical Incident Level", HELVETICA_9_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell_t2.addElement(paragraph);
        cell_t2.setBackgroundColor(new BaseColor(184, 204, 228));
        table2.addCell(cell_t2);

        cell_t2 = new PdfPCell();
        paragraph = new Paragraph("INCIDENT", HELVETICA_9_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell_t2.addElement(paragraph);
        paragraph = new Paragraph("Please check box that describes incident", HELVETICA_9);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell_t2.addElement(paragraph);
        cell_t2.setBackgroundColor(new BaseColor(184, 204, 228));
        table2.addCell(cell_t2);

        cell_t2 = new PdfPCell();
        paragraph = new Paragraph("Reporting Time Lines", HELVETICA_9_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell_t2.addElement(paragraph);
        cell_t2.setBackgroundColor(new BaseColor(184, 204, 228));
        cell_t2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell(cell_t2);

        cell_t2 = new PdfPCell();
        paragraph = new Paragraph("Follow-up Requirements", HELVETICA_9_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell_t2.addElement(paragraph);
        cell_t2.setBackgroundColor(new BaseColor(184, 204, 228));
        cell_t2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table2.addCell(cell_t2);

        cell_t2 = new PdfPCell();
        paragraph = new Paragraph();
        Chunk chunk = new Chunk("Level I – ", HELVETICA_9_BOLD);
        paragraph.add(chunk);
        chunk = new Chunk(" Urgent; Critical Incident", HELVETICA_9);
        paragraph.add(chunk);
        cell_t2.addElement(paragraph);
        table2.addCell(cell_t2);

        List parentList = new List(false, 20);
        parentList.setIndentationLeft(15f);
        cell_t2 = new PdfPCell();
        parentList = getIncidentList(incidentReport, parentList, incidentTypeDao.findByIncidentLevel(1));

        Phrase phrase = new Phrase();
        phrase.add(parentList);
        cell_t2.addElement(phrase);
        table2.addCell(cell_t2);

        IncidentTypeHelp incidentTypeHelp = incidentTypeHelpDao.findByIncidentLevel(1);

        cell_t2 = new PdfPCell(new Paragraph(incidentTypeHelp.getReportingTimelines(), HELVETICA_9));
        table2.addCell(cell_t2);

        cell_t2 = new PdfPCell(new Paragraph(incidentTypeHelp.getFollowupRequirements(), HELVETICA_9));
        table2.addCell(cell_t2);

        cell_t2 = new PdfPCell();
        paragraph = new Paragraph();
        chunk = new Chunk("Level II –", HELVETICA_9_BOLD);
        paragraph.add(chunk);
        chunk = new Chunk(" Serious; Reportable Incident", HELVETICA_9);
        paragraph.add(chunk);
        cell_t2.addElement(paragraph);
        table2.addCell(cell_t2);

        parentList = new List(false, 20);
        parentList.setIndentationLeft(15f);
        cell_t2 = new PdfPCell();
        parentList = getIncidentList(incidentReport, parentList, incidentTypeDao.findByIncidentLevel(2));

        phrase = new Phrase();
        phrase.add(parentList);
        cell_t2.addElement(phrase);
        table2.addCell(cell_t2);

        incidentTypeHelp = incidentTypeHelpDao.findByIncidentLevel(2);

        cell_t2 = new PdfPCell(new Paragraph(incidentTypeHelp.getReportingTimelines(), HELVETICA_9));
        table2.addCell(cell_t2);

        cell_t2 = new PdfPCell(new Paragraph(incidentTypeHelp.getFollowupRequirements(), HELVETICA_9));
        table2.addCell(cell_t2);

        cell_t2 = new PdfPCell();
        paragraph = new Paragraph();
        chunk = new Chunk("Level III –", HELVETICA_9_BOLD);
        paragraph.add(chunk);
        chunk = new Chunk(" Significant; Reportable Incident", HELVETICA_9);
        paragraph.add(chunk);
        cell_t2.addElement(paragraph);
        table2.addCell(cell_t2);


        cell_t2 = new PdfPCell();
        parentList = new List(false, 20);
        parentList.setIndentationLeft(15f);

        parentList = new List(false, 20);
        cell_t2 = new PdfPCell();
        parentList = getIncidentList(incidentReport, parentList, incidentTypeDao.findByIncidentLevel(3));

        phrase = new Phrase();
        phrase.add(parentList);
        cell_t2.addElement(phrase);
        table2.addCell(cell_t2);

        incidentTypeHelp = incidentTypeHelpDao.findByIncidentLevel(3);

        cell_t2 = new PdfPCell(new Paragraph(incidentTypeHelp.getReportingTimelines(), HELVETICA_9));
        table2.addCell(cell_t2);

        cell_t2 = new PdfPCell(new Paragraph(incidentTypeHelp.getFollowupRequirements(), HELVETICA_9));
        table2.addCell(cell_t2);

        table2.setSpacingBefore(7f);
        table2.setSpacingAfter(7f);

        document.add(table2);
    }

    private void addIndividualDetails(Document document, IncidentReport incidentReport)
            throws DocumentException, IOException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Chunk("RIN: ", HELVETICA_10_BOLD));
        String rin = StringUtils.isNotEmpty(incidentReport.getRin()) ? incidentReport.getRin() : "";
        paragraph.add(new Chunk(rin, HELVETICA_10));
        PdfPCell cell_table = new PdfPCell(paragraph);
        cell_table.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell_table);
        paragraph = new Paragraph();
        paragraph.add(new Chunk("NAME: ", HELVETICA_10_BOLD));
        String name = appendCommaStrings(incidentReport.getFirstName(), incidentReport.getLastName());
        paragraph.add(new Chunk(name, HELVETICA_10));
        cell_table = new PdfPCell(paragraph);
        cell_table.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell_table);
        table.setSpacingAfter(5f);
        document.add(table);

        java.util.List<Individual> listIndividuals = incidentReport.getIndividuals();
        boolean individualsInvolved = CollectionUtils.isNotEmpty(listIndividuals);

        paragraph = new Paragraph();
        paragraph.setSpacingAfter(5f);
        Chunk chunk = new Chunk(new VerticalPositionMark());
        Phrase ph = new Phrase();
        ph.add(new Chunk("Were other individuals involved in the incident?   ", HELVETICA_10_UNDERLINE));
        ph.add(chunk);
        Paragraph paragraph2 = new Paragraph();
        chunk = new Chunk(getSelectedCheckboxInstance(individualsInvolved), 0, 0);
        paragraph2.add(chunk);
        chunk = new Chunk("Yes  ", HELVETICA_10_UNDERLINE);
        paragraph2.add(chunk);
        chunk = new Chunk(getSelectedCheckboxInstance(!individualsInvolved), 0, 0);
        paragraph2.add(chunk);
        chunk = new Chunk("No_", HELVETICA_10_UNDERLINE);
        paragraph2.add(chunk);
        ph.add(paragraph2);
        paragraph.add(ph);
        document.add(paragraph);

        document.add(getParagraph(
                "If other individuals were involved, list the names, phone numbers and relationship to the participant___",
                ""));

        // 3 columns.
        PdfPTable table1 = new PdfPTable(new float[] { 14, 10, 14 });
        table1.setWidthPercentage(102);
        PdfPCell cell_t1 = new PdfPCell(new Paragraph("First and last name", HELVETICA_10));
        cell_t1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table1.addCell(cell_t1);
        cell_t1 = new PdfPCell(new Paragraph("Phone number", HELVETICA_10));
        cell_t1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table1.addCell(cell_t1);
        cell_t1 = new PdfPCell(new Paragraph("Relationship", HELVETICA_10));
        cell_t1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table1.addCell(cell_t1);

        int IndividualCount = 0;
        if (individualsInvolved) {
            for (Individual individual : listIndividuals) {
                IndividualCount++;
                String text = "";
                text = text + Integer.toString(IndividualCount) + ". ";
                paragraph = new Paragraph();
                chunk = new Chunk(text, HELVETICA_10_BOLD);
                paragraph.add(chunk);
                chunk = new Chunk(individual.getName(), HELVETICA_10);
                paragraph.add(chunk);
                cell_t1 = new PdfPCell(paragraph);
                cell_t1.setBorder(Rectangle.NO_BORDER);
                cell_t1.setPaddingLeft(40f);
                table1.addCell(cell_t1);
                cell_t1 = new PdfPCell(new Paragraph(individual.getPhone(), HELVETICA_10));
                cell_t1.setBorder(Rectangle.NO_BORDER);
                cell_t1.setPaddingLeft(30f);
                table1.addCell(cell_t1);
                cell_t1 = new PdfPCell(new Paragraph(individual.getRelationship(), HELVETICA_10));
                cell_t1.setBorder(Rectangle.NO_BORDER);
                cell_t1.setPaddingLeft(60f);
                table1.addCell(cell_t1);
            }
        }
        table1.setSpacingBefore(7f);
        table1.setSpacingAfter(7f);
        document.add(table1);

    }

    private Paragraph getEmptyParagraph() {
        Paragraph emptyParagraph = new Paragraph();
        Phrase emptyPhrase = new Phrase();
        emptyPhrase.add(new Chunk(""));
        emptyParagraph.add(emptyPhrase);
        return emptyParagraph;
    }

    private void addEmptyCellToTable(PdfPTable table) {
        PdfPCell cell = new PdfPCell(getEmptyParagraph());
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private void addIncidentPlaceDetails(Document document, IncidentReport incidentReport)
            throws DocumentException, IOException {
        document.add(getUnderlinedParagraph("Where did the incident take place?", ""));

        java.util.List<IncidentReportIncidentPlaceTypeFreeText> incidentReportIncidentPlaceTypeFreeTexts = incidentReport
                .getIncidentPlaceTypes();

        PdfPTable table = new PdfPTable(new float[] { 18, 14, 14 });
        table.setWidthPercentage(102);

        for (IncidentPlaceType incidentPlaceType : incidentPlaceTypeDao.findAll()) {
            boolean checked = false;
            IncidentReportIncidentPlaceTypeFreeText currentItem = null;
            for (IncidentReportIncidentPlaceTypeFreeText mappedItem : incidentReportIncidentPlaceTypeFreeTexts) {
                if (mappedItem.getIncidentPlaceType().getId().equals(incidentPlaceType.getId())) {
                    currentItem = mappedItem;
                    checked = true;
                }
            }

            if (incidentPlaceType.getName().contains("Other")) {
                addEmptyCellToTable(table);
            }

            Paragraph paragraph = new Paragraph();
            Chunk chunk = new Chunk(new VerticalPositionMark());
            Phrase ph = new Phrase();

            if (checked) {
                ph.add(new Chunk(Image.getInstance(getCheckedBox()), 0, 0));
            } else {
                ph.add(new Chunk(Image.getInstance(getUncheckedBox()), 0, 0));
            }

            ph.add(new Chunk(" " + incidentPlaceType.getName(), HELVETICA_10));
            if (currentItem != null && currentItem.getFreeText() != null) {
                ph.add(new Chunk(": ", HELVETICA_10));
                ph.add(new Chunk(currentItem.getFreeText().getFreeText(), HELVETICA_10_UNDERLINE));
            }
            ph.add(chunk);
            paragraph.add(ph);

            PdfPCell cell = new PdfPCell(paragraph);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            if (incidentPlaceType.getName().contains("Other")) {
                addEmptyCellToTable(table);
                addEmptyCellToTable(table);
            }
        }
        document.add(table);
    }

    private void addIncidentInfomation(Document document, IncidentReport incidentReport)
            throws DocumentException, IOException {
        Paragraph paragraph = new Paragraph("Incident Information", HELVETICA_10_BOLD);
        paragraph.setSpacingAfter(5f);
        document.add(paragraph);

        paragraph = new Paragraph();
        Chunk chunk = new Chunk(new VerticalPositionMark());
        Phrase ph = new Phrase();
        Paragraph main = new Paragraph();
        String dateOfIncident = StringUtils
                .isNotEmpty(DATE_FORMATTER_MM_DD_YYYY.format(incidentReport.getIncidentDatetime()))
                        ? DATE_FORMATTER_MM_DD_YYYY.format(incidentReport.getIncidentDatetime())
                        : "";
        ph.add(new Chunk("Date of Incident: " + dateOfIncident, HELVETICA_10));
        ph.add(chunk);
        TimeZone timeZone = TIME_FORMATTER_HH_mm_ss.getTimeZone();
        String offset = calculateOffset(timeZone.getRawOffset());
        String strTimeZone = String.format("(%s%s)", "UTC", offset);
        String timeOfIncident = StringUtils
                .isNotEmpty(TIME_FORMATTER_HH_mm_ss.format(incidentReport.getIncidentDatetime()))
                        ? TIME_FORMATTER_HH_mm_ss.format(incidentReport.getIncidentDatetime()) + " " + strTimeZone
                        : "";
        ph.add(new Chunk("Time of Incident: " + timeOfIncident, HELVETICA_10));
        main.add(ph);
        paragraph.add(main);
        paragraph.setSpacingAfter(5f);
        document.add(paragraph);

        document.add(getParagraph("Date incident discovered by agency staff: ",
                incidentReport.getIncidentDiscoveredDate() != null
                        ? DATE_FORMATTER_MM_DD_YYYY.format(incidentReport.getIncidentDiscoveredDate())
                        : ""));

        paragraph = new Paragraph();
        chunk = new Chunk("Did the incident occur when a provider was present or was scheduled to be present?  ",
                HELVETICA_10_UNDERLINE);
        paragraph.add(chunk);
        chunk = new Chunk(getSelectedCheckboxInstance(incidentReport.getWasProviderPresentOrScheduled()), 0, 0);
        paragraph.add(chunk);
        chunk = new Chunk("Yes  ", HELVETICA_10_UNDERLINE);
        paragraph.add(chunk);
        chunk = new Chunk(getSelectedCheckboxInstance(incidentReport.getWasProviderPresentOrScheduled() != null
                ? !incidentReport.getWasProviderPresentOrScheduled()
                : Boolean.TRUE), 0, 0);
        paragraph.add(chunk);
        chunk = new Chunk("No", HELVETICA_10_UNDERLINE);
        paragraph.add(chunk);
        paragraph.setSpacingAfter(5f);
        document.add(paragraph);

    }

    private String calculateOffset(int rawOffset) {
        if (rawOffset == 0) {
            return "+00:00";
        }
        long hours = TimeUnit.MILLISECONDS.toHours(rawOffset);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(rawOffset);
        minutes = Math.abs(minutes - TimeUnit.HOURS.toMinutes(hours));
        return String.format("%+03d:%02d", hours, Math.abs(minutes));
    }

    private void addAgencyContent(Document document, IncidentReport incidentReport) throws DocumentException {

        document.add(getParagraph("Agency Name and Address: ",
                appendCommaStrings(incidentReport.getAgencyName(), incidentReport.getAgencyAddress())));

        String qualityAdministrator = StringUtils.isNotEmpty(incidentReport.getQualityAdministrator())
                ? incidentReport.getQualityAdministrator()
                : "";
        document.add(getParagraph("Quality Administrator: ", qualityAdministrator));

        String careManagerOrStaffWithPrimServRespAndTitle = StringUtils
                .isNotEmpty(incidentReport.getCareManagerOrStaffWithPrimServRespAndTitle())
                ? incidentReport.getCareManagerOrStaffWithPrimServRespAndTitle()
                : "";
        document.add(getParagraph("Care Manager/Staff with Primary Service Responsibility and Title:",
                careManagerOrStaffWithPrimServRespAndTitle));

        String careManagerOrStaffPhoneAndEmail = appendCommaStrings(incidentReport.getCareManagerOrStaffPhone(),
                incidentReport.getCareManagerOrStaffEmail());
        document.add(getParagraph("Care Manager/Staff Person’s phone number and email address:",
                careManagerOrStaffPhoneAndEmail));

        String mcoCareCoordinatorAndAgency = StringUtils.isNotEmpty(incidentReport.getMcoCareCoordinatorAndAgency())
                ? incidentReport.getMcoCareCoordinatorAndAgency()
                : "";
        document.add(getParagraph("MCO Care Coordinator & Agency (Colbert only, if applicable):",
                mcoCareCoordinatorAndAgency));

        String mcoCareCoordinatorPhoneAndEmail = appendCommaStrings(incidentReport.getMcoCareCoordinatorPhone(),
                incidentReport.getMcoCareCoordinatorEmail());
        document.add(getParagraph("MCO Care Coordinator phone number/email address (Colbert only):",
                mcoCareCoordinatorPhoneAndEmail));

    }

    private String appendCommaStrings(String string1, String string2) {
        StringBuilder appendedString = new StringBuilder();
        if (StringUtils.isNotEmpty(string1)) {
            appendedString.append(string1);
        }
        if (StringUtils.isNotEmpty(string2)) {
            if (StringUtils.isNotEmpty(string1)) {
                appendedString.append(", ");
            }
            appendedString.append(string2);
        }
        return appendedString.toString();
    }

    private void addParagraphLine(Document document) throws DocumentException {
        Paragraph paragraphLine = new Paragraph(
                "_________________________________________________________________________________", HELVETICA_10);
        paragraphLine.setSpacingAfter(7f);
        document.add(paragraphLine);
    }

    private void addMedicationsDetails(Document document, IncidentReport incidentReport) throws DocumentException {
        document.add(getParagraph("Current/Active Medications (include dosage and frequency):", ""));
        java.util.List<String> medicationStrings = incidentReportAdditionalDataService.listMedicationStrings(incidentReport);

        for (int i = 0; i < medicationStrings.size(); ++i) {
            document.add(getParagraph(Integer.toString(i + 1), medicationStrings.get(i), ". "));
        }
    }

    private void addIncidentReportBasicContent(Document document, IncidentReport incidentReport)
            throws DocumentException {
        Chunk chunk;
        Paragraph paragraph = new Paragraph();
        chunk = new Chunk(new VerticalPositionMark());
        Phrase ph = new Phrase();
        Paragraph main = new Paragraph();
        String classMemberName = (StringUtils.isNotEmpty(incidentReport.getFirstName()) ? incidentReport.getFirstName()
                : "") + " "
                + (StringUtils.isNotEmpty(incidentReport.getLastName()) ? incidentReport.getLastName() : "");
        ph.add(new Chunk("Class Member’s Name: " + classMemberName, HELVETICA_10));
        ph.add(chunk);
        ph.add(new Chunk("Date of Report: " + DATE_FORMATTER_MM_DD_YYYY.format(incidentReport.getReportDate()),
                HELVETICA_10));
        main.add(ph);
        paragraph.add(main);
        paragraph.setSpacingBefore(10f);
        paragraph.setSpacingAfter(10f);
        document.add(paragraph);

        paragraph = new Paragraph();
        chunk = new Chunk(new VerticalPositionMark());
        ph = new Phrase();
        main = new Paragraph();
        String rin = StringUtils.isNotEmpty(incidentReport.getRin()) ? incidentReport.getRin() : "";
        ph.add(new Chunk("RIN: " + rin, HELVETICA_10));
        ph.add(chunk);
        String dob = StringUtils.isNotEmpty(DATE_FORMATTER_MM_DD_YYYY.format(incidentReport.getBirthDate()))
                ? DATE_FORMATTER_MM_DD_YYYY.format(incidentReport.getBirthDate())
                : "";
        ph.add(new Chunk("DOB: " + dob, HELVETICA_10));
        ph.add(chunk);
        String gender = "";
        if (incidentReport.getGender() != null && StringUtils.isNotEmpty(incidentReport.getGender().getDisplayName())) {
            gender = incidentReport.getGender().getDisplayName();
        }
        ph.add(new Chunk("Gender: " + gender, HELVETICA_10));
        ph.add(chunk);
        String race = "";
        if (incidentReport.getRace() != null && StringUtils.isNotEmpty(incidentReport.getRace().getName())) {
            race = incidentReport.getRace().getName();
        }
        ph.add(new Chunk("Race: " + race, HELVETICA_10));
        main.add(ph);
        paragraph.add(main);
        paragraph.setSpacingAfter(10f);
        document.add(paragraph);

        document.add(getParagraph("Date of Transition to Community:",
                incidentReport.getTransitionToCommunityDate() != null
                        ? DATE_FORMATTER_MM_DD_YYYY.format(incidentReport.getTransitionToCommunityDate())
                        : ""));

        document.add(getParagraph("Class Member’s Current Address:", incidentReport.getClassMemberCurrentAddress()));

    }

    private void addClassMemberTypesContent(Document document, IncidentReport incidentReport)
            throws IOException, DocumentException {
        Chunk chunk;
        ListItem listItem;
        List list = new List(false, 20);
        java.util.List<ClassMemberType> listClassMemberTypes = classMemberTypeDao.findAll();
        for (ClassMemberType item : listClassMemberTypes) {
            if (incidentReport.getClassMemberType().getId() == item.getId()) {
                list.setListSymbol(new Chunk(Image.getInstance(getCheckedBox()), 0, 0));
            } else {
                list.setListSymbol(new Chunk(Image.getInstance(getUncheckedBox()), 0, 0));
            }
            Paragraph paragraph = new Paragraph();
            chunk = new Chunk(item.getName(), HELVETICA_10);
            paragraph.add(chunk);
            listItem = new ListItem(paragraph);
            list.add(listItem);
        }
        document.add(list);
    }

    private Image getCheckedBox() throws BadElementException, IOException {
        Resource resource = new ClassPathResource("images/checked-checkbox.png");
        Image checkedBox = Image.getInstance(resource.getURL());
        checkedBox.scaleAbsolute(11, 11);
        checkedBox.setScaleToFitHeight(false);
        return checkedBox;
    }

    private Image getUncheckedBox() throws BadElementException, IOException {
        Resource resource = new ClassPathResource("images/unchecked-checkbox.png");
        Image uncheckedBox = Image.getInstance(resource.getURL());
        uncheckedBox.scaleAbsolute(11, 11);
        uncheckedBox.setScaleToFitHeight(false);
        return uncheckedBox;
    }

    private Image getSelectedCheckboxInstance(Boolean isChecked)
            throws BadElementException, IOException {
        if (BooleanUtils.isTrue(isChecked))
            return getCheckedBox();
        else
            return getUncheckedBox();
    }

    private List getIncidentList(IncidentReport incidentReport, List parentList,
            java.util.List<IncidentType> levelThreeIncidentTypes)
            throws BadElementException, IOException {
        java.util.List<IncidentReportIncidentTypeFreeText> incidentTypeSelectedList = incidentReport.getIncidentTypes();

        java.util.List<Long> allIncidentIds = new ArrayList<Long>();
        for (IncidentType item : levelThreeIncidentTypes) {
            boolean checked = false;
            IncidentReportIncidentTypeFreeText currentItem = null;
            for (IncidentReportIncidentTypeFreeText selectedItem : incidentTypeSelectedList) {
                if (selectedItem.getIncidentType().getId().equals(item.getId())) {
                    checked = true;
                    currentItem = selectedItem;
                }
            }
            if (!allIncidentIds.contains(item.getId())) {
                allIncidentIds.add(item.getId());

                if (checked) {
                    parentList.setListSymbol(new Chunk(Image.getInstance(getCheckedBox()), 0, 0));
                } else {
                    parentList.setListSymbol(new Chunk(Image.getInstance(getUncheckedBox()), 0, 0));
                }

                ListItem parentListItem = new ListItem(item.getName(), HELVETICA_9_BOLD);
                parentList.add(parentListItem);
                if (CollectionUtils.isNotEmpty(item.getChildIncidentTypes())) {
                    List childlist = new List(false, 20);
                    for (IncidentType subItem : item.getChildIncidentTypes()) {
                        allIncidentIds.add(subItem.getId());
                        checked = false;
                        currentItem = null;
                        for (IncidentReportIncidentTypeFreeText selectedItem : incidentTypeSelectedList) {
                            if (selectedItem.getIncidentType().getId().equals(subItem.getId())) {
                                checked = true;
                                currentItem = selectedItem;
                            }
                        }
                        if (checked) {
                            childlist.setListSymbol(new Chunk(Image.getInstance(getCheckedBox()), 0, 0));
                        } else {
                            childlist.setListSymbol(new Chunk(Image.getInstance(getUncheckedBox()), 0, 0));
                        }
                        if (currentItem != null && currentItem.getFreeText() != null) {
                            childlist.add(new ListItem(
                                    subItem.getName() + " " + currentItem.getFreeText().getFreeText(), HELVETICA_9));
                        } else {
                            childlist.add(new ListItem(subItem.getName(), HELVETICA_9));
                        }
                    }
                    parentList.add(childlist);
                }
            }
        }
        return parentList;
    }

    private Paragraph getParagraph(String text, String data, String separator) {
        Paragraph paragraph = new Paragraph(text + separator + data, HELVETICA_10);
        paragraph.setSpacingAfter(5f);
        return paragraph;
    }


    private Paragraph getParagraph(String text, String data) {
        return getParagraph(text, data, " ");
    }

    private Paragraph getUnderlinedParagraph(String text, String data) {
        Paragraph paragraph = new Paragraph(text + " " + data, HELVETICA_10_UNDERLINE);
        paragraph.setSpacingAfter(5f);
        return paragraph;

    }

    class IRHeaderAndFooterPdfPageEventHelper extends PdfPageEventHelper {

        public void onStartPage(PdfWriter pdfWriter, Document document) {
            Rectangle rect = pdfWriter.getBoxSize("rectangle");
            Font underlineFont = new Font(Font.FontFamily.HELVETICA, 10);
            underlineFont.setColor(98, 36, 35);
            Font underlineFont2 = new Font(Font.FontFamily.HELVETICA, 10);
            underlineFont2.setColor(168, 133, 132);
            Font font = new Font(Font.FontFamily.TIMES_ROMAN, 16);

            Chunk underline = new Chunk(
                    "_________________________________________________________________________________ ",
                    underlineFont);
            underline.setUnderline(2f, -2f);
            Paragraph paragraph = new Paragraph();
            paragraph.add(underline);

            underline = new Chunk("_________________________________________________________________________________ ",
                    underlineFont2);
            underline.setUnderline(2f, -2f);
            Paragraph paragraph2 = new Paragraph();
            paragraph2.add(underline);

            // TOP MEDIUM
            ColumnText.showTextAligned(pdfWriter.getDirectContent(), Element.ALIGN_CENTER,
                    new Paragraph("Colbert and Williams Consent Decrees", font), (rect.getRight() / 2) + 20,
                    rect.getTop(), 0);

            ColumnText.showTextAligned(pdfWriter.getDirectContent(), Element.ALIGN_CENTER,
                    new Paragraph("INCIDENT REPORTING FORM", font), (rect.getRight() / 2) + 20, rect.getTop() - 20, 0);

            ColumnText.showTextAligned(pdfWriter.getDirectContent(), Element.ALIGN_CENTER, paragraph2,
                    (rect.getRight() / 2) + 22, rect.getTop() - 25, 0);

            ColumnText.showTextAligned(pdfWriter.getDirectContent(), Element.ALIGN_CENTER, paragraph,
                    (rect.getRight() / 2) + 22, rect.getTop() - 27, 0);

        }

        public void onEndPage(PdfWriter pdfWriter, Document document) {
            Rectangle rect = pdfWriter.getBoxSize("rectangle");
            Font font = new Font(Font.FontFamily.HELVETICA, 10);
            Font underlineFont = new Font(Font.FontFamily.HELVETICA, 10);
            underlineFont.setColor(98, 36, 35);
            Font underlineFont2 = new Font(Font.FontFamily.HELVETICA, 10);
            underlineFont2.setColor(168, 133, 132);

            Chunk underline = new Chunk(
                    "_________________________________________________________________________________", underlineFont);
            underline.setUnderline(2f, -2f);
            Paragraph paragraph = new Paragraph();
            paragraph.add(underline);

            underline = new Chunk("_________________________________________________________________________________",
                    underlineFont2);
            underline.setUnderline(2f, -2f);
            Paragraph paragraph2 = new Paragraph();
            paragraph2.add(underline);

            ColumnText.showTextAligned(pdfWriter.getDirectContent(), Element.ALIGN_CENTER, paragraph2,
                    (rect.getRight() / 2) + 22, rect.getBottom() + 15, 0);
            ColumnText.showTextAligned(pdfWriter.getDirectContent(), Element.ALIGN_CENTER, paragraph,
                    (rect.getRight() / 2) + 22, rect.getBottom() + 17, 0);

            Calendar cal = Calendar.getInstance();
            String year = Integer.toString(cal.get(Calendar.YEAR));
            Formatter formatter = new Formatter();
            String month = (formatter.format("%tB", cal)).toString();
            formatter.close();

            // BOTTOM LEFT
            ColumnText.showTextAligned(pdfWriter.getDirectContent(), Element.ALIGN_CENTER,
                    new Paragraph("Revised: " + month + " " + year, font), rect.getLeft() + 89, rect.getBottom(), 0);

            String pageNo = "Page " + Integer.toString(document.getPageNumber());
            // BOTTOM RIGHT
            ColumnText.showTextAligned(pdfWriter.getDirectContent(), Element.ALIGN_CENTER, new Paragraph(pageNo, font),
                    rect.getRight() - 42, rect.getBottom(), 0);
        }
    }

}