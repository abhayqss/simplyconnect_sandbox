package com.scnsoft.eldermark.services;

import java.util.List;

public interface ReportGenerator {
    Report generate(Long residentId, List<Long> residentIds);
    Report generate(Long residentId, List<Long> residentIds, Integer timeZoneOffsetInMinutes);
    Report generate(Long residentId, boolean aggregated);
    Report generate(Long residentId, boolean aggregated, Integer timeZoneOffsetInMinutes);
    Report generate(Long residentId);
    Report metadata();
}
