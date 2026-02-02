package com.scnsoft.eldermark.service.storage;

import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.validation.ValidationConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

@Service
public class AvatarFileStorage extends BaseImageFileStorage {

    public AvatarFileStorage(@Value("${avatar.physical.path}") String storageLocation) {
        super(storageLocation);
    }

    @Override
    protected String generateNewFileName(String originalFileName) {
        return addTimestampPostfixToFileName(originalFileName);
    }

    @Override
    protected void validateFile(MultipartFile file) {
        super.validateFile(file);
        validateAvatarSize(file);
    }

    @Override
    protected void validateImage(BufferedImage image) {
        validateAvatarRatio(image);
    }

    private void validateAvatarSize(MultipartFile avatar) {
        if (avatar.getSize() > ValidationConstants.PHOTO_SIZE) {
            throw new ValidationException("Supported file types: JPG, PNG, GIF | Max 1mb");
        }
    }

    private void validateAvatarRatio(BufferedImage avatar) {
        double height = avatar.getHeight();
        double width = avatar.getWidth();
        double ratio = width / height;
        if (ratio < ValidationConstants.RATIO_MIN || ratio > ValidationConstants.RATIO_MAX) {
            throw new ValidationException(String.format(
                "Photo ratio should be between %.1f and %.1f",
                ValidationConstants.RATIO_MIN, ValidationConstants.RATIO_MAX
            ));
        }
    }
}
