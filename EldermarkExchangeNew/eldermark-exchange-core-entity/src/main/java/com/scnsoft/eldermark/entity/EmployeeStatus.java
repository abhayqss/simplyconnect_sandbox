package com.scnsoft.eldermark.entity;

import java.util.Arrays;

public enum EmployeeStatus {
    ACTIVE(0, false, true),
    PENDING(1, false, false),
    EXPIRED(2, false, false),
    INACTIVE(3, false, false),
    DECLINED(4, true, false),
    CONFIRMED(5, false, true);

    private final Integer value;
    private final boolean hidden;
    private final boolean canLogin;

    EmployeeStatus(Integer value, boolean hidden, boolean canLogin) {
        this.value = value;
        this.hidden = hidden;
        this.canLogin = canLogin;
    }

    public Integer getValue() {
        return value;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public boolean canLogin() {
        return canLogin;
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

    public static EmployeeStatus[] allCanLogin() {
        return Arrays.stream(EmployeeStatus.values()).filter(EmployeeStatus::canLogin).toArray(EmployeeStatus[]::new);
    }
}
