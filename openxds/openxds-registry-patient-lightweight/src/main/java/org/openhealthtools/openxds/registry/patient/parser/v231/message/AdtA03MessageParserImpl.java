package org.openhealthtools.openxds.registry.patient.parser.v231.message;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.v231.message.ADT_A03;
import org.openhealthtools.openxds.entity.message.ADTA03;
import org.openhealthtools.openxds.entity.segment.AdtDG1DiagnosisSegment;
import org.openhealthtools.openxds.entity.segment.PR1ProceduresSegment;
import org.openhealthtools.openxds.registry.patient.parser.message.AdtMessageParser;
import org.openhealthtools.openxds.registry.patient.parser.util.BiIntFunction;
import org.openhealthtools.openxds.registry.patient.parser.util.SegmentParserUtil;
import org.openhealthtools.openxds.registry.patient.parser.v231.segment.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public ADTA03 parse(final ADT_A03 message) throws HL7Exception, ApplicationException {
        final ADTA03 simplyConnectMessage = new ADTA03();
        simplyConnectMessage.setMsh(mshSegmentParser.parse(message.getMSH()));
        simplyConnectMessage.setEvn(evnSegmentParser.parse(message.getEVN()));
        simplyConnectMessage.setPid(pidSegmentParser.parse(message.getPID()));

        simplyConnectMessage.setPv1(pv1SegmentParser.parse(message.getPV1()));
        simplyConnectMessage.setPr1List(parsePR1List(message));

        simplyConnectMessage.setDg1List(parseDiagnosisList(message));
        simplyConnectMessage.setPd1(getPd1AdditionalDemographicSegmentParser().parse(message.getPD1()));
        return simplyConnectMessage;
    }

    //TODO ggavrysh move to common class
    private List<AdtDG1DiagnosisSegment> parseDiagnosisList(final ADT_A03 message) throws HL7Exception, ApplicationException {
        return SegmentParserUtil.parseList(message.getDG1Reps(), message, new BiIntFunction<ADT_A03, AdtDG1DiagnosisSegment>() {
            @Override
            public AdtDG1DiagnosisSegment apply(final ADT_A03 msg, final int i) throws HL7Exception, ApplicationException {
                return dg1DiagnosisSegmentParser.parse(msg.getDG1(i));
            }
        });
    }

    //TODO ggavrysh move to common class
    private List<PR1ProceduresSegment> parsePR1List(final ADT_A03 message) throws HL7Exception, ApplicationException {
        return SegmentParserUtil.parseList(message.getPR1ROLReps(), message, new BiIntFunction<ADT_A03, PR1ProceduresSegment>() {
            @Override
            public PR1ProceduresSegment apply(final ADT_A03 msg, final int i) throws HL7Exception, ApplicationException {
                return getPr1SegmentParser().parse(msg.getPR1ROL(i).getPR1());
            }
        });
    }

    @Override
    public Class<ADT_A03> getMessageClass() {
        return ADT_A03.class;
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

    public Dg1DiagnosisSegmentParser getDg1DiagnosisSegmentParser() {
        return dg1DiagnosisSegmentParser;
    }

    public void setDg1DiagnosisSegmentParser(final Dg1DiagnosisSegmentParser dg1DiagnosisSegmentParser) {
        this.dg1DiagnosisSegmentParser = dg1DiagnosisSegmentParser;
    }

    public Pd1AdditionalDemographicSegmentParser getPd1AdditionalDemographicSegmentParser() {
        return pd1AdditionalDemographicSegmentParser;
    }

    public void setPd1AdditionalDemographicSegmentParser(final Pd1AdditionalDemographicSegmentParser pd1AdditionalDemographicSegmentParser) {
        this.pd1AdditionalDemographicSegmentParser = pd1AdditionalDemographicSegmentParser;
    }
}
