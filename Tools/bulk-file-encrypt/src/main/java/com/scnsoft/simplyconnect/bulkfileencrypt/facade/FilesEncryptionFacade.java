package com.scnsoft.simplyconnect.bulkfileencrypt.facade;

import java.util.List;

public interface FilesEncryptionFacade {
    void encryptAll(List<String> paths, Boolean checkIfAlreadyEncrypted);
}
