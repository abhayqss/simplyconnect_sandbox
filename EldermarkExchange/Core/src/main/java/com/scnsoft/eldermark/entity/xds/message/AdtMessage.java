package com.scnsoft.eldermark.entity.xds.message;

import com.scnsoft.eldermark.entity.xds.segment.MSHMessageHeaderSegment;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.Serializable;


/**
 * @author sparuchnik
 */
@Entity
@Table(name = "AdtMessage")
@Inheritance(strategy = InheritanceType.JOINED)
public class AdtMessage implements MSHSegmentContainingMessage, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "msh_id")
    private MSHMessageHeaderSegment msh;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public MSHMessageHeaderSegment getMsh() {
        return msh;
    }

    public void setMsh(MSHMessageHeaderSegment msh) {
        this.msh = msh;
    }
}
