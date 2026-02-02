package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.pointclickcare.PointClickCareWebhookDto;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.EventService;
import com.scnsoft.eldermark.service.pointclickcare.PointClickCareEventFactory;
import com.scnsoft.eldermark.service.pointclickcare.PointClickCarePatientService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Service
@ConditionalOnProperty(value = "pcc.integration.enabled", havingValue = "true")
@Transactional
public class PointClickCareWebhookFacadeImpl implements PointClickCareWebhookFacade {
    private static final Logger logger = LoggerFactory.getLogger(PointClickCareWebhookFacadeImpl.class);

    @Autowired
    private PointClickCarePatientService pointClickCarePatientService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private PointClickCareEventFactory pointClickCareEventFactory;

    @Autowired
    private EventService eventService;

    @Override
    public void acceptWebhook(PointClickCareWebhookDto dto) {
        logger.info("Accepted PCC webhook {}, type {}", dto.getMessageId(), dto.getEventType());

        try {
            switch (dto.getEventType()) {
                //ADT01
                case "patient.admit": {
                    adt01PatientAdmit(dto);
                    return;
                }
                case "patient.readmit": {
                    adt01PatientReadmit(dto);
                    return;
                }
                case "patient.cancelAdmit": {
                    adt01PatientCancelAdmit(dto);
                    return;
                }
                case "patient.discharge": {
                    adt01PatientDischarge(dto);
                    return;
                }
                case "patient.cancelDischarge": {
                    adt01PatientCancelDischarge(dto);
                    return;
                }
                case "patient.updateResidentInfo": {
                    adt01PatientUpdateResidentInfo(dto);
                    return;
                }
            }
            logger.info("Processed PCC webhook {}, type {}", dto.getMessageId(), dto.getEventType());
        } catch (Exception ex) {
            logger.warn("Error during processing PCC webhook {}", dto.getMessageId(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    private void adt01PatientAdmit(PointClickCareWebhookDto dto) {
        var client = createOrUpdateClient(dto.getOrgUuid(), dto.getPatientId(), true);
        createEvent(client, "EADT", "Patient Admit", dto.getResourceId());
    }

    private void adt01PatientReadmit(PointClickCareWebhookDto dto) {
        var client = createOrUpdateClient(dto.getOrgUuid(), dto.getPatientId(), true);
        createEvent(client, "EADT", "Patient Readmit", dto.getResourceId());
    }

    private void adt01PatientCancelAdmit(PointClickCareWebhookDto dto) {
        createOrUpdateClient(dto.getOrgUuid(), dto.getPatientId(), false);
    }

    private void adt01PatientDischarge(PointClickCareWebhookDto dto) {
        var client = createOrUpdateClient(dto.getOrgUuid(), dto.getPatientId(), false);
        createEvent(client, "EADT", "Patient Discharge", dto.getResourceId());
    }

    private void adt01PatientCancelDischarge(PointClickCareWebhookDto dto) {
      createOrUpdateClient(dto.getOrgUuid(), dto.getPatientId(), true);
    }

    private void adt01PatientUpdateResidentInfo(PointClickCareWebhookDto dto) {
        var client = createOrUpdateClient(dto.getOrgUuid(), dto.getPatientId(), true);
        createEvent(client, "PRU", "Patient Update", dto.getResourceId());
    }

    private Client createOrUpdateClient(String orgUuid, Long patientId, Boolean active) {
        var client = pointClickCarePatientService.createOrUpdateClient(orgUuid, patientId);
        if (active != null) {
            client.setActive(active);
        }
        return clientService.save(client);
    }

    private void createEvent(Client client, String eventTypeCode, String situation, List<Long> resourceIds) {
        if (CollectionUtils.isNotEmpty(resourceIds)) {
            var event = pointClickCareEventFactory.createEvent(client, eventTypeCode, situation, resourceIds.get(0));
            eventService.save(event);
        }
    }
}
//ADL01	activitiesOfDailyLiving.add	A new ADL response was recorded.
//activitiesOfDailyLiving.strikeout	An ADL response for a patient is struck out in the PointClickCare app. Responses are typically struck out when they are incorrectly documented.
//ADT01	patient.admit	A new patient is admitted using Quick ADT or New Census Entry ( patientStatus is Current).
//patient.readmit	A patient who was previously discharged is now being readmitted using Quick ADT or New Census Entry. A new effective Date, Bed Location and other required Census fields are added.
//patient.cancelAdmit	A patient's admission or readmission is deleted in PointClickCare by deleting the Census entry.
//patient.discharge	A patient is discharged using Quick ADT or New Census Entry (patientStatus is Discharged).
//patient.cancelDischarge	A patient's discharge event is deleted in PointClickCare by deleting the Census entry.
//patient.transfer	A patient moves from one bed to another within the same facility. Usually done when a patient requires an internal transfer/room change or a different level of care.
//patient.cancelTransfer	A patient's transfer is deleted in PointClickCare by deleting the Census entry.
//patient.updateResidentInfo	A patient's demographic info, prior address info, allergies or diagnoses are updated.
//ADT02	patient.leave	A leave of absence is added for a patient. This is usually added when a patient requires hospital care or leaves the facility temporarily.
//patient.returnFromLeave	A patient returns from leave of absence.
//patient.cancelLeave	A patient's leave of absence event is cancelled in PointClickCare by deleting the Census entry.
//patient.cancelReturnFromLeave	A patient's return from leave of absence event is cancelled in PointClickCare by deleting the Census entry.
//ADT03	patient.preAdmit	A patient is pre-admitted (patientStatus is New).
//ADT04	patient.outpatientToInpatient	A patient who was previously an Outpatient is now an Inpatient by updating the patient's payer type.
//patient.inpatientToOutpatient	A patient who was previously an Inpatient is now an Outpatient by updating the patient's payer type.
//ADT05
//Note: To use this event you also need to subscribe to (and be validated for) the ADT01.
//patient.updateContactInfo	A patient's contact information is updated by adding new contacts or updating existing contacts.
//ADT06	patient.updateAccount	A patient's payer is updated or there is a change in the daily rate for the active payer.
//patient.updateAccountOnReturnFromLeave	A patient's payer is updated when they return from a Leave of Absence.
//ADT07	patient.updateHIEPrivacyConsent	A patient's consent to share information with Health Information Exchanges (HIE) is updated.
//ADT08	patient.updateEpisodeofCare	A new episode of care is recorded for a patient or an episode of care is updated.
//ALL01	allergy.add	A new allergy is added for a patient.
//allergy.strikeout	An allergy for a patient is struck out in the PointClickCare app.
//ALL02	allergy.update	An allergy for a patient is updated in the PointClickCare app.
//CLM01	claim.export	An exported batch of claims.
//CON01	condition.add	A new condition is added for a patient.
//condition.strikeout	A condition for a patient is struck out in the PointClickCare app.
//CON02	condition.update	A condition for a patient is updated in the PointClickCare app.
//CPT01	patient.pendingPatientAccepted	When a pending patient record is created using the POST PendingPatients API, this event tells them if the record is resolved and a patient ID is now available.
//patient.pendingPatientDeleted	When a pending patient record is created using the POST PendingPatients API, this event tells them if the record is deleted by a staff member at the Facility.
//IMM01	immunization.add	A new immunization is added for a patient.
//immunization.strikeout	An immunization for a patient is struck out in the PointClickCare app.
//IMM02	immunization.update	An immunization for a patient is updated in the PointClickCare app.
//INC01	incident.add	A new incident report was added for a patient.
//incident.strikeout	An incident report was struck out in the PointClickCare app. Reports are typically struck out when they are incorrectly documented.
//INC02	incident.update	An incident report was updated.
//MED01	medication.add	A new medication order is activated.
//medication.discontinue	An existing active medication order is discontinued.
//medication.strikeout	Strikeout an active order. Strikeout can be due to an entry error.
//MED02	medication.cancelDiscontinue	Cancel a discontinued order. A discontinued order is updated into an active order.
//medication.update	An active medication order is discontinued and replaced with new active order, thereby making an update to the PointClickCare app for the patient. Both order ID's will be included under the 'resourcedId' field, with discontinued order Id being less than the new order Id.
//
//Note: when simulating this webhook via the POST Simulate endpoint, only one resourceId will be included in the payload. This is a bug and will be addressed in a future release.
//MPR01	medicalProfessional.add	A medical professional is added to the Patient record.
//medicalProfessional.remove	A medical professional is removed from the Patient record.
//NUT01	nutritionOrder.add	A new nutrition order is activated.
//nutritionOrder.discontinue	An existing active nutrition order is discontinued.
//nutritionOrder.hold	A nutrition order is put on hold, or an order that is on hold resumes as active.
//nutritionOrder.strikeout	Strikeout an active nutrition order. Strikeout can be due to an entry error.
//NUT02	nutritionOrder.updateNotes	New direction is added to an active nutrition order.
//nutritionOrder.update	An active nutrition order is discontinued and replaced with new active order, thereby making an update to the PointClickCare app for the patient. Both order ID's will be included under the 'resourcedId' field, with discontinued order Id being less than the new order Id.
//OBS01	observation.add	An observation is added for a patient in the PointClickCare app or using other integrations.
//observation.strikeout	An observation for a patient is struck out in the PointClickCare app. Observations are typically struck out when they are incorrectly documented.
//OBS02	observation.warningGenerated	An observation is added for a patient that results in a warning being generated. Warnings are typically generated when the observation exceeds an enabled system level threshold.
//PAI01
//Note: The corresponding API is sandbox only. 	painManagement.add	A new pain management response is recorded.
//painManagement.strikeout	A pain management response for a patient is struck out in the PointClickCare app. Responses are typically struck out when they are incorrectly documented.
//PHT01	patientPhoto.Upload	A patient's photo is uploaded on the Patient record.
//patientPhoto.Delete	A patient's photo is deleted from the Patient record.
//PRO01	progressNote.add	New progress notes recorded.
//progressNote.strikeout	A recorded progress note for a patient is struck out in the PointClickCare app. Progress notes are typically struck out when they are incorrectly documented.
//RES01	labResults.add	A new lab result is added to the patient chart.
//labResults.upload	A new lab results file uploaded.
//RES02	labResults.warningGenerated	A lab result for a patient that has a warning generated. Warnings are typically generated when the lab result is flagged as critical or abnormal.
//SUP01	supplementOrder.add	A new supplement order is activated.
//supplementOrder.discontinue	An existing active supplement order is discontinued.
//supplementOrder.hold	A supplement order is put on hold, or an order that is on hold resumes as active.
//supplementOrder.strikeout	Strikeout an active supplement order. Strikeout can be due to an entry error.
//SUP02	supplementOrder.reactivateFromHold	A supplement order is re-activated either by cancelling the 'Hold' order or due to the expiration of the 'Hold'.
//supplementOrder.cancelDiscontinue	Cancel a discontinued order. A discontinued order is updated into an active order.
//supplementOrder.updateNotes	New direction is added to an active supplement order.
//supplementOrder.update	An active supplement order is discontinued and replaced with new active order, thereby making an update to the PointClickCare app for the patient. Both order ID's will be included under the 'resourcedId' field, with discontinued order Id being less than the new order Id.
//STM01
//statements.export
//A Statement is available for a patient/set of patients in the Facility. A webhook will be sent for each patient whose invoice is included in a statement.
