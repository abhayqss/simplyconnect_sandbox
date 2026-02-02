package com.scnsoft.eldermark.hl7v2.parse.v251.message;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.v251.message.ADT_A60;
import ca.uhn.hl7v2.model.v251.segment.AL1;
import com.scnsoft.eldermark.entity.xds.message.ADTA60;
import com.scnsoft.eldermark.hl7v2.HapiUtils;
import com.scnsoft.eldermark.hl7v2.parse.message.AdtMessageParser;
import com.scnsoft.eldermark.hl7v2.parse.v251.segment.*;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdtA60MessageParserImpl implements AdtMessageParser<ADTA60, ADT_A60> {

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
    public ADTA60 parse(ADT_A60 message, MessageSource messageSource) throws HL7Exception, ApplicationException {

        final ADTA60 simplyConnectMessage = new ADTA60();
        simplyConnectMessage.setMsh(mshSegmentParser.parse(message.getMSH(), messageSource));
        simplyConnectMessage.setEvn(evnSegmentParser.parse(message.getEVN(), messageSource));
        simplyConnectMessage.setPid(pidSegmentParser.parse(message.getPID(), messageSource));

        simplyConnectMessage.setPv1(pv1SegmentParser.parse(message.getPV1(), messageSource));

        Structure[] al1 = null;
        try {
            al1 = message.getAll("AL1");
        } catch (HL7Exception e) {
            //No al1 entries
        }
        simplyConnectMessage.setAl1List(HapiUtils.convertSegmentList(al1, AL1.class, al1AllergySegmentParser, messageSource));

        return simplyConnectMessage;
    }

    @Override
    public String getMessageType() {
        return "A60";
    }
}
