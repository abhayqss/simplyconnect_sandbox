package com.scnsoft.eldermark.service.hl7;

import com.scnsoft.eldermark.entity.lab.LabResearchOrderORU;

import java.util.Optional;

public interface ApolloOruProcessor {

    Optional<LabResearchOrderORU> process(String oruRaw, String fileName);

    Optional<LabResearchOrderORU> processTesting(String oruRaw);
}
