package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CurrentDayDefaultValueBuilder implements FieldDefaultValueBuilder {

    @Override
    public TemplateFieldDefaultValueType getTemplateFieldType() {
        return TemplateFieldDefaultValueType.CURRENT_DAY;
    }

    @Override
    public Object build(DocumentSignatureTemplateContext context) {
        var zoneOffset = DateTimeUtils.generateZoneOffset(context.getTimezoneOffset());
        return String.valueOf(LocalDate.now(zoneOffset).getDayOfMonth());
    }
}
