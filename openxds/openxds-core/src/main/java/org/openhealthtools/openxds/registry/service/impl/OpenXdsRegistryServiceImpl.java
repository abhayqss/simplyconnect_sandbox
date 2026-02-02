package org.openhealthtools.openxds.registry.service.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.datatype.CX;
import ca.uhn.hl7v2.model.v231.datatype.HD;
import ca.uhn.hl7v2.model.v231.message.ACK;
import ca.uhn.hl7v2.model.v231.segment.PID;
import com.misyshealthcare.connect.net.IConnectionDescription;
import com.misyshealthcare.connect.net.Identifier;
import org.openhealthexchange.openpixpdq.data.MessageHeader;
import org.openhealthexchange.openpixpdq.data.PatientIdentifier;
import org.openhealthexchange.openpixpdq.ihe.configuration.Configuration;
import org.openhealthexchange.openpixpdq.ihe.configuration.IheConfigurationException;
import org.openhealthexchange.openpixpdq.ihe.impl_v2.hl7.HL7Header;
import org.openhealthexchange.openpixpdq.ihe.impl_v2.hl7.HL7v231;
import org.openhealthexchange.openpixpdq.util.AssigningAuthorityUtil;
import org.openhealthtools.common.utils.CustomAssigningAuthorityUtil;
import org.openhealthtools.openxds.entity.PersonIdentifier;
import org.openhealthtools.openxds.registry.BaseHandler;
import org.openhealthtools.openxds.registry.HL7V231ConverterCustom;
import org.openhealthtools.openxds.registry.api.PatientExtended;
import org.openhealthtools.openxds.registry.api.RegistryPatientContext;
import org.openhealthtools.openxds.registry.api.RegistryPatientException;
import org.openhealthtools.openxds.registry.api.XdsRegistryPatientService;
import org.openhealthtools.openxds.registry.service.OpenXdsRegistryService;
import org.springframework.stereotype.Component;

/**
 * service for common functionality in handlers
 */
@Component
public class OpenXdsRegistryServiceImpl implements OpenXdsRegistryService {

    private XdsRegistryPatientService patientManager;

    private IConnectionDescription connection;

    /**
     * Initiates an acknowledgment instance for the incoming message.
     *
     * @param hl7Header the message header of the incoming message
     * @return an {@link ACK} instance
     * @throws HL7Exception if something is wrong with HL7 message
     * @throws ApplicationException If Application has trouble
     */
    public ACK initAcknowledgment(HL7Header hl7Header) throws HL7Exception, ApplicationException {
        //Send Response
        ACK reply = new ACK();

        //For the response message, the ReceivingApplication and ReceivingFacility
        //will become the sendingApplication and sendingFacility;
        //Also the sendingApplication and sendingFacility will become the
        //receivingApplication and receivingFacility.
        Identifier serverApplication = getServerApplication();
        Identifier serverFacility = getServerFacility();
        Identifier sendingApplication = hl7Header.getSendingApplication();
        Identifier sendingFacility = hl7Header.getSendingFacility();
        try {
            String event = hl7Header.getTriggerEvent();
            HL7v231.populateMSH(reply.getMSH(), "ACK", event, BaseHandler.getMessageControlId(),
                    serverApplication, serverFacility, sendingApplication, sendingFacility);
        } catch (IheConfigurationException e) {
            throw new ApplicationException("Error populate message header", e);
        }

        return reply;
    }

    public Identifier getServerFacility() throws ApplicationException {
        Identifier ret = null;
        try {
            ret = Configuration.getIdentifier(connection,
                    "ReceivingFacility", true);
        } catch (IheConfigurationException e) {
            throw new ApplicationException(
                    "Missing ReceivingFacility for connection "
                            + connection.getDescription(), e);
        }
        return ret;
    }

    public Identifier getServerApplication() throws ApplicationException {
        Identifier ret = null;
        try {
            ret = Configuration.getIdentifier(connection,
                    "ReceivingApplication", true);
        } catch (IheConfigurationException e) {
            throw new ApplicationException(
                    "Missing receivingApplication for connection "
                            + connection.getDescription(), e);
        }
        return ret;
    }

