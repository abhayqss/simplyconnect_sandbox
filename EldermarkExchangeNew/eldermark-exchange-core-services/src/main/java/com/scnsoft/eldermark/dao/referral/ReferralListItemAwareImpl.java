package com.scnsoft.eldermark.dao.referral;

import com.scnsoft.eldermark.entity.referral.ReferralStatus;

import java.time.Instant;
import java.util.List;

public class ReferralListItemAwareImpl implements ReferralListItemAware {
    private Long id;
    private Long clientId;
    private String clientFirstName;
    private String clientLastName;
    private String requestingEmployeeFirstName;
    private String requestingEmployeeLastName;
    private String serviceName;
    private Instant requestDatetime;
    private List<String> referralRequestsCommunityNames;
    private String priorityCode;
    private String priorityDisplayName;
    private ReferralStatus referralStatus;

    public ReferralListItemAwareImpl(ReferralListItemAware aware) {
        id = aware.getId();
        clientId = aware.getClientId();
        clientFirstName = aware.getClientFirstName();
        clientLastName = aware.getClientLastName();
        serviceName = aware.getServiceName();
        requestDatetime = aware.getRequestDatetime();
        priorityCode = aware.getPriorityCode();
        priorityDisplayName = aware.getPriorityDisplayName();
        referralStatus = aware.getReferralStatus();
        requestingEmployeeFirstName = aware.getRequestingEmployeeFirstName();
        requestingEmployeeLastName = aware.getRequestingEmployeeLastName();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    @Override
    public String getClientFirstName() {
        return clientFirstName;
    }

    public void setClientFirstName(String clientFirstName) {
        this.clientFirstName = clientFirstName;
    }

    @Override
    public String getClientLastName() {
        return clientLastName;
    }

    public void setClientLastName(String clientLastName) {
        this.clientLastName = clientLastName;
    }

    @Override
    public String getRequestingEmployeeFirstName() {
        return requestingEmployeeFirstName;
    }

    public void setRequestingEmployeeFirstName(String requestingEmployeeFirstName) {
        this.requestingEmployeeFirstName = requestingEmployeeFirstName;
    }

    @Override
    public String getRequestingEmployeeLastName() {
        return requestingEmployeeLastName;
    }

    public void setRequestingEmployeeLastName(String requestingEmployeeLastName) {
        this.requestingEmployeeLastName = requestingEmployeeLastName;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public Instant getRequestDatetime() {
        return requestDatetime;
    }

    public void setRequestDatetime(Instant requestDatetime) {
        this.requestDatetime = requestDatetime;
    }

    @Override
    public List<String> getReferralRequestsCommunityName() {
        return referralRequestsCommunityNames;
    }

    public void setReferralRequestsCommunityNames(List<String> referralRequestsCommunityNames) {
        this.referralRequestsCommunityNames = referralRequestsCommunityNames;
    }

    @Override
    public String getPriorityCode() {
        return priorityCode;
    }

    public void setPriorityCode(String priorityCode) {
        this.priorityCode = priorityCode;
    }

    @Override
    public String getPriorityDisplayName() {
        return priorityDisplayName;
    }

    public void setPriorityDisplayName(String priorityDisplayName) {
        this.priorityDisplayName = priorityDisplayName;
    }

    @Override
    public ReferralStatus getReferralStatus() {
        return referralStatus;
    }

    public void setReferralStatus(ReferralStatus referralStatus) {
        this.referralStatus = referralStatus;
    }
}
