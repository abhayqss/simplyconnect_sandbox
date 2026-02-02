package com.scnsoft.eldermark.service.docutrack.gateway;

import org.apache.cxf.configuration.jsse.TLSClientParameters;

import java.util.Optional;

public class DefaultTrustTlsParametersProvider implements DocutrackApiClientTlsParametersProvider {

    @Override
    public Optional<TLSClientParameters> get() {
        return Optional.empty();
    }
}
