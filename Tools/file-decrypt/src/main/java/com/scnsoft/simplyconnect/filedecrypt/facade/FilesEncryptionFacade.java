package com.scnsoft.simplyconnect.filedecrypt.facade;

import com.scnsoft.simplyconnect.filedecrypt.CipherMode;

public interface FilesEncryptionFacade {
    void process(CipherMode mode, String source, String target);
}
