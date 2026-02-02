package com.scnsoft.eldermark.converter.fax;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.adt.datatype.*;
import com.scnsoft.eldermark.dto.adt.segment.*;
import com.scnsoft.eldermark.dto.notification.event.*;
import com.scnsoft.eldermark.util.DataUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class EventDetailsFaxContentConverter extends AbstractITextPdfFaxContentConverter<EventFaxNotificationDto> {

    private static Font HELVETICA_8_COMPLEX = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);

    static {
        HELVETICA_8_COMPLEX.setColor(137, 137, 137);
    }

    @Override
    protected void createDocumentBody(Document document, EventFaxNotificationDto faxDto) throws DocumentException {

        final float contentTableIndentationAfter = 12f;
        var details = faxDto.getDetails();
        document.add(createHeaderTable());

        addIndentedTable(document, createFaxDetailsTable(faxDto), CONTENT_INDENTATION, 15f);

        document.add(new LineSeparator());
        document.add(Chunk.NEWLINE);

        document.add(headerParagraph("Details"));
        document.add(subHeaderParagraph("Client Info"));
        addIndentedTable(document, createPatientInfoTable(details.getClient()), CONTENT_INDENTATION, contentTableIndentationAfter);

        document.add(new LineSeparator());
        document.add(Chunk.NEWLINE);

        document.add(subHeaderParagraph("Event Essentials"));
        addIndentedTable(document, createEventEssentialsTable(details.getEssentials()), CONTENT_INDENTATION, contentTableIndentationAfter);

        document.add(new LineSeparator());
        document.add(Chunk.NEWLINE);

        document.add(subHeaderParagraph("Event Description"));
        addIndentedTable(document, createEventDescriptionTable(details.getDescription()), CONTENT_INDENTATION, contentTableIndentationAfter);

        if (DataUtils.hasData(details.getTreatment())) {
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);

            document.add(subHeaderParagraph("Treatment Details"));
            if (DataUtils.hasData(details.getTreatment().getPhysician())) {
                document.add(subSubHeaderParagraph("Details of treating physician"));
                addIndentedTable(document, createTreatingPhysicianDetailsTable(details.getTreatment().getPhysician()), CONTENT_INDENTATION, contentTableIndentationAfter);
            }

            if (DataUtils.hasData(details.getTreatment().getHospital())) {
                document.add(subSubHeaderParagraph("Details of treating hospital"));
                addIndentedTable(document, createTreatingHospitalDetailsTable(details.getTreatment().getHospital()), CONTENT_INDENTATION, contentTableIndentationAfter);
            }
        }

        if (DataUtils.hasData(details.getResponsibleManager())) {
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);

            document.add(subHeaderParagraph("Responsible Manager Details"));
            addIndentedTable(document, createResponsibleManagerDetailsTable(details.getResponsibleManager()), CONTENT_INDENTATION, contentTableIndentationAfter);

        }

        if (DataUtils.hasData(details.getRegisteredNurse())) {
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);

            document.add(subHeaderParagraph("Registered Nurse (RN) Details"));
            addIndentedTable(document, createRegisteredNurseDetailsTable(details.getRegisteredNurse()), CONTENT_INDENTATION, contentTableIndentationAfter);
        }

        if (DataUtils.hasData(details.getProcedures())) {
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);

            document.add(subHeaderParagraph("Procedures"));
            var count = 1;
            for (var procedure : details.getProcedures()) {
                document.add(subSubHeaderParagraph("PROCEDURE #" + count++));
                addIndentedTable(document, createProcedureTable(procedure), CONTENT_INDENTATION, contentTableIndentationAfter);
            }
        }

        if (DataUtils.hasData(details.getPatientVisit())) {
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);

            document.add(subHeaderParagraph("Patient visit"));
            addIndentedTable(document, createPatientVisitTable(details.getPatientVisit()), CONTENT_INDENTATION, contentTableIndentationAfter);
        }

        if (DataUtils.hasData(details.getProcedures())) {
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);

            document.add(subHeaderParagraph("Diagnosis"));
            var count = 1;
            for (var diagnosis : details.getDiagnoses()) {
                document.add(subSubHeaderParagraph("DIAGNOSIS #" + count++));
                addIndentedTable(document, createDiagnosisTable(diagnosis), CONTENT_INDENTATION, contentTableIndentationAfter);
            }
        }

        if (DataUtils.hasData(details.getGuarantors())) {
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);

            document.add(subHeaderParagraph("Guarantor"));
            var count = 1;
            for (var guarantor : details.getGuarantors()) {
                document.add(subSubHeaderParagraph("GUARANTOR #" + count++));
                addIndentedTable(document, createGuarantorTable(guarantor), CONTENT_INDENTATION, contentTableIndentationAfter);
            }
        }

        if (DataUtils.hasData(details.getInsurances())) {
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);

            document.add(subHeaderParagraph("Insurance"));
            var count = 1;
            for (var insurance : details.getInsurances()) {
                document.add(subSubHeaderParagraph("INSURANCE #" + count++));
                addIndentedTable(document, createInsuranceTable(insurance), CONTENT_INDENTATION, contentTableIndentationAfter);
            }
        }

        if (DataUtils.hasData(details.getAllergies())) {
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);

            document.add(subHeaderParagraph("Allergies"));
            var count = 1;
            for (var allergy : details.getAllergies()) {
                document.add(subSubHeaderParagraph("ALLERGY #" + count++));
                addIndentedTable(document, createAllergyTable(allergy), CONTENT_INDENTATION, contentTableIndentationAfter);
            }
        }
    }

    private PdfPTable createEventEssentialsTable(EventEssentialsNotificationDto essentials) throws DocumentException {
        final PdfPTable table = createContentTable();

        addTableRow(table, "Person Submitting Event:", essentials.getAuthor());
        addTableRow(table, "Care Team Role:", essentials.getAuthorRole());
        addDateTimeTableRow(table, "Event Date and Time:", essentials.getDate());
        addTableRow(table, "Event Type:", essentials.getTypeTitle());
        addBooleanTableRow(table, "Emergency Department Visit:", essentials.getIsEmergencyDepartmentVisit());
        addBooleanTableRow(table, "Overnight In-patient:", essentials.getIsOvernightInpatient());
        addTableRow(table, "Client device ID:", essentials.getDeviceId());
        addTableRow(table, "Event type code:", essentials.getTypeCode());
        addDateTimeTableRow(table, "Recorded date/time:", essentials.getRecordedDate());

        return table;
    }

    private PdfPTable createEventDescriptionTable(EventDescriptionNotificationDto eventDetailsDto) throws DocumentException {
        final PdfPTable table = createContentTable();
        addTableRow(table, "Location:", eventDetailsDto.getLocation());
        addBooleanTableRow(table, "Injury:", eventDetailsDto.getHasInjury());
        addTableRow(table, "Situation:", eventDetailsDto.getSituation());
        addTableRow(table, "Background:", eventDetailsDto.getBackground());
        addTableRow(table, "Assesment:", eventDetailsDto.getAssessment());
        addBooleanTableRow(table, "Follow Up Expected:", eventDetailsDto.getIsFollowUpExpected());
        addTableRow(table, "Follow Up Details:", eventDetailsDto.getFollowUpDetails());
        return table;
    }

    private PdfPTable createTreatingPhysicianDetailsTable(TreatingPhysicianMailDto treatingPhysician) throws DocumentException {
        final PdfPTable table = createContentTable();

        addTableRow(table, "Physitian name:", treatingPhysician.getFullName());
        addTableRow(table, "Address:", treatingPhysician.getAddress());
        addTableRow(table, "Phone:", treatingPhysician.getPhone());

        return table;
    }

    private PdfPTable createTreatingHospitalDetailsTable(TreatingHospitalMailDto treatingHospital) throws DocumentException {
        final PdfPTable table = createContentTable();

        addTableRow(table, "Hospital/Clinic:", treatingHospital.getName());
        addTableRow(table, "Address:", treatingHospital.getAddress());
        addTableRow(table, "Phone:", treatingHospital.getPhone());

        return table;
    }

    private PdfPTable createResponsibleManagerDetailsTable(PersonNotificationDto manager) throws DocumentException {
        final PdfPTable table = createContentTable();

        addTableRow(table, "Name:", manager.getName());
        addTableRow(table, "Phone:", manager.getPhone());
        addTableRow(table, "Email:", manager.getEmail());

        return table;
    }

    private PdfPTable createRegisteredNurseDetailsTable(PersonNotificationDto nurse) throws DocumentException {
        final PdfPTable table = createContentTable();

        addTableRow(table, "Name:", nurse.getName());
        addTableRow(table, "Address:", nurse.getAddress());

        return table;
    }

    private PdfPTable createProcedureTable(AdtProcedureDto procedure) throws DocumentException {
        final PdfPTable table = createContentTable();

        addTableRow(table, "Set ID:", procedure.getSetId());
        addTableRow(table, "Procedure coding method:", procedure.getProcedureCodingMethod());
        addCE(table, "Procedure code:", procedure.getProcedureCode());
        addTableRow(table, "Procedure description:", procedure.getProcedureDescription());
        addDateTimeTableRow(table, "Procedure date/time:", procedure.getProcedureDatetime());
        addTableRow(table, "Procedure functional type:", procedure.getProcedureFunctionalType());
        addCE(table, "Associated Diagnosis Code:", procedure.getAssociatedDiagnosisCode());

        return table;
    }

    private PdfPTable createPatientVisitTable(PatientVisitNotificationDto patientVisit) throws DocumentException {
        final PdfPTable table = createContentTable();

        addTableRow(table, "Patient Class:", patientVisit.getPatientClass());
        addPL(table, "Assigned patient location:", patientVisit.getAssignedPatientLocation());
        addTableRow(table, "Admission type:", patientVisit.getAdmissionType());
        addPL(table, "Prior Patient Location:", patientVisit.getPriorPatientLocation());
        addXCNList(table, "Attending Doctors", patientVisit.getAttendingDoctors());
        addXCNList(table, "Referring Doctors", patientVisit.getReferringDoctors());
        addXCNList(table, "Consulting Doctors", patientVisit.getConsultingDoctors());
        addTableRow(table, "Preadmit Test Indicator:", patientVisit.getPreadmitTestIndicator());
        addTableRow(table, "Readmission Indicator:", patientVisit.getReadmissionIndicator());
        addTableRow(table, "Admit Source:", patientVisit.getAdmitSource());
        addList(table, "Ambulatory Status", patientVisit.getAmbulatoryStatuses());
        addTableRow(table, "Discharge Disposition:", patientVisit.getDischargeDisposition());
        addDL(table, "Discharged to Location:", patientVisit.getDischargedToLocation());
        addTableRow(table, "Servicing facility:", patientVisit.getServicingFacility());
        addDateTimeTableRow(table, "Admit Date/Time:", patientVisit.getAdmitDate());
        addDateTimeTableRow(table, "Discharge Date/Time:", patientVisit.getDischargeDate());

        return table;
    }

    private PdfPTable createDiagnosisTable(AdtDiagnosisDto diagnosis) throws DocumentException {
        final PdfPTable table = createContentTable();

        addTableRow(table, "Set ID:", diagnosis.getSetId());
        addTableRow(table, "Diagnosis coding method:", diagnosis.getDiagnosisCodingMethod());
        addCE(table, "Diagnosis code:", diagnosis.getDiagnosisCode());
        addTableRow(table, "Diagnosis Description:", diagnosis.getDiagnosisDescription());
        addDateTimeTableRow(table, "Diagnosis Date/Time:", diagnosis.getDiagnosisDateTime());
        addTableRow(table, "Diagnosis type:", diagnosis.getDiagnosisType());
        addXCNList(table, "Diagnosing clinician", diagnosis.getDiagnosingClinicians());

        return table;
    }

    private PdfPTable createGuarantorTable(AdtGuarantorDto guarantor) throws DocumentException {
        final PdfPTable table = createContentTable();

        addTableRow(table, "Set ID:", guarantor.getSetId());
        addXPNList(table, "Guarantor name:", guarantor.getGuarantorNames());
        addAddressList(table, "Guarantor address:", guarantor.getGuarantorAddresses());
        addXTNList(table, "Guarantor Ph Num-Home:", guarantor.getGuarantorHomePhones());
        addCE(table, "Guarantor primary language:", guarantor.getPrimaryLanguage());

        return table;
    }

    private PdfPTable createInsuranceTable(AdtInsuranceDto insurance) throws DocumentException {
        final PdfPTable table = createContentTable();

        addTableRow(table, "Set ID:", insurance.getSetId());
        addCE(table, "Insurance Plan ID:", insurance.getInsurancePlanId());
        addCXList(table, "Insurance Company ID:", insurance.getInsuranceCompanyIds());
        addXONList(table, "Insurance Company Name:", insurance.getInsuranceCompanyNames());
        addXTNList(table, "'Insurance Co Phone Number", insurance.getInsuranceCoPhoneNumbers());
        addTableRow(table, "Group Number:", insurance.getGroupNumber());
        addDateTimeTableRow(table, "Plan Effective Date:", insurance.getPlanEffectiveDate());
        addDateTimeTableRow(table, "Plan Expiration Date:", insurance.getPlanExpirationDate());
        addTableRow(table, "Plan Type:", insurance.getPlanType());
        addXPNList(table, "Name of Insured", insurance.getNamesOfInsured());
        addCE(table, "Insured's Relationship to Patient:", insurance.getInsuredsRelationshipToPatient());

        return table;
    }

    private PdfPTable createAllergyTable(AdtAllergyDto allergy) throws DocumentException {
        final PdfPTable table = createContentTable();

        addTableRow(table, "Set ID:", allergy.getSetId());
        addTableRow(table, "Type:", allergy.getAllergyType());
        addCE(table, "Code/Mnemonic/Description:", allergy.getAllergyCode());

        addTableRow(table, "Severity:", allergy.getAllergySeverity());
        addList(table, "Reaction", allergy.getAllergyReactions());
        addDateTimeTableRow(table, "Identification Date:", allergy.getIdentificationDate());

        return table;
    }

    // ============= Utils ===========
    private void addCE(PdfPTable table, String label, CECodedElementDto ce) {
        addComplexType(table, label, pdfPCells -> {
            addComplexTypeField(pdfPCells, "IDENTIFIER", ce.getIdentifier(), getBottomPadding());
            addComplexTypeField(pdfPCells, "TEXT", ce.getText(), getBottomPadding());
            addComplexTypeField(pdfPCells, "NAME OF CODING SYSTEM", ce.getNameOfCodingSystem(), getBottomPadding());
        });
    }

    private void addPL(PdfPTable table, String label, ClientLocationDto pl) {
        addComplexType(table, label, pdfPCells -> {
            addComplexTypeField(pdfPCells, "Point Of Care", pl.getPointOfCare(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Room", pl.getRoom(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Bed", pl.getBed(), getBottomPadding());
            addHDFields(pdfPCells, "Facility ", pl.getFacility(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Location status", pl.getLocationStatus(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Person location status", pl.getPersonLocationStatus(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Building", pl.getBuilding(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Floor", pl.getFloor(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Location description", pl.getLocationDescription(), getBottomPadding());
        });
    }

    private void addDL(PdfPTable table, String label, DischargeLocationDto dl) {
        addComplexType(table, label, pdfPCells -> {
            addComplexTypeField(pdfPCells, "Discharge Location", dl.getDischargeLocation(), getBottomPadding());
            addComplexTypeDateTimeField(pdfPCells, "Effective Date", dl.getEffectiveDate(), getBottomPadding());
        });
    }

    private void addCX(PdfPTable table, String label, CXExtendedCompositeIdDto cx) {
        addComplexType(table, label, pdfPCells -> {
            addComplexTypeField(pdfPCells, "ID", cx.getpId(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Identifier Type Code", cx.getIdentifierTypeCode(), getBottomPadding());
        });
    }

    private void addCXList(PdfPTable table, String label, List<CXExtendedCompositeIdDto> cxList) {
        if (!DataUtils.hasData(cxList)) {
            return;
        }
        var count = 1;
        for (var cx : cxList) {
            addCX(table, label + " #" + count++ + ":", cx);
        }
    }

    private void addXON(PdfPTable table, String label, XONExtendedCompositeNameAndIdForOrganizationsDto xon) {
        addComplexType(table, label, pdfPCells -> {
            addComplexTypeField(pdfPCells, "Organization Name", xon.getOrganizationName(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Organization Name Type Code", xon.getOrganizationNameTypeCode(), getBottomPadding());
            addComplexTypeField(pdfPCells, "ID Number", xon.getIdNumber(), getBottomPadding());
            addHDFields(pdfPCells, "Assigning Authority ", xon.getAssigningAuthority(), getBottomPadding());
            addHDFields(pdfPCells, "Assigning Facility ", xon.getAssigningFacility(), getBottomPadding());
        });
    }

    private void addXONList(PdfPTable table, String label, List<XONExtendedCompositeNameAndIdForOrganizationsDto> xonList) {
        if (!DataUtils.hasData(xonList)) {
            return;
        }
        var count = 1;
        for (var xon : xonList) {
            addXON(table, label + " #" + count++ + ":", xon);
        }
    }

    private void addXCN(PdfPTable table, String label, XCNDto xcn) {
        addComplexType(table, label, pdfPCells -> {
            addComplexTypeField(pdfPCells, "Family Name & Last Name Prefix", xcn.getLastName(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Given name", xcn.getFirstName(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Middle Initial Or Name", xcn.getMiddleName(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Degree", xcn.getDegree(), getBottomPadding());
            addHDFields(pdfPCells, "Assigning Authority ", xcn.getAssigningAuthority(), getBottomPadding());
            addHDFields(pdfPCells, "Assigning Facility ", xcn.getAssigningFacility(), getBottomPadding());
        });
    }

    private void addXCNList(PdfPTable table, String label, List<XCNDto> xcnList) {
        if (!DataUtils.hasData(xcnList)) {
            return;
        }
        var count = 1;
        for (var xcn : xcnList) {
            addXCN(table, label + " #" + count++ + ":", xcn);
        }
    }

    private void addXTNList(PdfPTable table, String label, List<XTNPhoneNumberDto> xtnList) {
        if (!DataUtils.hasData(xtnList)) {
            return;
        }
        var count = 1;
        for (var xtn : xtnList) {
            addXTN(table, label + " #" + count++ + ":", xtn);
        }
    }

    private void addXTN(PdfPTable table, String label, XTNPhoneNumberDto xtn) {
        addComplexType(table, label, pdfPCells -> {
            addComplexTypeField(pdfPCells, "Telephone Number", xtn.getTelephoneNumber(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Country Code", xtn.getCountryCode(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Area/city Code", xtn.getAreaCode(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Phone Number", xtn.getPhoneNumber(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Extension", xtn.getExtension(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Email Address", xtn.getEmail(), getBottomPadding());
        });
    }

    private void addXPN(PdfPTable table, String label, XPNDto xpn) {
        addComplexType(table, label, pdfPCells -> {
            addComplexTypeField(pdfPCells, "First name", xpn.getFirstName(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Last name", xpn.getLastName(), getBottomPadding());
            addComplexTypeField(pdfPCells, "Middle Initial Or Name", xpn.getMiddleName(), getBottomPadding());
        });
    }


    private void addXPNList(PdfPTable table, String label, List<XPNDto> xpnList) {
        if (!DataUtils.hasData(xpnList)) {
            return;
        }
        var count = 1;
        for (var xpn : xpnList) {
            addXPN(table, label + " #" + count++ + ":", xpn);
        }
    }


    private void addComplexType(PdfPTable table, String label, Consumer<List<PdfPCell>> valueCellsFiller) {
        var valueColumns = new ArrayList<PdfPCell>();
        valueCellsFiller.accept(valueColumns);

        if (!DataUtils.hasData(valueColumns)) {
            return;
        }

        final PdfPCell labelCell = new PdfPCell(new Phrase(label, HELVETICA_12_BOLD));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setRowspan(valueColumns.size());
        labelCell.setPaddingBottom(getBottomPadding());

        table.addCell(labelCell);
        valueColumns.forEach(table::addCell);
    }

    protected void addHDFields(List<PdfPCell> cells, String labelPrefix, HDHierarchicDesignatorDto hd, float bottomPadding) {
        addComplexTypeField(cells, labelPrefix + "Namespace Id", hd.getNamespaceID(), bottomPadding);
        addComplexTypeField(cells, labelPrefix + "Universal Id", hd.getNamespaceID(), bottomPadding);
        addComplexTypeField(cells, labelPrefix + "Universal Id Type", hd.getUniversalIDType(), bottomPadding);
    }

    protected void addComplexTypeField(List<PdfPCell> cells, String label, String text, float bottomPadding) { //PdfPTable table,
        if (StringUtils.isEmpty(text)) {
            return;
        }
        final PdfPCell labelCell = new PdfPCell(new Phrase(label.toUpperCase(), HELVETICA_8_COMPLEX));
        final PdfPCell textCell = new PdfPCell(new Phrase(text, HELVETICA_12));
        labelCell.setPaddingBottom(0);
        textCell.setPaddingBottom(bottomPadding);
        textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        labelCell.setBorder(Rectangle.NO_BORDER);
        textCell.setBorder(Rectangle.NO_BORDER);

        cells.add(labelCell);
        cells.add(textCell);
    }

    protected void addComplexTypeDateTimeField(List<PdfPCell> cells, String label, Long date, float bottomPadding) { //PdfPTable table,
        if (date == null) {
            return;
        }

        addComplexTypeField(cells, label, formatDateTime(date), bottomPadding);
    }

    protected void addTableRow(PdfPTable table, String label, AddressDto addressDto) {
        if (DataUtils.hasData(addressDto)) {
            addTableRow(table, label, addressDto.getDisplayAddress());
        }
    }
}
