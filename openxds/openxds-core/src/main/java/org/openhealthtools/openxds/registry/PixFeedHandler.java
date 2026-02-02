/**
 * Copyright (c) 2009-2010 Misys Open Source Solutions (MOSS) and others
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * <p>
 * Contributors:
 * Misys Open Source Solutions - initial API and implementation
 * -
 */

package org.openhealthtools.openxds.registry;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Application;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.datatype.CX;
import ca.uhn.hl7v2.model.v231.group.ADT_A39_PIDPD1MRGPV1;
import ca.uhn.hl7v2.model.v231.message.*;
import ca.uhn.hl7v2.model.v231.segment.MRG;
import ca.uhn.hl7v2.model.v231.segment.PID;
import com.misyshealthcare.connect.net.Identifier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openhealthexchange.openpixpdq.data.MessageHeader;
import org.openhealthexchange.openpixpdq.data.Patient;
import org.openhealthexchange.openpixpdq.data.PatientIdentifier;
import org.openhealthexchange.openpixpdq.ihe.impl_v2.hl7.HL7Header;
import org.openhealthexchange.openpixpdq.ihe.impl_v2.hl7.HL7v231;
import org.openhealthexchange.openpixpdq.ihe.impl_v2.hl7.HL7v231ToBaseConvertor;
import org.openhealthexchange.openpixpdq.ihe.log.MessageStore;
import org.openhealthexchange.openpixpdq.util.ExceptionUtil;
import org.openhealthtools.common.utils.CustomAssigningAuthorityUtil;
import org.openhealthtools.openxds.XdsFactory;
import org.openhealthtools.openxds.registry.api.*;
import org.openhealthtools.openxds.registry.handlers.Iti39Handler;
import org.openhealthtools.openxds.registry.patient.LssiPv1PatientIdService;
import org.openhealthtools.openxds.registry.patient.XdsEntityUtil;
import org.openhealthtools.openxds.registry.service.OpenXdsRegistryService;

import java.util.Arrays;
import java.util.List;


/**
 * This class processes PIX Feed message in HL7 v2.3.1 format. It 
 * handles the PIX Feed transaction (including also PIX Update 
 * and PIX Merge transactions) of the PIX profile.  
 * The supported message type includes ADT^A01, ADT^A04, ADT^A05, 
 * ADT^A08 and ADT^A40.
 *
 * @author <a href="mailto:wenzhi.li@misys.com">Wenzhi Li</a>
 *
 */
class PixFeedHandler extends BaseHandler implements Application {

    private List<Class<? extends AbstractMessage>> supportedMessagesTypes = Arrays.asList(ADT_A01.class, ADT_A03.class, ADT_A04.class, ADT_A08.class);

    private static Log log = LogFactory.getLog(PixFeedHandler.class);

    private XdsRegistryImpl actor = null;

    private Iti39Handler iti39Handler = XdsFactory.getInstance().getBean(Iti39Handler.class);

    private OpenXdsRegistryService openXdsRegistryService = XdsFactory.getInstance().getBean(OpenXdsRegistryService.class);

    private LssiPv1PatientIdService lssiPv1PatientIdService = XdsFactory.getInstance().getBean(LssiPv1PatientIdService.class);

    /** The XDS Registry Patient Manager*/

    private XdsRegistryPatientService patientManager = null;



    /**
     * Constructor
     *
     * @param actor the {@link XdsRegistryImpl} actor
     */
    PixFeedHandler(XdsRegistryImpl actor) {
        super(actor.getPixRegistryConnection());
        this.actor = actor;
        this.patientManager = actor.getPatientManager();

        iti39Handler.setConnection(connection);
        iti39Handler.setPatientManager(patientManager);
        iti39Handler.init();
        assert this.connection != null;
        assert this.patientManager != null;
    }

    /**
     * Whether an incoming message can be processed by this handler.
     *
     * @return <code>true</code> if the incoming message can be processed;
     * otherwise <code>false</code>.
     */
    public boolean canProcess(Message theIn) {
        if (theIn instanceof ADT_A01 || theIn instanceof ADT_A04 ||
                theIn instanceof ADT_A05 || theIn instanceof ADT_A08 ||
                theIn instanceof ADT_A39)
            return true;
        else
            return false;
    }

