package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "AuditLogRelation_ResidentCareTeamMember")
public class AuditLogClientCareTeamMemberRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "care_team_member_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ClientCareTeamMember careTeamMember;

    @Column(name = "care_team_member_id", nullable = false)
    private Long careTeamMemberId;

    @Column(name = "care_team_member_full_name")
    private String careTeamMemberFullName;

    public ClientCareTeamMember getCareTeamMember() {
        return careTeamMember;
    }

    public void setCareTeamMember(ClientCareTeamMember careTeamMember) {
        this.careTeamMember = careTeamMember;
    }

    public Long getCareTeamMemberId() {
        return careTeamMemberId;
    }

    public void setCareTeamMemberId(Long careTeamMemberId) {
        this.careTeamMemberId = careTeamMemberId;
    }

    public String getCareTeamMemberFullName() {
        return careTeamMemberFullName;
    }

    public void setCareTeamMemberFullName(String careTeamMemberFullName) {
        this.careTeamMemberFullName = careTeamMemberFullName;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(careTeamMemberId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return List.of(careTeamMemberFullName);
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.CLIENT_CTM;
    }
}
