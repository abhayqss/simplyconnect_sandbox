package com.scnsoft.eldermark.merger.service;

import java.util.List;

public class MatchResult<R> {

    private final List<MatchResultEntry<R>> matchedRecords;
    private final List<MatchResultEntry<R>> probablyMatchedRecords;

    public MatchResult(List<MatchResultEntry<R>> matchedRecords, List<MatchResultEntry<R>> probablyMatchedRecords) {
        this.matchedRecords = matchedRecords;
        this.probablyMatchedRecords = probablyMatchedRecords;
    }

    public static <T> MatchResult<T> empty() {
        return new MatchResult<T>(List.of(), List.of());
    }

    public List<MatchResultEntry<R>> getMatchedRecords() {
        return matchedRecords;
    }

    public List<MatchResultEntry<R>> getProbablyMatchedRecords() {
        return probablyMatchedRecords;
    }
}
