package com.scnsoft.eldermark.hl7v2.processor.problem;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.ccd.Problem;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.entity.xds.segment.AdtDG1DiagnosisSegment;

import java.util.Optional;

interface HL7v2ProblemFactory {

    Optional<Problem> createProblem(Client client, AdtDG1DiagnosisSegment dg1, AdtMessage adtMessage);

}
