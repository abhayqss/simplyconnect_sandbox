package com.scnsoft.eldermark.entity.xds.datatype;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;

@Entity
@Table(name = "XCN_ExtendedCompositeIdNumberAndNameForPersons")
public class XCNExtendedCompositeIdNumberAndNameForPersons {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    //@Nationalized
    @Column(name = "last_name")
    private String lastName;

    //@Nationalized
    @Column(name = "first_name")
    private String firstName;

    //@Nationalized
    @Column(name = "middle_name")
    private String middleName;

    //@Nationalized
    @Column(name = "suffix")
    private String suffix;

    //@Nationalized
    @Column(name = "prefix")
    private String prefix;

    //@Nationalized
    @Column(name = "degree")
    private String degree;

    //@Nationalized
    @Column(name = "source_table")
    private String sourceTable;

    @ManyToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
    @JoinColumn(name = "assigning_authority_id")
    private HDHierarchicDesignator assigningAuthority;

    @ManyToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
    @JoinColumn(name = "assigning_facility_id")
    private HDHierarchicDesignator assigningFacility;

    //@Nationalized
    @Column(name = "name_type_code")
    private String nameTypeCode;

    //@Nationalized
    @Column(name = "identifier_type_code")
    private String identifierTypeCode;

    //@Nationalized
    @Column(name = "name_representation_code")
    private String nameRepresentationCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
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

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getNameTypeCode() {
        return nameTypeCode;
    }

    public void setNameTypeCode(String nameTypeCode) {
        this.nameTypeCode = nameTypeCode;
    }

    public String getNameRepresentationCode() {
        return nameRepresentationCode;
    }

    public void setNameRepresentationCode(String nameRepresentationCode) {
        this.nameRepresentationCode = nameRepresentationCode;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
