package com.scnsoft.eldermark.consana.sync.server.consana.fhir;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.client.api.IBasicClient;
import org.hl7.fhir.instance.model.*;
import org.hl7.fhir.instance.model.api.IIdType;

import java.util.List;

public interface ConsanaFhirClient extends IBasicClient {

    @Search
    Patient getPatient(@RequiredParam(name = "scid") String xrefId);

    @Read
    Patient getResourceById(@IdParam IdType theId);

    @Search
    List<MedicationOrder> getMedicationOrderList(@RequiredParam(name = "patient") String patient);

    @Read
    Medication getMedication(@IdParam IdType id);

    @Search
    List<Condition> getConditionList(@RequiredParam(name = "patient") String patient);

    @Search
    List<AllergyIntolerance> getAllergyIntoleranceList(@RequiredParam(name = "patient") String patient);

    @Search
    List<Encounter> getEncounterList(@RequiredParam(name = "patient") String patient);

    @Read
    Practitioner getPractitioner(@IdParam IIdType id);

    @Read
    RelatedPerson getRelatedPerson(@IdParam IIdType id);
}