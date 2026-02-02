package com.scnsoft.eldermark.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ImageFileStorage extends BaseImageFileStorage {

    public ImageFileStorage(@Value("${image.upload.basedir}") String storageLocation) {
        super(storageLocation);
    }
}
