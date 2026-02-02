package com.scnsoft.eldermark.hl7v2;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.protocol.MetadataKeys;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import com.scnsoft.eldermark.hl7v2.facade.HL7v2MessageFacade;
import com.scnsoft.eldermark.hl7v2.source.HL7v2IntegrationPartner;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import com.scnsoft.eldermark.hl7v2.source.MessageSourceChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Stream;

@Service
public class HapiMessagesReceiverApplication implements ReceivingApplication<Message> {

    @Autowired
    private HL7v2MessageFacade hl7v2MessageFacade;

    public boolean canProcess(Message theIn) {
        return true;
    }

    public Message processMessage(Message theMessage, Map<String, Object> theMetadata) throws ReceivingApplicationException, HL7Exception {
        var messageSource = new MessageSource();
        messageSource.setRawMessage((String) theMetadata.get(MetadataKeys.IN_RAW_MESSAGE));
        messageSource.setSourceAddress((String) theMetadata.get(MetadataKeys.IN_SENDING_IP));
        messageSource.setSourcePort((int) theMetadata.get(MetadataKeys.IN_SENDING_PORT));
        messageSource.setMessageControlId((String) theMetadata.get(MetadataKeys.IN_MESSAGE_CONTROL_ID));

        var msh = HapiUtils.getMSH(theMessage);
        messageSource.setSendingApplication(msh.getMsh3_SendingApplication());
        messageSource.setSendingFacility(msh.getMsh4_SendingFacility());
        messageSource.setReceivingApplication(msh.getMsh5_ReceivingApplication());
        messageSource.setReceivingFacility(msh.getMsh6_ReceivingFacility());
        messageSource.setChannel(MessageSourceChannel.TCP);
        messageSource.setHl7v2IntegrationPartner(resolveIntegrationPartner(messageSource));

        return hl7v2MessageFacade.processMessage(theMessage, messageSource).getResponseMessage();
    }

    private HL7v2IntegrationPartner resolveIntegrationPartner(MessageSource messageSource) {
        return Stream.of(HL7v2IntegrationPartner.values())
                .filter(t -> t.matchesTCPSource(messageSource))
                .findFirst()
                .orElse(null);
    }
}
