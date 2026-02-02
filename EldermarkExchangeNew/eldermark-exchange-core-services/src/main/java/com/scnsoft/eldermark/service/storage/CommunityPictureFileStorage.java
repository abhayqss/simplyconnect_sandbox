package com.scnsoft.eldermark.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CommunityPictureFileStorage extends BaseFileStorageWithoutEncryption {

    public CommunityPictureFileStorage(@Value("${community.picture.path}") String storageLocation) {
        super(storageLocation);
    }

    @Override
    protected String generateNewFileName(String originalFileName) {
        return addTimestampPostfixToFileName(originalFileName);
    }
}
