package org.openhealthtools.openxds.registry.patient.parser.facade;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openhealthtools.openxds.entity.message.AdtMessage;
import org.openhealthtools.openxds.registry.api.PatientExtended;
import org.openhealthtools.openxds.registry.patient.parser.message.AdtMessageParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdtMessageParserFacadeImpl implements AdtMessageParserFacade {
    private static Log log = LogFactory.getLog(AdtMessageParserFacadeImpl.class);

    private Map<Class<? extends Message>, AdtMessageParser<? extends AdtMessage, ? extends Message>> parserMap;

    @Autowired
    public AdtMessageParserFacadeImpl(List<AdtMessageParser<? extends AdtMessage, ? extends Message>> parsers) {
        this.parserMap = new HashMap<Class<? extends Message>, AdtMessageParser<? extends AdtMessage, ? extends Message>>(parsers.size());
        for (AdtMessageParser<? extends AdtMessage, ? extends Message> parser : parsers) {
            parserMap.put(parser.getMessageClass(), parser);
        }
    }

    @Override
    public AdtMessage parse(final Message msgIn) throws HL7Exception, ApplicationException {
        final AdtMessageParser parser = parserMap.get(msgIn.getClass());
        if (parser != null) {
            return parser.parse(msgIn);
        }
        log.warn("No parser for message class " + msgIn.getClass());
        return null;
    }
}
