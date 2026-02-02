package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.entity.signature.ScSourceTemplateFieldType;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
class DateSubmittedScFieldBuilder extends AbstractDateSubmittedScFieldBuilder {

    public DateSubmittedScFieldBuilder() {
        super(ScSourceTemplateFieldType.DATE);
    }

    @Override
    protected String extractValue(Instant date, Integer timezoneOffset) {
        return DateTimeUtils.formatDate(date, timezoneOffset);
    }
}
