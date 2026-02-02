package com.scnsoft.eldermark.service.docutrack.gateway;

import com.scnsoft.eldermark.docutrack.ws.api.DocutrackApiSslSocketFactoryGenerator;
import org.apache.cxf.configuration.jsse.TLSClientParameters;

import java.io.InputStream;
import java.util.Optional;

public class CustomTrustTlsParametersProvider implements DocutrackApiClientTlsParametersProvider {

    private final InputStream trustStore;
    private final String trustStorePass;
    private final String trustStoreType;
    private final String serverDomain;
    private final DocutrackApiSslSocketFactoryGenerator docutrackApiSslSocketFactoryGenerator;

    public CustomTrustTlsParametersProvider(
            InputStream trustStore,
            String trustStorePass,
            String trustStoreType,
            String serverDomain,
            DocutrackApiSslSocketFactoryGenerator docutrackApiSslSocketFactoryGenerator) {
        this.trustStore = trustStore;
        this.trustStorePass = trustStorePass;
        this.trustStoreType = trustStoreType;
        this.serverDomain = serverDomain;
        this.docutrackApiSslSocketFactoryGenerator = docutrackApiSslSocketFactoryGenerator;
    }

    @Override
    public Optional<TLSClientParameters> get() {
        var tls = new TLSClientParameters();

        tls.setSSLSocketFactory(
                docutrackApiSslSocketFactoryGenerator.getSSLSocketFactory(
                        trustStore,
                        trustStorePass,
                        trustStoreType
                )
        );


        tls.setHostnameVerifier((hostname, sslSession) -> hostname.equals(extractHost()));

        return Optional.of(tls);
    }

    private String extractHost() {
        var idx = serverDomain.indexOf(':');
        if (idx == -1) {
            return serverDomain;
        }
        return serverDomain.substring(0, idx);
    }
}
