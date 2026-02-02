package com.scnsoft.eldermark.entity.phr.converter;

import com.scnsoft.eldermark.entity.phr.SectionUpdateRequest;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @author phomal
 * Created on 5/24/17.
 */
@Converter
public class SectionUpdateRequestTypeConverter implements AttributeConverter<SectionUpdateRequest.Type, Character> {

    @Override
    public Character convertToDatabaseColumn(SectionUpdateRequest.Type attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getValueDb();
    }

    @Override
    public SectionUpdateRequest.Type convertToEntityAttribute(Character dbData) {
        if (dbData == null) {
            return null;
        }

        return SectionUpdateRequest.Type.fromValue(dbData);
    }
}
