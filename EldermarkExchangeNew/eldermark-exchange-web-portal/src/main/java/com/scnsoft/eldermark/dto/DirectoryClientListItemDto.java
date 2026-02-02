package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.entity.client.ClientPrimaryContactType;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

public class DirectoryClientListItemDto extends ClientNameDto {

    private Long communityId;
    private HieConsentPolicyType hieConsentPolicyName;
    private ClientPrimaryContactType primaryContactTypeName;

    public DirectoryClientListItemDto(
            Long id,
            String firstName,
            String lastName,
            String fullName,
            Long communityId,
            HieConsentPolicyType hieConsentPolicyName,
            ClientPrimaryContactType primaryContactTypeName
    ) {
        super(id, firstName, lastName, fullName);
        this.communityId = communityId;
        this.hieConsentPolicyName = hieConsentPolicyName;
        this.primaryContactTypeName = primaryContactTypeName;
    }
    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public HieConsentPolicyType getHieConsentPolicyName() {
        return hieConsentPolicyName;
    }

    public void setHieConsentPolicyName(HieConsentPolicyType hieConsentPolicyName) {
        this.hieConsentPolicyName = hieConsentPolicyName;
    }

    public ClientPrimaryContactType getPrimaryContactTypeName() {
        return primaryContactTypeName;
    }

    public void setPrimaryContactTypeName(ClientPrimaryContactType primaryContactTypeName) {
        this.primaryContactTypeName = primaryContactTypeName;
    }
}
