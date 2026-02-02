package gov.hhs.fha.nhinc.callback.openSAML;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.logging.Logger;

public class NhinCertificateManager { // implements CertificateManager {
    private static final Logger LOG = Logger.getLogger(NhinCertificateManager.class.getSimpleName());

    private String keystoreLocation;
    private String privateKeyAlias;
    private String keystorePassword;
    private String truststoreLocation;
    private String truststorePassword;

    private KeyStore keyStore = null;
    private KeyStore trustStore = null;


    public NhinCertificateManager(String keystoreLocation, String keystorePassword, String keystoreType, String privateKeyAlias,
                                  String truststoreLocation, String truststorePassword, String truststoreType) {
        this.keystoreLocation = keystoreLocation;
        this.keystorePassword = keystorePassword;
        this.privateKeyAlias = privateKeyAlias;

        try {
            keyStore = initStore(keystoreLocation, keystorePassword, keystoreType);
            trustStore = initStore(truststoreLocation, truststorePassword, truststoreType);
        } catch (Exception e) {
            LOG.severe("unable to initialize keystores");
            e.printStackTrace();
        }
    }

    //@Override
    public KeyStore getKeyStore() {
        return keyStore;
    }

    //@Override
    public KeyStore getTrustStore() {
        return trustStore;
    }

    private KeyStore initStore(String storeLoc, String password, String storeType) throws Exception {
        KeyStore keyStore = null;
        InputStream is = null;

        if (storeType == null) {
            LOG.severe("javax.net.ssl.keyStoreType is not defined");
            LOG.warning("Default to JKS keyStoreType");
            storeType = "JKS";
        }
        if (password == null || storeLoc == null) {
            LOG.severe("Store password or store location not defined");
            LOG.severe("Please define javax.net.ssl.keyStorePassword and javax.net.ssl.keyStore");
        }

        if ("JKS".equals(storeType) && storeLoc == null) {
            LOG.severe("javax.net.ssl.keyStore is not defined");
        } else {
            try {
                keyStore = KeyStore.getInstance(storeType);
                is = new FileInputStream(storeLoc);
                keyStore.load(is, password.toCharArray());
            } catch (NoSuchAlgorithmException ex) {
                LOG.severe("Error initializing KeyStore: " + ex);
                throw new Exception(ex.getMessage());
            } catch (CertificateException ex) {
                LOG.severe("Error initializing KeyStore: " + ex);
                throw new Exception(ex.getMessage());
            } catch (KeyStoreException ex) {
                LOG.severe("Error initializing KeyStore: " + ex);
                throw new Exception(ex.getMessage());
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException ex) {
                    LOG.fine("KeyStoreCallbackHandler " + ex);
                }
            }
        }
        return keyStore;
    }

    //@Override
    public X509Certificate getDefaultCertificate() throws Exception {
        return (X509Certificate) getPrivateKeyEntry().getCertificate();
    }

    //@Override
    public PrivateKey getDefaultPrivateKey() throws Exception {
        return getPrivateKeyEntry().getPrivateKey();
    }

    private KeyStore.PrivateKeyEntry getPrivateKeyEntry() throws Exception {
        KeyStore.PrivateKeyEntry pkEntry;

        try {
            pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(privateKeyAlias,
                    new KeyStore.PasswordProtection(keystorePassword.toCharArray()));

        } catch (NoSuchAlgorithmException ex) {
            LOG.severe("Error initializing Private Key: " + ex);
            throw new Exception(ex.getMessage());
        } catch (KeyStoreException ex) {
            LOG.severe("Error initializing Private Key: " + ex);
            throw new Exception(ex.getMessage());
        } catch (UnrecoverableEntryException ex) {
            LOG.severe("Error initializing Private Key: " + ex);
            throw new Exception(ex.getMessage());
        }

        return pkEntry;
    }

    // @Override
    public RSAPublicKey getDefaultPublicKey() {
        try {
            return (RSAPublicKey) getDefaultCertificate().getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getKeystoreLocation() {
        return keystoreLocation;
    }

    public String getPrivateKeyAlias() {
        return privateKeyAlias;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getTruststoreLocation() {
        return truststoreLocation;
    }

    public String getTruststorePassword() {
        return truststorePassword;
    }
}
