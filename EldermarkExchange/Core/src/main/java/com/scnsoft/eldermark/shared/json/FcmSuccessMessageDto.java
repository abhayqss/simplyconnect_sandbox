package com.scnsoft.eldermark.shared.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Firebase Cloud Messaging - Downstream HTTP message response body (JSON)<br/>
 * See <a href="https://firebase.google.com/docs/cloud-messaging/http-server-ref#table5">Firebase Docs</a>
 *
 * @author phomal
 * Created on 8/2/2017.
 */
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

    private static ObjectMapper mapper = new ObjectMapper();

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

    public static FcmSuccessMessageDto fromJsonString(String jsonString) {
        try {
            return mapper.readValue(jsonString, FcmSuccessMessageDto.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
