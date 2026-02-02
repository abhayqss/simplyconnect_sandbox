package com.scnsoft.eldermark.event.xml.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class DeviceEventProcessingResultDto implements Serializable {

    private Set<String> processed = new HashSet<>();

    private Set<String> failed = new HashSet<>();

    public void addProcessed(String deviceId) {
        processed.add(deviceId);
    }

    public void addFailed(String deviceId) {
        failed.add(deviceId);
    }

    public Set<String> getProcessed() {
        return processed;
    }

    public Set<String> getFailed() {
        return failed;
    }
}
