package com.scnsoft.eldermark.facade.signature;

import com.scnsoft.eldermark.dto.document.BaseDocumentDto;
import com.scnsoft.eldermark.dto.signature.*;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DocumentSignatureTemplateFacade {

    List<DocumentSignatureTemplateListItemDto> getAllTemplates(List<Long> communityIds, Boolean isManuallyCreated);

    Long countByCommunityId(Long communityId);

    Long uploadTemplate(UploadDocumentSignatureTemplateDto dto);

    DocumentSignatureTemplateDto findByIdWithDefaults(Long templateId, Long clientId, Integer timezoneOffset);

    DocumentSignatureTemplatePreviewResponseDto getTemplatePreview(DocumentSignatureTemplatePreviewRequestDto dto);

    BaseDocumentDto findTemplateDocumentById(Long templateName, Long communityId);

    void downloadTemplateById(Long templateId, Long communityId, HttpServletResponse httpServletResponse);

    void downloadMultipleTemplateByIds(List<Long> templateIds, Long communityId, HttpServletResponse httpServletResponse);

    List<IdentifiedNamedTitledEntityDto> getAutoFillFieldTypes();

    List<IdentifiedNamedTitledEntityDto> getOrganizationAutoFillFieldTypes();

    List<IdentifiedNamedTitledEntityDto> getToolboxRequesterTypes();

    List<DocumentSignatureTemplateToolboxSignerFieldTypeDto> getToolboxSignerFieldTypes();

    boolean canAdd(Long organizationId);

    Long updateTemplate(Long templateId, UpdateDocumentSignatureTemplateDto dto);

    void deleteTemplate(Long templateId);

    void assignToFolder(DocumentSignatureTemplateAssignToFolderDto dto);
}
