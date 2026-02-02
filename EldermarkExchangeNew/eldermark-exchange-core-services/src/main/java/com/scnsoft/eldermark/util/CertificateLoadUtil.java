package com.scnsoft.eldermark.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Collections;

public class CertificateLoadUtil {

    public static Collection<? extends Certificate> loadCertificatesFromFile(String file) throws IOException, CertificateException {
        try (var certStream = new FileInputStream(file)) {
            return loadCertificates(certStream);
        }
    }

    public static Collection<? extends Certificate> loadCertificates(InputStream inputStream) throws IOException, CertificateException {
        if (inputStream == null) {
            return Collections.emptyList();
        }
        final byte[] publicData = inputStream.readAllBytes();

        final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        final Collection<? extends Certificate> chain = certificateFactory.generateCertificates(
                new ByteArrayInputStream(publicData));

        return chain;
    }
}
