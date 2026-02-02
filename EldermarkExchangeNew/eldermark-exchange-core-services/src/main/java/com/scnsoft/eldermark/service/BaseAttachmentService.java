package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.BaseAttachment;

public interface BaseAttachmentService<T extends BaseAttachment, ID> {

    T saveWithContent(T attachment);

    T findByIdWithContent(ID id);

    void deleteWithContent(Iterable<T> attachments);
}
