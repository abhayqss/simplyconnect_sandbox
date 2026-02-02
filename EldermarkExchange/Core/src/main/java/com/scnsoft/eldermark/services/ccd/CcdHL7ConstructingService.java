package com.scnsoft.eldermark.services.ccd;

import com.scnsoft.eldermark.services.cda.ClinicalDocumentVO;
import org.openhealthtools.mdht.uml.cda.ccd.ContinuityOfCareDocument;

public interface CcdHL7ConstructingService {
    ContinuityOfCareDocument construct(ClinicalDocumentVO document);
}
