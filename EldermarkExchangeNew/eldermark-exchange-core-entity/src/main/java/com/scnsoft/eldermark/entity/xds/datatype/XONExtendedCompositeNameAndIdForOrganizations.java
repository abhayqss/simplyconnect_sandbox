package com.scnsoft.eldermark.entity.xds.datatype;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;

@Entity
@Table(name = "XON_ExtendedCompositeNameAndIdForOrganizations")
public class XONExtendedCompositeNameAndIdForOrganizations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "organization_name", columnDefinition = "nvarchar(80)")
    @Nationalized
    private String organizationName;

    @Column(name = "organization_name_type_code", columnDefinition = "nvarchar(50)")
    @Nationalized
    private String organizationNameTypeCode;

    @Column(name = "id_number", columnDefinition = "nvarchar(30)")
    @Nationalized
    private String idNumber;

    @ManyToOne
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL })
    @JoinColumn(name = "assigning_authority_id")
    private HDHierarchicDesignator assigningAuthority;

    @Column(name = "identifier_type_code", columnDefinition = "nvarchar(50)")
    @Nationalized
    private String identifierTypeCode;

    @ManyToOne
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL })
    @JoinColumn(name = "assigning_facility_id")
    private HDHierarchicDesignator assigningFacility;

    @Column(name = "name_representation_code", columnDefinition = "nvarchar(50)")
    @Nationalized
    private String nameRepresentationCode;

    public XONExtendedCompositeNameAndIdForOrganizations() {
    }

    public XONExtendedCompositeNameAndIdForOrganizations(String organizationName) {
        this.organizationName = organizationName;
    }

    public XONExtendedCompositeNameAndIdForOrganizations(String organizationName, String organizationNameTypeCode,
                                                         String idNumber, HDHierarchicDesignator assigningAuthority,
                                                         String identifierTypeCode, HDHierarchicDesignator assigningFacility,
                                                         String nameRepresentationCode) {
        this.organizationName = organizationName;
        this.organizationNameTypeCode = organizationNameTypeCode;
        this.idNumber = idNumber;
        this.assigningAuthority = assigningAuthority;
        this.identifierTypeCode = identifierTypeCode;
        this.assigningFacility = assigningFacility;
        this.nameRepresentationCode = nameRepresentationCode;
    }

    public Long getId() {
        return id;
    }

    public void etId(Long id) {
        this.id = id;
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

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationNameTypeCode() {
        return organizationNameTypeCode;
    }

    public void setOrganizationNameTypeCode(String organizationNameTypeCode) {
        this.organizationNameTypeCode = organizationNameTypeCode;
    }

    public String getNameRepresentationCode() {
        return nameRepresentationCode;
    }

    public void setNameRepresentationCode(String nameRepresentationCode) {
        this.nameRepresentationCode = nameRepresentationCode;
    }

}
