package com.scnsoft.eldermark.services;

import java.io.File;

@Deprecated
public interface SaveDocumentCallback {
    /**
     * Saves data to file. File must exist prior to this invocation, otherwise an error may occur.
     *
     * @param file output file
     * @throws com.scnsoft.eldermark.shared.exceptions.FileIOException
     *          in case of error
     */
    void saveToFile(File file);

    void rollbackSaveToFile(File file);
}
