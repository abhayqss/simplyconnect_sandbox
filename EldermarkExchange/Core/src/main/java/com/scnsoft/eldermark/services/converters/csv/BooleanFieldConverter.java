package com.scnsoft.eldermark.services.converters.csv;

import com.opencsv.bean.AbstractBeanField;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class BooleanFieldConverter extends AbstractBeanField {

    @Override
    protected Object convert(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if ("1".equals(value)) {
            return true;
        }
        if ("0".equals(value)) {
            return false;
        }
        return BooleanUtils.toBoolean(value);
    }
}
