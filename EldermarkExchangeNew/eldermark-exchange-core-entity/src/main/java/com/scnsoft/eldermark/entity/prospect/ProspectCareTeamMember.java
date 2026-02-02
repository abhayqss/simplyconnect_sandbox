package com.scnsoft.eldermark.entity.prospect;

import com.scnsoft.eldermark.entity.careteam.CareTeamMember;

import javax.persistence.*;

@Entity
@Table(name = "ProspectCareTeamMember")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
public class ProspectCareTeamMember extends CareTeamMember {

    @JoinColumn(name = "prospect_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Prospect prospect;

    @Column(name = "prospect_id", nullable = false, insertable = false, updatable = false)
    private Long prospectId;

    public Prospect getProspect() {
        return prospect;
    }

    public void setProspect(Prospect prospect) {
        this.prospect = prospect;
    }

    public Long getProspectId() {
        return prospectId;
    }

    public void setProspectId(Long prospectId) {
        this.prospectId = prospectId;
    }
}
