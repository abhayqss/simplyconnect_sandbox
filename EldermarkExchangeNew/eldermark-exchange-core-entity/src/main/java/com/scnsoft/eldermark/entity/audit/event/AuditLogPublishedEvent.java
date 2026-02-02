package com.scnsoft.eldermark.entity.audit.event;

import java.util.function.Consumer;

public class AuditLogPublishedEvent<T> {
    private T param;
    private Consumer<T> consumer;

    public AuditLogPublishedEvent(T param, Consumer<T> consumer) {
        this.param = param;
        this.consumer = consumer;
    }

    public T getParam() {
        return param;
    }

    public void setParam(T param) {
        this.param = param;
    }

    public Consumer<T> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<T> consumer) {
        this.consumer = consumer;
    }
}
