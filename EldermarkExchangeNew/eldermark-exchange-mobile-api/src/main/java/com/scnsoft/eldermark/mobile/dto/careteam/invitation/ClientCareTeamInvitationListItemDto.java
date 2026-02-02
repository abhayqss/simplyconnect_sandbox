package com.scnsoft.eldermark.mobile.dto.careteam.invitation;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import org.springframework.data.domain.Sort;

public class ClientCareTeamInvitationListItemDto {

    private Long id;

    private Long recipientAvatarId;
    private String recipientAvatarName;
    private Long recipientEmployeeId;
    private String firstName;
    private String lastName;

    private String statusName;
    private String statusTitle;

    @DefaultSort(direction = Sort.Direction.DESC)
    private Long createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecipientAvatarId() {
        return recipientAvatarId;
    }

    public void setRecipientAvatarId(Long recipientAvatarId) {
        this.recipientAvatarId = recipientAvatarId;
    }

    public String getRecipientAvatarName() {
        return recipientAvatarName;
    }

    public void setRecipientAvatarName(String recipientAvatarName) {
        this.recipientAvatarName = recipientAvatarName;
    }

    public Long getRecipientEmployeeId() {
        return recipientEmployeeId;
    }

    public void setRecipientEmployeeId(Long recipientEmployeeId) {
        this.recipientEmployeeId = recipientEmployeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
