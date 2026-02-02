package com.scnsoft.eldermark.consana.sync.client.consana;

import org.hl7.fhir.instance.model.Patient;

public interface ConsanaGateway {

    String getXCLOrganizationId(String xOwningOrgScId);

    Patient getPatient(String patientXrefId, String xOwningOrgScId);
}
