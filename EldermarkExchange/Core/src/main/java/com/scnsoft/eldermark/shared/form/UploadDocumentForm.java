package com.scnsoft.eldermark.shared.form;

import com.scnsoft.eldermark.shared.SharingOption;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class UploadDocumentForm {
    private SharingOption sharingOption;

    private CommonsMultipartFile document;

    public SharingOption getSharingOption() {
        return sharingOption;
    }

    public void setSharingOption(SharingOption sharingOption) {
        this.sharingOption = sharingOption;
    }

    public CommonsMultipartFile getDocument() {
        return document;
    }

    public void setDocument(CommonsMultipartFile document) {
        this.document = document;
    }
}
