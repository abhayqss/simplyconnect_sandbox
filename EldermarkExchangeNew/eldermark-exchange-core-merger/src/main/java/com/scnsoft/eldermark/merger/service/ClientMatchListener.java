package com.scnsoft.eldermark.merger.service;

import no.priv.garshol.duke.Record;
import no.priv.garshol.duke.matchers.AbstractMatchListener;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

public class ClientMatchListener<T> extends AbstractMatchListener {

    private final Converter<Record, T> recordConverter;

    private final List<MatchResultEntry<T>> matchedRecordsEntries = new ArrayList<>();
    private final List<MatchResultEntry<T>> probablyMatchedRecordsEntries = new ArrayList<>();

    public ClientMatchListener(Converter<Record, T> recordConverter) {
        this.recordConverter = recordConverter;
    }

    public void matches(Record r1, Record r2, double confidence) {
        super.matches(r1, r2, confidence);
        matchedRecordsEntries.add(new MatchResultEntry<>(
                recordConverter.convert(r1),
                recordConverter.convert(r2),
                confidence)
        );
    }

    public void matchesPerhaps(Record r1, Record r2, double confidence) {
        super.matchesPerhaps(r1, r2, confidence);
        probablyMatchedRecordsEntries.add(new MatchResultEntry<>(
                recordConverter.convert(r1),
                recordConverter.convert(r2),
                confidence)
        );
    }

    public List<MatchResultEntry<T>> getMatchedRecordsEntries() {
        return matchedRecordsEntries;
    }

    public List<MatchResultEntry<T>> getProbablyMatchedRecordsEntries() {
        return probablyMatchedRecordsEntries;
    }
}
