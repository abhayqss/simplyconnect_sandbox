package com.scnsoft.eldermark.facade.signature;

import com.scnsoft.eldermark.beans.projection.OidNameAndOrganizationOidNameAware;
import com.scnsoft.eldermark.beans.security.projection.dto.DocumentSignatureRequestSecurityFieldsAware;
import com.scnsoft.eldermark.dto.document.BaseDocumentDto;
import com.scnsoft.eldermark.dto.signature.DocumentSignatureTemplateAssignToFolderDto;
import com.scnsoft.eldermark.dto.signature.*;
import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateContext;
import com.scnsoft.eldermark.dto.singature.DocumentTemplatePreview;
import com.scnsoft.eldermark.dto.singature.UpdateDocumentSignatureTemplateData;
import com.scnsoft.eldermark.dto.singature.UploadDocumentSignatureTemplateData;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateAutoFillFieldType;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateOrganizationAutoFillFieldType;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate_;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.projection.signature.DocumentSignatureTemplateListItem;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.document.folder.DocumentFolderService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestSecurityService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureTemplateFieldService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureTemplateSecurityService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureTemplateService;
import com.scnsoft.eldermark.service.document.signature.template.field.DocumentSignatureTemplateAutoFillFieldTypeService;
import com.scnsoft.eldermark.service.document.signature.template.field.DocumentSignatureTemplateOrganizationAutoFillFieldTypeService;
import com.scnsoft.eldermark.service.document.signature.template.field.DocumentSignatureTemplateToolboxRequesterTypeService;
import com.scnsoft.eldermark.service.document.signature.template.field.DocumentSignatureTemplateToolboxSignerFieldTypeService;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentSignatureTemplateFacadeImpl implements DocumentSignatureTemplateFacade {

    private static final Sort SORT_BY_TITLE = Sort.by(
            DocumentSignatureTemplate_.TITLE
    );

    @Autowired
    private Converter<DocumentSignatureTemplateListItem, DocumentSignatureTemplateListItemDto>
            documentSignatureTemplateListItemDtoConverter;

    @Autowired
    private Converter<DocumentSignatureTemplate, BaseDocumentDto> templateDocumentDtoConverter;

    @Autowired
    private Converter<DocumentSignatureTemplatePreviewRequestDto, DocumentSignatureTemplateContext> templateContextConverter;

    @Autowired
    private Converter<DocumentTemplatePreview, DocumentSignatureTemplatePreviewResponseDto> templatePreviewResponseConverter;

    @Autowired
    private Converter<DocumentSignatureTemplate, DocumentSignatureTemplateDto> documentSignatureTemplateDtoConverter;

    @Autowired
    private Converter<UploadDocumentSignatureTemplateDto, UploadDocumentSignatureTemplateData> uploadDocumentSignatureTemplateDataConverter;

    @Autowired
    private Converter<UpdateDocumentSignatureTemplateDto, UpdateDocumentSignatureTemplateData> updateSignatureTemplateDataConverter;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private DocumentSignatureTemplateService signatureTemplateService;

    @Autowired
    private DocumentSignatureTemplateFieldService signatureTemplateFieldService;

    @Autowired
    private DocumentSignatureTemplateAutoFillFieldTypeService templateAutoFillFieldTypeService;

    @Autowired
    private DocumentSignatureTemplateToolboxRequesterTypeService templateToolboxRequesterTypeService;

    @Autowired
    private DocumentSignatureTemplateOrganizationAutoFillFieldTypeService organizationAutoFillFieldTypeService;

    @Autowired
    private DocumentSignatureTemplateToolboxSignerFieldTypeService toolboxSignerFieldTypeService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private DocumentSignatureTemplateSecurityService signatureTemplateSecurityService;

    @Autowired
    private DocumentSignatureRequestSecurityService signatureRequestSecurityService;

    @Autowired
    private DocumentFolderService documentFolderService;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@documentSignatureTemplateSecurityService.canViewList(#communityIds)")
    public List<DocumentSignatureTemplateListItemDto> getAllTemplates(
            List<Long> communityIds,
            Boolean isManuallyCreated
    ) {
        return signatureTemplateService.getProjectedTemplatesByCommunityIds(
                        communityIds,
                        isManuallyCreated,
                        DocumentSignatureTemplateListItem.class,
                        SORT_BY_TITLE,
                        permissionFilterService.createPermissionFilterForCurrentUser()
                )
                .stream()
                .map(documentSignatureTemplateListItemDtoConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@documentSignatureTemplateSecurityService.canViewList(#communityId)")
    public Long countByCommunityId(Long communityId) {
        return signatureTemplateService.countByCommunityId(
                communityId,
                permissionFilterService.createPermissionFilterForCurrentUser()
        );
    }

    @Override
    @Transactional
    @PreAuthorize("@documentSignatureTemplateSecurityService.canAdd(#dto.getOrganizationId())")
    public Long uploadTemplate(UploadDocumentSignatureTemplateDto dto) {
        var data = Objects.requireNonNull(uploadDocumentSignatureTemplateDataConverter.convert(dto));
        data.setAuthor(loggedUserService.getCurrentEmployee());
        return signatureTemplateService.create(data).getId();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@documentSignatureTemplateSecurityService.canView(#templateId)")
    public DocumentSignatureTemplateDto findByIdWithDefaults(Long templateId, Long clientId, Integer timezoneOffset) {
        var template = signatureTemplateService.findById(templateId);
        var dto = Objects.requireNonNull(documentSignatureTemplateDtoConverter.convert(template));
        if (signatureRequestSecurityService.canAdd(DocumentSignatureRequestSecurityFieldsAware.of(null, templateId))) {
            var context = new DocumentSignatureTemplateContext();
            context.setTimezoneOffset(timezoneOffset);
            context.setClient(clientId != null ? clientService.findById(clientId) : null);
            context.setRequestFromMultipleClients(clientId == null);
            context.setCurrentEmployee(loggedUserService.getCurrentEmployee());
            context.setTemplate(template);
            var schema = signatureTemplateService.generateSchema(context);
            dto.setDefaults(schema.getDefaultValues());
            dto.setSchema(schema.getFormSchema());
            dto.setUiSchema(schema.getUiFormSchema());
        }
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@documentSignatureRequestSecurityService.canAdd(#dto)")
    public DocumentSignatureTemplatePreviewResponseDto getTemplatePreview(DocumentSignatureTemplatePreviewRequestDto dto) {
        var templateContext = Objects.requireNonNull(templateContextConverter.convert(dto));
        templateContext.setCurrentEmployee(loggedUserService.getCurrentEmployee());
        var preview = signatureTemplateService.getTemplatePreview(templateContext);
        return templatePreviewResponseConverter.convert(preview);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@documentSignatureTemplateSecurityService.canView(#templateId, #communityId)")
    public BaseDocumentDto findTemplateDocumentById(Long templateId, Long communityId) {
        var template = signatureTemplateService.findByIdAndCommunityId(templateId, communityId);
        var community = communityService.findById(communityId, OidNameAndOrganizationOidNameAware.class);

        var documentDto = Objects.requireNonNull(templateDocumentDtoConverter.convert(template));
        documentDto.setCommunityOid(community.getOid());
        documentDto.setCommunityTitle(community.getName());
        documentDto.setOrganizationOid(community.getOrganizationOid());
        documentDto.setOrganizationTitle(community.getOrganizationName());
        return documentDto;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@documentSignatureTemplateSecurityService.canView(#templateId)")
    public void downloadTemplateById(Long templateId, Long communityId, HttpServletResponse httpServletResponse) {

        var templateContext = new DocumentSignatureTemplateContext();

        if (communityId == null) {
            templateContext.setTemplate(signatureTemplateService.findById(templateId));
        } else {
            templateContext.setTemplate(signatureTemplateService.findByIdAndCommunityId(templateId, communityId));
            templateContext.setCommunity(communityService.findById(communityId));
        }

        if (templateContext.getTemplate() == null) {
            throw new BusinessException(BusinessExceptionType.NOT_FOUND);
        }

        var data = signatureTemplateService.getTemplatePdf(templateContext);
        WriterUtils.copyDocumentContentToResponse(
                templateContext.getTemplate().getTitle() + ".pdf",
                data,
                MediaType.APPLICATION_PDF_VALUE,
                false,
                httpServletResponse
        );
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@documentSignatureTemplateSecurityService.canViewList(#communityId)")
    public void downloadMultipleTemplateByIds(List<Long> templateIds, Long communityId, HttpServletResponse httpServletResponse) {
        var templates = signatureTemplateService.getProjectedTemplatesByCommunityId(
                communityId,
                DocumentSignatureTemplate.class,
                SORT_BY_TITLE,
                permissionFilterService.createPermissionFilterForCurrentUser()
        );
        var community = communityService.findById(communityId);
        var documents = templates.stream()
                .filter(template -> CollectionUtils.isEmpty(templateIds) || templateIds.contains(template.getId()))
                .map(template -> WriterUtils.FileProvider.of(
                                template.getTitle() + ".pdf",
                                MediaType.APPLICATION_PDF_VALUE,
                                () -> new ByteArrayInputStream(signatureTemplateService.getTemplatePdf(
                                        new DocumentSignatureTemplateContext(template, community)
                                ))
                        )
                )
                .collect(Collectors.toList());

        var zipData = WriterUtils.generateZip(documents);
        WriterUtils.copyBytesAsZipToResponse("Company Documents", zipData, httpServletResponse);
    }

    @Override
    public List<IdentifiedNamedTitledEntityDto> getAutoFillFieldTypes() {
        return templateAutoFillFieldTypeService.findAll().stream()
                .sorted(Comparator.comparing(DocumentSignatureTemplateAutoFillFieldType::getPosition))
                .map(it -> new IdentifiedNamedTitledEntityDto(it.getId(), it.getCode().name(), it.getTitle()))
                .collect(Collectors.toList());
    }

    @Override
    public List<IdentifiedNamedTitledEntityDto> getOrganizationAutoFillFieldTypes() {
        return organizationAutoFillFieldTypeService.findAll().stream()
                .sorted(Comparator.comparing(DocumentSignatureTemplateOrganizationAutoFillFieldType::getPosition))
                .map(it -> new IdentifiedNamedTitledEntityDto(it.getId(), it.getCode().name(), it.getTitle()))
                .collect(Collectors.toList());
    }

    @Override
    public List<IdentifiedNamedTitledEntityDto> getToolboxRequesterTypes() {
        return templateToolboxRequesterTypeService.findAll()
                .stream()
                .map(it -> new IdentifiedNamedTitledEntityDto(it.getId(), it.getCode().name(), it.getTitle()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean canAdd(Long organizationId) {
        return signatureTemplateSecurityService.canAdd(organizationId);
    }

    @Override
    @Transactional
    @PreAuthorize("@documentSignatureTemplateSecurityService.canEdit(#templateId)")
    public Long updateTemplate(Long templateId, UpdateDocumentSignatureTemplateDto dto) {
        var data = Objects.requireNonNull(updateSignatureTemplateDataConverter.convert(dto));
        data.setTemplateId(templateId);
        data.setAuthor(loggedUserService.getCurrentEmployee());
        return signatureTemplateService.update(data).getId();
    }

    @Transactional
    @PreAuthorize("@documentSignatureTemplateSecurityService.canDelete(#templateId)")
    public void deleteTemplate(Long templateId) {
        var currentEmployee = loggedUserService.getCurrentEmployee();
        signatureTemplateService.delete(templateId, currentEmployee);
    }

    @Override
    public List<DocumentSignatureTemplateToolboxSignerFieldTypeDto> getToolboxSignerFieldTypes() {
        return toolboxSignerFieldTypeService.findAll()
                .stream()
                .map(it -> new DocumentSignatureTemplateToolboxSignerFieldTypeDto(
                                it.getId(),
                                it.getCode().name(),
                                it.getTitle(),
                                it.getWidth(),
                                it.getHeight()
                        )
                )
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("@documentSignatureTemplateSecurityService.canAssign(#dto.templateId) && @documentFolderSecurityService.canView(#dto.folderId)")
    public void assignToFolder(DocumentSignatureTemplateAssignToFolderDto dto) {
        var folderId = dto.getFolderId();
        var folderToAssign = documentFolderService.findById(folderId);
        var communityId = folderToAssign.getCommunityId();
        var templateToAssign = signatureTemplateService.findById(dto.getTemplateId());
        documentFolderService.findByCommunityIdAndIdIn(communityId, templateToAssign.getFolderIds())
                .ifPresent(
                        folder -> templateToAssign.getFolderIds()
                                .removeIf(id -> folder.getId().equals(id))
                );

        templateToAssign.getFolderIds().add(folderId);

        signatureTemplateService.save(templateToAssign);
    }
}
