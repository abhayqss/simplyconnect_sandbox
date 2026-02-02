package com.scnsoft.eldermark.service.docutrack.gateway;

import java.security.cert.X509Certificate;
import java.util.Optional;

public interface DocutrackTrustStoresManager {

    byte[] getTrustStoreBytes(Long communityId);

    Optional<X509Certificate> getCertificate(Long communityId);

    String getTrustStoresType();

    String getTrustStoresPassword();

    void updateServerCertificate(Long communityId, X509Certificate certificate);
}
