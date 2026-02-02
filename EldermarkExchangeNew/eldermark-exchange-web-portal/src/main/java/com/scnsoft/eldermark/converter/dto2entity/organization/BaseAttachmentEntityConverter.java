package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.entity.BaseAttachment;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public abstract class BaseAttachmentEntityConverter<T extends BaseAttachment> implements ListAndItemConverter<MultipartFile, T> {

    private static final Logger logger = LoggerFactory.getLogger(BaseAttachmentEntityConverter.class);

    @Override
    public T convert(MultipartFile source) {
        var target = getAttachment();
        target.setOriginalFileName(source.getOriginalFilename());
        target.setMimeType(source.getContentType());
        try {
            target.setContent(source.getBytes());
        } catch (IOException e) {
            logger.info("Error during getting attachment content {0}", e);
            throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR);
        }
        return target;
    }

    protected abstract T getAttachment();
}