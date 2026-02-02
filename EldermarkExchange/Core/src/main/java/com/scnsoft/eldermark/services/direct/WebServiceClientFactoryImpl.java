package com.scnsoft.eldermark.services.direct;

import com.scnsoft.eldermark.entity.DirectConfiguration;
import com.scnsoft.eldermark.services.direct.ws.directory.DirectoryServices;
import com.scnsoft.eldermark.services.direct.ws.directory.IDirectoryServices;
import com.scnsoft.eldermark.services.direct.ws.mail.SecureIntegrationServiceImap;
import com.scnsoft.eldermark.services.direct.ws.mail.SecureIntegrationServicesImap;
import com.scnsoft.eldermark.services.direct.ws.register.RegistrationAPI;
import com.scnsoft.eldermark.services.direct.ws.register.RegistrationService;
import com.scnsoft.eldermark.shared.exceptions.DirectMessagingException;
import com.scnsoft.eldermark.shared.exceptions.DirectNotConfiguredException;
import com.scnsoft.eldermark.util.ClassLoaderUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.Merlin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.security.wss4j.support.CryptoFactoryBean;

import javax.annotation.PostConstruct;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.security.KeyStore;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;


@Component
public class WebServiceClientFactoryImpl implements WebServiceClientFactory {
    private static final Logger logger = LoggerFactory.getLogger(WebServiceClientFactoryImpl.class);

    private static SecureIntegrationServicesImap secureIntegrationServicesImap = null;
    private static RegistrationAPI registrationAPI = null;
    private static DirectoryServices directoryServices = null;

    @Autowired
    private DirectConfigurationService directConfigurationService;

    @PostConstruct
    public void init() throws MalformedURLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ClassLoaderUtils.addPathToSystemClassloader(directConfigurationService.getKeystoresBaseLocation());
    }

    protected SecureIntegrationServicesImap getSecureIntegrationServiceImap() {
        if (secureIntegrationServicesImap == null) secureIntegrationServicesImap = new SecureIntegrationServicesImap();
        return secureIntegrationServicesImap;
    }

    protected RegistrationAPI getRegistrationAPI() {
        if (registrationAPI == null) registrationAPI = new RegistrationAPI();
        return registrationAPI;
    }

    protected DirectoryServices getDirectoryServices() {
        if (directoryServices == null) directoryServices = new DirectoryServices();
        return directoryServices;
    }


    public RegistrationService createRegistrationPort(String companyCode) {
        RegistrationService port = getRegistrationAPI().getWSHttpBindingRegistrationService();

        Client client = ClientProxy.getClient(port);
        fillRequestContext(companyCode, client);

        if (logger.isDebugEnabled()) {
            client.getInInterceptors().add(new LoggingInInterceptor());
            client.getOutInterceptors().add(new LoggingOutInterceptor());
        }

        return port;
    }

    public SecureIntegrationServiceImap createMailPort(String companyCode) {
        SecureIntegrationServiceImap port = getSecureIntegrationServiceImap().getWSHttpBindingSecureIntegrationServiceImap();

        Client client = ClientProxy.getClient(port);
        fillRequestContext(companyCode, client);

        if (logger.isDebugEnabled()) {
            client.getInInterceptors().add(new LoggingInInterceptor());
            client.getOutInterceptors().add(new LoggingOutInterceptor());
        }

        return port;
    }

    @Override
    public IDirectoryServices createDirectoryPort(String companyCode) {
        IDirectoryServices port = getDirectoryServices().getWSHttpBindingIDirectoryServices();

        Client client = ClientProxy.getClient(port);
        fillRequestContext(companyCode, client);

        if (logger.isDebugEnabled()) {
            client.getInInterceptors().add(new LoggingInInterceptor());
            client.getOutInterceptors().add(new LoggingOutInterceptor());
        }

        return port;
    }

    private void fillRequestContext(final String companyCode, Client client) {
        final DirectConfiguration config = directConfigurationService.find(companyCode);
        if (config == null) {
            throw new DirectNotConfiguredException("Secure Messaging functionality was not configured yet.");
        }

        try {
            Map<String, Object> requestContext = client.getRequestContext();
            requestContext.put("ws-security.callback-handler.sct", new CallbackHandler() {
                @Override
                public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                    WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
                    pc.setPassword(config.getPin());
                }
            });

            CryptoFactoryBean cryptoFactory = new CryptoFactoryBean();


            cryptoFactory.setCryptoProvider(org.apache.ws.security.components.crypto.Merlin.class);

            String keystoreLocation = directConfigurationService.getKeystoreRelativeLocation(companyCode);
            String keystoreType = "PKCS12";
            String keystorePassword = config.getPin();

            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(keystoreLocation);
            KeyStore store = KeyStore.getInstance(keystoreType);
            store.load(stream, keystorePassword.toCharArray());

            /* please don't use cryptoFactory.setKeyStoreLocation(resource) because it extracts absolute path from
             resource and we need relative path in cryptoFactory. Please refer to https://jira.scnsoft.com/browse/SCPAPP-987
             for more details */
            final Properties properties = new Properties();
            properties.setProperty(Merlin.KEYSTORE_FILE, keystoreLocation);
            cryptoFactory.setConfiguration(properties);

            cryptoFactory.setKeyStorePassword(keystorePassword);
            cryptoFactory.setKeyStoreType(keystoreType);

            Enumeration<String> aliases = store.aliases();
            cryptoFactory.setDefaultX509Alias(aliases.nextElement());

            cryptoFactory.afterPropertiesSet();

            Crypto crypto = cryptoFactory.getObject();
            requestContext.put("ws-security.encryption.crypto.sct", crypto);
            requestContext.put("ws-security.signature.crypto.sct", crypto);

        } catch (Exception e) {
            directConfigurationService.setConfigured(companyCode, false);

            logger.error("Exception while creating SES web service client: " + companyCode, e);
            throw new DirectMessagingException(e);
        }
        directConfigurationService.setConfigured(companyCode, true);
    }
}