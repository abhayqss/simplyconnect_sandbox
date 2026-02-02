package com.scnsoft.eldermark.mobile.dto;

import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.mobile.dto.client.ClientPrimaryContactDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ClientHieConsentPolicyDto {

    @ApiModelProperty(readOnly = true)
    private Long clientId;

    @ApiModelProperty(readOnly = true)
    private String clientFullName;

    @ApiModelProperty(readOnly = true)
    private String clientCommunityName;

    @ApiModelProperty(readOnly = true)
    private ClientPrimaryContactDto clientPrimaryContact;

    private HieConsentPolicyType hieConsentPolicy;

    @ApiModelProperty(readOnly = true)
    private boolean isConfirmed;

    @ApiModelProperty(readOnly = true)
    private boolean canEdit;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientFullName() {
        return clientFullName;
    }

    public void setClientFullName(String clientFullName) {
        this.clientFullName = clientFullName;
    }

    public String getClientCommunityName() {
        return clientCommunityName;
    }

    public void setClientCommunityName(String clientCommunityName) {
        this.clientCommunityName = clientCommunityName;
    }

    public ClientPrimaryContactDto getClientPrimaryContact() {
        return clientPrimaryContact;
    }

    public void setClientPrimaryContact(ClientPrimaryContactDto clientPrimaryContact) {
        this.clientPrimaryContact = clientPrimaryContact;
    }

    public HieConsentPolicyType getHieConsentPolicy() {
        return hieConsentPolicy;
    }

    public void setHieConsentPolicy(HieConsentPolicyType hieConsentPolicy) {
        this.hieConsentPolicy = hieConsentPolicy;
    }

    public boolean getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }
}
