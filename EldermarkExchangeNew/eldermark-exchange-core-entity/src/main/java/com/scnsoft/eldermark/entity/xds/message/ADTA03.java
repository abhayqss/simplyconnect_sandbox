package com.scnsoft.eldermark.entity.xds.message;

import com.scnsoft.eldermark.entity.xds.segment.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ADT_A03")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
public class ADTA03 extends AdtMessage implements
        PIDSegmentContainingMessage,
        EVNSegmentContainingMessage,
        PV1SegmentContainingMessage,
        DG1ListSegmentContainingMessage,
        PR1ListSegmentContaingMessage {

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pid_id")
    private PIDPatientIdentificationSegment pid;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "evn_id")
    private EVNEventTypeSegment evn;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pv1_id")
    private PV1ClientVisitSegment pv1;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pd1_id")
    private AdtPD1AdditionalDemographicSegment pd1;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "ADT_MSG2SGMNT_A03_TO_PR1",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "pr1_id")
    )
    private List<PR1ProceduresSegment> pr1List;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "ADT_MSG2SGMNT_A03_TO_DG1",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "DG1_SGMNT_id")
    )
    private List<AdtDG1DiagnosisSegment> dg1List;

    public List<PR1ProceduresSegment> getPr1List() {
        return pr1List;
    }

    public void setPr1List(List<PR1ProceduresSegment> pr1List) {
        this.pr1List = pr1List;
    }

    public List<AdtDG1DiagnosisSegment> getDg1List() {
        return dg1List;
    }

    public void setDg1List(List<AdtDG1DiagnosisSegment> dg1List) {
        this.dg1List = dg1List;
    }

    @Override
    public PV1ClientVisitSegment getPv1() {
        return pv1;
    }

    public void setPv1(PV1ClientVisitSegment pv1) {
        this.pv1 = pv1;
    }

    public AdtPD1AdditionalDemographicSegment getPd1() {
        return pd1;
    }

    public void setPd1(AdtPD1AdditionalDemographicSegment pd1) {
        this.pd1 = pd1;
    }

    @Override
    public PIDPatientIdentificationSegment getPid() {
        return pid;
    }

    public void setPid(PIDPatientIdentificationSegment pid) {
        this.pid = pid;
    }

    @Override
    public EVNEventTypeSegment getEvn() {
        return evn;
    }

    public void setEvn(EVNEventTypeSegment evn) {
        this.evn = evn;
    }

}
