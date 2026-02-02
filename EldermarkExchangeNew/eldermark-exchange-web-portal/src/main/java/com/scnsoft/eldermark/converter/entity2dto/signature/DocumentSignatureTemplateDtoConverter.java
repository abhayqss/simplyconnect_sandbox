package com.scnsoft.eldermark.converter.entity2dto.signature;

import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;
import com.scnsoft.eldermark.dto.signature.BaseDocumentSignatureTemplateDto;
import com.scnsoft.eldermark.dto.signature.DocumentSignatureTemplateDto;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateField;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateFieldLocation;
import com.scnsoft.eldermark.entity.signature.TemplateFieldPdcFlowType;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.document.folder.DocumentFolderService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureFieldLocationService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureTemplateJsonSchemaService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureTemplateService;
import com.scnsoft.eldermark.service.document.signature.template.field.DocumentSignatureTemplateAutoFillFieldTypeService;
import com.scnsoft.eldermark.service.document.signature.template.field.DocumentSignatureTemplateOrganizationAutoFillFieldTypeService;
import com.scnsoft.eldermark.service.document.signature.template.field.DocumentSignatureTemplateToolboxRequesterTypeService;
import com.scnsoft.eldermark.service.document.signature.template.field.DocumentSignatureTemplateToolboxSignerFieldTypeService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class DocumentSignatureTemplateDtoConverter implements Converter<DocumentSignatureTemplate, DocumentSignatureTemplateDto> {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private DocumentFolderService folderService;

    @Autowired
    private DocumentSignatureTemplateAutoFillFieldTypeService autoFillTypeService;

    @Autowired
    private DocumentSignatureTemplateOrganizationAutoFillFieldTypeService organizationAutoFillFieldTypeService;

    @Autowired
    private DocumentSignatureTemplateToolboxRequesterTypeService toolboxRequesterTypeService;

    @Autowired
    private DocumentSignatureTemplateToolboxSignerFieldTypeService toolboxSignerFieldTypeService;

    @Autowired
    private DocumentSignatureTemplateJsonSchemaService templateJsonSchemaService;

    @Autowired
    private DocumentSignatureTemplateService templateService;

    @Autowired
    private DocumentSignatureFieldLocationService fieldLocationService;

    @Override
    public DocumentSignatureTemplateDto convert(DocumentSignatureTemplate source) {
        var dto = new DocumentSignatureTemplateDto();
        dto.setId(source.getId());
        dto.setName(source.getName());
        dto.setTitle(source.getTitle());
        dto.setSchema(source.getFormSchema());
        dto.setUiSchema(source.getFormUiSchema());
        dto.setHasSignatureAreas(
                source.getFields().stream()
                        .anyMatch(it -> it.getPdcFlowType() == TemplateFieldPdcFlowType.SIGNATURE)
        );

        if (Boolean.TRUE.equals(source.getIsManuallyCreated())) {
            var communityIds = source.getCommunityIds();
            if (CollectionUtils.isNotEmpty(communityIds)) {
                // Community Template
                dto.setCommunityIds(new ArrayList<>(communityIds));
                communityService.findSecurityAwareEntities(communityIds)
                        .stream()
                        .map(OrganizationIdAware::getOrganizationId)
                        .findFirst()
                        .ifPresent(dto::setOrganizationId);
            } else {
                // Organization template
                dto.setCommunityIds(null);
                source.getOrganizationIds().stream()
                        .findFirst()
                        .ifPresent(dto::setOrganizationId);
            }
            dto.setConfiguration(new BaseDocumentSignatureTemplateDto.Configuration());
            dto.setStatusName(source.getStatus());
            if (source.getStatus() != null){
                dto.setStatusTitle(source.getStatus().getDisplayName());
            }
            fillConfiguration(source, dto.getConfiguration());
        }

        return dto;
    }

    private void fillConfiguration(
            DocumentSignatureTemplate source,
            BaseDocumentSignatureTemplateDto.Configuration target
    ) {
        var pageSizes = templateService.getTemplatePdfPageSizes(source);
        fillAutoFillFields(
                target,
                source.getFields().stream()
                        .filter(it -> it.getAutoFillTypeId() != null)
                        .collect(Collectors.toList()),
                pageSizes
        );

        fillOrganizationAutoFillFields(
            target,
            source.getFields().stream()
                .filter(it -> it.getOrganizationAutoFillTypeId() != null)
                .collect(Collectors.toList()),
            pageSizes
        );

        fillToolboxSignerFields(
            target,
            source.getFields().stream()
                .filter(it -> it.getToolboxSignerTypeId() != null)
                .collect(Collectors.toList()),
            pageSizes
        );
        fillToolboxRequesterFields(
                target,
                source.getFields().stream()
                        .filter(it -> it.getToolboxRequesterTypeId() != null)
                        .collect(Collectors.toList()),
                pageSizes
        );

        fillRules(
            target,
            source.getFields().stream()
                .filter(it -> it.getToolboxSignerTypeId() != null)
                .collect(Collectors.toList())
        );


        fillFolderConfiguration(source, target);
    }

    private void fillFolderConfiguration(DocumentSignatureTemplate source, BaseDocumentSignatureTemplateDto.Configuration target) {
        target.setFolders(
            folderService.findByDocumentSignatureTemplateId(source.getId()).stream()
                    .map(folder -> {
                        var config = new BaseDocumentSignatureTemplateDto.FolderConfiguration();
                        config.setFolderId(folder.getId());
                        config.setCommunityId(folder.getCommunityId());
                        return config;
                    })
                    .collect(Collectors.toList())
        );
    }

    private void fillRules(
        BaseDocumentSignatureTemplateDto.Configuration target,
        List<DocumentSignatureTemplateField> fields
    ) {
        if (CollectionUtils.isNotEmpty(fields)) {
            target.setRules(new ArrayList<>());
            fields.stream()
                .filter(field -> field.getRelatedFieldId() != null)
                .forEach(field -> {
                    var rule = new BaseDocumentSignatureTemplateDto.Rule();
                    rule.setDependentFieldId(field.getId());
                    rule.setFieldId(field.getRelatedFieldId());
                    rule.setDependentFieldTitle(field.getTitle());
                    fields.stream()
                        .filter(templateField -> templateField.getId().equals(field.getRelatedFieldId()))
                        .findFirst()
                        .ifPresent(templateField -> {
                            rule.setFieldTitle(templateField.getTitle());
                        });
                    target.getRules().add(rule);
                });
        }
    }

    private void fillToolboxSignerFields(
        BaseDocumentSignatureTemplateDto.Configuration target,
        List<DocumentSignatureTemplateField> toolboxSignerFields,
        List<Pair<Float, Float>> pageSizes
    ) {
        if (CollectionUtils.isNotEmpty(toolboxSignerFields)) {

            var typesMapById = toolboxSignerFieldTypeService.getTypesMapById();

            var fields = toolboxSignerFields.stream()
                .map(sourceField -> {
                    var targetField = new BaseDocumentSignatureTemplateDto.ToolboxSignerField();

                    var type = typesMapById.get(sourceField.getToolboxSignerTypeId());
                    targetField.setTitle(sourceField.getTitle());
                    targetField.setTypeCode(type.getCode().name());
                    targetField.setTypeTitle(type.getTitle());
                    targetField.setTypeId(type.getId());
                    targetField.setIsResizable(type.getWidth() == null || type.getHeight() == null);

                    targetField.setId(sourceField.getId());
                    targetField.setLocation(new BaseDocumentSignatureTemplateDto.Location());
                    fillLocation(sourceField.getLocations().get(0), targetField.getLocation(), pageSizes);
                    return targetField;
                })
                .collect(Collectors.toList());

            target.setToolboxSignerFields(fields);
        } else {
            target.setToolboxSignerFields(List.of());
        }
    }

    private void fillOrganizationAutoFillFields(
        BaseDocumentSignatureTemplateDto.Configuration target,
        List<DocumentSignatureTemplateField> organizationAutoFillFields,
        List<Pair<Float, Float>> pageSizes
    ) {
        if (CollectionUtils.isNotEmpty(organizationAutoFillFields)) {

            var typesMapById =
                organizationAutoFillFieldTypeService.getTypesMapById();

            var fields = organizationAutoFillFields.stream()
                .map(sourceField -> {
                    var targetField = new BaseDocumentSignatureTemplateDto.OrganizationAutoFillField();

                    var type = typesMapById.get(sourceField.getOrganizationAutoFillTypeId());
                    targetField.setTypeCode(type.getCode().name());
                    targetField.setTypeTitle(type.getTitle());
                    targetField.setTypeId(type.getId());

                    targetField.setId(sourceField.getId());
                    targetField.setLocation(new BaseDocumentSignatureTemplateDto.Location());
                    fillLocation(sourceField.getLocations().get(0), targetField.getLocation(), pageSizes);
                    return targetField;
                })
                .collect(Collectors.toList());

            target.setOrganizationAutoFillFields(fields);
        } else {
            target.setOrganizationAutoFillFields(List.of());
        }
    }

    private void fillAutoFillFields(
            BaseDocumentSignatureTemplateDto.Configuration target,
            List<DocumentSignatureTemplateField> autoFillFields,
            List<Pair<Float, Float>> pageSizes
    ) {
        if (CollectionUtils.isNotEmpty(autoFillFields)) {

            var typesMapById = autoFillTypeService.getTypesMapById();

            var fields = autoFillFields.stream()
                    .map(sourceField -> {
                        var targetField = new BaseDocumentSignatureTemplateDto.AutoFillField();

                        var type = typesMapById.get(sourceField.getAutoFillTypeId());
                        targetField.setTypeCode(type.getCode().name());
                        targetField.setTypeTitle(type.getTitle());
                        targetField.setTypeId(type.getId());

                        targetField.setId(sourceField.getId());
                        targetField.setIsEditable(sourceField.getJsonSchema() != null || sourceField.getJsonUiSchema() != null);
                        targetField.setLocation(new BaseDocumentSignatureTemplateDto.Location());
                        fillLocation(sourceField.getLocations().get(0), targetField.getLocation(), pageSizes);
                        return targetField;
                    })
                    .collect(Collectors.toList());

            target.setAutoFillFields(fields);
        } else {
            target.setAutoFillFields(List.of());
        }
    }

    private void fillToolboxRequesterFields(
            BaseDocumentSignatureTemplateDto.Configuration target,
            List<DocumentSignatureTemplateField> toolboxRequesterFields,
            List<Pair<Float, Float>> pageSizes
    ) {
        if (CollectionUtils.isNotEmpty(toolboxRequesterFields)) {

            var typesMapById = toolboxRequesterTypeService.getTypesMapById();

            var fields = toolboxRequesterFields.stream()
                    .map(sourceField -> {
                        var targetField = new BaseDocumentSignatureTemplateDto.ToolboxRequesterField();

                        var type = typesMapById.get(sourceField.getToolboxRequesterTypeId());
                        targetField.setTypeCode(type.getCode().name());
                        targetField.setTypeTitle(type.getTitle());
                        targetField.setTypeId(type.getId());
                        targetField.setTitle(sourceField.getTitle());

                        targetField.setId(sourceField.getId());

                        var sourceProperties = templateJsonSchemaService.extractPropertiesFromJsonSchemas(sourceField);
                        var targetProperties = new BaseDocumentSignatureTemplateDto.RequesterFieldProperties();
                        targetProperties.setLabel(sourceProperties.getLabel());
                        targetProperties.setValues(sourceProperties.getValues());
                        targetField.setProperties(targetProperties);

                        targetField.setLocation(new BaseDocumentSignatureTemplateDto.Location());
                        fillLocation(sourceField.getLocations().get(0), targetField.getLocation(), pageSizes);
                        return targetField;
                    })
                    .collect(Collectors.toList());

            target.setToolboxRequesterFields(fields);
        } else {
            target.setToolboxRequesterFields(List.of());
        }
    }

    private void fillLocation(
            DocumentSignatureTemplateFieldLocation source,
            BaseDocumentSignatureTemplateDto.Location target,
            List<Pair<Float, Float>> pageSizes
    ) {
        fieldLocationService.fillUiLocation(target, source, pageSizes.get(source.getPageNo() - 1));
        target.setPageNo((short) (source.getPageNo() - 1));
    }
}
