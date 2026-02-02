package com.scnsoft.eldermark.dump.model;

import java.util.List;

public class GAD7PHQ9Scoring extends BaseClientInfo {
    private List<Long> gad7scores;
    private List<Long> phq9Scores;

    public List<Long> getGad7scores() {
        return gad7scores;
    }

    public void setGad7scores(List<Long> gad7scores) {
        this.gad7scores = gad7scores;
    }

    public List<Long> getPhq9Scores() {
        return phq9Scores;
    }

    public void setPhq9Scores(List<Long> phq9Scores) {
        this.phq9Scores = phq9Scores;
    }
}
