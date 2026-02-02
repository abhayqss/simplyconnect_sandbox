package com.scnsoft.eldermark.util;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class MutualTLSUtils {

    public static SSLContext clientAuthSslContext(String protocol, PrivateKey clientPrateKey, Certificate[] clientCertificateChain) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, KeyManagementException {

        final char[] pwdChars = "fdfsadfwefasd14Has%^f".toCharArray(); // just some  random string
        KeyStore clientKeyStore = KeyStore.getInstance("jks");
        clientKeyStore.load(null, null);
        clientKeyStore.setKeyEntry("simplyConnect", clientPrateKey, pwdChars, clientCertificateChain);


        KeyManagerFactory keyMgrFactory = KeyManagerFactory.getInstance("SunX509");
        keyMgrFactory.init(clientKeyStore, pwdChars);

        var sslContext = SSLContext.getInstance(protocol);
        sslContext.init(keyMgrFactory.getKeyManagers(), null, null);
        return sslContext;

    }
}
