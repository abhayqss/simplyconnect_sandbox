package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;

import java.util.List;

public class ContactMembershipDto {

    private Long clientCount;

    private List<IdentifiedTitledEntityDto> clients;

    private Long communityCount;

    private List<IdentifiedTitledEntityDto> communities;

    private Long referralsProcessingCount;

    private List<IdentifiedTitledEntityDto> referralsProcessing;


    public Long getClientCount() {
        return clientCount;
    }

    public void setClientCount(Long clientCount) {
        this.clientCount = clientCount;
    }

    public List<IdentifiedTitledEntityDto> getClients() {
        return clients;
    }

    public void setClients(List<IdentifiedTitledEntityDto> clients) {
        this.clients = clients;
    }

    public Long getCommunityCount() {
        return communityCount;
    }

    public void setCommunityCount(Long communityCount) {
        this.communityCount = communityCount;
    }

    public List<IdentifiedTitledEntityDto> getCommunities() {
        return communities;
    }

    public void setCommunities(List<IdentifiedTitledEntityDto> communities) {
        this.communities = communities;
    }

    public Long getReferralsProcessingCount() {
        return referralsProcessingCount;
    }

    public void setReferralsProcessingCount(Long referralsProcessingCount) {
        this.referralsProcessingCount = referralsProcessingCount;
    }

    public List<IdentifiedTitledEntityDto> getReferralsProcessing() {
        return referralsProcessing;
    }

    public void setReferralsProcessing(List<IdentifiedTitledEntityDto> referralsProcessing) {
        this.referralsProcessing = referralsProcessing;
    }
}
