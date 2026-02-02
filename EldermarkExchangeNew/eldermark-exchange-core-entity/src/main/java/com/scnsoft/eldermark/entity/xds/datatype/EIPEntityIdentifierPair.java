package com.scnsoft.eldermark.entity.xds.datatype;

import javax.persistence.*;

@Entity
@Table(name = "EIP_EntityIdentifierPair")
public class EIPEntityIdentifierPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "placer_assigned_identifier_id")
    private EIEntityIdentifier placerAssignedIdentifier;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "filler_assigned_identifier_id")
    private EIEntityIdentifier fillerAssignedIdentifier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EIEntityIdentifier getPlacerAssignedIdentifier() {
        return placerAssignedIdentifier;
    }

    public void setPlacerAssignedIdentifier(EIEntityIdentifier placerAssignedIdentifier) {
        this.placerAssignedIdentifier = placerAssignedIdentifier;
    }

    public EIEntityIdentifier getFillerAssignedIdentifier() {
        return fillerAssignedIdentifier;
    }

    public void setFillerAssignedIdentifier(EIEntityIdentifier fillerAssignedIdentifier) {
        this.fillerAssignedIdentifier = fillerAssignedIdentifier;
    }
}
