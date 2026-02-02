package org.openhealthtools.openxds.entity.datatype;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
@Table(name = "XON_ExtendedCompositeNameAndIdForOrganizations")
public class XONExtendedCompositeNameAndIdForOrganizations {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "organization_name_type_code")
    private String organizationNameTypeCode;


    @ManyToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
    @JoinColumn(name = "assigning_authority_id")
    private HDHierarchicDesignator assigningAuthority;

    @ManyToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
    @JoinColumn(name = "assigning_facility_id")
    private HDHierarchicDesignator assigningFacility;

    @Column(name = "identifier_type_code")
    private String identifierTypeCode;

    @Column(name = "name_representation_code")
    private String nameRepresentationCode;

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
