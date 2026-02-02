package com.scnsoft.eldermark.entity.careteam;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CareTeamMemberModified")
public class CareTeamMemberModified extends CareTeamMemberModifiedBase {

    @Column(name = "removed")
    private boolean removed;

}