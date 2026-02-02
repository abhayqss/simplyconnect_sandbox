package com.scnsoft.eldermark.services.facesheet;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.Report;
import com.scnsoft.eldermark.services.ReportGenerator;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.shared.DocumentType;
import com.scnsoft.eldermark.shared.FaceSheetDto;
import com.scnsoft.eldermark.shared.exceptions.FacesheetGenerationException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component("FacesheetGenerator")
public class FacesheetGenerator implements ReportGenerator {

    @Autowired
    private ResidentService residentService;

    @Autowired
    private FacesheetService facesheetService;

    private static Font HELVETICA_8 = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
    private static Font HELVETICA_8_ITALIC = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC);
    private static Font HELVETICA_9 = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
    private static Font HELVETICA_9_BOLD = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
    private static Font HELVETICA_9_BOLD_ITALIC = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLDITALIC);
    private static Font HELVETICA_9_BOLD_ITALIC_RED = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLDITALIC, BaseColor.RED);
    private static Font HELVETICA_10_BOLD = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static Font HELVETICA_17_BOLD = new Font(Font.FontFamily.HELVETICA, 17, Font.BOLD);

    private static String UNKNOWN_VALUE = "--";

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
    private SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a (z)");

    @Override
    public Report generate(Long residentId) {
        return generate(residentId, false);
    }

    @Override
    public Report generate(Long residentId, List<Long> residentIds) {
        // temporary solution: this generate() method is not used, but since it's declared on the interface it has to be implemented
        return generate(residentId, residentIds.size() > 1);
    }

    @Override
    public Report generate(Long residentId, List<Long> residentIds, Integer timeZoneOffsetInMinutes) {
        return generate(residentId, residentIds.size() > 1, timeZoneOffsetInMinutes);
    }

    @Override
    public Report generate(Long residentId, boolean aggregated) {
        return generate(residentId, aggregated, null);
    }

    @Override
    @Transactional
    public Report generate(Long residentId, boolean aggregated, Integer timeZoneOffsetInMinutes) {
        FaceSheetDto facesheet = facesheetService.construct(residentId, aggregated);

        Resident resident = residentService.getResident(residentId);
        String documentName = String.format("Facesheet_%s_%s.pdf", resident.getFirstName(), resident.getLastName());

        ByteArrayOutputStream buffer = new ByteArrayOutputStream(1024);
        try {
            writeToStream(facesheet, buffer, timeZoneOffsetInMinutes);
        } catch (Exception e) {
            throw new FacesheetGenerationException();
        }

        Report document = new Report();
        document.setDocumentTitle(documentName);
        document.setMimeType("application/pdf");
        document.setInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        return document;
    }

    @Override
    public Report metadata() {
        Report document = new Report();
        document.setDocumentTitle("FACESHEET.PDF");
        document.setMimeType("application/pdf");
        document.setDocumentType(DocumentType.FACESHEET);
        return document;
    }

    private void writeToStream(FaceSheetDto faceSheetDto, OutputStream out, Integer timeZoneOffsetInMinutes) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4, 20, 20, 80, 40);

        // First pass: create document
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, buffer);

        document.open();
        createDocumentBody(document, faceSheetDto, timeZoneOffsetInMinutes);
        document.close();

        // Second pass: add the header and the footer
        PdfReader reader = new PdfReader(buffer.toByteArray());
        PdfStamper stamper = new PdfStamper(reader, out);

        int n = reader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {
            createDocumentHeader(document, faceSheetDto, i, n, stamper.getOverContent(i), timeZoneOffsetInMinutes);
            if(i != n) {
                createDocumentFooter(document, stamper.getOverContent(i));
            }
        }

        stamper.close();
        reader.close();
    }

    private void createDocumentHeader(Document document, FaceSheetDto faceSheetDto, int pageNumber, int totalNumber, PdfContentByte pdfContentByte, Integer timeZoneOffsetInMinutes) {
        Rectangle page = document.getPageSize();

        PdfPTable header = new PdfPTable(6);
        header.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
        header.setLockedWidth(true);

        PdfPCell companyInfo = new PdfPCell();
        Phrase content = new Phrase();
        content.add(new Phrase(12, showUnknownIfBlank(faceSheetDto.getCompanyName()) + "\n", HELVETICA_8));
        if (StringUtils.isNotBlank(faceSheetDto.getCompanyAddress1()) || StringUtils.isNotBlank(faceSheetDto.getCompanyAddress2())) {
            content.add(new Phrase(12, showEmptyIfBlank(faceSheetDto.getCompanyAddress1()) + "\n", HELVETICA_8));
            content.add(new Phrase(12, showEmptyIfBlank(faceSheetDto.getCompanyAddress2()) + "\n", HELVETICA_8));
        }
        content.add(new Phrase(12, showEmptyIfBlank(faceSheetDto.getCompanyPhone()) + "\n", HELVETICA_8));
        content.add(new Phrase(12, showEmptyIfBlank(faceSheetDto.getCompanyFax()) + "\n", HELVETICA_8));
        companyInfo.setPhrase(content);
        companyInfo.setHorizontalAlignment(Element.ALIGN_LEFT);
        companyInfo.setVerticalAlignment(Element.ALIGN_BOTTOM);
        companyInfo.setBorder(Rectangle.BOTTOM);
        companyInfo.setColspan(3);

        String printedTime = "";
        if (faceSheetDto.getFaceSheetPrintedTime() != null) {
            if (timeZoneOffsetInMinutes != null) {
                DateTimeZone dtz = DateTimeZone.forOffsetMillis(timeZoneOffsetInMinutes * 60 * 1000);
                DateTime dateTime = new DateTime(faceSheetDto.getFaceSheetPrintedTime(), dtz);
                DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy, hh:mm aaa");
                printedTime = dtf.print(dateTime);
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy, hh:mm aaa");
                printedTime = formatter.format(faceSheetDto.getFaceSheetPrintedTime());
            }
        }

        PdfPCell docInfo = new PdfPCell();
        content = new Phrase();
        content.add(new Phrase("Face Sheet\n", HELVETICA_17_BOLD));
        content.add(new Phrase(String.format("\nPage %d of %d\n", pageNumber, totalNumber), HELVETICA_9));
        content.add(new Phrase(String.format("Printed: %s", showUnknownIfBlank(printedTime)), HELVETICA_9));
        docInfo.setPhrase(content);
        docInfo.setHorizontalAlignment(Element.ALIGN_RIGHT);
        docInfo.setVerticalAlignment(Element.ALIGN_BOTTOM);
        docInfo.setBorder(Rectangle.BOTTOM);
        docInfo.setColspan(3);

        PdfPCell residentNameCell = new PdfPCell();
        content = new Phrase();
        content.add(new Phrase("Client Name:  ", HELVETICA_9_BOLD));
        content.add(new Phrase(showUnknownIfBlank(faceSheetDto.getResidentName()), HELVETICA_9));
        residentNameCell.setPhrase(content);
        residentNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        residentNameCell.setBorder(Rectangle.BOTTOM);
        residentNameCell.setColspan(3);
        residentNameCell.setMinimumHeight(20);

        PdfPCell preferredNameCell = new PdfPCell();
        content = new Phrase();
        content.add(new Phrase("Preferred Name:  ", HELVETICA_9_BOLD));
        content.add(new Phrase(showUnknownIfBlank(faceSheetDto.getPreferredName()), HELVETICA_9));
        preferredNameCell.setPhrase(content);
        preferredNameCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        preferredNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        preferredNameCell.setBorder(Rectangle.BOTTOM);
        preferredNameCell.setColspan(3);
        preferredNameCell.setMinimumHeight(20);

        header.addCell(companyInfo);
        header.addCell(docInfo);
        header.addCell(residentNameCell);
        header.addCell(preferredNameCell);

        header.writeSelectedRows(0, -1, document.leftMargin(), page.getHeight() - document.topMargin() + header.getTotalHeight(), pdfContentByte);
    }

    private void createDocumentFooter(Document document, PdfContentByte pdfContentByte) {
        Rectangle page = document.getPageSize();

        PdfPTable footer = new PdfPTable(1);
        PdfPCell nextPageCell = new PdfPCell(new Phrase("CONTINUED ON NEXT PAGE", HELVETICA_8_ITALIC));
        nextPageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        nextPageCell.setBorder(Rectangle.NO_BORDER);
        footer.addCell(nextPageCell);

        footer.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
        footer.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(), pdfContentByte);
    }

    private void createDocumentBody(Document document, FaceSheetDto faceSheetDto, Integer timeZoneOffsetInMinutes) throws DocumentException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingAfter(6f);

        String dateOfBirth = null;
        if (faceSheetDto.getDob() != null) {
            dateOfBirth = dateFormatter.format(faceSheetDto.getDob());
        }
        table.addCell(createCellWithTextField("DOB:", dateOfBirth));

        table.addCell(createCellWithTextField("Age:", faceSheetDto.getAge()));
        table.addCell(createCellWithTextField("Gender:", faceSheetDto.getGender()));
        table.addCell(createCellWithTextField("Religion:", faceSheetDto.getReligion()));
        table.addCell(createCellWithTextField("Marital Status:", faceSheetDto.getMaritalStatus()));
        table.addCell(createCellWithTextField("Race:", faceSheetDto.getRace()));
        table.addCell(createEmptyRow(3));
        table.addCell(createCellWithTextField("Primary Language:", faceSheetDto.getPrimaryLanguage()));
        table.addCell(createCellWithTextField("Veteran:", faceSheetDto.getVeteran(), 2));
        table.addCell(createCellWithTextField("Admission Date:", displayDate(faceSheetDto.getAdmissionDate())));
        table.addCell(createCellWithTextField("Unit:", faceSheetDto.getUnit()));
        table.addCell(createCellWithTextField("Start Of Care:", displayDate(faceSheetDto.getStartOfCare())));
        table.addCell(createCellWithTextField("Date Of Current Readmission:", displayDate(faceSheetDto.getReadmissionDate()), 3));
        table.addCell(createEmptyRow(3));
        table.addCell(createCellWithTextField("Home Phone:", faceSheetDto.getHomePhone()));
        table.addCell(createCellWithTextField("Other Phone:", faceSheetDto.getOtherPhone()));
        table.addCell(createCellWithTextField("Email:", faceSheetDto.getEmail()));
        table.addCell(createCellWithTextField("Previous Address:", faceSheetDto.getPreviousAddress(), 3));
        table.addCell(createCellWithTextField("Admitted From:", faceSheetDto.getAdmittedFrom(), 3));
        table.addCell(createCellWithTextField("County Admitted From:", faceSheetDto.getCountyAdmittedFrom(), 3));
        document.add(table);

        Paragraph paragraph = new Paragraph("CONTACTS", HELVETICA_10_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSpacingBefore(12f);
        table.setSpacingAfter(6f);
        table.getDefaultCell().setBorder(Rectangle.BOX);
        table.getDefaultCell().setMinimumHeight(15);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(new Phrase("Name", HELVETICA_9));
        table.addCell(new Phrase("Relationship", HELVETICA_9));
        table.addCell(new Phrase("Address", HELVETICA_9));
        table.addCell(new Phrase("Telecom", HELVETICA_9));
        if (CollectionUtils.isNotEmpty(faceSheetDto.getContactList())) {
            for (FaceSheetDto.Contact contact : faceSheetDto.getContactList()) {
                table.addCell(new Phrase(showUnknownIfBlank(contact.getName()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(contact.getRelationship()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(Objects.toString(contact.getAddress1(), "") + " " + Objects.toString(contact.getAddress2(), "")), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(contact.getPhone()), HELVETICA_9));
            }
        } else {
            for(int i = 0; i < table.getNumberOfColumns(); i++) {
                table.addCell(new Phrase(UNKNOWN_VALUE, HELVETICA_9));
            }
        }
        document.add(table);

        paragraph = new Paragraph("MEDICAL CONTACTS", HELVETICA_10_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingAfter(6f);
        if (CollectionUtils.isNotEmpty(faceSheetDto.getMedicalProfessional())) {
            for (FaceSheetDto.MedicalProfessional medicalProfessional : faceSheetDto.getMedicalProfessional()) {
                table.addCell(createCellWithTextField(medicalProfessional.getRole() + ":", medicalProfessional.getData()));
            }
        } else {
            table.addCell(createCellWithTextField("Medical Professional:", UNKNOWN_VALUE));
        }
        table.addCell(createCellWithTextField("Pharmacy:", faceSheetDto.getPharmacy()));
        table.addCell(createCellWithTextField("Hospital Pref:", faceSheetDto.getHospitalPref()));
        table.addCell(createCellWithTextField("Transportation:", faceSheetDto.getTransportation()));
        table.addCell(createCellWithTextField("Ambulance:", faceSheetDto.getAmbulance()));
        document.add(table);

        paragraph = new Paragraph("BILLING", HELVETICA_10_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingAfter(6f);
        if (CollectionUtils.isNotEmpty(faceSheetDto.getResponsibleParty())) {
            for (FaceSheetDto.Contact contact : faceSheetDto.getResponsibleParty()) {
                String contactInfo = buildContactInfoForBilling(contact);
                table.addCell(createCellWithTextField("Responsible Party:", contactInfo));
            }
        }
        table.addCell(createCellWithTextField("Primary Pay Type:",faceSheetDto.getPrimaryPayType()));
        table.addCell(createCellWithTextField("SSN:", faceSheetDto.getSsn()));
        table.addCell(createCellWithTextField("Medicare #:", faceSheetDto.getMedicareNumber()));
        table.addCell(createCellWithTextField("Medicaid #:", faceSheetDto.getMedicaidNumber()));

        table.addCell(createCellWithTextField("Health Plans:", faceSheetDto.getHealthPlanNumber()));
        table.addCell(createCellWithTextField("Dental Plan:", faceSheetDto.getDentalPlanNumber()));
        document.add(table);

        paragraph = new Paragraph("MEDICAL INFORMATION", HELVETICA_10_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        paragraph = new Paragraph("ALLERGIES", HELVETICA_9_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);


        table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSpacingBefore(12f);
        table.setSpacingAfter(6f);
        table.getDefaultCell().setBorder(Rectangle.BOX);
        table.getDefaultCell().setMinimumHeight(15);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(new Phrase("Substance", HELVETICA_9));
        table.addCell(new Phrase("Type", HELVETICA_9));
        table.addCell(new Phrase("Reaction(s)", HELVETICA_9));
        table.addCell(new Phrase("Start date", HELVETICA_9));
        table.addCell(new Phrase("Data Source", HELVETICA_9));
        if (CollectionUtils.isNotEmpty(faceSheetDto.getAllergies())) {
            for (FaceSheetDto.Allergy allergy : faceSheetDto.getAllergies()) {
                table.addCell(new Phrase(showUnknownIfBlank(allergy.getSubstance()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(allergy.getType()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(allergy.getReaction()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(displayDate(allergy.getStartDate())), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(allergy.getDataSource()), HELVETICA_9));
            }
        } else {
            for(int i = 0; i < table.getNumberOfColumns(); i++) {
                table.addCell(new Phrase(UNKNOWN_VALUE, HELVETICA_9));
            }
        }
        document.add(table);

        paragraph = new Paragraph("DIAGNOSIS", HELVETICA_9_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSpacingBefore(12f);
        table.setSpacingAfter(6f);
        table.getDefaultCell().setBorder(Rectangle.BOX);
        table.getDefaultCell().setMinimumHeight(15);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(new Phrase("Diagnosis", HELVETICA_9));
        table.addCell(new Phrase("Code", HELVETICA_9));
        table.addCell(new Phrase("Code Set", HELVETICA_9));
        table.addCell(new Phrase("Type", HELVETICA_9));
        table.addCell(new Phrase("Identified", HELVETICA_9));
        table.addCell(new Phrase("Data Source", HELVETICA_9));
        if (CollectionUtils.isNotEmpty(faceSheetDto.getDiagnosis())) {
            for (FaceSheetDto.Diagnosis diagnosis : faceSheetDto.getDiagnosis()) {
                table.addCell(new Phrase(showUnknownIfBlank(diagnosis.getDiagnosis()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(diagnosis.getCode()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(diagnosis.getCodeSet()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(diagnosis.getType()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(displayDate(diagnosis.getIdentified())), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(diagnosis.getDataSource()), HELVETICA_9));
            }
        } else {
            for(int i = 0; i < table.getNumberOfColumns(); i++) {
                table.addCell(new Phrase(UNKNOWN_VALUE, HELVETICA_9));
            }
        }
        document.add(table);


        paragraph = new Paragraph("ORDERS", HELVETICA_9_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSpacingBefore(12f);
        table.setSpacingAfter(6f);
        table.getDefaultCell().setBorder(Rectangle.BOX);
        table.getDefaultCell().setMinimumHeight(15);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(new Phrase("Name", HELVETICA_9));
        table.addCell(new Phrase("Start date", HELVETICA_9));
        if (CollectionUtils.isNotEmpty(faceSheetDto.getOrders())) {
            for (FaceSheetDto.Order order : faceSheetDto.getOrders()) {
                table.addCell(new Phrase(showUnknownIfBlank(order.getName()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(displayDate(order.getStartDate())), HELVETICA_9));
            }
        } else {
            for(int i = 0; i < table.getNumberOfColumns(); i++) {
                table.addCell(new Phrase(UNKNOWN_VALUE, HELVETICA_9));
            }
        }
        document.add(table);

        paragraph = new Paragraph("NOTES / ALERTS", HELVETICA_10_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingAfter(6f);
        table.addCell(createCellWithTextField("Evacuation Status:", faceSheetDto.getEvacuationStatus()));
        document.add(table);

        table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSpacingBefore(12f);
        table.setSpacingAfter(6f);
        table.getDefaultCell().setBorder(Rectangle.BOX);
        table.getDefaultCell().setMinimumHeight(15);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(new Phrase("Date", HELVETICA_9));
        table.addCell(new Phrase("Type", HELVETICA_9));
        table.addCell(new Phrase("Note", HELVETICA_9));
        if (CollectionUtils.isNotEmpty(faceSheetDto.getNotes())) {
            for (FaceSheetDto.Note note : faceSheetDto.getNotes()) {
                if (timeZoneOffsetInMinutes != null && note.getDate() != null) {
                    DateTimeZone dtz = DateTimeZone.forOffsetMillis(timeZoneOffsetInMinutes * 60 * 1000);
                    DateTime dateTime = new DateTime(note.getDate(), dtz);
                    DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy hh:mm a");
                    table.addCell(new Phrase(showUnknownIfBlank(dtf.print(dateTime)), HELVETICA_9));
                } else {
                    table.addCell(new Phrase(showUnknownIfBlank(displayDateTime(note.getDate())), HELVETICA_9));
                }
                table.addCell(new Phrase(showUnknownIfBlank(note.getType()), HELVETICA_9));
                if (StringUtils.isNotBlank(note.getNote())) {
                    table.addCell(new Phrase(showUnknownIfBlank(note.getNote()), HELVETICA_9));
                } else {
                    table.addCell(createCellWithNoteValue(note));
                }
            }
        } else {
            for(int i = 0; i < table.getNumberOfColumns(); i++) {
                table.addCell(new Phrase(UNKNOWN_VALUE, HELVETICA_9));
            }
        }
        document.add(table);

        paragraph = new Paragraph("ADVANCE DIRECTIVES", HELVETICA_10_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSpacingBefore(12f);
        table.setSpacingAfter(6f);
        table.getDefaultCell().setBorder(Rectangle.BOX);
        table.getDefaultCell().setMinimumHeight(15);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(new Phrase("Type", HELVETICA_9));
        table.addCell(new Phrase("Code", HELVETICA_9));
        table.addCell(new Phrase("Code Set", HELVETICA_9));
        table.addCell(new Phrase("Verification", HELVETICA_9));
        table.addCell(new Phrase("Date started", HELVETICA_9));
        table.addCell(new Phrase("Data Source", HELVETICA_9));
        if (CollectionUtils.isNotEmpty(faceSheetDto.getAdvanceDirectives())) {
            for (FaceSheetDto.AdvanceDirective advanceDirective : faceSheetDto.getAdvanceDirectives()) {
                table.addCell(new Phrase(showUnknownIfBlank(advanceDirective.getType()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(advanceDirective.getCode()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(advanceDirective.getCodeSet()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(advanceDirective.getVerification()), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(displayDate(advanceDirective.getDateStarted())), HELVETICA_9));
                table.addCell(new Phrase(showUnknownIfBlank(advanceDirective.getDataSource()), HELVETICA_9));
            }
        } else {
            for(int i = 0; i < table.getNumberOfColumns(); i++) {
                table.addCell(new Phrase(UNKNOWN_VALUE, HELVETICA_9));
            }
        }
        document.add(table);
    }

    private String buildContactInfoForBilling(FaceSheetDto.Contact contact) {
        StringBuilder result = new StringBuilder();
        boolean isFirst = false;
        if (StringUtils.isNotBlank(contact.getName())) {
            result.append(contact.getName());
            isFirst = false;
        }
        if (StringUtils.isNotBlank(contact.getAddress1())) {
            if (isFirst) {
                isFirst = false;
            } else {
                result.append(", ");
            }
            result.append(contact.getAddress1());
        }
        if (StringUtils.isNotBlank(contact.getAddress2())) {
            if (isFirst) {
                isFirst = false;
            } else {
                result.append(" ");
            }
            result.append(contact.getAddress2());
        }
        if (StringUtils.isNotBlank(contact.getPhone())) {
            if (!isFirst) {
                result.append(", ");
            }
            result.append(contact.getPhone());
        }
        return result.toString();
    }

    private PdfPCell createCellWithTextField(String label, String value, int columnSpan) {
        PdfPCell cell = createCellWithTextField(label, value);
        cell.setColspan(columnSpan);
        return cell;
    }

    private PdfPCell createCellWithTextField(String label, String value) {
        return createCellWithTextField(label, value, HELVETICA_9_BOLD_ITALIC);
    }

    private PdfPCell createCellWithTextField(String label, String value, Font labelFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOTTOM);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setMinimumHeight(15);

        Phrase p = new Phrase();
        p.add(new Phrase(label, labelFont));
        p.add(new Phrase("  ", HELVETICA_9));
        p.add(new Phrase(showUnknownIfBlank(value), HELVETICA_9));
        cell.setPhrase(p);

        return cell;
    }

    private PdfPCell createCellWithNoteValue(FaceSheetDto.Note note) {
        PdfPCell cell = new PdfPCell();
        Phrase p = new Phrase();
        boolean firstLine = true;
        if (StringUtils.isNotBlank(note.getSubjective())) {
            p.add(new Phrase("Subjective: ", HELVETICA_9_BOLD));
            p.add(new Phrase(note.getSubjective(), HELVETICA_9));
            p.add(new Phrase(".", HELVETICA_9));
            firstLine = false;
        }
        if (StringUtils.isNotBlank(note.getObjective())) {
            if (!firstLine) {
                p.add(Chunk.NEWLINE);
            } else {
                firstLine = false;
            }
            p.add(new Phrase("Objective: ", HELVETICA_9_BOLD));
            p.add(new Phrase(note.getObjective(), HELVETICA_9));
            p.add(new Phrase(".", HELVETICA_9));
        }
        if (StringUtils.isNotBlank(note.getAssessment())) {
            if (!firstLine) {
                p.add(Chunk.NEWLINE);
            } else {
                firstLine = false;
            }
            p.add(new Phrase("Assessment: ", HELVETICA_9_BOLD));
            p.add(new Phrase(note.getAssessment(), HELVETICA_9));
            p.add(new Phrase(".", HELVETICA_9));
        }
        if (StringUtils.isNotBlank(note.getPlan())) {
            if (!firstLine) {
                p.add(Chunk.NEWLINE);
            }
            p.add(new Phrase("Plan: ", HELVETICA_9_BOLD));
            p.add(new Phrase(note.getPlan(), HELVETICA_9));
            p.add(new Phrase(".", HELVETICA_9));
        }
        cell.setPhrase(p);
        return cell;
    }

    private PdfPCell createEmptyRow(int columnSpan) {
        PdfPCell cell = new PdfPCell();
        cell.setMinimumHeight(15);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setColspan(columnSpan);
        return cell;
    }

    private String showUnknownIfBlank(String str) {
        return (StringUtils.isBlank(str)) ? UNKNOWN_VALUE : str;
    }

    private String showEmptyIfBlank(String str) {
        return (StringUtils.isBlank(str)) ? "" : str;
    }

    private String displayDate(Date date) {
        return (date == null) ? UNKNOWN_VALUE : dateFormatter.format(date);
    }

    private String displayDateTime(Date date) {
        return (date == null) ? UNKNOWN_VALUE : dateTimeFormatter.format(date);
    }
}