    /**
     * Processes the incoming PIX Feed Message. Valid messages 
     * are ADT^A01, ADT^A04, ADT^A05, ADT^A08 and ADT^A40.
     *
     * @param msgIn the incoming message
     */
    public Message processMessage(Message msgIn) throws ApplicationException,
            HL7Exception {
        Message retMessage = null;

        lssiPv1PatientIdService.updateAssigningFacilityAccordingToPv1(msgIn);

        MessageStore store = actor.initMessageStore(msgIn, true);
        //String encodedMessage = HapiUtil.encodeMessage(msgIn);
        //log.info("Received message:\n" + encodedMessage + "\n\n");
        try {
            HL7Header hl7Header = new HL7Header(msgIn);

            //Populate MessageStore to persist the message
            hl7Header.populateMessageStore(store);

            final String triggerEvent = hl7Header.getTriggerEvent();

            if (msgIn instanceof ADT_A01 || "A01".equals(triggerEvent) || //Admission of in-patient into a facility
                    msgIn instanceof ADT_A04 || "A04".equals(triggerEvent) || //Registration of an outpatient for a visit of the facility
                    msgIn instanceof ADT_A05 || "A05".equals(triggerEvent)) { //Pre-admission of an in-patient
                retMessage = processCreate(msgIn);
            } else if (msgIn instanceof ADT_A08 || "A08".equals(triggerEvent)) { //Update patient information
                retMessage = processUpdate(msgIn);
            } else if (msgIn instanceof ADT_A39 || "A39".equals(triggerEvent)) { //Merge Patients
                retMessage = processMerge(msgIn);
            } else if (msgIn instanceof ADT_A03 || "A03".equals(triggerEvent)) {
                retMessage = iti39Handler.processMessage(msgIn);
            } else {
                String errorMsg = "Unexpected request to PIX Manager server. "
                        + "Valid message types are ADT^A01, ADT^A04, ADT^A05, ADT^A08 and ADT^A40";

                throw new ApplicationException(errorMsg);
            }
        } catch (ApplicationException e) {
            if (store != null) {
                store.setErrorMessage(e.getMessage());
            }
            throw new ApplicationException(ExceptionUtil.strip(e.getMessage()), e);
        } catch (HL7Exception e) {
            if (store != null) {
                store.setErrorMessage(e.getMessage());
            }
            throw new HL7Exception(ExceptionUtil.strip(e.getMessage()), e);
        } finally {
            //Persist the message
            if (store != null) {
                actor.saveMessageStore(retMessage, false, store);
            }
        }

        return retMessage;
    }

