package com.scnsoft.eldermark.consana.sync.server.services.gateway;

import com.scnsoft.eldermark.consana.sync.server.model.entity.IndividualPerson;
import com.scnsoft.eldermark.consana.sync.server.model.fhir.XCoverage;
import com.scnsoft.eldermark.consana.sync.server.model.fhir.XMedia;
import com.scnsoft.eldermark.consana.sync.server.model.fhir.XMedicationActionPlan;
import org.hl7.fhir.instance.model.*;
import org.hl7.fhir.instance.model.api.IIdType;

import java.util.List;

public interface ConsanaGateway {

    Patient getPatient(String patientXrefId, String xOwningOrgScId);

    //temporary method
    Patient getPatientById(String patientId, String xclId);

    String getXCLOrganizationId(String xOwningOrgScId);

    XCoverage getXCoverage(Patient patient, String xOwningOrgScId);

    List<MedicationOrder> getMedicationOrders(Patient patient, String xOwningOrgScId);

    Medication getMedication(String id);

    List<Condition> getConditions(String patientId, String xOwningOrgScId);

    List<AllergyIntolerance> getAllergyIntolerances(String patientId, String xOwningOrgScId);

    List<Encounter> getEncounters(String patientId, String xOwningOrgScId);

    IndividualPerson getIndividualPerson(IIdType idType);

    List<XMedicationActionPlan> getXMedicationActionPlans(String patientId, String xOwningOrgScId);

    XMedia getXMedia(String mapId, String xOwningOrgScId);
}
