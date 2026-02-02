package com.scnsoft.eldermark.service.storage;

import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class ApolloOruLogFileStorage extends BaseFileStorageWithEncryption {

    @Autowired
    private DocumentEncryptionService documentEncryptionService;

    private static final DateTimeFormatter dateTimeFormatter =
        DateTimeFormatter.ofPattern("MMddyyyy_HHmmss").withZone(ZoneId.of("UTC"));

    public ApolloOruLogFileStorage(@Value("${apollo.oru.log}") String storageLocation) {
        super(storageLocation);
    }

    @Override
    protected String generateNewFileName(String originalFileName) {
        return CareCoordinationUtils.concat("_", dateTimeFormatter.format(Instant.now()), originalFileName) + ".hl7";
    }

    @Override
    protected DocumentEncryptionService getDocumentEncryptionService() {
        return documentEncryptionService;
    }
}
