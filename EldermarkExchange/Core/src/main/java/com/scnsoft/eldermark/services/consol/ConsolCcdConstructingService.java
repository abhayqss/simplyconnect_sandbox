package com.scnsoft.eldermark.services.consol;

import com.scnsoft.eldermark.services.cda.ClinicalDocumentVO;
import org.openhealthtools.mdht.uml.cda.consol.ContinuityOfCareDocument;

public interface ConsolCcdConstructingService {
    ContinuityOfCareDocument construct(ClinicalDocumentVO document);
}
