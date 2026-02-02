package com.scnsoft.eldermark.entity.xds.segment;

import com.scnsoft.eldermark.entity.xds.datatype.*;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0001AdministrativeSex;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0066EmploymentStatus;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "ADT_SGMNT_GT1_Guarantor")
public class AdtGT1GuarantorSegment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "set_id")
    private String setId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "ADT_FIELD_GT1_GuarantorNumber",
            joinColumns = @JoinColumn(name = "gt1_id"),
            inverseJoinColumns = @JoinColumn(name = "cx_id")
    )
    private List<CXExtendedCompositeId> guarantorNumbers;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "ADT_FIELD_GT1_GuarantorName",
            joinColumns = @JoinColumn(name = "GT1_Id"),
            inverseJoinColumns = @JoinColumn(name = "XPN_Id")
    )
    private List<XPNPersonName> guarantorNameList;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "ADT_FIELD_GT1_GuarantorAddress",
            joinColumns = @JoinColumn(name = "GT1_Id"),
            inverseJoinColumns = @JoinColumn(name = "XAD_Id")
    )
    private List<XADPatientAddress> guarantorAddressList;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "ADT_FIELD_GT1_GuarantorPhNumPhone",
            joinColumns = @JoinColumn(name = "GT1_Id"),
            inverseJoinColumns = @JoinColumn(name = "XTN_Id")
    )
    private List<XTNPhoneNumber> guarantorPhNumHomeList;

    @Column(name = "guarantor_datetime_of_birth")
    private Instant guarantorDatetimeOfBirth;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "guarantor_administrative_sex_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0001AdministrativeSex> guarantorAdministrativeSex;

    @Column(name = "guarantor_type")
    private String guarantorType;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "guarantor_relationship_id")
    private CECodedElement guarantorRelationship;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "guarantor_employment_status_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0066EmploymentStatus> guarantorEmploymentStatus;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "primary_language_id")
    private CECodedElement primaryLanguage;

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

    public List<CXExtendedCompositeId> getGuarantorNumbers() {
        return guarantorNumbers;
    }

    public void setGuarantorNumbers(List<CXExtendedCompositeId> guarantorNumbers) {
        this.guarantorNumbers = guarantorNumbers;
    }

    public List<XPNPersonName> getGuarantorNameList() {
        return guarantorNameList;
    }

    public void setGuarantorNameList(List<XPNPersonName> guarantorNameList) {
        this.guarantorNameList = guarantorNameList;
    }

    public List<XADPatientAddress> getGuarantorAddressList() {
        return guarantorAddressList;
    }

    public void setGuarantorAddressList(List<XADPatientAddress> guarantorAddressList) {
        this.guarantorAddressList = guarantorAddressList;
    }

    public List<XTNPhoneNumber> getGuarantorPhNumHomeList() {
        return guarantorPhNumHomeList;
    }

    public void setGuarantorPhNumHomeList(List<XTNPhoneNumber> guarantorPhNumHomeList) {
        this.guarantorPhNumHomeList = guarantorPhNumHomeList;
    }

    public Instant getGuarantorDatetimeOfBirth() {
        return guarantorDatetimeOfBirth;
    }

    public void setGuarantorDatetimeOfBirth(Instant guarantorDatetimeOfBirth) {
        this.guarantorDatetimeOfBirth = guarantorDatetimeOfBirth;
    }

    public ISCodedValueForUserDefinedTables<HL7CodeTable0001AdministrativeSex> getGuarantorAdministrativeSex() {
        return guarantorAdministrativeSex;
    }

    public void setGuarantorAdministrativeSex(ISCodedValueForUserDefinedTables<HL7CodeTable0001AdministrativeSex> guarantorAdministrativeSex) {
        this.guarantorAdministrativeSex = guarantorAdministrativeSex;
    }

    public String getGuarantorType() {
        return guarantorType;
    }

    public void setGuarantorType(String guarantorType) {
        this.guarantorType = guarantorType;
    }

    public CECodedElement getGuarantorRelationship() {
        return guarantorRelationship;
    }

    public void setGuarantorRelationship(CECodedElement guarantorRelationship) {
        this.guarantorRelationship = guarantorRelationship;
    }

    public ISCodedValueForUserDefinedTables<HL7CodeTable0066EmploymentStatus> getGuarantorEmploymentStatus() {
        return guarantorEmploymentStatus;
    }

    public void setGuarantorEmploymentStatus(ISCodedValueForUserDefinedTables<HL7CodeTable0066EmploymentStatus> guarantorEmploymentStatus) {
        this.guarantorEmploymentStatus = guarantorEmploymentStatus;
    }

    public CECodedElement getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(CECodedElement primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }
}
