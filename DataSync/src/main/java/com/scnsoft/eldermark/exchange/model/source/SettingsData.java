package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.SourceEntity;

public class SettingsData extends SourceEntity {
    private String semiPrivateCountAsHalfUnit;
    private String moveOutsCountOnNextDay;

    public String getSemiPrivateCountAsHalfUnit() {
        return semiPrivateCountAsHalfUnit;
    }

    public void setSemiPrivateCountAsHalfUnit(String semiPrivateCountAsHalfUnit) {
        this.semiPrivateCountAsHalfUnit = semiPrivateCountAsHalfUnit;
    }

    public String getMoveOutsCountOnNextDay() {
        return moveOutsCountOnNextDay;
    }

    public void setMoveOutsCountOnNextDay(String moveOutsCountOnNextDay) {
        this.moveOutsCountOnNextDay = moveOutsCountOnNextDay;
    }
}
