package com.scnsoft.eldermark.hl7v2.processor.allergy;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;

public interface HL7v2AllergyService {
    void updateAllergies(Client client, AdtMessage adtMessage);
}
