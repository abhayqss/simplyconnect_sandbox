package com.scnsoft.eldermark.beans.reports.model.intune;

import java.time.Instant;
import java.util.List;

public class InTuneReportRowQuestion {
    private String question;
    private Integer yesAnswerCount;
    private List<Instant> yesAnswerDates;
    private Integer noAnswerCount;
    private List<Instant> noAnswerDates;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getYesAnswerCount() {
        return yesAnswerCount;
    }

    public void setYesAnswerCount(Integer yesAnswerCount) {
        this.yesAnswerCount = yesAnswerCount;
    }

    public List<Instant> getYesAnswerDates() {
        return yesAnswerDates;
    }

    public void setYesAnswerDates(List<Instant> yesAnswerDates) {
        this.yesAnswerDates = yesAnswerDates;
    }

    public Integer getNoAnswerCount() {
        return noAnswerCount;
    }

    public void setNoAnswerCount(Integer noAnswerCount) {
        this.noAnswerCount = noAnswerCount;
    }

    public List<Instant> getNoAnswerDates() {
        return noAnswerDates;
    }

    public void setNoAnswerDates(List<Instant> noAnswerDates) {
        this.noAnswerDates = noAnswerDates;
    }
}
