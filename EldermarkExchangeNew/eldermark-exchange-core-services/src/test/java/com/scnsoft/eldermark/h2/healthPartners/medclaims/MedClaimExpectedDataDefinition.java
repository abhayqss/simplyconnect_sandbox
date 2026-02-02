package com.scnsoft.eldermark.h2.healthPartners.medclaims;

import com.scnsoft.eldermark.dto.healthpartners.MedClaimCSVDto;

public class MedClaimExpectedDataDefinition {

    private final String description;
    private final int expectedClientOrder;
    private final int expectedProblemOrder;
    private final boolean shouldCreateCode;

    private final boolean isSuccess;
    private final String errorMessage;

    private final boolean isDuplicate;

    private final MedClaimCSVDto csvData;

    public static MedClaimExpectedDataDefinition successful(String description, int expectedClientOrder,
                                                            int expectedProblemOrder, boolean shouldCreateCode,
                                                            MedClaimCSVDto csvData) {
        return new MedClaimExpectedDataDefinition(description, expectedClientOrder, expectedProblemOrder, shouldCreateCode,
                true, null, false, csvData);
    }

    public static MedClaimExpectedDataDefinition failed(String description, String errorMessage, MedClaimCSVDto csvData) {
        return new MedClaimExpectedDataDefinition(description, -1, -1, false,
                false, errorMessage, false, csvData);
    }

    public static MedClaimExpectedDataDefinition duplicate(String description, MedClaimCSVDto csvData) {
        return new MedClaimExpectedDataDefinition(description, -1, -1, false,
                true, null, true, csvData);
    }

    private MedClaimExpectedDataDefinition(String description, int expectedClientOrder,
                                          int expectedProblemOrder, boolean shouldCreateCode,
                                          boolean isSuccess, String errorMessage, boolean isDuplicate, MedClaimCSVDto csvData) {
        this.description = description;
        this.expectedClientOrder = expectedClientOrder;
        this.expectedProblemOrder = expectedProblemOrder;
        this.shouldCreateCode = shouldCreateCode;
        this.isSuccess = isSuccess;
        this.errorMessage = errorMessage;
        this.isDuplicate = isDuplicate;
        this.csvData = csvData;
    }

    public String getDescription() {
        return description;
    }

    public int getExpectedClientOrder() {
        return expectedClientOrder;
    }

    public int getExpectedProblemOrder() {
        return expectedProblemOrder;
    }

    public boolean isShouldCreateCode() {
        return shouldCreateCode;
    }

    public MedClaimCSVDto getCsvData() {
        return csvData;
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

    @Override
    public String toString() {
        return "MedClaimExpectedDataDefinition{" +
                "description='" + description + '\'' +
                ", expectedClientOrder=" + expectedClientOrder +
                ", expectedProblemOrder=" + expectedProblemOrder +
                ", shouldCreateCode=" + shouldCreateCode +
                ", isSuccess=" + isSuccess +
                ", errorMessage='" + errorMessage + '\'' +
                ", isDuplicate=" + isDuplicate +
                ", csvData=" + csvData +
                '}';
    }
}