    public PatientIdentifier getPatientIdentifiers(PID pid) {
        PatientIdentifier identifier = new PatientIdentifier();
        CX[] cxs = pid.getPatientIdentifierList();
        for (CX cx : cxs) {

            Identifier assignAuth = HDtoIdentifier(cx.getAssigningAuthority());
            Identifier assignFac = HDtoIdentifier(cx.getAssigningFacility());

            identifier.setAssigningAuthority(CustomAssigningAuthorityUtil.reconcileIdentifier(assignAuth, connection));
            identifier.setAssigningFacility(assignFac);
            identifier.setId(cx.getID().getValue());
            identifier.setIdentifierTypeCode(cx.getIdentifierTypeCode()
                    .getValue());
        }
        return identifier;
    }

    private Identifier HDtoIdentifier(HD hd) {
        return new Identifier(
                hd.getNamespaceID().getValue(),
                PersonIdentifier.removeAmpersandsEncoding(hd.getUniversalID().getValue()),
                PersonIdentifier.removeAmpersandsEncoding(hd.getUniversalIDType().getValue()));
    }

    public boolean validateMessage(ACK reply, HL7Header hl7Header, PatientIdentifier patientId, PatientIdentifier mrgPatientId, boolean isPixCreate)
            throws HL7Exception, ApplicationException {
        Identifier serverApplication = getServerApplication();
        Identifier serverFacility = getServerFacility();
        Identifier receivingApplication = hl7Header.getReceivingApplication();
        Identifier receivingFacility = hl7Header.getReceivingFacility();
        String incomingMessageId = hl7Header.getMessageControlId();
        //1. validate receiving facility and receiving application
        boolean isValidFacilityApplication = validateReceivingFacilityApplication(reply,
                receivingApplication, receivingFacility,
                serverApplication, serverFacility, incomingMessageId);
        if (!isValidFacilityApplication) return false;

        //2.validate the domain
        boolean isValidDomain = validateDomain(reply, patientId, incomingMessageId);
        if (!isValidDomain) return false;

        //3. validate ID itself
        if (!isPixCreate) {
            //Do not valid patient id for PIX patient creation
            boolean isValidPid = validatePatientId(reply, patientId, hl7Header.toMessageHeader(), false, incomingMessageId);
            if (!isValidPid) return false;
        }

        //4. validate mrgPatientId
        if (mrgPatientId != null) {
            boolean isValidMrgPid = validatePatientId(reply, mrgPatientId, hl7Header.toMessageHeader(), true, incomingMessageId);
            if (!isValidMrgPid) return false;
        }

        //Finally, it must be true when it reaches here
        return true;
    }

    public boolean validateReceivingFacilityApplication(ACK reply, Identifier receivingApplication,
                                                        Identifier receivingFacility, Identifier expectedApplication, Identifier expectedFacility,
                                                        String incomingMessageId)
            throws HL7Exception, ApplicationException {
        //In case of tests, don't validate receiving application and facility,
        //It is not easy to switch to different receiving applications and facilities
        boolean isTest = Boolean.parseBoolean(connection.getProperty("test"));
        if (isTest) return true;

        //We first need to validate ReceivingApplication and ReceivingFacility.
        //Currently we are not validating SendingApplication and SendingFacility
        if (!receivingApplication.equals(expectedApplication)) {
            HL7v231.populateMSA(reply.getMSA(), "AE", incomingMessageId);
            //segmentId=MSH, sequence=1, fieldPosition=5, fieldRepetition=1, componentNubmer=1
            HL7v231.populateERR(reply.getERR(), "MSH", "1", "5", "1", "1",
                    null, "Unknown Receiving Application");
            return false;
        }
        if (!receivingFacility.equals(expectedFacility)) {
            HL7v231.populateMSA(reply.getMSA(), "AE", incomingMessageId);
            //segmentId=MSH, sequence=1, fieldPosition=6, fieldRepetition=1, componentNubmer=1
            HL7v231.populateERR(reply.getERR(), "MSH", "1", "6", "1", "1",
                    null, "Unknown Receiving Facility");
            return false;
        }

        return true;
    }

    public boolean validatePatientId(ACK reply, PatientIdentifier patientId,
                                     MessageHeader header, boolean isMrgPatientId, String incomingMessageId)
            throws HL7Exception, ApplicationException {
        boolean validPatient = isValidPatient(patientId, header);
        if (!validPatient) {
            HL7v231.populateMSA(reply.getMSA(), "AE", incomingMessageId);
            if (isMrgPatientId) {
                //segmentId=MRG, sequence=1, fieldPosition=1, fieldRepetition=1, componentNubmer=1
                HL7v231.populateERR(reply.getERR(), "MRG", "1", "1", "1", "1",
                        "204", "Unknown Key Identifier");
            } else {
                //segmentId=PID, sequence=1, fieldPosition=3, fieldRepetition=1, componentNubmer=1
                HL7v231.populateERR(reply.getERR(), "PID", "1", "3", "1", "1",
                        "204", "Unknown Key Identifier");
            }
        }
        return validPatient;
    }

