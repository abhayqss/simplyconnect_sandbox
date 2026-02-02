package org.openhealthtools.openxds.registry.handlers;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import com.misyshealthcare.connect.net.IConnectionDescription;
import org.openhealthtools.openxds.registry.api.XdsRegistryPatientService;

/**
 * helper method to handle ITI-39 messages
 */
public interface Iti39Handler {
    /**
     * the same functionality as in ca.uhn.hl7v2.app.Application
     * @param msgIn
     * @return
     * @throws ApplicationException
     * @throws HL7Exception
     */
    Message processMessage(Message msgIn) throws ApplicationException, HL7Exception;

    void setPatientManager(final XdsRegistryPatientService patientManager);

    void setConnection(final IConnectionDescription connection);

    void init();
}
