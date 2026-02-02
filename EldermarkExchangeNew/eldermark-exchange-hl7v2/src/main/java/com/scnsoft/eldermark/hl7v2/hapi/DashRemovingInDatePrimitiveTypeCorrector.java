package com.scnsoft.eldermark.hl7v2.hapi;

import org.apache.commons.lang3.StringUtils;

public class DashRemovingInDatePrimitiveTypeCorrector extends PrimitiveTypeCorrector {

    @Override
    protected String doCorrect(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }

        var nanosSeparatorIdx = value.indexOf('.');
        if (nanosSeparatorIdx == -1) {
            return value.replace("-", "");
        }
        return value.substring(0, nanosSeparatorIdx).replace("-", "") + value.substring(nanosSeparatorIdx);
    }
}
