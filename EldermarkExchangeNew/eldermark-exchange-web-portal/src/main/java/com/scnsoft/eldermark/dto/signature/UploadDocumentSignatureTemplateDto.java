package com.scnsoft.eldermark.dto.signature;

import org.springframework.web.multipart.MultipartFile;

public class UploadDocumentSignatureTemplateDto extends BaseDocumentSignatureTemplateDto {

    private MultipartFile template;

    public MultipartFile getTemplate() {
        return template;
    }

    public void setTemplate(MultipartFile template) {
        this.template = template;
    }
}
