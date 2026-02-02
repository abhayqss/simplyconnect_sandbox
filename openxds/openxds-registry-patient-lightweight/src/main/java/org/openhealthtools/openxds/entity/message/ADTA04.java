package org.openhealthtools.openxds.entity.message;

import org.hibernate.annotations.Cascade;
import org.openhealthtools.openxds.entity.segment.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ADT_A04")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
public class ADTA04 extends AdtMessage implements
        MSHSegmentContainingMessage,
        EVNSegmentContainingMessage,
        PIDSegmentContainingMessage,
        PV1SegmentContainingMessage,
        PR1ListSegmentContaingMessage,
        IN1ListSegmentContainingMessage,
        AL1ListSegmentContainingMessage,
        DG1ListSegmentContainingMessage,
        GT1ListSegmentContainingMessage,
        PD1SegmentContainingMessage
{

    private static final long serialVersionUID = 1L;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "evn_id")
    private EVNEventTypeSegment evn;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "pid_id")
    private PIDPatientIdentificationSegment pid;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "pv1_id")
    private PV1PatientVisitSegment pv1;

    @OneToMany
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(
            name = "ADT_MSG2SGMNT_A04_TO_PR1",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "pr1_id")
    )
    private List<PR1ProceduresSegment> pr1List;

    @OneToMany
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
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

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "pd1_id")
    private AdtPD1AdditionalDemographicSegment pd1;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "ADT_MSG2SGMNT_A04_TO_AL1",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "segment_id")
    )
    private List<AdtAL1AllergySegment> al1List;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public EVNEventTypeSegment getEvn() {
        return evn;
    }

    public void setEvn(EVNEventTypeSegment evn) {
        this.evn = evn;
    }

    @Override
    public PIDPatientIdentificationSegment getPid() {
        return pid;
    }

    public void setPid(PIDPatientIdentificationSegment pid) {
        this.pid = pid;
    }

    @Override
    public PV1PatientVisitSegment getPv1() {
        return pv1;
    }

    public void setPv1(PV1PatientVisitSegment pv1) {
        this.pv1 = pv1;
    }

    @Override
    public List<PR1ProceduresSegment> getPr1List() {
        return pr1List;
    }

    public void setPr1List(List<PR1ProceduresSegment> pr1List) {
        this.pr1List = pr1List;
    }

    @Override
    public List<IN1InsuranceSegment> getIn1List() {
        return in1List;
    }

    public void setIn1List(List<IN1InsuranceSegment> in1List) {
        this.in1List = in1List;
    }

    public List<AdtGT1GuarantorSegment> getGt1List() {
        return gt1List;
    }

    public void setGt1List(final List<AdtGT1GuarantorSegment> gt1List) {
        this.gt1List = gt1List;
    }

    public List<AdtDG1DiagnosisSegment> getDg1List() {
        return dg1List;
    }

    public void setDg1List(final List<AdtDG1DiagnosisSegment> dg1List) {
        this.dg1List = dg1List;
    }

    @Override
    public List<AdtAL1AllergySegment> getAL1List() {
        return al1List;
    }

    @Override
    public AdtPD1AdditionalDemographicSegment getPd1() {
        return pd1;
    }

    public void setPd1(final AdtPD1AdditionalDemographicSegment pd1) {
        this.pd1 = pd1;
    }

    public List<AdtAL1AllergySegment> getAl1List() {
        return al1List;
    }

    public void setAl1List(final List<AdtAL1AllergySegment> al1List) {
        this.al1List = al1List;
    }


}
