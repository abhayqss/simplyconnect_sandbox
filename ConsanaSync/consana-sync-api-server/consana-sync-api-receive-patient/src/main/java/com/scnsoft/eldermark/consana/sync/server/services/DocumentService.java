package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;

import java.util.Set;

public interface DocumentService {

    Set<String> getAllDocumentConsanaIds(Resident resident);
}
