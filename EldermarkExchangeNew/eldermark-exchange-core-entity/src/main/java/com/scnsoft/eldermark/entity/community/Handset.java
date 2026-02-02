package com.scnsoft.eldermark.entity.community;

import com.scnsoft.eldermark.entity.basic.AuditableEntity;

import javax.persistence.*;

@Entity
@Table(name = "Handset")
public class Handset extends AuditableEntity {

    @JoinColumn(name = "community_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Community community;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "handset_id", nullable = false)
    private String handsetId;

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getHandsetId() {
        return handsetId;
    }

    public void setHandsetId(String handsetId) {
        this.handsetId = handsetId;
    }
}
