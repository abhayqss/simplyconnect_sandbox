package com.scnsoft.eldermark.service.document.signature.template.field.value;

import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.entity.signature.TemplateFieldDefaultValueType;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

@Component
public class CurrentMonthDefaultValueBuilder implements FieldDefaultValueBuilder {

    @Override
    public TemplateFieldDefaultValueType getTemplateFieldType() {
        return TemplateFieldDefaultValueType.CURRENT_MONTH;
    }

    @Override
    public Object build(DocumentSignatureTemplateContext context) {
        var zoneOffset = DateTimeUtils.generateZoneOffset(context.getTimezoneOffset());
        return LocalDate.now(zoneOffset).getMonth().getDisplayName(TextStyle.FULL, Locale.US);
    }
}
