package com.scnsoft.eldermark.service.storage;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;

@Service
public class ApolloOruLogTestingFileStorage extends ApolloOruLogFileStorage {

    @SuppressFBWarnings(
        value = "PATH_TRAVERSAL_IN",
        justification = "storageLocation is configured on Spring context initialization"
    )
    public ApolloOruLogTestingFileStorage(@Value("${apollo.oru.log}") String storageLocation) {
        super(Paths.get(storageLocation, "testing").toString());
    }
}
