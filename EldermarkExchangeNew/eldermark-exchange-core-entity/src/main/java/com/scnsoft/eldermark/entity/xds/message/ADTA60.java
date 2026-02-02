package com.scnsoft.eldermark.entity.xds.message;

import com.scnsoft.eldermark.entity.xds.segment.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ADT_A60")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
public class ADTA60 extends AdtMessage implements
        PIDSegmentContainingMessage,
        EVNSegmentContainingMessage,
        PV1SegmentContainingMessage,
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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "ADT_MSG2SGMNT_A60_TO_AL1",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "segment_id")
    )
    private List<AdtAL1AllergySegment> al1List;

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

    @Override
    public PV1ClientVisitSegment getPv1() {
        return pv1;
    }

    public void setPv1(PV1ClientVisitSegment pv1) {
        this.pv1 = pv1;
    }

    @Override
    public List<AdtAL1AllergySegment> getAL1List() {
        return al1List;
    }

    public void setAl1List(List<AdtAL1AllergySegment> al1List) {
        this.al1List = al1List;
    }
}
