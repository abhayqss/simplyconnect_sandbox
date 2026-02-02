package com.scnsoft.eldermark.entity.prospect;

import com.scnsoft.eldermark.entity.client.ClientPrimaryContactNotificationMethod;
import com.scnsoft.eldermark.entity.client.ClientPrimaryContactType;

import javax.persistence.*;

@Entity
@Table(name = "ProspectPrimaryContact")
public class ProspectPrimaryContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private ClientPrimaryContactType type;

    @JoinColumn(name = "prospect_care_team_member_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ProspectCareTeamMember prospectCareTeamMember;

    @Column(name = "prospect_care_team_member_id", insertable = false, updatable = false)
    private Long prospectCareTeamMemberId;

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

    public ProspectCareTeamMember getProspectCareTeamMember() {
        return prospectCareTeamMember;
    }

    public void setProspectCareTeamMember(ProspectCareTeamMember prospectCareTeamMember) {
        this.prospectCareTeamMember = prospectCareTeamMember;
    }

    public Long getProspectCareTeamMemberId() {
        return prospectCareTeamMemberId;
    }

    public void setProspectCareTeamMemberId(Long prospectCareTeamMemberId) {
        this.prospectCareTeamMemberId = prospectCareTeamMemberId;
    }

    public ClientPrimaryContactNotificationMethod getNotificationMethod() {
        return notificationMethod;
    }

    public void setNotificationMethod(ClientPrimaryContactNotificationMethod notificationMethod) {
        this.notificationMethod = notificationMethod;
    }
}
