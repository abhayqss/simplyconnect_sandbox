package com.scnsoft.eldermark.converter.dto2entity.signature;

import com.scnsoft.eldermark.dto.signature.UpdateDocumentSignatureTemplateDto;
import com.scnsoft.eldermark.dto.singature.UpdateDocumentSignatureTemplateData;
import org.springframework.stereotype.Component;

@Component
public class UpdateDocumentSignatureTemplateDtoConverter extends BaseDocumentSignatureTemplateDtoConverter<UpdateDocumentSignatureTemplateDto, UpdateDocumentSignatureTemplateData> {

    @Override
    public UpdateDocumentSignatureTemplateData convert(final UpdateDocumentSignatureTemplateDto source) {
        var data = new UpdateDocumentSignatureTemplateData();
        fillData(source, data);
        return data;
    }
}
