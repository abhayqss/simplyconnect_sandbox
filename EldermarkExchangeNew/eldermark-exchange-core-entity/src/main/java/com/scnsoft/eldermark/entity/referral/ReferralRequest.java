package com.scnsoft.eldermark.entity.referral;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.network.PartnerNetwork;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ReferralRequest")
public class ReferralRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referral_id", nullable = false)
    private Referral referral;

    @Column(name = "referral_id", nullable = false, insertable = false, updatable = false)
    private Long referralId;

    @ManyToMany
    @JoinTable(name = "ReferralRequest_PartnerNetwork",
            joinColumns = @JoinColumn(name = "request_id"),
            inverseJoinColumns = @JoinColumn(name = "partner_network_id"))
    private List<PartnerNetwork> partnerNetworks;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Community community;

    @Column(name = "organization_id", insertable = false, updatable = false)
    private Long communityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_employee_id")
    private Employee assignedEmployee;

    @Enumerated(EnumType.STRING)
    @Column(name = "shared_channel")
    private ReferralRequestSharedChannel sharedChannel;

    @Column(name = "shared_fax")
    private String sharedFax;

    @Column(name = "shared_phone")
    private String sharedPhone;

    @Column(name = "shared_fax_comment")
    private String sharedFaxComment;

    @Column(name = "timezone_id")
    private String zoneId;

    @OneToOne
    @JoinColumn(name = "last_response_id")
    private ReferralRequestResponse lastResponse;

    @Column(name = "last_response_id", insertable = false, updatable = false)
    private Long lastResponseId;

    @OneToMany(mappedBy = "referralRequest")
    private List<ReferralInfoRequest> infoRequests;

    @OneToMany(mappedBy = "referralRequest")
    private List<ReferralRequestResponse> responses;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Referral getReferral() {
        return referral;
    }

    public void setReferral(Referral referral) {
        this.referral = referral;
    }

    public Long getReferralId() {
        return referralId;
    }

    public void setReferralId(Long referralId) {
        this.referralId = referralId;
    }

    public List<PartnerNetwork> getPartnerNetworks() {
        return partnerNetworks;
    }

    public void setPartnerNetworks(List<PartnerNetwork> partnerNetworks) {
        this.partnerNetworks = partnerNetworks;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public Employee getAssignedEmployee() {
        return assignedEmployee;
    }

    public void setAssignedEmployee(Employee assignedEmployee) {
        this.assignedEmployee = assignedEmployee;
    }

    public ReferralRequestSharedChannel getSharedChannel() {
        return sharedChannel;
    }

    public void setSharedChannel(ReferralRequestSharedChannel sharedChannel) {
        this.sharedChannel = sharedChannel;
    }

    public String getSharedFax() {
        return sharedFax;
    }

    public void setSharedFax(String sharedFax) {
        this.sharedFax = sharedFax;
    }

    public String getSharedPhone() {
        return sharedPhone;
    }

    public void setSharedPhone(String sharedPhone) {
        this.sharedPhone = sharedPhone;
    }

    public String getSharedFaxComment() {
        return sharedFaxComment;
    }

    public void setSharedFaxComment(String sharedFaxComment) {
        this.sharedFaxComment = sharedFaxComment;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public ReferralRequestResponse getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(ReferralRequestResponse lastResponse) {
        this.lastResponse = lastResponse;
    }

    public Long getLastResponseId() {
        return lastResponseId;
    }

    public void setLastResponseId(Long lastResponseId) {
        this.lastResponseId = lastResponseId;
    }

    public List<ReferralInfoRequest> getInfoRequests() {
        return infoRequests;
    }

    public void setInfoRequests(List<ReferralInfoRequest> infoRequests) {
        this.infoRequests = infoRequests;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public List<ReferralRequestResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<ReferralRequestResponse> responses) {
        this.responses = responses;
    }
}
