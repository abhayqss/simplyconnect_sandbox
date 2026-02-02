package com.scnsoft.eldermark.services.connect;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import gov.hhs.fha.nhinc.callback.openSAML.NhinCertificateManager;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.xml.ws.BindingProvider;
import java.nio.file.Path;
import java.nio.file.Paths;


public abstract class NhinAbstractService {
    private static final Logger logger = LoggerFactory.getLogger(NhinAbstractService.class);

    @Value("${connect.keystore}")
    private String keystoreLocation;

    @Value("${connect.keystore.password}")
    private String keystorePassword;

    @Value("${connect.truststore}")
    private String trustStoreLocation;

    @Value("${connect.truststore.password}")
    private String trustStorePassword;

    @Value("${connect.sign.alias}")
    private String keystoreSignAlias;

    @Value("${connect.encrypt.alias}")
    private String keystoreEncryptAlias;

    private NhinCertificateManager certificateManager;

    @PostConstruct
    public void initCertificateManager() {
        certificateManager = new NhinCertificateManager(
                keystoreLocation, keystorePassword, "JKS",
                keystoreSignAlias,
                trustStoreLocation, trustStorePassword, "JKS");
    }

    protected void applySSLParameters(BindingProvider provider, final ExchangeUserDetails userDetails) {
        try {
            final Client client = ClientProxy.getClient(provider);

//            To enable SAML token logic revert to 1295 revision.

//            Map<String, Object> requestContext = provider.getRequestContext();
//
//            Properties signProperties = new Properties();
//            signProperties.put("org.apache.ws.security.crypto.merlin.keystore.password", keystorePassword);
//            signProperties.put("org.apache.ws.security.crypto.merlin.keystore.type", "JKS");
//            signProperties.put("org.apache.ws.security.crypto.merlin.keystore.file", getRelativePath(keystoreLocation));
//            signProperties.put("org.apache.ws.security.crypto.merlin.keystore.alias", keystoreSignAlias);
//
//            Properties encryptProperties = new Properties();
//            encryptProperties.put("org.apache.ws.security.crypto.merlin.keystore.password", keystorePassword);
//            encryptProperties.put("org.apache.ws.security.crypto.merlin.keystore.type", "JKS");
//            encryptProperties.put("org.apache.ws.security.crypto.merlin.keystore.file", getRelativePath(keystoreLocation));
//            encryptProperties.put("org.apache.ws.security.crypto.merlin.keystore.alias", keystoreEncryptAlias);
//
//            requestContext.put("ws-security.signature.properties", signProperties);
//            requestContext.put("ws-security.encryption.properties", encryptProperties);
//            requestContext.put("ws-security.encryption.username", keystoreEncryptAlias);
//            requestContext.put("ws-security.signature.username", keystoreSignAlias);
//
//            requestContext.put("ws-security.callback-handler", new CallbackHandler() {
//                @Override
//                public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
//                    WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
//                    if (keystoreSignAlias.equals(pc.getIdentifier()))
//                        pc.setPassword(certificateManager.getKeystorePassword());
//                }
//            });
//
//            requestContext.put(
//                    "ws-security.saml-callback-handler", new ExchangeCXFSAMLCallbackHandler(
//                            new ExchangeHOKSAMLAssertionBuilder(certificateManager)) {
//                        @Override
//                        protected AssertionType populateAssertion() {
//                            return ConnectUtil.createAssertion(userDetails);
//                        }
//                    }
//            );

            client.getInInterceptors().add(new LoggingInInterceptor());
            client.getOutInterceptors().add(new LoggingOutInterceptor());

            final HTTPConduit http = (HTTPConduit) client.getConduit();
            final TLSClientParameters tlsParametes = new TLSClientParameters();

            final SSLSocketFactoryGenerator sslSocketFactoryGenerator = new SSLSocketFactoryGenerator(certificateManager.getKeyStore(),
                    certificateManager.getTrustStore(), keystorePassword);

            tlsParametes.setSSLSocketFactory(sslSocketFactoryGenerator.getSSLSocketFactory());

            // TODO: set disable CN Check false after correct certificate (from godaddy) would be used by Prod gateway
            tlsParametes.setDisableCNCheck(true);

            http.setTlsClientParameters(tlsParametes);
            client.setConduitSelector(new CxfConduilSelector(client.getConduitSelector(), http));

            final HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
            httpClientPolicy.setConnectionTimeout(5*60*1000L); // 5 min.
            httpClientPolicy.setReceiveTimeout(15*60*1000L); // 15 min.
            httpClientPolicy.setAllowChunking(false);
            http.setClient(httpClientPolicy);

        } catch (Exception e) {
            logger.error("Can't apply SSL parameters", e);
        }
    }

    private String getRelativePath(String absolutePathStr) {
        Path pathAbsolute =  Paths.get(absolutePathStr);
        Path pathBase = Paths.get(".").toAbsolutePath();
        try {
            return pathBase.relativize(pathAbsolute).toString();
        } catch (IllegalArgumentException e) {
            logger.error("Gateway keystore has different root: " + pathAbsolute, e);
            return absolutePathStr;
        }
    }
}
