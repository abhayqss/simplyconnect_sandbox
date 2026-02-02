package com.scnsoft.eldermark.consana.sync.client.model.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "MergedResidentsView")
public class MergedClientView {

    public static final String CLIENT_ID = "clientId";
    public static final String MERGED_CLIENT_ID = "mergedClientId";
    public static final String CLIENT = "client";
    public static final String MERGED_CLIENT = "mergedClient";

    @EmbeddedId
    private MergedClientView.Id id;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long clientId;

    @Column(name = "merged_resident_id", nullable = false, insertable = false, updatable = false)
    private Long mergedClientId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "resident_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private Resident client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "merged_resident_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private Resident mergedClient;

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getMergedClientId() {
        return mergedClientId;
    }

    public void setMergedClientId(Long mergedClientId) {
        this.mergedClientId = mergedClientId;
    }

    public Resident getClient() {
        return client;
    }

    public void setClient(Resident client) {
        this.client = client;
    }

    public Resident getMergedClient() {
        return mergedClient;
    }

    public void setMergedClient(Resident mergedClient) {
        this.mergedClient = mergedClient;
    }

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
        private Long clientId;

        @Column(name = "merged_resident_id", nullable = false, insertable = false, updatable = false)
        private Long mergedClientId;

        public Long getClientId() {
            return clientId;
        }

        public void setClientId(Long clientId) {
            this.clientId = clientId;
        }

        public Long getMergedClientId() {
            return mergedClientId;
        }

        public void setMergedClientId(Long mergedClientId) {
            this.mergedClientId = mergedClientId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return Objects.equals(clientId, id.clientId) &&
                    Objects.equals(mergedClientId, id.mergedClientId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clientId, mergedClientId);
        }
    }
}
