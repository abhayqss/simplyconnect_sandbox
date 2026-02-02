package com.scnsoft.eldermark.service.pointclickcare;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PccTimezonesTest {

    @ParameterizedTest
    //these are the values specified in their api documentation
    @ValueSource(strings = {
            "Europe/London",
            "America/Halifax",
            "America/New_York",
            "America/Chicago",
            "America/Denver",
            "America/Los_Angeles",
            "America/Anchorage",
            "US/Aleutian",
            "GMT",
            "America/Barbados",
            "EST",
            "America/Regina",
            "America/Phoenix",
            "Pacific/Pitcairn",
            "Pacific/Gambier",
            "Pacific/Honolulu",
            "Asia/Hong_Kong"
    })
    void testTimeZone(String timezone) {
        var zone = ZoneId.of(timezone, ZoneId.SHORT_IDS);
        assertNotNull(zone);
    }
}
