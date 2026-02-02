package com.scnsoft.eldermark.facade.signature;

import com.scnsoft.eldermark.dto.RoleDto;
import com.scnsoft.eldermark.dto.signature.*;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DocumentSignatureRequestFacade {

    DocumentSignatureRequestInfoDto findInfoById(Long requestId);

    Page<DocumentSignatureHistoryDto> findHistoryByDocumentId(Long documentId, Pageable pageable, Integer timezoneOffset);

    List<Long> submitRequests(SubmitTemplateSignatureRequestsDto dto);

    void cancelRequest(Long requestId);

    List<RoleDto> getAllowedRecipientRoles();

    boolean canAdd(Long clientId);

    Long renewRequest(DocumentSignatureRequestRenewDto dto);

    List<IdentifiedNamedEntityDto> getAllowedRequestRecipientContacts(Long clientId, Long documentId);

    DocumentSignatureResendPinResponseDto resendPin(Long requestId);

    Long count(DocumentSignatureRequestFilterDto filterDto);

    boolean canAddForOrganization(Long organizationId);

    List<IdentifiedTitledEntityDto> findOrganizationsAvailableForSignatureRequest();

    List<IdentifiedTitledEntityDto> findCommunitiesAvailableForSignatureRequest(Long organizationId);
}
