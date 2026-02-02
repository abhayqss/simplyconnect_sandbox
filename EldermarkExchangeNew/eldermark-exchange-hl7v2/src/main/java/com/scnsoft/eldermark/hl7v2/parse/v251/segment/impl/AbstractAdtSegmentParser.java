package com.scnsoft.eldermark.hl7v2.parse.v251.segment.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.AbstractSegment;
import com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.datatype.DataTypeConverter;
import com.scnsoft.eldermark.hl7v2.parse.segment.AdtSegmentParser;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractAdtSegmentParser<TO, FROM extends AbstractSegment> implements AdtSegmentParser<TO, FROM> {

    @Autowired
    protected DataTypeConverter dataTypeService;

    @Override
    public TO parse(FROM segment, MessageSource messageSource) throws HL7Exception, ApplicationException {
        if (isHl7SegmentEmpty(segment)) {
            return null;
        }
        return doParse(segment, messageSource);
    }

    protected abstract TO doParse(FROM segment, MessageSource messageSource) throws HL7Exception, ApplicationException;


    protected boolean isHl7SegmentEmpty(FROM hl7Segment) throws HL7Exception {
        return hl7Segment == null || hl7Segment.isEmpty();
    }
}
