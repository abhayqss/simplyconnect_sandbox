package com.scnsoft.eldermark.mobile.facade.home;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.document.ClientDocument_;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest_;
import com.scnsoft.eldermark.mobile.dto.home.DocumentHomeSectionDto;
import com.scnsoft.eldermark.mobile.projection.home.DocumentHomeSectionProjection;
import com.scnsoft.eldermark.service.document.ClientDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentsHomeSectionProviderImpl implements DocumentsHomeSectionProvider {

    private static final Sort DOCUMENTS_SORT = Sort.by(Sort.Order.desc(
            ClientDocument_.SIGNATURE_REQUEST + "." + DocumentSignatureRequest_.DATE_CREATED)
    );

    @Autowired
    private ClientDocumentService clientDocumentService;

    @Override
    public List<DocumentHomeSectionDto> loadDocuments(Long currentEmployeeId,
                                                      Collection<Long> associatedClientIds,
                                                      PermissionFilter permissionFilter, int limit) {
        return clientDocumentService.findDocumentShouldBeSignedByEmployeeId(
                        currentEmployeeId,
                        associatedClientIds,
                        permissionFilter,
                        DocumentHomeSectionProjection.class,
                        DOCUMENTS_SORT,
                        limit)
                .stream()
                .map(this::convertDocumentSectionItemDto)
                .collect(Collectors.toList());
    }

    private DocumentHomeSectionDto convertDocumentSectionItemDto(DocumentHomeSectionProjection source) {
        var dto = new DocumentHomeSectionDto();
        dto.setDocumentId(source.getId());
        dto.setClientId(source.getClientId());
        dto.setTitle(source.getDocumentTitle());
        dto.setMimeType(source.getMimeType());
        return dto;
    }
}
