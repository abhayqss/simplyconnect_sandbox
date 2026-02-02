package com.scnsoft.exchange.adt.ssl;

import javax.net.ssl.*;
import java.io.FileInputStream;
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

    public HostnameVerifier getLocalhostResolvedHostnameVerifier() {
        return new HostnameVerifier() {
            public boolean verify(String hostname,
                                  SSLSession sslSession) {
                return true;
/*
                if (hostname.equals("localhost")) {
                    return true;
                }


                sslSocketFactory.setHostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };

                return false;
*/
            }
        };
    }

    public SSLSocketFactory getSSLSocketFactory() {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(keyStorePath), keyStorePassword.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, keyStorePassword.toCharArray());

            KeyStore ts = KeyStore.getInstance("JKS");
            ts.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(trustStorePath), trustStorePassword.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            tmf.init(ts);

            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            SSLSocketFactory sslSocketFactory = sc.getSocketFactory();

            return sslSocketFactory;
        } catch (GeneralSecurityException e) {
            throw new SslCertificatesException("SSL keystores initialization fails", e);
        } catch (IOException e) {
            throw new SslCertificatesException("Unable to load keystores", e);
        }

    }

}
