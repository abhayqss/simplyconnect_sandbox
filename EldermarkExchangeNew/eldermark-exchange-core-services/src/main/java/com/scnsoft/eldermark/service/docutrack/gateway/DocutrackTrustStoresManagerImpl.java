package com.scnsoft.eldermark.service.docutrack.gateway;

import com.scnsoft.eldermark.util.KeyStoreUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.security.cert.X509Certificate;
import java.util.Optional;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class DocutrackTrustStoresManagerImpl implements DocutrackTrustStoresManager {
    private static final Logger logger = LoggerFactory.getLogger(DocutrackTrustStoresManagerImpl.class);

    private static final String TRUST_STORE_TEMPLATE = "SC_Comm_%d.jks";
    private static final String TRUST_STORE_TYPE = "JKS";
    private static final String CERTIFICATE_ALIAS = "docutrack-server-cert";

    @Value("${docutrack.truststores.location}")
    private String trustStoresLocation;

    @Value("${docutrack.truststores.password}")
    private String trustStoresPassword;

    @SuppressFBWarnings(value = "PATH_TRAVERSAL_IN", justification = "trustStoresLocation doesn't depend on user input")
    @EventListener(ApplicationReadyEvent.class)
    public void initCustomTrustStoresDir() {
        new File(trustStoresLocation).mkdirs();
    }

    @Override
    public byte[] getTrustStoreBytes(Long communityId) {
        var inputStream = getTrustStoreInputStream(getTrustStoreFile(communityId));
        if (inputStream == null) {
            return null;
        }
        try (inputStream) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            logger.error("Error during fetching docutrack truststore bytes for community [{}]", communityId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<X509Certificate> getCertificate(Long communityId) {
        return Optional.of(getTrustStoreFile(communityId))
                .map(this::getTrustStoreInputStream)
                .map(is -> {
                    try (is) {
                        return KeyStoreUtil.getOrCreateKeyStore(is, getTrustStoresPassword(), getTrustStoresType());
                    } catch (IOException e) {
                        logger.error("Failed to get docutrack certificate for community [{}]", communityId);
                        throw new RuntimeException(e);
                    }
                })
                .map(keystore -> KeyStoreUtil.getCertificate(keystore, CERTIFICATE_ALIAS))
                .map(x -> (X509Certificate) x);
    }

    @Override
    public String getTrustStoresType() {
        return TRUST_STORE_TYPE;
    }

    @Override
    public String getTrustStoresPassword() {
        return trustStoresPassword;
    }

    @Override
    public void updateServerCertificate(Long communityId, X509Certificate cert) {
        if (cert == null) {
            deleteKeyStore(communityId);
        } else {
            assignCertificate(communityId, cert);
        }
    }

    private void deleteKeyStore(Long communityId) {
        var file = getTrustStoreFile(communityId);
        if (file.exists()) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                logger.warn("Failed to delete docutrack certificate truststore", e);
            }
        }
    }

    private void assignCertificate(Long communityId, X509Certificate cert) {
        var trustStoreFile = getTrustStoreFile(communityId);
        var inputStream = getTrustStoreInputStream(trustStoreFile);
        try (inputStream) {
            var keyStore = KeyStoreUtil.getOrCreateKeyStore(inputStream, trustStoresPassword, TRUST_STORE_TYPE);
            KeyStoreUtil.clear(keyStore);
            KeyStoreUtil.addCertificate(keyStore, CERTIFICATE_ALIAS, cert);
            KeyStoreUtil.save(keyStore, trustStoreFile, trustStoresPassword);
        } catch (IOException e) {
            logger.error("Error during assigning docutrack certificate to community [{}]", communityId, e);
            throw new RuntimeException(e);
        }
    }

    @SuppressFBWarnings(value = "PATH_TRAVERSAL_IN", justification = "communityId doesn't depend on user input")
    private File getTrustStoreFile(Long communityId) {
        return new File(trustStoresLocation, String.format(TRUST_STORE_TEMPLATE, communityId));
    }

    private InputStream getTrustStoreInputStream(File trustStoreFile) {
        try {
            return trustStoreFile.exists() ? new FileInputStream(trustStoreFile) : null;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
