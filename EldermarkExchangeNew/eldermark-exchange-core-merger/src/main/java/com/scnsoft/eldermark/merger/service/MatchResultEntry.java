package com.scnsoft.eldermark.merger.service;

public class MatchResultEntry<T> {

    private final T r1;
    private final T r2;
    private final double confidence;

    public MatchResultEntry(T r1, T r2, double confidence) {
        this.r1 = r1;
        this.r2 = r2;
        this.confidence = confidence;
    }

    public T getR1() {
        return r1;
    }

    public T getR2() {
        return r2;
    }

    public double getConfidence() {
        return confidence;
    }
}
