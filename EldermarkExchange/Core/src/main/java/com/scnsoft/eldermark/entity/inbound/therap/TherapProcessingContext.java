package com.scnsoft.eldermark.entity.inbound.therap;

import java.util.HashMap;
import java.util.Map;

public class TherapProcessingContext {

    private Map<String, Long> idfFormIdToCommunityId = new HashMap<>();
    private Map<String, Long> idfFormIdToResidentId = new HashMap<>();


    public Map<String, Long> getIdfFormIdToCommunityId() {
        return idfFormIdToCommunityId;
    }

    public Map<String, Long> getIdfFormIdToResidentId() {
        return idfFormIdToResidentId;
    }
}
