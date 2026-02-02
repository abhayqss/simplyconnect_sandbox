package com.scnsoft.simplyconnect.filedecrypt.facade;

import com.scnsoft.simplyconnect.filedecrypt.CipherMode;
import com.scnsoft.simplyconnect.filedecrypt.service.DocumentEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FilesEncryptionFacadeImpl implements FilesEncryptionFacade {

    @Autowired
    private DocumentEncryptionService documentDecryptionService;

    @Override
    public void process(CipherMode mode, String source, String target) {
        documentDecryptionService.processAllFiles(mode, source, target);
    }
}
