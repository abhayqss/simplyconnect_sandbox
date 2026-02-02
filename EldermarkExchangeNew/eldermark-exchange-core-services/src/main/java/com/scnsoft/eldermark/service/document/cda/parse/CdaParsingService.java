package com.scnsoft.eldermark.service.document.cda.parse;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.service.document.cda.schema.ClinicalDocumentVO;

public interface CdaParsingService<D> {

    ClinicalDocumentVO parse(D document, Client resident);

    Client parsePatientOnly(D document, Community organization);
}
