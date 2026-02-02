package com.scnsoft.eldermark.entity.xds.message;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

import com.scnsoft.eldermark.entity.xds.segment.MSHMessageHeaderSegment;

@Entity
@Table(name = "AdtMessage")
@Inheritance(strategy = InheritanceType.JOINED)
public class AdtMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @JoinColumn(name = "msh_id")
    private MSHMessageHeaderSegment msh;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MSHMessageHeaderSegment getMsh() {
        return msh;
    }

    public void setMsh(MSHMessageHeaderSegment msh) {
        this.msh = msh;
    }

}
