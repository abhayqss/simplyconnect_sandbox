package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CurrentDateDefaultValueBuilder implements FieldDefaultValueBuilder {

    @Override
    public TemplateFieldDefaultValueType getTemplateFieldType() {
        return TemplateFieldDefaultValueType.CURRENT_DATE;
    }

    @Override
    public Object build(DocumentSignatureTemplateContext context) {
        var zoneOffset = DateTimeUtils.generateZoneOffset(context.getTimezoneOffset());
        return LocalDate.now(zoneOffset).atStartOfDay(zoneOffset).toInstant().toEpochMilli();
    }
}
