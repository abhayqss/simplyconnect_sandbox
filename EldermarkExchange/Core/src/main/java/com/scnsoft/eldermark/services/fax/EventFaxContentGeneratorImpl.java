package com.scnsoft.eldermark.services.fax;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.scnsoft.eldermark.shared.carecoordination.*;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import com.scnsoft.eldermark.shared.carecoordination.service.FaxDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class EventFaxContentGeneratorImpl extends AbstractITextPdfFaxContentGenerator<EventDto> implements EventFaxContentGenerator  {

    @Override
    protected void createDocumentBody(Document document, FaxDto faxDto, EventDto eventDto) throws DocumentException, IOException {

        final float contentTableIndentationAfter = 12f;
        document.add(createHeaderTable());

        addIndentedTable(document, createFaxDetailsTable(faxDto), CONTENT_INDENTATION, 15f);

        document.add(new LineSeparator());
        document.add(Chunk.NEWLINE);

        document.add(headerParagraph("Details"));
        document.add(subHeaderParagraph("Patient Info"));
        addIndentedTable(document, createPatientInfoTable(eventDto.getPatient()), CONTENT_INDENTATION, contentTableIndentationAfter);

        document.add(subHeaderParagraph("Event Essentials"));
        addIndentedTable(document, createEventEssentialsTable(eventDto), CONTENT_INDENTATION, contentTableIndentationAfter);

        document.add(subHeaderParagraph("Event Description"));
        addIndentedTable(document, createEventDescriptionTable(eventDto.getEventDetails()), CONTENT_INDENTATION, contentTableIndentationAfter);

        if (checkTreatingPhysitian(eventDto.getTreatingPhysician())) {
            document.add(subHeaderParagraph("Treating Physitian Details"));
            addIndentedTable(document, createTreatingPhysitianDetailsTable(eventDto.getTreatingPhysician()), CONTENT_INDENTATION, contentTableIndentationAfter);
        }


        if (checkTreatingHospitalDetails(eventDto.getTreatingHospital())) {
            document.add(subHeaderParagraph("Treating Hospital Details"));
            addIndentedTable(document, createTreatingHospitalDetailsTable(eventDto.getTreatingHospital()), CONTENT_INDENTATION, contentTableIndentationAfter);

        }

        if (checkResponsibleManagerDetails(eventDto.getManager())) {
            document.add(subHeaderParagraph("Responsible Manager Details"));
            addIndentedTable(document, createResponsibleManagerDetailsTable(eventDto.getManager()), CONTENT_INDENTATION, contentTableIndentationAfter);

        }

        if (checkRegisteredNurseDetails(eventDto.getResponsible())) {
            document.add(subHeaderParagraph("Registered Nurse (RN) Details"));
            addIndentedTable(document, createRegisteredNurseDetailsTable(eventDto.getResponsible()), CONTENT_INDENTATION, contentTableIndentationAfter);

        }


        //        table.addCell(createCellWithTextField("", String.format("A new note has been %s the Simply Connect system.",
//                status.equals(NoteStatus.CREATED.getDisplayName()) ? "added to" : "updated in"), 2));
    }

    private boolean checkRegisteredNurseDetails(NameWithAddressDto responsible) {
        if (responsible == null) {
            return false;
        }

        if (StringUtils.isNotEmpty(responsible.getDisplayName())) {
            return true;
        }

        return responsible.isIncludeAddress() && responsible.getAddress() != null
                && StringUtils.isNotEmpty(responsible.getAddress().getDisplayAddress());

    }

    private boolean checkResponsibleManagerDetails(ManagerDto manager) {
        if (manager == null) {
            return false;
        }

        if (StringUtils.isNotEmpty(manager.getDisplayName())) {
            return true;
        }

        if (StringUtils.isNotEmpty(manager.getPhone())) {
            return true;
        }

        return StringUtils.isNotEmpty(manager.getEmail());

    }

    private boolean checkTreatingHospitalDetails(HospitalDto treatingHospital) {
        if (treatingHospital == null) {
            return false;
        }

        if (StringUtils.isNotEmpty(treatingHospital.getName())) {
            return true;
        }

        if(treatingHospital.isIncludeAddress() && treatingHospital.getAddress() != null
                && StringUtils.isNotEmpty(treatingHospital.getAddress().getDisplayAddress())) {
            return true;
        }

        return StringUtils.isNotEmpty(treatingHospital.getPhone());

    }


    private PdfPTable createResponsibleManagerDetailsTable(ManagerDto manager) throws DocumentException {
        final PdfPTable table = createContentTable();

        addTableRow(table, "Manager name:", manager.getDisplayName());
        addTableRow(table, "Phone:", manager.getPhone());
        addTableRow(table, "Email:", manager.getEmail());

        return table;
    }

    private PdfPTable createTreatingHospitalDetailsTable(HospitalDto treatingHospital) throws DocumentException {
        final PdfPTable table = createContentTable();

        addTableRow(table, "Hospital/Clinic:", treatingHospital.getName());

        if (treatingHospital.isIncludeAddress() && treatingHospital.getAddress() != null) {
            addTableRow(table, "Address:", treatingHospital.getAddress().getDisplayAddress());
        }

        addTableRow(table, "Phone:", treatingHospital.getPhone());

        return table;
    }

    private PdfPTable createRegisteredNurseDetailsTable(NameWithAddressDto responsible) throws DocumentException {
        final PdfPTable table = createContentTable();

        addTableRow(table, "RN name:", responsible.getDisplayName());

        if (responsible.isIncludeAddress() && responsible.getAddress() != null) {
            addTableRow(table, "Address:", responsible.getAddress().getDisplayAddress());
        }

        return table;
    }

    private boolean checkTreatingPhysitian(NameWithAddressDto treatingPhysician) {
        if (treatingPhysician == null) {
            return false;
        }

        if (StringUtils.isNotEmpty(treatingPhysician.getDisplayName())) {
            return true;
        }

        return treatingPhysician.isIncludeAddress() && treatingPhysician.getAddress() != null && StringUtils.isNotEmpty(treatingPhysician.getAddress().getDisplayAddress()) || StringUtils.isNotEmpty(treatingPhysician.getPhone());

    }

    private PdfPTable createTreatingPhysitianDetailsTable(NameWithAddressDto treatingPhysician) throws DocumentException {
        final PdfPTable table = createContentTable();

        addTableRow(table, "Physitian name:", treatingPhysician.getDisplayName());

        if (treatingPhysician.isIncludeAddress() && treatingPhysician.getAddress() != null) {
            addTableRow(table, "Address:", treatingPhysician.getAddress().getDisplayAddress());
        }

        addTableRow(table, "Phone:", treatingPhysician.getPhone());

        return table;
    }

    protected PdfPTable createFaxDetailsTable(FaxDto faxDto) {
        final PdfPTable table = super.createFaxDetailsTable(faxDto);
        table.addCell(createCellWithTextField("", String.format("A new event has been logged to the Simply Connect system and you are %s for this type of event.",
                faxDto.getResponsibility()), 2));
        return table;
    }

    private PdfPTable createEventEssentialsTable(EventDto eventDto) throws DocumentException {
        final PdfPTable table = createContentTable();
        final EmployeeDto employeeDto = eventDto.getEmployee();
        final EventDetailsDto eventDetailsDto = eventDto.getEventDetails();

        addTableRow(table, "Person Submitting Event:", employeeDto.getDisplayName());
        addTableRow(table, "Care Team Role:", employeeDto.getRole());
        addDateTimeTableRow(table, "Event Date and Time:", eventDetailsDto.getEventDatetime());
        addTableRow(table, "Event Type:", eventDetailsDto.getEventType());
        addBooleanTableRow(table, "Emergency Department Visit:", eventDetailsDto.isEmergencyVisit());
        addBooleanTableRow(table, "Overnight In-patient:", eventDetailsDto.isOvernightPatient());

        return table;
    }

    private PdfPTable createEventDescriptionTable(EventDetailsDto eventDetailsDto) throws DocumentException {
        final PdfPTable table = createContentTable();
        addTableRow(table, "Location:", eventDetailsDto.getLocation());
        addBooleanTableRow(table, "Injury:", eventDetailsDto.isInjury());
        addTableRow(table, "Situation:", eventDetailsDto.getSituation());
        addTableRow(table, "Background:", eventDetailsDto.getBackground());
        addTableRow(table, "Assesment:", eventDetailsDto.getAssessment());
        addBooleanTableRow(table, "Follow Up Expected:", eventDetailsDto.isFollowUpExpected());
        addTableRow(table, "Follow Up Details:", eventDetailsDto.getFollowUpDetails());
        return table;
    }

    protected PdfPTable createContentTable() throws DocumentException {
        final PdfPTable table = createFullWidthTable(2);
        table.setWidths(new int[] {2, 5});
        return table;
    }
}
