package com.scnsoft.eldermark.services.carecoordination.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.scnsoft.eldermark.dao.DatabasesDao;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.serviceplan.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;

import java.io.*;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class ServicePlanPdfGenerator{
	
	private static SimpleDateFormat DATE_FORMATTER_MM_DD_YYYY_WITH_HYPHEN = new SimpleDateFormat("MM-dd-YYYY");
    private static SimpleDateFormat DATE_FORMATTER_MM_DD_YYYY = new SimpleDateFormat("MM/dd/YYYY");

    private static BaseColor HEADING_COLOR = new BaseColor(70, 79, 129);
    private static BaseColor LINE_COLOR = new BaseColor(220, 220, 220);
    private static BaseColor GREY_COLOR = new BaseColor(246, 246, 246);
    private static BaseColor RED_COLOR = new BaseColor(243, 108, 50);
    private static BaseColor YELLOW_COLOR = new BaseColor(254, 207, 51);
    private static BaseColor GREEN_COLOR = new BaseColor(27, 161, 96);
    private static BaseColor DARKGREY_COLOR = new BaseColor(76, 78, 81);

    private static Font HELVETICA_12 = new Font(Font.FontFamily.HELVETICA, 12);
    private static Font HELVETICA_12_BOLD = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static Font HELVETICA_14_BOLD_BLUE = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, HEADING_COLOR);
    private static Font HELVETICA_14_BOLD_DARKGREY = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, DARKGREY_COLOR);
    private static Font HELVETICA_10 = new Font(Font.FontFamily.HELVETICA, 10);
    private static Font HELVETICA_10_BOLD = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    
    @Autowired
    private DatabasesDao databaseDao;
    
    @Autowired
    private ResidentDao residentDao;

    @Value("classpath:images/logo-new.png")
    private Resource logoImage;

    @Value("${image.upload.basedir}")
    String pictureUploadBasedir;
    
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

    public ByteArrayOutputStream generate(ServicePlan servicePlan, Long timeZoneOffset) throws DocumentException, IOException {
    	setTimeZoneToFormatter(timeZoneOffset);
        Resident resident = residentDao.getResident(servicePlan.getResident().getId());
        Document document = new Document(PageSize.A4, 35, 35, 80, 48);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        Rectangle rectangle = new Rectangle(30, 30, 550, 800);
        writer.setBoxSize("rectangle", rectangle);
        document.open();
        createDocumentBody(document, resident, servicePlan);
        document.close();

        byte[] logoBytes = getLogoBytes(servicePlan.getResident().getDatabaseId());
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
        return out;
    }

    private void setTimeZoneToFormatter(Long timeZoneOffset) {
    	Long invertedTimeZone = (timeZoneOffset!=null ? -timeZoneOffset : 0 );
    	TimeZone timeZone;
    	try {
            timeZone = TimeZone.getTimeZone(TimeZone.getAvailableIDs((int) TimeUnit.MINUTES.toMillis(invertedTimeZone))[0]);
        } catch (Exception e) {
        	timeZone = TimeZone.getTimeZone(TimeZone.getAvailableIDs((int) TimeUnit.MINUTES.toMillis(0))[0]);
        }
        DATE_FORMATTER_MM_DD_YYYY_WITH_HYPHEN.setTimeZone(timeZone);
        DATE_FORMATTER_MM_DD_YYYY.setTimeZone(timeZone);
    }
    
    private void createDocumentHeader(Document document, PdfContentByte overContent, byte[] headerByteArray)
            throws IOException, DocumentException {
        final Rectangle page = document.getPageSize();
        final PdfPTable header = new PdfPTable(10);
        header.setSplitLate(false);
        header.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
        header.setLockedWidth(true);
        PdfPCell imageCell = createImageCell(Arrays.copyOf(headerByteArray, headerByteArray.length));
        imageCell.setColspan(3);
        header.addCell(imageCell);
        header.addCell(createHeaderTextCell("", null));
        PdfPCell textCell = createHeaderTextCell("LSA Senior Connect Client Service Plan", 6, HELVETICA_14_BOLD_BLUE);
        header.addCell(textCell);
        header.writeSelectedRows(0, -1, document.leftMargin(),
                page.getHeight() - document.topMargin() + header.getTotalHeight() + 5, overContent);
    }
    
    private void createDocumentBody(Document document, Resident resident, ServicePlan servicePlan)
            throws DocumentException, IOException {
        addProgramInfoDetails(document, servicePlan);
        addClientInfoDetails(document, resident.getId());
        PdfPTable scoringTable = new PdfPTable((new float[] { 1 }));
        scoringTable.setSplitLate(false);
        scoringTable.setWidthPercentage(100);
        scoringTable.setSplitLate(false);
        scoringTable.getDefaultCell().setPadding(0);
        scoringTable.getDefaultCell().setIndent(0);
        scoringTable = addServicePlanDetails(scoringTable, servicePlan, document);
        document.add(scoringTable);
    }
    
    private void createDocumentFooter(Document document, PdfContentByte pdfContentByte, int currentPage,
            int totalPages) {
        Rectangle page = document.getPageSize();
        PdfPTable footer = new PdfPTable(1);
        footer.setSplitLate(false);
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
        final PdfPCell cell = new PdfPCell(img, true);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private PdfPCell createHeaderTextCell(String value, Integer colspan) {
        return createHeaderTextCell(value, colspan, HELVETICA_14_BOLD_DARKGREY);
    }

    private PdfPCell createHeaderTextCell(String value, Integer colspan, Font font) {
        return createHeaderTextCell(value, colspan, font, null);
    }
    
    private PdfPCell createHeaderTextCell(String value, Integer colspan, Font font, Float paddingBottom) {
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
        cell.addElement(p);
        cell.setPaddingTop(15f);
        cell.setPaddingBottom(paddingBottom != null ? paddingBottom : 17f);
        return cell;
    }

    private byte[] getLogoBytes(Long databaseId) throws IOException {
        Pair<String, String> logoPaths = databaseDao.getDatabaseLogos(databaseId);
        String mainLogoPath = logoPaths.getFirst();
        String additionalLogoPath = logoPaths.getSecond();
        if (StringUtils.isNotEmpty(mainLogoPath)) {
            File f = new File(pictureUploadBasedir + File.separator + mainLogoPath);
            FileInputStream fis = new FileInputStream(f);
            return IOUtils.toByteArray(fis);
        } else if (StringUtils.isNotEmpty(additionalLogoPath)) {
            File f = new File(pictureUploadBasedir + File.separator + additionalLogoPath);
            FileInputStream fis = new FileInputStream(f);
            return IOUtils.toByteArray(fis);
        }
        return IOUtils.toByteArray(logoImage.getInputStream());
    }
    
    private PdfPTable addServicePlanDetails(PdfPTable scoringTable, ServicePlan servicePlan, Document document)
            throws DocumentException, MalformedURLException, IOException {
        int needCounter = 1;
        int taskCounter = 1;
        int needDomainCounter = 1;
        if (servicePlan.getNeeds() != null) {
            java.util.List<ServicePlanNeed> servicePlanNeeds = servicePlan.getNeeds();
            Collections.sort(servicePlanNeeds);

            java.util.Map<String, ServicePlanNeed> servicePlanNeedsMap = new TreeMap<String, ServicePlanNeed>();
            for (ServicePlanNeed need : servicePlanNeeds) {
                if (need instanceof ServicePlanGoalNeed) {
                    String domain = " ";
                    if (need.getType() != null && StringUtils.isNotEmpty(need.getType().getDisplayName())) {
                        domain = need.getType().getDisplayName() + String.valueOf(needDomainCounter);
                        needDomainCounter++;
                        servicePlanNeedsMap.put(domain, need);
                    }

                }
            }
            String domainName = " ";
            for (Map.Entry<String, ServicePlanNeed> entry : servicePlanNeedsMap.entrySet()) {
                ServicePlanNeed need = entry.getValue();
                PdfPTable table = new PdfPTable((new float[] { 36, 866 }));
                table.setSplitLate(false);
                if (need instanceof ServicePlanGoalNeed) {
                    table.setWidthPercentage(scoringTable.getWidthPercentage());
                    ServicePlanGoalNeed servicePlanGoalNeed = (ServicePlanGoalNeed) need;

                    if (need.getType() != null && StringUtils.isNotEmpty(need.getType().getDisplayName())
                            && !need.getType().getDisplayName().equals(domainName)) {
                        needCounter = 1;
                        scoringTable = addTableCellLine(scoringTable);
                        scoringTable = addTableCellLine(scoringTable);
                        int score = 0;
                        score = getScore(need.getType(), servicePlan);
                        PdfPCell domainCell = new PdfPCell();
                        domainCell.setPadding(0);
                        PdfPTable domainTitle = new PdfPTable((new float[] { 4, 1 }));
                        domainTitle.setSplitLate(false);
                        domainTitle.setWidthPercentage(scoringTable.getWidthPercentage());
                        domainTitle.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                        domainTitle.getDefaultCell().setPadding(0);
                        domainTitle.addCell(getTitle(need.getType().getDisplayName()));
                        domainTitle.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                        domainTitle.getDefaultCell().setPaddingTop(-5);
                        domainTitle.addCell(addScoringImg(score));
                        domainTitle.setHorizontalAlignment(Element.ALIGN_LEFT);
                        domainCell.addElement(domainTitle);
                        scoringTable.addCell(setCellNoBorder(domainCell));
                        domainName = need.getType().getDisplayName();
                        scoringTable = addTableCellLine(scoringTable);
                    } else {
                        scoringTable = addTableCellLine(scoringTable);
                        scoringTable = addTableCellLine(scoringTable);
                    }
                    table = addTableCellWithNeed(needCounter, servicePlanGoalNeed.getPriority().getNumberPriority(),
                            servicePlanGoalNeed, table);
                    needCounter++;
                    scoringTable.addCell(setCellNoBorder(new PdfPCell(table)));
                    if (CollectionUtils.isNotEmpty(servicePlanGoalNeed.getGoals())) {
                        PdfPTable servicePlanGoalNeedtable = new PdfPTable((new float[] { 9, 3, 10, 9, 8, 8, 8 }));
                        servicePlanGoalNeedtable.setWidthPercentage(scoringTable.getWidthPercentage());
                        servicePlanGoalNeedtable.setSplitLate(false);
                        servicePlanGoalNeedtable = addTableCellTitle("Goal", servicePlanGoalNeedtable, Rectangle.LEFT);
                        servicePlanGoalNeedtable = addTableCellTitle("%", servicePlanGoalNeedtable, Rectangle.TOP);
                        servicePlanGoalNeedtable = addTableCellTitle("Barriers", servicePlanGoalNeedtable,
                                Rectangle.TOP);
                        servicePlanGoalNeedtable = addTableCellTitle("Intervention / Action", servicePlanGoalNeedtable,
                                Rectangle.TOP);
                        servicePlanGoalNeedtable = addTableCellTitle("Resource Name", servicePlanGoalNeedtable,
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
                            servicePlanGoalNeedtable = addTableCellsWithBorder(goal.getResourceName(),
                                    servicePlanGoalNeedtable);
                            if (goal.getTargetCompletionDate() != null) {
                                servicePlanGoalNeedtable = addTableCellsWithBorder(
                                        (DATE_FORMATTER_MM_DD_YYYY.format(goal.getTargetCompletionDate())).toString(),
                                        servicePlanGoalNeedtable);
                            } else {
                                servicePlanGoalNeedtable = addTableCellsWithBorder(" ", servicePlanGoalNeedtable);
                            }
                            if (goal.getCompletionDate() != null) {

                                servicePlanGoalNeedtable = addTableCellsWithBorder(
                                        (DATE_FORMATTER_MM_DD_YYYY.format(goal.getCompletionDate())).toString(),
                                        servicePlanGoalNeedtable);
                            } else {
                                servicePlanGoalNeedtable = addTableCellsWithBorder(" ", servicePlanGoalNeedtable);
                            }
                        }
                        scoringTable.addCell(setCellNoBorder(new PdfPCell(servicePlanGoalNeedtable)));
                    }
                }
            }

            boolean educationTaskExists = false;
            for (ServicePlanNeed need : servicePlanNeeds) {
                if (need instanceof ServicePlanEducationNeed) {
                    educationTaskExists = true;
                    break;
                }
            }

            if (educationTaskExists) {
                PdfPTable table = new PdfPTable((new float[] { 1, 8, 2, 2, 2 }));
                table.setSplitLate(false);
                table.setWidthPercentage(scoringTable.getWidthPercentage());
                scoringTable = addTableCellLine(scoringTable);
                scoringTable = addTableCellLine(scoringTable);
                scoringTable.addCell(setCellNoBorder(new PdfPCell(getTitle("Activation or Education Task"))));
                scoringTable = addTableCellLine(scoringTable);
                table = addTableCellTitle(" ", table, Rectangle.LEFT);
                table = addTableCellTitle("Activation or Education Task", table, Rectangle.TOP);
                table = addTableCellTitle("Priority", table, Rectangle.TOP);
                table = addTableCellTitle_RightAlignment("Target Completion Date", table, Rectangle.TOP);
                table = addTableCellTitle_RightAlignment("Completion Date", table, Rectangle.RIGHT);
                scoringTable.addCell(setCellNoBorder(new PdfPCell(table)));
            }

            for (ServicePlanNeed need : servicePlanNeeds) {
                PdfPTable table = new PdfPTable((new float[] { 1, 8, 2, 2, 2 }));
                table.setSplitLate(false);
                table.setWidthPercentage(scoringTable.getWidthPercentage());
                if (need instanceof ServicePlanEducationNeed) {
                	ServicePlanEducationNeed servicePlanEducationNeed = (ServicePlanEducationNeed) need;
                    int servicePlanEducationNeedPriority = 0;
                    if (servicePlanEducationNeed.getPriority() != null) {
                        if (servicePlanEducationNeed.getPriority().getNumberPriority() != null)
                            servicePlanEducationNeedPriority = servicePlanEducationNeed.getPriority()
                                    .getNumberPriority();
                    }
                    table = addTableCellWithTask(taskCounter, servicePlanEducationNeedPriority,
                            servicePlanEducationNeed, table);
                    taskCounter++;
                }
                scoringTable.addCell(setCellNoBorder(new PdfPCell(table)));
            }
        }
        return scoringTable;
    }
    
    private int getScore(ServicePlanNeedType domain, ServicePlan servicePlan) throws DocumentException {
        int score = 0;
        if (ServicePlanNeedType.HEALTH_STATUS == domain) {
            score = servicePlan.getScoring().getHealthStatusScore() != null
                    ? servicePlan.getScoring().getHealthStatusScore()
                    : 0;
        }
        if (ServicePlanNeedType.TRANSPORTATION == domain) {
            score = servicePlan.getScoring().getTransportationScore() != null
                    ? servicePlan.getScoring().getTransportationScore()
                    : 0;
        }
        if (ServicePlanNeedType.HOUSING == domain) {
            score = servicePlan.getScoring().getHousingScore() != null ? servicePlan.getScoring().getHousingScore() : 0;
        }
        if (ServicePlanNeedType.NUTRITION_SECURITY == domain) {
            score = servicePlan.getScoring().getNutritionSecurityScore() != null
                    ? servicePlan.getScoring().getNutritionSecurityScore()
                    : 0;
        }
        if (ServicePlanNeedType.SUPPORT == domain) {
            score = servicePlan.getScoring().getSupportScore() != null ? servicePlan.getScoring().getSupportScore() : 0;
        }
        if (ServicePlanNeedType.BEHAVIORAL == domain) {
            score = servicePlan.getScoring().getBehavioralScore() != null
                    ? servicePlan.getScoring().getBehavioralScore()
                    : 0;
        }
        if (ServicePlanNeedType.OTHER == domain) {
            score = servicePlan.getScoring().getOtherScore() != null ? servicePlan.getScoring().getOtherScore() : 0;
        }
        if (ServicePlanNeedType.HOUSING_ONLY == domain) {
            score = servicePlan.getScoring().getHousingOnlyScore() != null ? servicePlan.getScoring().getHousingOnlyScore() : 0;
        }
        if (ServicePlanNeedType.SOCIAL_WELLNESS == domain) {
            score = servicePlan.getScoring().getSocialWellnessScore() != null ? servicePlan.getScoring().getSocialWellnessScore() : 0;
        }
        if (ServicePlanNeedType.EMPLOYMENT == domain) {
            score = servicePlan.getScoring().getEmploymentScore() != null ? servicePlan.getScoring().getEmploymentScore() : 0;
        }
        if (ServicePlanNeedType.MENTAL_WELLNESS == domain) {
            score = servicePlan.getScoring().getMentalWellnessScore() != null ? servicePlan.getScoring().getMentalWellnessScore() : 0;
        }
        if (ServicePlanNeedType.PHYSICAL_WELLNESS == domain) {
            score = servicePlan.getScoring().getPhysicalWellness() != null ? servicePlan.getScoring().getPhysicalWellness() : 0;
        }
        if (ServicePlanNeedType.LEGAL == domain) {
            score = servicePlan.getScoring().getLegalScore() != null ? servicePlan.getScoring().getLegalScore() : 0;
        }
        if (ServicePlanNeedType.FINANCES == domain) {
            score = servicePlan.getScoring().getFinancesScore() != null ? servicePlan.getScoring().getFinancesScore() : 0;
        }
        if (ServicePlanNeedType.MEDICAL_OTHER_SUPPLY == domain) {
            score = servicePlan.getScoring().getMedicalOtherSupplyScore() != null ? servicePlan.getScoring().getMedicalOtherSupplyScore() : 0;
        }
        if (ServicePlanNeedType.MEDICATION_MGMT_ASSISTANCE == domain) {
            score = servicePlan.getScoring().getMedicationMgmtAssistanceScore() != null ? servicePlan.getScoring().getMedicationMgmtAssistanceScore() : 0;
        }
        if (ServicePlanNeedType.HOME_HEALTH == domain) {
            score = servicePlan.getScoring().getHomeHealthScore() != null ? servicePlan.getScoring().getHomeHealthScore() : 0;
        }

        return score;

    }
    
    private void addProgramInfoDetails(Document document, ServicePlan servicePlan) throws DocumentException {
        document.add(getTitle("Program Info"));
        addParagraphLine(document);
        PdfPTable table = new PdfPTable((new float[] { 1, 2 }));
        table.setSplitLate(false);
        table.setWidthPercentage(98);
        if (servicePlan.getDateCreated() != null) {
            table = addTableCells("Date Started",
                    (DATE_FORMATTER_MM_DD_YYYY.format(servicePlan.getDateCreated())).toString(), table);
        }
        if (servicePlan.getDateCompleted() != null) {
            table = addTableCells("Date Completed",
                    (DATE_FORMATTER_MM_DD_YYYY.format(servicePlan.getDateCompleted())).toString(), table);
        }
        if (servicePlan.getEmployee() != null && servicePlan.getEmployee().getFullName() != null) {
            table = addTableCells("Service Coordinator",
                    StringUtils.isNotEmpty(servicePlan.getEmployee().getFullName())
                            ? servicePlan.getEmployee().getFullName()
                            : "",
                    table);
        }
        String phone = null, email = null;
        for (PersonTelecom telecom : servicePlan.getEmployee().getPerson().getTelecoms()) {
            if (PersonTelecomCode.HP.name().equalsIgnoreCase(telecom.getUseCode())
                    && StringUtils.isNotEmpty(telecom.getValue())) {
                phone = telecom.getValue();
            }else if(PersonTelecomCode.WP.name().equalsIgnoreCase(telecom.getUseCode())
                    && StringUtils.isNotEmpty(telecom.getValue())) {
            	phone = telecom.getValue();
            }
            if (PersonTelecomCode.EMAIL.name().equalsIgnoreCase(telecom.getUseCode())
                    && StringUtils.isNotEmpty(telecom.getValue())) {
                email = telecom.getValue();
            }
        }
        if (StringUtils.isNotEmpty(phone)) {
            table = addTableCells("Phone number", phone, table);
        }
        if (StringUtils.isNotEmpty(email)) {
            table = addTableCells("Email", email, table);
        }
        document.add(table);
    }

    private void addClientInfoDetails(Document document, Long clientId) throws DocumentException {
        Resident resident = residentDao.get(clientId);
        document.add(getTitle(" "));
        document.add(getTitle("Client Info"));
        addParagraphLine(document);
        PdfPTable table = new PdfPTable((new float[] { 1, 2 }));
        table.setSplitLate(false);
        table.setWidthPercentage(98);
        StringBuilder patientName = new StringBuilder();
        if (resident.getFirstName() != null) {
            patientName.append(resident.getFirstName());
            if (resident.getLastName() != null) {
                patientName.append(" ");
            }
        }
        if (resident.getLastName() != null) {
            patientName.append(resident.getLastName());
        }
        if (StringUtils.isNotEmpty(patientName.toString())) {
            table = addTableCells("Patient Name", patientName.toString(), table);
        }
        if (resident.getGender() != null && StringUtils.isNotEmpty(resident.getGender().getDisplayName())) {
            table = addTableCells("Gender", resident.getGender().getDisplayName(), table);
        }
        if (StringUtils.isNotEmpty(resident.getSsnLastFourDigits())) {
            table = addTableCells("SSN", "###-##-" + resident.getSsnLastFourDigits(), table);
        }
        if (resident.getBirthDate() != null) {
            table = addTableCells("Date of Birth",
            		resident.getBirthDate() != null ? DATE_FORMATTER_MM_DD_YYYY.format(resident.getBirthDate()).toString()
                            : "",
                    table);
        }
        String riskScore = null;
        if (StringUtils.isNotEmpty(riskScore)) { // TODO
            table = addTableCells("Risk Score", riskScore, table);
        }
        if (resident.getPerson() != null && CollectionUtils.isNotEmpty(resident.getPerson().getAddresses())
                && resident.getPerson().getAddresses().get(0).getFullAddress() != null) {
            table = addTableCells("Address", resident.getPerson().getAddresses().get(0).getFullAddress(), table);
        }
        String phone = null, email = null;
        for (PersonTelecom telecom : resident.getPerson().getTelecoms()) {
            if (PersonTelecomCode.HP.name().equalsIgnoreCase(telecom.getUseCode())
                    && StringUtils.isNotEmpty(telecom.getValue())) {
                phone = telecom.getValue();
            }else if (PersonTelecomCode.WP.name().equalsIgnoreCase(telecom.getUseCode())
                    && StringUtils.isNotEmpty(telecom.getValue())) {
                phone = telecom.getValue();
            }
            if (PersonTelecomCode.EMAIL.name().equalsIgnoreCase(telecom.getUseCode())
                    && StringUtils.isNotEmpty(telecom.getValue())) {
                email = telecom.getValue();
            }
        }
        if (StringUtils.isNotEmpty(phone)) {
            table = addTableCells("Phone", phone, table);
        }
        if (StringUtils.isNotEmpty(email)) {
            table = addTableCells("Email", email, table);
        }
        document.add(table);
    }

    private Paragraph getTitle(String text) {
        Paragraph paragraph = new Paragraph(text, HELVETICA_14_BOLD_DARKGREY);
        return paragraph;
    }

    private PdfPTable addTableCellWithNeed(int index, int priority, ServicePlanGoalNeed servicePlanGoalNeed,
            PdfPTable table) {
        PdfPCell cell_table = new PdfPCell(getTableParagraphBold(String.valueOf(index)));
        cell_table = setPriority(cell_table, priority);
        cell_table.setBorder(Rectangle.NO_BORDER);
        cell_table.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell_table.setPadding(5);
        table.addCell(cell_table);
        table = addTableCellWithHeading("Priority", servicePlanGoalNeed.getPriority().getDisplayName(), table);
        cell_table = new PdfPCell(getTableParagraph(" "));
        cell_table = setPriority(cell_table, priority);
        cell_table.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell_table);
        if (servicePlanGoalNeed.getNeedOpportunity() != null) {
            table = addTableCellWithHeading("Need / Opportunity", servicePlanGoalNeed.getNeedOpportunity(), table);
            table.addCell(cell_table);
        }
        if (servicePlanGoalNeed.getProficiencyGraduationCriteria() != null) {
            table = addTableCellWithHeading("Proficiency / Graduation Criteria",
                    servicePlanGoalNeed.getProficiencyGraduationCriteria(), table);
        }

        return table;
    }

    private PdfPTable addTableCellWithTask(int index, int priority, ServicePlanEducationNeed servicePlanEducationNeed,
            PdfPTable table) {

        PdfPCell cell_table = new PdfPCell(getTableParagraphBold(String.valueOf(index)));
        cell_table = setPriority(cell_table, priority);
        cell_table.setBorder(Rectangle.NO_BORDER);
        cell_table.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell_table.setPadding(5);
        table.addCell(cell_table);
        cell_table = new PdfPCell();
        cell_table.addElement(getTableParagraph(servicePlanEducationNeed.getActivationOrEducationTask()));
        cell_table.setBorderColor(LINE_COLOR);
        cell_table.setPadding(5);
        table.addCell(cell_table);
        cell_table = new PdfPCell();
        cell_table.addElement(getTableParagraph(servicePlanEducationNeed.getPriority().getDisplayName()));
        cell_table.setBorderColor(LINE_COLOR);
        cell_table.setPadding(5);
        table.addCell(cell_table);
        if (servicePlanEducationNeed.getTargetCompletionDate() != null) {
            cell_table = new PdfPCell();
            cell_table.addElement(getTableParagraph(
                    (DATE_FORMATTER_MM_DD_YYYY.format(servicePlanEducationNeed.getTargetCompletionDate())).toString()));
        } else {
            cell_table = new PdfPCell();
            cell_table.addElement(getTableParagraph(" "));
        }
        cell_table.setBorderColor(LINE_COLOR);
        cell_table.setPadding(5);
        table.addCell(cell_table);
        if (servicePlanEducationNeed.getCompletionDate() != null) {
            cell_table = new PdfPCell();
            cell_table.addElement(getTableParagraph(
                    (DATE_FORMATTER_MM_DD_YYYY.format(servicePlanEducationNeed.getCompletionDate())).toString()));
        } else {
            cell_table = new PdfPCell();
            cell_table.addElement(getTableParagraph(" "));
        }
        cell_table.setBorderColor(LINE_COLOR);
        cell_table.setPadding(5);
        table.addCell(cell_table);
        return table;
    }

    private PdfPCell setPriority(PdfPCell cell, int priority) {
        switch (priority) {
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

    private Image addScoringImg(int score) throws BadElementException, MalformedURLException, IOException {

        Image img = Image.getInstance(score0Image.getURL());

        switch (score) {
        case 0:
            img = Image.getInstance(score0Image.getURL());
            break;
        case 1:
            img = Image.getInstance(score1Image.getURL());
            break;
        case 2:
            img = Image.getInstance(score2Image.getURL());
            break;
        case 3:
            img = Image.getInstance(score3Image.getURL());
            break;
        case 4:
            img = Image.getInstance(score4Image.getURL());
            break;
        case 5:
            img = Image.getInstance(score5Image.getURL());
            break;
        default:
            img = Image.getInstance(score0Image.getURL());
            break;
        }
        img.scalePercent(5);
        img.setScaleToFitLineWhenOverflow(true);
        return img;

    }

    private PdfPTable addTableCellsWithBorder(String text, PdfPTable table) {

        PdfPCell cell_table = new PdfPCell(getTableParagraph(text));
        cell_table.addElement(getTableParagraph(text));
        cell_table.setBorderColor(LINE_COLOR);
        cell_table.setPadding(5);
        table.addCell(cell_table);
        return table;
    }

    private PdfPCell setCellNoBorder(PdfPCell cell_table) {
        cell_table.setBorder(Rectangle.NO_BORDER);
        return cell_table;
    }

    private PdfPTable addTableCellTitle(String text, PdfPTable table, int border) {

        PdfPCell cell_table = new PdfPCell();
        cell_table.addElement(getTableParagraphBold(text));
        cell_table.setBorder(Rectangle.NO_BORDER | Rectangle.TOP | border);
        cell_table.setBackgroundColor(GREY_COLOR);
        cell_table.setBorderColor(LINE_COLOR);
        cell_table.setPadding(5);
        table.addCell(cell_table);
        return table;
    }

    private PdfPTable addTableCellTitle_RightAlignment(String text, PdfPTable table, int border) {
        PdfPCell cell_table = new PdfPCell();
        cell_table.addElement(getTableParagraphBold_RightAllignment(text));
        cell_table.setBorder(Rectangle.NO_BORDER | Rectangle.TOP | border);
        cell_table.setBackgroundColor(GREY_COLOR);
        cell_table.setBorderColor(LINE_COLOR);
        cell_table.setPadding(5);
        table.addCell(cell_table);
        return table;
    }

    private PdfPTable addTableCellLine(PdfPTable table) {
        table.addCell(setCellNoBorder(new PdfPCell(getTableParagraph(" "))));
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
        cell_table.addElement(getTableParagraphBold(text));
        cell_table.addElement(getTableParagraph(data));
        cell_table.setBorderColor(LINE_COLOR);
        cell_table.setPadding(5);
        table.addCell(cell_table);
        return table;
    }

    private Paragraph getParagraph(String text) {
        Paragraph paragraph = new Paragraph(text, HELVETICA_12);
        paragraph.setSpacingAfter(5f);
        return paragraph;
    }

    private Paragraph getParagraphBold(String text) {
        Paragraph paragraph = new Paragraph(text, HELVETICA_12_BOLD);
        paragraph.setSpacingAfter(5f);
        return paragraph;
    }

    private Paragraph getTableParagraph(String text) {
        Paragraph paragraph = new Paragraph(text, HELVETICA_10);
        paragraph.setSpacingAfter(5f);
        return paragraph;
    }

    private Paragraph getTableParagraphBold(String text) {
        Paragraph paragraph = new Paragraph(text, HELVETICA_10_BOLD);
        paragraph.setSpacingAfter(5f);
        return paragraph;
    }

    private Paragraph getTableParagraphBold_RightAllignment(String text) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Phrase(text, HELVETICA_10_BOLD));
        paragraph.setAlignment(Element.ALIGN_RIGHT);
        paragraph.setSpacingAfter(5f);
        return paragraph;
    }

    private void addParagraphLine(Document document) throws DocumentException {
        PdfPTable table = new PdfPTable((new float[] { 1 }));
        table.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(getTableParagraphBold(" "));
        cell.setBorder(Rectangle.NO_BORDER | Rectangle.BOTTOM);
        cell.setBorderColor(GREY_COLOR);
        cell.setBorderWidth(2f);
        table.addCell(cell);
        table.setSpacingAfter(3f);
        document.add(table);
    }
    
    
}