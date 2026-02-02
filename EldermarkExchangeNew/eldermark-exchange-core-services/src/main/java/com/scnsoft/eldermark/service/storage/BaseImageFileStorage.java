package com.scnsoft.eldermark.service.storage;

import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class BaseImageFileStorage extends BaseFileStorageWithoutEncryption {

    public BaseImageFileStorage(String storageLocation) {
        super(storageLocation);
    }

    @Override
    protected byte[] processInputData(byte[] inputData, String fileName) {
        try {
            var image = ImageIO.read(new ByteArrayInputStream(inputData));
            validateImage(image);

            var extension = FilenameUtils.getExtension(fileName);

            var output = new ByteArrayOutputStream();
            ImageIO.write(image, extension, output);

            return output.toByteArray();
        } catch (IOException e) {
            throw new InternalServerException(InternalServerExceptionType.FILE_IO_ERROR, e);
        }
    }

    protected void validateImage(BufferedImage image) {
    }
}
