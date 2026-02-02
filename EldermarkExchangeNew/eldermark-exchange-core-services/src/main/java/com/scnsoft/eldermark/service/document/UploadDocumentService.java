package com.scnsoft.eldermark.service.document;

import com.scnsoft.eldermark.entity.document.BaseUploadData;
import com.scnsoft.eldermark.entity.document.Document;

public interface UploadDocumentService<T extends BaseUploadData> {

    Document upload(T data);
}
