package com.scnsoft.eldermark.entity.xds.segment;

import com.scnsoft.eldermark.entity.xds.datatype.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "IN1_Insurance")
public class IN1InsuranceSegment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "set_id")
    private String setId;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "insurance_plan_id")
    private CECodedElement insurancePlanId;

    @ManyToOne
    @JoinColumn(name = "insurance_company_id")
    private CXExtendedCompositeId insuranceCompanyId;

    @ManyToOne
    @JoinColumn(name = "insurance_company_name_id")
    private XONExtendedCompositeNameAndIdForOrganizations insuranceCompanyName;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name="IN1_Insurance_InsuranceCoPhoneNumber",
            joinColumns = @JoinColumn(name = "in1_id"),
            inverseJoinColumns = @JoinColumn(name = "xtn_id"))
    private List<XTNPhoneNumber> insuranceCoPhoneNumbers;

    @Column(name = "group_number")
    private String groupNumber;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name="IN1_Insurance_GroupName",
            joinColumns = @JoinColumn(name = "in1_id"),
            inverseJoinColumns = @JoinColumn(name = "xon_id"))
    private List<XONExtendedCompositeNameAndIdForOrganizations> groupNames;

    @Column(name = "plan_effective_date")
    private Date planEffectiveDate;

    @Column(name = "plan_expiration_date")
    private Date planExpirationDate;

    @Column(name = "plan_type")
    private String planType;


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)

    @JoinTable(name="IN1_Insurance_NameOfInsured",
            joinColumns = @JoinColumn(name = "in1_id"),
            inverseJoinColumns = @JoinColumn(name = "xpn_id"))
    private List<XPNPersonName> namesOfInsured;

    @OneToOne
    @JoinColumn(name = "insured_s_relationship_to_patient_id")
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private CECodedElement insuredsRelationshipToPatient;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

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

    public Date getPlanEffectiveDate() {
        return planEffectiveDate;
    }

    public void setPlanEffectiveDate(Date planEffectiveDate) {
        this.planEffectiveDate = planEffectiveDate;
    }

    public Date getPlanExpirationDate() {
        return planExpirationDate;
    }

    public void setPlanExpirationDate(Date planExpirationDate) {
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
}
