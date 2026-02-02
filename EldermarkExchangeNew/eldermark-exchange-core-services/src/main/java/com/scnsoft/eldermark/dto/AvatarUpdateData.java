package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.entity.EntityWithAvatar;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class AvatarUpdateData {

    private final EntityWithAvatar entityWithAvatar;

    private final String name;
    private final byte[] bytes;
    private final String mimeType;

    private final boolean shouldRemoveAvatar;

    private AvatarUpdateData(EntityWithAvatar entityWithAvatar, String name, byte[] bytes, String mimeType, boolean shouldRemoveAvatar) {
        this.entityWithAvatar = entityWithAvatar;
        this.name = name;
        this.bytes = bytes;
        this.mimeType = mimeType;
        this.shouldRemoveAvatar = shouldRemoveAvatar;
    }

    public AvatarUpdateData(EntityWithAvatar entityWithAvatar, byte[] byes, String mimeType) {
        this(entityWithAvatar, null, byes, mimeType, false);
    }

    public AvatarUpdateData(EntityWithAvatar entityWithAvatar, MultipartFile file, Boolean shouldRemoveAvatar) {
        if (file != null && Boolean.TRUE.equals(shouldRemoveAvatar)) {
            throw new IllegalArgumentException("only file or shouldRemoveAvatar argument should be present");
        }

        this.entityWithAvatar = entityWithAvatar;

        if (file != null) {
            try {
                this.name = file.getOriginalFilename();
                this.bytes = IOUtils.toByteArray(file.getInputStream());
                this.mimeType = null;
                this.shouldRemoveAvatar = false;
            } catch (IOException e) {
                throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR);
            }
        } else {
            this.name = null;
            this.mimeType = null;
            this.bytes = null;
            this.shouldRemoveAvatar = Boolean.TRUE.equals(shouldRemoveAvatar);
        }
    }

    public AvatarUpdateData(EntityWithAvatar entityWithAvatar, boolean shouldRemoveAvatar) {
        this(entityWithAvatar, null, shouldRemoveAvatar);
    }

    public EntityWithAvatar getEntityWithAvatar() {
        return entityWithAvatar;
    }

    public String getName() {
        return name;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getMimeType() {
        return mimeType;
    }

    public boolean getShouldRemoveAvatar() {
        return shouldRemoveAvatar;
    }
}
