package com.scnsoft.eldermark.entity.audit;

import com.scnsoft.eldermark.entity.CommunityCareTeamMember;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "AuditLogRelation_OrganizationCareTeamMember")
public class AuditLogCommunityCareTeamMemberRelation extends AuditLogRelation<Long> {

    @JoinColumn(name = "care_team_member_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CommunityCareTeamMember careTeamMember;

    @Column(name = "care_team_member_id", nullable = false)
    private Long careTeamMemberId;

    @Column(name = "care_team_member_full_name")
    private String careTeamMemberFullName;

    @Column(name = "care_team_member_community_name")
    private String careTeamMemberCommunityName;

    public CommunityCareTeamMember getCareTeamMember() {
        return careTeamMember;
    }

    public void setCareTeamMember(CommunityCareTeamMember careTeamMember) {
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

    public String getCareTeamMemberCommunityName() {
        return careTeamMemberCommunityName;
    }

    public void setCareTeamMemberCommunityName(String careTeamMemberCommunityName) {
        this.careTeamMemberCommunityName = careTeamMemberCommunityName;
    }

    @Override
    public List<Long> getRelatedIds() {
        return List.of(careTeamMemberId);
    }

    @Override
    public List<String> getAdditionalFields() {
        return Stream.of(careTeamMemberCommunityName, careTeamMemberFullName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.COMMUNITY_CTM;
    }
}