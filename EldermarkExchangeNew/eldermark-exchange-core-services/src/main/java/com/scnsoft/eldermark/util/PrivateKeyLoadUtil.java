package com.scnsoft.eldermark.util;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class PrivateKeyLoadUtil {
    private static final Logger logger = LoggerFactory.getLogger(PrivateKeyLoadUtil.class);

    public static PrivateKey loadKeyFromFile(String file) throws IOException {
        try (var keyStream = new FileInputStream(file)) {
           return loadKey(keyStream);
        }
    }

    public static PrivateKey loadKey(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }
        var key = inputStream.readAllBytes();
        var keyString = new String(key);

        if (isPemFormat(keyString)) {
            return readPemPrivateKey(keyString);
        }
        try {
            return readRsaPkcs1Der(key);
        } catch (Exception e) {
            logger.info("Failed to read key as PKCS#1 RSA DER");
        }
        try {
            return readRsaPkcs8Der(key);
        } catch (Exception e) {
            logger.info("Failed to read key as PKCS#8 RSA DER");
        }
        return null;
    }


    private static PrivateKey readRsaPkcs8Der(byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private static PrivateKey readRsaPkcs1Der(byte[] keyBytes) throws IOException {
        ASN1Sequence seq = ASN1Sequence.getInstance(keyBytes);
        RSAPrivateKey bcPrivateKey = RSAPrivateKey.getInstance(seq);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        AlgorithmIdentifier algId = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
        return converter.getPrivateKey(new PrivateKeyInfo(algId, bcPrivateKey));
    }

    private static boolean isPemFormat(String keyString) {
        return keyString.startsWith("-----BEGIN PRIVATE KEY-----") ||
                keyString.startsWith("-----BEGIN RSA PRIVATE KEY-----") ||
                keyString.startsWith("-----BEGIN EC PRIVATE KEY-----");
    }

    private static PrivateKey readPemPrivateKey(String keyString) throws IOException {
        PEMParser pemParser = new PEMParser(new StringReader(keyString));
        Object bouncyCastleResult = pemParser.readObject();

        PrivateKeyInfo info = null;
        if (bouncyCastleResult instanceof PrivateKeyInfo) {
            info = (PrivateKeyInfo) bouncyCastleResult;
        } else if (bouncyCastleResult instanceof PEMKeyPair) {
            PEMKeyPair keys = (PEMKeyPair) bouncyCastleResult;
            info = keys.getPrivateKeyInfo();
        } else {
            throw new RuntimeException("No private key found in the provided file");
        }

        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        return converter.getPrivateKey(info);
    }
}
