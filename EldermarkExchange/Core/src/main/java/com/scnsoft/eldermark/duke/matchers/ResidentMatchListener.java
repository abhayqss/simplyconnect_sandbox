package com.scnsoft.eldermark.duke.matchers;

import no.priv.garshol.duke.Property;
import no.priv.garshol.duke.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by knetkachou on 1/27/2017.
 */
public class ResidentMatchListener extends AbstractResidentMatchListener {

    List<Record> matchedRecords = new ArrayList<Record>();
    List<Record> probablyMatchedRecords = new ArrayList<Record>();

    public ResidentMatchListener(boolean showmatches, boolean showmaybe, boolean progress, boolean linkage, List<Property> properties, boolean pretty) {
        super(showmatches, showmaybe, progress, linkage, properties, pretty);
    }

    public void matches(Record r1, Record r2, double confidence) {
        super.matches(r1, r2, confidence);
        matchedRecords.add(r1);
    }

    public void matchesPerhaps(Record r1, Record r2, double confidence) {
        super.matchesPerhaps(r1, r2, confidence);
        probablyMatchedRecords.add(r1);
    }

    public List<Record> getMatchedRecords() {
        return matchedRecords;
    }

    public void setMatchedRecords(List<Record> matchedRecords) {
        this.matchedRecords = matchedRecords;
    }

    public List<Record> getProbablyMatchedRecords() {
        return probablyMatchedRecords;
    }

    public void setProbablyMatchedRecords(List<Record> probablyMatchedRecords) {
        this.probablyMatchedRecords = probablyMatchedRecords;
    }
}
