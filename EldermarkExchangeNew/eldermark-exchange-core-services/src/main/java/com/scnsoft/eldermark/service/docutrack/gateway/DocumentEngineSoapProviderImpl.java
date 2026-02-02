package com.scnsoft.eldermark.service.docutrack.gateway;

import com.scnsoft.eldermark.docutrack.ws.api.DocumentEngine;
import com.scnsoft.eldermark.docutrack.ws.api.DocumentEngineSoap;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.BindingProvider;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DocumentEngineSoapProviderImpl implements DocumentEngineSoapProvider {

    private final Map<Long, SoapPortWrapper> PORT_MAP;
    private final DocumentEngine documentEngine;

    @Autowired
    public DocumentEngineSoapProviderImpl() {
        documentEngine = new DocumentEngine();
        PORT_MAP = new ConcurrentHashMap<>();
    }

    @Override
    public DocumentEngineSoap get(DocutrackApiClient docutrackApiClient) {
        if (PORT_MAP.containsKey(docutrackApiClient.getCommunityId())) {
            var wrapper = PORT_MAP.get(docutrackApiClient.getCommunityId());
            if (wrapper.serverDomain.equals(docutrackApiClient.getServerDomain())
                    && Arrays.equals(wrapper.certSha1, docutrackApiClient.getCertificateSha1Fingerprint())
            ) {
                return wrapper.port;
            }
        }
        return createPort(docutrackApiClient);
    }

    private DocumentEngineSoap createPort(DocutrackApiClient docutrackApiClient) {
        var port = documentEngine.getDocumentEngineSoap();

        docutrackApiClient.getTlsParametersProvider().get()
                .ifPresent(tls -> {
                    var client = ClientProxy.getClient(port);
                    HTTPConduit http = (HTTPConduit) client.getConduit();
                    http.setTlsClientParameters(tls);
                });

        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, docutrackApiClient.getSoapUrl());

        var wrapper = new SoapPortWrapper();
        wrapper.port = port;
        wrapper.serverDomain = docutrackApiClient.getServerDomain();
        wrapper.certSha1 = docutrackApiClient.getCertificateSha1Fingerprint();

        PORT_MAP.put(docutrackApiClient.getCommunityId(), wrapper);

        return port;
    }

    private static class SoapPortWrapper {
        DocumentEngineSoap port;
        String serverDomain;
        byte[] certSha1;
    }
}