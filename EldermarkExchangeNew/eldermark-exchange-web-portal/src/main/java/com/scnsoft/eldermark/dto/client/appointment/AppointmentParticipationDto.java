package com.scnsoft.eldermark.dto.client.appointment;

public class AppointmentParticipationDto {
    private boolean hasExternalProvider;
    private boolean hasNoProviders;

    public AppointmentParticipationDto(boolean hasExternalProvider, boolean hasNoProviders) {
        this.hasExternalProvider = hasExternalProvider;
        this.hasNoProviders = hasNoProviders;
    }

    public boolean getHasExternalProvider() {
        return hasExternalProvider;
    }

    public void setHasExternalProvider(boolean hasExternalProvider) {
        this.hasExternalProvider = hasExternalProvider;
    }

    public boolean getHasNoProviders() {
        return hasNoProviders;
    }

    public void setHasNoProviders(boolean hasNoProviders) {
        this.hasNoProviders = hasNoProviders;
    }
}
