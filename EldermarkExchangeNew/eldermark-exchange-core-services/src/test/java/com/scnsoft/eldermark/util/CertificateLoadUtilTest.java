package com.scnsoft.eldermark.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static org.assertj.core.api.Assertions.assertThat;

class CertificateLoadUtilTest {

    @Test
    void loadCertificates_domainCert() throws CertificateException, IOException {
        var certs = CertificateLoadUtil.loadCertificatesFromFile("src/test/resources/key/simply_connect_dev.cer");

        assertThat(certs).hasSize(1);

        var cert = certs.iterator().next();
        assertThat(cert).isInstanceOf(X509Certificate.class);
    }

    @Test
    void loadCertificates_chainCerts() throws CertificateException, IOException {
        var certs = CertificateLoadUtil.loadCertificatesFromFile("src/test/resources/key/gd_bundle-g2-g1.crt");

        assertThat(certs).hasSize(3);

        var cert = certs.iterator().next();
        assertThat(cert).isInstanceOf(X509Certificate.class);
    }
}