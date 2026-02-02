package com.scnsoft.eldermark.beans;

public class ClientMedicationCount {

    private ClientMedicationStatus status;
    private Long count;

    public ClientMedicationCount(ClientMedicationStatus status, Long count) {
        this.count = count;
        this.status = status;
    }

    public ClientMedicationStatus getStatus() {
        return status;
    }

    public void setStatusCode(ClientMedicationStatus status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
