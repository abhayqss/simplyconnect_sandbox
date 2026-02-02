package com.scnsoft.eldermark.service.healthpartners.ctx;

import java.util.List;

public class RxClaimProcessingContext extends ClaimProcessingContext {

    private List<Long> existedMedicationDispenses;

    public List<Long> getExistedMedicationDispenses() {
        return existedMedicationDispenses;
    }

    public void setExistedMedicationDispenses(List<Long> existedMedicationDispenses) {
        this.existedMedicationDispenses = existedMedicationDispenses;
    }
}
