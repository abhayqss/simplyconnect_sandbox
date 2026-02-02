package com.scnsoft.simplyconnect.bulkfileencrypt.service;

import com.scnsoft.simplyconnect.bulkfileencrypt.dao.EncryptedDocumentDao;
import com.scnsoft.simplyconnect.bulkfileencrypt.entity.EncryptedDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EncryptedDocumentServiceImpl implements EncryptedDocumentService {

    @Autowired
    private EncryptedDocumentDao encryptedDocumentDao;

    @Override
    @Transactional(readOnly = true)
    public boolean isEncrypted(String path) {
        var result =  encryptedDocumentDao.existsByPath(path);
        if (result) {
            System.out.println("Document was already marked as encrypted: " + path);
        }
        return result;
    }

    @Override
    public void markEncrypted(String path) {
        EncryptedDocument encryptedDocument = new EncryptedDocument();
        encryptedDocument.setPath(path);
        encryptedDocumentDao.save(encryptedDocument);
    }
}
