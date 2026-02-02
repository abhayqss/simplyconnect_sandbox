package com.scnsoft.eldermark.hl7v2;


import com.scnsoft.eldermark.hl7v2.processor.MessageProcessingResult;

public interface CcnRestClient {

    void postAdt(MessageProcessingResult messageProcessingResult, Long hl7MessageLogId);

}
