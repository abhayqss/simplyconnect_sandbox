package com.scnsoft.eldermark.util.cda;

import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EldermarkMedicationRecurrenceTest {

    @Test
    public void test_NullInput_throws() {
        assertThatThrownBy(() -> new EldermarkMedicationRecurrence(null))
                .isInstanceOf(EldermarkRecurrenceParseException.class);
    }

    @Test
    public void test_EmptyInput_throws() {
        assertThatThrownBy(() -> new EldermarkMedicationRecurrence(""))
                .isInstanceOf(EldermarkRecurrenceParseException.class);
    }

    @Test
    public void test_InvalidInput_throws() {
        assertThatThrownBy(() -> new EldermarkMedicationRecurrence("asdf fdsa test"))
                .isInstanceOf(EldermarkRecurrenceParseException.class);
    }

    @Test
    public void test_Daily_1() throws EldermarkRecurrenceParseException {
        var recurrenceString = "Daily";
        var expected = new EldermarkMedicationRecurrence();
        expected.setPeriod(EldermarkMedicationRecurrence.Period.DAILY);

        test(recurrenceString, expected, "Daily");
    }

    @Test
    public void test_Daily_2() throws EldermarkRecurrenceParseException {
        var recurrenceString = "Daily.EveryXDays:3.StartDate:08/21/2009.StartDayOffset.3520";
        var expected = new EldermarkMedicationRecurrence();
        expected.setPeriod(EldermarkMedicationRecurrence.Period.DAILY);
        expected.setEveryXDays(3);
        expected.setStartDate(LocalDate.of(2009, 8, 21));
        expected.setStartDayOffset(3520);

        test(recurrenceString, expected, "Every 3 days, starting on 08/21/2009");
    }

    @Test
    public void test_Daily_3() throws EldermarkRecurrenceParseException {
        var recurrenceString = "Daily.EvenDays";
        var expected = new EldermarkMedicationRecurrence();
        expected.setPeriod(EldermarkMedicationRecurrence.Period.DAILY);
        expected.setEvenDays(true);

        test(recurrenceString, expected, "Even days of the month");
    }

    @Test
    public void test_Daily_4() throws EldermarkRecurrenceParseException {
        var recurrenceString = "Daily.OddDays";
        var expected = new EldermarkMedicationRecurrence();
        expected.setPeriod(EldermarkMedicationRecurrence.Period.DAILY);
        expected.setOddDays(true);

        test(recurrenceString, expected, "Odd days of the month");
    }

    @Test
    public void test_Daily_5() throws EldermarkRecurrenceParseException {
        var recurrenceString = "Daily.EveryWeekday";
        var expected = new EldermarkMedicationRecurrence();
        expected.setPeriod(EldermarkMedicationRecurrence.Period.DAILY);
        expected.setEveryWeekday(true);

        test(recurrenceString, expected, "Monday through Friday");
    }

    @Test
    public void test_Daily_6() throws EldermarkRecurrenceParseException {
        var recurrenceString = "Daily.DaysON:21.DaysOFF:7.StartDate:07/20/2019.StartDayOffset.7171";
        var expected = new EldermarkMedicationRecurrence();
        expected.setPeriod(EldermarkMedicationRecurrence.Period.DAILY);
        expected.setDaysON(21);
        expected.setDaysOFF(7);
        expected.setStartDate(LocalDate.of(2019, 7, 20));
        expected.setStartDayOffset(7171);
        test(recurrenceString, expected, "Daily for 21 day(s), then OFF for 7 day(s), starting on 07/20/2019");
    }

    @Test
    public void test_Daily_7() throws EldermarkRecurrenceParseException {
        var recurrenceString = "Daily.EveryXDaysOrLess:15.DayNames:Sun,Tue,Thu,Fri,Sat.StartDate:02/19/2015.StartDayOffset.5528";
        var expected = new EldermarkMedicationRecurrence();
        expected.setPeriod(EldermarkMedicationRecurrence.Period.DAILY);
        expected.setEveryXDaysOrLess(15);
        expected.setDayNames(Set.of(DayOfWeek.SUNDAY, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY));
        expected.setStartDate(LocalDate.of(2015, 2, 19));
        expected.setStartDayOffset(5528);
        test(recurrenceString, expected, "Daily for 15 days on Tuesday, Thursday, Friday, Saturday, Sunday, starting on 02/19/2015");
    }

    @Test
    public void test_Weekly_1() throws EldermarkRecurrenceParseException {
        var recurrenceString = "Weekly.EveryXWeeks:1.DayNames:Sun,Tue,Thu,Sat";
        var expected = new EldermarkMedicationRecurrence();
        expected.setPeriod(EldermarkMedicationRecurrence.Period.WEEKLY);
        expected.setEveryXWeeks(1);
        expected.setDayNames(Set.of(DayOfWeek.SUNDAY, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY));

        test(recurrenceString, expected, "Every week on Tuesday, Thursday, Saturday, Sunday");
    }

    @Test
    public void test_Weekly_2() throws EldermarkRecurrenceParseException {
        var recurrenceString = "Weekly.EveryXWeeks:2.DayNames:Mon,Wed,Fri.StartDate:11/22/2017.StartDayOffset.6535";
        var expected = new EldermarkMedicationRecurrence();
        expected.setPeriod(EldermarkMedicationRecurrence.Period.WEEKLY);
        expected.setEveryXWeeks(2);
        expected.setDayNames(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
        expected.setStartDate(LocalDate.of(2017, 11, 22));
        expected.setStartDayOffset(6535);

        test(recurrenceString, expected, "Every 2 weeks on Monday, Wednesday, Friday, starting on 11/22/2017");
    }

    @Test
    public void test_Monthly_1() throws EldermarkRecurrenceParseException {
        var recurrenceString = "Monthly.EveryXMonths:1.DayOfMonth:29";
        var expected = new EldermarkMedicationRecurrence();
        expected.setPeriod(EldermarkMedicationRecurrence.Period.MONTHLY);
        expected.setEveryXMonths(1);
        expected.setDayOfMonth(29);

        test(recurrenceString, expected, "Day 29 every month");
    }

    @Test
    public void test_Monthly_2() throws EldermarkRecurrenceParseException {
        var recurrenceString = "Monthly.EveryXMonths:2.DayOfMonth:23.StartDate:04/23/2019.StartDayOffset.7052";
        var expected = new EldermarkMedicationRecurrence();
        expected.setPeriod(EldermarkMedicationRecurrence.Period.MONTHLY);
        expected.setEveryXMonths(2);
        expected.setDayOfMonth(23);
        expected.setStartDate(LocalDate.of(2019, 4, 23));
        expected.setStartDayOffset(7052);

        test(recurrenceString, expected, "Day 23 every 2 months, starting on 04/23/2019");
    }

    @Test
    public void test_Monthly_3() throws EldermarkRecurrenceParseException {
        var recurrenceString = "Monthly.EveryXMonths:1.OrdinalWeekNum:4.DayName:Sat";
        var expected = new EldermarkMedicationRecurrence();
        expected.setPeriod(EldermarkMedicationRecurrence.Period.MONTHLY);
        expected.setEveryXMonths(1);
        expected.setOrdinalWeekNum("4");
        expected.setDayName(DayOfWeek.SATURDAY);

        test(recurrenceString, expected, "The 4th Saturday of every month");
    }

    @Test
    public void test_Monthly_4() throws EldermarkRecurrenceParseException {
        var recurrenceString = "Monthly.EveryXMonths:2.OrdinalWeekNum:L.DayName:Tue.StartDate:05/11/2021.StartDayOffset.7801";
        var expected = new EldermarkMedicationRecurrence();
        expected.setPeriod(EldermarkMedicationRecurrence.Period.MONTHLY);
        expected.setEveryXMonths(2);
        expected.setOrdinalWeekNum("L");
        expected.setDayName(DayOfWeek.TUESDAY);
        expected.setStartDate(LocalDate.of(2021, 5, 11));
        expected.setStartDayOffset(7801);

        test(recurrenceString, expected, "The last Tuesday of every 2 months, starting on 05/11/2021");
    }

    @Test
    public void test_Monthly_5() throws EldermarkRecurrenceParseException {
        var recurrenceString = "Monthly.EveryXMonths:1.DayNumbers:15,16,17";
        var expected = new EldermarkMedicationRecurrence();
        expected.setPeriod(EldermarkMedicationRecurrence.Period.MONTHLY);
        expected.setEveryXMonths(1);
        expected.setDayNumbers(Arrays.asList(15, 16, 17));

        test(recurrenceString, expected, "Days 15, 16, 17 of the month");
    }

    @Test
    public void test_Yearly_1() throws EldermarkRecurrenceParseException {
        var recurrenceString = "Yearly.MonthNumX:1.DayOfMonth:25";
        var expected = new EldermarkMedicationRecurrence();
        expected.setPeriod(EldermarkMedicationRecurrence.Period.YEARLY);
        expected.setMonthNumX(1);
        expected.setDayOfMonth(25);

        test(recurrenceString, expected, "Every January 25");
    }

    @Test
    public void test_Yearly_2() throws EldermarkRecurrenceParseException {
        var recurrenceString = "Yearly.MonthNumX:10.OrdinalWeekNum:L.DayName:Wed";
        var expected = new EldermarkMedicationRecurrence();
        expected.setPeriod(EldermarkMedicationRecurrence.Period.YEARLY);
        expected.setMonthNumX(10);
        expected.setOrdinalWeekNum("L");
        expected.setDayName(DayOfWeek.WEDNESDAY);

        test(recurrenceString, expected, "The last Wednesday of October");
    }


    private void test(String recurrence, EldermarkMedicationRecurrence expected, String toStringExpected) throws EldermarkRecurrenceParseException {
        var actual = new EldermarkMedicationRecurrence(recurrence);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        assertThat(actual.toString()).isEqualTo(toStringExpected);
    }
}