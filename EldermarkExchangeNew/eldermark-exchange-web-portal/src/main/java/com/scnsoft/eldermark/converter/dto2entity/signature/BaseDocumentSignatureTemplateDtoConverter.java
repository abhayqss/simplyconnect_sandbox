package com.scnsoft.eldermark.converter.dto2entity.signature;

import com.scnsoft.eldermark.dto.signature.BaseDocumentSignatureTemplateDto;
import com.scnsoft.eldermark.dto.singature.BaseDocumentSignatureTemplateData;
import com.scnsoft.eldermark.dto.singature.DocumentSignatureTemplateFieldProperties;
import com.scnsoft.eldermark.entity.signature.*;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.OrganizationService;
import com.scnsoft.eldermark.service.document.signature.template.field.DocumentSignatureTemplateAutoFillFieldTypeService;
import com.scnsoft.eldermark.service.document.signature.template.field.DocumentSignatureTemplateOrganizationAutoFillFieldTypeService;
import com.scnsoft.eldermark.service.document.signature.template.field.DocumentSignatureTemplateToolboxRequesterTypeService;
import com.scnsoft.eldermark.service.document.signature.template.field.DocumentSignatureTemplateToolboxSignerFieldTypeService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseDocumentSignatureTemplateDtoConverter<T extends BaseDocumentSignatureTemplateDto, R extends BaseDocumentSignatureTemplateData> implements Converter<T, R> {

    private final static Logger logger = LoggerFactory.getLogger(BaseDocumentSignatureTemplateDtoConverter.class);

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private DocumentSignatureTemplateAutoFillFieldTypeService autoFillTypeService;

    @Autowired
    private DocumentSignatureTemplateOrganizationAutoFillFieldTypeService organizationAutoFillFieldTypeService;

    @Autowired
    private DocumentSignatureTemplateToolboxRequesterTypeService toolboxRequesterTypeService;

    @Autowired
    private DocumentSignatureTemplateToolboxSignerFieldTypeService toolboxSignerFieldTypeService;

    protected void fillData(T source, R target) {
        target.setTitle(source.getTitle());

        if (CollectionUtils.isNotEmpty(source.getCommunityIds())) {
            target.setCommunities(communityService.findAllById(source.getCommunityIds()));
        } else {
            target.setOrganization(organizationService.findById(source.getOrganizationId()));
        }

        target.setConfiguration(new BaseDocumentSignatureTemplateData.Configuration());
        fillConfiguration(source.getConfiguration(), target.getConfiguration());

        target.setStatus(
                source.getStatusName() != null
                        ? source.getStatusName()
                        : DocumentSignatureTemplateStatus.COMPLETED
        );
    }

    private void fillConfiguration(
            BaseDocumentSignatureTemplateDto.Configuration source,
            BaseDocumentSignatureTemplateData.Configuration target
    ) {
        fillAutoFillFields(source, target);
        fillOrganizationAutoFillFields(source, target);
        fillToolboxRequesterFields(source, target);
        fillToolboxSignerFields(source, target);
        fillRules(source, target);
        fillFolders(source, target);
    }

    private void fillFolders(BaseDocumentSignatureTemplateDto.Configuration source, BaseDocumentSignatureTemplateData.Configuration target) {
        var folderIds = source.getFolders().stream()
                .map(BaseDocumentSignatureTemplateDto.FolderConfiguration::getFolderId)
                .collect(Collectors.toList());

        target.setFolderIds(folderIds);
    }

    private void fillRules(BaseDocumentSignatureTemplateDto.Configuration source, BaseDocumentSignatureTemplateData.Configuration target) {
        if (CollectionUtils.isNotEmpty(source.getRules())) {
            var titles = source.getRules().stream()
                    .map(BaseDocumentSignatureTemplateDto.Rule::getDependentFieldTitle)
                    .collect(Collectors.toList());
            checkUniqueTitles(titles);
            var rules = source.getRules().stream()
                    .map(rule -> {
                        var targetRule = new BaseDocumentSignatureTemplateData.Rule();
                        targetRule.setDependentFieldTitle(rule.getDependentFieldTitle());
                        targetRule.setFieldTitle(rule.getFieldTitle());
                        return targetRule;
                    })
                    .collect(Collectors.toList());
            target.setRules(rules);
        } else {
            target.setRules(Collections.emptyList());
        }
    }

    private void checkUniqueTitles(List<String> titles) {
        var uniqueCount = titles.stream().distinct().count();
        var sourceCount = (long) titles.size();
        if (uniqueCount != sourceCount) {
            throw new BusinessException("Non unique fields");
        }
    }

    private void fillAutoFillFields(
            BaseDocumentSignatureTemplateDto.Configuration source,
            BaseDocumentSignatureTemplateData.Configuration target
    ) {
        if (CollectionUtils.isNotEmpty(source.getAutoFillFields())) {
            var typesMapById = autoFillTypeService.getTypesMapById();
            var fields = source.getAutoFillFields().stream()
                    .map(sourceField -> {
                        var targetField = new BaseDocumentSignatureTemplateData.AutoFillField();
                        fillAutoFillField(sourceField, targetField, typesMapById);
                        return targetField;
                    })
                    .collect(Collectors.toList());
            target.setAutoFillFields(fields);
        } else {
            target.setAutoFillFields(new ArrayList<>());
        }
    }

    private void fillOrganizationAutoFillFields(BaseDocumentSignatureTemplateDto.Configuration source, BaseDocumentSignatureTemplateData.Configuration target) {
        if (CollectionUtils.isNotEmpty(source.getOrganizationAutoFillFields())) {
            var typesMapById = organizationAutoFillFieldTypeService.getTypesMapById();
            var fields = source.getOrganizationAutoFillFields().stream()
                    .map(sourceField -> {
                        var targetField = new BaseDocumentSignatureTemplateData.OrganizationAutoFillField();
                        fillOrganizationAutoFillField(sourceField, targetField, typesMapById);
                        return targetField;
                    })
                    .collect(Collectors.toList());
            target.setOrganizationAutoFillFields(fields);
        } else {
            target.setOrganizationAutoFillFields(new ArrayList<>());
        }
    }

    private void fillToolboxSignerFields(BaseDocumentSignatureTemplateDto.Configuration source, BaseDocumentSignatureTemplateData.Configuration target) {
        if (CollectionUtils.isNotEmpty(source.getToolboxSignerFields())) {
            var titles = source.getToolboxSignerFields().stream()
                    .map(BaseDocumentSignatureTemplateDto.ToolboxSignerField::getTitle)
                    .collect(Collectors.toList());
            checkUniqueTitles(titles);
            var typesMapById = toolboxSignerFieldTypeService.getTypesMapById();
            var fields = source.getToolboxSignerFields().stream()
                    .map(sourceField -> {
                        var targetField = new BaseDocumentSignatureTemplateData.ToolboxSignerField();
                        fillToolboxSignerField(sourceField, targetField, typesMapById);
                        return targetField;
                    })
                    .collect(Collectors.toList());
            target.setToolboxSignerFields(fields);
        } else {
            target.setToolboxSignerFields(new ArrayList<>());
        }
    }

    private void fillToolboxRequesterFields(
            BaseDocumentSignatureTemplateDto.Configuration source,
            BaseDocumentSignatureTemplateData.Configuration target
    ) {
        if (CollectionUtils.isNotEmpty(source.getToolboxRequesterFields())) {
            var titles = source.getToolboxRequesterFields().stream()
                    .map(BaseDocumentSignatureTemplateDto.ToolboxRequesterField::getTitle)
                    .collect(Collectors.toList());
            checkUniqueTitles(titles);
            var typesMapById = toolboxRequesterTypeService.getTypesMapById();
            var fields = source.getToolboxRequesterFields().stream()
                    .map(sourceField -> {
                        var targetField = new BaseDocumentSignatureTemplateData.ToolboxRequesterField();
                        fillToolboxRequesterField(sourceField, targetField, typesMapById);
                        return targetField;
                    })
                    .collect(Collectors.toList());
            target.setToolboxRequesterFields(fields);
        } else {
            target.setToolboxRequesterFields(new ArrayList<>());
        }
    }

    private void fillToolboxSignerField(
            BaseDocumentSignatureTemplateDto.ToolboxSignerField source,
            BaseDocumentSignatureTemplateData.ToolboxSignerField target,
            Map<Long, DocumentSignatureTemplateToolboxSignerFieldType> typesMapById
    ) {
        target.setId(source.getId());
        target.setLocation(new BaseDocumentSignatureTemplateData.Location());
        target.setType(typesMapById.get(source.getTypeId()));
        target.setTitle(source.getTitle());

        fillLocation(source.getLocation(), target.getLocation());
    }

    private void fillOrganizationAutoFillField(
            BaseDocumentSignatureTemplateDto.OrganizationAutoFillField source,
            BaseDocumentSignatureTemplateData.OrganizationAutoFillField target,
            Map<Long, DocumentSignatureTemplateOrganizationAutoFillFieldType> typesMapById
    ) {
        target.setId(source.getId());
        target.setLocation(new BaseDocumentSignatureTemplateData.Location());
        target.setType(typesMapById.get(source.getTypeId()));

        fillLocation(source.getLocation(), target.getLocation());
    }

    private void fillAutoFillField(
            BaseDocumentSignatureTemplateDto.AutoFillField source,
            BaseDocumentSignatureTemplateData.AutoFillField target,
            Map<Long, DocumentSignatureTemplateAutoFillFieldType> typesMapById
    ) {
        target.setId(source.getId());
        target.setEditable(source.getIsEditable());
        target.setLocation(new BaseDocumentSignatureTemplateData.Location());
        target.setType(typesMapById.get(source.getTypeId()));

        fillLocation(source.getLocation(), target.getLocation());
    }

    private void fillToolboxRequesterField(
            BaseDocumentSignatureTemplateDto.ToolboxRequesterField source,
            BaseDocumentSignatureTemplateData.ToolboxRequesterField target,
            Map<Long, DocumentSignatureTemplateToolboxRequesterFieldType> typesMapById
    ) {
        target.setId(source.getId());
        target.setTitle(source.getTitle());
        target.setType(typesMapById.get(source.getTypeId()));

        target.setProperties(new DocumentSignatureTemplateFieldProperties());
        target.getProperties().setLabel(source.getProperties().getLabel());
        target.getProperties().setValues(source.getProperties().getValues());

        target.setLocation(new BaseDocumentSignatureTemplateData.Location());
        fillLocation(source.getLocation(), target.getLocation());
    }

    private void fillLocation(
            BaseDocumentSignatureTemplateDto.Location source,
            BaseDocumentSignatureTemplateData.Location target
    ) {
        target.setBottomRightY(source.getBottomRightY());
        target.setBottomRightX(source.getBottomRightX());
        target.setPageNo(source.getPageNo());
        target.setTopLeftX(source.getTopLeftX());
        target.setTopLeftY(source.getTopLeftY());
    }
}
