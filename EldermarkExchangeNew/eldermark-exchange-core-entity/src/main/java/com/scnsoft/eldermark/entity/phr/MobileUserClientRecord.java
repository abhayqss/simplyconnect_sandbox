package com.scnsoft.eldermark.entity.phr;

import com.scnsoft.eldermark.entity.Client;

import javax.persistence.*;

@Entity
@Table(name = "UserResidentRecords")
public class MobileUserClientRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "int")
    private Long id;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true, updatable = false, insertable = false)
    private MobileUser mobileUser;

    @Column(name="resident_id")
    private Long clientId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false, insertable = false)
    private Client client;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MobileUser getMobileUser() {
        return mobileUser;
    }

    public void setMobileUser(MobileUser mobileUser) {
        this.mobileUser = mobileUser;
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
}
