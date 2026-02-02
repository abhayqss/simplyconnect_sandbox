package com.scnsoft.eldermark.dto.pointclickcare.model.facility;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PccFacilityDetails {

    private String country;
    private String timeZone;
//    Represents the timezone the facility operates in.
//            If DST (Daylight Savings Time) is enabled:
//
//    Time Zone in PointClickCare	API Response
//    Greenwich Mean	Europe/London
//    Atlantic	America/Halifax
//    Eastern	America/New_York
//    Central	America/Chicago
//    Mountain	America/Denver
//    Pacific	America/Los_Angeles
//    Alaska	America/Anchorage
//    Hawaii	US/Aleutian
//    If DST is disabled:
//    Time Zone in PointClickCare	API Response
//    Greenwich Mean	GMT
//    Atlantic	America/Barbados
//    Eastern	EST
//    Central	America/Regina
//    Mountain	America/Phoenix
//    Pacific	Pacific/Pitcairn
//    Alaska	Pacific/Gambier
//    Hawaii	Pacific/Honolulu
//    Hong Kong	Asia/Hong_Kong


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}