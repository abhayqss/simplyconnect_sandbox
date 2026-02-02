package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.shared.exceptions.FileIOException;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class SaveDocumentCallbackImpl implements SaveDocumentCallback {

    protected void saveToFile(InputStream is, File file, String originalFileName) {
        try {
            FileCopyUtils.copy(is, new FileOutputStream(file));
        } catch (IOException e) {
            throw new FileIOException("Failed to save file " + originalFileName, e);
        }
    }

    public void rollbackSaveToFile(File file) {
        if (file.exists()) file.delete();
    }
}
