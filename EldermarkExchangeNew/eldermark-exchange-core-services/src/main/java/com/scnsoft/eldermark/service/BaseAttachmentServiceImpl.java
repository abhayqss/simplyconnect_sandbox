package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.BaseAttachment;
import com.scnsoft.eldermark.service.storage.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.StreamSupport;

@Transactional
public abstract class BaseAttachmentServiceImpl<T extends BaseAttachment, ID> implements BaseAttachmentService<T, ID> {

    private final FileStorage fileStorage;

    protected BaseAttachmentServiceImpl(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public T saveWithContent(T attachment) {
        var fileName = fileStorage.save(attachment.getContent(), attachment.getOriginalFileName());
        attachment.setFileName(fileName);
        return getAttachmentDao().save(attachment);
    }

    @Override
    @Transactional(readOnly = true)
    public T findByIdWithContent(ID id) {
        var attachment = getAttachmentDao().findById(id).orElseThrow();
        attachment.setContent(fileStorage.loadAsBytes(attachment.getFileName()));
        return attachment;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteWithContent(Iterable<T> attachments) {
        getAttachmentDao().deleteAll(attachments);
        StreamSupport.stream(attachments.spliterator(), false)
            .map(a -> a.getFileName())
            .filter(fileStorage::exists)
            .forEach(fileStorage::delete);
    }

    protected abstract JpaRepository<T, ID> getAttachmentDao();
}
