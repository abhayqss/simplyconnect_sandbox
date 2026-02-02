package com.scnsoft.eldermark.service.inbound;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InboundProcessingReportServiceImpl implements InboundProcessingReportService {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(InboundProcessingReportServiceImpl.class);

    static {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    public String createLocalReport(ProcessingSummary processingSummary) throws JsonProcessingException {
        return createReport(processingSummary, ProcessingSummary.LocalView.class);
    }

    @Override
    public String createRemoteReport(ProcessingSummary processingSummary) throws JsonProcessingException {
        return createReport(processingSummary, ProcessingSummary.RemoteView.class);
    }

    private String createReport(ProcessingSummary processingSummary, Class<?> viewClass) throws JsonProcessingException {
        final String result = objectMapper.writerWithView(viewClass).writeValueAsString(processingSummary);
        logger.debug(result);
        return result;
    }
}
