package com.scnsoft.simplyconnect.filedecrypt.service;

import com.scnsoft.simplyconnect.filedecrypt.CipherMode;

public interface DocumentEncryptionService {
    void processAllFiles(CipherMode mode, String source, String target);
}
