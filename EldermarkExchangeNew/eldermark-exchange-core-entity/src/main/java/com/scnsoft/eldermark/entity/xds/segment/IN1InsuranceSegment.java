package com.scnsoft.eldermark.entity.xds.segment;

import com.scnsoft.eldermark.entity.xds.datatype.*;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0098TypeOfAgreement;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "IN1_Insurance")
public class IN1InsuranceSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "set_id")
    private String setId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "insurance_plan_id")
    private CECodedElement insurancePlanId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "insurance_company_id")
    private CXExtendedCompositeId insuranceCompanyId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "insurance_company_name_id")
    private XONExtendedCompositeNameAndIdForOrganizations insuranceCompanyName;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "IN1_Insurance_InsuranceCompanyAddress", joinColumns = @JoinColumn(name = "in1_id"), inverseJoinColumns = @JoinColumn(name = "xad_id"))
    private List<XADPatientAddress> insuranceCompanyAddresses;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "IN1_Insurance_InsuranceCoPhoneNumber", joinColumns = @JoinColumn(name = "in1_id"), inverseJoinColumns = @JoinColumn(name = "xtn_id"))
    private List<XTNPhoneNumber> insuranceCoPhoneNumbers;

    @Column(name = "group_number")
    private String groupNumber;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "IN1_Insurance_GroupName", joinColumns = @JoinColumn(name = "in1_id"), inverseJoinColumns = @JoinColumn(name = "xon_id"))
    private List<XONExtendedCompositeNameAndIdForOrganizations> groupNames;

    @Column(name = "plan_effective_date")
    private Instant planEffectiveDate;

    @Column(name = "plan_expiration_date")
    private Instant planExpirationDate;

    @Column(name = "plan_type")
    private String planType;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "IN1_Insurance_NameOfInsured", joinColumns = @JoinColumn(name = "in1_id"), inverseJoinColumns = @JoinColumn(name = "xpn_id"))
    private List<XPNPersonName> namesOfInsured;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "insured_s_relationship_to_patient_id")
    private CECodedElement insuredsRelationshipToPatient;

    @Column(name = "insureds_date_of_birth")
    private Instant insuredsDateOfBirth;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "IN1_Insurance_InsuredsAddress", joinColumns = @JoinColumn(name = "in1_id"), inverseJoinColumns = @JoinColumn(name = "xad_id"))
    private List<XADPatientAddress> insuredsAddresses;

    @Column(name = "pre_admit_cert")
    private String preAdmitCert;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "type_of_agreement_code_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0098TypeOfAgreement> typeOfAgreementCode;

    @Column(name = "policy_number")
    private String policyNumber;

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

    public CECodedElement getInsurancePlanId() {
        return insurancePlanId;
    }

    public void setInsurancePlanId(CECodedElement insurancePlanId) {
        this.insurancePlanId = insurancePlanId;
    }

    public CXExtendedCompositeId getInsuranceCompanyId() {
        return insuranceCompanyId;
    }

    public void setInsuranceCompanyId(CXExtendedCompositeId insuranceCompanyId) {
        this.insuranceCompanyId = insuranceCompanyId;
    }

    public XONExtendedCompositeNameAndIdForOrganizations getInsuranceCompanyName() {
        return insuranceCompanyName;
    }

    public void setInsuranceCompanyName(XONExtendedCompositeNameAndIdForOrganizations insuranceCompanyName) {
        this.insuranceCompanyName = insuranceCompanyName;
    }

    public List<XADPatientAddress> getInsuranceCompanyAddresses() {
        return insuranceCompanyAddresses;
    }

    public void setInsuranceCompanyAddresses(List<XADPatientAddress> insuranceCompanyAddresses) {
        this.insuranceCompanyAddresses = insuranceCompanyAddresses;
    }

    public List<XTNPhoneNumber> getInsuranceCoPhoneNumbers() {
        return insuranceCoPhoneNumbers;
    }

    public void setInsuranceCoPhoneNumbers(List<XTNPhoneNumber> insuranceCoPhoneNumbers) {
        this.insuranceCoPhoneNumbers = insuranceCoPhoneNumbers;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public List<XONExtendedCompositeNameAndIdForOrganizations> getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(List<XONExtendedCompositeNameAndIdForOrganizations> groupNames) {
        this.groupNames = groupNames;
    }

    public Instant getPlanEffectiveDate() {
        return planEffectiveDate;
    }

    public void setPlanEffectiveDate(Instant planEffectiveDate) {
        this.planEffectiveDate = planEffectiveDate;
    }

    public Instant getPlanExpirationDate() {
        return planExpirationDate;
    }

    public void setPlanExpirationDate(Instant planExpirationDate) {
        this.planExpirationDate = planExpirationDate;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public List<XPNPersonName> getNamesOfInsured() {
        return namesOfInsured;
    }

    public void setNamesOfInsured(List<XPNPersonName> namesOfInsured) {
        this.namesOfInsured = namesOfInsured;
    }

    public CECodedElement getInsuredsRelationshipToPatient() {
        return insuredsRelationshipToPatient;
    }

    public void setInsuredsRelationshipToPatient(CECodedElement insuredsRelationshipToPatient) {
        this.insuredsRelationshipToPatient = insuredsRelationshipToPatient;
    }

    public Instant getInsuredsDateOfBirth() {
        return insuredsDateOfBirth;
    }

    public void setInsuredsDateOfBirth(Instant insuredsDateOfBirth) {
        this.insuredsDateOfBirth = insuredsDateOfBirth;
    }

    public List<XADPatientAddress> getInsuredsAddresses() {
        return insuredsAddresses;
    }

    public void setInsuredsAddresses(List<XADPatientAddress> insuredsAddresses) {
        this.insuredsAddresses = insuredsAddresses;
    }

    public String getPreAdmitCert() {
        return preAdmitCert;
    }

    public void setPreAdmitCert(String preAdmitCert) {
        this.preAdmitCert = preAdmitCert;
    }

    public ISCodedValueForUserDefinedTables<HL7CodeTable0098TypeOfAgreement> getTypeOfAgreementCode() {
        return typeOfAgreementCode;
    }

    public void setTypeOfAgreementCode(ISCodedValueForUserDefinedTables<HL7CodeTable0098TypeOfAgreement> typeOfAgreementCode) {
        this.typeOfAgreementCode = typeOfAgreementCode;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
}
