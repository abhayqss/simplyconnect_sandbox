package com.scnsoft.eldermark.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.scnsoft.eldermark.entity.IncidentReportNotificationDestination;
import com.scnsoft.eldermark.entity.document.DocumentReport;
import com.scnsoft.eldermark.entity.document.DocumentType;
import com.scnsoft.eldermark.entity.event.incident.*;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class IncidentReportPdfGenerationServiceImpl implements IncidentReportPdfGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(IncidentReportPdfGenerationServiceImpl.class);

    private static Font TIMES_ROMAN_10_UNDERLINE = new Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.UNDERLINE);

    private static Font TIMES_ROMAN_10_5 = new Font(Font.FontFamily.TIMES_ROMAN, 10.5f);

    private static Font TIMES_ROMAN_11 = new Font(Font.FontFamily.TIMES_ROMAN, 11);
    private static Font TIMES_ROMAN_11_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);
    private static Font TIMES_ROMAN_11_ITALIC = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
    private static Font TIMES_ROMAN_11_BOLD_ITALIC = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD | Font.ITALIC);

    private static Font TIMES_ROMAN_12_BOLD_UNDERLINE = new Font(Font.FontFamily.TIMES_ROMAN, 10.5f, Font.BOLD | Font.UNDERLINE);

    private static DateTimeFormatter DATE_FORMATTER_MM_DD_YYYY_WITH_HYPHEN = DateTimeFormatter.ofPattern("MM-dd-YYYY");

    @Value("images/body_diagram.jpg")
    private String bodyDiagramPath;

    @Value("images/cross.png")
    private String crossImagePath;

    @Value("images/checked-checkbox.png")
    private String checkedCheckboxPath;

    @Value("images/unchecked-checkbox.png")
    private String uncheckedCheckboxPath;

    @Autowired
    private IncidentPictureService incidentPictureService;

    @Autowired
    private LogoService logoService;

    @Override
    public DocumentReport generatePdfReport(IncidentReport incidentReport, ZoneId zoneId) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4, 36f, 36f, 18f, 18f);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        Rectangle rectangle = new Rectangle(36f, 18f, 559f, 824f);
        writer.setBoxSize("rectangle", rectangle);
        document.open();
        addLogo(document, incidentReport);
        createDocumentBody(document, incidentReport, zoneId, writer);
        document.close();

        PdfReader reader = new PdfReader(baos.toByteArray());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, out);
        stamper.close();
        reader.close();

        var report = new DocumentReport();
        report.setDocumentTitle(getPdfFileName(incidentReport, zoneId));
        report.setInputStream(new ByteArrayInputStream(out.toByteArray()));
        report.setMimeType(MediaType.APPLICATION_PDF_VALUE);
        report.setDocumentType(DocumentType.CUSTOM);
        return report;
    }

    public String getPdfFileName(IncidentReport incidentReport, ZoneId zoneId) {
        return "Incident reporting form for " + incidentReport.getFirstName() + " " +
                incidentReport.getLastName() + " " + DATE_FORMATTER_MM_DD_YYYY_WITH_HYPHEN.withZone(zoneId).format(incidentReport.getReportDate()) + ".pdf";
    }

    private void addLogo(Document document, IncidentReport incidentReport) throws IOException, DocumentException {
        var logoTable = new PdfPTable(1);
        logoTable.setTotalWidth(document.right() - document.left());
        logoTable.setLockedWidth(true);
        var logoBytes = logoService.getLogoBytes(incidentReport.getEvent().getClient().getCommunity());
        if (logoBytes == null) {
            return;
        }
        var logo = Image.getInstance(logoBytes);
        logo.scaleToFit(200f, 70f);
        var cell = new PdfPCell(logo);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(10f);
        logoTable.addCell(cell);
        document.add(logoTable);
    }

    private void createDocumentBody(Document document, IncidentReport incidentReport, ZoneId zoneId, PdfWriter writer) throws DocumentException, IOException {
        document.add(getConfidentialInfo(true));
        addTitle(document);
        addReportData(document, incidentReport, zoneId, writer);
    }

    private void addConfidentialInfo(Document document, PdfWriter writer) {
        var footer = new PdfPTable(1);
        footer.setPaddingTop(10f);
        var cell = new PdfPCell();
        cell.addElement(getConfidentialInfo(false));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        footer.addCell(cell);
        footer.setTotalWidth(document.right() - document.left());
        footer.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin() + footer.calculateHeights(), writer.getDirectContent());
    }

    private Paragraph getConfidentialInfo(boolean isTop) {
        var paragraph1 = new Paragraph("PRIVILEGED AND CONFIDENTIAL REPORT", TIMES_ROMAN_10_5);
        paragraph1.setAlignment(Element.ALIGN_CENTER);
        var paragraph2 = new Paragraph("PREPARED IN CONNECTION WITH QUALITY ASSURANCE COMMITTEE AND AT DIRECTION OF COUNSEL", TIMES_ROMAN_10_5);
        paragraph2.setAlignment(Element.ALIGN_CENTER);
        var paragraph3 = new Paragraph("AND PROTECTED BY RISK MANAGEMENT, PEER REVIEW, AND ATTORNEY CLIENT PRIVILEGES", TIMES_ROMAN_10_5);
        paragraph3.setAlignment(Element.ALIGN_CENTER);
        if (isTop) {
            paragraph3.setSpacingAfter(10f);
        }
        var paragraph = new Paragraph();
        paragraph.add(paragraph1);
        paragraph.add(paragraph2);
        paragraph.add(paragraph3);
        return paragraph;
    }

    private void addTitle(Document document) throws DocumentException {
        var title = new Paragraph("Incident Investigation Tool", TIMES_ROMAN_12_BOLD_UNDERLINE);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        var underTitle = new Paragraph("Resident/Visitor Injury, Abuse, Grievance, Theft, Lost Item/Person, Mistreatment, Neglect, Misappropriation/Exploitation, etc", TIMES_ROMAN_10_UNDERLINE);
        underTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(underTitle);
    }

    private void addReportData(Document document, IncidentReport incidentReport, ZoneId zoneId, PdfWriter writer) throws DocumentException, IOException {
        var pageWidth = document.right() - document.left();
        addCommunityData(document, pageWidth, incidentReport);
        var mainTable = new PdfPTable(new float[]{1, 1});
        mainTable.setWidthPercentage(100f);
        addTwoSplittedCells(pageWidth, mainTable, "Date of report", DateTimeUtils.formatDate(incidentReport.getReportDate(), zoneId), "Time of report",
                DateTimeUtils.formatTime(incidentReport.getReportDate(), zoneId), 0);
        addOneMergedCell(pageWidth, mainTable, "Who was involved (Resident (Name, Unit Number, Phone Number)/If staff, indicate their position)",
                CareCoordinationUtils.concat(", ",
                        CareCoordinationUtils.concat(" ", incidentReport.getFirstName(), incidentReport.getLastName()),
                        incidentReport.getUnitNumber(),
                        incidentReport.getClientPhone()), 0);
        addOneMergedCell(pageWidth, mainTable, "Person completing incident report (name, position, phone)",
                CareCoordinationUtils.concat(", ", incidentReport.getReportAuthor(), incidentReport.getReportAuthorTitle(), incidentReport.getReportAuthorPhone()), 1);
        addOneMergedCell(pageWidth, mainTable, "Incident reported by whom (name, position, phone)",
                CareCoordinationUtils.concat(", ", incidentReport.getReportedBy(), incidentReport.getReportedByTitle(), incidentReport.getReportedByPhone()), 1);
        addTwoSplittedCells(pageWidth, mainTable, "Date of alleged incident", DateTimeUtils.formatDate(incidentReport.getIncidentDatetime(), zoneId),
                "Time of alleged incident", DateTimeUtils.formatTime(incidentReport.getIncidentDatetime(), zoneId), 0);
        addTwoCells(mainTable, "Date incident discovered by agency staff", DateTimeUtils.formatLocalDate(incidentReport.getIncidentDiscoveredDate()),
                "Did the incident occur when a provider was present or was scheduled to be present?", booleanResult(incidentReport.getWasProviderPresentOrScheduled()), true);
        addPlaces(pageWidth, mainTable, incidentReport);
        addWeatherConditions(pageWidth, mainTable, incidentReport);
        addTwoSplittedCells(pageWidth, mainTable, "Was the participant taken to the hospital?", booleanResult(incidentReport.getWasIncidentParticipantTakenToHospital()),
                "Hospital name", incidentReport.getIncidentParticipantHospitalName(), 1);
        addOneMergedCell(pageWidth, mainTable, "Details of the alleged incident", incidentReport.getNarrative(), 1);
        addMergedCellWithoutBottomBorder(mainTable, new Phrase(" ", TIMES_ROMAN_11));
        addInjuryDiagramWithVitalSigns(pageWidth, mainTable, incidentReport);
        addMergedCellWithoutBottomBorder(mainTable, new Phrase(" ", TIMES_ROMAN_11));
        addOneMergedCell(pageWidth, mainTable, "If injury, the current condition of the injured participant/resident", incidentReport.getInjuredClientCondition(), 2);
        addWitnesses(pageWidth, mainTable, incidentReport);
        addInvolvedIndividuals(pageWidth, mainTable, incidentReport);
        addMergedCellWithoutBottomBorder(mainTable, new Phrase(" ", TIMES_ROMAN_11));
        addNotifications(mainTable, incidentReport, zoneId);
        addOneMergedCell(pageWidth, mainTable, "Immediate intervention", incidentReport.getImmediateIntervention(), 2);
        addOneMergedCell(pageWidth, mainTable, "Follow up information", incidentReport.getFollowUpInformation(), 2);
        document.add(mainTable);

        var additionalTable = new PdfPTable(new float[]{7, 3});
        additionalTable.setWidthPercentage(100f);
        addMergedCellWithoutBottomBorder(additionalTable, new Phrase(" ", TIMES_ROMAN_11));
        addOneMergedCell(pageWidth, additionalTable, "Printed name of person reporting incident", incidentReport.getReportedBy(), 0);
        addMergedCellWithoutBottomBorder(additionalTable, new Phrase(" ", TIMES_ROMAN_11));
        addTwoCells(additionalTable, "Signature of person reporting", null, "Date", null, false);
        addMergedCellWithoutBottomBorder(additionalTable, new Phrase(" ", TIMES_ROMAN_11));
        addOneMergedCell(pageWidth, additionalTable, "Printed name of person completing report", incidentReport.getReportAuthor(), 0);
        addMergedCellWithoutBottomBorder(additionalTable, new Phrase(" ", TIMES_ROMAN_11));
        addTwoCells(additionalTable, "Signature of person completing report", null, "Date", null, false);
        addMergedCellWithoutBottomBorder(additionalTable, new Phrase(" ", TIMES_ROMAN_11));
        addWitnessSignatures(additionalTable, incidentReport);
        document.add(additionalTable);

        addLastLines(document);
        addConfidentialInfo(document, writer);
        addIncidentPictures(document, incidentReport.getPictures(), writer);
    }

    private void addCommunityData(Document document, float pageWidth, IncidentReport incidentReport) throws DocumentException {
        var table = new PdfPTable(new float[]{26, 74});
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        var cell = new PdfPCell(getPhraseWithTitle("SITE NAME & ADDRESS", null));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        var data = CareCoordinationUtils.concat(", ", incidentReport.getSiteName(), incidentReport.getClassMemberCurrentAddress());
        var phrase = getPhraseWithTitle(null, data);
        var width = ColumnText.getWidth(phrase);
        if (pageWidth * 0.74f < width) {
            var phrases = splitDataByWidth(phrase, pageWidth * 0.74f, data);
            IntStream.range(0, phrases.size()).forEach(i -> {
                if (i == 0) {
                    addCellWithBottomBorder(table, phrases.get(i));
                } else {
                    addMergedCellWithBottomBorder(table, phrases.get(i));
                }
            });
        } else {
            addCellWithBottomBorder(table, phrase);
        }
        document.add(table);
    }

    private void addTwoCells(PdfPTable table, String firstText, String firstData, String secondText, String secondData, boolean withBorderBetweenCells) {
        addCellWithBottomBorder(table, getPhraseWithTitle(firstText, firstData));
        if (withBorderBetweenCells) {
            addCellWithLeftAndBottomBorder(table, getPhraseWithTitle(secondText, secondData));
        } else {
            addCellWithBottomBorder(table, getPhraseWithTitle(secondText, secondData));
        }
    }

    private void addTwoSplittedCells(float pageWidth, PdfPTable table, String firstText, String firstData, String secondText, String secondData, int additionalRowCount) {
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

    private void addCellWithBottomBorder(PdfPTable table, Phrase phrase) {
        var cell = new PdfPCell(phrase);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingBottom(5f);
        table.addCell(cell);
    }

    private void addCellWithLeftAndBottomBorder(PdfPTable table, Phrase phrase) {
        var cell = new PdfPCell(phrase);
        cell.setBorder(Rectangle.BOTTOM | Rectangle.LEFT);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingBottom(5f);
        table.addCell(cell);
    }

    private void addMergedCell(PdfPTable table, Phrase phrase, boolean withBottomBorder, float leftPadding) {
        var cell = new PdfPCell(phrase);
        if (withBottomBorder) {
            cell.setBorder(Rectangle.BOTTOM);
        } else {
            cell.setBorder(Rectangle.NO_BORDER);
        }
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingBottom(5f);
        cell.setPaddingLeft(leftPadding);
        cell.setColspan(2);
        table.addCell(cell);
    }

    private void addMergedCellWithoutBottomBorder(PdfPTable table, Phrase phrase) {
        addMergedCell(table, phrase, false, 0);
    }

    private void addMergedCellWithBottomBorder(PdfPTable table, Phrase phrase) {
        addMergedCell(table, phrase, true, 0);
    }

    private void addMergedCellWithLeftPadding(PdfPTable table, Phrase phrase) {
        addMergedCell(table, phrase, false, 20f);
    }

    private void addMergedCellWithBottomBorderAndLeftPadding(PdfPTable table, Phrase phrase) {
        addMergedCell(table, phrase, true, 20f);
    }

    private void addEmptyCells(PdfPTable table, int needAddRowCount) {
        if (needAddRowCount > 0) {
            IntStream.range(0, needAddRowCount).forEach(i -> addMergedCellWithBottomBorder(table, new Phrase(" ", TIMES_ROMAN_11)));
        }
    }

    private void addTableCellParams(PdfPCell cell) {
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
    }

    private void addPlaces(float pageWidth, PdfPTable table, IncidentReport incidentReport) {
        var places = Stream.ofNullable(incidentReport.getIncidentPlaceTypes())
                .flatMap(java.util.List::stream)
                .map(place -> {
                    if (place.getIncidentPlaceType().getFreeText()) {
                        return CareCoordinationUtils.concat(" - ", place.getIncidentPlaceType().getName(),
                                Optional.ofNullable(place.getFreeText())
                                        .map(FreeText::getFreeText)
                                        .orElse(null));
                    }
                    return place.getIncidentPlaceType().getName();
                })
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(", "));
        addOneMergedCell(pageWidth, table, "Address & Exact Location of alleged incident", places, 1);
    }

    private void addWeatherConditions(float pageWidth, PdfPTable table, IncidentReport incidentReport) {
        var conditions = Stream.ofNullable(incidentReport.getIncidentWeatherConditionTypes())
                .flatMap(java.util.List::stream)
                .map(condition -> {
                    if (condition.getIncidentWeatherConditionType().getFreeText()) {
                        return CareCoordinationUtils.concat(" - ", condition.getIncidentWeatherConditionType().getName(),
                                Optional.ofNullable(condition.getFreeText())
                                        .map(FreeText::getFreeText)
                                        .orElse(null));
                    }
                    return condition.getIncidentWeatherConditionType().getName();
                })
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(", "));
        addOneMergedCell(pageWidth, table, "Weather conditions", conditions, 0);
    }

    private void addInjuryDiagramWithVitalSigns(float pageWidth, PdfPTable table, IncidentReport incidentReport) throws DocumentException, IOException {
        addInjuryDiagram(table, incidentReport);
        addVitalSigns(pageWidth, table, getVitalSigns(incidentReport));
    }

    private void addInjuryDiagram(PdfPTable table, IncidentReport incidentReport) throws IOException, DocumentException {
        var innerTab = new PdfPTable(1);
        innerTab.setWidthPercentage(100f);
        var frontTitleCell = new PdfPCell(new Phrase("R    FRONT    L     L    BACK    R", TIMES_ROMAN_11_BOLD));
        frontTitleCell.setBorder(Rectangle.NO_BORDER);
        frontTitleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        frontTitleCell.setVerticalAlignment(Element.ALIGN_CENTER);
        innerTab.addCell(frontTitleCell);

        addBodyDiagramWithInjuries(innerTab, incidentReport);

        var cell = new PdfPCell(innerTab);
        addTableCellParams(cell);
        table.addCell(cell);
    }

    private void addBodyDiagramWithInjuries(PdfPTable table, IncidentReport incidentReport) throws IOException, BadElementException {
        var resource = new ClassPathResource(bodyDiagramPath);
        Image bodyDiagram = Image.getInstance(resource.getURL());
        bodyDiagram.scaleAbsolute(166.83f, 156.83f);
        bodyDiagram.setScaleToFitHeight(false);
        var imageCell = new PdfPCell();
        imageCell.setCellEvent((cell, position, canvases) -> {
            bodyDiagram.setAbsolutePosition(position.getLeft(), position.getTop() - bodyDiagram.getScaledHeight());
            PdfContentByte canvas = canvases[PdfPTable.BASECANVAS];
            try {
                canvas.addImage(bodyDiagram);
            } catch (DocumentException ex) {
                throw new RuntimeException(ex);
            }
        });
        imageCell.setMinimumHeight(bodyDiagram.getScaledHeight());
        imageCell.setBorder(Rectangle.NO_BORDER);
        imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        imageCell.setVerticalAlignment(Element.ALIGN_CENTER);

        addInjuryPictures(imageCell, incidentReport.getIncidentInjuries(), bodyDiagram.getScaledWidth(), bodyDiagram.getScaledHeight());

        table.addCell(imageCell);
    }

    private void addInjuryPictures(PdfPCell imageCell, List<IncidentInjury> incidentInjuries, float scaledWidth, float scaledHeight) throws IOException, BadElementException {
        var resource = new ClassPathResource(crossImagePath);
        Image cross = Image.getInstance(resource.getURL());
        cross.scaleAbsolute(4, 4);
        cross.setScaleToFitHeight(false);

        Stream.ofNullable(incidentInjuries)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .forEach(injury -> imageCell.setCellEvent((cell, position, canvases) -> {
                    cross.setAbsolutePosition(position.getLeft() - cross.getScaledWidth() / 2 + (float) injury.getX() * scaledWidth,
                            position.getTop() - cross.getScaledHeight() / 2 - (float) injury.getY() * scaledHeight);
                    PdfContentByte canvas = canvases[PdfPTable.BASECANVAS];
                    try {
                        canvas.addImage(cross);
                    } catch (DocumentException ex) {
                        logger.info("Incident injury image adding error stack {0}", ex);
                    }
                }));
    }

    private void addVitalSigns(float pageWidth, PdfPTable table, String data) {
        var text = "Vital Signs";
        var innerTable = new PdfPTable(1);
        innerTable.setWidthPercentage(100f);
        var titleCell = new PdfPCell(getPhraseWithTitle(text, null));
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        titleCell.setVerticalAlignment(Element.ALIGN_CENTER);
        innerTable.addCell(titleCell);

        var needAddRowCount = 3;
        if (StringUtils.isNotEmpty(data)) {
            var phrase = getPhraseWithTitle(null, data);
            var width = ColumnText.getWidth(phrase);
            if (pageWidth / 2.1 < width) {
                var phrases = splitDataByWidth(phrase, pageWidth / 2.1f, data);
                needAddRowCount -= phrases.size();
                for (var ph : phrases) {
                    addMergedCellWithBottomBorder(innerTable, ph);
                }
            } else {
                addMergedCellWithBottomBorder(innerTable, phrase);
                needAddRowCount--;
            }
        }

        addEmptyCells(innerTable, needAddRowCount);

        var cell = new PdfPCell(innerTable);
        addTableCellParams(cell);
        table.addCell(cell);
    }

    private String getVitalSigns(IncidentReport incidentReport) {
        return Optional.ofNullable(incidentReport.getVitalSigns())
                .map(vs -> CareCoordinationUtils.concat(", ",
                        getVitalSign("Blood pressure", vs.getBloodPressure()),
                        getVitalSign("Pulse", vs.getPulse()),
                        getVitalSign("Respiration rate", vs.getRespirationRate()),
                        getVitalSign("Temperature", vs.getTemperature()),
                        getVitalSign("o2 saturation", vs.getO2Saturation()),
                        getVitalSign("Blood sugar", vs.getBloodSugar()))
                ).orElse(null);
    }

    private String getVitalSign(String text, String data) {
        return Optional.ofNullable(data)
                .filter(StringUtils::isNotEmpty)
                .map(d -> CareCoordinationUtils.concat(": ", text, d))
                .orElse(null);

    }

    private void addWitnesses(float pageWidth, PdfPTable table, IncidentReport incidentReport) {
        var witnesses = incidentReport.getWitnesses();
        var phrases = new ArrayList<Phrase>();
        phrases.add(getPhraseWithTitle("Witness/es name, phone, their report of what occurred", null));
        if (CollectionUtils.isNotEmpty(witnesses)) {
            IntStream.range(0, witnesses.size())
                    .filter(i -> Objects.nonNull(witnesses.get(i)))
                    .forEach(i -> {
                        var witness = witnesses.get(i);
                        var data = CareCoordinationUtils.concat(", ", witness.getName(), witness.getRelationship(), witness.getPhone());
                        if (StringUtils.isNotEmpty(data)) {
                            var value = CareCoordinationUtils.concat(": ", "Witness #" + (i + 1), data);
                            phrases.addAll(splitDataByWidth(getPhraseWithTitle(null, value), pageWidth, value));
                        }
                        if (StringUtils.isNotEmpty(witness.getReport())) {
                            var value = CareCoordinationUtils.concat(": ", "Report", witness.getReport());
                            phrases.addAll(splitDataByWidth(getPhraseWithTitle(null, value), pageWidth, value));
                        }
                    });
        }
        for (var phase : phrases) {
            addMergedCellWithBottomBorder(table, phase);
        }

        addEmptyCells(table, 2 - phrases.size());
    }

    private void addInvolvedIndividuals(float pageWidth, PdfPTable table, IncidentReport incidentReport) {
        var individuals = incidentReport.getIndividuals();
        var phrases = new ArrayList<Phrase>();
        phrases.add(getPhraseWithTitle("Participant/resident involved, account of alleged incident (if not the person initially reporting)", null));
        if (CollectionUtils.isNotEmpty(individuals)) {
            IntStream.range(0, individuals.size())
                    .filter(i -> Objects.nonNull(individuals.get(i)))
                    .forEach(i -> {
                        var individual = individuals.get(i);
                        var data = CareCoordinationUtils.concat(", ", individual.getName(), individual.getRelationship(), individual.getPhone());
                        if (StringUtils.isNotEmpty(data)) {
                            var value = CareCoordinationUtils.concat(": ", "Individual #" + (i + 1), data);
                            phrases.addAll(splitDataByWidth(getPhraseWithTitle(null, value), pageWidth, value));
                        }
                    });
        }
        for (var phase : phrases) {
            addMergedCellWithBottomBorder(table, phase);
        }

        addEmptyCells(table, 4 - phrases.size());
    }

    private void addNotifications(PdfPTable table, IncidentReport incidentReport, ZoneId zoneId) throws DocumentException, IOException {
        var notifications = Stream.ofNullable(incidentReport.getNotifications())
                .flatMap(java.util.List::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(IncidentReportNotification::getDestination, Function.identity()));
        var notificationsInLine = new HashMap<IncidentReportNotificationDestination, Phrase>();
        addMergedCellWithBottomBorder(table, getPhraseWithTitle("Who has been notified of alleged incident, when, by whom", null));
        for (var dest : IncidentReportNotificationDestination.values()) {
            var notification = notifications.getOrDefault(dest, null);
            var mainPhrase = new Phrase(new Chunk(getCheckbox(notification != null), 0, 0));
            var additionalTitle = (dest.hasPersonalInfo() ? " (name and number)" : "") + "/date/time/by whom:" + (isNotificationInLine(dest) ? "\n" : " ");
            mainPhrase.add(new Chunk("  " + dest.getDisplayName() + additionalTitle, TIMES_ROMAN_11));
            if (notification != null) {
                String name = null;
                String phone = null;
                if (dest.hasPersonalInfo()) {
                    name = notification.getFullName();
                    phone = notification.getPhone();
                }
                mainPhrase.add(new Chunk(CareCoordinationUtils
                        .concat("/", name, phone, DATE_FORMATTER_MM_DD_YYYY_WITH_HYPHEN.withZone(zoneId).format(notification.getDatetime()),
                                DateTimeUtils.formatTime(notification.getDatetime(), zoneId),
                                notification.getByWhom()), TIMES_ROMAN_11));
            } else {
                mainPhrase.add(new Chunk(" ", TIMES_ROMAN_11));
            }
            if (isNotificationInLine(dest)) {
                notificationsInLine.put(dest, mainPhrase);
                continue;
            }
            if (dest == IncidentReportNotificationDestination.CARE_MANAGER) {
                addMergedCellWithLeftPadding(table, mainPhrase);
                var phrase = new Phrase("*CM must be notified within ", TIMES_ROMAN_11);
                phrase.add(new Chunk("1 business day", TIMES_ROMAN_11_ITALIC));
                phrase.add(new Chunk(" from the date we are notified of incident for Passport, AL-waiver, ADC.", TIMES_ROMAN_11));
                addMergedCellWithBottomBorder(table, phrase);
                continue;
            }
            if (dest == IncidentReportNotificationDestination.OHIO_DEPARTMENT_OF_HEALTH) {
                addMergedCellWithLeftPadding(table, mainPhrase);
                var phrase = new Phrase("*ODH (for AL only) prefer report within ", TIMES_ROMAN_11);
                phrase.add(new Chunk("5 business days", TIMES_ROMAN_11_ITALIC));
                phrase.add(new Chunk(". If required.", TIMES_ROMAN_11));
                addMergedCellWithBottomBorder(table, phrase);
                continue;
            }
            addMergedCellWithBottomBorderAndLeftPadding(table, mainPhrase);
        }
        addNotificationsInLine(table, notificationsInLine);
    }

    private Image getCheckbox(Boolean isChecked) throws BadElementException, IOException {
        var resource = new ClassPathResource(isChecked ? checkedCheckboxPath : uncheckedCheckboxPath);
        Image checkedBox = Image.getInstance(resource.getURL());
        checkedBox.scaleAbsolute(9, 9);
        checkedBox.setScaleToFitHeight(false);
        return checkedBox;
    }

    private boolean isNotificationInLine(IncidentReportNotificationDestination dest) {
        return dest == IncidentReportNotificationDestination._9_1_1 || dest == IncidentReportNotificationDestination.POLICE || dest == IncidentReportNotificationDestination.OTHER;
    }

    private void addNotificationsInLine(PdfPTable table, Map<IncidentReportNotificationDestination, Phrase> notificationsInLine) {
        var innerTable = new PdfPTable(3);
        innerTable.setWidthPercentage(100f);
        var emergency = notificationsInLine.getOrDefault(IncidentReportNotificationDestination._9_1_1, null);
        var police = notificationsInLine.getOrDefault(IncidentReportNotificationDestination.POLICE, null);
        var other = notificationsInLine.getOrDefault(IncidentReportNotificationDestination.OTHER, null);
        Stream.of(emergency, police, other)
                .filter(Objects::nonNull)
                .forEach(phrase -> {
                    var cell = new PdfPCell(phrase);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    cell.setPaddingTop(3f);
                    cell.setPaddingLeft(20f);
                    cell.setPaddingBottom(3f);
                    innerTable.addCell(cell);
                });

        var cell = new PdfPCell(innerTable);
        cell.setColspan(3);
        table.addCell(cell);
    }

    private void addWitnessSignatures(PdfPTable table, IncidentReport incidentReport) {
        var witnesses = incidentReport.getWitnesses();
        var witnessQty = CollectionUtils.isEmpty(witnesses) || (CollectionUtils.isNotEmpty(witnesses) && witnesses.size() <= 2) ? 2 : witnesses.size();
        IntStream.range(0, witnessQty)
                .forEach(i -> {
                    addTwoCells(table, "Witness Signature", null, "Date", null, false);
                    addMergedCellWithoutBottomBorder(table, new Phrase(" ", TIMES_ROMAN_11));
                });
    }

    private void addLastLines(Document document) throws DocumentException {
        var copyParagraph = new Paragraph("*Copy should be sent to the Vice President of Home and Community Based Services, the immediate supervisor, and the Director of Compliance and Risk Management*", TIMES_ROMAN_11_BOLD_ITALIC);
        copyParagraph.setSpacingAfter(15f);
        copyParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(copyParagraph);

        var table = new PdfPTable(1);
        table.setWidthPercentage(100f);
        addCellWithBottomBorder(table, getPhraseWithTitle("Date copy sent to all pertinent supervisory staff above", null));
        var emptyCell = new PdfPCell(new Phrase(" ", TIMES_ROMAN_11));
        emptyCell.setBorder(Rectangle.NO_BORDER);
        emptyCell.setPaddingBottom(50f);
        table.addCell(emptyCell);
        document.add(table);
    }

    private void addIncidentPictures(Document document, List<IncidentPicture> pictures, PdfWriter writer) throws IOException, DocumentException {
        if (CollectionUtils.isEmpty(pictures)) {
            return;
        }

        for (var position = 0; position < pictures.size(); position++) {
            var pair = incidentPictureService.downloadById(pictures.get(position).getId());
            if (pair != null) {
                var appendix = "Appendix " + (char) (position + 65);
                if (!MediaType.APPLICATION_PDF.equals(pair.getSecond())) {
                    addIncidentImage(document, pair.getFirst(), appendix);
                } else {
                    addIncidentPdfDocument(document, pair.getFirst(), writer, appendix);
                }
            } else {
                throw new IOException("Could not load picture or document with id: " + pictures.get(position).getId());
            }
        }
    }

    private void addIncidentImage(Document document, byte[] bytes, String appendix) throws IOException, DocumentException {
        var image = Image.getInstance(bytes);
        document.newPage();
        var table = new PdfPTable(1);
        table.setWidthPercentage(100f);

        var appendixCell = new PdfPCell(getPhraseWithTitle(null, appendix));
        appendixCell.setBorder(Rectangle.NO_BORDER);
        appendixCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        appendixCell.setVerticalAlignment(Element.ALIGN_CENTER);
        appendixCell.setPaddingBottom(10f);
        table.addCell(appendixCell);

        var imageCell = new PdfPCell(image, false);
        imageCell.setFixedHeight((document.top() + 30f) - (document.bottom() + 102f));
        var maxImageWidth = document.right() - document.left();
        var maxImageHeight = document.top() + 30f - (document.bottom() + 105f);
        if (image.getWidth() > maxImageWidth || image.getHeight() > maxImageHeight) {
            image.scaleToFit(maxImageWidth, maxImageHeight);
        } else {
            image.setScaleToFitLineWhenOverflow(false);
        }
        imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        imageCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(imageCell);

        document.add(table);
        document.add(getConfidentialInfo(false));
    }

    private void addIncidentPdfDocument(Document document, byte[] bytes, PdfWriter writer, String appendix) throws IOException, DocumentException {
        var reader = new PdfReader(bytes);
        for (var pageNum = 1; pageNum <= reader.getNumberOfPages(); pageNum++) {
            document.newPage();
            var page = writer.getImportedPage(reader, pageNum);
            var cb = writer.getDirectContent();
            cb.setFontAndSize(BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED), 10.5f);
            cb.beginText();
            cb.showTextAligned(Element.ALIGN_RIGHT, appendix, document.right(), document.top() - 18f, 0);
            cb.endText();
            cb.addTemplate(page, 0.9, 0, 0, 0.9, 10f, 50f);
            cb.beginText();
            var confidentialInfo = getConfidentialInfo(false);
            cb.showTextAligned(Element.ALIGN_CENTER, confidentialInfo.getChunks().get(0).getContent(), document.getPageSize().getWidth() / 2, document.bottom() + 30f, 0);
            cb.showTextAligned(Element.ALIGN_CENTER, confidentialInfo.getChunks().get(1).getContent(), document.getPageSize().getWidth() / 2, document.bottom() + 15f, 0);
            cb.showTextAligned(Element.ALIGN_CENTER, confidentialInfo.getChunks().get(2).getContent(), document.getPageSize().getWidth() / 2, document.bottom(), 0);
            cb.endText();
        }
    }

    private String booleanResult(Boolean value) {
        return BooleanUtils.isTrue(value) ? "Yes" : "No";
    }

    private Phrase getPhraseWithTitle(String title, String data) {
        var phrase = new Phrase();
        if (StringUtils.isNotEmpty(title)) {
            phrase.add(new Chunk(title + ": ", TIMES_ROMAN_11_BOLD));
        }
        if (StringUtils.isNotEmpty(data)) {
            phrase.add(new Chunk(data, TIMES_ROMAN_11));
        }
        return phrase;
    }
}
