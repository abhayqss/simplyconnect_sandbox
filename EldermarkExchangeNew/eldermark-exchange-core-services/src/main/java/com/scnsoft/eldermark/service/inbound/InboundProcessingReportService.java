package com.scnsoft.eldermark.service.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;

public interface InboundProcessingReportService {

    String createLocalReport(ProcessingSummary processingSummary) throws JsonProcessingException;

    String createRemoteReport(ProcessingSummary processingSummary) throws JsonProcessingException;

}
