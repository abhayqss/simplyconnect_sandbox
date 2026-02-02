package com.scnsoft.eldermark.entity.client;

import javax.persistence.*;

@Entity
@Table(name = "ResidentPrimaryContact")
public class ClientPrimaryContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private ClientPrimaryContactType type;

    @JoinColumn(name = "resident_care_team_member_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ClientCareTeamMember clientCareTeamMember;

    @Column(name = "resident_care_team_member_id", insertable = false, updatable = false)
    private Long clientCareTeamMemberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 10)
    private ClientPrimaryContactNotificationMethod notificationMethod;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClientPrimaryContactType getType() {
        return type;
    }

    public void setType(ClientPrimaryContactType type) {
        this.type = type;
    }

    public ClientCareTeamMember getClientCareTeamMember() {
        return clientCareTeamMember;
    }

    public void setClientCareTeamMember(ClientCareTeamMember clientCareTeamMember) {
        this.clientCareTeamMember = clientCareTeamMember;
    }

    public Long getClientCareTeamMemberId() {
        return clientCareTeamMemberId;
    }

    public void setClientCareTeamMemberId(Long clientCareTeamMemberId) {
        this.clientCareTeamMemberId = clientCareTeamMemberId;
    }

    public ClientPrimaryContactNotificationMethod getNotificationMethod() {
        return notificationMethod;
    }

    public void setNotificationMethod(ClientPrimaryContactNotificationMethod notificationMethod) {
        this.notificationMethod = notificationMethod;
    }
}
