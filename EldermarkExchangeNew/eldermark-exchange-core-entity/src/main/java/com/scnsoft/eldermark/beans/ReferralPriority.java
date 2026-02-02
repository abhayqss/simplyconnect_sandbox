package com.scnsoft.eldermark.beans;

public enum ReferralPriority {
    ROUTINE(1L),
    URGENT(2L),
    ASAP(3L),
    STAT(4L),
    OTHER(5L);

    private Long id;

    ReferralPriority(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}