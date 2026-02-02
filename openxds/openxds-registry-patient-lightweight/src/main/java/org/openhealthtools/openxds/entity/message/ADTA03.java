package org.openhealthtools.openxds.entity.message;

import org.hibernate.annotations.Cascade;
import org.openhealthtools.openxds.entity.segment.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ADT_A03")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
public class ADTA03 extends AdtMessage implements
        MSHSegmentContainingMessage,
        EVNSegmentContainingMessage,
        PIDSegmentContainingMessage,
        PV1SegmentContainingMessage,
        PR1ListSegmentContaingMessage,
        PD1SegmentContainingMessage,
        DG1ListSegmentContainingMessage
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

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "pd1_id")
    private AdtPD1AdditionalDemographicSegment pd1;

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

    public List<AdtDG1DiagnosisSegment> getDg1List() {
        return dg1List;
    }

    public void setDg1List(final List<AdtDG1DiagnosisSegment> dg1List) {
        this.dg1List = dg1List;
    }

    @Override
    public AdtPD1AdditionalDemographicSegment getPd1() {
        return pd1;
    }

    public void setPd1(final AdtPD1AdditionalDemographicSegment pd1) {
        this.pd1 = pd1;
    }
}
