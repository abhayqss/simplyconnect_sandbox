package com.scnsoft.eldermark.mobile.request;

public class MobileRequestContext {

    public static final String API_SUB_VERSION_HEADER = "Api-Sub-Version";
    public static final String PLATFORM_HEADER = "Platform";

    String apiSubVersion;
    String platform;

    public MobileRequestContext(String apiSubVersion, String platform) {
        this.apiSubVersion = apiSubVersion;
        this.platform = platform;
    }

    public String getApiSubVersion() {
        return apiSubVersion;
    }

    public void setApiSubVersion(String apiSubVersion) {
        this.apiSubVersion = apiSubVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
