package com.scnsoft.eldermark.service.transformer;

import com.scnsoft.eldermark.web.entity.DateDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DateDtoTransformer implements Converter<Date, DateDto> {

    private String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private SimpleDateFormat simpleDateFormat;

    @PostConstruct
    private void construct() {
        simpleDateFormat = new SimpleDateFormat(pattern);
    }

    @Override
    public DateDto convert(Date date) {
        if (date == null) {
            return null;
        }
        final DateDto dateDto = new DateDto();
        dateDto.setDateTime(date.getTime());
        dateDto.setDateTimeStr(simpleDateFormat.format(date));
        return dateDto;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
