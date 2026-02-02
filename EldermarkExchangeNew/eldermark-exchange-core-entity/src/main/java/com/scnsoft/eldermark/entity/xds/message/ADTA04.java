package com.scnsoft.eldermark.entity.xds.message;

import com.scnsoft.eldermark.entity.xds.segment.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ADT_A04")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
public class ADTA04 extends AdtMessage implements
        PIDSegmentContainingMessage,
        EVNSegmentContainingMessage,
        PV1SegmentContainingMessage,
        DG1ListSegmentContainingMessage,
        GT1ListSegmentContainingMessage,
        PR1ListSegmentContaingMessage,
        IN1ListSegmentContainingMessage,
        AL1ListSegmentContainingMessage {

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
            name = "ADT_MSG2SGMNT_A04_TO_PR1",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "pr1_id")
    )
    private List<PR1ProceduresSegment> pr1List;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "ADT_MSG2SGMNT_A04_TO_IN1",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "in1_id")
    )
    private List<IN1InsuranceSegment> in1List;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "ADT_MSG2SGMNT_A04_TO_GT1",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "GT1_SGMNT_id")
    )
    private List<AdtGT1GuarantorSegment> gt1List;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "ADT_MSG2SGMNT_A04_TO_DG1",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "DG1_SGMNT_id")
    )
    private List<AdtDG1DiagnosisSegment> dg1List;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "ADT_MSG2SGMNT_A04_TO_AL1",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "segment_id")
    )
    private List<AdtAL1AllergySegment> al1List;

    public List<PR1ProceduresSegment> getPr1List() {
        return pr1List;
    }

    public void setPr1List(List<PR1ProceduresSegment> pr1List) {
        this.pr1List = pr1List;
    }

    public List<IN1InsuranceSegment> getIn1List() {
        return in1List;
    }

    public void setIn1List(List<IN1InsuranceSegment> in1List) {
        this.in1List = in1List;
    }

    public List<AdtGT1GuarantorSegment> getGt1List() {
        return gt1List;
    }

    public void setGt1List(List<AdtGT1GuarantorSegment> gt1List) {
        this.gt1List = gt1List;
    }

    public List<AdtDG1DiagnosisSegment> getDg1List() {
        return dg1List;
    }

    public void setDg1List(List<AdtDG1DiagnosisSegment> dg1List) {
        this.dg1List = dg1List;
    }

    @Override
    public List<AdtAL1AllergySegment> getAL1List() {
        return al1List;
    }

    public void setAl1List(List<AdtAL1AllergySegment> al1List) {
        this.al1List = al1List;
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
