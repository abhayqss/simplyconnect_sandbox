package com.scnsoft.eldermark.util;

import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalQuery;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {

    public static final ZoneId CST_TIMEZONE = ZoneId.of("America/Chicago");

    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MILLIS_IN_MINUTE = 60 * 1000;

    private static final String DATE_FORMAT_PATTERN = "MM/dd/yyyy";
    private static final String DATE_TIME_WITH_ZONE_FORMAT_PATTERN = "MM/dd/yyyy hh:mm a (z)";
    private static final String DATE_TIME_FORMAT_PATTERN = "MM/dd/yyyy, hh:mm a";
    private static final String TIME_FORMAT_PATTERN = "hh:mm a";
    private static final String TIME_WITH_ZONE_FORMAT_PATTERN = "hh:mm a (z)";

    private static DateTimeFormatter dateFormatterStartDay = new DateTimeFormatterBuilder().appendPattern(DATE_FORMAT_PATTERN)
            .parseDefaulting(ChronoField.SECOND_OF_DAY, 0).toFormatter().withZone(ZoneId.of("UTC"));

    private static DateTimeFormatter dateFormatterEndDay = new DateTimeFormatterBuilder().appendPattern(DATE_FORMAT_PATTERN)
            .parseDefaulting(ChronoField.SECOND_OF_DAY, (24 * 60 * 60) - 1).toFormatter().withZone(ZoneId.of("UTC"));

    private static DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
    private static DateTimeFormatter dateTimeWithZoneFormatter = DateTimeFormatter.ofPattern(DATE_TIME_WITH_ZONE_FORMAT_PATTERN);
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTERN);
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT_PATTERN);
    private static DateTimeFormatter timeWithZoneFormatter = DateTimeFormatter.ofPattern(TIME_WITH_ZONE_FORMAT_PATTERN);

    //datetime2(7) is used to represent dates in db
    private static final int DATABASE_TIME_NANOS_PRECISION = 7;

    private static final int MAX_TIME_NANOS_PRECISION = (int) (Math.log10(LocalTime.MAX.getNano()) + 1);

    private DateTimeUtils() {
    }

    @Deprecated
    public static Long fixUTCshift(Long epochMillis, Integer timezoneOffset) {
        if (epochMillis == null) {
            return null;
        }
        if (timezoneOffset == null) {
            return epochMillis;
        }
        return epochMillis + (-1) * timezoneOffset * MILLIS_IN_MINUTE;
    }

    public static TimeZone generateTimeZone(Integer timeZoneOffset) {
        if (timeZoneOffset == null) {
            timeZoneOffset = 0;
        }
        try {
            return TimeZone.getTimeZone(TimeZone.getAvailableIDs((int) TimeUnit.MINUTES.toMillis(timeZoneOffset))[0]);
        } catch (Exception e) {
            return TimeZone.getTimeZone(TimeZone.getAvailableIDs((int) TimeUnit.MINUTES.toMillis(0))[0]);
        }

    }

    public static Instant toInstant(Long epochMilli) {
        if (epochMilli == null) {
            return null;
        }
        return Instant.ofEpochMilli(epochMilli);
    }

    public static Long toEpochMilli(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.toEpochMilli();
    }

    public static Long toEpochMilli(Date date) {
        if (date == null) {
            return null;
        }
        return date.getTime();
    }

    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date toDate(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Date.from(instant);
    }

    public static LocalDate toUTCLocalDate(Instant instant) {
        return LocalDate.ofInstant(instant, ZoneId.of("UTC"));
    }

    public static LocalDate toLocalDate(Instant instant, Integer timeZoneOffset) {
        return LocalDate.ofInstant(instant, generateZoneOffset(timeZoneOffset));
    }

    public static LocalDate toLocalDate(Date date) {
        return date != null ? new java.sql.Date(date.getTime()).toLocalDate() : null;
    }

    @Deprecated
    public static LocalDateTime toLocalDateTimeAtZone(LocalDateTime dateTime, Integer timeZoneOffset) {
        return LocalDateTime.ofInstant(dateTime.toInstant(ZoneOffset.ofTotalSeconds(timeZoneOffset * SECONDS_IN_MINUTE)), ZoneId.of("UTC"));
    }

    public static LocalDateTime toLocalDateTime(Instant instant, Integer timeZoneOffset) {
        return LocalDateTime.ofInstant(instant, generateZoneOffset(timeZoneOffset));
    }

    public static ZoneOffset generateZoneOffset(Integer timeZoneOffset) {
        if (timeZoneOffset != null) {
            return ZoneOffset.ofTotalSeconds((-1) * timeZoneOffset * SECONDS_IN_MINUTE);
        }
        return ZoneOffset.UTC;
    }

    public static Instant atStartOfMonth(Instant instant, ZoneId zoneId) {
        return LocalDate.ofInstant(instant, zoneId)
                .withDayOfMonth(1)
                .atStartOfDay(zoneId)
                .toInstant();
    }

    public static Instant atDatabaseEndOfDay(Instant instant, ZoneId zoneId) {
        //if datetime with higher nanos precision than in DB saved in DB - nanos are rounded.
        //For example, date with LocalTime.MAX = 23:59:59.999999999
        //will be saved to datetime2(7) column as 00:00:00.000000000 of next day.
        //In order to avoid such a result, here we truncate 999_999_999 nanos to 999_999_900
        var time = getMaxTimeWithNanosPrecision(DATABASE_TIME_NANOS_PRECISION);
        return atTime(instant, time, zoneId);
    }

    public static Instant atTrueEndOfDay(Instant instant, ZoneId zoneId) {
        return LocalDate.ofInstant(instant, zoneId).atTime(LocalTime.MAX).atZone(zoneId).toInstant();
    }

    private static LocalTime getMaxTimeWithNanosPrecision(int precision) {
        if (precision < 0 || precision > MAX_TIME_NANOS_PRECISION) {
            throw new IllegalArgumentException("precision should be >= 0 and <= 9");
        }

        var maxNanoPrecision = LocalTime.MAX.getNano();
        var delim = (int) Math.pow(10, (MAX_TIME_NANOS_PRECISION - precision));
        var truncatedNanos = (maxNanoPrecision / delim) * delim;

        return LocalTime.of(23, 59, 59, truncatedNanos);
    }

    public static Instant atTime(Instant instant, LocalTime time, ZoneId zoneId) {
        return LocalDate.ofInstant(instant, zoneId).atTime(time).atZone(zoneId).toInstant();
    }

    public static String formatInstantToUtcDate(Instant date) {
        return Optional.ofNullable(date).map(instant -> dateFormatterStartDay.format(date)).orElse(null);
    }

    public static Instant parseUtcDateToInstantStartDay(String date) {
        return parseDate(date, dateFormatterStartDay, Instant::from);
    }

    public static Instant parseUtcDateToInstantEndDay(String date) {
        return parseDate(date, dateFormatterEndDay, Instant::from);
    }

    public static LocalDate parseDateToLocalDate(String date) {
        return parseDate(date, localDateFormatter, LocalDate::from);
    }

    public static String formatLocalDate(LocalDate date) {
        return Optional.ofNullable(date).map(localDate -> localDate.format(localDateFormatter)).orElse(null);
    }

    public static String formatLocalDate(LocalDate date, ZoneId zoneId) {
        return Optional.ofNullable(date).map(localDate -> localDateFormatter.withZone(zoneId).format(localDate)).orElse(null);
    }

    public static String formatDate(Instant instant, ZoneId zoneId) {
        return formatDate(instant, zoneId, localDateFormatter);
    }

    public static String formatDate(Instant instant, ZoneId zoneId, DateTimeFormatter dateTimeFormatter) {
        return Optional.ofNullable(instant).map(i -> dateTimeFormatter.withZone(zoneId).format(i)).orElse(null);
    }

    public static String formatDate(Instant instant, Integer timezoneOffset) {
        return formatDate(instant, generateZoneOffset(timezoneOffset));
    }

    public static String formatDate(Instant instant, Integer timezoneOffset, DateTimeFormatter dateTimeFormatter) {
        return formatDate(instant, generateZoneOffset(timezoneOffset), dateTimeFormatter);
    }

    public static String formatTime(Instant instant, ZoneId zoneId) {
        return Optional.ofNullable(instant).map(i -> timeFormatter.withZone(zoneId).format(i)).orElse(null);
    }

    public static String formatTimeWithZone(Instant instant, ZoneId zoneId) {
        return Optional.ofNullable(instant).map(i -> timeWithZoneFormatter.withZone(zoneId).format(i)).orElse(null);
    }

    public static String formatDateTime(Instant instant, Integer timezoneOffset) {
        return formatDateTime(instant, generateZoneOffset(timezoneOffset));
    }

    public static String formatDateTime(Instant instant, ZoneId zoneId) {
        return Optional.ofNullable(instant).map(i -> dateTimeFormatter.withZone(zoneId).format(i)).orElse(null);
    }

    public static String formatDateTimeWithZone(Instant instant, ZoneId zoneId) {
        return Optional.ofNullable(instant).map(i -> dateTimeWithZoneFormatter.withZone(zoneId).format(i)).orElse(null);
    }

    public static String formatDate(Long date, Integer timezoneOffset) {
        return Optional.ofNullable(date)
                .map(dateMillis -> formatDate(Instant.ofEpochMilli(dateMillis), generateZoneOffset(timezoneOffset)))
                .orElse(null);
    }

    private static <T> T parseDate(String date, DateTimeFormatter formatter, TemporalQuery<T> query) {
        try {
            if (StringUtils.isNotBlank(date)) {
                return formatter.parse(date, query);
            }
            return null;
        } catch (DateTimeParseException e) {
            throw new InternalServerException(InternalServerExceptionType.DATE_FORMAT_INCORRECT);
        }
    }

    /**
     * Converts milliseconds to minutes. If the value is > x min 30 seconds -> result will be x+1, otherwise - x
     *
     * @param millis
     * @return minutes
     */
    public static Long millisToMinutes(Long millis) {
        return Optional.ofNullable(millis).map(aLong -> aLong / MILLIS_IN_MINUTE + (aLong % MILLIS_IN_MINUTE > MILLIS_IN_MINUTE / 2 ? 1 : 0))
                .orElse(null);
    }

    public static Instant plusMonths(Instant time, Integer monthCount) {
        var localDateTime = LocalDateTime.ofInstant(time, ZoneOffset.UTC);
        return localDateTime.plusMonths(monthCount).toInstant(ZoneOffset.UTC);
    }
}
