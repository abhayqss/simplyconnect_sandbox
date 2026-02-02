package com.scnsoft.eldermark.hl7v2.parse.v251.message;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.v251.group.ADT_A01_INSURANCE;
import ca.uhn.hl7v2.model.v251.group.ADT_A01_PROCEDURE;
import ca.uhn.hl7v2.model.v251.group.ADT_A05_INSURANCE;
import ca.uhn.hl7v2.model.v251.group.ADT_A05_PROCEDURE;
import ca.uhn.hl7v2.model.v251.message.ADT_A01;
import ca.uhn.hl7v2.model.v251.message.ADT_A05;
import com.scnsoft.eldermark.entity.xds.message.ADTA01;
import com.scnsoft.eldermark.entity.xds.message.ADTA05;
import com.scnsoft.eldermark.hl7v2.HapiUtils;
import com.scnsoft.eldermark.hl7v2.parse.message.AdtMessageParser;
import com.scnsoft.eldermark.hl7v2.parse.v251.segment.*;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdtA05MessageParserImpl implements AdtMessageParser<ADTA05, ADT_A05> {

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
    private In1SegmentParser in1SegmentParser;

    @Autowired
    private Dg1DiagnosisSegmentParser dg1DiagnosisSegmentParser;

    @Autowired
    private Gt1GuarantorSegmentParser gt1GuarantorSegmentParser;

    @Autowired
    private Pd1AdditionalDemographicSegmentParser pd1AdditionalDemographicSegmentParser;

    @Autowired
    private Al1AllergySegmentParser al1AllergySegmentParser;

    @Override
    public ADTA05 parse(ADT_A05 message, MessageSource messageSource) throws HL7Exception, ApplicationException {
        final ADTA05 simplyConnectMessage = new ADTA05();
        simplyConnectMessage.setMsh(mshSegmentParser.parse(message.getMSH(), messageSource));
        simplyConnectMessage.setEvn(evnSegmentParser.parse(message.getEVN(), messageSource));
        simplyConnectMessage.setPid(pidSegmentParser.parse(message.getPID(), messageSource));

        simplyConnectMessage.setPv1(pv1SegmentParser.parse(message.getPV1(), messageSource));
        simplyConnectMessage.setPr1List(HapiUtils.convertSegmentListFromGroup(
                message.getPROCEDUREAll(),
                ADT_A05_PROCEDURE::getPR1,
                pr1SegmentParser,
                messageSource)
        );
        simplyConnectMessage.setIn1List(HapiUtils.convertSegmentListFromGroup(
                message.getINSURANCEAll(),
                ADT_A05_INSURANCE::getIN1,
                in1SegmentParser,
                messageSource)
        );

        simplyConnectMessage.setDg1List(HapiUtils.convertSegmentList(message.getDG1All(), dg1DiagnosisSegmentParser, messageSource));
        simplyConnectMessage.setGt1List(HapiUtils.convertSegmentList(message.getGT1All(), gt1GuarantorSegmentParser, messageSource));
        simplyConnectMessage.setPd1(pd1AdditionalDemographicSegmentParser.parse(message.getPD1(), messageSource));
        simplyConnectMessage.setAl1List(HapiUtils.convertSegmentList(message.getAL1All(), al1AllergySegmentParser, messageSource));
        return simplyConnectMessage;
    }

    @Override
    public String getMessageType() {
        return "A05";
    }
}
