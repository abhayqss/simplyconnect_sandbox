package com.scnsoft.eldermark.service.xds.ssl;

import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public class SslConnectionFactory {

    private String keyStorePath;
    private String keyStorePassword;
    private String trustStorePath;
    private String trustStorePassword;

    public SslConnectionFactory(String keyStorePath, String keyStorePassword, String trustStorePath, String trustStorePassword) {
        this.keyStorePath = keyStorePath;
        this.keyStorePassword = keyStorePassword;
        this.trustStorePath = trustStorePath;
        this.trustStorePassword = trustStorePassword;
    }

    /**
     * Default implementation's keystore, truststore and secure random will be used. Protocol is set to TLS v1.2
     */
    public SslConnectionFactory() {
    }

    public HostnameVerifier getLocalhostResolvedHostnameVerifier() {
        return (hostname, sslSession) -> {
            if (hostname.equals("localhost")) {
                return true;
            }
            return false;
        };
    }

    public SSLSocketFactory getSSLSocketFactory() {
        try {
            KeyManager[] keyManagers = null;
            if (StringUtils.isNotEmpty(keyStorePath)) {
                KeyStore ks = KeyStore.getInstance("JKS");
                ks.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(keyStorePath), keyStorePassword.toCharArray());
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(ks, keyStorePassword.toCharArray());
                keyManagers = kmf.getKeyManagers();
            }

            TrustManager[] trustManagers = null;
            if (StringUtils.isNotEmpty(trustStorePath)) {
                KeyStore ts = KeyStore.getInstance("JKS");
                ts.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(trustStorePath), trustStorePassword.toCharArray());
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                tmf.init(ts);
                trustManagers = tmf.getTrustManagers();
            }

            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(keyManagers, trustManagers, null);
            SSLSocketFactory sslSocketFactory = sc.getSocketFactory();
            return sslSocketFactory;
        } catch (GeneralSecurityException e) {
            throw new SslCertificatesException("SSL keystores initialization fails", e);
        } catch (IOException e) {
            throw new SslCertificatesException("Unable to load keystores", e);
        }
    }
}
