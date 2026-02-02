package com.scnsoft.eldermark.service.image;

public interface TiffImageConverter {
    byte[] convertToTiff(byte[] data, long maxSize);
}
