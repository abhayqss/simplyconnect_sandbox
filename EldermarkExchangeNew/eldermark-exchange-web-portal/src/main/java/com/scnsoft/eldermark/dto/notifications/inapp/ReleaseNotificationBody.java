package com.scnsoft.eldermark.dto.notifications.inapp;

public class ReleaseNotificationBody extends InAppNotificationBody {
    private String features;
    private String fixes;

    public ReleaseNotificationBody(String features, String fixes) {
        this.features = features;
        this.fixes = fixes;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getFixes() {
        return fixes;
    }

    public void setFixes(String fixes) {
        this.fixes = fixes;
    }
}
