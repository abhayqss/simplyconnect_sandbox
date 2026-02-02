package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dto.pointclickcare.PointClickCareApiException;
import org.springframework.http.HttpMethod;

import java.time.Instant;
import java.util.Map;

public interface PointClickCareNotificationService {

    boolean sendDailyThresholdAboutToHit(int percentsRemaining, int requestsRemaining, int limit, Instant resetAt);

    boolean sendDailyThresholdReached(Instant resetAt);

    boolean resetDailyThreshold(Instant resetAt);

    boolean sendApiError(PointClickCareApiException apiException, HttpMethod method, String url, Map<String, Object> pathVariables);
}
