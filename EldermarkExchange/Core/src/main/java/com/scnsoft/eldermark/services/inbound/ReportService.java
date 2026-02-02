package com.scnsoft.eldermark.services.inbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.scnsoft.eldermark.entity.inbound.summary.ProcessingSummary;

public interface ReportService {

    String createLocalReport(ProcessingSummary processingSummary) throws JsonProcessingException;

    String createRemoteReport(ProcessingSummary processingSummary) throws JsonProcessingException;

}
