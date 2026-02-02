package com.scnsoft.eldermark.dto.referral;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.scnsoft.eldermark.beans.security.projection.dto.ReferralSecurityFieldsAware;
import com.scnsoft.eldermark.dto.BaseAttachmentDto;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReferralDto implements ReferralSecurityFieldsAware {

    private Long id;
    private Long requestId;

    private Long date;

    private String statusName;
    private String statusTitle;

    @NotNull
    private Long priorityId;
    private String priorityName;
    private String priorityTitle;

    @NotEmpty
    private List<Long> services;
    private String serviceTitle;

    private Long referringCommunityId;
    private List<Long> sharedCommunityIds;

    @Size(max = 5000)
    @NotEmpty
    private String instructions;

    private Long assigneeId;
    private String assigneeName;

    private String declineReason;
    private String comment;

    private Long serviceStartDate;
    private Long serviceEndDate;

    private boolean isCcdShared;
    private boolean isFacesheetShared;
    private boolean isServicePlanShared;
    private boolean hasSharedServicePlan;

    private boolean canRequestInfo;
    private boolean canPreadmit;
    private boolean canAccept;
    private boolean canDecline;
    private boolean canCancel;
    private boolean canAssign;

    private Long referralRequestId;

    @Valid
    private ReferralClientDto client;

    @Valid
    private ReferralMarketplaceDto marketplace;

    @Size(max = 10)
    private List<MultipartFile> attachmentFiles;
    private List<BaseAttachmentDto> attachments;

    private List<Long> attachedClientDocumentIds;

    @JsonIgnore
    private ZoneId zoneId;

    @Size(max = 256)
    @NotEmpty
    private String person;
    
    private String communityTitle;

    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    @NotEmpty
    private String organizationPhone;

    @Pattern(regexp = ValidationRegExpConstants.EMAIL_REGEXP)
    @NotEmpty
    private String organizationEmail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
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

    public Long getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
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


    public List<Long> getServices() {
        return services;
    }

    public void setServices(List<Long> services) {
        this.services = services;
    }

    @Override
    public Long getReferringCommunityId() {
        return referringCommunityId;
    }

    public void setReferringCommunityId(Long referringCommunityId) {
        this.referringCommunityId = referringCommunityId;
    }

    @Override
    @JsonIgnore
    public Long getMarketplaceCommunityId() {
        return Optional.ofNullable(marketplace).map(ReferralMarketplaceDto::getCommunityId).orElse(null);
    }

    @Override
    public List<Long> getSharedCommunityIds() {
        return sharedCommunityIds;
    }

    public void setSharedCommunityIds(List<Long> sharedCommunityIds) {
        this.sharedCommunityIds = sharedCommunityIds;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public boolean getIsCcdShared() {
        return isCcdShared;
    }

    public void setIsCcdShared(boolean ccdShared) {
        isCcdShared = ccdShared;
    }

    public boolean getIsFacesheetShared() {
        return isFacesheetShared;
    }

    public void setIsFacesheetShared(boolean facesheetShared) {
        isFacesheetShared = facesheetShared;
    }

    public boolean getIsServicePlanShared() {
        return isServicePlanShared;
    }

    public void setIsServicePlanShared(boolean servicePlanShared) {
        isServicePlanShared = servicePlanShared;
    }

    public ReferralClientDto getClient() {
        return client;
    }

    public void setClient(ReferralClientDto client) {
        this.client = client;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getCommunityTitle() {
        return communityTitle;
    }

    public void setCommunityTitle(String communityTitle) {
        this.communityTitle = communityTitle;
    }

    public String getOrganizationPhone() {
        return organizationPhone;
    }

    public void setOrganizationPhone(String organizationPhone) {
        this.organizationPhone = organizationPhone;
    }

    public String getOrganizationEmail() {
        return organizationEmail;
    }

    public void setOrganizationEmail(String organizationEmail) {
        this.organizationEmail = organizationEmail;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public boolean getCanRequestInfo() {
        return canRequestInfo;
    }

    public void setCanRequestInfo(boolean canRequestInfo) {
        this.canRequestInfo = canRequestInfo;
    }

    public boolean getCanPreadmit() {
        return canPreadmit;
    }

    public void setCanPreadmit(boolean canPreadmit) {
        this.canPreadmit = canPreadmit;
    }

    public boolean getCanAccept() {
        return canAccept;
    }

    public void setCanAccept(boolean canAccept) {
        this.canAccept = canAccept;
    }

    public boolean getCanDecline() {
        return canDecline;
    }

    public void setCanDecline(boolean canDecline) {
        this.canDecline = canDecline;
    }

    public boolean getCanCancel() {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public boolean getCanAssign() {
        return canAssign;
    }

    public void setCanAssign(boolean canAssign) {
        this.canAssign = canAssign;
    }

    public String getDeclineReason() {
        return declineReason;
    }

    public void setDeclineReason(String declineReason) {
        this.declineReason = declineReason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean getHasSharedServicePlan() {
        return hasSharedServicePlan;
    }

    public void setHasSharedServicePlan(boolean hasSharedServicePlan) {
        this.hasSharedServicePlan = hasSharedServicePlan;
    }

    public Long getServiceStartDate() {
        return serviceStartDate;
    }

    public void setServiceStartDate(Long serviceStartDate) {
        this.serviceStartDate = serviceStartDate;
    }

    public Long getServiceEndDate() {
        return serviceEndDate;
    }

    public void setServiceEndDate(Long serviceEndDate) {
        this.serviceEndDate = serviceEndDate;
    }

    @Override
    @JsonIgnore
    public Long getClientId() {
        return Optional.ofNullable(client).map(ReferralClientDto::getId).orElse(null);
    }

    public ReferralMarketplaceDto getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(ReferralMarketplaceDto marketplace) {
        this.marketplace = marketplace;
    }

    public List<MultipartFile> getAttachmentFiles() {
        return attachmentFiles;
    }

    public void setAttachmentFiles(List<MultipartFile> attachmentFiles) {
        this.attachmentFiles = attachmentFiles;
    }

    public List<BaseAttachmentDto> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<BaseAttachmentDto> attachments) {
        this.attachments = attachments;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public Long getReferralRequestId() {
        return referralRequestId;
    }

    public void setReferralRequestId(Long referralRequestId) {
        this.referralRequestId = referralRequestId;
    }

    public List<Long> getAttachedClientDocumentIds() {
        return attachedClientDocumentIds;
    }

    public void setAttachedClientDocumentIds(List<Long> attachedClientDocumentIds) {
        this.attachedClientDocumentIds = attachedClientDocumentIds;
    }
}
