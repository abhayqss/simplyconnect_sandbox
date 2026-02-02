package com.scnsoft.eldermark.dto.notification.fcm;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class FcmSuccessMessageDto {

    @JsonProperty("multicast_id")
    private long multicastId;
    @JsonProperty("canonical_ids")
    private long canonicalIds;
    @JsonProperty("success")
    private long success;
    @JsonProperty("failure")
    private long failure;
    @JsonProperty("results")
    private List<FcmResult> results = new ArrayList<FcmResult>();

    public long getMulticastId() {
        return multicastId;
    }

    public void setMulticastId(long multicastId) {
        this.multicastId = multicastId;
    }

    public long getCanonicalIds() {
        return canonicalIds;
    }

    public void setCanonicalIds(long canonicalIds) {
        this.canonicalIds = canonicalIds;
    }

    public long getSuccess() {
        return success;
    }

    public void setSuccess(long success) {
        this.success = success;
    }

    public long getFailure() {
        return failure;
    }

    public void setFailure(long failure) {
        this.failure = failure;
    }

    public List<FcmResult> getResults() {
        return results;
    }

    public void setResults(List<FcmResult> results) {
        this.results = results;
    }
}
