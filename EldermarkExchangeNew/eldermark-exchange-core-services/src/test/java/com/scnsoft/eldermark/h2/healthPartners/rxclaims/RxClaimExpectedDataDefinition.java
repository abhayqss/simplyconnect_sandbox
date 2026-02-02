package com.scnsoft.eldermark.h2.healthPartners.rxclaims;

import com.scnsoft.eldermark.dto.healthpartners.RxClaimCSVDto;

import java.util.ArrayList;
import java.util.List;

public class RxClaimExpectedDataDefinition {
    static final int MEDICATION_DELETED = -100;
    static final int DISPENSE_DELETED = -100;

    private final String description;
    private final int expectedClientOrder;
    private final int expectedMedicationOrder;
    private final int expectedDispenseOrder;

    private final boolean isSuccess;
    private final String errorMessage;

    private final boolean isDuplicate;

    private final RxClaimCSVDto csvData;

    private boolean isAdjustment;
    private final List<RxClaimExpectedDataDefinition> adjustedBy = new ArrayList<>();

    public static RxClaimExpectedDataDefinition successful(String description, int expectedClientOrder,
                                                           int expectedMedicationOrder, int expectedDispenseOrder,
                                                           RxClaimCSVDto csvData) {
        return new RxClaimExpectedDataDefinition(description, expectedClientOrder, expectedMedicationOrder, expectedDispenseOrder, true, null, false, csvData);
    }

    public static RxClaimExpectedDataDefinition adjusts(String description, RxClaimExpectedDataDefinition original,
                                                        int expectedMedicationOrder, int expectedDispenseOrder,
                                                        RxClaimCSVDto csvData) {
        var result = new RxClaimExpectedDataDefinition(description,
                original.expectedClientOrder, original.expectedMedicationOrder, original.expectedDispenseOrder, true, null, false, csvData);

        result.isAdjustment = true;
        original.adjustedBy.add(result);
        return result;
    }


    public static RxClaimExpectedDataDefinition failed(String description, String errorMessage, RxClaimCSVDto csvData) {
        return new RxClaimExpectedDataDefinition(description, -1, -1, -1, false, errorMessage, false, csvData);
    }

    public static RxClaimExpectedDataDefinition duplicate(String description, RxClaimCSVDto csvData) {
        return new RxClaimExpectedDataDefinition(description, -1, -1, -1, true, null, true, csvData);
    }

    private RxClaimExpectedDataDefinition(String description, int expectedClientOrder,
                                          int expectedMedicationOrder, int expectedDispenseOrder,
                                          boolean isSuccess, String errorMessage, boolean isDuplicate, RxClaimCSVDto csvData) {
        this.description = description;
        this.expectedClientOrder = expectedClientOrder;
        this.expectedMedicationOrder = expectedMedicationOrder;
        this.expectedDispenseOrder = expectedDispenseOrder;
        this.isSuccess = isSuccess;
        this.errorMessage = errorMessage;
        this.isDuplicate = isDuplicate;
        this.csvData = csvData;
    }


    public int getExpectedClientOrder() {
        return expectedClientOrder;
    }

    public int getExpectedMedicationOrder() {
        return expectedMedicationOrder;
    }

    public int getExpectedDispenseOrder() {
        return expectedDispenseOrder;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isDuplicate() {
        return isDuplicate;
    }

    public boolean isAdjustment() {
        return isAdjustment;
    }

    public List<RxClaimExpectedDataDefinition> getAdjustedBy() {
        return adjustedBy;
    }

    public RxClaimCSVDto getCsvData() {
        return csvData;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "RxClaimExpectedDataDefinition{" +
                "description='" + description + '\'' +
                ", expectedClientOrder=" + expectedClientOrder +
                ", expectedMedicationOrder=" + expectedMedicationOrder +
                ", expectedDispenseOrder=" + expectedDispenseOrder +
                ", isSuccess=" + isSuccess +
                ", errorMessage='" + errorMessage + '\'' +
                ", isDuplicate=" + isDuplicate +
                ", csvData=" + csvData +
                ", isAdjustment=" + isAdjustment +
                ", adjustedBy=" + adjustedBy +
                '}';
    }
}
