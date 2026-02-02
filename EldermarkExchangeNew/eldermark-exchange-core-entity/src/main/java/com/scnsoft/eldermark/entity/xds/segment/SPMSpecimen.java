package com.scnsoft.eldermark.entity.xds.segment;

import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.datatype.DRDateRange;
import com.scnsoft.eldermark.entity.xds.datatype.EIPEntityIdentifierPair;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "SPM_Specimen")
public class SPMSpecimen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "set_id")
    private String setId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "specimen_ID_id")
    private EIPEntityIdentifierPair specimenID;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "specimen_type_id")
    private CECodedElement specimenType;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "specimen_collection_datetime_id")
    private DRDateRange specimenCollectionDatetime;

    @Column(name = "specimen_received_datetime")
    private Instant specimenReceivedDatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public EIPEntityIdentifierPair getSpecimenID() {
        return specimenID;
    }

    public void setSpecimenID(EIPEntityIdentifierPair specimenID) {
        this.specimenID = specimenID;
    }

    public CECodedElement getSpecimenType() {
        return specimenType;
    }

    public void setSpecimenType(CECodedElement specimenType) {
        this.specimenType = specimenType;
    }

    public DRDateRange getSpecimenCollectionDatetime() {
        return specimenCollectionDatetime;
    }

    public void setSpecimenCollectionDatetime(DRDateRange specimenCollectionDatetime) {
        this.specimenCollectionDatetime = specimenCollectionDatetime;
    }

    public Instant getSpecimenReceivedDatetime() {
        return specimenReceivedDatetime;
    }

    public void setSpecimenReceivedDatetime(Instant specimenReceivedDatetime) {
        this.specimenReceivedDatetime = specimenReceivedDatetime;
    }
}
