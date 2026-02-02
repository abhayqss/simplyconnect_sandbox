package com.scnsoft.eldermark.consana.sync.common.config.auth.oauth2;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class DefaultAndCustormTrustManager implements X509TrustManager {

    final X509TrustManager defaultTm;
    final X509TrustManager customTm;


    public DefaultAndCustormTrustManager(InputStream customTrustStore,
                                         String password) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        defaultTm = initTm(null);

        KeyStore myTrustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        myTrustStore.load(customTrustStore, password.toCharArray());
        customTm = initTm(myTrustStore);
    }

    private X509TrustManager initTm(KeyStore myTrustStore) throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(myTrustStore);

        X509TrustManager defaultTm = null;
        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                return (X509TrustManager) tm;
            }
        }
        return null;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        // If you're planning to use client-cert auth,
        // merge results from "defaultTm" and "myTm".
        return defaultTm.getAcceptedIssuers();
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain,
                                   String authType) throws CertificateException {
        try {
            DefaultAndCustormTrustManager.this.customTm.checkServerTrusted(chain, authType);
        } catch (CertificateException e) {
            // This will throw another CertificateException if this fails too.
            defaultTm.checkServerTrusted(chain, authType);
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain,
                                   String authType) throws CertificateException {
        // If you're planning to use client-cert auth,
        // do the same as checking the server.
        defaultTm.checkClientTrusted(chain, authType);
    }
}
