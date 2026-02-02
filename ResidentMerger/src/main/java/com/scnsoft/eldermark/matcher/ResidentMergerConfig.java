package com.scnsoft.eldermark.matcher;

public class ResidentMergerConfig {
    private int scheduleInMillis;
    private boolean runOnce;

    public ResidentMergerConfig(int scheduleInMillis, boolean runOnce) {
        this.scheduleInMillis = scheduleInMillis;
        this.runOnce = runOnce;
    }

    public int getScheduleInMillis() {
        return scheduleInMillis;
    }

    public void setScheduleInMillis(int scheduleInMillis) {
        this.scheduleInMillis = scheduleInMillis;
    }

    public boolean isRunOnce() {
        return runOnce;
    }

    public void setRunOnce(boolean runOnce) {
        this.runOnce = runOnce;
    }
}
