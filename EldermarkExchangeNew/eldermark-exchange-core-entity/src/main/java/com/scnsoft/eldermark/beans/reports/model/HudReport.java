package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;

public class HudReport extends Report {

    private HudFirstTab hudFirstTab;

    private List<HudSecondTab> hudSecondTab;

    public HudFirstTab getHudFirstTab() {
        return hudFirstTab;
    }

    public void setHudFirstTab(HudFirstTab hudFirstTab) {
        this.hudFirstTab = hudFirstTab;
    }

    public List<HudSecondTab> getHudSecondTab() {
        return hudSecondTab;
    }

    public void setHudSecondTab(List<HudSecondTab> hudSecondTab) {
        this.hudSecondTab = hudSecondTab;
    }
}
