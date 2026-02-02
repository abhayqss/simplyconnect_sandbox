package com.scnsoft.eldermark.dto.pointclickcare.notification;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public class PccDailyThresholdResetNotificationQueueDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant resetAt;

    public PccDailyThresholdResetNotificationQueueDto() {
    }

    public PccDailyThresholdResetNotificationQueueDto(Instant resetAt) {
        this.resetAt = resetAt;
    }

    public Instant getResetAt() {
        return resetAt;
    }

    public void setResetAt(Instant resetAt) {
        this.resetAt = resetAt;
    }
}
