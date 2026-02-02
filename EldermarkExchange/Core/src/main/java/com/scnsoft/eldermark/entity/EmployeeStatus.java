package com.scnsoft.eldermark.entity;

public enum EmployeeStatus {
    ACTIVE(0),
    PENDING(1),
    EXPIRED(2),
    INACTIVE(3),
    DECLINED(4),
    CONFIRMED(5);

    private final Integer value;

    EmployeeStatus(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public String getText() {
        return this.toString();
    }

    public static EmployeeStatus getByValue(Integer value) {
        for (EmployeeStatus status : EmployeeStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}