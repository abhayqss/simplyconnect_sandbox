package com.scnsoft.eldermark.hl7v2.parse.facade;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.hl7v2.HapiUtils;
import com.scnsoft.eldermark.hl7v2.parse.message.AdtMessageParser;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdtMessageParserFacadeImpl implements AdtMessageParserFacade {
    private static final Logger logger = LoggerFactory.getLogger(AdtMessageParserFacadeImpl.class);

    private final Map<String, AdtMessageParser<? extends AdtMessage, ? extends Message>> parserMap;

    @Autowired
    public AdtMessageParserFacadeImpl(List<AdtMessageParser<? extends AdtMessage, ? extends Message>> parsers) {
        this.parserMap = new HashMap<>(parsers.size());
        for (var parser : parsers) {
            parserMap.put(parser.getMessageType(), parser);
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Optional<AdtMessage> parse(final Message msgIn, MessageSource messageSource) throws HL7Exception, ApplicationException {
        var messageEventType = HapiUtils.getMSH(msgIn).getMsh9_MessageType().getMsg2_TriggerEvent().getValue();
        final AdtMessageParser parser = parserMap.get(messageEventType);
        if (parser != null) {
            return Optional.ofNullable(parser.parse(msgIn, messageSource));
        }
        logger.warn("No parser for message class {}", msgIn.getClass());
        return Optional.empty();
    }
}
