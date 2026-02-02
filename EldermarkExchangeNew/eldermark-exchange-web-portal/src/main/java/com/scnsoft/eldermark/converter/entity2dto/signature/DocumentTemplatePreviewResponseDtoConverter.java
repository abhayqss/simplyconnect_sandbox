package com.scnsoft.eldermark.converter.entity2dto.signature;

import com.scnsoft.eldermark.dto.signature.DocumentSignatureTemplatePreviewResponseDto;
import com.scnsoft.eldermark.dto.signature.DocumentSignatureTemplateSignatureAreaDto;
import com.scnsoft.eldermark.dto.singature.DocumentTemplatePreview;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureFieldLocationService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignaturePdfService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DocumentTemplatePreviewResponseDtoConverter
        implements Converter<DocumentTemplatePreview, DocumentSignatureTemplatePreviewResponseDto> {

    @Autowired
    private DocumentSignaturePdfService signaturePdfService;

    @Autowired
    private DocumentSignatureFieldLocationService signatureFieldLocationService;

    @Autowired
    private DocumentSignatureTemplateService signatureTemplateService;

    @Override
    public DocumentSignatureTemplatePreviewResponseDto convert(DocumentTemplatePreview preview) {

        var context = preview.getTemplateContext();

        var originalTemplatePageCount = signatureTemplateService.getTemplatePdfPageSizes(context.getTemplate()).size();

        var templatePageSizes = signaturePdfService.getPdfPageSizes(preview.getData());
        var pageOffset = templatePageSizes.size() - originalTemplatePageCount;
        var signatureAreas = preview.getSignatureAreas().stream()
                .map(field -> {
                    var target = new DocumentSignatureTemplateSignatureAreaDto();
                    target.setId(field.getId());
                    signatureFieldLocationService.fillUiLocation(target, field.getLocation(), templatePageSizes, pageOffset);
                    return target;
                })
                .collect(Collectors.toList());

        var responseDto = new DocumentSignatureTemplatePreviewResponseDto();
        responseDto.setData(preview.getData());
        responseDto.setSignatureAreas(signatureAreas);
        return responseDto;
    }
}
