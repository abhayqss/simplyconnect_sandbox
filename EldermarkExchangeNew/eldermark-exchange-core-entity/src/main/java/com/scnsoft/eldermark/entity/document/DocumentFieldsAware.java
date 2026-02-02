package com.scnsoft.eldermark.entity.document;

import com.scnsoft.eldermark.beans.projection.IdAware;

public interface DocumentFieldsAware extends DocumentFileFieldsAware, IdAware {
    Boolean getIsCDA();

    void setIsCDA(Boolean CDA);
}
