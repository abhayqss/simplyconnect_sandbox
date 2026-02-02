package org.openhealthtools.openxds.registry.service;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.message.ACK;
import ca.uhn.hl7v2.model.v231.segment.PID;
import com.misyshealthcare.connect.net.IConnectionDescription;
import com.misyshealthcare.connect.net.Identifier;
import org.openhealthexchange.openpixpdq.data.MessageHeader;
import org.openhealthexchange.openpixpdq.data.Patient;
import org.openhealthexchange.openpixpdq.data.PatientIdentifier;
import org.openhealthexchange.openpixpdq.ihe.impl_v2.hl7.HL7Header;
import org.openhealthtools.openxds.registry.api.PatientExtended;
import org.openhealthtools.openxds.registry.api.XdsRegistryPatientService;

public interface OpenXdsRegistryService {
    /**
     * Initiates an acknowledgment instance for the incoming message.
     *
     * @param hl7Header the message header of the incoming message
     * @return an {@link ACK} instance
     * @throws HL7Exception if something is wrong with HL7 message
     * @throws ApplicationException If Application has trouble
     */
    ACK initAcknowledgment(HL7Header hl7Header) throws HL7Exception, ApplicationException;

    Identifier getServerApplication() throws ApplicationException;

    Identifier getServerFacility() throws ApplicationException;

    /**
     * Gets the patient identifier from a Patient PID segment.
     *
     * @param pid the PID segment
     * @return a {@link PatientIdentifier}
     */
    PatientIdentifier getPatientIdentifiers(PID pid);

    /**
     * Validates the incoming Message in this order:
     *
     * <ul>
     * <li> Validate Receiving Facility and Receiving Application</li>
     * <li> Validate Domain </li>
     * <li> Validate patient Id <li>
     * <li> Validate merge patient Id if applicable<li>
     * </ul>
     *
     * @param reply the reply message to be populated if any validation is failed
     * @param hl7Header the message header of the incoming message
     * @param patientId the id of the patient to be validated
     * @param mrgPatientId the id of the patient to be merged
     * @param isPixCreate Whether this validation is for PIX patient creation
     * @return <code>true</code> if the message is correct; <code>false</code>otherwise.
     * @throws HL7Exception if something is wrong with HL7 message
     * @throws ApplicationException if something is wrong with the application
     */
    boolean validateMessage(ACK reply, HL7Header hl7Header, PatientIdentifier patientId, PatientIdentifier mrgPatientId, boolean isPixCreate)
            throws HL7Exception, ApplicationException;

    /**
     * Validates the receiving facility and receiving application of an incoming message.
     *
     * @param reply the reply message to be populated if any validation is failed
     * @param receivingApplication the receiving application of the incoming message
     * @param receivingFacility the receiving facility of the incoming message
     * @param expectedApplication the expected receiving application
     * @param expectedFacility the expected receiving facility
     * @param incomingMessageId the incoming message
     * @return <code>true</code> if validation is passed;
     *         otherwise <code>false</code>.
     * @throws HL7Exception if something is wrong with HL7 message
     * @throws ApplicationException if something is wrong with the application
     */
    boolean validateReceivingFacilityApplication(ACK reply, Identifier receivingApplication,
                                                 Identifier receivingFacility, Identifier expectedApplication, Identifier expectedFacility,
                                                 String incomingMessageId)
            throws HL7Exception, ApplicationException;

    /**
     * Validates a patient identifier domain, namely, assigning authority.
     *
     * @param reply the reply message to be populated if the validation fails
     * @param patientId the patient id
     * @param incomingMessageId the incoming message id
     * @return <code>true</code> if the patient domain is validated successfully;
     *         otherwise <code>false</code>.
     * @throws HL7Exception if something is wrong with HL7 message
     */
    boolean validateDomain(ACK reply, PatientIdentifier patientId, String incomingMessageId)
            throws HL7Exception;

    /**
     * Checks the given whether the given patient id is a valid patient id and populates reply message
     *
     * @param reply the reply message to be populated if any validation is failed
     * @param patientId the patient id to be checked
     * @param header the incoming message header
     * @param isMrgPatientId whether the patient id to be checked is a merge patient id.
     * @param incomingMessageId the incoming message id.
     * @return <code>true</code> if the patientId is valid; otherwise <code>false</code>.
     * @throws HL7Exception if something is wrong with HL7 message
     * @throws ApplicationException if something is wrong with the application
     */
    boolean validatePatientId(ACK reply, PatientIdentifier patientId,
                              MessageHeader header, boolean isMrgPatientId, String incomingMessageId)
            throws HL7Exception, ApplicationException;

    /**
     * Checks the given whether the given patient id is a valid patient. To be more specific, patient is valid if he is
     * successfully found in our system
     * @param patientId the patient id to be checked
     * @param header the incoming message header
     * @return <code>true</code> if the patientId is valid; otherwise <code>false</code>.
     * @throws ApplicationException if something is wrong with the application
     */
    boolean isValidPatient(PatientIdentifier patientId, MessageHeader header) throws ApplicationException;

    /**
     * Converts a PIX Feed Patient message to a {@link Patient} object.
     *
     * @param msgIn the incoming PIX Feed message
     * @return a {@link Patient} object
     * @throws ApplicationException if something is wrong with the application
     */
    PatientExtended getPatient(Message msgIn) throws ApplicationException,HL7Exception;

    void setPatientManager(final XdsRegistryPatientService patientManager);

    void setConnection(final IConnectionDescription connection);
}
