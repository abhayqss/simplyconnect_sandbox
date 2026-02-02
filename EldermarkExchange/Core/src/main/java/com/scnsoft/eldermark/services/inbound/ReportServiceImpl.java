package com.scnsoft.eldermark.services.inbound;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.scnsoft.eldermark.entity.inbound.summary.ProcessingSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

@Service
@Conditional(InboundFilesServiceRunCondition.class)
public class ReportServiceImpl implements ReportService {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    static {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    }

    @Override
    public String createLocalReport(ProcessingSummary processingSummary) throws JsonProcessingException {
        return createReport(processingSummary, ProcessingSummary.LocalView.class);
    }

    @Override
    public String createRemoteReport(ProcessingSummary processingSummary) throws JsonProcessingException {
        return createReport(processingSummary,  ProcessingSummary.RemoteView.class);
    }

    private String createReport(ProcessingSummary processingSummary, Class<?> viewClass) throws JsonProcessingException {
        final String result = objectMapper.writerWithView(viewClass).writeValueAsString(processingSummary);
        logger.debug(result);
        return result;
    }
}