package com.scnsoft.eldermark.dto;

public class ProblemStatusCount {

    private Long active;

    private Long resolved;

    private Long other;

    public Long getActive() {
        return active;
    }

    public void setActive(Long active) {
        this.active = active;
    }

    public Long getResolved() {
        return resolved;
    }

    public void setResolved(Long resolved) {
        this.resolved = resolved;
    }

    public Long getOther() {
        return other;
    }

    public void setOther(Long other) {
        this.other = other;
    }
}
