package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Entity
public class PolicyActivity extends LegacyIdAwareEntity {
    @Column(name = "sequence_number")
    private BigInteger sequenceNumber;

    @ManyToOne
    @JoinColumn(name = "health_insurance_type_code_id")
    private CcdCode healthInsuranceTypeCode;

    @ManyToOne
    @JoinColumn(name = "payer_financially_responsible_party_code_id")
    private CcdCode payerFinanciallyResponsiblePartyCode;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "payer_org_id")
    private Organization payerOrganization;

    @Column(name = "guarantor_time")
    private Date guarantorTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "guarantor_organization_id")
    private Organization guarantorOrganization;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "guarantor_person_id")
    private Person guarantorPerson;

    @Lob
    @Column(name = "participant_member_id")
    private String participantMemberId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "participant_id")
    private Participant participant;

    @Column(name = "participant_date_of_birth")
    private Date participantDateOfBirth;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Participant subscriber;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(nullable = false)
    private Payer payer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "policyActivity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuthorizationActivity> authorizationActivities;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "policyActivity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoveragePlanDescription> coveragePlanDescriptions;

//    @Lob
//    private String text;
//
//

    /**
     * Payment priority between policies
     */
    public BigInteger getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(BigInteger sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public CcdCode getHealthInsuranceTypeCode() {
        return healthInsuranceTypeCode;
    }

    public void setHealthInsuranceTypeCode(CcdCode healthInsuranceTypeCode) {
        this.healthInsuranceTypeCode = healthInsuranceTypeCode;
    }

//    public String getPayerId() {
//        return payerId;
//    }
//
//    public void setPayerId(String payerId) {
//        this.payerId = payerId;
//    }

    public CcdCode getPayerFinanciallyResponsiblePartyCode() {
        return payerFinanciallyResponsiblePartyCode;
    }

    public void setPayerFinanciallyResponsiblePartyCode(CcdCode payerFinanciallyResponsiblePartyCode) {
        this.payerFinanciallyResponsiblePartyCode = payerFinanciallyResponsiblePartyCode;
    }

    public Organization getPayerOrganization() {
        return payerOrganization;
    }

    public void setPayerOrganization(Organization payerOrganization) {
        this.payerOrganization = payerOrganization;
    }

    public Date getGuarantorTime() {
        return guarantorTime;
    }

    public void setGuarantorTime(Date guarantorTime) {
        this.guarantorTime = guarantorTime;
    }

    public Organization getGuarantorOrganization() {
        return guarantorOrganization;
    }

    public void setGuarantorOrganization(Organization guarantorOrganization) {
        this.guarantorOrganization = guarantorOrganization;
    }

    public Person getGuarantorPerson() {
        return guarantorPerson;
    }

    public void setGuarantorPerson(Person guarantorPerson) {
        this.guarantorPerson = guarantorPerson;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Date getParticipantDateOfBirth() {
        return participantDateOfBirth;
    }

    public void setParticipantDateOfBirth(Date participantDateOfBirth) {
        this.participantDateOfBirth = participantDateOfBirth;
    }

    public Participant getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Participant subscriber) {
        this.subscriber = subscriber;
    }

    public Payer getPayer() {
        return payer;
    }

    public void setPayer(Payer payer) {
        this.payer = payer;
    }

    public List<AuthorizationActivity> getAuthorizationActivities() {
        return authorizationActivities;
    }

    public void setAuthorizationActivities(List<AuthorizationActivity> authorizationActivities) {
        this.authorizationActivities = authorizationActivities;
    }

    public List<CoveragePlanDescription> getCoveragePlanDescriptions() {
        return coveragePlanDescriptions;
    }

    public void setCoveragePlanDescriptions(List<CoveragePlanDescription> coveragePlanDescriptions) {
        this.coveragePlanDescriptions = coveragePlanDescriptions;
    }

    public String getParticipantMemberId() {
        return participantMemberId;
    }

    public void setParticipantMemberId(String participantMemberId) {
        this.participantMemberId = participantMemberId;
    }
}
