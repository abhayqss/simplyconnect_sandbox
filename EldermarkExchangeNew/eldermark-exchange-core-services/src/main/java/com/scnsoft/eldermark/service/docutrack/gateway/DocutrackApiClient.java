package com.scnsoft.eldermark.service.docutrack.gateway;

import java.util.Objects;

public class DocutrackApiClient {

    private final Long communityId;
    private final String clientType;
    private final String serverDomain;
    private final byte[] certificateSha1Fingerprint;
    private final DocutrackApiClientTlsParametersProvider tlsParametersProvider;

    public DocutrackApiClient(Long communityId,
                              String clientType,
                              String serverDomain,
                              byte[] certificateSha1Fingerprint,
                              DocutrackApiClientTlsParametersProvider tlsParametersProvider) {
        this.communityId = Objects.requireNonNull(communityId);
        this.clientType = Objects.requireNonNull(clientType);
        this.serverDomain = Objects.requireNonNull(serverDomain);
        this.certificateSha1Fingerprint = certificateSha1Fingerprint;
        this.tlsParametersProvider = Objects.requireNonNull(tlsParametersProvider);
    }

    public Long getCommunityId() {
        return communityId;
    }

    public String getClientType() {
        return clientType;
    }

    public String getServerDomain() {
        return serverDomain;
    }

    public byte[] getCertificateSha1Fingerprint() {
        return certificateSha1Fingerprint;
    }

    public DocutrackApiClientTlsParametersProvider getTlsParametersProvider() {
        return tlsParametersProvider;
    }

    public String getSoapUrl() {
        return getSoapUrl(this.serverDomain);
    }

    public static String getSoapUrl(String serverDomain) {
        return "https://" + serverDomain + "/DocuTrack/DirectConnect/DocumentEngine.asmx";
    }
}
