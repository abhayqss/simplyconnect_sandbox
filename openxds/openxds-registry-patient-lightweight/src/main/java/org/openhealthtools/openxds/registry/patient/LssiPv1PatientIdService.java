package org.openhealthtools.openxds.registry.patient;

import ca.uhn.hl7v2.model.Message;

public interface LssiPv1PatientIdService {

    String findExistingIdentifier(String originalPatientIdentifierStr);

    void updateAssigningFacilityAccordingToPv1(Message adtMessage);
}
