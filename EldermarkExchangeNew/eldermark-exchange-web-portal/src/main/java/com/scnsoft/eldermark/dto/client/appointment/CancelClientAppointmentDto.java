package com.scnsoft.eldermark.dto.client.appointment;

import javax.validation.constraints.NotEmpty;

public class CancelClientAppointmentDto {

    @NotEmpty
    private String cancellationReason;

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

}
