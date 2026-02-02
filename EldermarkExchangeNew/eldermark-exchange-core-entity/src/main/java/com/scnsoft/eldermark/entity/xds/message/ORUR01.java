package com.scnsoft.eldermark.entity.xds.message;

import com.scnsoft.eldermark.entity.xds.segment.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ORU_R01")
public class ORUR01 implements
        PIDSegmentContainingMessage,
        PV1SegmentContainingMessage,
        NTEListSegmentContainingMessage,
        ORCSegmentContainingMessage,
        OBXListSegmentContainingMessage,
        SPMSegmentContainingMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "msh_id")
    private MSHMessageHeaderSegment msh;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "pid_id")
    private PIDPatientIdentificationSegment pid;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "pv1_id")
    private PV1ClientVisitSegment pv1;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "orc_id")
    private ORCCommonOrderSegment orc;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "ORU_R01_NTE",
            joinColumns = @JoinColumn(name = "oru_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "nte_id", nullable = false)
    )
    private List<NTENotesAndComments> nteList;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "ORU_R01_OBX",
            joinColumns = @JoinColumn(name = "oru_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "obx_id", nullable = false)
    )
    private List<OBXObservationResult> obxList;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "spm_id")
    private SPMSpecimen spm;

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

    @Override
    public PIDPatientIdentificationSegment getPid() {
        return pid;
    }

    public void setPid(PIDPatientIdentificationSegment pid) {
        this.pid = pid;
    }

    @Override
    public PV1ClientVisitSegment getPv1() {
        return pv1;
    }

    public void setPv1(PV1ClientVisitSegment pv1) {
        this.pv1 = pv1;
    }

    @Override
    public ORCCommonOrderSegment getOrc() {
        return orc;
    }

    public void setOrc(ORCCommonOrderSegment orc) {
        this.orc = orc;
    }

    @Override
    public List<NTENotesAndComments> getNteList() {
        return nteList;
    }

    public void setNteList(List<NTENotesAndComments> nteList) {
        this.nteList = nteList;
    }

    @Override
    public List<OBXObservationResult> getObxList() {
        return obxList;
    }

    public void setObxList(List<OBXObservationResult> obxList) {
        this.obxList = obxList;
    }

    @Override
    public SPMSpecimen getSpm() {
        return spm;
    }

    public void setSpm(SPMSpecimen spm) {
        this.spm = spm;
    }
}

