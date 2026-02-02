package com.scnsoft.eldermark.hl7v2.poll;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.hl7v2.HapiUtils;
import com.scnsoft.eldermark.hl7v2.facade.MessageAndLogProcessingResult;
import com.scnsoft.eldermark.hl7v2.source.HL7v2IntegrationPartner;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;

public class MessagePollingUtils {

    public static MessageSource createMessageSource(String rawMessage,
                                                    Message message,
                                                    HL7v2IntegrationPartner integrationPartner,
                                                    PollingHL7InboundFileGateway<?> pollingHL7InboundFileGateway) throws HL7Exception {
        var messageSource = new MessageSource();
        messageSource.setHl7v2IntegrationPartner(integrationPartner);
        messageSource.setRawMessage(rawMessage);
        pollingHL7InboundFileGateway.fillMessageSource(messageSource);
        var msh = HapiUtils.getMSH(message);
        messageSource.setMessageControlId(msh.getMessageControlID().getValueOrEmpty());
        messageSource.setSendingApplication(msh.getMsh3_SendingApplication());
        messageSource.setSendingFacility(msh.getMsh4_SendingFacility());
        messageSource.setReceivingApplication(msh.getMsh5_ReceivingApplication());
        messageSource.setReceivingFacility(msh.getMsh6_ReceivingFacility());

        return messageSource;
    }

    public static void fillSummary(HL7ProcessingSummary summary, MessageAndLogProcessingResult response) throws HL7Exception {
        summary.setResponseMessage(response.getResponseMessage());
        summary.setResponseMessageRaw(response.getResponseMessage().encode());
        summary.setHl7MessageLogId(response.getHl7LogId());

        summary.setStatus(response.isSuccess() ?
                ProcessingSummary.ProcessingStatus.OK :
                ProcessingSummary.ProcessingStatus.WARN);
    }
}
