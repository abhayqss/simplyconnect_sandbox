package com.scnsoft.eldermark.exchange;

import java.util.List;

public final class Configuration {
    private final List<String> databaseUrls;
    private final List<String> cloudDatabaseUrls;
    private final boolean runInBackground;

    public Configuration(List<String> databaseUrls, List<String> cloudDatabaseUrls, boolean runInBackground) {
        this.databaseUrls = databaseUrls;
        this.cloudDatabaseUrls = cloudDatabaseUrls;
        this.runInBackground = runInBackground;
    }

    public List<String> getDatabaseUrls() {
        return databaseUrls;
    }

    public List<String> getCloudDatabaseUrls() {
        return cloudDatabaseUrls;
    }

    public boolean isRunInBackground() {
        return runInBackground;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "databaseUrls=" + databaseUrls +
                ", cloudDatabaseUrls=" + cloudDatabaseUrls +
                ", runInBackground=" + runInBackground +
                '}';
    }
}
