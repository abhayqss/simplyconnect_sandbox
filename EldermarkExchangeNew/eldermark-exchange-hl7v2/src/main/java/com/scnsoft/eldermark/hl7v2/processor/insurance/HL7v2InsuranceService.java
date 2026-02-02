package com.scnsoft.eldermark.hl7v2.processor.insurance;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.entity.xds.segment.IN1InsuranceSegment;

public interface HL7v2InsuranceService {

    void updateInsurances(Client client, AdtMessage adtMessage);
}
