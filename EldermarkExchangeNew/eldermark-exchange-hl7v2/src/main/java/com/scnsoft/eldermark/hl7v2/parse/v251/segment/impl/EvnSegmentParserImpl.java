package com.scnsoft.eldermark.hl7v2.parse.v251.segment.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.v251.segment.EVN;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0062EventReason;
import com.scnsoft.eldermark.entity.xds.segment.EVNEventTypeSegment;
import com.scnsoft.eldermark.hl7v2.parse.v251.segment.EvnSegmentParser;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class EvnSegmentParserImpl extends AbstractAdtSegmentParser<EVNEventTypeSegment, EVN> implements EvnSegmentParser {

    @Override
    public EVNEventTypeSegment doParse(final EVN segment, MessageSource messageSource) throws HL7Exception, ApplicationException {
        final EVNEventTypeSegment evn = new EVNEventTypeSegment();
        evn.setEventTypeCode(segment.getEvn1_EventTypeCode().getValue());
        evn.setRecordedDatetime(dataTypeService.convertHL7Date(segment.getEvn2_RecordedDateTime().getTs1_Time().getValue()));
        evn.setEventReasonCode(dataTypeService.createIS(segment.getEvn4_EventReasonCode(), HL7CodeTable0062EventReason.class));
        evn.setEventOccurred(dataTypeService.convertHL7Date(segment.getEvn6_EventOccurred().getTs1_Time().getValue()));
        evn.setEventFacility(dataTypeService.createHd(segment.getEvn7_EventFacility()));
        return evn;
    }
}
