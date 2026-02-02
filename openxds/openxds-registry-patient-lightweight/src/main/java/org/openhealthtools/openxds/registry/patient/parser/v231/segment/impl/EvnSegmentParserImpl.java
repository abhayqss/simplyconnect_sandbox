package org.openhealthtools.openxds.registry.patient.parser.v231.segment.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.v231.segment.EVN;
import org.openhealthtools.openxds.entity.hl7table.HL7CodeTable0062EventReason;
import org.openhealthtools.openxds.entity.segment.EVNEventTypeSegment;
import org.openhealthtools.openxds.registry.patient.parser.datatype.DataTypeService;
import org.openhealthtools.openxds.registry.patient.parser.datatype.EmptyHL7Field231Service;
import org.openhealthtools.openxds.registry.patient.parser.v231.segment.EvnSegmentParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EvnSegmentParserImpl extends AbstractAdtSegmentParser<EVNEventTypeSegment, EVN> implements EvnSegmentParser {

    @Autowired
    private DataTypeService dataTypeService;

    @Autowired
    private EmptyHL7Field231Service emptyHL7Field231Service;

    @Override
    public EVNEventTypeSegment doParse(final EVN segment) throws HL7Exception, ApplicationException {
        final EVNEventTypeSegment evn = new EVNEventTypeSegment();
        evn.setEventTypeCode(segment.getEventTypeCode().getValue());
        evn.setRecordedDatetime(getDataTypeService().convertHL7Date(segment.getRecordedDateTime().getTimeOfAnEvent().getValue()));
        evn.setEventReasonCode(getDataTypeService().createIS(segment.getEventReasonCode(), HL7CodeTable0062EventReason.class));
        evn.setEventOccurred(getDataTypeService().convertHL7Date(segment.getEventOccurred().getTimeOfAnEvent().getValue()));
        return evn;
    }

    @Override
    public boolean isHl7SegmentEmpty(final EVN evn) {
        return evn == null || getEmptyHL7Field231Service().isTSEmpty(evn.getRecordedDateTime());
    }

    public DataTypeService getDataTypeService() {
        return dataTypeService;
    }

    public void setDataTypeService(final DataTypeService dataTypeService) {
        this.dataTypeService = dataTypeService;
    }

    public EmptyHL7Field231Service getEmptyHL7Field231Service() {
        return emptyHL7Field231Service;
    }

    public void setEmptyHL7Field231Service(final EmptyHL7Field231Service emptyHL7Field231Service) {
        this.emptyHL7Field231Service = emptyHL7Field231Service;
    }
}
