package com.scnsoft.eldermark.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.document.DocumentReport;
import com.scnsoft.eldermark.entity.document.DocumentType;
import com.scnsoft.eldermark.entity.serviceplan.*;
import com.scnsoft.eldermark.service.storage.ImageFileStorage;
import com.scnsoft.eldermark.util.ServicePlanUtils;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ServicePlanPdfGenerationServiceImpl implements ServicePlanPdfGenerationService {

    private static DateTimeFormatter DATE_FORMATTER_MM_DD_YYYY_WITH_HYPHEN = DateTimeFormatter.ofPattern("MM-dd-YYYY");
    private static DateTimeFormatter DATE_FORMATTER_MM_DD_YYYY = DateTimeFormatter.ofPattern("MM/dd/YYYY");

    private static BaseColor HEADING_COLOR = new BaseColor(70, 79, 129);
    private static BaseColor LINE_COLOR = new BaseColor(220, 220, 220);
    private static BaseColor GREY_COLOR = new BaseColor(246, 246, 246);
    private static BaseColor RED_COLOR = new BaseColor(243, 108, 50);
    private static BaseColor YELLOW_COLOR = new BaseColor(254, 207, 51);
    private static BaseColor GREEN_COLOR = new BaseColor(27, 161, 96);
    private static BaseColor DARKGREY_COLOR = new BaseColor(76, 78, 81);

    private static Font HELVETICA_14_BOLD_BLUE = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, HEADING_COLOR);
    private static Font HELVETICA_14_BOLD_DARKGREY = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, DARKGREY_COLOR);
    private static Font HELVETICA_10 = new Font(Font.FontFamily.HELVETICA, 10);
    private static Font HELVETICA_10_BOLD = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

    private final Map<Integer, URL> scoreImageUrlMap = new HashMap<>();
    private final Map<ReferralServiceStatus, BaseColor> referralServiceStatusBaseColorMap = new HashMap<>();

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private ImageFileStorage imageFileStorage;

    @Value("classpath:images/simply-connect-logo.png")
    private Resource logoImage;

    @Value("classpath:images/scoring/0.png")
    private Resource score0Image;

    @Value("classpath:images/scoring/1.png")
    private Resource score1Image;

    @Value("classpath:images/scoring/2.png")
    private Resource score2Image;

    @Value("classpath:images/scoring/3.png")
    private Resource score3Image;

    @Value("classpath:images/scoring/4.png")
    private Resource score4Image;

    @Value("classpath:images/scoring/5.png")
    private Resource score5Image;

    @PostConstruct
    void init() throws IOException {
        scoreImageUrlMap.put(0, score0Image.getURL());
        scoreImageUrlMap.put(1, score1Image.getURL());
        scoreImageUrlMap.put(2, score2Image.getURL());
        scoreImageUrlMap.put(3, score3Image.getURL());
        scoreImageUrlMap.put(4, score4Image.getURL());
        scoreImageUrlMap.put(5, score5Image.getURL());

        referralServiceStatusBaseColorMap.put(ReferralServiceStatus.PENDING, new BaseColor(224, 224, 224));
        referralServiceStatusBaseColorMap.put(ReferralServiceStatus.IN_PROCESS, new BaseColor(255, 241, 202));
        referralServiceStatusBaseColorMap.put(ReferralServiceStatus.COMPLETED, new BaseColor(213, 243, 184));
        referralServiceStatusBaseColorMap.put(ReferralServiceStatus.OTHER, new BaseColor(201, 229, 255));
    }

    @Override
    public DocumentReport generatePdfReport(ServicePlan servicePlan, List<Long> domainIds, ZoneId zoneId)
            throws DocumentException, IOException {

        Document document = new Document(PageSize.A4, 35, 35, 80, 48);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        Rectangle rectangle = new Rectangle(30, 30, 550, 800);
        writer.setBoxSize("rectangle", rectangle);
        document.open();
        createDocumentBody(document, servicePlan, domainIds, zoneId);
        document.close();

        byte[] logoBytes = getLogoBytes(servicePlan.getClient().getOrganizationId());
        PdfReader reader = new PdfReader(baos.toByteArray());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, out);

        int n = reader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {
            createDocumentHeader(document, stamper.getOverContent(i), logoBytes);
            createDocumentFooter(document, stamper.getOverContent(i), i, n);
        }
        stamper.close();
        reader.close();
        var report = new DocumentReport();
        report.setDocumentTitle(getPdfFileName(servicePlan, zoneId));
        report.setInputStream(new ByteArrayInputStream(out.toByteArray()));
        report.setMimeType("application/pdf");
        report.setDocumentType(DocumentType.CUSTOM);
        return report;
    }

    private void createDocumentHeader(Document document, PdfContentByte overContent, byte[] headerByteArray)
            throws IOException, DocumentException {
        final Rectangle page = document.getPageSize();
        final PdfPTable header = new PdfPTable(10);
        header.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
        header.setLockedWidth(true);
        PdfPCell imageCell = createImageCell(Arrays.copyOf(headerByteArray, headerByteArray.length));
        imageCell.setColspan(3);
        header.addCell(imageCell);
        header.addCell(createHeaderTextCell("", null));
        PdfPCell textCell = createHeaderTextCell("Client Service Plan", 6, HELVETICA_14_BOLD_BLUE, null, null);
        header.addCell(textCell);
        header.writeSelectedRows(0, -1, document.leftMargin(),
                page.getHeight() - document.topMargin() + header.getTotalHeight() + 5, overContent);
    }

    private void createDocumentBody(Document document, ServicePlan servicePlan, List<Long> domainIds, ZoneId zoneId)
            throws DocumentException, IOException {
        addProgramInfoDetails(document, servicePlan, zoneId);
        addClientInfoDetails(document, servicePlan.getClient());
        PdfPTable scoringTable = new PdfPTable((new float[]{1}));
        scoringTable.setWidthPercentage(100);
        scoringTable.setSplitLate(false);
        scoringTable.getDefaultCell().setPadding(0);
        scoringTable.getDefaultCell().setIndent(0);
        scoringTable = addServicePlanDetails(scoringTable, servicePlan, domainIds, zoneId);
        document.add(scoringTable);
    }

    private void createDocumentFooter(Document document, PdfContentByte pdfContentByte, int currentPage,
                                      int totalPages) {
        Rectangle page = document.getPageSize();
        PdfPTable footer = new PdfPTable(1);
        PdfPCell nextPageCell = new PdfPCell(
                new Phrase(String.format("%s  %d/%d", "Page", currentPage, totalPages), HELVETICA_10));
        nextPageCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        nextPageCell.setBorder(Rectangle.NO_BORDER);
        footer.addCell(nextPageCell);
        footer.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
        footer.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(), pdfContentByte);
    }

    private PdfPCell createImageCell(byte[] image) throws DocumentException, IOException {
        Image img = Image.getInstance(image);
        img.scaleToFit(200f, 70f);
        final PdfPCell cell = new PdfPCell(img);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setUseAscender(true);
        cell.setPaddingTop(10F);
        return cell;
    }

    private PdfPCell createHeaderTextCell(String value, Integer colspan) {
        return createHeaderTextCell(value, colspan, HELVETICA_14_BOLD_DARKGREY);
    }

    private PdfPCell createHeaderTextCell(String value, Integer colspan, Font font) {
        return createHeaderTextCell(value, colspan, font, null, null);
    }

    private PdfPCell createHeaderTextCell(String value, Integer colspan, Font font, Float paddingBottom, Float paddingTop) {
        final Font fontToUse = font != null ? font : HELVETICA_14_BOLD_DARKGREY;
        PdfPCell cell = new PdfPCell();
        if (colspan != null) {
            cell.setColspan(colspan);
        }
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setMinimumHeight(15);
        Paragraph p = new Paragraph();
        p.add(new Phrase(value, fontToUse));
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setUseAscender(true);
        cell.addElement(p);
        cell.setPaddingTop(paddingTop != null ? paddingTop : 15f);
        cell.setPaddingBottom(paddingBottom != null ? paddingBottom : 17f);
        return cell;
    }

    private byte[] getLogoBytes(Long organizationId) throws IOException {
        Organization organization = organizationDao.getOne(organizationId);
        String logoPath = StringUtils.isNotEmpty(organization.getMainLogoPath()) ? organization.getMainLogoPath() : organization.getAdditionalLogoPath();

        if (StringUtils.isNotEmpty(logoPath) && imageFileStorage.exists(logoPath)) {
            return imageFileStorage.loadAsBytes(logoPath);
        } else {
            return IOUtils.toByteArray(logoImage.getInputStream());
        }
    }

    public String getPdfFileName(ServicePlan servicePlan, ZoneId zoneId) {
        var client = servicePlan.getClient();
        String date = servicePlan.getDateCompleted() != null
                ? (DATE_FORMATTER_MM_DD_YYYY_WITH_HYPHEN.withZone(zoneId).format(servicePlan.getDateCompleted()))
                : (DATE_FORMATTER_MM_DD_YYYY_WITH_HYPHEN.withZone(zoneId).format(servicePlan.getDateCreated()));
        return "Client Service Plan for " + client.getFirstName() + " " + client.getLastName() + " " + date + ".pdf";
    }

    private PdfPTable addServicePlanDetails(PdfPTable scoringTable, ServicePlan servicePlan, List<Long> domainIds, ZoneId zoneId)
            throws DocumentException, IOException {
        List<ServicePlanNeedType> servicePlanNeedTypes;
        if (CollectionUtils.isEmpty(domainIds)) {
            servicePlanNeedTypes = Arrays.asList(ServicePlanNeedType.values());
        } else {
            servicePlanNeedTypes = domainIds.stream().map(ServicePlanNeedType::findByDomainId).collect(Collectors.toList());
        }

        var taskCounter = 1;

        if (CollectionUtils.isNotEmpty(servicePlan.getNeeds())) {
            var servicePlanGoalNeedsMap = servicePlan.getNeeds().stream()
                    .filter(servicePlanGoalNeed -> servicePlanNeedTypes.contains(servicePlanGoalNeed.getDomain()))
                    .filter(servicePlanNeed -> servicePlanNeed instanceof ServicePlanGoalNeed)
                    .map(servicePlanNeed -> (ServicePlanGoalNeed) servicePlanNeed).sorted(Comparator.comparing(need -> need.getDomain().getDisplayName()))
                    .collect(Collectors.groupingBy(ServicePlanNeed::getDomain, LinkedHashMap::new, Collectors.toList()));
            for (var entry : servicePlanGoalNeedsMap.entrySet()) {
                int needNumber = 1;
                var sortedNeeds = entry.getValue().stream().sorted(Comparator.comparing(servicePlanNeed -> servicePlanNeed.getPriority().getNumberPriority(), Comparator.reverseOrder())).collect(Collectors.toList());
                for (var servicePlanNeed : sortedNeeds) {
                    addGoalNeedToTable(scoringTable, servicePlan, servicePlanNeed, zoneId, needNumber);
                    needNumber++;
                }
            }

            var educationTaskExistsAndShouldBeIncluded = false;
            for (ServicePlanNeed need : servicePlan.getNeeds()) {
                if (need instanceof ServicePlanEducationNeed) {
                    if (servicePlanNeedTypes.contains(need.getDomain())) {
                        educationTaskExistsAndShouldBeIncluded = true;
                        break;
                    }
                }
            }
            if (educationTaskExistsAndShouldBeIncluded) {
                PdfPTable table = new PdfPTable((new float[]{1, 8, 6, 2, 3, 3}));
                table.setWidthPercentage(scoringTable.getWidthPercentage());
                scoringTable = addTableCellLine(scoringTable);
                scoringTable = addTableCellLine(scoringTable);
                scoringTable.addCell(setCellNoBorder(new PdfPCell(getTitle("Activation or Education Task"))));
                scoringTable = addTableCellLine(scoringTable);
                table = addTableCellTitle(" ", table, Rectangle.LEFT);
                table = addTableCellTitle("Activation or Education Task", table, Rectangle.TOP);
                table = addTableCellTitle("Program type / Sub type", table, Rectangle.TOP);
                table = addTableCellTitle("Priority", table, Rectangle.TOP);
                table = addTableCellTitle_RightAlignment("Target Completion Date", table, Rectangle.TOP);
                table = addTableCellTitle_RightAlignment("Completion Date", table, Rectangle.RIGHT);
                scoringTable.addCell(setCellNoBorder(new PdfPCell(table)));
            }
            for (ServicePlanNeed need : servicePlan.getNeeds()) {
                if (servicePlanNeedTypes.contains(need.getDomain())) {
                    PdfPTable table = new PdfPTable((new float[]{1, 8, 6, 2, 3, 3}));
                    table.setWidthPercentage(scoringTable.getWidthPercentage());
                    if (need instanceof ServicePlanEducationNeed) {
                        var servicePlanEducationNeed = (ServicePlanEducationNeed) need;
                        Long servicePlanEducationNeedPriority = 0L;
                        if (servicePlanEducationNeed.getPriority() != null) {
                            if (servicePlanEducationNeed.getPriority().getNumberPriority() != null)
                                servicePlanEducationNeedPriority = servicePlanEducationNeed.getPriority()
                                        .getNumberPriority();
                        }
                        table = addTableCellWithTask(taskCounter, servicePlanEducationNeedPriority,
                                servicePlanEducationNeed, table, zoneId);
                        taskCounter++;
                    }
                    scoringTable.addCell(setCellNoBorder(new PdfPCell(table)));
                }
            }

        }
        return scoringTable;
    }

    private void addGoalNeedToTable(PdfPTable scoringTable, ServicePlan servicePlan, ServicePlanGoalNeed servicePlanGoalNeed, ZoneId zoneId, int needNumber)
            throws DocumentException, IOException {
        PdfPTable table = new PdfPTable((new float[]{36, 866}));
        table.setWidthPercentage(scoringTable.getWidthPercentage());
        scoringTable = addTableCellLine(scoringTable);
        scoringTable = addTableCellLine(scoringTable);
        if (needNumber == 1) {
            int score = Optional.ofNullable(ServicePlanUtils.resolveScore(servicePlan.getScoring(), servicePlanGoalNeed.getDomain())).orElse(0);
            PdfPCell domainCell = new PdfPCell();
            domainCell.setPadding(0);
            PdfPTable domainTitle = new PdfPTable((new float[]{4, 1}));
            domainTitle.setWidthPercentage(scoringTable.getWidthPercentage());
            domainTitle.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            domainTitle.getDefaultCell().setPadding(0);
            domainTitle.addCell(getTitle(servicePlanGoalNeed.getDomain().getDisplayName()));
            domainTitle.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            domainTitle.getDefaultCell().setPaddingTop(-5);
            domainTitle.addCell(addScoringImg(score));
            domainTitle.setHorizontalAlignment(Element.ALIGN_LEFT);
            domainCell.addElement(domainTitle);
            scoringTable.addCell(setCellNoBorder(domainCell));
            scoringTable = addTableCellLine(scoringTable);
        }

        table = addTableCellWithNeed(needNumber, servicePlanGoalNeed.getPriority().getNumberPriority(),
                servicePlanGoalNeed, table);
        scoringTable.addCell(setCellNoBorder(new PdfPCell(table)));
        if (CollectionUtils.isNotEmpty(servicePlanGoalNeed.getGoals())) {
            PdfPTable servicePlanGoalNeedtable = new PdfPTable((new float[]{9, 3, 10, 9, 8, 8, 8}));
            servicePlanGoalNeedtable.setWidthPercentage(scoringTable.getWidthPercentage());
            servicePlanGoalNeedtable = addTableCellTitle("Goal", servicePlanGoalNeedtable, Rectangle.LEFT);
            servicePlanGoalNeedtable = addTableCellTitle("%", servicePlanGoalNeedtable, Rectangle.TOP);
            servicePlanGoalNeedtable = addTableCellTitle("Barriers", servicePlanGoalNeedtable,
                    Rectangle.TOP);
            servicePlanGoalNeedtable = addTableCellTitle("Intervention / Action", servicePlanGoalNeedtable,
                    Rectangle.TOP);
            servicePlanGoalNeedtable = addTableCellTitle("Service", servicePlanGoalNeedtable,
                    Rectangle.TOP);
            servicePlanGoalNeedtable = addTableCellTitle_RightAlignment("Target Completion Date",
                    servicePlanGoalNeedtable, Rectangle.TOP);
            servicePlanGoalNeedtable = addTableCellTitle_RightAlignment("Completion Date",
                    servicePlanGoalNeedtable, Rectangle.RIGHT);
            for (ServicePlanGoal goal : servicePlanGoalNeed.getGoals()) {
                addTableCellsWithBorder(goal.getGoal(), servicePlanGoalNeedtable);
                if (goal.getGoalCompletion() != null) {
                    servicePlanGoalNeedtable = addTableCellsWithBorder(goal.getGoalCompletion().toString(),
                            servicePlanGoalNeedtable);
                } else {
                    servicePlanGoalNeedtable = addTableCellsWithBorder(" ", servicePlanGoalNeedtable);
                }
                servicePlanGoalNeedtable = addTableCellsWithBorder(goal.getBarriers(),
                        servicePlanGoalNeedtable);
                servicePlanGoalNeedtable = addTableCellsWithBorder(goal.getInterventionAction(),
                        servicePlanGoalNeedtable);
                String[] referralServiceData = {goal.getResourceName(), goal.getProviderName()};
                servicePlanGoalNeedtable = addReferralServiceTableCellsWithBorder(servicePlanGoalNeedtable,
                        goal.getReferralServiceStatus(), goal.getWasPreviouslyInPlace(), referralServiceData);
                if (goal.getTargetCompletionDate() != null) {
                    servicePlanGoalNeedtable = addTableCellsWithBorder(
                            (DATE_FORMATTER_MM_DD_YYYY.withZone(zoneId).format(goal.getTargetCompletionDate())),
                            servicePlanGoalNeedtable, Element.ALIGN_RIGHT);
                } else {
                    servicePlanGoalNeedtable = addTableCellsWithBorder(" ", servicePlanGoalNeedtable);
                }
                if (goal.getCompletionDate() != null) {
                    servicePlanGoalNeedtable = addTableCellsWithBorder(
                            (DATE_FORMATTER_MM_DD_YYYY.withZone(zoneId).format(goal.getCompletionDate())),
                            servicePlanGoalNeedtable, Element.ALIGN_RIGHT);
                } else {
                    servicePlanGoalNeedtable = addTableCellsWithBorder(" ", servicePlanGoalNeedtable);
                }
            }
            scoringTable.addCell(setCellNoBorder(new PdfPCell(servicePlanGoalNeedtable)));
        }
    }

    private void addProgramInfoDetails(Document document, ServicePlan servicePlan, ZoneId zoneId) throws DocumentException {
        document.add(getTitle("Program Info"));
        addParagraphLine(document);
        PdfPTable table = new PdfPTable((new float[]{1, 2}));
        table.setWidthPercentage(98);
        if (servicePlan.getDateCreated() != null) {
            table = addTableCells("Date Started",
                    (DATE_FORMATTER_MM_DD_YYYY.withZone(zoneId).format(servicePlan.getDateCreated())), table);
        }
        if (servicePlan.getDateCompleted() != null) {
            table = addTableCells("Date Completed",
                    (DATE_FORMATTER_MM_DD_YYYY.withZone(zoneId).format(servicePlan.getDateCompleted())), table);
        }
        if (servicePlan.getEmployee() != null && servicePlan.getEmployee().getFullName() != null) {
            table = addTableCells("Service Coordinator",
                    StringUtils.isNotEmpty(servicePlan.getEmployee().getFullName())
                            ? servicePlan.getEmployee().getFullName()
                            : "",
                    table);
        }
        if (servicePlan.getEmployee().getPerson() != null) {
            table = addTelecoms(servicePlan.getEmployee().getPerson().getTelecoms(), table);
        }
        document.add(table);
    }

    private void addClientInfoDetails(Document document, Client client) throws DocumentException {
        document.add(getTitle(" "));
        document.add(getTitle("Client Info"));
        addParagraphLine(document);
        PdfPTable table = new PdfPTable((new float[]{1, 2}));
        table.setWidthPercentage(98);
        var patientName = new StringBuilder();
        if (client.getFirstName() != null) {
            patientName.append(client.getFirstName());
            if (client.getLastName() != null) {
                patientName.append(" ");
            }
        }
        if (client.getLastName() != null) {
            patientName.append(client.getLastName());
        }
        if (StringUtils.isNotEmpty(patientName)) {
            table = addTableCells("Client Name", patientName.toString(), table);
        }
        if (client.getGender() != null && StringUtils.isNotEmpty(client.getGender().getDisplayName())) {
            table = addTableCells("Gender", client.getGender().getDisplayName(), table);
        }
        if (StringUtils.isNotEmpty(client.getSsnLastFourDigits())) {
            table = addTableCells("SSN", "###-##-" + client.getSsnLastFourDigits(), table);
        }
        if (client.getBirthDate() != null) {
            table = addTableCells("Date of Birth",
                    client.getBirthDate() != null ? CcdUtils.formatTableLocalDate(client.getBirthDate())
                            : "",
                    table);
        }
        String riskScore = client.getRiskScore();
        if (StringUtils.isNotEmpty(riskScore)) {
            table = addTableCells("Risk Score", riskScore, table);
        }
        if (client.getPerson() != null) {
            if (client.getPerson().getAddresses() != null
                    && client.getPerson().getAddresses().get(0).getFullAddress() != null) {
                table = addTableCells("Address", client.getPerson().getAddresses().get(0).getFullAddress(), table);
            }
            table = addTelecoms(client.getPerson().getTelecoms(), table);
        }
        document.add(table);
    }

    private PdfPTable addTelecoms(List<PersonTelecom> telecoms, PdfPTable table) {
        if (CollectionUtils.isNotEmpty(telecoms)) {
            Predicate<PersonTelecom> phoneOnly = s -> PersonTelecomCode.HP.name().equalsIgnoreCase(s.getUseCode())
                    || PersonTelecomCode.MC.name().equalsIgnoreCase(s.getUseCode())
                    || PersonTelecomCode.WP.name().equalsIgnoreCase(s.getUseCode());
            Predicate<PersonTelecom> emailOnly = s -> PersonTelecomCode.EMAIL.name().equalsIgnoreCase(s.getUseCode());
            String phone = telecoms.stream().filter(phoneOnly).map(PersonTelecom::getValue)
                    .filter(StringUtils::isNotBlank).collect(Collectors.joining("; "));
            String email = telecoms.stream().filter(emailOnly).map(PersonTelecom::getValue)
                    .filter(StringUtils::isNotBlank).findFirst().orElse(null);
            if (StringUtils.isNotEmpty(phone)) {
                table = addTableCells("Phone", phone, table);
            }
            if (StringUtils.isNotEmpty(email)) {
                table = addTableCells("Email", email, table);
            }
        }
        return table;
    }

    private Paragraph getTitle(String text) {
        Paragraph paragraph = new Paragraph(text, HELVETICA_14_BOLD_DARKGREY);
        return paragraph;
    }

    private PdfPTable addTableCellWithNeed(int index, Long long1, ServicePlanGoalNeed servicePlanGoalNeed,
                                           PdfPTable table) {
        PdfPCell cell_table = new PdfPCell(getParagraphBold(String.valueOf(index)));
        cell_table = setPriority(cell_table, long1);
        cell_table.setBorder(Rectangle.NO_BORDER);
        cell_table.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell_table.setPadding(5);
        table.addCell(cell_table);
        table = addTableCellWithHeading("Priority", servicePlanGoalNeed.getPriority().getDisplayName(), table);
        cell_table = new PdfPCell(getParagraph(" "));
        cell_table = setPriority(cell_table, long1);
        cell_table.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell_table);
        if (servicePlanGoalNeed.getProgramType() != null) {
            table = addTableCellWithHeading("Program type / Sub type", getProgramType(servicePlanGoalNeed), table);
            table.addCell(cell_table);
        }
        if (servicePlanGoalNeed.getNeedOpportunity() != null) {
            table = addTableCellWithHeading("Need / Opportunity", servicePlanGoalNeed.getNeedOpportunity(), table);
            table.addCell(cell_table);
        }
        if (StringUtils.isNotEmpty(servicePlanGoalNeed.getProficiencyGraduationCriteria())) {
            table = addTableCellWithHeading("Proficiency / Graduation Criteria",
                    servicePlanGoalNeed.getProficiencyGraduationCriteria(), table);
        }

        return table;
    }

    private PdfPTable addTableCellWithTask(int index, Long priority, ServicePlanEducationNeed servicePlanEducationNeed,
                                           PdfPTable table, ZoneId zoneId) {

        PdfPCell cell_table = new PdfPCell(getParagraphBold(String.valueOf(index)));
        cell_table = setPriority(cell_table, priority);
        cell_table.setBorder(Rectangle.NO_BORDER);
        cell_table.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell_table.setPadding(5);
        table.addCell(cell_table);
        table = addTableCellsWithBorder(servicePlanEducationNeed.getActivationOrEducationTask(), table);
        table = addTableCellsWithBorder(getProgramType(servicePlanEducationNeed), table);
        table = addTableCellsWithBorder(servicePlanEducationNeed.getPriority().getDisplayName(), table);
        if (servicePlanEducationNeed.getTargetCompletionDate() != null) {
            table = addTableCellsWithBorder(
                    (DATE_FORMATTER_MM_DD_YYYY.withZone(zoneId).format(servicePlanEducationNeed.getTargetCompletionDate())),
                    table, Element.ALIGN_RIGHT);
        } else {
            table = addTableCellsWithBorder(" ", table);
        }
        if (servicePlanEducationNeed.getCompletionDate() != null) {
            table = addTableCellsWithBorder(
                    (DATE_FORMATTER_MM_DD_YYYY.withZone(zoneId).format(servicePlanEducationNeed.getCompletionDate())),
                    table, Element.ALIGN_RIGHT);
        } else {
            table = addTableCellsWithBorder(" ", table);
        }
        return table;
    }

    private PdfPCell setPriority(PdfPCell cell, Long priority) {
        switch (priority.intValue()) {
            case 1:
                cell.setBackgroundColor(GREEN_COLOR);
                break;
            case 2:
                cell.setBackgroundColor(YELLOW_COLOR);
                break;
            case 3:
                cell.setBackgroundColor(RED_COLOR);
                break;
            default:
                cell.setBackgroundColor(GREEN_COLOR);
        }
        return cell;
    }

    private Image addScoringImg(int score) throws BadElementException, IOException {

        Image img = Image.getInstance(scoreImageUrlMap.getOrDefault(score, score0Image.getURL()));

        img.scalePercent(5);
        img.setScaleToFitLineWhenOverflow(true);
        return img;

    }

    private PdfPTable addTableCellsWithBorder(String text, PdfPTable table) {
        return addTableCellsWithBorder(text, table, null);
    }

    private PdfPTable addTableCellsWithBorder(String text, PdfPTable table, Integer alignment) {
        PdfPCell cell_table = new PdfPCell();
        cell_table.addElement(getParagraph(text, alignment));
        cell_table.setBorderColor(LINE_COLOR);
        cell_table.setPadding(5);
        table.addCell(cell_table);
        return table;
    }

    private PdfPTable addReferralServiceTableCellsWithBorder(PdfPTable table, ReferralServiceStatus status, Boolean wasPreviouslyInPlace, String... texts) {
        PdfPCell cell_table = new PdfPCell();
        Arrays.stream(texts).forEach(t -> cell_table.addElement(getParagraph(t, null)));
        Optional.ofNullable(status).ifPresent(st -> cell_table.addElement(getReferralServiceStatusParagraph(st)));
        if (BooleanUtils.isTrue(wasPreviouslyInPlace)) {
            cell_table.addElement(getParagraph("Service was previously in place"));
        }
        cell_table.setBorderColor(LINE_COLOR);
        cell_table.setPadding(5);
        table.addCell(cell_table);
        return table;
    }

    private Paragraph getReferralServiceStatusParagraph(ReferralServiceStatus status) {
        Chunk chunk = new Chunk(status.getDisplayName(), HELVETICA_10)
                .setBackground(referralServiceStatusBaseColorMap.getOrDefault(status, BaseColor.WHITE), 5f, 1f, 5f, 3f);
        var paragraph = new Paragraph(chunk);
        paragraph.setIndentationLeft(5);
        return paragraph;
    }

    private PdfPCell setCellNoBorder(PdfPCell cell_table) {
        cell_table.setBorder(Rectangle.NO_BORDER);
        return cell_table;
    }

    private PdfPTable addTableCellTitle(String text, PdfPTable table, int border) {
        PdfPCell cell_table = new PdfPCell();
        cell_table.addElement(getParagraphBold(text));
        cell_table.setBorder(Rectangle.NO_BORDER | Rectangle.TOP | border);
        cell_table.setBackgroundColor(GREY_COLOR);
        cell_table.setBorderColor(LINE_COLOR);
        cell_table.setPadding(5);
        table.addCell(cell_table);
        return table;
    }

    private PdfPTable addTableCellTitle_RightAlignment(String text, PdfPTable table, int border) {
        PdfPCell cell_table = new PdfPCell();
        cell_table.addElement(getParagraph(text, Element.ALIGN_RIGHT, true));
        cell_table.setBorder(Rectangle.NO_BORDER | Rectangle.TOP | border);
        cell_table.setBackgroundColor(GREY_COLOR);
        cell_table.setBorderColor(LINE_COLOR);
        cell_table.setPadding(5);
        table.addCell(cell_table);
        return table;
    }

    private PdfPTable addTableCellLine(PdfPTable table) {
        table.addCell(setCellNoBorder(new PdfPCell(getParagraph(" "))));
        return table;
    }

    private PdfPTable addTableCells(String text, String data, PdfPTable table) {

        PdfPCell cell_table = new PdfPCell(getParagraphBold(text));
        cell_table.setBorder(Rectangle.NO_BORDER);

        table.addCell(cell_table);

        cell_table = new PdfPCell(getParagraph(data));
        cell_table.setBorder(Rectangle.NO_BORDER);

        table.addCell(cell_table);
        return table;
    }

    private PdfPTable addTableCellWithHeading(String text, String data, PdfPTable table) {
        PdfPCell cell_table = new PdfPCell();
        cell_table.addElement(getParagraphBold(text));
        cell_table.addElement(getParagraph(data));
        cell_table.setBorderColor(LINE_COLOR);
        cell_table.setPadding(5);
        table.addCell(cell_table);
        return table;
    }

    private Paragraph getParagraphBold(String text) {
        return getParagraph(text, null, true);
    }

    private Paragraph getParagraph(String text) {
        return getParagraph(text, null, false);
    }

    private Paragraph getParagraph(String text, Integer alignment) {
        return getParagraph(text, alignment, false);
    }

    private Paragraph getParagraph(String text, Integer alignment, boolean bold) {
        Paragraph paragraph = new Paragraph(text, bold ? HELVETICA_10_BOLD : HELVETICA_10);
        paragraph.setSpacingAfter(5f);
        if (alignment != null) {
            paragraph.setAlignment(alignment);
        }
        return paragraph;
    }

    private void addParagraphLine(Document document) throws DocumentException {
        PdfPTable table = new PdfPTable((new float[]{1}));
        table.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(getParagraphBold(" "));
        cell.setBorder(Rectangle.NO_BORDER | Rectangle.BOTTOM);
        cell.setBorderColor(GREY_COLOR);
        cell.setBorderWidth(2f);
        table.addCell(cell);
        table.setSpacingAfter(3f);
        document.add(table);
    }

    private <T extends ServicePlanNeed> String getProgramType(T need) {
        StringBuilder sb = new StringBuilder();
        if (need.getProgramType() != null) {
            sb.append(need.getProgramType().getDisplayName());
        }
        if (need.getProgramSubType() != null) {
            sb.append(" / ");
            sb.append(need.getProgramSubType().getDisplayName());
            sb.append("\n");
            sb.append(need.getProgramSubType().getzCode().getCode());
            sb.append(": ");
            sb.append(need.getProgramSubType().getzCode().getDescription());
        }
        return sb.toString();
    }

}
