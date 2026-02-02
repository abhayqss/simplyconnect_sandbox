package com.scnsoft.eldermark.services.connect;


import javax.net.ssl.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

/**
 * Created by pzhurba on 29-Jan-16.
 */
public class SSLSocketFactoryGenerator {

    private KeyStore keyStore = null;
    private KeyStore trustStore = null;

    private String keyStorePassword;

    public SSLSocketFactoryGenerator(KeyStore keyStore, KeyStore trustStore, String keyStorePassword) {
        this.keyStore = keyStore;
        this.trustStore = trustStore;
        this.keyStorePassword = keyStorePassword;

    }

    public SSLSocketFactory getSSLSocketFactory() throws IOException, GeneralSecurityException {
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(getKeyManagers(), getTrustManagers(), null);
        return sc.getSocketFactory();
    }

    private KeyManager[] getKeyManagers()
            throws IOException, GeneralSecurityException {

        //Init a key store with the given file.
        final String alg = KeyManagerFactory.getDefaultAlgorithm();
        final KeyManagerFactory kmFact = KeyManagerFactory.getInstance(alg);

        //Init the key manager factory with the loaded key store
        kmFact.init(keyStore, keyStorePassword.toCharArray());

        return kmFact.getKeyManagers();
    }


    protected TrustManager[] getTrustManagers() throws IOException, GeneralSecurityException {

        final String alg = TrustManagerFactory.getDefaultAlgorithm();
        final TrustManagerFactory tmFact = TrustManagerFactory.getInstance(alg);

        //Init the key manager factory with the loaded key store
        tmFact.init(trustStore);

        return tmFact.getTrustManagers();
    }
}