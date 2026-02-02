package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.projection.dto.ReferralSecurityFieldsAware;

public interface ReferralSecurityService {

    boolean canAdd(ReferralSecurityFieldsAware dto);

    boolean canAddToCommunity(Long communityId);

    boolean canViewOutboundsInCommunity(Long communityId);

    boolean canViewInboundsInCommunity(Long communityId);

    boolean canViewOutbound(Long referralId);

    boolean canViewOutboundList();

    boolean canViewInbound(Long requestId);

    boolean canViewInboundList();

    boolean canRequestInfo(Long requestId);

    boolean canViewInboundInfoRequest(Long infoRequestId);

    boolean canViewInboundInfoRequestList();

    boolean canViewOutboundInfoRequest(Long infoRequestId);

    boolean canViewOutboundInfoRequestList();

    boolean canRespondToInfoRequest(Long infoRequestId);

    boolean canPreadmit(Long requestId);

    boolean canDecline(Long requestId);

    boolean canAccept(Long requestId);

    boolean canCancel(Long referralId);

    boolean canReassign(Long requestId);

    boolean hasAddPermissions();

    boolean canDownloadOutboundAttachment(Long attachmentId);

    boolean canDownloadInboundAttachment(Long requestId, Long attachmentId);

}
