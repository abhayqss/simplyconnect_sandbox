package com.scnsoft.eldermark.converter.entity2dto.signature;

import com.scnsoft.eldermark.dto.document.BaseDocumentDto;
import com.scnsoft.eldermark.entity.document.DocumentAndFolderType;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureTemplateSecurityService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class DocumentTemplateDtoConverter implements Converter<DocumentSignatureTemplate, BaseDocumentDto> {

    @Autowired
    private DocumentSignatureTemplateService templateService;

    @Autowired
    private DocumentSignatureTemplateSecurityService securityService;

    @Override
    public BaseDocumentDto convert(DocumentSignatureTemplate source) {
        var target = new BaseDocumentDto();

        target.setTitle(source.getTitle() + ".pdf");
        target.setAuthor("System");
        target.setMimeType(MediaType.APPLICATION_PDF_VALUE);
        target.setSize(templateService.getTemplatePdfSize(source));
        target.setType(DocumentAndFolderType.TEMPLATE.name());

        target.setCanDelete(securityService.canDelete(source));
        target.setCanEdit(securityService.canEdit(source));
        target.setIsTemporarilyDeleted(false);
        return target;
    }
}
