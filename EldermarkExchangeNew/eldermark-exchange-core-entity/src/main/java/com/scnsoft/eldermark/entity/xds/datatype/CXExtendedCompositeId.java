package com.scnsoft.eldermark.entity.xds.datatype;

import javax.persistence.*;

@Entity
@Table(name = "CX_ExtendedCompositeId")
public class CXExtendedCompositeId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "p_id")
    private String pId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "assigning_authority_id")
    private HDHierarchicDesignator assigningAuthority;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "assigning_facility_id")
    private HDHierarchicDesignator assigningFacility;

    @Column(name = "identifier_type_code")
    private String identifierTypeCode;

    public CXExtendedCompositeId() {
    }

    public CXExtendedCompositeId(String pId) {
        this.pId = pId;
    }

    public CXExtendedCompositeId(String pId, HDHierarchicDesignator assigningAuthority, HDHierarchicDesignator assigningFacility, String identifierTypeCode) {
        this.pId = pId;
        this.assigningAuthority = assigningAuthority;
        this.assigningFacility = assigningFacility;
        this.identifierTypeCode = identifierTypeCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public HDHierarchicDesignator getAssigningAuthority() {
        return assigningAuthority;
    }

    public void setAssigningAuthority(HDHierarchicDesignator assigningAuthority) {
        this.assigningAuthority = assigningAuthority;
    }

    public HDHierarchicDesignator getAssigningFacility() {
        return assigningFacility;
    }

    public void setAssigningFacility(HDHierarchicDesignator assigningFacility) {
        this.assigningFacility = assigningFacility;
    }

    public String getIdentifierTypeCode() {
        return identifierTypeCode;
    }

    public void setIdentifierTypeCode(String identifierTypeCode) {
        this.identifierTypeCode = identifierTypeCode;
    }

}
