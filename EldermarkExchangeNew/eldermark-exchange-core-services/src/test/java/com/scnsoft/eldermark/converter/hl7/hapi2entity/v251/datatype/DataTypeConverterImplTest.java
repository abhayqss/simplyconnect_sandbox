package com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.datatype;

import ca.uhn.hl7v2.model.v251.datatype.DTM;
import ca.uhn.hl7v2.model.v251.datatype.TS;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class DataTypeConverterImplTest {

    DataTypeConverter instance = new DataTypeConverterImpl();

    @Test
    void testConvertTS() {

        assertThat(instance.convertTS(mockTS("1997"))).isEqualTo(
                LocalDate.of(1997, 1, 1).atStartOfDay().atOffset(ZoneOffset.UTC).toInstant()
        );

        assertThat(instance.convertTS(mockTS("1997+0400"))).isEqualTo(
                LocalDate.of(1997, 1, 1).atStartOfDay().atOffset(ZoneOffset.ofHours(4)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("1997-0330"))).isEqualTo(
                LocalDate.of(1997, 1, 1).atStartOfDay().atOffset(ZoneOffset.ofHoursMinutes(-3, -30)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("199704"))).isEqualTo(
                LocalDate.of(1997, 4, 1).atStartOfDay().atOffset(ZoneOffset.UTC).toInstant()
        );

        assertThat(instance.convertTS(mockTS("199704+0400"))).isEqualTo(
                LocalDate.of(1997, 4, 1).atStartOfDay().atOffset(ZoneOffset.ofHours(4)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("199704-0330"))).isEqualTo(
                LocalDate.of(1997, 4, 1).atStartOfDay().atOffset(ZoneOffset.ofHoursMinutes(-3, -30)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("19970415"))).isEqualTo(
                LocalDate.of(1997, 4, 15).atStartOfDay().atOffset(ZoneOffset.UTC).toInstant()
        );

        assertThat(instance.convertTS(mockTS("19970415+0400"))).isEqualTo(
                LocalDate.of(1997, 4, 15).atStartOfDay().atOffset(ZoneOffset.ofHours(4)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("19970415-0330"))).isEqualTo(
                LocalDate.of(1997, 4, 15).atStartOfDay().atOffset(ZoneOffset.ofHoursMinutes(-3, -30)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("1997041513"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 0).atOffset(ZoneOffset.UTC).toInstant()
        );

        assertThat(instance.convertTS(mockTS("1997041513+0400"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 0).atOffset(ZoneOffset.ofHours(4)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("1997041513-0330"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 0).atOffset(ZoneOffset.ofHoursMinutes(-3, -30)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("199704151314"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 14).atOffset(ZoneOffset.UTC).toInstant()
        );

        assertThat(instance.convertTS(mockTS("199704151314+0400"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 14).atOffset(ZoneOffset.ofHours(4)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("199704151314-0330"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 14).atOffset(ZoneOffset.ofHoursMinutes(-3, -30)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("19970415131415"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 14, 15).atOffset(ZoneOffset.UTC).toInstant()
        );

        assertThat(instance.convertTS(mockTS("19970415131415+0400"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 14, 15).atOffset(ZoneOffset.ofHours(4)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("19970415131415-0330"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 14, 15).atOffset(ZoneOffset.ofHoursMinutes(-3, -30)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("19970415131415.1"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 14, 15, 100_000_000).atOffset(ZoneOffset.UTC).toInstant()
        );

        assertThat(instance.convertTS(mockTS("19970415131415.1+0400"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 14, 15, 100_000_000).atOffset(ZoneOffset.ofHours(4)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("19970415131415.1-0330"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 14, 15, 100_000_000).atOffset(ZoneOffset.ofHoursMinutes(-3, -30)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("19970415131415.12"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 14, 15, 120_000_000).atOffset(ZoneOffset.UTC).toInstant()
        );

        assertThat(instance.convertTS(mockTS("19970415131415.12+0400"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 14, 15, 120_000_000).atOffset(ZoneOffset.ofHours(4)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("19970415131415.12-0330"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 14, 15, 120_000_000).atOffset(ZoneOffset.ofHoursMinutes(-3, -30)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("19970415131415.123"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 14, 15, 123_000_000).atOffset(ZoneOffset.UTC).toInstant()
        );

        assertThat(instance.convertTS(mockTS("19970415131415.123+0400"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 14, 15, 123_000_000).atOffset(ZoneOffset.ofHours(4)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("19970415131415.123-0330"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 14, 15, 123_000_000).atOffset(ZoneOffset.ofHoursMinutes(-3, -30)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("19970415131415.1234"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 14, 15, 123_400_000).atOffset(ZoneOffset.UTC).toInstant()
        );

        assertThat(instance.convertTS(mockTS("19970415131415.1234+0400"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 14, 15, 123_400_000).atOffset(ZoneOffset.ofHours(4)).toInstant()
        );

        assertThat(instance.convertTS(mockTS("19970415131415.1234-0330"))).isEqualTo(
                LocalDateTime.of(1997, 4, 15, 13, 14, 15, 123_400_000).atOffset(ZoneOffset.ofHoursMinutes(-3, -30)).toInstant()
        );
    }


    private TS mockTS(String dateTime) {
        var ts = Mockito.mock(TS.class);
        var dtm = Mockito.mock(DTM.class);

        when(ts.getTs1_Time()).thenReturn(dtm);
        when(dtm.getValue()).thenReturn(dateTime);

        return ts;
    }
}