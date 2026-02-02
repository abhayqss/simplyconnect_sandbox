package com.scnsoft.eldermark.consana.sync.server.consana.fhir;

import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.client.api.IBasicClient;
import com.scnsoft.eldermark.consana.sync.server.model.fhir.XCoverage;
import com.scnsoft.eldermark.consana.sync.server.model.fhir.XOwningOrganization;

public interface ConsanaApolloFhirClient extends IBasicClient {

    @Search
    XOwningOrganization getXOwningOrganization(
            @RequiredParam(name = XOwningOrganization.SP_SIMPLYCONNECT_OID) String xrefId
    );

    @Search
    XCoverage getXCoverage(@RequiredParam(name = XCoverage.SP_BENEFICIARY) String beneficiary);
}
