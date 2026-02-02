package com.scnsoft.eldermark.h2.healthPartners;

import com.scnsoft.eldermark.service.inbound.healthpartners.fileprocessors.BaseHpFileProcessor;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class HealthPartnersITSupport {

    public static <T> void copy(T source, T destination) {
        try {
            BeanUtils.copyProperties(destination, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static String numberToString(Number number) {
        if (number == null) {
            return "";
        }
        return String.valueOf(number);
    }

    public static String dateToString(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static Instant atStartOfDayCentralTime(LocalDate localDate) {
        return Optional.ofNullable(localDate)
                .map(sd -> sd.atStartOfDay(BaseHpFileProcessor.CT_ZONE).toInstant())
                .orElse(null);

    }

}
