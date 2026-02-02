package com.scnsoft.eldermark.dto.prospect;

import java.time.Instant;

public class ProspectActivationDto {
    private Instant activationDate;
    private String comment;

    public Instant getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Instant activationDate) {
        this.activationDate = activationDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
