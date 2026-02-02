package com.scnsoft.eldermark.service.document.signature.template.field.builder;

import com.scnsoft.eldermark.entity.signature.ScSourceTemplateFieldType;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Component
class DateDdSubmittedScFieldBuilder extends AbstractDateSubmittedScFieldBuilder {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd");

    public DateDdSubmittedScFieldBuilder() {
        super(ScSourceTemplateFieldType.DATE_DD);
    }

    @Override
    protected String extractValue(Instant date, Integer timezoneOffset) {
        return DateTimeUtils.formatDate(date, timezoneOffset, DATE_TIME_FORMATTER);
    }
}
