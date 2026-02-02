package com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.segment;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.datatype.DataTypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

public abstract class HL7SegmentConverter<S extends AbstractSegment, T> implements Converter<S, T> {

    private static final Logger logger = LoggerFactory.getLogger(HL7SegmentConverter.class);

    @Autowired
    protected DataTypeConverter dataTypeService;

    public T convert(S source) {
        try {
            return isSegmentEmpty(source) ? null : doConvert(source);
        } catch (HL7Exception e) {
            logger.warn("Error during HL7 segment conversion ", e);
            return null;
        }
    }

    protected boolean isSegmentEmpty(S source) throws HL7Exception {
        return source == null || source.isEmpty();

    }

    protected abstract T doConvert(S source) throws HL7Exception;

}
