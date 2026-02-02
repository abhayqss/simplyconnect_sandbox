package com.scnsoft.eldermark.dto.pointclickcare.notification;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public class PccDailyThresholdAboutToHitNotificationQueueDto {

    private int percentsRemaining;
    private int requestsRemaining;
    private int limit;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant resetAt;

    public PccDailyThresholdAboutToHitNotificationQueueDto() {
    }

    public PccDailyThresholdAboutToHitNotificationQueueDto(int percentsRemaining, int requestsRemaining, int limit, Instant resetAt) {
        this.percentsRemaining = percentsRemaining;
        this.requestsRemaining = requestsRemaining;
        this.limit = limit;
        this.resetAt = resetAt;
    }

    public int getPercentsRemaining() {
        return percentsRemaining;
    }

    public void setPercentsRemaining(int percentsRemaining) {
        this.percentsRemaining = percentsRemaining;
    }

    public int getRequestsRemaining() {
        return requestsRemaining;
    }

    public void setRequestsRemaining(int requestsRemaining) {
        this.requestsRemaining = requestsRemaining;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Instant getResetAt() {
        return resetAt;
    }

    public void setResetAt(Instant resetAt) {
        this.resetAt = resetAt;
    }
}