    public boolean isValidPatient(PatientIdentifier patientId, MessageHeader header) throws ApplicationException {
        boolean validPatient;
        RegistryPatientContext context = new RegistryPatientContext(header);
        try {
            validPatient = patientManager.isValidPatient(patientId, context);
        } catch (RegistryPatientException e) {
            throw new ApplicationException(e);
        }
        return validPatient;
    }


    public boolean validateDomain(ACK reply, PatientIdentifier patientId, String incomingMessageId)
            throws HL7Exception {
        Identifier domain = patientId.getAssigningAuthority();
        boolean domainOk = AssigningAuthorityUtil.validateDomain(
                domain, connection);
        if (!domainOk) {
            HL7v231.populateMSA(reply.getMSA(), "AE", incomingMessageId);
            //segmentId=PID, sequence=1, fieldPosition=3, fieldRepetition=1,componentNubmer=4
            HL7v231.populateERR(reply.getERR(), "PID", "1", "3", "1", "4",
                    "204", "Unknown Key Identifier");
            return false;
        }
        return true;
    }

    public PatientExtended getPatient(Message msgIn) throws ApplicationException, HL7Exception {

        HL7V231ConverterCustom convertor = null;
        if (msgIn.getVersion().equals("2.3.1")) {
            convertor = new HL7V231ConverterCustom(msgIn, connection);
        } else {
            throw new ApplicationException("Unexpected HL7 version");
        }

        PatientExtended patientDesc = new PatientExtended();
        patientDesc.setPatientIds(convertor.getPatientIds());
        patientDesc.setPatientName(convertor.getPatientName());
        patientDesc.setMonthersMaidenName(convertor.getMotherMaidenName());
        patientDesc.setBirthDateTime(convertor.getBirthDate());
        patientDesc.setAdministrativeSex(convertor.getSexType());
        patientDesc.setPatientAlias(convertor.getPatientAliasName());
        patientDesc.setRace(convertor.getRace());
        patientDesc.setPrimaryLanguage(convertor.getPrimaryLanguage());
        patientDesc.setMaritalStatus(convertor.getMartialStatus());
        patientDesc.setReligion(convertor.getReligion());
        patientDesc.setPatientAccountNumber(convertor.getpatientAccountNumber());
        patientDesc.setSsn(convertor.getSsn());
        patientDesc.setDriversLicense(convertor.getDriversLicense());
        patientDesc.setMonthersId(convertor.getMonthersId());
        patientDesc.setEthnicGroup(convertor.getEthnicGroup());
        patientDesc.setBirthPlace(convertor.getBirthPlace());
        patientDesc.setBirthOrder(convertor.getBirthOrder());
        patientDesc.setCitizenship(convertor.getCitizenShip());
        patientDesc.setDeathDate(convertor.getDeathDate());
        patientDesc.setDeathIndicator(convertor.getDeathIndicator());
        patientDesc.setPhoneNumbers(convertor.getPhoneList());
        patientDesc.setEmails(convertor.getEmails());
        patientDesc.setAddresses(convertor.getAddressList());
        patientDesc.setVisits(convertor.getVisitList());

        patientDesc.setPrimaryLanguageIdentifier(convertor.getPrimaryLanguageIdentifier());
        patientDesc.setMaritalStatusIdentifier(convertor.getMartialStatusIdentifier());
        patientDesc.setRaceIdentifier(convertor.getRaceIdentifier());
        patientDesc.setReligionIdentifier(convertor.getReligionIdentifier());
        patientDesc.setEthnicGroupIdentifier(convertor.getEthnicGroupIdentifier());
        patientDesc.setVeteranStatus(convertor.getVeteranMilitaryStatus());
        patientDesc.setVeteranStatusIdentifier(convertor.getVeteranMilitaryStatusIdentifier());
        patientDesc.setNationality(convertor.getNationality());
        patientDesc.setNationalityIdentifier(convertor.getNationalityIdentifier());

        return patientDesc;
    }

    public void setPatientManager(final XdsRegistryPatientService patientManager) {
        this.patientManager = patientManager;
    }

    public void setConnection(final IConnectionDescription connection) {
        this.connection = connection;
    }
}
