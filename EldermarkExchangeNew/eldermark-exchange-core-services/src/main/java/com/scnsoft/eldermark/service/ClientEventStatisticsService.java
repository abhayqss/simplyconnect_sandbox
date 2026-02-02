package com.scnsoft.eldermark.service;

import java.time.Instant;
import java.util.List;

import com.scnsoft.eldermark.beans.EventGroupStatistics;
import com.scnsoft.eldermark.beans.security.PermissionFilter;

public interface ClientEventStatisticsService {
    
    List<EventGroupStatistics> findEventStatistics(Long clientId, PermissionFilter permissionFilter, Instant fromDate, Instant toDate);

}
