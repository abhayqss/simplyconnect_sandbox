package com.scnsoft.eldermark.hl7v2.parse.v251.message;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.v251.group.ADT_A03_PROCEDURE;
import ca.uhn.hl7v2.model.v251.message.ADT_A03;
import com.scnsoft.eldermark.entity.xds.message.ADTA03;
import com.scnsoft.eldermark.hl7v2.HapiUtils;
import com.scnsoft.eldermark.hl7v2.parse.message.AdtMessageParser;
import com.scnsoft.eldermark.hl7v2.parse.v251.segment.*;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdtA03MessageParserImpl implements AdtMessageParser<ADTA03, ADT_A03> {

    @Autowired
    private MshSegmentParser mshSegmentParser;

    @Autowired
    private EvnSegmentParser evnSegmentParser;

    @Autowired
    private PidSegmentParser pidSegmentParser;

    @Autowired
    private Pv1SegmentParser pv1SegmentParser;

    @Autowired
    private Pr1SegmentParser pr1SegmentParser;

    @Autowired
    private Dg1DiagnosisSegmentParser dg1DiagnosisSegmentParser;

    @Autowired
    private Pd1AdditionalDemographicSegmentParser pd1AdditionalDemographicSegmentParser;

    @Override
    public ADTA03 parse(final ADT_A03 message, MessageSource messageSource) throws HL7Exception, ApplicationException {
        final ADTA03 simplyConnectMessage = new ADTA03();
        simplyConnectMessage.setMsh(mshSegmentParser.parse(message.getMSH(), messageSource));
        simplyConnectMessage.setEvn(evnSegmentParser.parse(message.getEVN(), messageSource));
        simplyConnectMessage.setPid(pidSegmentParser.parse(message.getPID(), messageSource));

        simplyConnectMessage.setPv1(pv1SegmentParser.parse(message.getPV1(), messageSource));
        simplyConnectMessage.setPr1List(HapiUtils.convertSegmentListFromGroup(
                message.getPROCEDUREAll(),
                ADT_A03_PROCEDURE::getPR1,
                pr1SegmentParser,
                messageSource)
        );

        simplyConnectMessage.setDg1List(HapiUtils.convertSegmentList(message.getDG1All(), dg1DiagnosisSegmentParser, messageSource));
        simplyConnectMessage.setPd1(pd1AdditionalDemographicSegmentParser.parse(message.getPD1(), messageSource));
        return simplyConnectMessage;
    }

    @Override
    public String getMessageType() {
        return "A03";
    }
}
