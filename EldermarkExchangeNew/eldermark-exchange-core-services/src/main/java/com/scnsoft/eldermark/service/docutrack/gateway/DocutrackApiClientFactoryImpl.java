package com.scnsoft.eldermark.service.docutrack.gateway;

import com.scnsoft.eldermark.docutrack.ws.api.DocutrackApiSslSocketFactoryGenerator;
import com.scnsoft.eldermark.entity.community.Community;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class DocutrackApiClientFactoryImpl implements DocutrackApiClientFactory {

    @Autowired
    private DocutrackTrustStoresManager docutrackTrustStoresManager;

    @Autowired
    private DocutrackApiSslSocketFactoryGenerator docutrackApiSslSocketFactoryGenerator;

    @Override
    public DocutrackApiClient createDocutrackApiClient(Community community) {
        return new DocutrackApiClient(
                community.getId(),
                community.getDocutrackClientType(),
                community.getDocutrackServerDomain(),
                community.getDocutrackServerCertificateSha1(),
                createTlsParametersProvider(community)
        );
    }

    private DocutrackApiClientTlsParametersProvider createTlsParametersProvider(Community community) {
        byte[] bytes = null;
        if (community.getDocutrackServerCertificateSha1() != null) {
            bytes = docutrackTrustStoresManager.getTrustStoreBytes(community.getId());
        }

        if (bytes == null) {
            return new DefaultTrustTlsParametersProvider();
        } else {
            return new CustomTrustTlsParametersProvider(
                    new ByteArrayInputStream(bytes),
                    docutrackTrustStoresManager.getTrustStoresPassword(),
                    docutrackTrustStoresManager.getTrustStoresType(),
                    community.getDocutrackServerDomain(),
                    docutrackApiSslSocketFactoryGenerator
            );
        }
    }
}
