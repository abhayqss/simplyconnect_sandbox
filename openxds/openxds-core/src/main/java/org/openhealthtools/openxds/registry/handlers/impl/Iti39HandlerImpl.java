package org.openhealthtools.openxds.registry.handlers.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.message.ACK;
import ca.uhn.hl7v2.model.v231.segment.PID;
import org.openhealthexchange.openpixpdq.data.MessageHeader;
import org.openhealthexchange.openpixpdq.data.PatientIdentifier;
import org.openhealthexchange.openpixpdq.ihe.impl_v2.hl7.HL7Header;
import org.openhealthexchange.openpixpdq.ihe.impl_v2.hl7.HL7v231;
import org.openhealthtools.openxds.XdsFactory;
import org.openhealthtools.openxds.registry.BaseHandler;
import org.openhealthtools.openxds.registry.api.PatientExtended;
import org.openhealthtools.openxds.registry.api.RegistryPatientContext;
import org.openhealthtools.openxds.registry.api.RegistryPatientException;
import org.openhealthtools.openxds.registry.api.XdsRegistryPatientService;
import org.openhealthtools.openxds.registry.handlers.Iti39Handler;
import org.openhealthtools.openxds.registry.patient.LssiPv1PatientIdService;
import org.openhealthtools.openxds.registry.service.OpenXdsRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Iti39HandlerImpl extends BaseHandler implements Iti39Handler {

    @Autowired
    private OpenXdsRegistryService openXdsRegistryService;

    private XdsRegistryPatientService patientManager;

    private LssiPv1PatientIdService lssiPv1PatientIdService = XdsFactory.getInstance().getBean(LssiPv1PatientIdService.class);

    public void init() {
        openXdsRegistryService.setConnection(getConnection());
        openXdsRegistryService.setPatientManager(patientManager);
    }

    public Message processMessage(final Message msgIn) throws ApplicationException, HL7Exception {
        lssiPv1PatientIdService.updateAssigningFacilityAccordingToPv1(msgIn);

        final HL7Header hl7Header = new HL7Header(msgIn);
        ACK reply = getOpenXdsRegistryService().initAcknowledgment(hl7Header);

        //Validate incoming message first
        PID pid = (PID) msgIn.get("PID");
        PatientIdentifier patientId = getOpenXdsRegistryService().getPatientIdentifiers(pid);

        boolean isValidMessage = getOpenXdsRegistryService().validateMessage(reply, hl7Header, patientId, null, true);
        if (!isValidMessage) return reply;

        //Invoke eMPI function
        MessageHeader header = hl7Header.toMessageHeader();
        RegistryPatientContext context = new RegistryPatientContext(header);
        Long msgDbId = patientManager.saveXdsMessage(header, msgIn);
        try {
            logAccess(hl7Header, patientId);
            // Create patient
            final PatientExtended patient = openXdsRegistryService.getPatient(msgIn);
            patientManager.createPatient(patient, context, msgDbId);
        } catch (RegistryPatientException e) {
            throw new ApplicationException(e);
        }
        HL7v231.populateMSA(reply.getMSA(), "AA", hl7Header.getMessageControlId());

        //TODO: revisit Audit
        //Finally, Audit Log PIX Feed Success
        //auditLog(hl7Header, patient, AuditCodeMappings.EventActionCode.Create);

        return reply;
    }

    public OpenXdsRegistryService getOpenXdsRegistryService() {
        return openXdsRegistryService;
    }

    public void setOpenXdsRegistryService(final OpenXdsRegistryService openXdsRegistryService) {
        this.openXdsRegistryService = openXdsRegistryService;
    }

    public XdsRegistryPatientService getPatientManager() {
        return patientManager;
    }

    public void setPatientManager(final XdsRegistryPatientService patientManager) {
        this.patientManager = patientManager;
    }
}
