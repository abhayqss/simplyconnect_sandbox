package com.scnsoft.eldermark.dto.lab;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.entity.lab.LabResearchOrder_;
import org.springframework.data.domain.Sort;

public class LabResearchOrderListItemDto {

    private Long id;
    private Long clientId;
    @EntitySort.List(
            {
                    @EntitySort(joined = {LabResearchOrder_.CLIENT, Client_.FIRST_NAME}),
                    @EntitySort(joined = {LabResearchOrder_.CLIENT, Client_.LAST_NAME})
            }
    )
    private String clientName;
    @EntitySort(joined = {LabResearchOrder_.CLIENT, Client_.COMMUNITY, Community_.NAME})
    private String community;
    private String requisitionNumber;
    @EntitySort(LabResearchOrder_.REASON)
    private String reason;
    @EntitySort(LabResearchOrder_.STATUS)
    private String statusName;
    private String statusTitle;
    @EntitySort.List(
            {
                    @EntitySort(joined = {LabResearchOrder_.CREATED_BY, Employee_.FIRST_NAME}),
                    @EntitySort(joined = {LabResearchOrder_.CREATED_BY, Employee_.LAST_NAME})
            }
    )
    private String createdByName;

    @EntitySort(LabResearchOrder_.CREATED_DATE)
    @DefaultSort(direction = Sort.Direction.DESC)
    private Long createdDate;
    private Long avatarId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getRequisitionNumber() {
        return requisitionNumber;
    }

    public void setRequisitionNumber(String requisitionNumber) {
        this.requisitionNumber = requisitionNumber;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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


    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }
}
