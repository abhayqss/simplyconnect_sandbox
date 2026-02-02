package com.scnsoft.eldermark.service.document.cda.generator;

import com.scnsoft.eldermark.entity.document.DocumentReport;

public interface CcdGeneratorService {
    DocumentReport metadata();

    DocumentReport generate(Long clientId, boolean aggregated);
}
