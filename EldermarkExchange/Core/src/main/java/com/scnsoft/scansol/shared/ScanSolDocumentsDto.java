package com.scnsoft.scansol.shared;

import java.util.List;

/**
 * Date: 19.05.15
 * Time: 10:46
 */
public class ScanSolDocumentsDto {
    List<ScanSolDocumentDto> documents;

    public List<ScanSolDocumentDto> getDocuments () {
        return documents;
    }

    public void setDocuments (List<ScanSolDocumentDto> documents) {
        this.documents = documents;
    }
}
