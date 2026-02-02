package com.scnsoft.eldermark.entity.client;

import com.scnsoft.eldermark.entity.Client;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ClientPharmacyFilterView")
public class ClientPharmacyFilterView {

    public static String NO_PHARMACY = "#NO_PHARMACY_FOUND_FOR_CLIENT";

    @EmbeddedId
    private ClientPharmacyFilterView.Id id;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long clientId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "resident_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private Client client;

    @Column(name = "pharmacy_name", nullable = false, insertable = false, updatable = false)
    private String pharmacyName;

    @Column(name = "facility_id", nullable = false, insertable = false, updatable = false)
    private Long communityId;

    @Column(name = "database_id", nullable = false, insertable = false, updatable = false)
    private Long organizationId;

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
        private Long clientId;

        @Column(name = "pharmacy_name", nullable = false, insertable = false, updatable = false)
        private String pharmacyName;

        public Long getClientId() {
            return clientId;
        }

        public void setClientId(Long clientId) {
            this.clientId = clientId;
        }

        public String getPharmacyName() {
            return pharmacyName;
        }

        public void setPharmacyName(String pharmacyName) {
            this.pharmacyName = pharmacyName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return Objects.equals(clientId, id.clientId) &&
                    Objects.equals(pharmacyName, id.pharmacyName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clientId, pharmacyName);
        }
    }

}
