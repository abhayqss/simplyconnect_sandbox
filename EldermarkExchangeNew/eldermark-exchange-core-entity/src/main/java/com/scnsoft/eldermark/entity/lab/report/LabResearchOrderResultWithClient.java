package com.scnsoft.eldermark.entity.lab.report;

import com.scnsoft.eldermark.entity.lab.LabResearchOrderReason;

import java.time.Instant;
import java.util.Objects;

public class LabResearchOrderResultWithClient {

    private Long id;
    private LabResearchOrderReason reason;
    private Instant specimenDate;
    private Long clientId;
    private String clientFirstName;
    private String clientLastName;
    private String clientCommunityName;
    private Instant oruReceivedDatetime;

    public LabResearchOrderResultWithClient(Long id, LabResearchOrderReason reason, Instant specimenDate, Long clientId,
                                            String clientFirstName, String clientLastName, String clientCommunityName, Instant oruReceivedDatetime) {
        this.id = id;
        this.reason = reason;
        this.specimenDate = specimenDate;
        this.clientId = clientId;
        this.clientFirstName = clientFirstName;
        this.clientLastName = clientLastName;
        this.clientCommunityName = clientCommunityName;
        this.oruReceivedDatetime = oruReceivedDatetime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LabResearchOrderReason getReason() {
        return reason;
    }

    public void setReason(LabResearchOrderReason reason) {
        this.reason = reason;
    }

    public Instant getSpecimenDate() {
        return specimenDate;
    }

    public void setSpecimenDate(Instant specimenDate) {
        this.specimenDate = specimenDate;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientFirstName() {
        return clientFirstName;
    }

    public void setClientFirstName(String clientFirstName) {
        this.clientFirstName = clientFirstName;
    }

    public String getClientLastName() {
        return clientLastName;
    }

    public void setClientLastName(String clientLastName) {
        this.clientLastName = clientLastName;
    }

    public String getClientCommunityName() {
        return clientCommunityName;
    }

    public void setClientCommunityName(String clientCommunityName) {
        this.clientCommunityName = clientCommunityName;
    }

    public Instant getOruReceivedDatetime() {
        return oruReceivedDatetime;
    }

    public void setOruReceivedDatetime(Instant oruReceivedDatetime) {
        this.oruReceivedDatetime = oruReceivedDatetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LabResearchOrderResultWithClient)) return false;
        LabResearchOrderResultWithClient that = (LabResearchOrderResultWithClient) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
