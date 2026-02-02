package com.scnsoft.eldermark.entity.careteam;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.NotificationPreferences;

@Entity
@Table(name = "CareTeamMemberNotificationPreferences")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
public class CareTeamMemberNotificationPreferences extends NotificationPreferences {

    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "care_team_member_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private CareTeamMember careTeamMember;

    public CareTeamMemberNotificationPreferences() {
    }

    public CareTeamMember getCareTeamMember() {
        return careTeamMember;
    }

    public void setCareTeamMember(CareTeamMember careTeamMember) {
        this.careTeamMember = careTeamMember;
    }

}
