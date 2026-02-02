package com.scnsoft.eldermark.services.transformer.adt.dto;

import com.scnsoft.eldermark.entity.xds.hl7table.HL7UserDefinedCodeTable;
import com.scnsoft.eldermark.entity.xds.segment.EVNEventTypeSegment;
import com.scnsoft.eldermark.entity.xds.datatype.ISCodedValueForUserDefinedTables;
import com.scnsoft.eldermark.shared.carecoordination.adt.EVNEventTypeSegmentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EVNSegmentTransformer implements Converter<EVNEventTypeSegment, EVNEventTypeSegmentDto> {

    @Autowired
    private Converter<ISCodedValueForUserDefinedTables<? extends HL7UserDefinedCodeTable>, String> isCodedValueForUserDefinedTablesStringConverter;

    @Override
    public EVNEventTypeSegmentDto convert(EVNEventTypeSegment evnEventTypeSegment) {
        if (evnEventTypeSegment == null) {
            return null;
        }
        EVNEventTypeSegmentDto target = new EVNEventTypeSegmentDto();
        target.setEventTypeCode(evnEventTypeSegment.getEventTypeCode());
        target.setRecordedDateTime(evnEventTypeSegment.getRecordedDatetime());
        target.setEventReasonCode(isCodedValueForUserDefinedTablesStringConverter.convert(evnEventTypeSegment.getEventReasonCode()));
        target.setEventOccured(evnEventTypeSegment.getEventOccurred());
        return target;
    }

}
