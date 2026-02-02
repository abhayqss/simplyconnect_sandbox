package org.openhealthtools.openxds.registry.patient;

import org.openhealthexchange.openpixpdq.data.Patient;
import org.openhealthtools.openxds.entity.Resident;
import org.openhealthtools.openxds.registry.api.RegistryPatientException;


public interface XdsRepoPatientService {
    /**
     * Creates a new patient.  This method sends the patient demographics contained
     * in the <code>Patient</code> to the patient manager implementation.
     * <p>
     *
     * @param patient the demographics of the patient to be created
     * @throws RegistryPatientException When there is trouble creating the patient
     */
    Resident createPatient(Patient patient) throws RegistryPatientException;

    /**
     * Updates the patient's demographics in the patient manager implementation.
     * This method sends the updated patient demographics contained
     * in the <code>Patient</code> to the patient manager implementation.
     *
     * @param patient the new demographics of the patient to be updated
     * @throws RegistryPatientException when there is trouble updating the patient
     */
    Resident updatePatient(Patient patient, Long patientRepoId) throws RegistryPatientException;

}
