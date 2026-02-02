package com.scnsoft.eldermark.entity.lab.review;

import java.time.Instant;
import java.util.Objects;

public class LabResearchOrderWithClient {
    private Long id;
    private Long clientId;
    private String clientFirstName;
    private String clientLastName;
    private Instant orderDate;

    public LabResearchOrderWithClient(Long id, Long clientId, String clientFirstName, String clientLastName, Instant orderDate) {
        this.id = id;
        this.clientId = clientId;
        this.clientFirstName = clientFirstName;
        this.clientLastName = clientLastName;
        this.orderDate = orderDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Instant getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LabResearchOrderWithClient)) return false;
        LabResearchOrderWithClient that = (LabResearchOrderWithClient) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