    /**
     * Processes PIX Feed Create Patient message in HL72.3.1.
     *
     * @param msgIn the PIX Feed request message
     * @return a response message for PIX Feed
     * @throws ApplicationException If Application has trouble
     * @throws HL7Exception if something is wrong with HL7 message
     */
    private Message processCreate(Message msgIn)
            throws ApplicationException, HL7Exception {

        assert msgIn instanceof ADT_A01 ||
                msgIn instanceof ADT_A04 ||
                msgIn instanceof ADT_A05 ||
                msgIn instanceof ADT_A08;

        HL7Header hl7Header = new HL7Header(msgIn);

        //Create Acknowledgment and its Header
        ACK reply = openXdsRegistryService.initAcknowledgment(hl7Header);

        //Validate incoming message first
        PID pid = (PID) msgIn.get("PID");
        PatientIdentifier patientId = openXdsRegistryService.getPatientIdentifiers(pid);

        boolean isValidMessage = openXdsRegistryService.validateMessage(reply, hl7Header, patientId, null, true);
        if (!isValidMessage) return reply;

        //Invoke eMPI function
        MessageHeader header = hl7Header.toMessageHeader();
        RegistryPatientContext context = new RegistryPatientContext(header);
        Long msgDbId = null;
        if (supportedMessagesTypes.contains(msgIn.getClass())) {
            msgDbId = patientManager.saveXdsMessage(header, msgIn);
        }
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


    /**
     * Processes PIX Feed Update Patient message.
     *
     * @param msgIn the PIX Feed request message
     * @return a response message for PIX Feed
     * @throws ApplicationException If Application has trouble
     * @throws HL7Exception if something is wrong with HL7 message
     */
    private Message processUpdate(Message msgIn) throws ApplicationException,
            HL7Exception {
        assert msgIn instanceof ADT_A01 ||
                msgIn instanceof ADT_A08;

        HL7Header hl7Header = new HL7Header(msgIn);

        //Validate incoming message first
        PID pid = (PID) msgIn.get("PID");
        PatientIdentifier patientId = openXdsRegistryService.getPatientIdentifiers(pid);

        if (!openXdsRegistryService.isValidPatient(patientId, hl7Header.toMessageHeader())) {
            //the patient was not found in the system - proceed with create flow
            return processCreate(msgIn);
        }

        //Create Acknowledgment and its Header
        ACK reply = openXdsRegistryService.initAcknowledgment(hl7Header);

        boolean isValidMessage = openXdsRegistryService.validateMessage(reply, hl7Header, patientId, null, false);
        if (!isValidMessage) return reply;

        //Invoke eMPI function
        MessageHeader header = hl7Header.toMessageHeader();
        RegistryPatientContext context = new RegistryPatientContext(header);

        Long msgDbId = null;
        if (supportedMessagesTypes.contains(msgIn.getClass())) {
            msgDbId = patientManager.saveXdsMessage(header, msgIn);
        }
        try {
            logAccess(hl7Header, patientId);
            //Update Patient
            final PatientExtended patient = openXdsRegistryService.getPatient(msgIn);
            patientManager.updatePatient(patient, context, msgDbId);
        } catch (RegistryPatientException e) {
            throw new ApplicationException(e);
        }
        //

        HL7v231.populateMSA(reply.getMSA(), "AA", hl7Header.getMessageControlId());

        //TODO: revisit Audit
        //Finally, Audit Log PIX Feed Success
        //auditLog(hl7Header, patient, AuditCodeMappings.EventActionCode.Update);

        return reply;
    }

    /**
     * Processes PIX Feed Merge Patient message.
     *
     * @param msgIn the PIX Feed request message
     * @return a response message for PIX Feed
     * @throws ApplicationException If Application has trouble
     * @throws HL7Exception if something is wrong with HL7 message
     */
    private Message processMerge(Message msgIn)
            throws ApplicationException, HL7Exception {

        assert msgIn instanceof ADT_A39;

        HL7Header hl7Header = new HL7Header(msgIn);

        //Create Acknowledgment and its Header
        ACK reply = openXdsRegistryService.initAcknowledgment(hl7Header);

        //Validate incoming message first
        ADT_A39_PIDPD1MRGPV1 requestId = ((ADT_A39) msgIn).getPIDPD1MRGPV1();
        PatientIdentifier patientId = openXdsRegistryService.getPatientIdentifiers(requestId.getPID());
        PatientIdentifier mrgPatientId = getMrgPatientIdentifiers(requestId.getMRG());
        boolean isValidMessage = openXdsRegistryService.validateMessage(reply, hl7Header, patientId, mrgPatientId, false);
        if (!isValidMessage) return reply;

        //Invoke eMPI function
        MessageHeader header = hl7Header.toMessageHeader();
        RegistryPatientContext context = new RegistryPatientContext(header);
        Patient patient = openXdsRegistryService.getPatient(msgIn);
        Patient mrgPatient = getMrgPatient(msgIn);
        try {
            logAccess(hl7Header, patientId);
            //Merge Patients
            patientManager.mergePatients(patient, mrgPatient, context);

        } catch (RegistryPatientException e) {
            throw new ApplicationException(e);
        }
        String survivingPatient = XdsEntityUtil.convertPatientIdentifier(patient.getPatientIds());
        String mergePatient = XdsEntityUtil.convertPatientIdentifier(mrgPatient.getPatientIds());
        XdsRegistryLifeCycleService lifeCycleManager = XdsFactory.getXdsRegistryLifeCycleService();
        try {
            lifeCycleManager.mergePatients(survivingPatient, mergePatient, new RegistryLifeCycleContext());
        } catch (Exception e) {
            try {
                patientManager.unmergePatients(patient, mrgPatient, context);
            } catch (Exception e1) {
                throw new ApplicationException(e1);
            }
            log.error("error while merging patient document in xds regsitry");
            throw new ApplicationException(e);
        }
        HL7v231.populateMSA(reply.getMSA(), "AA", hl7Header.getMessageControlId());

        //TODO: revisit Audit
        //Finally, Audit Log PIX Feed Success
        //auditLog(hl7Header, patient, AuditCodeMappings.EventActionCode.Update);
        //auditLog(hl7Header, mrgPatient, AuditCodeMappings.EventActionCode.Delete);

        return reply;
    }

//TODO: revisit Audit log	
//	/**
//	 * Audit Logging of PIX Feed message.
//	 * 
//	 * @param hl7Header the header message from the source application
//	 * @param patient the patient to create, update or merged
//	 * @param eventActionCode the {@link EventActionCode}
//	 */
//	private void auditLog(HL7Header hl7Header, Patient patient, AuditCodeMappings.EventActionCode eventActionCode) {
//		if (actor.getAuditTrail() == null)
//			return;
//		
//		String userId = hl7Header.getSendingFacility().getNamespaceId() + "|" +
//						hl7Header.getSendingApplication().getNamespaceId();
//		String messageId = hl7Header.getMessageControlId();
//		//TODO: Get the ip address of the source application
//		String sourceIp = "127.0.0.1";
//
//		ActiveParticipant source = new ActiveParticipant(userId, messageId, sourceIp);
//		
//		ParticipantObject patientObj = new ParticipantObject(patient);
//		patientObj.setDetail(hl7Header.getMessageControlId());
//		
//		actor.getAuditTrail().logPixFeed(source, patientObj, eventActionCode);		
//	}


    /**
     * Extracts the merge patient out of a PIX Merge Patient message.
     *
     * @param msgIn the incoming PIX Merge message
     * @return a {@link Patient} object that represents the merge patient
     * @throws ApplicationException if something is wrong with the application
     */
    private Patient getMrgPatient(Message msgIn) throws ApplicationException, HL7Exception {
        HL7v231ToBaseConvertor convertor = null;
        convertor = new HL7v231ToBaseConvertor(msgIn, connection);
        Patient patientDesc = new Patient();
        patientDesc.setPatientIds(convertor.getMrgPatientIds());
        patientDesc.setPatientName(convertor.getMrgPatientName());
        patientDesc.setPatientAccountNumber(convertor
                .getMrgpatientAccountNumber());
        patientDesc.setVisits(convertor.getMrgVisitList());
        return patientDesc;
    }

    /**
     * Gets the merge patient identifier out of a MRG segment.
     *
     * @param mrg the merge segment
     * @return a {@link PatientIdentifier}
     */
    private PatientIdentifier getMrgPatientIdentifiers(MRG mrg) {
        PatientIdentifier identifier = new PatientIdentifier();
        CX[] cxs = mrg.getPriorPatientIdentifierList();
        for (CX cx : cxs) {
            Identifier assignAuth = new Identifier(cx.getAssigningAuthority()
                    .getNamespaceID().getValue(), cx.getAssigningAuthority()
                    .getUniversalID().getValue(), cx.getAssigningAuthority()
                    .getUniversalIDType().getValue());
            Identifier assignFac = new Identifier(cx.getAssigningFacility()
                    .getNamespaceID().getValue(), cx.getAssigningFacility()
                    .getUniversalID().getValue(), cx.getAssigningFacility()
                    .getUniversalIDType().getValue());
            identifier.setAssigningAuthority(CustomAssigningAuthorityUtil.reconcileIdentifier(assignAuth, connection));
            identifier.setAssigningFacility(assignFac);
            identifier.setId(cx.getID().getValue());
            identifier.setIdentifierTypeCode(cx.getIdentifierTypeCode()
                    .getValue());
        }
        return identifier;
    }


}
