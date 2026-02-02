package com.scnsoft.eldermark.converter.entity2dto.organization;


import com.scnsoft.eldermark.dto.CertificateInfoDto;
import com.scnsoft.eldermark.util.KeyStoreUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Optional;

@Component
public class CertificateInfoDtoConverter implements Converter<X509Certificate, CertificateInfoDto> {

    @Override
    public CertificateInfoDto convert(X509Certificate x509Certificate) {
        var result = new CertificateInfoDto();

        result.setIssuer(Optional.ofNullable(x509Certificate.getIssuerDN())
                .map(Principal::getName)
                .orElse(null)
        );

        result.setSubject(Optional.ofNullable(x509Certificate.getSubjectDN())
                .map(Principal::getName)
                .orElse(null)
        );

        result.setIssuedAt(x509Certificate.getNotBefore().getTime());
        result.setExpiresAt(x509Certificate.getNotAfter().getTime());

        result.setSha1Fingerprint(KeyStoreUtil.sha1HexFingerprint(x509Certificate));

        return result;
    }
}
