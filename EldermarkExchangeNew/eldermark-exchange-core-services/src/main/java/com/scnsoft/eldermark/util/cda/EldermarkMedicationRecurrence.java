package com.scnsoft.eldermark.util.cda;

import org.apache.commons.collections.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class EldermarkMedicationRecurrence {

    private static final String ENTRY_SEPARATOR = "\\.";
    private static final String KEY_VALUE_SEPARATOR = ":";
    private static final String VALUE_LIST_SEPARATOR = ",";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/d/yyyy");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH);

    private Period period;

    //common
    private LocalDate startDate;
    private int startDayOffset;
    private Set<DayOfWeek> dayNames;
    private DayOfWeek dayName;

    private Integer dayOfMonth;
    private String ordinalWeekNum;

    //daily fields
    private Integer everyXDays;
    private Boolean everyWeekday;
    private Integer everyXDaysOrLess;
    private Boolean oddDays;
    private Boolean evenDays;
    private Integer daysON;
    private Integer daysOFF;

    //weekly fields
    private Integer everyXWeeks;

    //monthly fields
    private Integer everyXMonths;
    private List<Integer> dayNumbers;

    //yearly fields
    private Integer monthNumX;

    public EldermarkMedicationRecurrence(String recurrence) throws EldermarkRecurrenceParseException {
        try {
            Objects.requireNonNull(recurrence);
            parse(recurrence);
        } catch (Exception e) {
            throw new EldermarkRecurrenceParseException(e);
        }
    }

    EldermarkMedicationRecurrence() {
    }

    /*
        Daily
        Daily.EveryXDays:X:StartDate:{StartDate}.StartDayOffset.X      (start date only specified if EveryXWeeks>1)
        Daily.EveryWeekday
        Daily.EveryXDaysOrLess:X.DayNames:ddd,ddd,ddd.StartDate:{StartDate}.StartDayOffset.X
        Daily.OddDays
        Daily.EvenDays
        Daily.DaysON:X.DaysOFF:X.StartDate:{StartDate}.StartDayOffset.X

        Weekly.EveryXWeeks:X.DayNames:ddd,ddd,ddd.StartDate:{StartDate}.StartDayOffset.X      (start date only specified if EveryXWeeks>1)

        Monthly.EveryXMonths:X.DayOfMonth:X.StartDate:{StartDate}.StartDayOffset.X   (start date only specified if EveryXMonths>1)
        Monthly.EveryXMonths:X.OrdinalWeekNum:X{1,2,3,4,L}.DayName:Ddd.StartDate:{StartDate}.StartDayOffset.X   (start date only specified if EveryXMonths>1)
        Monthly.EveryXMonths:X.DayNumbers:X,X,X

        Yearly.MonthNumX:X.DayOfMonth:X
        Yearly.MonthNumX:X.OrdinalWeekNum:X{1,2,3,4,L}.DayName:Ddd

        Note - StartDayOffset is {StartDate} - 1/1/2000, or the number of days that the StartDate is after 1/1/2000
    */

    private void parse(String recurrence) throws EldermarkRecurrenceParseException {

        //adjust StartDayOffset to be split with value by : as others
        recurrence = recurrence.replaceAll("StartDayOffset\\.", "StartDayOffset:");


        var keyWithValues = recurrence.split(ENTRY_SEPARATOR);

        this.period = Period.valueOf(keyWithValues[0].toUpperCase());

        for (int i = 0; i < keyWithValues.length; ++i) {
            var split = keyWithValues[i].split(KEY_VALUE_SEPARATOR);
            parseValue(split[0], split.length > 1 ? split[1] : null);
        }
    }

    private void parseValue(String key, String value) {
        switch (key) {
            case "StartDate":
                this.startDate = LocalDate.parse(value, DATE_FORMATTER);
                break;
            case "StartDayOffset":
                this.startDayOffset = Integer.valueOf(value);
                break;
            case "DayNames":
                var days = value.split(VALUE_LIST_SEPARATOR);
                this.dayNames = Arrays.stream(days).map(this::parseDay).collect(Collectors.toSet());
                break;
            case "DayName":
                this.dayName = parseDay(value);
                break;
            case "DayOfMonth":
                this.dayOfMonth = Integer.valueOf(value);
                break;
            case "OrdinalWeekNum":
                this.ordinalWeekNum = value;
                break;

            //daily
            case "EveryXDays":
                this.everyXDays = Integer.valueOf(value);
                break;
            case "EveryWeekday":
                this.everyWeekday = true;
                break;
            case "EveryXDaysOrLess":
                this.everyXDaysOrLess = Integer.valueOf(value);
                break;
            case "OddDays":
                this.oddDays = true;
                break;
            case "EvenDays":
                this.evenDays = true;
                break;
            case "DaysON":
                this.daysON = Integer.valueOf(value);
                break;
            case "DaysOFF":
                this.daysOFF = Integer.valueOf(value);
                break;

            //weekly fields
            case "EveryXWeeks":
                this.everyXWeeks = Integer.valueOf(value);
                break;

            //monthly fields
            case "EveryXMonths":
                this.everyXMonths = Integer.valueOf(value);
                break;
            case "DayNumbers":
                this.dayNumbers = Arrays.stream(value.split(VALUE_LIST_SEPARATOR)).map(Integer::valueOf).collect(Collectors.toList());
                break;

            //yearly fields
            case "MonthNumX":
                this.monthNumX = Integer.valueOf(value);
                break;
        }
    }

    private DayOfWeek parseDay(String day) {
        return DayOfWeek.from(DAY_FORMATTER.parse(day));
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public int getStartDayOffset() {
        return startDayOffset;
    }

    public void setStartDayOffset(int startDayOffset) {
        this.startDayOffset = startDayOffset;
    }

    public Set<DayOfWeek> getDayNames() {
        return dayNames;
    }

    public void setDayNames(Set<DayOfWeek> dayNames) {
        this.dayNames = dayNames;
    }

    public DayOfWeek getDayName() {
        return dayName;
    }

    public void setDayName(DayOfWeek dayName) {
        this.dayName = dayName;
    }

    public Integer getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getOrdinalWeekNum() {
        return ordinalWeekNum;
    }

    public void setOrdinalWeekNum(String ordinalWeekNum) {
        this.ordinalWeekNum = ordinalWeekNum;
    }

    public Integer getEveryXDays() {
        return everyXDays;
    }

    public void setEveryXDays(Integer everyXDays) {
        this.everyXDays = everyXDays;
    }

    public Boolean getEveryWeekday() {
        return everyWeekday;
    }

    public void setEveryWeekday(Boolean everyWeekday) {
        this.everyWeekday = everyWeekday;
    }

    public Integer getEveryXDaysOrLess() {
        return everyXDaysOrLess;
    }

    public void setEveryXDaysOrLess(Integer everyXDaysOrLess) {
        this.everyXDaysOrLess = everyXDaysOrLess;
    }

    public Boolean getOddDays() {
        return oddDays;
    }

    public void setOddDays(Boolean oddDays) {
        this.oddDays = oddDays;
    }

    public Boolean getEvenDays() {
        return evenDays;
    }

    public void setEvenDays(Boolean evenDays) {
        this.evenDays = evenDays;
    }

    public Integer getDaysON() {
        return daysON;
    }

    public void setDaysON(Integer daysON) {
        this.daysON = daysON;
    }

    public Integer getDaysOFF() {
        return daysOFF;
    }

    public void setDaysOFF(Integer daysOFF) {
        this.daysOFF = daysOFF;
    }

    public Integer getEveryXWeeks() {
        return everyXWeeks;
    }

    public void setEveryXWeeks(Integer everyXWeeks) {
        this.everyXWeeks = everyXWeeks;
    }

    public Integer getEveryXMonths() {
        return everyXMonths;
    }

    public void setEveryXMonths(Integer everyXMonths) {
        this.everyXMonths = everyXMonths;
    }

    public List<Integer> getDayNumbers() {
        return dayNumbers;
    }

    public void setDayNumbers(List<Integer> dayNumbers) {
        this.dayNumbers = dayNumbers;
    }

    public Integer getMonthNumX() {
        return monthNumX;
    }

    public void setMonthNumX(Integer monthNumX) {
        this.monthNumX = monthNumX;
    }

    public enum Period {
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY
    }


    @Override
    public String toString() {
        var result = new StringBuilder();
        switch (period) {
            case DAILY:
                if (everyXDays != null && everyXDays > 1) {
                    result.append("Every ").append(everyXDays).append(" days");
                    break;
                }
                if (everyXDaysOrLess != null && CollectionUtils.isNotEmpty(dayNames)) {
                    result.append("Daily for ").append(everyXDaysOrLess).append(" days on ")
                            .append(dayNamesToString(dayNames));
                    break;
                }
                //todo        Daily.EveryXDaysOrLess:X.DayNames:ddd,ddd,ddd.StartDate:{StartDate}.StartDayOffset.X
                if (Boolean.TRUE.equals(oddDays)) {
                    result.append("Odd days of the month");
                    break;
                }
                if (Boolean.TRUE.equals(evenDays)) {
                    result.append("Even days of the month");
                    break;
                }
                if (Boolean.TRUE.equals(everyWeekday)) {
                    result.append("Monday through Friday");
                    break;
                }
                if (daysON != null && daysOFF != null) {
                    result.append("Daily for ").append(daysON).append(" day(s), then OFF for ").append(daysOFF).append(" day(s)");
                    break;
                }
                if (everyXDays == null || everyXDays == 1) {
                    result.append("Daily");
                    break;
                }
                break;
            case WEEKLY:
                if (everyXWeeks != null) {
                    result.append("Every ");
                    if (everyXWeeks > 1) {
                        result.append(everyXWeeks).append(" weeks");
                    } else {
                        result.append("week");
                    }
                    if (CollectionUtils.isNotEmpty(dayNames)) {
                        result.append(" on ").append(dayNamesToString(dayNames));
                    }
                }

                break;
            case MONTHLY:
                if (CollectionUtils.isNotEmpty(dayNumbers)) {
                    result.append("Days ").append(
                            dayNumbers.stream().map(Object::toString).collect(Collectors.joining(", "))
                    ).append(" of the month");
                    break;
                }
                if (everyXMonths != null) {
                    if (dayOfMonth != null) {
                        result.append("Day ").append(dayOfMonth);
                    }
                    if (ordinalWeekNum != null && dayName != null) {
                        appendWeekDay(result, ordinalWeekNum, dayName);
                        result.append(" of");
                    }
                    result.append(" every ");
                    if (everyXMonths > 1) {
                        result.append(everyXMonths).append(" months");
                    } else {
                        result.append("month");
                    }
                }
                break;
            case YEARLY:
                if (monthNumX != null) {
                    var monthName = Month.of(monthNumX).getDisplayName(TextStyle.FULL, Locale.US);
                    if (dayOfMonth != null) {
                        result.append("Every ").append(monthName).append(" ").append(dayOfMonth);
                        break;
                    }
                    if (ordinalWeekNum != null && dayName != null) {
                        appendWeekDay(result, ordinalWeekNum, dayName);
                        result.append(" of ").append(monthName);
                    }
                }
                break;
        }
        if (startDate != null) {
            result.append(", starting on ").append(startDate.format(DATE_FORMATTER));
        }

        return result.toString();
    }

    private String dayToString(DayOfWeek day) {
        return day.getDisplayName(TextStyle.FULL, Locale.US);
    }

    private static final Map<String, String> ordinalWeekNumDisplay = Map.of(
            "1", "1st",
            "2", "2nd",
            "3", "3rd",
            "4", "4th",
            "L", "last"
    );

    private void appendWeekDay(StringBuilder result, String ordinalWeekNum, DayOfWeek dayName) {
        result.append("The ")
                .append(ordinalWeekNumDisplay.getOrDefault(ordinalWeekNum, ordinalWeekNum))
                .append(" ").append(dayToString(dayName));
    }

    private String dayNamesToString(Collection<DayOfWeek> dayNames) {
        return dayNames.stream()
                .sorted(Comparator.comparing(DayOfWeek::getValue))
                .map(this::dayToString)
                .collect(Collectors.joining(", "));
    }
}
