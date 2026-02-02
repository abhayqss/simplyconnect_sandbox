package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.ReferralFilter;
import com.scnsoft.eldermark.web.commons.dto.FileBytesDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import com.scnsoft.eldermark.dto.referral.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReferralFacade {

    Long add(ReferralDto referralDto);

    ReferralDto findDefault(Long clientId);

    Page<ReferralCommunicationListItemDto> findInboundInfoRequests(Long requestId, Pageable pageable);

    Page<ReferralCommunicationListItemDto> findOutboundInfoRequests(Long referralId, Pageable pageable);

    Long sendInfoRequest(ReferralCommunicationDto dto);

    void respondToInfoRequest(ReferralCommunicationDto dto);

    ReferralCommunicationDto getInboundInfoRequest(Long infoRequestId);

    ReferralCommunicationDto getOutboundInfoRequest(Long infoRequestId);

    List<IdentifiedTitledEntityDto> findPossibleAssignees(Long requestId);

    void assign(Long requestId, Long employeeId);

    Page<ReferralListItemDto> findOutbounds(ReferralFilter filter, Pageable pageable);

    Page<ReferralListItemDto> findInbounds(ReferralFilter filter, Pageable pageable);

    ReferralDto findOutboundById(Long id);

    ReferralDto findInboundById(Long id);

    Page<ReferralSharedWithListItemDto> findOutboundReferralRequests(Long id, Pageable pageable);

    ReferralSharedWithDetailsDto findOutboundRequestById(Long id);

    List<IdentifiedTitledEntityDto> findRecipients(ReferralFilter filter);

    List<IdentifiedTitledEntityDto> findSenders(ReferralFilter filter);

    boolean canAddToCommunity(Long communityId);

//    Long edit(ReferralDto referralDto);

    void preadmit(Long requestId);

    void accept(Long requestId, ReferralAcceptDto dto);

    void decline(Long requestId, ReferralDeclineDto referralDeclineDto);

    void cancel(Long referralId);

    List<IdentifiedTitledEntityDto> getReferralOrganizations(Long targetCommunityId);

    List<IdentifiedTitledEntityDto> getReferralCommunities(Long targetCommunityId, Long organizationId);

    FileBytesDto downloadOutboundReferralAttachmentById(Long attachmentId);

    FileBytesDto downloadInboundReferralAttachmentById(Long requestId, Long attachmentId);
}
