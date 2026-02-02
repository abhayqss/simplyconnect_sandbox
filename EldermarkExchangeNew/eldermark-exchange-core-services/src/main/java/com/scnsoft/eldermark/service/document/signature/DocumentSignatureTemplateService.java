package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.beans.projection.DocumentSignatureTemplateFileAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dto.singature.*;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestSubmittedField;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateSchema;
import com.scnsoft.eldermark.service.ProjectingService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface DocumentSignatureTemplateService extends ProjectingService<Long> {
    <P> List<P> getProjectedTemplatesByCommunityId(
            Long communityId,
            Class<P> projectionClass,
            Sort sort,
            PermissionFilter permissionFilter
    );

    <P> List<P> getProjectedTemplatesByCommunityIds(
            List<Long> communityIds,
            Boolean isManuallyCreated,
            Class<P> projectionClass,
            Sort sort,
            PermissionFilter permissionFilter
    );

    DocumentSignatureTemplate create(UploadDocumentSignatureTemplateData templateData);

    Long countByCommunityId(Long communityId, PermissionFilter permissionFilter);

    boolean existsByCommunityId(Long communityId, PermissionFilter permissionFilter);

    DocumentSignatureTemplate findById(Long id);

    List<DocumentSignatureFieldData> getAvailableSignatureAreas(DocumentSignatureTemplateContext context);

    DocumentSignatureTemplate findByIdAndCommunityId(Long id, Long communityId);

    int getTemplatePdfSize(DocumentSignatureTemplateFileAware template);

    DocumentTemplatePreview getTemplatePreview(DocumentSignatureTemplateContext context);

    List<Pair<Float, Float>> getTemplatePdfPageSizes(DocumentSignatureTemplateFileAware template);

    byte[] getTemplatePdf(DocumentSignatureTemplateContext context);

    byte[] getTemplatePdf(DocumentSignatureTemplateFileAware template, List<DocumentSignatureRequestSubmittedField> fields);

    DocumentSignatureTemplate update(UpdateDocumentSignatureTemplateData data);

    void delete(Long templateId, Employee currentEmployee);

    void save(DocumentSignatureTemplate template);

    DocumentSignatureTemplateSchema generateSchema(DocumentSignatureTemplateContext context);
}
