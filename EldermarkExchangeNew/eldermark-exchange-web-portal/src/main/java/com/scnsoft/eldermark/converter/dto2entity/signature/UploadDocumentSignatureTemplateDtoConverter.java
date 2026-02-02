package com.scnsoft.eldermark.converter.dto2entity.signature;


import com.scnsoft.eldermark.dto.signature.UploadDocumentSignatureTemplateDto;
import com.scnsoft.eldermark.dto.singature.UploadDocumentSignatureTemplateData;
import org.springframework.stereotype.Component;

@Component
public class UploadDocumentSignatureTemplateDtoConverter extends BaseDocumentSignatureTemplateDtoConverter<UploadDocumentSignatureTemplateDto, UploadDocumentSignatureTemplateData> {

    @Override
    public UploadDocumentSignatureTemplateData convert(UploadDocumentSignatureTemplateDto source) {
        var data = new UploadDocumentSignatureTemplateData();
        fillData(source, data);
        data.setFile(source.getTemplate());
        return data;
    }
}
