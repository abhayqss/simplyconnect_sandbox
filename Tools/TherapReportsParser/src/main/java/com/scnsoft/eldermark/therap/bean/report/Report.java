package com.scnsoft.eldermark.therap.bean.report;

import java.util.HashMap;
import java.util.Map;

public class Report {
    private int totalFiles;
    private Map<String, OrganizationReport> organizations = new HashMap<>();

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public Map<String, OrganizationReport> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(Map<String, OrganizationReport> organizations) {
        this.organizations = organizations;
    }
}
