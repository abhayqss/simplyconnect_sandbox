package com.scnsoft.eldermark.consana.sync.client.consana.fhir;

import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.client.api.IBasicClient;
import com.scnsoft.eldermark.consana.sync.client.consana.fhir.model.XOwningOrganization;

public interface ConsanaApolloFhirClient extends IBasicClient {

    @Search
    XOwningOrganization getXOwningOrganization(
            @RequiredParam(name = XOwningOrganization.SP_SIMPLYCONNECT_OID) String xOwningOrgScId
    );
}
