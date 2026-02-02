package com.scnsoft.eldermark.service.pushnotification;

import java.util.ArrayList;
import java.util.List;

public class SendPushNotificationResult {
    private long deliveredCount;
    private List<Exception> exceptions = new ArrayList<>();

    public long getDeliveredCount() {
        return deliveredCount;
    }

    public void setDeliveredCount(long deliveredCount) {
        this.deliveredCount = deliveredCount;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }

    public boolean hasExceptions() {
        return exceptions.size() > 0;
    }

    void merge(SendPushNotificationResult other) {
        this.deliveredCount += other.deliveredCount;
        this.exceptions.addAll(other.exceptions);
    }

    public long addDelivered() {
        return ++deliveredCount;
    }
}
