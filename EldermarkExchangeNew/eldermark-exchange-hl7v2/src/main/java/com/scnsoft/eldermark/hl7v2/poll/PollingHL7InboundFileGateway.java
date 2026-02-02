package com.scnsoft.eldermark.hl7v2.poll;

import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import com.scnsoft.eldermark.service.inbound.InboundFileGateway;

import java.io.File;

public interface PollingHL7InboundFileGateway<P extends ProcessingSummary> extends InboundFileGateway<File, P> {

    void fillMessageSource(MessageSource messageSource);
}
