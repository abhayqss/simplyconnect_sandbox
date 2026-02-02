package com.scnsoft.eldermark.dto.referral;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.referral.*;
import org.springframework.data.domain.Sort;

import java.util.List;

public class ReferralListItemDto {
    private Long id;
    private Long requestId;

    @EntitySort.List(
            {
                    @EntitySort(joined = {Referral_.CLIENT, Client_.FIRST_NAME}, entity = Referral.class),
                    @EntitySort(joined = {Referral_.CLIENT, Client_.LAST_NAME}, entity = Referral.class),

                    @EntitySort(joined = {ReferralRequest_.REFERRAL, Referral_.CLIENT, Client_.FIRST_NAME}, entity = ReferralRequest.class),
                    @EntitySort(joined = {ReferralRequest_.REFERRAL, Referral_.CLIENT, Client_.LAST_NAME}, entity = ReferralRequest.class)
            }
    )
    private String name;

    private String priorityName;

    @EntitySort.List(
            {
                    @EntitySort(joined = {Referral_.PRIORITY, ReferralPriority_.DISPLAY_NAME}, entity = Referral.class),
                    @EntitySort(joined = {ReferralRequest_.REFERRAL, Referral_.PRIORITY, ReferralPriority_.DISPLAY_NAME},
                            entity = ReferralRequest.class)
            }
    )
    private String priorityTitle;

    @EntitySort.List(
            {
                    @EntitySort(value = Referral_.REFERRAL_STATUS, entity = Referral.class),
                    @EntitySort(joined = {ReferralRequest_.REFERRAL, Referral_.REFERRAL_STATUS}, entity = ReferralRequest.class)
            }
    )
    private String statusName;
    private String statusTitle;

    @EntitySort.List(
            {
                    @EntitySort(value = Referral_.SERVICE_NAME, entity = Referral.class),
                    @EntitySort(joined = {ReferralRequest_.REFERRAL, Referral_.SERVICE_NAME}, entity = ReferralRequest.class)
            }
    )
    private String serviceTitle;
    private String referredBy;
    private List<String> referredTo;

    @DefaultSort(direction = Sort.Direction.DESC)
    @EntitySort.List(
            {
                    @EntitySort(value = Referral_.REQUEST_DATETIME, entity = Referral.class),
                    @EntitySort(joined = {ReferralRequest_.REFERRAL, Referral_.REQUEST_DATETIME}, entity = ReferralRequest.class)
            }
    )
    private Long date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    public String getPriorityTitle() {
        return priorityTitle;
    }

    public void setPriorityTitle(String priorityTitle) {
        this.priorityTitle = priorityTitle;
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


    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public String getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(String referredBy) {
        this.referredBy = referredBy;
    }

    public List<String> getReferredTo() {
        return referredTo;
    }

    public void setReferredTo(List<String> referredTo) {
        this.referredTo = referredTo;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

}
