package com.scnsoft.eldermark.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;

public class LoggingRetryListener extends RetryListenerSupport {

    private static final Logger logger = LoggerFactory.getLogger(LoggingRetryListener.class);

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        logger.error("Exception during retry: ", throwable);
    }
}
