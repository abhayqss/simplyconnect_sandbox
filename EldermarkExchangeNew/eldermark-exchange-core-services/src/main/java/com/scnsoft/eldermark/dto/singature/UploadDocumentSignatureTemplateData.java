package com.scnsoft.eldermark.dto.singature;

import org.springframework.web.multipart.MultipartFile;

public class UploadDocumentSignatureTemplateData extends BaseDocumentSignatureTemplateData {

    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
