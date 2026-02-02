package com.scnsoft.eldermark.dto.referral;

import com.scnsoft.eldermark.entity.referral.ReferralRequestSharedChannel;
import com.scnsoft.eldermark.validation.SpELAssert;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@SpELAssert.List(
        value = {
                @SpELAssert(
                        applyIf = "sharedChannel.name().equals('FAX')",
                        value = "#isNotEmpty(sharedFax)",
                        message = "sharedFax {javax.validation.constraints.NotEmpty.message}",
                        helpers = StringUtils.class
                ),
                @SpELAssert(
                        applyIf = "sharedChannel.name().equals('FAX')",
                        value = "#isNotEmpty(sharedPhone)",
                        message = "sharedPhone {javax.validation.constraints.NotEmpty.message}",
                        helpers = StringUtils.class
                )
        }
)
public class ReferralMarketplaceDto {

    @NotNull
    private Long communityId;
    private String communityTitle;
    private String organizationTitle;
    private String communityEmail;
    @NotNull
    private ReferralRequestSharedChannel sharedChannel;
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String sharedFax;
    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String sharedPhone;
    @Size(max = 1500)
    private String sharedFaxComment;

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getCommunityTitle() {
        return communityTitle;
    }

    public void setCommunityTitle(String communityTitle) {
        this.communityTitle = communityTitle;
    }

    public String getOrganizationTitle() {
        return organizationTitle;
    }

    public void setOrganizationTitle(String organizationTitle) {
        this.organizationTitle = organizationTitle;
    }

    public String getCommunityEmail() {
        return communityEmail;
    }

    public void setCommunityEmail(String communityEmail) {
        this.communityEmail = communityEmail;
    }

    public ReferralRequestSharedChannel getSharedChannel() {
        return sharedChannel;
    }

    public void setSharedChannel(ReferralRequestSharedChannel sharedChannel) {
        this.sharedChannel = sharedChannel;
    }

    public String getSharedFax() {
        return sharedFax;
    }

    public void setSharedFax(String sharedFax) {
        this.sharedFax = sharedFax;
    }

    public String getSharedPhone() {
        return sharedPhone;
    }

    public void setSharedPhone(String sharedPhone) {
        this.sharedPhone = sharedPhone;
    }

    public String getSharedFaxComment() {
        return sharedFaxComment;
    }

    public void setSharedFaxComment(String sharedFaxComment) {
        this.sharedFaxComment = sharedFaxComment;
    }
}
