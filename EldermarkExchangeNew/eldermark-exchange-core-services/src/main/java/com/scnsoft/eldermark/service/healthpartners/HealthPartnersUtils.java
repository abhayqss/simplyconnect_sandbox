package com.scnsoft.eldermark.service.healthpartners;

import com.scnsoft.eldermark.entity.healthpartner.BaseHealthPartnersRecord;

import java.util.List;

public class HealthPartnersUtils {
    private HealthPartnersUtils() {
    }


    public static final String MEDICATION_CLAIM_LEGACY_TABLE = "Medication_Claim";
    public static final String PROBLEM_CLAIM_LEGACY_TABLE = "Problem_Claim";
    public static final String DISPENSE_PROVIDER_LEGACY_TABLE = "DISPENSE_PERFORMER";


    public static final String DISPENSE_DELETED_BY_ADJUSTMENT = "DISPENSE_DELETED_BY_ADJ";
    public static final String ADJUSTMENT_CAUSED_MEDICATION_DELETE = "ADJ_CAUSED_MEDICATION_DELETE";
    public static final String MEDICATION_DELETED_BY_ADJUSTMENT = "MEDICATION_DELETED_BY_ADJ";
    public static final String ADJUSTMENT_CAUSED_DISPENSE_DELETE = "ADJ_CAUSED_DISPENSE_DELETE";

    public static void updateClaimWithValidationResult(List<String> errors, BaseHealthPartnersRecord record) {
        if (errors.size() > 0) {
            record.setSuccess(false);
            record.setErrorMessage(String.join(", ", errors));
        } else {
            record.setSuccess(true);
        }
    }
}
