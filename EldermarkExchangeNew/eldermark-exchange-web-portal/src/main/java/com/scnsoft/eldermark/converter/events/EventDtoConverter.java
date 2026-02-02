package com.scnsoft.eldermark.converter.events;

import com.scnsoft.eldermark.beans.projection.DocumentSignatureHistoryEventDetailsAware;
import com.scnsoft.eldermark.converter.event.base.*;
import com.scnsoft.eldermark.converter.hl7.entity2dto.segment.PatientVisitViewDataConverter;
import com.scnsoft.eldermark.dto.basic.PersonDto;
import com.scnsoft.eldermark.dto.events.*;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureStatus;
import com.scnsoft.eldermark.service.ClientAppointmentService;
import com.scnsoft.eldermark.service.IncidentReportService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureHistoryService;
import com.scnsoft.eldermark.service.security.ClientAppointmentSecurityService;
import com.scnsoft.eldermark.service.security.ClientAssessmentResultSecurityService;
import com.scnsoft.eldermark.service.security.ClientSecurityService;
import com.scnsoft.eldermark.service.security.IncidentReportSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.EventNotificationUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventDtoConverter extends EventViewDataConverter<EventDto> {

    @Autowired
    private ClientSummaryViewDataConverter<ClientSummaryDto> clientSummaryConverter;

    @Autowired
    private EventEssentialsViewDataConverter<EventEssentialsDto> eventEssentialsDtoConverter;

    @Autowired
    private EventDescriptionViewDataConverter<EventDescriptionDto> eventDescriptionDtoConverter;

    @Autowired
    private TreatmentViewDataConverter<PhysicianDto, HospitalDto, TreatmentDto> treatmentDetailsDtoConverter;

    @Autowired
    private ResponsibleManagerConverter<PersonDto> responsibleManagerConverter;

    @Autowired
    private RegisteredNurseConverter<PersonDto> registeredNurseConverter;

    @Autowired
    private PatientVisitViewDataConverter<PatientVisitDto> patientVisitConverter;

    @Autowired
    private IncidentReportService incidentReportService;

    @Autowired
    private ClientSecurityService clientSecurityService;

    @Autowired
    private IncidentReportSecurityService incidentReportSecurityService;

    @Autowired
    private ClientAssessmentResultSecurityService assessmentResultSecurityService;

    @Autowired
    private DocumentSignatureHistoryService documentSignatureHistoryService;

    @Autowired
    private ClientAppointmentService clientAppointmentService;

    @Autowired
    private ClientAppointmentSecurityService clientAppointmentSecurityService;

    @Override
    protected EventDto create() {
        return new EventDto();
    }

    @Override
    protected void fill(Event event, EventDto dto) {
        super.fill(event, dto);

        dto.setId(event.getId());

        dto.setCanHaveIncidentReport(BooleanUtils.isTrue(event.getClient().getCommunity().getIrEnabled()) && (event.getEventType().isRequireIr() || event.getIsInjury()));
        boolean hasAccessToIr = incidentReportSecurityService.hasAccessByEventId(event.getId());
        dto.setCanViewIncidentReport(hasAccessToIr);
        dto.setCanAddIncidentReport(hasAccessToIr);
        dto.setIncidentReportId(incidentReportService.findIncidentReportId(event.getId()));

        dto.setHasRegisteredNurse(dto.getRegisteredNurse() != null);
        dto.setHasResponsibleManager(dto.getResponsibleManager() != null);

        dto.setCanViewClient(clientSecurityService.canView(event.getClientId()));

        if (event.getAssessmentResult() != null) {
            dto.setCanViewAssessment(assessmentResultSecurityService.canView(event.getAssessmentResult().getId()));
            dto.setAssessmentId(event.getAssessmentResult().getId());
            dto.setAssessmentTypeName(event.getAssessmentResult().getAssessment().getCode());
        }

        if (EventNotificationUtils.DOCUMENT_SIGNED.equals(event.getEventType().getCode())) {
            var documentSignature = documentSignatureHistoryService.findByDocumentSignedEventId(
                            event.getId(),
                            DocumentSignatureHistoryEventDetailsAware.class
                    )
                    .map(info -> {
                        var documentSignatureDto = new EventDocumentSignatureDto();
                        documentSignatureDto.setTemplateName(info.getRequestSignatureTemplateTitle());
                        documentSignatureDto.setSignedDate(DateTimeUtils.toEpochMilli(info.getDate()));
                        documentSignatureDto.setDocumentId(info.getDocumentId());
                        var status = DocumentSignatureStatus.fromRequestStatus(info.getRequestStatus());
                        documentSignatureDto.setStatusName(status.name());
                        documentSignatureDto.setStatusTitle(status.getTitle());
                        documentSignatureDto.setIsDeleted(info.getDocumentDeletionTime() != null);
                        return documentSignatureDto;
                    })
                    .orElse(null);

            dto.setDocumentSignature(documentSignature);
        }

        if (event.getAppointmentChainId() != null) {
            var appointmentId = clientAppointmentService.findUnarchivedIdByChainId(event.getAppointmentChainId());
            dto.setAppointmentId(appointmentId);
            dto.setCanViewAppointment(clientAppointmentSecurityService.canView(appointmentId));
        }
    }

    @Override
    protected ClientSummaryViewDataConverter getClientSummaryViewDataConverter() {
        return clientSummaryConverter;
    }

    @Override
    protected EventEssentialsViewDataConverter getEventEssentialsViewDataConverter() {
        return eventEssentialsDtoConverter;
    }

    @Override
    protected EventDescriptionViewDataConverter getEventDescriptionViewDataConverter() {
        return eventDescriptionDtoConverter;
    }

    @Override
    protected TreatmentViewDataConverter getTreatmentViewDataConverter() {
        return treatmentDetailsDtoConverter;
    }

    @Override
    protected ResponsibleManagerConverter getResponsibleManagerConverter() {
        return responsibleManagerConverter;
    }

    @Override
    protected RegisteredNurseConverter getRegisteredNurseConverter() {
        return registeredNurseConverter;
    }

    @Override
    protected PatientVisitViewDataConverter getPatientVisitViewDataConverter() {
        return patientVisitConverter;
    }
}
