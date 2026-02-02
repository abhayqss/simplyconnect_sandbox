package com.scnsoft.eldermark.services.inbound.document;

import com.scnsoft.eldermark.entity.Document;

public interface DocumentAssignmentListener {

    void postSuccessfulProcessing(Document document);
}
