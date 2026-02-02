package org.openhealthtools.openxds.registry.patient.parser.v231.message;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.v231.message.ADT_A01;
import org.openhealthtools.openxds.entity.message.ADTA01;
import org.openhealthtools.openxds.entity.segment.*;
import org.openhealthtools.openxds.registry.patient.parser.message.AdtMessageParser;
import org.openhealthtools.openxds.registry.patient.parser.util.BiIntFunction;
import org.openhealthtools.openxds.registry.patient.parser.util.SegmentParserUtil;
import org.openhealthtools.openxds.registry.patient.parser.v231.segment.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdtA01MessageParserImpl implements AdtMessageParser<ADTA01, ADT_A01> {

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
    public ADTA01 parse(ADT_A01 message) throws HL7Exception, ApplicationException {
        final ADTA01 simplyConnectMessage = new ADTA01();
        simplyConnectMessage.setMsh(mshSegmentParser.parse(message.getMSH()));
        simplyConnectMessage.setEvn(evnSegmentParser.parse(message.getEVN()));
        simplyConnectMessage.setPid(pidSegmentParser.parse(message.getPID()));

        simplyConnectMessage.setPv1(pv1SegmentParser.parse(message.getPV1()));
        message.getPR1ROL().getPR1();
        simplyConnectMessage.setPr1List(parsePR1List(message));
        simplyConnectMessage.setIn1List(parseIN1List(message));

        simplyConnectMessage.setDg1List(parseDiagnosisList(message));
        simplyConnectMessage.setGt1List(parseGuarantorList(message));
        simplyConnectMessage.setPd1(getPd1AdditionalDemographicSegmentParser().parse(message.getPD1()));
        simplyConnectMessage.setAl1List(parseAllergyList(message));
        return simplyConnectMessage;
    }

    private List<AdtAL1AllergySegment> parseAllergyList(final ADT_A01 message) throws HL7Exception, ApplicationException {
        return SegmentParserUtil.parseList(message.getAL1Reps(), message, new BiIntFunction<ADT_A01, AdtAL1AllergySegment>() {
            @Override
            public AdtAL1AllergySegment apply(final ADT_A01 msg, final int i) throws HL7Exception, ApplicationException {
                return getAl1AllergySegmentParser().parse(msg.getAL1(i));
            }
        });
    }

    //TODO ggavrysh move to common class
    private List<AdtDG1DiagnosisSegment> parseDiagnosisList(final ADT_A01 message) throws HL7Exception, ApplicationException {
        return SegmentParserUtil.parseList(message.getDG1Reps(), message, new BiIntFunction<ADT_A01, AdtDG1DiagnosisSegment>() {
            @Override
            public AdtDG1DiagnosisSegment apply(final ADT_A01 msg, final int i) throws HL7Exception, ApplicationException {
                return getDg1DiagnosisSegmentParser().parse(msg.getDG1(i));
            }
        });
    }

    //TODO ggavrysh move to common class
    private List<AdtGT1GuarantorSegment> parseGuarantorList(final ADT_A01 message) throws HL7Exception, ApplicationException {
        return SegmentParserUtil.parseList(message.getDG1Reps(), message, new BiIntFunction<ADT_A01, AdtGT1GuarantorSegment>() {
            @Override
            public AdtGT1GuarantorSegment apply(final ADT_A01 msg, final int i) throws HL7Exception, ApplicationException {
                return getGt1GuarantorSegmentParser().parse(msg.getGT1(i));
            }
        });
    }

    //TODO ggavrysh move to common class
    private List<PR1ProceduresSegment> parsePR1List(final ADT_A01 message) throws HL7Exception, ApplicationException {
        return SegmentParserUtil.parseList(message.getPR1ROLReps(), message, new BiIntFunction<ADT_A01, PR1ProceduresSegment>() {
            @Override
            public PR1ProceduresSegment apply(final ADT_A01 msg, final int i) throws HL7Exception, ApplicationException {
                return getPr1SegmentParser().parse(msg.getPR1ROL(i).getPR1());
            }
        });
    }

    //TODO ggavrysh move to common class
    private List<IN1InsuranceSegment> parseIN1List(final ADT_A01 message) throws HL7Exception, ApplicationException {
        return SegmentParserUtil.parseList(message.getIN1IN2IN3Reps(), message, new BiIntFunction<ADT_A01, IN1InsuranceSegment>() {
            @Override
            public IN1InsuranceSegment apply(final ADT_A01 msg, final int i) throws HL7Exception, ApplicationException {
                return getIn1SegmentParser().parse(msg.getIN1IN2IN3(i).getIN1());
            }
        });
    }

    @Override
    public Class<ADT_A01> getMessageClass() {
        return ADT_A01.class;
    }

    public Dg1DiagnosisSegmentParser getDg1DiagnosisSegmentParser() {
        return dg1DiagnosisSegmentParser;
    }

    public void setDg1DiagnosisSegmentParser(final Dg1DiagnosisSegmentParser dg1DiagnosisSegmentParser) {
        this.dg1DiagnosisSegmentParser = dg1DiagnosisSegmentParser;
    }

    public Gt1GuarantorSegmentParser getGt1GuarantorSegmentParser() {
        return gt1GuarantorSegmentParser;
    }

    public void setGt1GuarantorSegmentParser(final Gt1GuarantorSegmentParser gt1GuarantorSegmentParser) {
        this.gt1GuarantorSegmentParser = gt1GuarantorSegmentParser;
    }

    public MshSegmentParser getMshSegmentParser() {
        return mshSegmentParser;
    }

    public void setMshSegmentParser(final MshSegmentParser mshSegmentParser) {
        this.mshSegmentParser = mshSegmentParser;
    }

    public EvnSegmentParser getEvnSegmentParser() {
        return evnSegmentParser;
    }

    public void setEvnSegmentParser(final EvnSegmentParser evnSegmentParser) {
        this.evnSegmentParser = evnSegmentParser;
    }

    public PidSegmentParser getPidSegmentParser() {
        return pidSegmentParser;
    }

    public void setPidSegmentParser(final PidSegmentParser pidSegmentParser) {
        this.pidSegmentParser = pidSegmentParser;
    }

    public Pv1SegmentParser getPv1SegmentParser() {
        return pv1SegmentParser;
    }

    public void setPv1SegmentParser(final Pv1SegmentParser pv1SegmentParser) {
        this.pv1SegmentParser = pv1SegmentParser;
    }

    public Pr1SegmentParser getPr1SegmentParser() {
        return pr1SegmentParser;
    }

    public void setPr1SegmentParser(final Pr1SegmentParser pr1SegmentParser) {
        this.pr1SegmentParser = pr1SegmentParser;
    }

    public In1SegmentParser getIn1SegmentParser() {
        return in1SegmentParser;
    }

    public void setIn1SegmentParser(final In1SegmentParser in1SegmentParser) {
        this.in1SegmentParser = in1SegmentParser;
    }

    public Pd1AdditionalDemographicSegmentParser getPd1AdditionalDemographicSegmentParser() {
        return pd1AdditionalDemographicSegmentParser;
    }

    public void setPd1AdditionalDemographicSegmentParser(final Pd1AdditionalDemographicSegmentParser pd1AdditionalDemographicSegmentParser) {
        this.pd1AdditionalDemographicSegmentParser = pd1AdditionalDemographicSegmentParser;
    }

    public Al1AllergySegmentParser getAl1AllergySegmentParser() {
        return al1AllergySegmentParser;
    }

    public void setAl1AllergySegmentParser(final Al1AllergySegmentParser al1AllergySegmentParser) {
        this.al1AllergySegmentParser = al1AllergySegmentParser;
    }
}
