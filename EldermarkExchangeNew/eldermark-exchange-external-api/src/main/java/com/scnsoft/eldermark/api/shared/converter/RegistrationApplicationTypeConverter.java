package com.scnsoft.eldermark.api.shared.converter;


import com.scnsoft.eldermark.api.external.entity.RegistrationApplication;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @author phomal
 * Created on 10/25/17.
 */
@Converter
public class RegistrationApplicationTypeConverter implements AttributeConverter<RegistrationApplication.Type, String> {

    @Override
    public String convertToDatabaseColumn(RegistrationApplication.Type attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getValueDb();
    }

    @Override
    public RegistrationApplication.Type convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return RegistrationApplication.Type.fromValue(dbData);
    }
}
