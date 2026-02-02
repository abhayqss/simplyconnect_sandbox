package com.scnsoft.eldermark.services.exceptions;

import org.springframework.security.authentication.LockedException;

/**
 * @author phomal
 * Created on 11/15/2017.
 */
public class TimedLockedException extends LockedException {
    private final Long durationMs;

    public TimedLockedException(String msg, Long durationMs) {
        super(msg);
        this.durationMs = durationMs;
    }

    public Long getDurationMs() {
        return durationMs;
    }
}
