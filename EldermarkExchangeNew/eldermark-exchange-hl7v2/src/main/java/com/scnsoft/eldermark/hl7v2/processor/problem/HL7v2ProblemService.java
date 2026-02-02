package com.scnsoft.eldermark.hl7v2.processor.problem;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;

public interface HL7v2ProblemService {

    void updateProblems(Client client, AdtMessage adtMessage);

}
