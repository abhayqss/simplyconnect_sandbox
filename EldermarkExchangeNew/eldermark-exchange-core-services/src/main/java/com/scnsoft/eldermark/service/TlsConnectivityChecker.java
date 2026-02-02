package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dto.TlsConnectivityCheckResult;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public interface TlsConnectivityChecker {

    /**
     * Check that TLS connection is succussful using default truststore
     *
     * @param url   URL to check against
     * @return
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     */
    TlsConnectivityCheckResult checkTls(String url) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException;

    /**
     * Check that SSL connection is successful if serverCertificate is trusted.
     * @param url
     * @param serverCertificate
     * @return
     */
    TlsConnectivityCheckResult checkTls(String url, Certificate serverCertificate) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException;
}
