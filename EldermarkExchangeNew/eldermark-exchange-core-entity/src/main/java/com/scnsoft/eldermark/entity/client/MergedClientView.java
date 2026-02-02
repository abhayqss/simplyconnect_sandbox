package com.scnsoft.eldermark.entity.client;

import com.scnsoft.eldermark.entity.Client;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "MergedResidentsView")
public class MergedClientView {

    @EmbeddedId
    private MergedClientView.Id id;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long clientId;

    @Column(name = "merged_resident_id", nullable = false, insertable = false, updatable = false)
    private Long mergedClientId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "resident_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private Client client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "merged_resident_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private Client mergedClient;

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getMergedClient() {
        return mergedClient;
    }

    public void setMergedClient(Client mergedClient) {
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
