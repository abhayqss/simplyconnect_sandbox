package com.scnsoft.eldermark.h2.healthPartners.termedmembers;

import com.scnsoft.eldermark.dto.healthpartners.TermedMembersCSVDto;

public class TermedMemberExpectedDataDefinition {

    private final String description;
    private final int expectedClientOrder;

    private final boolean isSuccess;
    private final String errorMessage;

    private final TermedMembersCSVDto csvData;

    public static TermedMemberExpectedDataDefinition successful(String description, int expectedClientOrder, TermedMembersCSVDto csvData) {
        return new TermedMemberExpectedDataDefinition(description, expectedClientOrder, true, null,
                csvData);
    }

    public static TermedMemberExpectedDataDefinition failed(String description, String errorMessage, TermedMembersCSVDto csvData) {
        return new TermedMemberExpectedDataDefinition(description, -1, false, errorMessage,
                csvData);
    }

    public TermedMemberExpectedDataDefinition(String description, int expectedClientOrder, boolean isSuccess, String errorMessage, TermedMembersCSVDto csvData) {
        this.description = description;
        this.expectedClientOrder = expectedClientOrder;
        this.isSuccess = isSuccess;
        this.errorMessage = errorMessage;
        this.csvData = csvData;
    }

    public String getDescription() {
        return description;
    }

    public int getExpectedClientOrder() {
        return expectedClientOrder;
    }

    public TermedMembersCSVDto getCsvData() {
        return csvData;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "TermedMemberExpectedDataDefinition{" +
                "description='" + description + '\'' +
                ", expectedClientOrder=" + expectedClientOrder +
                ", isSuccess=" + isSuccess +
                ", errorMessage='" + errorMessage + '\'' +
                ", csvData=" + csvData +
                '}';
    }
}
