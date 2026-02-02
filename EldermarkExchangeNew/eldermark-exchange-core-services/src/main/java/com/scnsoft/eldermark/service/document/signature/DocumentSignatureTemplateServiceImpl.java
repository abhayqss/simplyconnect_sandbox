package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.beans.projection.DocumentSignatureTemplateFileAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.NameAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.signature.DocumentSignatureTemplateDao;
import com.scnsoft.eldermark.dao.specification.DocumentSignatureTemplateSpecificationGenerator;
import com.scnsoft.eldermark.dto.singature.*;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.folder.DocumentFolder;
import com.scnsoft.eldermark.entity.signature.*;
import com.scnsoft.eldermark.exception.*;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.document.DocumentService;
import com.scnsoft.eldermark.service.document.folder.DocumentFolderService;
import com.scnsoft.eldermark.service.storage.DocumentSignatureTemplateFileStorage;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.CaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class DocumentSignatureTemplateServiceImpl implements DocumentSignatureTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentSignatureTemplateServiceImpl.class);

    private static final String TEMPLATES_RESOURCE_PATH = "documents/signature/templates/";

    @Autowired
    private DocumentSignatureTemplateDao signatureTemplateDao;

    @Autowired
    private DocumentSignatureTemplateSpecificationGenerator documentSignatureTemplateSpecificationGenerator;

    @Autowired
    private DocumentSignaturePdfService documentSignaturePdfService;

    @Autowired
    private DocumentSignatureTemplateFieldService signatureTemplateFieldService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentSignatureTemplateFileStorage templateFileStorage;

    @Autowired
    private DocumentSignatureTemplateJsonSchemaService templateJsonSchemaService;

    @Autowired
    private DocumentSignatureFieldLocationService fieldLocationService;

    @Autowired
    private DocumentSignatureFieldStyleService fieldStyleService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private DocumentFolderService folderService;


    @Override
    @Transactional(readOnly = true)
    public <P> List<P> getProjectedTemplatesByCommunityId(
            Long communityId,
            Class<P> projectionClass,
            Sort sort,
            PermissionFilter permissionFilter
    ) {
        var spec = documentSignatureTemplateSpecificationGenerator.byPermissionFilterAndCommunityId(permissionFilter, communityId);
        return signatureTemplateDao.findAll(spec, projectionClass, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> getProjectedTemplatesByCommunityIds(
            List<Long> communityIds,
            Boolean isManuallyCreated,
            Class<P> projectionClass,
            Sort sort,
            PermissionFilter permissionFilter
    ) {
        var spec =
                documentSignatureTemplateSpecificationGenerator.byPermissionFilterAndCommunityIds(permissionFilter, communityIds);

        if (isManuallyCreated != null) {
            var manuallyCreated = documentSignatureTemplateSpecificationGenerator.manuallyCreated();
            spec = spec.and(
                    isManuallyCreated
                            ? manuallyCreated
                            : Specification.not(manuallyCreated)
            );
        }

        return signatureTemplateDao.findAll(spec, projectionClass, sort);
    }

    @Override
    @Transactional
    public DocumentSignatureTemplate create(UploadDocumentSignatureTemplateData templateData) {

        var fileName = templateFileStorage.save(templateData.getFile());

        var template = new DocumentSignatureTemplate();
        template.setName(fileName);
        template.setIsManuallyCreated(true);
        template.setFields(new ArrayList<>());
        template.setCreatedBy(templateData.getAuthor());
        template.setCreationDatetime(Instant.now());

        fillTemplate(template, templateData);

        return signatureTemplateDao.save(template);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByCommunityId(Long communityId, PermissionFilter permissionFilter) {
        var spec = documentSignatureTemplateSpecificationGenerator.byPermissionFilterAndCommunityId(permissionFilter, communityId);
        return signatureTemplateDao.count(spec);
    }

    @Override
    public boolean existsByCommunityId(Long communityId, PermissionFilter permissionFilter) {
        var spec = documentSignatureTemplateSpecificationGenerator.byPermissionFilterAndCommunityId(permissionFilter, communityId);
        return signatureTemplateDao.exists(spec);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentSignatureTemplate findById(Long id) {
        return signatureTemplateDao.findById(id).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentSignatureFieldData> getAvailableSignatureAreas(DocumentSignatureTemplateContext context) {

        if (context.getDocument() != null) {
            return context.getDocument().getSignatureRequest().getNotSubmittedFields().stream()
                    .filter(it -> it.getPdcFlowType() == TemplateFieldPdcFlowType.SIGNATURE)
                    .map(DocumentSignatureRequestNotSubmittedFieldDataAdapter::new)
                    .collect(Collectors.toList());

        } else {
            return context.getTemplate().getFields()
                    .stream()
                    .filter(it -> it.getPdcFlowType() == TemplateFieldPdcFlowType.SIGNATURE)
                    .map(DocumentSignatureTemplateFieldDataAdapter::new)
                    .collect(Collectors.toList());

        }
    }

    @Override
    public DocumentSignatureTemplate findByIdAndCommunityId(Long id, Long communityId) {
        return signatureTemplateDao.findOne(documentSignatureTemplateSpecificationGenerator.byIdAndCommunityId(id, communityId))
                .orElse(null);
    }

    @Override
    public int getTemplatePdfSize(DocumentSignatureTemplateFileAware template) {
        // todo improve size calculating
        return getTemplatePdf(template, List.of()).length;
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentTemplatePreview getTemplatePreview(DocumentSignatureTemplateContext context) {

        var data = context.getDocument() == null
                ? getTemplatePdf(context.getTemplate(), signatureTemplateFieldService.createScSubmittedFields(context))
                : documentService.readDocumentAsBytes(context.getDocument());

        var availableSignatureAreas = getAvailableSignatureAreas(context);
        return new DocumentTemplatePreview(
                data,
                availableSignatureAreas,
                context
        );
    }

    @Override
    public List<Pair<Float, Float>> getTemplatePdfPageSizes(DocumentSignatureTemplateFileAware template) {
        return documentSignaturePdfService.getPdfPageSizes(getTemplatePdf(template, List.of()));
    }

    @Override
    public byte[] getTemplatePdf(DocumentSignatureTemplateContext context) {
        return getTemplatePdf(context.getTemplate(), signatureTemplateFieldService.createScSubmittedFields(context));
    }

    @Override
    public byte[] getTemplatePdf(DocumentSignatureTemplateFileAware template, List<DocumentSignatureRequestSubmittedField> fields) {
        try (var templateStream = getTemplateAsStream(template)) {
            return documentSignaturePdfService.writeFieldsToPdf(fields, templateStream);
        } catch (IOException e) {
            throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR);
        }
    }

    @Override
    public DocumentSignatureTemplate update(UpdateDocumentSignatureTemplateData templateData) {
        var template = signatureTemplateDao.findById(templateData.getTemplateId()).orElseThrow();
        template.setUpdatedBy(templateData.getAuthor());
        template.setUpdateDatetime(Instant.now());
        fillTemplate(template, templateData);
        return signatureTemplateDao.save(template);
    }

    @Override
    public void delete(Long templateId, Employee currentEmployee) {
        var template = signatureTemplateDao.findById(templateId).orElseThrow();
        template.setDeletedBy(currentEmployee);
        template.setDeleteDatetime(Instant.now());
        template.setStatus(DocumentSignatureTemplateStatus.DELETED);
        signatureTemplateDao.save(template);
    }

    @Override
    public void save(DocumentSignatureTemplate template) {
        signatureTemplateDao.save(template);
    }

    @Override
    public DocumentSignatureTemplateSchema generateSchema(DocumentSignatureTemplateContext context) {

        var schema = new DocumentSignatureTemplateSchema();

        if (!context.getTemplate().getIsManuallyCreated() || !context.getIsRequestFromMultipleClients()) {
            schema.setFormSchema(context.getTemplate().getFormSchema());
            schema.setUiFormSchema(context.getTemplate().getFormUiSchema());
        } else {
            Predicate<DocumentSignatureTemplateField> fieldPredicate = field -> Optional.of(field)
                    .map(DocumentSignatureTemplateField::getDefaultValueType)
                    .map(TemplateFieldDefaultValueType::getIsClientField)
                    .map(isClientDefaultField -> !isClientDefaultField)
                    .orElse(true);
            schema.setFormSchema(templateJsonSchemaService.generateJsonSchema(context.getTemplate(), fieldPredicate));
            schema.setUiFormSchema(templateJsonSchemaService.generateJsonUiSchema(context.getTemplate(), fieldPredicate));
        }

        schema.setDefaultValues(signatureTemplateFieldService.findDefaults(context));

        return schema;
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long id, Class<P> projection) {
        return signatureTemplateDao.findById(id, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return signatureTemplateDao.findByIdIn(ids, projection);
    }

    private InputStream getTemplateAsStream(DocumentSignatureTemplateFileAware template) {
        if (template.getIsManuallyCreated()) {
            return templateFileStorage.loadAsInputStream(template.getName());
        } else {
            return Thread.currentThread().getContextClassLoader().getResourceAsStream(getTemplateResourceName(template));
        }
    }

    private String getTemplateResourceName(NameAware template) {
        return TEMPLATES_RESOURCE_PATH + template.getName() + ".pdf";
    }

    private void fillTemplate(DocumentSignatureTemplate template, BaseDocumentSignatureTemplateData data) {
        template.setTitle(data.getTitle());
        if (CollectionUtils.isNotEmpty(data.getCommunities())) {
            CareCoordinationUtils.setEmptyListIfNullAndClear(template::getOrganizations, template::setOrganizations);
            CareCoordinationUtils.setEmptyListIfNullAndClear(template::getCommunities, template::setCommunities);

            data.getCommunities().stream()
                    .filter(communityService::isEligibleForDiscovery)
                    .forEach(template.getCommunities()::add);

            if (data.getCommunities().size() != template.getCommunities().size()) {
                var excludedCommunities = data.getCommunities().stream()
                        .map(BasicEntity::getId)
                        .filter(communityId -> template.getCommunities().stream().map(BasicEntity::getId).noneMatch(communityId::equals))
                        .collect(Collectors.toList());

                logger.info("Template add/edit: Filtered out communities {}", excludedCommunities);
            }

            if (data.getCommunities().isEmpty()) {
                throw new ValidationException("Invalid template configuration: no accessible communities provided");
            }
        } else if (data.getOrganization() != null) {
            CareCoordinationUtils.setEmptyListIfNullAndClear(template::getOrganizations, template::setOrganizations);
            CareCoordinationUtils.setEmptyListIfNullAndClear(template::getCommunities, template::setCommunities);
            template.getOrganizations().add(data.getOrganization());
        }

        updateTemplateStatus(template, data);

        fillFolderConfiguration(template, data);
        fillFields(template, data);
    }

    private void fillFolderConfiguration(DocumentSignatureTemplate template, BaseDocumentSignatureTemplateData data) {
        Set<Long> newFoldersIds = resolveFolders(template, data);

        if (template.getFolderIds() == null) {
            template.setFolderIds(new HashSet<>());
        }

        template.getFolderIds().clear();
        template.getFolderIds().addAll(newFoldersIds);
    }

    //exclude folders from non-eligible communities (possible in case community becomes non-eligible during template update by user)
    //add missing folders (possible in case of org template when community in org becomes eligible during template update by user)
    private Set<Long> resolveFolders(DocumentSignatureTemplate template,
                                     BaseDocumentSignatureTemplateData source) {
        var submittedFolderIds = source.getConfiguration().getFolderIds();
        var submittedFolders = folderService.findByIdIn(submittedFolderIds);

        var communityIdsEligible = (CollectionUtils.isNotEmpty(template.getCommunities()) ?
                template.getCommunities() : communityService.findAllByOrgId(source.getOrganization().getId()))
                .stream()
                .map(IdAware::getId)
                .collect(Collectors.toSet());

        var filteredFolders = submittedFolders.stream()
                .filter(folder -> communityIdsEligible.contains(folder.getCommunityId()))
                .collect(Collectors.toList());

        if (submittedFolders.size() != filteredFolders.size()) {
            var excludedFolders = submittedFolders.stream()
                    .filter(folder -> filteredFolders.stream().map(DocumentFolder::getId).noneMatch(folder.getId()::equals))
                    .map(this::folderInLogRepresentation)
                    .collect(Collectors.joining(", ", "[", "]"));

            logger.info("Template add/edit: Filtered out folders {}", excludedFolders);
        }

        var communityIdsFromFilteredFolders = submittedFolders.stream()
                .map(DocumentFolder::getCommunityId)
                .collect(Collectors.toSet());

        var communitiesWithMissingFolders = communityIdsEligible.stream()
                .filter(communityId -> !communityIdsFromFilteredFolders.contains(communityId))
                .collect(Collectors.toList());

        //load default folders
        var missingFolders = communitiesWithMissingFolders.isEmpty() ?
                List.<DocumentFolder>of() :
                folderService.findDefaultTemplateFolders(communitiesWithMissingFolders);

        if (!communitiesWithMissingFolders.isEmpty()) {
            logger.info("Template add/edit: communities with missing folders - {}", communitiesWithMissingFolders);
            logger.info("Template add/edit: loaded missing folders - {}", missingFolders.stream()
                    .map(this::folderInLogRepresentation)
                    .collect(Collectors.joining(", ", "[", "]")));
        }

        return Stream.of(filteredFolders, missingFolders)
                .flatMap(Collection::stream)
                .map(DocumentFolder::getId)
                .collect(Collectors.toSet());
    }

    private String folderInLogRepresentation(DocumentFolder folder) {
        return "(id: " + folder.getId() + ", communityId " + folder.getCommunityId() + ")";
    }

    private void fillFields(DocumentSignatureTemplate template, BaseDocumentSignatureTemplateData data) {
        var pageSizes = getTemplatePdfPageSizes(template);
        fillAutoFillFields(template, data.getConfiguration().getAutoFillFields(), pageSizes);
        fillOrganizationAutoFillFields(template, data.getConfiguration().getOrganizationAutoFillFields(), pageSizes);
        fillToolboxRequesterFields(template, data.getConfiguration().getToolboxRequesterFields(), pageSizes);
        fillToolboxSignerFields(template, data.getConfiguration().getToolboxSignerFields(), pageSizes);

        template.getFields().stream()
                .filter(it -> it.getScSourceFieldType() != null)
                .forEach(fieldStyleService::populateDefaultTextFieldStyles);

        templateJsonSchemaService.fillJsonSchemasGeneratedByTemplateFields(template);

        template.getFields().forEach(field -> field.setRelatedFieldId(null));

        signatureTemplateDao.save(template);

        Optional.of(data)
                .map(BaseDocumentSignatureTemplateData::getConfiguration)
                .map(BaseDocumentSignatureTemplateData.Configuration::getRules)
                .ifPresent(rules -> rules.forEach(rule -> {
                    var relatedField = template.getFields().stream()
                            .filter(field -> Objects.equals(field.getPdcFlowType(), TemplateFieldPdcFlowType.SIGNATURE))
                            .filter(field -> Objects.equals(field.getTitle(), rule.getFieldTitle()))
                            .findFirst()
                            .orElseThrow(() -> new BusinessException("Invalid rule: invalid signature field", rule.getFieldTitle()));

                    template.getFields().stream()
                            .filter(field -> field.getToolboxSignerFieldType() != null)
                            .filter(field -> Objects.equals(field.getTitle(), rule.getDependentFieldTitle()))
                            .findFirst()
                            .map(field -> {
                                field.setRelatedFieldId(relatedField.getId());
                                return field;
                            })
                            .orElseThrow(() -> new BusinessException("Invalid rule: invalid dependent field", rule.getDependentFieldTitle()));
                }));
    }

    private void updateTemplateStatus(DocumentSignatureTemplate template, BaseDocumentSignatureTemplateData data) {
        var status = data.getStatus();
        if (DocumentSignatureTemplateStatus.DELETED.equals(status)) {
            throw new ValidationException(data.getTitle() + " can't have '" + status + "' status");
        }
        template.setStatus(status);
    }

    private void fillOrganizationAutoFillFields(
            DocumentSignatureTemplate template,
            List<BaseDocumentSignatureTemplateData.OrganizationAutoFillField> organizationAutoFillFields,
            List<Pair<Float, Float>> pageSizes
    ) {
        if (CollectionUtils.isNotEmpty(organizationAutoFillFields)) {
            var fieldIdsToUpdate = organizationAutoFillFields.stream()
                    .map(BaseDocumentSignatureTemplateData.OrganizationAutoFillField::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            template.getFields()
                    .removeIf(it -> it.getOrganizationAutoFillTypeId() != null && !fieldIdsToUpdate.contains(it.getId()));

            organizationAutoFillFields.forEach(dto -> {
                DocumentSignatureTemplateField field;
                if (dto.getId() != null) {
                    field = template.getFields().stream()
                            .filter(it -> Objects.equals(it.getId(), dto.getId()))
                            .findFirst()
                            .orElseThrow(() -> new BusinessException(BusinessExceptionType.NOT_FOUND,
                                    "Invalid Organization Auto-Fill id"));
                } else {
                    field = new DocumentSignatureTemplateField();
                    field.setSignatureTemplate(template);
                }

                field.setOrganizationAutoFillTypeId(dto.getType().getId());
                field.setOrganizationAutoFillType(dto.getType());
                field.setName(CaseUtils.toCamelCase(dto.getType().getCode().name(), false, '_'));
                field.setScSourceFieldType(dto.getType().getCode());

                fillLocation(field, dto.getLocation(), pageSizes);

                template.getFields().add(field);
            });
        } else {
            template.getFields().removeIf(it -> it.getOrganizationAutoFillTypeId() != null);
        }
    }

    private void fillToolboxSignerFields(
            DocumentSignatureTemplate template,
            List<BaseDocumentSignatureTemplateData.ToolboxSignerField> toolboxSignerFields,
            List<Pair<Float, Float>> pageSizes
    ) {
        if (CollectionUtils.isNotEmpty(toolboxSignerFields)) {
            var fieldIdsToUpdate = toolboxSignerFields.stream()
                    .map(BaseDocumentSignatureTemplateData.ToolboxSignerField::getId)
                    .collect(Collectors.toSet());

            template.getFields()
                    .removeIf(it -> it.getToolboxSignerTypeId() != null && !fieldIdsToUpdate.contains(it.getId()));

            toolboxSignerFields.forEach(dto -> {
                DocumentSignatureTemplateField field;
                if (dto.getId() != null) {
                    field = template.getFields().stream()
                            .filter(it -> Objects.equals(it.getId(), dto.getId()))
                            .findFirst()
                            .orElseThrow(() -> new BusinessException(BusinessExceptionType.NOT_FOUND, "Invalid Toolbox-Signer id"));
                } else {
                    field = new DocumentSignatureTemplateField();
                    field.setSignatureTemplate(template);
                }

                field.setToolboxSignerTypeId(dto.getType().getId());
                field.setToolboxSignerFieldType(dto.getType());
                field.setName(CaseUtils.toCamelCase(dto.getTitle(), false, ' '));
                field.setToolboxSignerFieldType(dto.getType());
                field.setPdcFlowType(dto.getType().getCode());
                field.setTitle(dto.getTitle());

                fillLocation(field, dto.getLocation(), pageSizes);

                template.getFields().add(field);
            });
        } else {
            template.getFields().removeIf(it -> it.getToolboxSignerTypeId() != null);
        }
    }

    private void fillAutoFillFields(
            DocumentSignatureTemplate template,
            List<BaseDocumentSignatureTemplateData.AutoFillField> autoFillFields,
            List<Pair<Float, Float>> pageSizes
    ) {
        if (CollectionUtils.isNotEmpty(autoFillFields)) {
            var fieldIdsToUpdate = autoFillFields.stream()
                    .map(BaseDocumentSignatureTemplateData.AutoFillField::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            template.getFields()
                    .removeIf(it -> it.getAutoFillTypeId() != null && !fieldIdsToUpdate.contains(it.getId()));

            autoFillFields.forEach(dto -> {
                DocumentSignatureTemplateField field;
                if (dto.getId() != null) {
                    field = template.getFields().stream()
                            .filter(it -> Objects.equals(it.getId(), dto.getId()))
                            .findFirst()
                            .orElseThrow(() -> new BusinessException(BusinessExceptionType.NOT_FOUND, "Invalid Auto-Fill id"));
                } else {
                    field = new DocumentSignatureTemplateField();
                    field.setSignatureTemplate(template);
                }

                if (dto.isEditable()) {
                    field.setJsonSchema(dto.getType().getJsonSchema());
                    field.setJsonUiSchema(dto.getType().getJsonUiSchema());
                }

                field.setAutoFillTypeId(dto.getType().getId());
                field.setAutoFillType(dto.getType());
                field.setName(CaseUtils.toCamelCase(dto.getType().getCode().name(), false, '_'));
                field.setScSourceFieldType(dto.getType().getScFieldType());
                field.setDefaultValueType(dto.getType().getCode());

                fillLocation(field, dto.getLocation(), pageSizes);

                template.getFields().add(field);
            });
        } else {
            template.getFields().removeIf(it -> it.getAutoFillTypeId() != null);
        }
    }

    private void fillToolboxRequesterFields(
            DocumentSignatureTemplate template,
            List<BaseDocumentSignatureTemplateData.ToolboxRequesterField> toolboxRequesterFields,
            List<Pair<Float, Float>> pageSizes
    ) {
        if (CollectionUtils.isNotEmpty(toolboxRequesterFields)) {
            var fieldIdsToUpdate = toolboxRequesterFields.stream()
                    .map(BaseDocumentSignatureTemplateData.ToolboxRequesterField::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            template.getFields()
                    .removeIf(it -> it.getToolboxRequesterTypeId() != null && !fieldIdsToUpdate.contains(it.getId()));

            toolboxRequesterFields.forEach(dto -> {
                DocumentSignatureTemplateField field;
                if (dto.getId() != null) {
                    field = template.getFields().stream()
                            .filter(it -> Objects.equals(it.getId(), dto.getId()))
                            .findFirst()
                            .orElseThrow(() -> new BusinessException(BusinessExceptionType.NOT_FOUND, "Invalid Toolbox Requester filed id"));
                } else {
                    field = new DocumentSignatureTemplateField();
                    field.setSignatureTemplate(template);
                }

                field.setName(CaseUtils.toCamelCase(dto.getTitle(), false, ' '));

                field.setToolboxRequesterTypeId(dto.getType().getId());
                field.setToolboxRequesterType(dto.getType());
                field.setTitle(dto.getTitle());
                field.setScSourceFieldType(dto.getType().getScFieldType());

                fillLocation(field, dto.getLocation(), pageSizes);

                templateJsonSchemaService.fillJsonSchemasForToolboxRequesterField(field, dto.getProperties());

                template.getFields().add(field);
            });
        } else {
            template.getFields().removeIf(it -> it.getToolboxRequesterTypeId() != null);
        }
    }

    private void fillLocation(
            DocumentSignatureTemplateField field,
            BaseDocumentSignatureTemplateData.Location location,
            List<Pair<Float, Float>> pageSizes
    ) {
        DocumentSignatureTemplateFieldLocation fieldLocation;
        if (CollectionUtils.isNotEmpty(field.getLocations())) {
            fieldLocation = field.getLocations().get(0);
        } else {
            fieldLocation = new DocumentSignatureTemplateFieldLocation();
            fieldLocation.setField(field);
            field.setLocations(new ArrayList<>());
            field.getLocations().add(fieldLocation);
        }

        if (location.getPageNo() >= pageSizes.size()) {
            throw new BusinessException("Invalid page number");
        }

        var pageSize = pageSizes.get(location.getPageNo());
        fieldLocation.setPageNo((short) (location.getPageNo() + 1));

        fieldLocationService.fillFromUiLocation(fieldLocation, location, pageSize);
    }
}
