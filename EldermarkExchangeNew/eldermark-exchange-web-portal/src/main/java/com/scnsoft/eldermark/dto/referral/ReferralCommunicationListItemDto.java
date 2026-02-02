package com.scnsoft.eldermark.dto.referral;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.referral.ReferralInfoRequest_;
import org.springframework.data.domain.Sort;

public class ReferralCommunicationListItemDto {

    private Long id;
    @DefaultSort(direction = Sort.Direction.DESC)
    @EntitySort(ReferralInfoRequest_.REQUEST_DATETIME)
    private Long requestDate;
    @EntitySort(ReferralInfoRequest_.REQUESTER_NAME)
    private String author;
    private String subject;
    private String statusName;
    @EntitySort(ReferralInfoRequest_.RESPONSE_DATETIME)
    private String statusTitle;
    private boolean canRespond;
    private boolean isRequestAvailable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Long requestDate) {
        this.requestDate = requestDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public boolean getCanRespond() {
        return canRespond;
    }

    public void setCanRespond(boolean canRespond) {
        this.canRespond = canRespond;
    }

    public boolean getRequestAvailable() {
        return isRequestAvailable;
    }

    public void setRequestAvailable(boolean requestAvailable) {
        isRequestAvailable = requestAvailable;
    }
}

