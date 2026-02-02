package com.scnsoft.eldermark.entity.xds.datatype;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;

@Entity
@Table(name = "XCN_ExtendedCompositeIdNumberAndNameForPersons")
public class XCNExtendedCompositeIdNumberAndNameForPersons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "last_name", columnDefinition = "nvarchar(80)")
    @Nationalized
    private String lastName;

    @Column(name = "first_name", columnDefinition = "nvarchar(80)")
    @Nationalized
    private String firstName;

    @Column(name = "middle_name", columnDefinition = "nvarchar(80)")
    @Nationalized
    private String middleName;

    @Column(name = "suffix", columnDefinition = "nvarchar(20)")
    @Nationalized
    private String suffix;

    @Column(name = "prefix", columnDefinition = "nvarchar(20)")
    @Nationalized
    private String prefix;

    @Column(name = "degree", columnDefinition = "nvarchar(50)")
    @Nationalized
    private String degree;

    @Column(name = "source_table", columnDefinition = "nvarchar(50)")
    @Nationalized
    private String sourceTable;

    @ManyToOne
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL })
    @JoinColumn(name = "assigning_authority_id")
    private HDHierarchicDesignator assigningAuthority;

    @Column(name = "name_type_code", columnDefinition = "nvarchar(50)")
    @Nationalized
    private String nameTypeCode;

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

    public XCNExtendedCompositeIdNumberAndNameForPersons() {
    }

    public XCNExtendedCompositeIdNumberAndNameForPersons(String idNumber, String lastName, String firstName, String middleName) {
        this.idNumber = idNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
    }

    public XCNExtendedCompositeIdNumberAndNameForPersons(String idNumber, String lastName, String firstName,
                                                         String middleName, String suffix, String prefix, String degree,
                                                         String sourceTable, HDHierarchicDesignator assigningAuthority,
                                                         String nameTypeCode, String identifierTypeCode,
                                                         HDHierarchicDesignator assigningFacility, String nameRepresentationCode) {
        this.idNumber = idNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.suffix = suffix;
        this.prefix = prefix;
        this.degree = degree;
        this.sourceTable = sourceTable;
        this.assigningAuthority = assigningAuthority;
        this.nameTypeCode = nameTypeCode;
        this.identifierTypeCode = identifierTypeCode;
        this.assigningFacility = assigningFacility;
        this.nameRepresentationCode = nameRepresentationCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
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
}
