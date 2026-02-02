package com.scnsoft.eldermark.service.docutrack.gateway;

import com.scnsoft.eldermark.docutrack.ws.api.DocutrackApiSslSocketFactoryGenerator;
import com.scnsoft.eldermark.entity.community.Community;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocutrackApiClientFactoryTest {
    private static final String SERVER_DOMAIN = "server.domain";
    private static final String BUSINESS_UNIT_CODE = "buc";

    @Mock
    private DocutrackTrustStoresManager docutrackTrustStoresManager;

    @Mock
    private DocutrackApiSslSocketFactoryGenerator docutrackApiSslSocketFactoryGenerator;

    @InjectMocks
    private DocutrackApiClientFactoryImpl instance;

    @Test
    void createDocutrackApiClient_whenNoCertNoTrustStore_TrustDefault() {
        var pharmacy = createPharmacy(null);

        var actual = instance.createDocutrackApiClient(pharmacy);

        assertPharmacyFieldsCorrect(actual, pharmacy);
        assertThat(actual.getTlsParametersProvider()).isExactlyInstanceOf(DefaultTrustTlsParametersProvider.class);
    }

    @Test
    void createDocutrackApiClient_whenHasCertNoTrustStore_TrustDefault() {
        var pharmacy = createPharmacy(new byte[20]);

        when(docutrackTrustStoresManager.getTrustStoreBytes(pharmacy.getId())).thenReturn(null);

        var actual = instance.createDocutrackApiClient(pharmacy);

        assertPharmacyFieldsCorrect(actual, pharmacy);
        assertThat(actual.getTlsParametersProvider()).isExactlyInstanceOf(DefaultTrustTlsParametersProvider.class);
    }

    @Test
    void createDocutrackApiClient_whenHasCertHasTrustStore_TrustCustom() {
        var sha1 = new byte[20];
        var pharmacy = createPharmacy(sha1);
        var password = "password";
        var type = "jks";
        var bytes = new byte[]{1, 2, 3};
        var expectedProvider = new CustomTrustTlsParametersProvider(
                new ByteArrayInputStream(bytes),
                password,
                type,
                SERVER_DOMAIN,
                docutrackApiSslSocketFactoryGenerator
        );


        when(docutrackTrustStoresManager.getTrustStoreBytes(pharmacy.getId())).thenReturn(bytes);
        when(docutrackTrustStoresManager.getTrustStoresPassword()).thenReturn(password);
        when(docutrackTrustStoresManager.getTrustStoresType()).thenReturn(type);

        var actual = instance.createDocutrackApiClient(pharmacy);

        assertPharmacyFieldsCorrect(actual, pharmacy);
        assertThat(actual.getTlsParametersProvider()).isInstanceOf(CustomTrustTlsParametersProvider.class);
        assertThat((CustomTrustTlsParametersProvider) actual.getTlsParametersProvider())
                .usingRecursiveComparison()
                .isEqualTo(expectedProvider);
    }


    private void assertPharmacyFieldsCorrect(DocutrackApiClient actual, Community pharmacy) {
        assertThat(actual.getCommunityId()).isEqualTo(pharmacy.getId());
        assertThat(actual.getClientType()).isEqualTo(pharmacy.getDocutrackClientType());
        assertThat(actual.getServerDomain()).isEqualTo(pharmacy.getDocutrackServerDomain());
        assertThat(actual.getCertificateSha1Fingerprint()).isEqualTo(pharmacy.getDocutrackServerCertificateSha1());
    }

    private Community createPharmacy(byte[] certificateSha1) {
        var community = new Community();
        community.setId(1L);
        community.setIsDocutrackPharmacy(true);
        community.setDocutrackClientType("ClientType");
        community.setBusinessUnitCodes(Collections.singletonList(BUSINESS_UNIT_CODE));
        community.setDocutrackServerDomain(SERVER_DOMAIN);
        community.setDocutrackServerCertificateSha1(certificateSha1);

        return community;
    }

    private byte[] readAllBytes(InputStream inputStream) {
        try {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}