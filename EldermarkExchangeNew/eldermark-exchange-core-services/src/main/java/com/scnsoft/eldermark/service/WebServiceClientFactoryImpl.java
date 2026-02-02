package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.DirectConfiguration;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.service.direct.DirectConfigurationService;
import com.scnsoft.eldermark.services.direct.ws.mail.SecureIntegrationServiceImap;
import com.scnsoft.eldermark.services.direct.ws.mail.SecureIntegrationServicesImap;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.Merlin;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.soap.security.wss4j2.support.CryptoFactoryBean;

import javax.security.auth.callback.CallbackHandler;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

@Component
@Transactional
public class WebServiceClientFactoryImpl implements WebServiceClientFactory {
    private static final Logger logger = LoggerFactory.getLogger(WebServiceClientFactoryImpl.class);

    @Autowired
    private DirectConfigurationService directConfigurationService;

    private static SecureIntegrationServicesImap secureIntegrationServicesImap;

    public WebServiceClientFactoryImpl() {
        logger.info("Below might be logged error \"org.apache.cxf.transport.https.SSLUtils  : Default key managers cannot be initialized: name\". " +
                "This doesn't affect application, never mind");
        secureIntegrationServicesImap = new SecureIntegrationServicesImap();
    }

    public SecureIntegrationServiceImap createMailPort(String companyCode) {

        SecureIntegrationServiceImap port = secureIntegrationServicesImap.getWSHttpBindingSecureIntegrationServiceImap();

        Client client = ClientProxy.getClient(port);

        fillRequestContext(companyCode, client);

        if (logger.isDebugEnabled()) {
            client.getInInterceptors().add(new LoggingInInterceptor());
            client.getOutInterceptors().add(new LoggingOutInterceptor());
        }

        return port;
    }

    private void fillRequestContext(final String companyCode, Client client) {
        DirectConfiguration config = directConfigurationService.find(companyCode).orElseThrow(
                () -> new BusinessException("Secure Messaging functionality was not configured yet.")
        );

        try {
            Map<String, Object> requestContext = client.getRequestContext();
            requestContext.put("ws-security.callback-handler.sct", (CallbackHandler) callbacks -> {
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
                pc.setPassword(config.getPin());
            });

            String keystoreLocation = directConfigurationService.getKeystoreLocation(companyCode);
            String keystoreType = "PKCS12";
            String keystorePassword = config.getPin();

             Crypto crypto = buildCryptoObject(keystoreLocation, keystoreType, keystorePassword);

            requestContext.put("ws-security.encryption.crypto.sct", crypto);
            requestContext.put("ws-security.signature.crypto.sct", crypto);

        } catch (Exception e) {
            directConfigurationService.setConfigured(companyCode, false);

            logger.error("Exception while creating SES web service client: " + companyCode, e);
            throw new BusinessException("Exception while creating SES web service client: " + companyCode);
        }
        directConfigurationService.setConfigured(companyCode, true);
    }

    private Crypto buildCryptoObject(String keystoreLocation, String keystoreType, String keystorePassword) throws Exception {
        CryptoFactoryBean cryptoFactory = new CryptoFactoryBean();
        cryptoFactory.setCryptoProvider(Merlin.class);

        //we can't use cryptoFactory.setKeyStoreLocation because it accepts resource. Then it takes file of resource and
        //takes absolute path. We need to be able to pass urls in external forms.
        final Properties properties = new Properties();
        properties.setProperty("org.apache.ws.security.crypto.merlin.file", resolveKeystoreLocationForMerlin(keystoreLocation));
        cryptoFactory.setConfiguration(properties);

        cryptoFactory.setKeyStorePassword(keystorePassword);
        cryptoFactory.setKeyStoreType(keystoreType);

        cryptoFactory.setDefaultX509Alias(findFirstAlias(keystoreLocation, keystoreType, keystorePassword));

        cryptoFactory.afterPropertiesSet();
        return cryptoFactory.getObject();
    }

    @SuppressFBWarnings(
        value = "PATH_TRAVERSAL_IN",
        justification = "keystoreLocation is taken from DirectConfiguration and cannot be configured by user for now"
    )
    private String resolveKeystoreLocationForMerlin(String keystoreLocation) throws MalformedURLException {
//      Please refer to https://jira.scnsoft.com/browse/SCPAPP-1211 to understand why this is needed.
        if (Thread.currentThread().getContextClassLoader() instanceof URLClassLoader) {
            try {
                var resource = Thread.currentThread().getContextClassLoader().getResource(keystoreLocation);
                if (resource == null) {
                    return Paths.get(keystoreLocation).toUri().toURL().toExternalForm();
                }
            } catch (Exception ex) {
                return Paths.get(keystoreLocation).toUri().toURL().toExternalForm();
            }
        }
        return keystoreLocation;
    }

    @SuppressFBWarnings(
        value = "PATH_TRAVERSAL_IN",
        justification = "keystoreLocation is taken from DirectConfiguration and cannot be configured by user for now"
    )
    private String findFirstAlias(String keystoreLocation, String keystoreType, String keystorePassword) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        InputStream stream = new FileInputStream(keystoreLocation);

        KeyStore store = KeyStore.getInstance(keystoreType);
        store.load(stream, keystorePassword.toCharArray());

        Enumeration<String> aliases = store.aliases();
        return aliases.nextElement();
    }
}
