package com.scnsoft.eldermark.consana.sync.server.consana.auth;

import ca.uhn.fhir.rest.client.api.IBasicClient;

public interface ConsanaAuthenticationManager {

    void authenticate(IBasicClient client);

}
