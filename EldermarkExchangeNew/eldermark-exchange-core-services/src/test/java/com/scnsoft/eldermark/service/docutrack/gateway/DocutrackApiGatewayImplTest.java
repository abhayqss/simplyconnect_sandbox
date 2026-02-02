package com.scnsoft.eldermark.service.docutrack.gateway;

import com.scnsoft.eldermark.docutrack.ws.api.DocumentEngineSoap;
import com.scnsoft.eldermark.docutrack.ws.api.DocutrackApiSslSocketFactoryGeneratorImpl;
import com.scnsoft.eldermark.util.KeyStoreUtil;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.CompletableFuture;


/**
 * Class validates that docutrack soap client is working properly.
 * Tests are working against actual DocuTrack Sandbox Api, so launch only when needed
 */

@ExtendWith(MockitoExtension.class)
class DocutrackApiGatewayImplTest {

    @Mock
    private DocumentEngineSoap mockPort;

    DocutrackApiGatewayImpl init(DocumentEngineSoapProvider soapProvider) {
        return new DocutrackApiGatewayImpl(soapProvider, "SimplyConnect", 600000L);
    }

//    @Test
//    void insertDocument_tokenNotPresent_generatesNewToken() {
//        var apiGateway = init(mockSoapProvider());
//
//        when()
//
//    }
    //todo add mocked tests

//    @Test
    void run_SandboxApi() throws IOException {
        var apiGateway = init(sandboxApiProvider());
//        insertDocument_SandboxApi(apiGateway);
        var f = CompletableFuture.runAsync(() -> {
                    try {
                        getDocument_SandboxApi(apiGateway);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        getDocument_SandboxApi(apiGateway);
        f.join();
    }

    void insertDocument_SandboxApi(DocutrackApiGatewayImpl docutrackApiGateway) throws IOException {
        var doc = Thread.currentThread().getContextClassLoader().getResourceAsStream("docutrack/test.tiff").readAllBytes();
        var result = docutrackApiGateway.insertDocument(docutrackApiClient(), "SimplyConnect_test_source",
                "Test Simply Connect Source", "image/tiff", doc, "SCON",
                "test_doc_text_" + System.currentTimeMillis());
        System.out.println(result);
    }

    void getDocument_SandboxApi(DocutrackApiGatewayImpl docutrackApiGateway) throws IOException {
        var doc = docutrackApiGateway.getDocument(docutrackApiClient(), 5426L);
        FileUtils.writeByteArrayToFile(new File("Test_out" + System.currentTimeMillis() + ".pdf"), doc);
    }


    private DocumentEngineSoapProvider mockSoapProvider() {
        return Mockito.mock(DocumentEngineSoapProvider.class);
    }

    private DocumentEngineSoapProvider sandboxApiProvider() {
        return new DocumentEngineSoapProviderImpl();
    }

    private DocutrackApiClient docutrackApiClient() {
        var type = "jks";
        var pass = "pass";
        var domain = "dtpublic.integragroup.com";

        var is = prepareTrustStore(type, pass);

        var trustProvider = new CustomTrustTlsParametersProvider(
                is,
                pass,
                type,
                domain,
                new DocutrackApiSslSocketFactoryGeneratorImpl()
        );

        return new DocutrackApiClient(
                1L,
                "QS/1",
                domain,
                null,
                trustProvider
        );
    }

    private InputStream prepareTrustStore(String type, String pass) {
        try {
            var certificateStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("docutrack/docutrack_sandbox.cer");

            var ts = KeyStoreUtil.createKeyStore(type, pass);
            KeyStoreUtil.addCertificate(ts, "alias", KeyStoreUtil.loadX509Certificate(certificateStream));

            var baos = new ByteArrayOutputStream();
            ts.store(baos, pass.toCharArray());
            baos.close();

            return new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

}