package com.scnsoft.eldermark.service.image;

import com.scnsoft.eldermark.exception.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.scnsoft.eldermark.exception.InternalServerExceptionType.TIFF_CONVERSION_ERROR;

@Service
public class TiffImageConverterImpl implements TiffImageConverter {

    private static final Logger logger = LoggerFactory.getLogger(TiffImageConverterImpl.class);

    // Take header size with some margin (approximate header size less that 3KB)
    private static final long TIFF_HEADERS_SIZE = 50 * 1024;
    private static final long TIFF_BYTES_PER_PIXEL = 3;
    private static final String TIFF_FORMAT_NAME = "tiff";
    private static final float RESIZE_FACTOR = 0.9f;

    @Override
    public byte[] convertToTiff(byte[] imageBytes, long maxSize) {

        var image = readImage(imageBytes);
        var width = image.getWidth();
        var height = image.getHeight();

        var resultImageSize = width * height * TIFF_BYTES_PER_PIXEL + TIFF_HEADERS_SIZE;
        if (resultImageSize > maxSize) {

            var k = Math.sqrt(
                (float) (maxSize - TIFF_HEADERS_SIZE) / (TIFF_BYTES_PER_PIXEL * width * height)
            );

            width = (int) (width * k);
            height = (int) (height * k);

            image = resizeImage(image, width, height);
        }

        var result = getTiffImageBytes(image);

        // Should never happen
        if (result.length > maxSize) {
            logger.warn("Convert image size exceed specified value: {} > {}", result.length, maxSize);

            while (result.length > maxSize) {
                image = resizeImage(image, RESIZE_FACTOR);
                result = getTiffImageBytes(image);
            }
        }

        return result;
    }

    private byte[] getTiffImageBytes(BufferedImage image) {
        var outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, TIFF_FORMAT_NAME, outputStream);
        } catch (IOException e) {
            throw new InternalServerException(TIFF_CONVERSION_ERROR, e);
        }
        return outputStream.toByteArray();
    }

    private BufferedImage readImage(byte[] imageBytes) {
        var sourceImageInputStream = new ByteArrayInputStream(imageBytes);
        try {
            return ImageIO.read(sourceImageInputStream);
        } catch (IOException e) {
            throw new InternalServerException(TIFF_CONVERSION_ERROR, e);
        }
    }

    private BufferedImage resizeImage(BufferedImage image, float scaleFactor) {
        return resizeImage(
            image,
            (int) (image.getWidth() * scaleFactor),
            (int) (image.getHeight() * scaleFactor)
        );
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        var scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        var resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        resultImage.getGraphics().drawImage(scaledImage, 0, 0, null);
        return resultImage;
    }
}
