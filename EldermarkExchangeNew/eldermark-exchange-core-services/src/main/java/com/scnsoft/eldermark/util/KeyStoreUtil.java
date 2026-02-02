package com.scnsoft.eldermark.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;

public class KeyStoreUtil {

    public static KeyStore createKeyStore(String type, String password) throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
        KeyStore keystore = KeyStore.getInstance(type);
        keystore.load(null, password.toCharArray());

        return keystore;
    }

    public static KeyStore getOrCreateKeyStore(InputStream inputStream, String password, String type) {
        try {
            KeyStore keystore = KeyStore.getInstance(type);
            keystore.load(inputStream, password.toCharArray());

            return keystore;
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public static void clear(KeyStore keyStore) {
        try {
            var aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                keyStore.deleteEntry(alias);
            }
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addCertificate(KeyStore keyStore, String alias, Certificate cert) {
        try {
            keyStore.setCertificateEntry(alias, cert);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public static Certificate getCertificate(KeyStore keyStore, String alias) {
        try {
            return keyStore.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public static X509Certificate loadX509Certificate(byte[] certBytes) {
        return loadX509Certificate(new ByteArrayInputStream(certBytes));
    }

    public static X509Certificate loadX509Certificate(InputStream certInputStream) {
        try {
            var cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate) cf.generateCertificate(certInputStream);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressFBWarnings(
        value = "WEAK_MESSAGE_DIGEST_SHA1",
        justification = "sha1 is used widely for certificate thumbprint"
    )
    public static byte[] sha1Fingerprint(Certificate certificate) {
        try {
            return DigestUtils.sha1(certificate.getEncoded());
        } catch (CertificateEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressFBWarnings(
        value = "WEAK_MESSAGE_DIGEST_SHA1",
        justification = "sha1 is used widely for certificate thumbprint"
    )
    public static String sha1HexFingerprint(Certificate certificate) {
        try {
            return DigestUtils.sha1Hex(certificate.getEncoded());
        } catch (CertificateEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save(KeyStore keyStore, File file, String password) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            keyStore.store(fos, password.toCharArray());
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException(e);
        }
    }
}
