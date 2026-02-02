package com.scnsoft.eldermark.consana.sync.client.consana.fhir;

import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.client.api.IBasicClient;
import org.hl7.fhir.instance.model.Patient;

public interface ConsanaFhirClient extends IBasicClient {

    @Search
    Patient getPatient(@RequiredParam(name = "scid") String xrefId);
}