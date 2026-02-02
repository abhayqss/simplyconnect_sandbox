package com.scnsoft.eldermark.entity.client.expense;

public enum ClientExpenseType {
    APPLICATION_FEE("Application Fee"),
    DEPOSIT("Deposit"),
    FIRST_MONTH_RENT("First Month's Rent"),
    RENT("Rent"),
    UTILITIES_DEPOSIT("Utilities Deposit"),
    UTILITIES("Utilities"),
    FURNITURE("Furniture"),
    BED("Bed"),
    BATH_AND_BEYOND_DONATIONS("Bath & Beyond Donations"),
    OTHER_EXPENSE("Other Expense");

    private final String displayName;

    ClientExpenseType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
