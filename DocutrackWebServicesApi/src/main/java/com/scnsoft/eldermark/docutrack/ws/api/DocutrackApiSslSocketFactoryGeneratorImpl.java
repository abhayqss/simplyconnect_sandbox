package com.scnsoft.eldermark.docutrack.ws.api;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;


public class DocutrackApiSslSocketFactoryGeneratorImpl implements DocutrackApiSslSocketFactoryGenerator {

    public static final String DEFAULT_TLS_VERSION = "TLSv1.2";

    private final String tlsVersion;

    public DocutrackApiSslSocketFactoryGeneratorImpl() {
        this(DEFAULT_TLS_VERSION);
    }

    public DocutrackApiSslSocketFactoryGeneratorImpl(String tlsVersion) {
        this.tlsVersion = tlsVersion;
    }

    @Override
    public SSLSocketFactory getSSLSocketFactory(InputStream trustStore, String trustStorePass, String trustStoreType) {
        try {
            SSLContext sc = SSLContext.getInstance(tlsVersion);
            sc.init(null, getTrustManagers(trustStore, trustStorePass, trustStoreType), null);
            return sc.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("SSL keystores initialization fails", e);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load keystores", e);
        }
    }

    private TrustManager[] getTrustManagers(InputStream trustStore, String trustStorePass, String trustStoreType)
            throws IOException, GeneralSecurityException {
        KeyStore ts = KeyStore.getInstance(trustStoreType);
        ts.load(trustStore, trustStorePass.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        tmf.init(ts);
        return tmf.getTrustManagers();
    }
}
