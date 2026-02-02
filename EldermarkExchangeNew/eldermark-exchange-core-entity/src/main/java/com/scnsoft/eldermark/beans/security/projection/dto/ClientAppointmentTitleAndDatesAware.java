package com.scnsoft.eldermark.beans.security.projection.dto;

import com.scnsoft.eldermark.beans.projection.TitleAware;

import java.time.Instant;

public interface ClientAppointmentTitleAndDatesAware extends TitleAware {
    Instant getDateFrom();

    Instant getDateTo();
}
