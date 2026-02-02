package com.scnsoft.eldermark.mobile.dto.careteam.invitation;

import java.time.LocalDate;

public class ClientCareTeamInvitationDto {

    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String email;
    private String statusName;
    private String statusTitle;
    private Long expirationTime;
    private Long recipientAvatarId;
    private String recipientAvatarName;
    private Long recipientEmployeeId;
    private String clientFirstName;
    private String clientLastName;
    private Long clientAvatarId;
    private String clientAvatarName;
    private boolean canCancel;
    private boolean canResend;
    private boolean canAcceptOrDecline;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
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

    public String getClientFirstName() {
        return clientFirstName;
    }

    public void setClientFirstName(String clientFirstName) {
        this.clientFirstName = clientFirstName;
    }

    public String getClientLastName() {
        return clientLastName;
    }

    public void setClientLastName(String clientLastName) {
        this.clientLastName = clientLastName;
    }

    public Long getClientAvatarId() {
        return clientAvatarId;
    }

    public void setClientAvatarId(Long clientAvatarId) {
        this.clientAvatarId = clientAvatarId;
    }

    public String getClientAvatarName() {
        return clientAvatarName;
    }

    public void setClientAvatarName(String clientAvatarName) {
        this.clientAvatarName = clientAvatarName;
    }

    public boolean getCanCancel() {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public boolean getCanResend() {
        return canResend;
    }

    public void setCanResend(boolean canResend) {
        this.canResend = canResend;
    }

    public boolean getCanAcceptOrDecline() {
        return canAcceptOrDecline;
    }

    public void setCanAcceptOrDecline(boolean canAcceptOrDecline) {
        this.canAcceptOrDecline = canAcceptOrDecline;
    }
}
