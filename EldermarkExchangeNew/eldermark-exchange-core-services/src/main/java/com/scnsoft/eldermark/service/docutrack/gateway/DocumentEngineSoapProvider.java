package com.scnsoft.eldermark.service.docutrack.gateway;

import com.scnsoft.eldermark.docutrack.ws.api.DocumentEngineSoap;

public interface DocumentEngineSoapProvider {

    DocumentEngineSoap get(DocutrackApiClient docutrackApiClient);
}
