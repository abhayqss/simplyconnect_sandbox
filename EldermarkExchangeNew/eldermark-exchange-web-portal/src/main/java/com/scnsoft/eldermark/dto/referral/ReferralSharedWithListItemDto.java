package com.scnsoft.eldermark.dto.referral;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.Organization_;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.referral.ReferralRequestResponse_;
import com.scnsoft.eldermark.entity.referral.ReferralRequest_;
import com.scnsoft.eldermark.entity.referral.Referral_;
import com.scnsoft.eldermark.utils.CustomSortUtils;
import org.springframework.data.domain.Sort;

public class ReferralSharedWithListItemDto {

    private Long id;

    @EntitySort(joined = {ReferralRequest_.COMMUNITY, Community_.ORGANIZATION, Organization_.NAME})
    private String organization;

    @EntitySort(joined = {ReferralRequest_.COMMUNITY, Community_.NAME})
    private String community;

    private String network;

    private String statusName;

    @EntitySort(joined = {ReferralRequest_.LAST_RESPONSE, ReferralRequestResponse_.RESPONSE})
    private String statusTitle;

    @DefaultSort(direction = Sort.Direction.DESC)
    @EntitySort(value = CustomSortUtils.EXPRESSION_ORDER_PREFIX + CustomSortUtils.Functions.FIRST_NON_NULL + "("
            + ReferralRequest_.LAST_RESPONSE + "." + ReferralRequestResponse_.RESPONSE_DATETIME + ", "
            + ReferralRequest_.REFERRAL + "." + Referral_.REQUEST_DATETIME + ")")
    //#expression:#firstNonNull(lastResponse.responseDatetime, referral.requestDatetime)
    //todo create better way of expression sort usage
    private Long date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
