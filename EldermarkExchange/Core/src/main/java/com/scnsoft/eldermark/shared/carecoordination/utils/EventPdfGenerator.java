package com.scnsoft.eldermark.shared.carecoordination.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.scnsoft.eldermark.shared.carecoordination.*;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class EventPdfGenerator {

    private static Font HELVETICA_8 = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
    private static Font HELVETICA_8_ITALIC = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC);
    private static Font HELVETICA_9 = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
    private static Font HELVETICA_9_BOLD = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
    private static Font HELVETICA_9_BOLD_ITALIC = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLDITALIC);
    private static Font HELVETICA_9_BOLD_ITALIC_RED = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLDITALIC, BaseColor.RED);
    private static Font HELVETICA_10_BOLD = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static Font HELVETICA_17_BOLD = new Font(Font.FontFamily.HELVETICA, 17, Font.BOLD);

    private static String UNKNOWN_VALUE = "--";

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
    private static SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a z");
    static{
        dateTimeFormatter.setTimeZone(TimeZone.getTimeZone("CST"));
    }


    public static  ByteArrayOutputStream generate(EventDto eventDto) throws DocumentException {


//        String documentName = String.format("Event_Details_%s.pdf", eventDto.getPatient().getFirstName() + "_" + eventDto.getPatient().getLastName());
            Document document = new Document(PageSize.A4, 20, 20, 80, 40);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();
            createDocumentBody(document, eventDto);
            document.close();
            return baos;
    }

    private static void createCell (String label, String value, PdfPTable table) {
        if(StringUtils.isNotBlank(value)) {
            table.addCell(createCellWithTextField(label, value));
        }
    }

    private static void createCell (String label, Boolean value, PdfPTable table) {
        if (value!=null) {
           table.addCell(createCellWithTextField(label, value ? "Yes" : "No"));
        }
    }

    private static void createCell (String label, Date value, PdfPTable table) {
        if(value!=null) {
            table.addCell(createCellWithTextField(label, dateTimeFormatter.format(value)));
        }
    }

    private static void createDateCell (String label, Date value, PdfPTable table) {
        if(value!=null) {
            table.addCell(createCellWithTextField(label, dateFormatter.format(value)));
        }
    }

    private static PdfPTable createTable () {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
//        table.setHeaderRows(1);
//        table.setSpacingBefore(12f);
//        table.setSpacingAfter(6f);
//        table.getDefaultCell().setMinimumHeight(15);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
        return table;
    }

    private static void createDocumentBody(Document document, EventDto eventDto) throws DocumentException {
        Paragraph paragraph = new Paragraph("Patient Info", HELVETICA_10_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        PdfPTable table=createTable();

        PatientDto patient = eventDto.getPatient();
        AdtEventDto adtEvent = eventDto.getAdtEvent();

        createCell("Patient Name : ",patient.getDisplayName(),table);
        createCell("Social Security Number : ",patient.getSsn(),table);
        createDateCell("Date of Birth : ",patient.getBirthDate(),table);
        createCell("Gender : ",patient.getGender(),table);
        createCell("Marital Status : ",patient.getMaritalStatus(),table);
        createCell("Address : ",patient.getAddress().getDisplayAddress(),table);
        createCell("Organization : ",patient.getOrganization(),table);
        if (adtEvent!=null) {
            createCell("Patient Identifier : ", adtEvent.getPatientIdentifier(), table);
            createCell("Mother's Maiden Name : ", adtEvent.getMothersMaidenName(), table);
            createCell("Mother Identifier : ", adtEvent.getMotherIdentifier(), table);
            createCell("Patient Alias : ", adtEvent.getPatientAlias(), table);
            createCell("Phone Number - Home : ", adtEvent.getPhoneNumberHome(), table);
            createCell("Phone Number - Business : ", adtEvent.getPhoneNumberBusiness(), table);
            createCell("Race : ", adtEvent.getRace(), table);
            createCell("Primary Language : ", adtEvent.getPrimaryLanguage(), table);
            createCell("Religion : ", adtEvent.getReligion(), table);
            createCell("Patient Account Number : ", adtEvent.getPatientAccountNumber(), table);
            createCell("Driver's License Number : ", adtEvent.getDriverLicenseNumber(), table);
            createCell("Mother Identifier : ", adtEvent.getMotherIdentifier(), table);
            createCell("Ethnic Group : ", adtEvent.getEtnicGroup(), table);
            createCell("Birth Place:", adtEvent.getBirthPlace(), table);
            if (adtEvent.getBirthOrder() != null && adtEvent.getBirthOrder() != 0)
                createCell("Birth Order:", adtEvent.getBirthOrder().toString(), table);
            createCell("Veterans Military Status:", adtEvent.getVeteransMilitaryStatus(), table);
            createCell("Nationality:", adtEvent.getNationality(), table);
            createCell("Death Date:", adtEvent.getDeathDateTime(), table);
            if (adtEvent.getDeathIndicator()) {
                createCell("Death Indicator:", adtEvent.getDeathIndicator(), table);
            }
        }
        document.add(table);


        paragraph = new Paragraph("Event Essentials", HELVETICA_10_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        table=createTable();
        EmployeeDto employeeDto = eventDto.getEmployee();
        EventDetailsDto eventDetailsDto = eventDto.getEventDetails();
        createCell("Person Submitting Event : ",employeeDto.getDisplayName(),table);
        createCell("Care Team Role : ",employeeDto.getRole(),table);
        createCell("Event Date and Time: ",eventDetailsDto.getEventDatetime(),table);
        createCell("Event Type : ",eventDetailsDto.getEventType(),table);
        createCell("Emergency Department Visit: ",eventDetailsDto.isEmergencyVisit(),table);
        createCell("Overnight In-patient : ",eventDetailsDto.isOvernightPatient(),table);

        if (adtEvent!=null) {
            createCell("Event Type Code : ", adtEvent.getEventTypeCode(), table);
            createCell("Recorded Date/Time : ", adtEvent.getRecordedDateTime(), table);
            createCell("Event Reason Code : ", adtEvent.getEventReasonCode(), table);
            createCell("Event Occurred : ", adtEvent.getEventOccured(), table);
        }
        document.add(table);


        paragraph = new Paragraph("Event Description", HELVETICA_10_BOLD);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);

        table=createTable();
        createCell("Location : ",eventDetailsDto.getLocation(),table);
        createCell("Injury : ",eventDetailsDto.isInjury(),table);
        createCell("Situation : ",eventDetailsDto.getSituation(),table);
        createCell("Background : ",eventDetailsDto.getBackground() ,table);
        createCell("Assessment : ",eventDetailsDto.getAssessment() ,table);
        createCell("Follow Up Expected : ",eventDetailsDto.isFollowUpExpected() ,table);
        createCell("Follow Up Details : ",eventDetailsDto.getFollowUpDetails() ,table);
        document.add(table);

        if (eventDto.isIncludeTreatingPhysician() || eventDto.isIncludeHospital()) {
            paragraph = new Paragraph("Treatment Details", HELVETICA_10_BOLD);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);

            if (eventDto.isIncludeTreatingPhysician()) {

                paragraph = new Paragraph("Details of Treating Physician", HELVETICA_9_BOLD);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                document.add(paragraph);
                table=createTable();
                NameWithAddressDto treatingPhysician =  eventDto.getTreatingPhysician();
                createCell("Physician Name : ",treatingPhysician.getDisplayName(),table);
                if (treatingPhysician.isIncludeAddress()) {
                    createCell("Address : ", treatingPhysician.getAddress().getDisplayAddress(), table);
                }
                createCell("Phone : ",treatingPhysician.getPhone(),table);
                document.add(table);
            }

            if (eventDto.isIncludeHospital()) {

                paragraph = new Paragraph("Details of Treating Hospital", HELVETICA_9_BOLD);
                paragraph.setAlignment(Element.ALIGN_CENTER);
                document.add(paragraph);
                table=createTable();
                HospitalDto treatingHospital =  eventDto.getTreatingHospital();
                createCell("Hospital/Clinic : ",treatingHospital.getName(),table);
                if (treatingHospital.isIncludeAddress()) {
                    createCell("Address : ", treatingHospital.getAddress().getDisplayAddress(), table);
                }
                createCell("Phone : ",treatingHospital.getPhone(),table);
                document.add(table);
            }


        }
        if (eventDto.isIncludeManager()) {
            paragraph = new Paragraph("Details of Responsible Manager", HELVETICA_10_BOLD);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            table=createTable();

            ManagerDto managerDto = eventDto.getManager();
            createCell("Manager Name : ", managerDto.getDisplayName(), table);
            createCell("Phone  : ", managerDto.getPhone(), table);
            createCell("Email: ", managerDto.getEmail(), table);

            document.add(table);
        }

        if (eventDto.isIncludeResponsible()) {
            paragraph = new Paragraph("Details of Registered Nurse (RN)", HELVETICA_10_BOLD);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            table=createTable();

            NameWithAddressDto responsible = eventDto.getResponsible();
            createCell("RN Name : ", responsible.getDisplayName(), table);
            if (responsible.isIncludeAddress()) {
                createCell("Address : ", responsible.getAddress().getDisplayAddress(), table);
            }
            document.add(table);
        }

        PatientVisitDto patientVisitDto = eventDto.getPatientVisit();
        if (patientVisitDto!=null) {
            paragraph = new Paragraph("Patient Visit", HELVETICA_10_BOLD);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            table=createTable();

            createCell("Patient Class : ", patientVisitDto.getPatientClass(), table);
            createCell("Attending Doctor : ", patientVisitDto.getAttendingDoctor(), table);
            createCell("Referring Doctor : ", patientVisitDto.getReferringDoctor(), table);
            createCell("Consulting Doctor : ", patientVisitDto.getConsultingDoctor(), table);
            createCell("Ambulatory Status : ", patientVisitDto.getAmbulatoryStatus(), table);
            createCell("Admission type : ", patientVisitDto.getAdmissionType(), table);
            createCell("Preadmit Test Indicator : ", patientVisitDto.getPreadmitTestIndicator(), table);
            createCell("Re-admission Indicator : ", patientVisitDto.getReadmissionIndicator(), table);
            createCell("Admit Source : ", patientVisitDto.getAdmitSource(), table);
            createCell("Discharge Disposition : ", patientVisitDto.getDischargeDisposition(), table);
            createCell("Discharged to Location : ", patientVisitDto.getDischargedToLocation(), table);
            createCell("Admit Date/Time : ", patientVisitDto.getAdmitDateTime(), table);
            createCell("Discharge Date/Time : ", patientVisitDto.getDischargeDateTime(), table);

            document.add(table);
        }

        ProcedureDto procedureDto = eventDto.getProcedure();
        if (procedureDto!=null) {
            paragraph = new Paragraph("Procedures", HELVETICA_10_BOLD);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            table=createTable();

            createCell("Procedure Description  : ", procedureDto.getDescription(), table);
            createCell("Procedure Date/Time : ", procedureDto.getDateTime(), table);
            createCell("Procedure Code Identifier : ", procedureDto.getCode().getIdentifier(), table);
            createCell("Procedure Code Text : ", procedureDto.getCode().getText(), table);
            createCell("Procedure Code Name Of Coding System : ", procedureDto.getCode().getNameOfCodingSystem(), table);
            createCell("Procedure Code Alternate Identifier : ", procedureDto.getCode().getAlternateIdentifier(), table);
            createCell("Procedure Code Alternate Text : ", procedureDto.getCode().getAlternateText(), table);
            createCell("Procedure Code Name Of Alternate Coding System : ", procedureDto.getCode().getNameOfAlternateCodingSystem(), table);

            if (procedureDto.getAssociatedDiagnosisCode()!=null) {
                createCell("Associated Diagnosis Code Identifier : ", procedureDto.getAssociatedDiagnosisCode().getIdentifier(), table);
                createCell("Associated Diagnosis Code Text : ", procedureDto.getAssociatedDiagnosisCode().getText(), table);
                createCell("Associated Diagnosis Code Name Of Coding System : ", procedureDto.getAssociatedDiagnosisCode().getNameOfCodingSystem(), table);
                createCell("Associated Diagnosis Code Alternate Identifier : ", procedureDto.getAssociatedDiagnosisCode().getAlternateIdentifier(), table);
                createCell("Associated Diagnosis Code Alternate Text : ", procedureDto.getAssociatedDiagnosisCode().getAlternateText(), table);
                createCell("Associated Diagnosis Code Of Alternate Coding System : ", procedureDto.getAssociatedDiagnosisCode().getNameOfAlternateCodingSystem(), table);
            }
            document.add(table);
        }

        InsuranceDto insuranceDto = eventDto.getInsurance();
        if (insuranceDto!=null) {
            paragraph = new Paragraph("Insurance", HELVETICA_10_BOLD);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            table=createTable();

            createCell("Insurance Plan ID : ", insuranceDto.getPlanId(), table);
            createCell("Insurance Company ID : ", insuranceDto.getCompanyId(), table);
            createCell("Insurance Company Name : ", insuranceDto.getCompanyName(), table);
            createDateCell("Plan Effective Date : ",  insuranceDto.getPlanEffectiveDate(), table);
            createDateCell("Plan Expiration Date : ", insuranceDto.getPlanExpirationDate(), table);
            createCell("Plan Type : ", insuranceDto.getPlanType(), table);

            document.add(table);
        }
    }

    private PdfPCell createCellWithTextField(String label, String value, int columnSpan) {
        PdfPCell cell = createCellWithTextField(label, value);
        cell.setColspan(columnSpan);
        return cell;
    }

    private static PdfPCell createCellWithTextField(String label, String value) {
        return createCellWithTextField(label, value, HELVETICA_9_BOLD_ITALIC);
    }

    private static PdfPCell createCellWithTextField(String label, String value, Font labelFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOTTOM);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setMinimumHeight(15);

        Phrase p = new Phrase();
        p.add(new Phrase(label, labelFont));
        p.add(new Phrase(value, HELVETICA_9));
//        p.add(new Phrase(showUnknownIfBlank(value), HELVETICA_9));
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

    private static String staticshowUnknownIfBlank(String str) {
        return (str == null || str.isEmpty()) ? UNKNOWN_VALUE : str;
    }

    private String showEmptyIfBlank(String str) {
        return (str == null || str.isEmpty()) ? "" : str;
    }

    private String displayDate(Date date) {
        return (date == null) ? UNKNOWN_VALUE : dateFormatter.format(date);
    }
}
