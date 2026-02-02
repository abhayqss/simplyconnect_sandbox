package com.scnsoft.simplyconnect.bulkfileencrypt.facade;

import com.scnsoft.simplyconnect.bulkfileencrypt.service.DocumentEncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FilesEncryptionFacadeImpl implements FilesEncryptionFacade {

    @Autowired
    private DocumentEncryptionService documentEncryptionService;

    @Override
    public void encryptAll(List<String> paths, Boolean checkIfAlreadyEncrypted) {
        paths.forEach(s -> encryptAll(s, checkIfAlreadyEncrypted));
    }

    private void encryptAll(String path, Boolean checkIfAlreadyEncrypted) {
        documentEncryptionService.encryptAllFiles(path, checkIfAlreadyEncrypted);
    }

}
